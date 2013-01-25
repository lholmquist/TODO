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
package org.aerogear.todo.server.security.rest;

import org.aerogear.todo.server.security.idm.AeroGearUser;
import org.aerogear.todo.server.security.service.AuthenticationManager;
import org.aerogear.todo.server.security.service.IDMHelper;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * <p>JAX-RS Endpoint to authenticate users.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
@Stateless
@Path("/auth")
public class AuthenticationEndpoint {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationEndpoint.class);

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private IDMHelper idm;

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(final AeroGearUser user) {

        //TODO it should be done by admin screen
        idm.grant(user.getRole()).to(user);

        return authenticationManager.login(user.getUsername(), user.getPassword());

    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(AeroGearUser user) {

        LOGGER.debug("Logged in!");

        return authenticationManager.login(user.getUsername(), user.getPassword());

    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public void logout() {
        authenticationManager.logout();
    }

}