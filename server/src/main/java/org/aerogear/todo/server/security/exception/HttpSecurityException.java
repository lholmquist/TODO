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

package org.aerogear.todo.server.security.exception;

import org.apache.deltaspike.security.api.authorization.AccessDeniedException;
import org.apache.deltaspike.security.api.authorization.SecurityViolation;

import java.util.HashSet;
import java.util.Set;

public class HttpSecurityException {

    public static void violation(final String message) {
        Set<SecurityViolation> violations = new HashSet<SecurityViolation>();
        violations.add(new SecurityViolation() {
            private static final long serialVersionUID = 2358753444038521129L;

            @Override
            public String getReason() {
                return message;
            }
        });

        throw new AccessDeniedException(violations);
    }
}
