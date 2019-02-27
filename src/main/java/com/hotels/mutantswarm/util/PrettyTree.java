/**
 * Copyright (C) 2018-2019 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.mutantswarm.util;

import java.util.ArrayList;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.MutantSwarmParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

public class PrettyTree {

  public static void main(String[] args) throws ParseException {
    ASTNode tree = new MutantSwarmParseDriver().parse(args[0]);
    printAST(tree);
  }

  public static void printAST(ASTNode node) {
    printMutation(node, 0);
  }

  private static void printMutation(ASTNode node, int indentation) {
    for (int i = 0; i < indentation; i++) {
      System.out.print("\t");
    }
    System.out.print(node.getToken().toString());
    CommonToken token = (CommonToken) node.getToken();
    if (hasLocation(token)) {
      System.out.print("\t\t\t");
      System.out.print(node.getText());
    }
    System.out.println();

    ArrayList<Node> children = node.getChildren();
    if (children != null) {
      for (Node child : children) {
        printMutation((ASTNode) child, indentation + 1);
      }
    }
  }

  private static boolean hasLocation(CommonToken token) {
    return token.getStartIndex() > 0 && token.getStopIndex() >= token.getStartIndex();
  }

}
