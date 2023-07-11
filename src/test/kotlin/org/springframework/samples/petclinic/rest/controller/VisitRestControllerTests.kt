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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.VisitMapper
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.model.Pet
import org.springframework.samples.petclinic.model.PetType
import org.springframework.samples.petclinic.model.Visit
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate

/**
 * Test class for [VisitRestController]
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
class VisitRestControllerTests(
    @Autowired visitRestController: VisitRestController,
    @Autowired @MockBean val clinicService: ClinicService
) {
    private val mockMvc = MockMvcBuilders.standaloneSetup(visitRestController)
        .setControllerAdvice(ExceptionControllerAdvice())
        .build()
    private var visits = mutableListOf<Visit>()

    @BeforeEach
    fun initVisits() {
        val owner = Owner(
            id = 1,
            firstName = "Eduardo",
            lastName = "Rodriquez",
            address = "2693 Commerce St.",
            city = "McFarland",
            telephone = "6085558763",
        )

        val pet = Pet(
            id = 8,
            name = "Rosy",
            birthDate = LocalDate.now(),
            owner = owner,
            type = PetType(id = 2, name = "dof"),
        )

        visits.add(Visit(
            id = 2,
            pet = pet,
            date = LocalDate.now(),
            description = "rabies shot"
        ))
        visits.add(Visit(
            id = 3,
            pet = pet,
            date = LocalDate.now(),
            description = "neutered",
        ))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetVisitSuccess() {
        whenever(clinicService.findVisitById(2)).thenReturn(visits[0])
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/visits/2")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("rabies shot"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetVisitNotFound() {
        whenever(clinicService.findVisitById(999)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/visits/999")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetAllVisitsSuccess() {
        whenever(clinicService.findAllVisits()).thenReturn(visits)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/visits/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("rabies shot"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value("neutered"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetAllVisitsNotFound() {
        whenever(clinicService.findAllVisits()).thenReturn(emptyList())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/visits/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testCreateVisitSuccess() {
        val newVisit = visits[0].apply { id = 999 }
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newVisitAsJSON = mapper.writeValueAsString(VisitMapper.toVisitDto(newVisit))
        println("newVisitAsJSON $newVisitAsJSON")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/visits/")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testCreateVisitError() {
        val newVisit = visits[0].apply { id = null; description = null }
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newVisitAsJSON = mapper.writeValueAsString(VisitMapper.toVisitDto(newVisit))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/visits/")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testUpdateVisitSuccess() {
        whenever(clinicService.findVisitById(2)).thenReturn(visits[0])
        val newVisit = visits[0].apply { description = "rabies shot test" }
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newVisitAsJSON = mapper.writeValueAsString(VisitMapper.toVisitDto(newVisit))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/visits/2")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("rabies shot test"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testUpdateVisitError() {
        val newVisit = visits[0].apply { description = null }
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newVisitAsJSON = mapper.writeValueAsString(VisitMapper.toVisitDto(newVisit))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testDeleteVisitSuccess() {
        val newVisit = visits[0]
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newVisitAsJSON = mapper.writeValueAsString(VisitMapper.toVisitDto(newVisit))
        whenever(clinicService.findVisitById(2)).thenReturn(visits[0])
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testDeleteVisitError() {
        val newVisit = visits[0]
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newVisitAsJSON = mapper.writeValueAsString(VisitMapper.toVisitDto(newVisit))
        whenever(clinicService.findVisitById(999)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/visits/999")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }
}
