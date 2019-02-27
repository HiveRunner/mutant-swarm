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
package com.hotels.mutantswarm.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

class AssetUtil {

  static void writeResourceToFile(String resourcePath, File destination) throws IOException {
    try (InputStream resourceStream = AssetUtil.class.getResourceAsStream(resourcePath)) {
      if (resourceStream != null) {
        FileUtils.copyInputStreamToFile(resourceStream, destination);
      } else {
        throw new NullPointerException("Null resource at path: " + resourcePath);
      }

    }
  }

  static String readResourceAsString(String resourcePath) throws IOException {
    try (InputStream resourceStream = AssetUtil.class.getResourceAsStream(resourcePath)) {
      if (resourceStream != null) {
        return IOUtils.toString(resourceStream, Charset.defaultCharset());
      } else {
        throw new NullPointerException("Null resource at path: " + resourcePath);
      }
    }
  }

}
