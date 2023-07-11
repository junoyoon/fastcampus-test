/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.io.Serializable

/**
 * Simple JavaBean domain object with an id property. Used as a base class for objects needing this property.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass
open class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
) : Serializable {
    @get:JsonIgnore
    val isNew: Boolean
        get() = this.id == null

    @Override
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (this.javaClass != other.javaClass) {
            return false
        }
        return this.id == (other as BaseEntity).id
    }

    @Override
    override fun hashCode(): Int {
        return id.hashCode()
    }
}
