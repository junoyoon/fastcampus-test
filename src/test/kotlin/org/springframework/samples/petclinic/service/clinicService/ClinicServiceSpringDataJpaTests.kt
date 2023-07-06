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
package org.springframework.samples.petclinic.service.clinicService

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.samples.petclinic.model.*
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.samples.petclinic.util.EntityUtils.getById
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
    @Autowired private val clinicService: ClinicService
) {
    @Test
    fun shouldFindOwnersByLastName() {
        var owners = clinicService.findOwnerByLastName("Davis")
        assertThat(owners.size).isEqualTo(2)
        owners = clinicService.findOwnerByLastName("Daviss")
        assertThat(owners.isEmpty()).isTrue()
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val owner = clinicService.findOwnerById(1)
        assertThat(owner).isNotNull
        assertThat(owner!!.lastName).startsWith("Franklin")
        assertThat(owner.getPets().size).isEqualTo(1)
        assertThat(owner.getPets()[0].type).isNotNull()
        assertThat(owner.getPets()[0].type!!.name).isEqualTo("cat")
    }

    @Test
    @Transactional
    fun shouldInsertOwner() {
        var owners = clinicService.findOwnerByLastName("Schultz")
        val found = owners.size
        val owner = Owner().apply {
            this.firstName = "Sam"
            this.lastName = "Schultz"
            this.address = "4, Evans Street"
            this.city = "Wollongong"
            this.telephone = "4444444444"
        }

        clinicService.saveOwner(owner)
        assertThat(owner.id).isNotEqualTo(0)
        assertThat(owner.getPet("null value")).isNull()
        owners = clinicService.findOwnerByLastName("Schultz")
        assertThat(owners.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateOwner() {
        val owner = clinicService.findOwnerById(1)!!
        val oldLastName = owner.lastName
        val newLastName = oldLastName + "X"
        owner.lastName = newLastName
        clinicService.saveOwner(owner)

        // retrieving new name from database
        val modified = clinicService.findOwnerById(1)!!
        assertThat(modified.lastName).isEqualTo(newLastName)
    }

    @Test
    fun shouldFindPetWithCorrectId() {
        val pet7 = clinicService.findPetById(7)
        assertThat(pet7!!.name).startsWith("Samantha")
        assertThat(pet7.owner!!.firstName).isEqualTo("Jean")
    }

    //    @Test
    //    void shouldFindAllPetTypes() {
    //        Collection<PetType> petTypes = this.clinicService.findPetTypes();
    //
    //        PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
    //        assertThat(petType1.getName()).isEqualTo("cat");
    //        PetType petType4 = EntityUtils.getById(petTypes, PetType.class, 4);
    //        assertThat(petType4.getName()).isEqualTo("snake");
    //    }
    @Test
    @Transactional
    fun shouldInsertPetIntoDatabaseAndGenerateId() {
        var owner6 = clinicService.findOwnerById(6)!!
        val found: Int = owner6.getPets().size
        val pet = Pet().apply {
            this.name = "bowser"
            val types = clinicService.findPetTypes()
            this.type = getById(types, PetType::class.java, 2)
            this.birthDate = LocalDate.now()
        }

        owner6.addPet(pet)
        assertThat(owner6.getPets().size).isEqualTo(found + 1)

        clinicService.savePet(pet)
        clinicService.saveOwner(owner6)
        owner6 = clinicService.findOwnerById(6)!!
        assertThat(owner6.getPets()).hasSize(found + 1)
        // checks that id has been generated
        assertThat(pet.id).isNotNull()
    }

    @Test
    @Transactional
    fun shouldUpdatePetName() {
        var pet7: Pet = clinicService.findPetById(7)!!
        val oldName = pet7.name
        val newName = oldName + "X"
        pet7.name = newName
        clinicService.savePet(pet7)
        pet7 = clinicService.findPetById(7)!!
        assertThat(pet7.name).isEqualTo(newName)
    }

    @Test
    fun shouldFindVets() {
        val vets = clinicService.findVets()
        val vet = getById(vets, Vet::class.java, 3)
        assertThat(vet.lastName).isEqualTo("Douglas")
        assertThat(vet.nrOfSpecialties).isEqualTo(2)
        assertThat(vet.getSpecialties()[0].name).isEqualTo("dentistry")
        assertThat(vet.getSpecialties()[1].name).isEqualTo("surgery")
    }

    @Test
    @Transactional
    fun shouldAddNewVisitForPet() {
        var pet7: Pet = clinicService.findPetById(7)!!
        val found: Int = pet7.getVisits().size
        val visit = Visit()
        pet7.addVisit(visit)
        visit.description = "test"
        clinicService.saveVisit(visit)
        clinicService.savePet(pet7)
        pet7 = clinicService.findPetById(7)!!
        assertThat(pet7.getVisits().size).isEqualTo(found + 1)
        assertThat(visit.id).isNotNull()
    }

    @Test
    @Throws(Exception::class)
    fun shouldFindVisitsByPetId() {
        val visits = clinicService.findVisitsByPetId(7)
        assertThat(visits).hasSize(2)
        val visitArr = visits.toTypedArray<Visit>()
        assertThat(visitArr[0].pet).isNotNull()
        assertThat(visitArr[0].date).isNotNull()
        assertThat(visitArr[0].pet!!.id).isEqualTo(7)
    }

    @Test
    fun shouldFindAllPets() {
        val pets = clinicService.findAllPets()
        val pet1 = getById(pets, Pet::class.java, 1)
        assertThat(pet1.name).isEqualTo("Leo")
        val pet3 = getById(pets, Pet::class.java, 3)
        assertThat(pet3.name).isEqualTo("Rosy")
    }

    @Test
    @Transactional
    fun shouldDeletePet() {
        val pet = clinicService.findPetById(1)!!
        clinicService.deletePet(pet)
        assertThat(clinicService.findPetById(1)).isNull()
    }

    @Test
    fun shouldFindVisitDyId() {
        val visit = clinicService.findVisitById(1)!!
        assertThat(visit.id).isEqualTo(1)
        assertThat(visit.pet!!.name).isEqualTo("Samantha")
    }

    @Test
    fun shouldFindAllVisits() {
        val visits = clinicService.findAllVisits()
        val visit1 = getById(visits, Visit::class.java, 1)
        assertThat(visit1.pet!!.name).isEqualTo("Samantha")
        val visit3 = getById(visits, Visit::class.java, 3)
        assertThat(visit3.pet!!.name).isEqualTo("Max")
    }

    @Test
    @Transactional
    fun shouldInsertVisit() {
        var visits = clinicService.findAllVisits()
        val found = visits.size
        val pet = clinicService.findPetById(1)
        val visit = Visit().apply {
            this.pet = pet
            this.date = LocalDate.now()
            this.description = "new visit"
        }
        clinicService.saveVisit(visit)
        assertThat(visit.id).isNotEqualTo(0)
        visits = clinicService.findAllVisits()
        assertThat(visits).hasSize(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateVisit() {
        var visit = clinicService.findVisitById(1)!!
        val oldDesc = visit.description
        val newDesc = oldDesc + "X"
        visit.description = newDesc
        clinicService.saveVisit(visit)
        visit = clinicService.findVisitById(1)!!
        assertThat(visit.description).isEqualTo(newDesc)
    }

    @Test
    @Transactional
    fun shouldDeleteVisit() {
        val visit = clinicService.findVisitById(1)!!
        clinicService.deleteVisit(visit)
        assertThat(clinicService.findVisitById(1)).isNull()
    }

    @Test
    fun shouldFindVetDyId() {
        val vet = clinicService.findVetById(1)!!
        assertThat(vet.firstName).isEqualTo("James")
        assertThat(vet.lastName).isEqualTo("Carter")
    }

    @Test
    @Transactional
    fun shouldInsertVet() {
        var vets = clinicService.findAllVets()
        val found = vets.size
        val vet = Vet().apply { this.firstName = "John"; this.lastName = "Dow" }
        clinicService.saveVet(vet)
        assertThat(vet.id).isNotEqualTo(0)
        vets = clinicService.findAllVets()
        assertThat(vets).hasSize(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateVet() {
        val vet = clinicService.findVetById(1)!!
        val oldLastName = vet.lastName
        val newLastName = oldLastName + "X"
        vet.lastName = newLastName
        clinicService.saveVet(vet)
        assertThat(clinicService.findVetById(1)!!.lastName).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteVet() {
        val vet = clinicService.findVetById(1)!!
        clinicService.deleteVet(vet)
        assertThat(clinicService.findVetById(1)).isNull()
    }

    @Test
    fun shouldFindAllOwners() {
        val owners = clinicService.findAllOwners()
        val owner1 = getById(owners, Owner::class.java, 1)
        assertThat(owner1.firstName).isEqualTo("George")
        val owner3 = getById(owners, Owner::class.java, 3)
        assertThat(owner3.firstName).isEqualTo("Eduardo")
    }

    @Test
    @Transactional
    fun shouldDeleteOwner() {
        val owner = clinicService.findOwnerById(1)!!
        clinicService.deleteOwner(owner)
        assertThat(clinicService.findOwnerById(1)).isNull()
    }

    @Test
    fun shouldFindPetTypeById() {
        val petType = clinicService.findPetTypeById(1)!!
        assertThat(petType.name).isEqualTo("cat")
    }

    @Test
    fun shouldFindAllPetTypes() {
        val petTypes = clinicService.findAllPetTypes()
        val petType1 = getById(petTypes, PetType::class.java, 1)
        assertThat(petType1.name).isEqualTo("cat")
        val petType3 = getById(petTypes, PetType::class.java, 3)
        assertThat(petType3.name).isEqualTo("lizard")
    }

    @Test
    @Transactional
    fun shouldInsertPetType() {
        var petTypes = clinicService.findAllPetTypes()
        val found = petTypes.size
        val petType = PetType().apply { name = "tiger" }
        clinicService.savePetType(petType)
        assertThat(petType.id).isNotEqualTo(0)
        petTypes = clinicService.findAllPetTypes()
        assertThat(petTypes).hasSize(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdatePetType() {
        var petType = clinicService.findPetTypeById(1)!!
        val oldLastName = petType.name
        val newLastName = oldLastName + "X"
        petType.name = newLastName
        clinicService.savePetType(petType)
        petType = clinicService.findPetTypeById(1)!!
        assertThat(petType.name).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeletePetType() {
        val petType = clinicService.findPetTypeById(1)!!
        clinicService.deletePetType(petType)
        assertThat(clinicService.findPetTypeById(1)).isNull()
    }

    @Test
    fun shouldFindSpecialtyById() {
        val specialty = clinicService.findSpecialtyById(1)!!
        assertThat(specialty.name).isEqualTo("radiology")
    }

    @Test
    fun shouldFindAllSpecialtys() {
        val specialties = clinicService.findAllSpecialties()
        val specialty1 = getById(specialties, Specialty::class.java, 1)
        assertThat(specialty1.name).isEqualTo("radiology")
        val specialty3 = getById(specialties, Specialty::class.java, 3)
        assertThat(specialty3.name).isEqualTo("dentistry")
    }

    @Test
    @Transactional
    fun shouldInsertSpecialty() {
        var specialties = clinicService.findAllSpecialties()
        val found = specialties.size
        val specialty = Specialty().apply { name = "dermatologist" }
        clinicService.saveSpecialty(specialty)
        assertThat(specialty.id).isNotEqualTo(0)
        specialties = clinicService.findAllSpecialties()
        assertThat(specialties).hasSize(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateSpecialty() {
        val specialty = clinicService.findSpecialtyById(1)!!
        val oldLastName = specialty.name
        val newLastName = oldLastName + "X"
        specialty.name = newLastName
        clinicService.saveSpecialty(specialty)
        assertThat(clinicService.findSpecialtyById(1)!!.name).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteSpecialty() {
        val specialty = Specialty().apply { name = "test" }
        clinicService.saveSpecialty(specialty)
        val specialtyId = specialty.id!!
        assertThat(specialtyId).isNotNull()
        val saved = clinicService.findSpecialtyById(specialtyId)!!
        assertThat(saved).isNotNull()
        clinicService.deleteSpecialty(saved)
        assertThat(clinicService.findSpecialtyById(specialtyId)).isNull()
    }
}
