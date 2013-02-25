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

import org.aerogear.todo.server.model.Task;

@Stateless
@TransactionAttribute
public class TaskEndpoint {
    
    @PersistenceContext
    private EntityManager em;

    public Task create(Task entity) {
        em.persist(entity);
        return entity;
    }

    public void deleteById(String id) {
        Task result = em.find(Task.class, Long.valueOf(id));
        em.remove(result);
    }

    public Task findById(String id) {
        return em.find(Task.class, Long.valueOf(id));
    }

    public List<Task> listAll() {
        @SuppressWarnings("unchecked")
        final List<Task> results = em.createQuery("SELECT x FROM Task x").getResultList();
        return results;
    }

    public Task update(String id, Task entity) {
        entity.setId(Long.valueOf(id));
        entity = em.merge(entity);
        return entity;
    }
}