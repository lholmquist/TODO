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
package org.aerogear.todo.server.model;

import org.aerogear.todo.server.serializer.ProjectCustomSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
@JsonSerialize(using = ProjectCustomSerializer.class)
@Entity
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column
    private String title;

    @Column
    private String style;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private Set<Task> tasks = new HashSet<Task>();

    public Project() {
    }

    public Project(Long id) {
        this.id = id;
    }

    public Project(String title, String style, Set<Task> tasks) {
        this.title = title;
        this.style = style;
        this.tasks = tasks;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(final String style) {
        this.style = style;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(final Set<Task> tasks) {
        this.tasks = tasks;
    }

    public String toString() {
        String result = "";
        if (title != null && !title.trim().isEmpty())
            result += title;
        if (style != null && !style.trim().isEmpty())
            result += " " + style;
        return result;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        if (id != null) {
            return id.equals(((Project) that).id);
        }
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

}