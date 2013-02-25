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

package org.aerogear.todo.server;

import org.aerogear.todo.server.model.Project;
import org.aerogear.todo.server.rest.ProjectEndpoint;
import org.jboss.aerogear.controller.router.AbstractRoutingModule;
import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.RequestMethod;

public class Routes extends AbstractRoutingModule {

    @Override
    public void configuration() throws Exception {
        route()
                .from("/projects")
                .roles("admin")
                .on(RequestMethod.POST)
                .consumes(JSON)
                .produces(JSON)
                .to(ProjectEndpoint.class).create(param(Project.class));
        route()
                .from("/projects/{id}")
                .roles("admin")
                .on(RequestMethod.DELETE)
                .consumes(JSON)
                .produces(JSON)
                .to(ProjectEndpoint.class).deleteById(param("id"));
        route()
                .from("/projects/{id}")
                .roles("admin", "simple")
                .on(RequestMethod.GET)
                .consumes(JSON)
                .produces(JSON)
                .to(ProjectEndpoint.class).findById(param("id"));
        route()
                .from("/projects")
                .roles("admin", "simple")
                .on(RequestMethod.GET)
                .consumes(JSON)
                .produces(JSON)
                .to(ProjectEndpoint.class).listAll();
        route()
                .from("/projects/{id}")
                .roles("admin")
                .on(RequestMethod.PUT)
                .consumes(JSON)
                .produces(MediaType.JSON)
                .to(ProjectEndpoint.class).update(param("id"), param(Project.class));
    }
}
    
    