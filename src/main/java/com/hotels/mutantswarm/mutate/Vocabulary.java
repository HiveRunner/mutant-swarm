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

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Collections.unmodifiableMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hive.ql.parse.HiveParser;

enum Vocabulary {

  INSTANCE;

  private static final Map<Integer, String> idToName;
  private static final Map<String, Integer> nameToId;

  static {
    Map<Integer, String> buildIdToName = new HashMap<>();
    Map<String, Integer> buildNameToId = new HashMap<>();
    Field[] fields = HiveParser.class.getDeclaredFields();
    for (Field field : fields) {
      int modifiers = field.getModifiers();
      if (field.getType().equals(int.class) && isFinal(modifiers) && isPublic(modifiers) && isStatic(modifiers)) {
        String name = field.getName();
        int id;
        try {
          id = field.getInt(HiveParser.class);
          if (buildIdToName.put(id, name) != null) {
            throw new RuntimeException(id + " " + name);
          }
          buildNameToId.put(name, id);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    idToName = unmodifiableMap(buildIdToName);
    nameToId = unmodifiableMap(buildNameToId);
  }

  String getName(int id) {
    return idToName.get(id);
  }

  int getId(String name) {
    return nameToId.get(name);
  }

}
