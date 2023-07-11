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
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.VetMapper
import org.springframework.samples.petclinic.model.Vet
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * Test class for [VetRestController]
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
class VetRestControllerTests(
    @Autowired vetRestController: VetRestController,
    @Autowired @MockBean val clinicService: ClinicService
) {
    private val mockMvc = MockMvcBuilders.standaloneSetup(vetRestController)
        .setControllerAdvice(ExceptionControllerAdvice())
        .build()

    private var vets = listOf(
        Vet( id = 1, firstName = "James", lastName = "Carter" ),
        Vet(id = 2, firstName = "Helen", lastName = "Leary" ),
        Vet( id = 3, firstName = "Linda", lastName = "Douglas" )
    )


    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetVetSuccess() {
        whenever(clinicService.findVetById(1)).thenReturn(vets[0])
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/vets/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("James"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetVetNotFound() {
        whenever(clinicService.findVetById(-1)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/vets/999")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetAllVetsSuccess() {
        whenever(clinicService.findAllVets()).thenReturn(vets)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/vets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].firstName").value("James"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].firstName").value("Helen"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetAllVetsNotFound() {
        whenever(clinicService.findAllVets()).thenReturn(emptyList())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/vets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testCreateVetSuccess() {
        val newVet = Vet(id = 999, lastName = vets[0].lastName, firstName = vets[0].firstName)
        val mapper = ObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(VetMapper.toVetDto(newVet))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/vets/")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testCreateVetError() {
        val newVet = Vet(id = null, lastName = vets[0].lastName, firstName = null)
        val mapper = ObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(VetMapper.toVetDto(newVet))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/vets/")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testUpdateVetSuccess() {
        whenever(clinicService.findVetById(1)).thenReturn(vets[0])
        val newVet =  Vet(id = vets[0].id, lastName = vets[0].lastName, firstName = "James")
        val mapper = ObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(VetMapper.toVetDto(newVet))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/vets/1")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/vets/1")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("James"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testUpdateVetError() {
        val newVet =  Vet(id = vets[0].id, lastName = vets[0].lastName, firstName = null)
        val mapper = ObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(VetMapper.toVetDto(newVet))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/vets/1")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testDeleteVetSuccess() {
        val newVet = vets[0]
        val mapper = ObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(VetMapper.toVetDto(newVet))
        whenever(clinicService.findVetById(1)).thenReturn(vets[0])
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/vets/1")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testDeleteVetError() {
        val newVet = vets[0]
        val mapper = ObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(VetMapper.toVetDto(newVet))
        whenever(clinicService.findVetById(-1)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/vets/999")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }
}
