/*
 * Copyright (C) 2018-2021 Expedia, Inc.
 * Copyright (C) 2021 The HiveRunner Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.apache.hadoop.hive.ql.optimizer.calcite.translator.ASTBuilder;
import org.apache.hadoop.hive.ql.parse.CalcitePlanner.ASTSearcher;
import org.apache.hadoop.hive.ql.parse.ParseDriver.HiveLexerX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * ParseDriver. Inside the Hive package in order to access non-public class members. 
 */
public class MutantSwarmParseDriver {

	private static final Logger LOG = LoggerFactory.getLogger(MutantSwarmParseDriver.class);
	private final ParseDriver pd;

	public MutantSwarmParseDriver() {
		pd = new ParseDriver();
	}

	public List<CommonToken> extractTokens(TokenRewriteStream tokenStream) {
	  tokenStream.seek(0);
		List<CommonToken> tokens = new ArrayList<>();
		int i = 1;
		while (tokenStream.LT(i).getType() != -1) {
			tokens.add((CommonToken) tokenStream.LT(i++));
		}
		return tokens;
	}

	public TokenRewriteStream lex(String command) throws ParseException {
		HiveLexerX lexer = pd.new HiveLexerX(pd.new ANTLRNoCaseStringStream(command));
		if (lexer.getErrors().size() == 0) {
			LOG.debug("Lex Completed");
		} else {
			throw new ParseException(lexer.getErrors());
		}
		return new TokenRewriteStream(lexer);
	}

	public ASTNode parse(TokenRewriteStream tokens) throws ParseException {
		HiveParser parser = new HiveParser(tokens);
		parser.setTreeAdaptor(ParseDriver.adaptor);
		HiveParser.statement_return r = null;
		try {
			r = parser.statement();
		} catch (RecognitionException e) {
			e.printStackTrace();
			throw new ParseException(parser.errors);
		}

		if (parser.errors.size() == 0) {
			LOG.debug("Parse Completed");
		} else {
			throw new ParseException(parser.errors);
		}

		ASTNode tree = (ASTNode) r.getTree();
		tree.setUnknownTokenBoundaries();
		tree = findRootNonNullToken(tree);
		handleSetColRefs(tree);
		return tree;
	}

	/**
	 * Parses a command, optionally assigning the parser's token stream to the
	 * given context.
	 *
	 * @param command
	 *            command to parse
	 * @return parsed AST
	 */
	public ASTNode parse(String command) throws ParseException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Parsing command: " + command);
		}
		return parse(lex(command));
	}

	/**
	 * Performs a descent of the leftmost branch of a tree, stopping when either
	 * a node with a non-null token is found or the leaf level is encountered.
	 *
	 * @param tree
	 *            candidate node from which to start searching
	 *
	 * @return node at which descent stopped
	 */
	private static ASTNode findRootNonNullToken(ASTNode tree) {
		while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
			tree = (ASTNode) tree.getChild(0);
		}
		return tree;
	}

	private static void handleSetColRefs(ASTNode tree) {
		CalcitePlanner.ASTSearcher astSearcher = new CalcitePlanner.ASTSearcher();
		while (true) {
			astSearcher.reset();
			ASTNode setCols = astSearcher.depthFirstSearch(tree, HiveParser.TOK_SETCOLREF);
			if (setCols == null)
				break;
			processSetColsNode(setCols, astSearcher);
		}
	}
	/**
	 * Replaces a spurious TOK_SETCOLREF added by parser with column names
	 * referring to the query in e.g. a union. This is to maintain the
	 * expectations that some code, like order by position alias, might have
	 * about not having ALLCOLREF. If it cannot find the columns with confidence
	 * it will just replace SETCOLREF with ALLCOLREF. Most of the cases where
	 * that happens are easy to work around in the query (e.g. by adding column
	 * aliases in the union).
	 * 
	 * @param setCols
	 *            TOK_SETCOLREF ASTNode.
	 * @param searcher
	 *            AST searcher to reuse.
	 */
	private static void processSetColsNode(ASTNode setCols, ASTSearcher searcher) {
		searcher.reset();
		CommonTree rootNode = setCols;
		while (rootNode != null && rootNode.getType() != HiveParser.TOK_INSERT) {
			rootNode = rootNode.parent;
		}
		if (rootNode == null || rootNode.parent == null) {
			// Couldn't find the parent insert; replace with ALLCOLREF.
			LOG.debug("Replacing SETCOLREF with ALLCOLREF because we couldn't find the root INSERT");
			setCols.token.setType(HiveParser.TOK_ALLCOLREF);
			return;
		}
		rootNode = rootNode.parent; // TOK_QUERY above insert
		Tree fromNode = null;
		for (int j = 0; j < rootNode.getChildCount(); ++j) {
			Tree child = rootNode.getChild(j);
			if (child.getType() == HiveParser.TOK_FROM) {
				fromNode = child;
				break;
			}
		}
		if (!(fromNode instanceof ASTNode)) {
			// Couldn't find the from that contains subquery; replace with
			// ALLCOLREF.
			LOG.debug("Replacing SETCOLREF with ALLCOLREF because we couldn't find the FROM");
			setCols.token.setType(HiveParser.TOK_ALLCOLREF);
			return;
		}
		// We are making what we are trying to do more explicit if there's a
		// union alias; so
		// that if we do something we didn't expect to do, it'd be more likely
		// to fail.
		String alias = null;
		if (fromNode.getChildCount() > 0) {
			Tree fromWhat = fromNode.getChild(0);
			if (fromWhat.getType() == HiveParser.TOK_SUBQUERY && fromWhat.getChildCount() > 1) {
				Tree child = fromWhat.getChild(fromWhat.getChildCount() - 1);
				if (child.getType() == HiveParser.Identifier) {
					alias = child.getText();
				}
			}
		}
		// Note: we assume that this isn't an already malformed query;
		// we don't check for that here - it will fail later anyway.
		// First, we find the SELECT closest to the top.
		ASTNode select = searcher.simpleBreadthFirstSearchAny((ASTNode) fromNode, HiveParser.TOK_SELECT,
				HiveParser.TOK_SELECTDI);
		if (select == null) {
			// Couldn't find the from that contains subquery; replace with
			// ALLCOLREF.
			LOG.debug("Replacing SETCOLREF with ALLCOLREF because we couldn't find the SELECT");
			setCols.token.setType(HiveParser.TOK_ALLCOLREF);
			return;
		}

		// Then, find the leftmost logical sibling select, because that's what
		// Hive uses for aliases.
		while (true) {
			CommonTree queryOfSelect = select.parent;
			while (queryOfSelect != null && queryOfSelect.getType() != HiveParser.TOK_QUERY) {
				queryOfSelect = queryOfSelect.parent;
			}
			// We should have some QUERY; and also its parent because by
			// supposition we are in subq.
			if (queryOfSelect == null || queryOfSelect.parent == null) {
				LOG.debug("Replacing SETCOLREF with ALLCOLREF because we couldn't find the QUERY");
				setCols.token.setType(HiveParser.TOK_ALLCOLREF);
				return;
			}
			if (queryOfSelect.childIndex == 0)
				break; // We are the left-most child.
			Tree moreToTheLeft = queryOfSelect.parent.getChild(0);
			Preconditions.checkState(moreToTheLeft != queryOfSelect);
			ASTNode newSelect = searcher.simpleBreadthFirstSearchAny((ASTNode) moreToTheLeft, HiveParser.TOK_SELECT,
					HiveParser.TOK_SELECTDI);
			Preconditions.checkState(newSelect != select);
			select = newSelect;
			// Repeat the procedure for the new select.
		}

		// Found the proper columns.
		List<ASTNode> newChildren = new ArrayList<>(select.getChildCount());
		HashSet<String> aliases = new HashSet<>();
		for (int i = 0; i < select.getChildCount(); ++i) {
			Tree selExpr = select.getChild(i);
			assert selExpr.getType() == HiveParser.TOK_SELEXPR;
			assert selExpr.getChildCount() > 0;
			// Examine the last child. It could be an alias.
			Tree child = selExpr.getChild(selExpr.getChildCount() - 1);
			switch (child.getType()) {
			case HiveParser.TOK_SETCOLREF:
				// We have a nested setcolref. Process that and start from
				// scratch TODO: use stack?
				processSetColsNode((ASTNode) child, searcher);
				processSetColsNode(setCols, searcher);
				return;
			case HiveParser.TOK_ALLCOLREF:
				// We should find an alias of this insert and do (alias).*. This
				// however won't fix e.g.
				// positional order by alias case, cause we'd still have a star
				// on the top level. Bail.
				LOG.debug("Replacing SETCOLREF with ALLCOLREF because of nested ALLCOLREF");
				setCols.token.setType(HiveParser.TOK_ALLCOLREF);
				return;
			case HiveParser.TOK_TABLE_OR_COL:
				Tree idChild = child.getChild(0);
				assert idChild.getType() == HiveParser.Identifier : idChild;
				if (!createChildColumnRef(idChild, alias, newChildren, aliases)) {
					setCols.token.setType(HiveParser.TOK_ALLCOLREF);
					return;
				}
				break;
			case HiveParser.Identifier:
				if (!createChildColumnRef(child, alias, newChildren, aliases)) {
					setCols.token.setType(HiveParser.TOK_ALLCOLREF);
					return;
				}
				break;
			case HiveParser.DOT: {
				Tree colChild = child.getChild(child.getChildCount() - 1);
				assert colChild.getType() == HiveParser.Identifier : colChild;
				if (!createChildColumnRef(colChild, alias, newChildren, aliases)) {
					setCols.token.setType(HiveParser.TOK_ALLCOLREF);
					return;
				}
				break;
			}
			default:
				// Not really sure how to refer to this (or if we can).
				// TODO: We could find a different from branch for the union,
				// that might have an alias?
				// Or we could add an alias here to refer to, but that might
				// break other branches.
				LOG.debug("Replacing SETCOLREF with ALLCOLREF because of the nested node " + child.getType() + " "
						+ child.getText());
				setCols.token.setType(HiveParser.TOK_ALLCOLREF);
				return;
			}
		}
		// Insert search in the beginning would have failed if these parents
		// didn't exist.
		ASTNode parent = (ASTNode) setCols.parent.parent;
		int t = parent.getType();
		assert t == HiveParser.TOK_SELECT || t == HiveParser.TOK_SELECTDI : t;
		int ix = setCols.parent.childIndex;
		parent.deleteChild(ix);
		for (ASTNode node : newChildren) {
			parent.insertChild(ix++, node);
		}
	}

	private static boolean createChildColumnRef(Tree child, String alias, List<ASTNode> newChildren,
			HashSet<String> aliases) {
		String colAlias = child.getText();
		if (!aliases.add(colAlias)) {
			// TODO: if a side of the union has 2 columns with the same name,
			// noone on the higher
			// level can refer to them. We could change the alias in the
			// original node.
			LOG.debug("Replacing SETCOLREF with ALLCOLREF because of duplicate alias " + colAlias);
			return false;
		}
		ASTBuilder selExpr = ASTBuilder.construct(HiveParser.TOK_SELEXPR, "TOK_SELEXPR");
		ASTBuilder toc = ASTBuilder.construct(HiveParser.TOK_TABLE_OR_COL, "TOK_TABLE_OR_COL");
		ASTBuilder id = ASTBuilder.construct(HiveParser.Identifier, colAlias);
		if (alias == null) {
			selExpr = selExpr.add(toc.add(id));
		} else {
			ASTBuilder dot = ASTBuilder.construct(HiveParser.DOT, ".");
			ASTBuilder aliasNode = ASTBuilder.construct(HiveParser.Identifier, alias);
			selExpr = selExpr.add(dot.add(toc.add(aliasNode)).add(id));
		}
		newChildren.add(selExpr.node());
		return true;
	}

}
