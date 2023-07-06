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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.PetTypeMapper
import org.springframework.samples.petclinic.model.PetType
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.samples.petclinic.ApplicationTestConfig
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * Test class for [PetTypeRestController]
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
@ContextConfiguration(classes = [ApplicationTestConfig::class])
@WebAppConfiguration
class PetTypeRestControllerTests(
    @Autowired petTypeRestController: PetTypeRestController
) {

    @MockBean
    lateinit var clinicService: ClinicService

    private val mockMvc = MockMvcBuilders.standaloneSetup(petTypeRestController)
        .setControllerAdvice(ExceptionControllerAdvice())
        .build()
    private val petTypes = mutableListOf<PetType>()

    @BeforeEach
    fun initPetTypes() {
        petTypes.add(PetType().apply { id = 1; name = "cat" })
        petTypes.add(PetType().apply { id = 2; name = "dog" })
        petTypes.add(PetType().apply { id = 3; name = "lizard" })
        petTypes.add(PetType().apply { id = 4; name = "snake" })
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetPetTypeSuccessAsOwnerAdmin() {
        BDDMockito.given(clinicService.findPetTypeById(1)).willReturn(petTypes[0])
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pettypes/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("cat"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetPetTypeSuccessAsVetAdmin() {
        BDDMockito.given(clinicService.findPetTypeById(1)).willReturn(petTypes[0])
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pettypes/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("cat"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetPetTypeNotFound() {
        BDDMockito.given(clinicService.findPetTypeById(999)).willReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pettypes/999")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetAllPetTypesSuccessAsOwnerAdmin() {
        petTypes.removeAt(0)
        petTypes.removeAt(1)
        BDDMockito.given(clinicService.findAllPetTypes()).willReturn(petTypes)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pettypes/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("dog"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(4))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("snake"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetAllPetTypesSuccessAsVetAdmin() {
        petTypes.removeAt(0)
        petTypes.removeAt(1)
        BDDMockito.given(clinicService.findAllPetTypes()).willReturn(petTypes)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pettypes/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("dog"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(4))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("snake"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetAllPetTypesNotFound() {
        petTypes.clear()
        BDDMockito.given(clinicService.findAllPetTypes()).willReturn(petTypes)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pettypes/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testCreatePetTypeSuccess() {
        val newPetType = petTypes[0].apply { id = 999 }
        val mapper = ObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(PetTypeMapper.toPetTypeDto(newPetType))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pettypes/")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testCreatePetTypeError() {
        val newPetType = petTypes[0].apply { id = null; name = null }
        val mapper = ObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(PetTypeMapper.toPetTypeDto(newPetType))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/pettypes/")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    @Throws(Exception::class)
    fun testUpdatePetTypeSuccess() {
        BDDMockito.given(clinicService.findPetTypeById(2)).willReturn(petTypes[1])
        val newPetType = petTypes[1].apply { name = "dog I" }
        val mapper = ObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(PetTypeMapper.toNotNullPetTypeDto(newPetType))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/pettypes/2")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/pettypes/2")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("dog I"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testUpdatePetTypeError() {
        val newPetType = petTypes[0].apply { name = "" }
        val mapper = ObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(PetTypeMapper.toNotNullPetTypeDto(newPetType))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/pettypes/1")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testDeletePetTypeSuccess() {
        val newPetType = petTypes[0]
        val mapper = ObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(newPetType)
        BDDMockito.given(clinicService.findPetTypeById(1)).willReturn(petTypes[0])
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/pettypes/1")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testDeletePetTypeError() {
        val newPetType = petTypes[0]
        val mapper = ObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(PetTypeMapper.toPetTypeDto(newPetType))
        BDDMockito.given(clinicService.findPetTypeById(999)).willReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/pettypes/999")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }
}
