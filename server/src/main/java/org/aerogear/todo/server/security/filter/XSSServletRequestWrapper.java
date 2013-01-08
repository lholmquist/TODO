/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.aerogear.todo.server.security.filter;

import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class XSSServletRequestWrapper extends HttpServletRequestWrapper {

    public XSSServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    @Override
    public String[] getParameterValues(String param) {
        String[] values = super.getParameterValues(param);

        List<String> data = new ArrayList<String>();
        for (String value : values) {
            data.add(StringEscapeUtils.escapeHtml(value));
        }

        return (String[]) data.toArray();
    }

    @Override
    public String getParameter(String param) {
        String value = super.getParameter(param);

        return StringEscapeUtils.escapeHtml(value);
    }

}

