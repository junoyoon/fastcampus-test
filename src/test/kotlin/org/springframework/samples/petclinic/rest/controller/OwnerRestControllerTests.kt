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
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.OwnerMapper
import org.springframework.samples.petclinic.mapper.VisitMapper
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.samples.petclinic.rest.dto.PetTypeDto
import org.springframework.samples.petclinic.rest.dto.VisitDto
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.SimpleDateFormat
import java.time.LocalDate

/**
 * Test class for [OwnerRestController]
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
class OwnerRestControllerTests(
    @Autowired ownerRestController: OwnerRestController,
    @Autowired @MockBean var clinicService: ClinicService
) {
    private var mockMvc = MockMvcBuilders.standaloneSetup(ownerRestController)
        .setControllerAdvice(ExceptionControllerAdvice())
        .build()
    private var owners = listOf(
        OwnerDto(
            id = 1,
            firstName = "George",
            lastName = "Franklin",
            address = "110 W. Liberty St.",
            city = "Maison",
            telephone = "6085551023",
            pets = listOf(getTestPetWithIdAndName(1, "Rosy"))
        ),
        OwnerDto(
            id = 2, firstName = "Betty", lastName = "Davis",
            address = "638 Cardinal Ave.", city = "Sun Prairie", telephone = "6085551749"
        ),
        OwnerDto(
            id = 3, firstName = "Eduardo", lastName = "Rodriquez",
            address = "2693 Commerce St.", city = "McFarland", telephone = "6085558763"
        ),
        OwnerDto(
            id = 4, firstName = "Harold", lastName = "Davis",
            address = "563 Friendly St.", city = "Windsor", telephone = "6085553198"
        )
    )
    private var pets = listOf(
        PetDto(id = 3, name = "Rosy", birthDate = LocalDate.now(), type = PetTypeDto(id = 2, name = "dog")),
        PetDto(id = 4, name = "Jewel", birthDate = LocalDate.now(), type = PetTypeDto(id = 2, name = "dog"))
    )
    private var visits = listOf(
        VisitDto(id = 2, petId = 4, date = LocalDate.now(), description = "rabies shot"),
        VisitDto(id = 3, petId = 4, date = LocalDate.now(), description = "neutered shot")
    )

    private fun getTestPetWithIdAndName(id: Int, name: String): PetDto {
        val petType = PetTypeDto(id = 2, name = "dog")
        return PetDto(id = id, name = name, birthDate = LocalDate.now(), type = petType)
    }


    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetOwnerSuccess() {
        whenever(clinicService.findOwnerById(1)).thenReturn(OwnerMapper.toOwner(owners[0]))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/owners/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("George"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetOwnerNotFound() {
        whenever(clinicService.findOwnerById(2)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/owners/2")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetOwnersListSuccess() {
        whenever(clinicService.findOwnerByLastName("Davis")).thenReturn(OwnerMapper.toOwners(owners.drop(2)))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/owners?lastName=Davis")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].firstName").value("Eduardo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(4))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].firstName").value("Harold"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetOwnersListNotFound() {
        whenever(clinicService.findOwnerByLastName("0")).thenReturn(OwnerMapper.toOwners(emptyList()))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/owners/?lastName=0")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetAllOwnersSuccess() {
        whenever(clinicService.findAllOwners()).thenReturn(OwnerMapper.toOwners(owners.drop(2)))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/owners/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].firstName").value("Eduardo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(4))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].firstName").value("Harold"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testGetAllOwnersNotFound() {
        whenever(clinicService.findAllOwners()).thenReturn(OwnerMapper.toOwners(emptyList()))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/owners/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testCreateOwnerSuccess() {
        val newOwnerDto = owners[0].copy(id = null)
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/owners/")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testCreateOwnerError() {
        // firstName 이 null 인 테스트였다,
        val newOwnerDto = owners[0].copy(id = null, firstName = "")
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/owners/")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testUpdateOwnerSuccess() {
        whenever(clinicService.findOwnerById(1)).thenReturn(OwnerMapper.toOwner(owners[0]))
        val ownerId = owners[0].id
        val updatedOwnerDto = OwnerDto(
            // body.id = ownerId which is used in url path
            id = ownerId,
            firstName = "GeorgeI",
            lastName = "Franklin",
            address = "110 W. Liberty St.",
            city = "Madison",
            telephone = "6085551023",
        )

        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newOwnerAsJSON = mapper.writeValueAsString(updatedOwnerDto)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/owners/$ownerId")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/owners/$ownerId")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ownerId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("GeorgeI"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testUpdateOwnerSuccessNoBodyId() {
        whenever(clinicService.findOwnerById(1)).thenReturn(OwnerMapper.toOwner(owners[0]))
        val ownerId = owners[0].id
        val updatedOwnerDto = OwnerDto(
            firstName = "GeorgeI",
            lastName = "Franklin",
            address = "110 W. Liberty St.",
            city = "Madison",
            telephone = "6085551023"
        )

        val mapper = ObjectMapper()
        val newOwnerAsJSON = mapper.writeValueAsString(updatedOwnerDto)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/owners/$ownerId")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/owners/$ownerId")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ownerId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("GeorgeI"))
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testUpdateOwnerError() {
        val newOwnerDto = owners[0].copy(firstName = "")
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/owners/1")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testDeleteOwnerSuccess() {
        val newOwnerDto = owners[0]
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto)
        val owner = OwnerMapper.toOwner(owners[0])
        whenever(clinicService.findOwnerById(1)).thenReturn(owner)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/owners/1")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testDeleteOwnerError() {
        val newOwnerDto = owners[0]
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val newOwnerAsJSON = mapper.writeValueAsString(newOwnerDto)
        whenever(clinicService.findOwnerById(999)).thenReturn(null)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/owners/999")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testCreatePetSuccess() {
        val newPet = pets[0].copy(id = 999)
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.setDateFormat(SimpleDateFormat("yyyy-MM-dd"))
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        System.err.println("--> newPetAsJSON=$newPetAsJSON")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/owners/1/pets/")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testCreatePetError() {
        // name 이 null 인 테스트임.
        val newPet = pets[0].copy(id = 0, name = "")
        val mapper = ObjectMapper()
        mapper.setDateFormat(SimpleDateFormat("yyyy-MM-dd"))
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.registerModule(JavaTimeModule())
        val newPetAsJSON = mapper.valueToTree<ObjectNode>(newPet)
            .remove("name").toString()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/owners/1/pets/")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest()).andDo(MockMvcResultHandlers.print())
    }

    @Test
    @WithMockUser(roles = ["OWNER_ADMIN"])
    fun testCreateVisitSuccess() {
        val newVisit = visits[0].copy(id = 999)
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val newVisitAsJSON = mapper.writeValueAsString(VisitMapper.toVisit(newVisit))
        println("newVisitAsJSON $newVisitAsJSON")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/owners/1/pets/1/visits")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }
}
