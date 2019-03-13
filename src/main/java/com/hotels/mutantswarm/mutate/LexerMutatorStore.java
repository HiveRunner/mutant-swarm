/**
 * Copyright (C) 2018-2019 Expedia, Inc.
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
package com.hotels.mutantswarm.mutate;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.CommonToken;

import com.hotels.mutantswarm.mutate.LexerMutatorStore.Imposters.Imposter;

/**
 * WARNING: This is a naive implementation. Identifies mutable elements of a token stream extracted by the lexer. Note
 * that the abstract syntax tree should always be used in preference to this, however some elements are only easily used
 * from the lexer token stream.
 */
public class LexerMutatorStore {

  public List<Mutator> getMutatorsFor(int position, List<CommonToken> stream) {
    List<Mutator> mutations = new ArrayList<>();
    CommonToken token = stream.get(position);
    List<Imposter> imposters = Imposters.INSTANCE.impostersFor(token);
    for (Imposter imposter : imposters) {
      String description = Vocabulary.INSTANCE.getName(token.getType())
          + " → "
          + Vocabulary.INSTANCE.getName(imposter.getTokenId())
          + " '"
          + imposter.getText()
          + "'";
      mutations.add(new TextReplaceMutator(description, token.getText(), imposter.getText()));
    }
    return unmodifiableList(mutations);
  }

  static class Imposters {
    public static Imposters INSTANCE = new Imposters.Builder()
        .group()
        .imposter("KW_ASC", "ASC")
        .imposter("KW_DESC", "DESC")
        .build()
        .group()
        .imposter("StringLiteral", "'IMPOSTER'")
        .imposter("KW_NULL", "null")
        .build()
        .group()
        .imposter("KW_STRING", "STRING")
        .imposter("KW_INT", "INT")
        .imposter("KW_FLOAT", "FLOAT")
        .imposter("KW_BOOLEAN", "BOOLEAN")
        .build()
        .group()
        .imposter("KW_INNER", "INNER")
        .imposter("TOK_FULLOUTERJOIN", "FULL OUTER")
        .imposter("TOK_LEFTOUTERJOIN", "LEFT OUTER")
        .imposter("TOK_RIGHTOUTERJOIN", "RIGHT OUTER")
        .build()
        .group()
        .imposter("KW_DISTINCT", "DISTINCT")
        .imposter("KW_ALL", "ALL")
        .build()
        .build();

    private final Map<Integer, Group> groups;
    private final Map<Integer, Integer> lengths = new HashMap<>();

    private Imposters(Map<Integer, Group> groups) {
      this.groups = groups;
      for (Group group : groups.values()) {
        for (Imposter imposter : group.imposters.values()) {
          lengths.put(imposter.getTokenId(), imposter.length());
        }
      }
    }

    List<Imposter> impostersFor(CommonToken token) {
      return impostersFor(token.getType());
    }

    List<Imposter> impostersFor(int tokenId) {
      Group group = groups.get(tokenId);
      if (group == null) {
        return emptyList();
      }
      return group.getAlternateImposters(tokenId);
    }

    static class Builder {

      private final Map<Integer, Group> groups = new HashMap<>();

      Group.Builder group() {
        return new Group.Builder(this);
      }

      private void internalAdd(Group group) {
        for (int tokenId : group.getTokenIds()) {
          if (groups.put(tokenId, group) != null) {
            throw new IllegalArgumentException("Already exists: " + groups.get(tokenId));
          }
        }
      }

      Imposters build() {
        return new Imposters(unmodifiableMap(groups));
      }
    }

    static class Group {

      private final Map<Integer, Imposter> imposters;

      private Group(Map<Integer, Imposter> imposters) {
        this.imposters = imposters;
      }

      Set<Integer> getTokenIds() {
        return imposters.keySet();
      }

      List<Imposter> getAlternateImposters(int tokenId) {
        Map<Integer, Imposter> subGroup = new HashMap<>(imposters);
        subGroup.remove(tokenId);
        return new ArrayList<>(subGroup.values());
      }

      static class Builder {
        private final Imposters.Builder impostersBuilder;
        private final Map<Integer, Imposter> imposters = new HashMap<>();

        Builder(Imposters.Builder impostersBuilder) {
          this.impostersBuilder = impostersBuilder;
        }

        Builder imposter(String tokenName, String text) {
          int tokenId = Vocabulary.INSTANCE.getId(tokenName);
          if (imposters.put(tokenId, new Imposter(tokenId, text)) != null) {
            throw new IllegalArgumentException("Already exists: " + imposters.get(tokenId));
          }
          return this;
        }

        Imposters.Builder build() {
          if (imposters.size() < 2) {
            throw new IllegalArgumentException();
          }
          Group group = new Group(unmodifiableMap(imposters));
          impostersBuilder.internalAdd(group);
          return impostersBuilder;
        }
      }
    }

    static class Imposter {

      private final int tokenId;
      private final String text;

      private Imposter(int tokenId, String text) {
        this.tokenId = tokenId;
        this.text = text;
      }

      int getTokenId() {
        return tokenId;
      }

      String getText() {
        return text;
      }

      int length() {
        return text.length();
      }

      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + tokenId;
        return result;
      }

      @Override
      public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (obj == null)
          return false;
        if (getClass() != obj.getClass())
          return false;
        Imposter other = (Imposter) obj;
        if (text == null) {
          if (other.text != null)
            return false;
        } else if (!text.equals(other.text))
          return false;
        if (tokenId != other.tokenId)
          return false;
        return true;
      }

      @Override
      public String toString() {
        return "Imposter [tokenId=" + tokenId + ", text=" + text + "]";
      }

      public CommonToken appliedTo(CommonToken token) {
        CommonToken t = new CommonToken(token);
        t.setText(getText());
        t.setType(getTokenId());
        return t;
      }

    }
  }
}
