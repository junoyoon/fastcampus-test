/*
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.samples.petclinic.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.samples.petclinic.model.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 *
 *  Base class for [ClinicService] integration tests.
 *
 * Subclasses should specify Spring context
 * configuration using [@ContextConfiguration][ContextConfiguration] annotation
 *
 *
 * AbstractclinicServiceTests and its subclasses benefit from the following services provided by the Spring
 * TestContext Framework:    * **Spring IoC container caching** which spares us unnecessary set up
 * time between test execution.  * **Dependency Injection** of test fixture instances, meaning that
 * we don't need to perform application context lookups. See the use of [@Autowired][Autowired] on the `[ ][ClinicServiceSpringDataJpaTests.clinicService]` instance variable, which uses autowiring *by
 * type*.  * **Transaction management**, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script.  *  An [ ApplicationContext][org.springframework.context.ApplicationContext] is also inherited and can be used for explicit bean lookup if necessary.
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
class ClinicServiceSpringDataJpaTests(
    @Autowired val clinicService: ClinicService,
) {
    @DisplayName("Owner")
    @Nested
    inner class OwnerTest {

        @Test
        fun findOwnerByLastName() {
            var owners = clinicService.findOwnerByLastName("Davis")
            assertThat(owners).hasSize(2)
            owners = clinicService.findOwnerByLastName("Daviss")
            assertThat(owners).isEmpty()
        }

        @Test
        fun findOwnerById_withPet() {
            val owner = clinicService.findOwnerById(1)
            assertThat(owner).isNotNull
            assertThat(owner!!.lastName).startsWith("Franklin")
            assertThat(owner.getPets()).hasSize(1)
            assertThat(owner.getPets()[0].type).isNotNull()
            assertThat(owner.getPets()[0].type!!.name).isEqualTo("cat")
        }

        @Test
        @Transactional
        fun insertOwner() {
            val owners = clinicService.findOwnerByLastName("Schultz")
            val owner = Owner(
                firstName = "Sam",
                lastName = "Schultz",
                address = "4, Evans Street",
                city = "Wollongong",
                telephone = "4444444444",
            )

            clinicService.saveOwner(owner)
            assertThat(owner.id).isNotEqualTo(0)
            assertThat(owner.getPet("null value")).isNull()
            val actual = clinicService.findOwnerByLastName("Schultz")
            assertThat(actual).hasSize(owners.size + 1)
        }

        @Test
        @Transactional
        fun updateOwner() {
            val owner = clinicService.findOwnerById(1)!!.apply { lastName += "X" }
            clinicService.saveOwner(owner)

            // retrieving new name from database
            val actual = clinicService.findOwnerById(1)!!
            assertThat(actual.lastName).isEqualTo(owner.lastName)
        }


        @Test
        fun findAllOwners() {
            val owners = clinicService.findAllOwners()
            val owner1 = owners.first { it.id == 1 }
            assertThat(owner1.firstName).isEqualTo("George")
            val owner3 = owners.first { it.id == 3 }
            assertThat(owner3.firstName).isEqualTo("Eduardo")
        }


        @ParameterizedTest
        @CsvSource(value = ["1, George", "3, Eduardo"])
        fun findAllOwners(id: Int, name: String) {
            val owners = clinicService.findAllOwners()
            val owner = owners.first { it.id == id }
            assertThat(owner.firstName).isEqualTo(name)
        }

        @Test
        @Transactional
        fun deleteOwner() {
            val owner = clinicService.findOwnerById(1)!!
            clinicService.deleteOwner(owner)
            assertThat(clinicService.findOwnerById(1)).isNull()
        }

    }

    @DisplayName("Pet")
    @Nested
    inner class PetTest {
        @Test
        fun findPetById() {
            val actual = clinicService.findPetById(7)
            assertThat(actual!!.name).startsWith("Samantha")
            assertThat(actual.owner!!.firstName).isEqualTo("Jean")
        }

        @Test
        @Transactional
        fun insertPet() {
            val owner6 = clinicService.findOwnerById(6)!!
            val found: Int = owner6.getPets().size
            val pet = Pet(
                name = "bowser",
                type = clinicService.findPetTypes().first { it.id == 2 },
                birthDate = LocalDate.now()
            )

            owner6.addPet(pet)
            assertThat(owner6.getPets()).hasSize(found + 1)

            clinicService.savePet(pet)
            clinicService.saveOwner(owner6)
            val actual = clinicService.findOwnerById(6)!!
            assertThat(actual.getPets()).hasSize(found + 1)
            // checks that id has been generated
            assertThat(pet.id).isNotNull()
        }


        @Test
        @Transactional
        fun savePet_petName() {
            val pet7: Pet = clinicService.findPetById(7)!!.apply { name += "X" }
            clinicService.savePet(pet7)
            val actual = clinicService.findPetById(7)!!
            assertThat(actual.name).isEqualTo(pet7.name)
        }


        @Test
        fun findAllPets() {
            val pets = clinicService.findAllPets()
            val pet1 = pets.first { it.id == 1 }
            assertThat(pet1.name).isEqualTo("Leo")
            val pet3 = pets.first { it.id == 3 }
            assertThat(pet3.name).isEqualTo("Rosy")
        }

        @Test
        @Transactional
        fun deletePet() {
            val pet = clinicService.findPetById(1)!!
            clinicService.deletePet(pet)
            assertThat(clinicService.findPetById(1)).isNull()
        }
    }

    @DisplayName("Vet")
    @Nested
    inner class VetTest {

        @Test
        fun findVets() {
            val vets = clinicService.findVets()
            val vet = vets.first { it.id == 3 }
            assertThat(vet.lastName).isEqualTo("Douglas")
            assertThat(vet.nrOfSpecialties).isEqualTo(2)
            assertThat(vet.getSpecialties()[0].name).isEqualTo("dentistry")
            assertThat(vet.getSpecialties()[1].name).isEqualTo("surgery")
        }

        @Test
        fun findVetById() {
            val vet = clinicService.findVetById(1)!!
            assertThat(vet.firstName).isEqualTo("James")
            assertThat(vet.lastName).isEqualTo("Carter")
        }

        @Test
        @Transactional
        fun saveVet_insert() {
            var vets = clinicService.findAllVets()
            val found = vets.size
            val vet = Vet( firstName = "John", lastName = "Dow" )
            clinicService.saveVet(vet)
            assertThat(vet.id).isNotEqualTo(0)
            vets = clinicService.findAllVets()
            assertThat(vets).hasSize(found + 1)
        }

        @Test
        @Transactional
        fun saveVet_update() {
            val vet = clinicService.findVetById(1)!!.apply { this.lastName += "X" }
            clinicService.saveVet(vet)
            assertThat(clinicService.findVetById(1)!!.lastName).isEqualTo(vet.lastName)
        }

        @Test
        @Transactional
        fun deleteVet() {
            val vet = clinicService.findVetById(1)!!
            clinicService.deleteVet(vet)
            assertThat(clinicService.findVetById(1)).isNull()
        }

    }

    @DisplayName("Visit")
    @Nested
    inner class VisitTest {

        @Transactional
        @Test
        fun saveVisit_withPet() {
            val pet7: Pet = clinicService.findPetById(7)!!
            val found = pet7.getVisits().size
            val visit = Visit(description = "test")

            pet7.addVisit(visit)
            clinicService.saveVisit(visit)
            clinicService.savePet(pet7)

            val actual = clinicService.findPetById(7)!!
            assertThat(actual.getVisits()).hasSize(found + 1)
            assertThat(visit.id).isNotNull()
        }

        @Test
        fun findVisitsByPetId() {
            val visits = clinicService.findVisitsByPetId(7)
            assertThat(visits).hasSize(2)
            assertThat(visits[0].pet).isNotNull()
            assertThat(visits[0].date).isNotNull()
            assertThat(visits[0].pet!!.id).isEqualTo(7)
        }

        @Test
        fun findVisitById() {
            val visit = clinicService.findVisitById(1)!!
            assertThat(visit.id).isEqualTo(1)
            assertThat(visit.pet!!.name).isEqualTo("Samantha")
        }

        @Test
        fun findAllVisits() {
            val visits = clinicService.findAllVisits()
            val visit1 = visits.first { it.id == 1 }
            assertThat(visit1.pet!!.name).isEqualTo("Samantha")
            val visit3 = visits.first { it.id == 3 }
            assertThat(visit3.pet!!.name).isEqualTo("Max")
        }

        @Test
        @Transactional
        fun saveVisit_insert() {
            val visits = clinicService.findAllVisits()
            val pet = clinicService.findPetById(1)
            val visit = Visit(
                pet = pet,
                date = LocalDate.now(),
                description = "new visit"
            )
            clinicService.saveVisit(visit)
            assertThat(visit.id).isNotEqualTo(0)
            val actual = clinicService.findAllVisits()
            assertThat(actual).hasSize(visits.size + 1)
        }

        @Test
        @Transactional
        fun saveVisit_update() {
            val visit = clinicService.findVisitById(1)!!.apply { this.description += "X" }
            clinicService.saveVisit(visit)
            val actual = clinicService.findVisitById(1)!!
            assertThat(actual.description).isEqualTo(visit.description)
        }

        @Test
        @Transactional
        fun deleteVisit() {
            val visit = clinicService.findVisitById(1)!!
            clinicService.deleteVisit(visit)
            assertThat(clinicService.findVisitById(1)).isNull()
        }
    }

    @DisplayName("PetType")
    @Nested
    inner class PetTypeTest {
        @Test
        fun findPetTypeById() {
            val actual = clinicService.findPetTypeById(1)!!
            assertThat(actual.name).isEqualTo("cat")
        }

        @Test
        fun findAllPetTypes() {
            val petTypes = clinicService.findAllPetTypes()
            val petType1 = petTypes.first { it.id == 1 }
            assertThat(petType1.name).isEqualTo("cat")
            val petType3 = petTypes.first { it.id == 3 }
            assertThat(petType3.name).isEqualTo("lizard")
        }

        @Test
        @Transactional
        fun savePetType_insert() {
            val petTypes = clinicService.findAllPetTypes()
            val petType = PetType(name = "tiger")
            clinicService.savePetType(petType)
            assertThat(petType.id).isNotEqualTo(0)
            val actual = clinicService.findAllPetTypes()
            assertThat(actual).hasSize(petTypes.size + 1)
        }

        @Test
        @Transactional
        fun savePetType_update() {
            val petType = clinicService.findPetTypeById(1)!!.apply { name += "X" }
            clinicService.savePetType(petType)
            val actual = clinicService.findPetTypeById(1)!!
            assertThat(actual.name).isEqualTo(petType.name)
        }

        @Test
        @Transactional
        fun deletePetType() {
            val petType = clinicService.findPetTypeById(1)!!
            clinicService.deletePetType(petType)
            assertThat(clinicService.findPetTypeById(1)).isNull()
        }
    }

    @DisplayName("Specialty")
    @Nested
    inner class SpecialtyTest {
        @Test
        fun findSpecialtyById() {
            val actual = clinicService.findSpecialtyById(1)!!
            assertThat(actual.name).isEqualTo("radiology")
        }

        @Test
        fun findAllSpecialties() {
            val specialties = clinicService.findAllSpecialties()
            val specialty1 = specialties.first { it.id == 1 }
            assertThat(specialty1.name).isEqualTo("radiology")
            val specialty3 = specialties.first { it.id == 3 }
            assertThat(specialty3.name).isEqualTo("dentistry")
        }

        @Test
        @Transactional
        fun saveSpecialty_insert() {
            val specialties = clinicService.findAllSpecialties()
            val specialty = Specialty(name = "dermatologist")
            clinicService.saveSpecialty(specialty)
            assertThat(specialty.id).isNotEqualTo(0)
            val actual = clinicService.findAllSpecialties()
            assertThat(actual).hasSize(specialties.size + 1)
        }

        @Test
        @Transactional
        fun saveSpecialty_update() {
            val specialty = clinicService.findSpecialtyById(1)!!.apply { name += "X" }
            clinicService.saveSpecialty(specialty)
            assertThat(clinicService.findSpecialtyById(1)!!.name).isEqualTo(specialty.name)
        }

        @Test
        @Transactional
        fun deleteSpecialty() {
            val specialty = Specialty(name = "test")
            clinicService.saveSpecialty(specialty)
            val specialtyId = specialty.id!!
            assertThat(specialtyId).isNotNull()
            val saved = clinicService.findSpecialtyById(specialtyId)!!
            assertThat(saved).isNotNull()
            clinicService.deleteSpecialty(saved)
            assertThat(clinicService.findSpecialtyById(specialtyId)).isNull()
        }
    }
}
