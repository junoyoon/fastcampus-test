/*
 * Copyright 2016-2017 the original author or authors.
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
package org.springframework.samples.petclinic.rest.controller

import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.PetMapper
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.samples.petclinic.rest.dto.PetTypeDto
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate

/**
 * Test class for [PetRestController]
 *
 * @author Vitaliy Fedoriv
 */
@WebMvcTest(PetRestController::class)
class PetRestControllerWebMvcTests(
    @Autowired val mockMvc : MockMvc,
    @Autowired @MockBean val clinicService: ClinicService
) {

    private val pets = listOf(
        PetDto(
            id = 3, name = "Rosy", birthDate = LocalDate.now(),
            type = PetTypeDto(id = 2, name = "dog"), visits = emptyList()
        ),
        PetDto(id = 4, name = "Jewel", birthDate = LocalDate.now(), type = PetTypeDto(id = 2, name = "dog"))
    )

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetPetSuccess() {
        whenever(clinicService.findPetById(3)).thenReturn(PetMapper.toPet(pets[0]))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Rosy"))
    }
}
