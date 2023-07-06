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
import org.springframework.samples.petclinic.mapper.VisitMapper
import org.springframework.samples.petclinic.model.Visit
import org.springframework.samples.petclinic.rest.api.VisitsApi
import org.springframework.samples.petclinic.rest.dto.VisitDto
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate

/**
 * @author Vitaliy Fedoriv
 */
@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("api")
class VisitRestController(
    private val clinicService: ClinicService,
) : VisitsApi {

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun listVisits(): ResponseEntity<List<VisitDto>> {
        val visits: List<Visit> = clinicService.findAllVisits()
        return if (visits.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(VisitMapper.toVisitsDto(visits), HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun getVisit(visitId: Int): ResponseEntity<VisitDto> {
        val visit: Visit = clinicService.findVisitById(visitId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(VisitMapper.toVisitDto(visit), HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun addVisit(visitDto: VisitDto): ResponseEntity<VisitDto> {

        val visit = VisitMapper.toVisit(visitDto)
        clinicService.saveVisit(visit)

        val headers = HttpHeaders().apply {
            location =
                UriComponentsBuilder.newInstance().path("/api/visits/{id}").buildAndExpand(visit.id).toUri()
        }
        return ResponseEntity(VisitMapper.toVisitDto(visit), headers, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun updateVisit(visitId: Int, visitFieldsDto: VisitFieldsDto): ResponseEntity<VisitDto> {
        val currentVisit: Visit = clinicService.findVisitById(visitId)?.apply {
            this.date = visitFieldsDto.date ?: LocalDate.now()
            this.description = visitFieldsDto.description
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        clinicService.saveVisit(currentVisit)
        return ResponseEntity(VisitMapper.toVisitDto(currentVisit), HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Transactional
    override fun deleteVisit(visitId: Int): ResponseEntity<VisitDto> {
        val visit: Visit = clinicService.findVisitById(visitId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.deleteVisit(visit)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
