/*
 * Copyright (C) 2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.hackathon.car.parking.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for the broker
 */
@Component
public class Util {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER= new ObjectMapper();
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static <T>T loadClassPathResource(String path, Class<T> targetType) throws IOException {
        InputStream inputStream = new ClassPathResource(path).getInputStream();
        return OBJECT_MAPPER.readValue(inputStream, targetType);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static <T> T jsonToObject(String json,Class<T> targetType) throws IOException {
        try{
            return OBJECT_MAPPER.readValue(json,targetType);
        }catch (Exception e){
            throw e;
        }
    }

}
