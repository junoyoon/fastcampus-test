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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.PetMapper
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.samples.petclinic.rest.dto.PetTypeDto
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.time.LocalDate

/**
 * Test class for [PetRestController]
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
class PetRestControllerTests(
    @Autowired petRestController: PetRestController,
    @Autowired @MockBean val clinicService: ClinicService
) {

    private val mockMvc = MockMvcBuilders.standaloneSetup(petRestController)
        .setControllerAdvice(ExceptionControllerAdvice())
        .build()

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

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetPetNotFound() {
        whenever(PetMapper.toNullablePetDto(clinicService.findPetById(-1))).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pets/999")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetAllPetsSuccess() {
        val pets = PetMapper.toPets(pets)
        whenever(clinicService.findAllPets()).thenReturn(pets)
        //given(this.clinicService.findAllPets()).thenReturn(PetMapper.toPets(pets));
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Rosy"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(4))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("Jewel"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetAllPetsNotFound() {
        whenever(clinicService.findAllPets()).thenReturn(PetMapper.toPets(emptyList()))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testUpdatePetSuccess() {
        whenever(clinicService.findPetById(3)).thenReturn(PetMapper.toPet(pets[0]))
        val newPet = pets[0].copy(name = "Rosy I")
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.setDateFormat(SimpleDateFormat("yyyy-MM-dd"))
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/pets/3")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Rosy I"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testUpdatePetError() {
        // 여기를 null 을 넣으면 에러인데.. 재현을 못하겠다.
        val newPet = pets[0].copy(name = "")
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.setDateFormat(SimpleDateFormat("yyyy-MM-dd"))
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newPetAsJSON = mapper.valueToTree<ObjectNode>(newPet)
            .set<ObjectNode>("name", NullNode.instance).toString()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/pets/3")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testDeletePetSuccess() {
        val newPet = pets[0]
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        whenever(clinicService.findPetById(3)).thenReturn(PetMapper.toPet(pets[0]))
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/pets/3")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testDeletePetError() {
        val newPet = pets[0]
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        whenever(clinicService.findPetById(999)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/pets/999")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }
}
