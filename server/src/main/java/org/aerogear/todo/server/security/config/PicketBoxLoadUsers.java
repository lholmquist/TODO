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
import org.jboss.picketlink.idm.internal.JPAIdentityStore;
import org.jboss.picketlink.idm.internal.jpa.JPATemplate;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.model.User;
import org.jboss.picketlink.idm.spi.IdentityStore;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@Singleton
@Startup
public class PicketBoxLoadUsers {

    private static final Logger LOGGER = Logger.getLogger(PicketBoxLoadUsers.class);

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    /**
     * <p>Loads some users during the first construction.</p>
     */
    //TODO this entire initialization code will be removed
    @PostConstruct
    public void create() {
        buildNewUser("john", "john@doe.org", "John", "Doe", "123", "admin");
        buildNewUser("jane", "jane@doe.org", "Jane", "Doe", "123", "simple");
    }

    private void buildNewUser(String username, String email, String firstname, String lastname, String password, String role) {

        IdentityStore identityStore = createIdentityStore();

        User jane = identityStore.createUser(username);
        jane.setEmail(email);
        jane.setFirstName(firstname);
        jane.setLastName(lastname);

        identityStore.updatePassword(jane, password);

        Role roleSimple = identityStore.createRole(role);
        identityStore.createMembership(roleSimple, jane, null);

    }

    /**
     * Extracted from PicketLink test cases
     *
     * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
     */
    protected IdentityStore createIdentityStore() {
        JPAIdentityStore identityStore = new JPAIdentityStore();

        JPATemplate jpaTemplate = new JPATemplate();

        jpaTemplate.setEntityManager(entityManager);

        identityStore.setJpaTemplate(jpaTemplate);

        return identityStore;
    }


}
