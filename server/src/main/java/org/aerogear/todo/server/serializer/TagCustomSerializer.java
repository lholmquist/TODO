/*
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aerogear.todo.server.serializer;

import org.aerogear.todo.server.model.Tag;
import org.aerogear.todo.server.model.Task;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Replacement to the default serialization strategy for JSON
 */
public class TagCustomSerializer extends JsonSerializer<Tag> {

    @Override
    public void serialize(Tag tag, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("id");
        jsonGenerator.writeNumber(tag.getId());
        jsonGenerator.writeFieldName("title");
        jsonGenerator.writeString(tag.getTitle());
        jsonGenerator.writeFieldName("style");
        jsonGenerator.writeString(tag.getStyle());
        jsonGenerator.writeObjectField("tasks", retrieveIds(tag.getTasks()));

        jsonGenerator.writeEndObject();
    }

    /**
     * This can decrease the server performance
     *
     * @TODO must be replaced by jpa-ql refinement
     */
    private List<Long> retrieveIds(List<Task> tasks) {
        List<Long> ids = new ArrayList<Long>();
        for (Task tag : tasks) {
            ids.add(tag.getId());
        }
        return ids;
    }
}
