/*
 * Copyright 2016 Ripple OSI
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rippleosi.security.common.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    private JsonUtils() {
        // prevent construction
    }

    public static JsonNode extractJsonFromString(final String rawJson) {
        final ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;

        try {
            rootNode = mapper.readTree(rawJson);
        }
        catch (final IOException ioe) {
            LOGGER.warn("Failed to convert JSON string to a node for reuse.", ioe);
        }
        catch (final NullPointerException npe) {
            LOGGER.warn("The input JSON string was null and could not be converted.", npe);
        }

        return rootNode;
    }
}
