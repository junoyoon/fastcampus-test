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

import jakarta.transaction.Transactional
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.mapper.SpecialtyMapper
import org.springframework.samples.petclinic.model.Specialty
import org.springframework.samples.petclinic.rest.api.SpecialtiesApi
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto
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
class SpecialtyRestController(
    private val clinicService: ClinicService,
) : SpecialtiesApi {

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun listSpecialties(): ResponseEntity<List<SpecialtyDto>> {
        val specialties = SpecialtyMapper.toSpecialtyDtos(clinicService.findAllSpecialties())
        return if (specialties.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(specialties, HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun getSpecialty(specialtyId: Int): ResponseEntity<SpecialtyDto> {
        val specialty: Specialty = clinicService.findSpecialtyById(specialtyId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(SpecialtyMapper.toSpecialtyDto(specialty), HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun addSpecialty(specialtyDto: SpecialtyDto): ResponseEntity<SpecialtyDto> {

        val specialty = SpecialtyMapper.toSpecialty(specialtyDto)
        clinicService.saveSpecialty(specialty)
        val headers = HttpHeaders().apply {
            location =
                UriComponentsBuilder.newInstance()
                    .path("/api/specialties/{id}")
                    .buildAndExpand(specialty.id).toUri()
        }
        return ResponseEntity(SpecialtyMapper.toSpecialtyDto(specialty), headers, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun updateSpecialty(specialtyId: Int, specialtyDto: SpecialtyDto): ResponseEntity<SpecialtyDto> {
        val currentSpecialty: Specialty = clinicService.findSpecialtyById(specialtyId)?.apply {
            name = specialtyDto.name
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        clinicService.saveSpecialty(currentSpecialty)
        return ResponseEntity(SpecialtyMapper.toSpecialtyDto(currentSpecialty), HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    override fun deleteSpecialty(specialtyId: Int): ResponseEntity<SpecialtyDto> {
        val specialty: Specialty = clinicService.findSpecialtyById(specialtyId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.deleteSpecialty(specialty)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
