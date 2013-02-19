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
package org.aerogear.todo.server.security.config;

import org.jboss.logging.Logger;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.internal.Password;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class PicketLinkLoadUsers {

    private static final Logger LOGGER = Logger.getLogger(PicketLinkLoadUsers.class);

    @Inject
    private IdentityManager identityManager;

    /**
     * <p>Loads some users during the first construction.</p>
     */
    //TODO this entire initialization code will be removed
    @PostConstruct
    public void create() {

        buildNewUser("john", "john@doe.org", "John", "Doe", "123", "admin");
        buildNewUser("jane", "jane@doe.org", "Jane", "Doe", "123", "simple");
    }

    private User buildNewUser(String username, String email, String firstname, String lastname, String password, String role) {

        User user = new SimpleUser(username);

        user.setEmail(email);
        user.setFirstName(firstname);
        user.setLastName(lastname);

        /*
         * Note: Password will be encoded in SHA-512 with SecureRandom-1024 salt
         * See http://lists.jboss.org/pipermail/security-dev/2013-January/000650.html for more information
         */

        this.identityManager.add(user);
        this.identityManager.updateCredential(user, new Password(password));

        Role simpleRole = new SimpleRole(role);

        this.identityManager.add(simpleRole);

        identityManager.grantRole(user, simpleRole);

        return user;
    }

}
