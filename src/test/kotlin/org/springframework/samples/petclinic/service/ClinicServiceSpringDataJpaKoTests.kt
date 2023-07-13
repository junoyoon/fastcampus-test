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

import io.kotest.assertions.assertSoftly
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.samples.petclinic.SpringFunSpec
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
 * we don't need to perform application context lookups. See the use of [@Autowired][Autowired] on the `[ ][ClinicServiceSpringDataJpaKoTests.sut]` instance variable, which uses autowiring *by
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
@Transactional
@SpringBootTest
class ClinicServiceSpringDataJpaKoTests(
    @Autowired private val sut: ClinicService
) : SpringFunSpec({

    context("owner") {
        test("findOwnerByLastName") {
            sut.findOwnerByLastName("Davis") shouldHaveSize 2
            sut.findOwnerByLastName("Daviss") shouldHaveSize 0
        }

        test("findOwnerById") {
            val actual = sut.findOwnerById(1)
            assertSoftly(actual.shouldNotBeNull()) {
                lastName shouldStartWith "Franklin"
                getPets() shouldHaveSize 1
            }

            actual.getPets()[0].shouldNotBeNull().name shouldBe "cat"
        }

        test("saveOwner") {
            val owners = sut.findOwnerByLastName("Schultz")
            val found = owners.size
            val owner = Owner(
                firstName = "Sam",
                lastName = "Schultz",
                address = "4, Evans Street",
                city = "Wollongong",
                telephone = "4444444444",
            )
            sut.saveOwner(owner)
            owner.id shouldNotBe 0
            owner.getPet("null value").shouldBeNull()
            sut.findOwnerByLastName("Schultz") shouldHaveSize found + 1
        }

        test("updateOwner") {
            val owner = sut.findOwnerById(1)!!.apply { lastName += "X" }
            sut.saveOwner(owner)

            // retrieving new name from database
            sut.findOwnerById(1).shouldNotBeNull()
                .lastName shouldBe owner.lastName
        }

        test("saveOwner with Pet") {
            val owner6 = sut.findOwnerById(6)
            val found6: Int = owner6
                .shouldNotBeNull().getPets().size

            val pet = Pet(
                name = "bowser",
                type = sut.findPetTypes().first() { it.id == 2 },
                birthDate = LocalDate.now(),
            )

            owner6.addPet(pet)

            owner6.getPets() shouldHaveSize found6 + 1
            sut.savePet(pet)
            sut.saveOwner(owner6)

            val actual = sut.findOwnerById(6)
            actual.shouldNotBeNull().getPets() shouldHaveSize found6 + 1
            // checks that id has been generated
            pet.id.shouldNotBeNull()
        }

        test("findAllOwners") {
            val actual = sut.findAllOwners()
            actual.first { it.id == 1 }.firstName shouldBe "George"
            actual.first { it.id == 3 }.firstName shouldBe "Eduardo"
        }

        test("deleteOwner") {
            val owner = sut.findOwnerById(1)!!
            sut.deleteOwner(owner)
            sut.findOwnerById(1).shouldBeNull()
        }
    }

    context("pet") {
        // FIXME : 실습 data driven test
        test("shouldFindAllPets") {
            val actual = sut.findAllPets()
            actual.first { it.id == 1 }.shouldNotBeNull().name shouldBe "Leo"
            actual.first { it.id == 3 }.shouldNotBeNull().name shouldBe "Rosy"
        }

        test("shouldDeletePet") {
            val actual = sut.findPetById(1)!!
            sut.deletePet(actual)
            sut.findPetById(1).shouldBeNull()
        }
        test("findPetById") {
            val actual = sut.findPetById(7)

            actual.shouldNotBeNull()
            actual.name shouldStartWith "Samantha"
            actual.owner.shouldNotBeNull().firstName shouldStartWith "Jean"
        }

        test("savePet") {
            val pet7 = sut.findPetById(7)!!.apply { name += "X" }
            sut.savePet(pet7)
            sut.findPetById(7).shouldNotBeNull().name shouldBe pet7.name
        }
    }

    context("visit") {

        test("findVisitById") {
            val actual = sut.findVisitById(1)!!
            actual.id shouldBe 1
            actual.pet.shouldNotBeNull().name shouldBe "Samantha"
        }

        context("findAllVisits") {
            withData(listOf(1 to "Samantha", 3 to "Max")) { (id, name) ->
                sut.findAllVisits().first { it.id == id }
                    .shouldNotBeNull().pet.shouldNotBeNull().name shouldBe name
            }
        }

        test("saveVisit") {
            val visits = sut.findAllVisits()
            val found = visits.size
            val visit = Visit(
                pet = sut.findPetById(1),
                date = LocalDate.now(),
                description = "new visit",
            )

            sut.saveVisit(visit)
            visit.id shouldNotBe 0
            sut.findAllVisits() shouldHaveSize found + 1
        }


        test("update") {
            val visit = sut.findVisitById(1)!!.apply {this.description += "X"}

            sut.saveVisit(visit)
            sut.findVisitById(1).shouldNotBeNull()
                .description shouldBe visit.description
        }


        test("deleteVisit") {
            val visit = sut.findVisitById(1)!!
            sut.deleteVisit(visit)
            sut.findVisitById(1).shouldBeNull()
        }


        test("saveVisit with Pet") {
            val pet7: Pet = sut.findPetById(7)!!
            val found: Int = pet7.getVisits().size
            val visit = Visit( description = "test" )
            pet7.addVisit(visit)

            sut.saveVisit(visit)
            sut.savePet(pet7)

            sut.findPetById(7).shouldNotBeNull().getVisits() shouldHaveSize found + 1
            visit.id.shouldNotBeNull()

        }

        // FIXME: assertSoftly 로 수정
        test("findVisitsByPetId") {
            val actual = sut.findVisitsByPetId(7)
            actual.shouldHaveSize(2)
            actual[0].should {
                it.pet.shouldNotBeNull().id shouldBe 7
                it.date.shouldNotBeNull()
            }
//
//            assertSoftly(actual[0]) {
//                pet.shouldNotBeNull().id shouldBe 7
//                date.shouldNotBeNull()
//            }
        }

    }


    context("vet") {
        test("findVetById") {
            val actual = sut.findVetById(1)!!
            actual.firstName shouldBe "James"
            actual.lastName shouldBe "Carter"
        }

        test("saveVet") {
            val vets = sut.findAllVets()
            val vet = Vet( firstName = "John", lastName = "Dow" )
            sut.saveVet(vet)
            vet.id shouldNotBe 0
            sut.findAllVets() shouldHaveSize vets.size + 1
        }

        test("updateVet") {
            val vet = sut.findVetById(1)!!.apply { lastName += "X" }
            sut.saveVet(vet)
            sut.findVetById(1).shouldNotBeNull().lastName shouldBe vet.lastName
        }

        test("deleteVet") {
            val vet = sut.findVetById(1)!!
            sut.deleteVet(vet)
            sut.findVetById(1).shouldBeNull()
        }
        test("findVets") {
            sut.findVets().first { it.id == 3 }.shouldNotBeNull()
                .should {
                    it.lastName shouldBe "Douglas"
                    it.nrOfSpecialties shouldBe 2
                    it.getSpecialties()[0].name shouldBe "dentistry"
                    it.getSpecialties()[1].name shouldBe "surgery"
                }
        }

    }

    context("petType") {
        test("findPetTypeById") {
            val actual = sut.findPetTypeById(1)!!
            actual.name shouldBe "cat"
        }

        test("findAllPetTypes") {
            val actual = sut.findAllPetTypes()
            actual.first { it.id == 1 }.name shouldBe "cat"
            actual.first { it.id == 3 }.name shouldBe "lizard"
        }

        test("savePetType") {
            val petTypes = sut.findAllPetTypes()
            val found = petTypes.size
            val petType = PetType( name = "tiger" )
            sut.savePetType(petType)

            petType.id shouldNotBe 0
            sut.findAllPetTypes() shouldHaveSize found + 1
        }

        test("updatePetType") {
            val petType = sut.findPetTypeById(1)!!.apply { name += "X" }
            sut.savePetType(petType)
            sut.findPetTypeById(1).shouldNotBeNull().name shouldBe petType.name
        }

        test("deletePetType") {
            val petType = sut.findPetTypeById(1)!!
            sut.deletePetType(petType)
            sut.findPetTypeById(1).shouldBeNull()
        }
    }

    context("specialty") {
        test("findSpecialtyById") {
            sut.findSpecialtyById(1).shouldNotBeNull().name shouldBe "radiology"
        }

        test("findAllSpecialties") {
            val actual = sut.findAllSpecialties()
            actual.first { it.id == 1 }.name shouldBe "radiology"
            actual.first { it.id == 3 }.name shouldBe "dentistry"
        }

        test("saveSpecialty") {
            val specialties = sut.findAllSpecialties()
            val specialty = Specialty( name = "dermatologist" )
            sut.saveSpecialty(specialty)
            specialty.id.shouldNotBeNull()
            sut.findAllSpecialties() shouldHaveSize specialties.size + 1
        }

        test("updateSpecialty") {
            val specialty = sut.findSpecialtyById(1)!!.apply { name += "X" }
            sut.saveSpecialty(specialty)
            sut.findSpecialtyById(1).shouldNotBeNull().name shouldBe specialty.name
        }


        test("deleteSpecialty") {
            val specialty = Specialty( name = "test" )
            sut.saveSpecialty(specialty)
            val specialtyId = specialty.id.shouldNotBeNull()
            val saved = sut.findSpecialtyById(specialtyId).shouldNotBeNull()
            sut.deleteSpecialty(saved)
            sut.findSpecialtyById(specialtyId).shouldBeNull()
        }
    }
})
