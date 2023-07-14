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
package org.springframework.samples.petclinic.backup

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
/*
 junit & assertj 실습
 1. Owner 테스트와 Pet 테스트를 각각 별도의 Nested Test 로 분리
 2. Junit assertion 을 assertj 로 변경
 3. findAllOwner 를 @CsvSource 기반의 Parameterized Test 로 변경

 kotest & kotest assertion 실습
 1. kotest fun spec 으로 컨버팅
 2. Kotest assertion 사용
 3. @Nested => context
 */
@SpringBootTest
class ClinicServiceSpringDataJpaTests(
    @Autowired val clinicService: ClinicService,
) {

    // owner 테스트
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
            assertThat(owner!!.lastName!!).startsWith("Franklin")
            assertThat(owner.getPets()).hasSize(1)
            assertThat(owner.getPets()[0].type).isNotNull()
            assertThat(owner.getPets()[0].type!!.name).isEqualTo("cat")
        }

        @Test
        @Transactional
        fun insertOwner() {
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


        // FIXME: 이 부분을 Parameterized Test 로 교체 (CsvSource)
        @ParameterizedTest
        @CsvSource(value = ["1, George", "3, Eduardo"])
        fun findAllOwners(id : Int, firstName: String) {
            val owners = clinicService.findAllOwners()
            val owner1 = owners.first { it.id == id }
            assertThat(owner1.firstName).isEqualTo(firstName)
        }

        @Test
        @Transactional
        fun deleteOwner() {
            val owner = clinicService.findOwnerById(1)!!
            clinicService.deleteOwner(owner)
            assertThat(clinicService.findOwnerById(1)).isNull()
        }
    }
    // pet 테스트

    @Nested
    inner class PetTest {
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

}
