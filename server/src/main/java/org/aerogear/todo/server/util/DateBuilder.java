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

package org.aerogear.todo.server.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateBuilder {

    private static DateBuilder dateBuilder = null;
    public static final String PATTERN = "yyyy-MM-dd";
    private Calendar calendar;

    private DateBuilder() {
    }

    public static DateBuilder newDateBuilder() {
        if (dateBuilder == null) {
            dateBuilder = new DateBuilder();
        }
        return dateBuilder;
    }

    public DateBuilder withCalendar(Calendar calendar) {
        this.calendar = calendar;
        return this;
    }

    /**
     * Workaround for date build issue
     *
     * @TODO: Refactor it to use consolidated solutions like Joda time
     */
    public Calendar build() {
        calendar.add(Calendar.DATE, 1);
        return calendar;
    }

    public String format(Calendar calendar){
        return new SimpleDateFormat(PATTERN).format(calendar.getTime());
    }

}
