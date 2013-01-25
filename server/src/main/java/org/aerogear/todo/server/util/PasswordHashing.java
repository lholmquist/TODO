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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashing {

    private static final String CHARSETNAME = "UTF-8";

    public static String digest(String password) {

        StringBuilder builder = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance(Digest.SHA256.hash());

            md.update(password.getBytes(CHARSETNAME));
            byte[] digest = md.digest();

            for (byte c : digest) {
                builder.append(Integer.toHexString((int) (c & 0xff)));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }


    public enum Digest {

        SHA256("SHA-256"),
        SHA512("SHA-512");

        private String hash;

        Digest(String hash) {
            this.hash = hash;
        }

        public String hash() {
            return hash;
        }
    }
}
