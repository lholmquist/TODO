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
package org.aerogear.todo.server.rest;

import org.aerogear.todo.server.model.Task;
import org.picketlink.extensions.core.pbox.authorization.RolesAllowed;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Stateless
@Path("/tasks")
@TransactionAttribute
@RolesAllowed({"simple", "admin"})
public class TaskEndpoint {
    @PersistenceContext
    private EntityManager em;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task create(Task entity) {
        em.persist(entity);
        return entity;
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteById(@PathParam("id")
                           Long id) {
        Task result = em.find(Task.class, id);
        em.remove(result);
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Task findById(@PathParam("id")
                         Long id) {
        return em.find(Task.class, id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> listAll() {
        @SuppressWarnings("unchecked")
        final List<Task> results = em.createQuery("SELECT x FROM Task x").getResultList();
        return results;
    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task update(@PathParam("id")
                       Long id, Task entity) {
        entity.setId(id);
        entity = em.merge(entity);
        return entity;
    }
}