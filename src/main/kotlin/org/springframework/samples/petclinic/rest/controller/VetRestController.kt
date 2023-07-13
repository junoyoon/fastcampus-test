/*
 * Copyright 2016-2018 the original author or authors.
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

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.mapper.SpecialtyMapper
import org.springframework.samples.petclinic.mapper.VetMapper
import org.springframework.samples.petclinic.model.Vet
import org.springframework.samples.petclinic.rest.api.VetsApi
import org.springframework.samples.petclinic.rest.dto.VetDto
import org.springframework.samples.petclinic.rest.dto.VetFieldsDto
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

/**
 * @author Vitaliy Fedoriv
 */
@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("api")
class VetRestController(
    private val clinicService: ClinicService,
) : VetsApi {

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun listVets(): ResponseEntity<List<VetDto>> {
        val vets = VetMapper.toVetDtos(clinicService.findAllVets())
        return if (vets.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(vets, HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun getVet(vetId: Int): ResponseEntity<VetDto> {
        val vet: Vet = clinicService.findVetById(vetId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(VetMapper.toVetDto(vet), HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun addVet(@Valid vetDto: VetDto): ResponseEntity<VetDto> {
        val vet = VetMapper.toVet(vetDto)
        clinicService.saveVet(vet)
        val headers = HttpHeaders().apply {
            location =
                UriComponentsBuilder.newInstance()
                    .path("/api/vets/{id}").buildAndExpand(vet.id).toUri()
        }
        return ResponseEntity(VetMapper.toVetDto(vet), headers, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun updateVet(vetId: Int, vetDto: VetFieldsDto): ResponseEntity<VetDto> {
        val currentVet: Vet = clinicService.findVetById(vetId)?.apply {
            firstName = vetDto.firstName
            lastName = vetDto.lastName
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        currentVet.clearSpecialties()
        for (spec in SpecialtyMapper.toSpecialtys(vetDto.specialties)) {
            currentVet.addSpecialty(spec)
        }
        clinicService.saveVet(currentVet)
        return ResponseEntity(VetMapper.toVetDto(currentVet), HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    override fun deleteVet(vetId: Int): ResponseEntity<VetDto> {
        val vet: Vet = clinicService.findVetById(vetId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.deleteVet(vet)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
