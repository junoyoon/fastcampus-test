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
import org.springframework.samples.petclinic.ApplicationTestConfig
import org.springframework.samples.petclinic.mapper.SpecialtyMapper
import org.springframework.samples.petclinic.model.Specialty
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * Test class for [SpecialtyRestController]
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
@ContextConfiguration(classes = [ApplicationTestConfig::class])
@WebAppConfiguration
class SpecialtyRestControllerTests(
    @Autowired specialtyRestController: SpecialtyRestController,
    @Autowired @MockBean val clinicService: ClinicService
) {
    private val mockMvc = MockMvcBuilders.standaloneSetup(specialtyRestController)
        .setControllerAdvice(ExceptionControllerAdvice())
        .build()

    private val specialties = listOf(
        Specialty( id = 1, name = "radiology" ),
        Specialty( id = 2, name = "surgery" ),
        Specialty( id = 3, name = "dentistry" ),
    )

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetSpecialtySuccess() {
        whenever(clinicService.findSpecialtyById(1)).thenReturn(specialties[0])
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/specialties/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("radiology"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetSpecialtyNotFound() {
        whenever(clinicService.findSpecialtyById(999)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/specialties/999")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetAllSpecialtiesSuccess() {
        whenever(clinicService.findAllSpecialties()).thenReturn(specialties.drop(1))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/specialties/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("surgery"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("dentistry"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testGetAllSpecialtiesNotFound() {
        whenever(clinicService.findAllSpecialties()).thenReturn(emptyList())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/specialties/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testCreateSpecialtySuccess() {
        val newSpecialty = Specialty( id = 999, name = specialties[0].name )
        val mapper = ObjectMapper()
        val newSpecialtyAsJSON = mapper.writeValueAsString(SpecialtyMapper.toSpecialtyDto(newSpecialty))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/specialties/")
                .content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testCreateSpecialtyError() {
        val newSpecialty = Specialty( id = null, name = null )
        val mapper = ObjectMapper()
        val newSpecialtyAsJSON = mapper.writeValueAsString(SpecialtyMapper.toSpecialtyDto(newSpecialty))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/specialties/")
                .content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testUpdateSpecialtySuccess() {
        whenever(clinicService.findSpecialtyById(2)).thenReturn(specialties[1])
        val newSpecialty = specialties[1].apply { name = "surgery I" }
        val mapper = ObjectMapper()
        val newSpecialtyAsJSON = mapper.writeValueAsString(SpecialtyMapper.toSpecialtyDto(newSpecialty))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/specialties/2")
                .content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/specialties/2")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("surgery I"))
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testUpdateSpecialtyError() {
        val newSpecialty = Specialty(name =  "", id = 1)
        val mapper = ObjectMapper()
        val newSpecialtyAsJSON = mapper.writeValueAsString(SpecialtyMapper.toSpecialtyDto(newSpecialty))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/specialties/1")
                .content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testDeleteSpecialtySuccess() {
        val newSpecialty = specialties[0]
        val mapper = ObjectMapper()
        val newSpecialtyAsJSON = mapper.writeValueAsString(SpecialtyMapper.toSpecialtyDto(newSpecialty))
        whenever(clinicService.findSpecialtyById(1)).thenReturn(specialties[0])
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/specialties/1")
                .content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testDeleteSpecialtyError() {
        val newSpecialty = specialties[0]
        val mapper = ObjectMapper()
        val newSpecialtyAsJSON = mapper.writeValueAsString(SpecialtyMapper.toSpecialtyDto(newSpecialty))
        whenever(clinicService.findSpecialtyById(999)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/specialties/999")
                .content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }
}
