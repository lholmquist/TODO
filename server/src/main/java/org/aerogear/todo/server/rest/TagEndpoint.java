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

import org.aerogear.todo.server.model.Tag;
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
@Path("/tags")
@TransactionAttribute
@RolesAllowed({"admin"})
public class TagEndpoint {
    @PersistenceContext
    private EntityManager em;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Tag create(Tag entity) {
        em.persist(entity);
        return entity;
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Long> deleteById(@PathParam("id")
                                 Long id) {

        //@TODO extract it to another class
        @SuppressWarnings("unchecked")
        List<Long> taskIds = em.createQuery("select c.id from Task c inner join c.tags o where o.id = :id")
                .setParameter("id", id)
                .getResultList();

        Tag tag = em.find(Tag.class, id);
        for (Task task : tag.getTasks()) {
            task.getTags().remove(tag);
        }
        em.merge(tag);
        em.remove(tag);
        em.flush();

        return taskIds;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "simple"})
    public Tag findById(@PathParam("id")
                        Long id) {
        return em.find(Tag.class, id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "simple"})
    public List<Tag> listAll() {
        @SuppressWarnings("unchecked")
        final List<Tag> results = em.createQuery("SELECT x FROM Tag x").getResultList();
        return results;
    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Tag update(@PathParam("id")
                      Long id, Tag entity) {
        entity.setId(id);
        entity = em.merge(entity);
        return entity;
    }
}