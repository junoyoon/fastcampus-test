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

import jakarta.persistence.*
import org.springframework.beans.support.MutableSortDefinition
import org.springframework.beans.support.PropertyComparator
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.util.*

/**
 * Simple business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name = "pets")
class Pet(
    id: Int? = null,
    name: String? = null,

    @Column(name = "birth_date")
    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    var birthDate: LocalDate? = null,

    @ManyToOne
    @JoinColumn(name = "type_id")
    var type: PetType? = null,

    @ManyToOne
    @JoinColumn(name = "owner_id")
    var owner: Owner? = null,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "pet", fetch = FetchType.EAGER)
    private var visits: MutableSet<Visit>? = null,
) : NamedEntity(id, name) {
    fun getVisitsInternal(): MutableSet<Visit> {
        if (visits == null) {
            visits = HashSet()
        }
        return visits!!
    }

    fun setVisitsInternal(visits: Set<Visit>) {
        this.visits = visits.toMutableSet()
    }

    fun getVisits(): List<Visit> {
        val sortedVisits: List<Visit> = getVisitsInternal().toList()
        PropertyComparator.sort(sortedVisits, MutableSortDefinition("date", false, false))
        return Collections.unmodifiableList(sortedVisits)
    }

    fun setVisits(visits: List<Visit>?) {
        this.visits = visits?.toMutableSet()
    }

    fun addVisit(visit: Visit) {
        getVisitsInternal().add(visit)
        visit.pet = this
    }
}
