/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
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

package org.aerogear.todo.server.rest;

import java.util.Arrays;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.aerogear.todo.server.util.HttpResponse;
import org.jboss.aerogear.security.auth.AuthenticationManager;
import org.jboss.aerogear.security.auth.LoggedUser;
import org.jboss.aerogear.security.auth.Secret;
import org.jboss.aerogear.security.auth.Token;
import org.jboss.aerogear.security.authz.IdentityManagement;
import org.jboss.aerogear.security.model.AeroGearUser;

/**
 * Default authentication endpoint implementation
 */
@Stateless
@TransactionAttribute
public class AuthenticationService {

    //TODO it must be replaced by some admin page
    private static final String AUTH_TOKEN = "Auth-Token";

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private IdentityManagement configuration;

    @Inject
    @Secret
    private Instance<String> secret;

    @Inject
    @Token
    private Instance<String> token;

    @Inject
    @LoggedUser
    private Instance<String> loggedUser;

    @Inject
    Event<ResponseHeaders> headers;

    /**
     * Logs in the specified {@link org.jboss.aerogear.security.model.AeroGearUser}
     *
     * @param aeroGearUser represents a simple implementation that holds user's credentials.
     * @return HTTP response and the session ID
     */
    public HttpResponse login(final AeroGearUser aeroGearUser) {
        //This will be removed
        performLogin(aeroGearUser);
        fireResponseHeaderEvent();
        return createResponse(aeroGearUser);
    }
    
    private void performLogin(AeroGearUser aeroGearUser) {
        authenticationManager.login(aeroGearUser);
    }
    
    private void fireResponseHeaderEvent() {
        headers.fire(new ResponseHeaders(AUTH_TOKEN, token.get().toString()));
    }

    /**
     * {@link org.jboss.aerogear.security.model.AeroGearUser} registration
     *
     * @param aeroGearUser represents a simple implementation that holds user's credentials.
     * @return HTTP response and the session ID
     */
    public HttpResponse register(AeroGearUser aeroGearUser) {
        configuration.create(aeroGearUser);
        configuration.grant(aeroGearUser.getRole()).to(aeroGearUser);
        authenticationManager.login(aeroGearUser);
        fireResponseHeaderEvent();
        return createResponse(aeroGearUser);
    }
    
    private HttpResponse createResponse(AeroGearUser aeroGearUser) {
        return new HttpResponse(aeroGearUser.getUsername(), Arrays.asList(aeroGearUser.getRole()));
    }

    /**
     * Logs out the specified {@link org.jboss.aerogear.security.model.AeroGearUser} from the system.
     *
     * @throws org.jboss.aerogear.security.exception.AeroGearSecurityException
     *          on logout failure
     *          {@link org.jboss.aerogear.security.exception.HttpExceptionMapper} return the HTTP status code
     */
    public void logout() {
        authenticationManager.logout();
    }

}