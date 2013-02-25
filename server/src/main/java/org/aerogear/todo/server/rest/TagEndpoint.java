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

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aerogear.todo.server.model.Tag;
import org.aerogear.todo.server.model.Task;

@Stateless
@TransactionAttribute
public class TagEndpoint {
    
    @PersistenceContext
    private EntityManager em;

    public Tag create(Tag entity) {
        em.persist(entity);
        return entity;
    }

    public List<Long> deleteById(String idParam) {
        long id = Long.valueOf(idParam);

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

    public Tag findById(String id) {
        return em.find(Tag.class, Long.valueOf(id));
    }

    public List<Tag> listAll() {
        @SuppressWarnings("unchecked")
        final List<Tag> results = em.createQuery("SELECT x FROM Tag x").getResultList();
        return results;
    }

    public Tag update(String id, Tag entity) {
        entity.setId(Long.valueOf(id));
        entity = em.merge(entity);
        return entity;
    }
}