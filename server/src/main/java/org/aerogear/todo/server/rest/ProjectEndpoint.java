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

import org.aerogear.todo.server.model.Project;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import java.util.List;

@Stateless
@TransactionAttribute
public class ProjectEndpoint {

    private EntityManager em;

    public Project create(Project entity) {
        em.persist(entity);
        return entity;
    }

    public List<Long> deleteById(String idParam) {
        long id = Long.valueOf(idParam);

        //@TODO extract it to another class
        @SuppressWarnings("unchecked")
        List<Long> taskIds = em.createQuery("select c.id from Task c inner join c.project o where o.id = :id")
                .setParameter("id", Long.valueOf(id))
                .getResultList();


        Project project = em.find(Project.class, id);

        em.createQuery("UPDATE Task e SET e.project.id = null WHERE e.project.id = :id")
                .setParameter("id", id)
                .executeUpdate();

        em.remove(project);

        return taskIds;
    }

    public Project findById(String id) {
        return em.find(Project.class, Long.valueOf(id));
    }

    public List<Project> listAll() {
        @SuppressWarnings("unchecked")
        final List<Project> results = em.createQuery("SELECT x FROM Project x").getResultList();
        return results;
    }

    public Project update(String id, Project entity) {
        entity.setId(Long.valueOf(id));
        entity = em.merge(entity);
        return entity;
    }
}