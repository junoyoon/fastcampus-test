/*
 * Copyright 2002-2018 the original author or authors.
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

import jakarta.persistence.*
import org.springframework.beans.support.MutableSortDefinition
import org.springframework.beans.support.PropertyComparator

/**
 * Simple JavaBean domain object representing a veterinarian.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Arjen Poutsma
 */
@Entity
@Table(name = "vets")
class Vet(
    id: Int? = null,
    firstName: String? = "",
    lastName: String? = ""

) : Person(id, firstName, lastName) {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vet_specialties",
        joinColumns = [JoinColumn(name = "vet_id")],
        inverseJoinColumns = [JoinColumn(name = "specialty_id")]
    )
    private var specialties: MutableSet<Specialty>? = null

    //@get:JsonIgnore
    var specialtiesInternal: MutableSet<Specialty>
        get() {
            if (specialties == null) {
                specialties = mutableSetOf()
            }
            return specialties!!
        }
        set(specialties) {
            this.specialties = specialties
        }

    //@XmlElement
    fun getSpecialties(): List<Specialty> {
        val sortedSpecs: List<Specialty> = specialtiesInternal.toList()
        PropertyComparator.sort(sortedSpecs, MutableSortDefinition("name", true, true))
        return sortedSpecs
    }

    fun setSpecialties(specialties: List<Specialty>) {
        this.specialties = specialties.toMutableSet()
    }

    //@get:JsonIgnore
    val nrOfSpecialties: Int
        get() = specialtiesInternal.size

    fun addSpecialty(specialty: Specialty) {
        specialtiesInternal.add(specialty)
    }

    fun clearSpecialties() {
        specialtiesInternal.clear()
    }
}
