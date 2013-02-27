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

import org.jboss.aerogear.security.auth.AuthenticationManager;
import org.jboss.aerogear.security.auth.LoggedUser;
import org.jboss.aerogear.security.auth.Secret;
import org.jboss.aerogear.security.auth.Token;
import org.jboss.aerogear.security.authz.IdentityManagement;
import org.jboss.aerogear.security.model.AeroGearUser;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Default authentication endpoint implementation
 */
@Path("/")
@Stateless
@TransactionAttribute
public class AuthenticationServiceImpl {

    //TODO it must be replaced by some admin page
    public static final String DEFAULT_ROLE = "admin";
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

    /**
     * Logs in the specified {@link org.jboss.aerogear.security.model.AeroGearUser}
     *
     * @param aeroGearUser represents a simple implementation that holds user's credentials.
     * @return HTTP response and the session ID
     */
    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(final AeroGearUser aeroGearUser) {

        authenticationManager.login(aeroGearUser);
        return Response.ok(aeroGearUser).header(AUTH_TOKEN, token.get().toString()).build();
    }

    /**
     * {@link org.jboss.aerogear.security.model.AeroGearUser} registration
     *
     * @param aeroGearUser represents a simple implementation that holds user's credentials.
     * @return HTTP response and the session ID
     */
    @Path("/enroll")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(AeroGearUser aeroGearUser) {
        configuration.grant(DEFAULT_ROLE).to(aeroGearUser);
        authenticationManager.login(aeroGearUser);
        return Response.ok(aeroGearUser).build();
    }

    /**
     * Logs out the specified {@link org.jboss.aerogear.security.model.AeroGearUser} from the system.
     *
     * @throws org.jboss.aerogear.security.exception.AeroGearSecurityException
     *          on logout failure
     *          {@link org.jboss.aerogear.security.exception.HttpExceptionMapper} return the HTTP status code
     */
    @Path("/logout")
    public void logout() {
        authenticationManager.logout();
    }

}