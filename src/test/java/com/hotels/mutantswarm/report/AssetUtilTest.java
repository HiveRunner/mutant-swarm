/**
 * Copyright (C) 2018-2021 Expedia, Inc.
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
package com.hotels.mutantswarm.report;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AssetUtilTest {

  @Test
  public void writeResourceNullException() throws IOException {
    Assertions.assertThrows(NullPointerException.class, () -> AssetUtil.writeResourceToFile("unexistent resource path", null));
  }
  
  @Test
  public void readResourceNullException() throws IOException {
    Assertions.assertThrows(NullPointerException.class, () -> AssetUtil.readResourceAsString("unexistent resource path"));
  }
  
}
