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
package org.springframework.samples.petclinic.practice

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.samples.petclinic.SpringFunSpec
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.model.Pet
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

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
 * we don't need to perform application context lookups. See the use of [@Autowired][Autowired] on the `[ ][ClinicServiceSpringDataJpaKoTests.clinicService]` instance variable, which uses autowiring *by
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
/*
 junit & assertj 실습
 1. Owner 테스트와 Pet 테스트를 각각 별도의 Nested Test 로 분리
 2. Junit assertion 을 assertj 로 변경
 3. findAllOwners 를 @CsvSource 기반의 Parameterized Test 로 변경

 kotest & kotest assertion 실습
 1. kotest fun spec 으로 컨버팅
 2. Kotest assertion 사용
 3. @Nested => context
 */
@Transactional
@SpringBootTest
class ClinicServiceSpringDataJpaKoTests(
    @Autowired val clinicService: ClinicService,
) : SpringFunSpec({
    // owner 테스트
    context("owner") {
        test("findOwnerByLastName") {
            clinicService.findOwnerByLastName("Davis")
                .shouldHaveSize(2)
            clinicService.findOwnerByLastName("Daviss")
                .shouldBeEmpty()
        }

        test("findOwnerById_withPet") {
            clinicService.findOwnerById(1)
                .shouldNotBeNull()
                .should {
                    it.lastName shouldStartWith "Franklin"
                    it.getPets().shouldHaveSize(1)
                    it.getPets()[0].type.shouldNotBeNull().name shouldBe "cat"
                }
        }

        test("insertOwner") {
            val owners = clinicService.findOwnerByLastName("Schultz")
            //FIXME 이 부분을 FixtureMonkey 로 생성해 보기
            val owner = Owner(
                firstName = "Sam",
               lastName = "Schultz",
                address = "4, Evans Street",
                city = "Wollongong",
                telephone = "4444444444",
            )

            clinicService.saveOwner(owner)
            owner.id.shouldNotBe(0)
            owner.getPet("null value").shouldBeNull()
            clinicService.findOwnerByLastName("Schultz").shouldHaveSize(owners.size + 1)
        }

        test("updateOwner") {
            val owner = clinicService.findOwnerById(1)!!.apply { lastName += "X" }
            clinicService.saveOwner(owner)

            // retrieving new name from database
            clinicService.findOwnerById(1).shouldNotBeNull().lastName shouldBe  owner.lastName
        }


        // FIXME: 이 부분을 Parameterized Test 로 교체 (CsvSource)

        table(
            headers("id", "firstName"),
            row(1, "George"),
            row(3, "Eduardo")
        ).forAll { id, firstName ->
            test("findAllOwners $id, $firstName") {
                val owners = clinicService.findAllOwners()
                val owner1 = owners.first { it.id == id }
                owner1.firstName shouldBe firstName
            }
        }

        test("deleteOwner") {
            val owner = clinicService.findOwnerById(1)!!
            clinicService.deleteOwner(owner)
            clinicService.findOwnerById(1).shouldBeNull()
        }
    }

    // pet 테스트
    context("pet") {
        test("savePet_petName") {
            val pet7: Pet = clinicService.findPetById(7)!!.apply { name += "X" }
            clinicService.savePet(pet7)
            clinicService.findPetById(7).shouldNotBeNull().name shouldBe  pet7.name
        }

        test("findAllPets") {
            val pets = clinicService.findAllPets()
            val pet1 = pets.first { it.id == 1 }
            pet1.name shouldBe "Leo"
            val pet3 = pets.first { it.id == 3 }
            pet3.name shouldBe "Rosy"
        }

        test("deletePet") {
            val pet = clinicService.findPetById(1)!!
            clinicService.deletePet(pet)
            clinicService.findPetById(1).shouldBeNull()
        }
    }
})
