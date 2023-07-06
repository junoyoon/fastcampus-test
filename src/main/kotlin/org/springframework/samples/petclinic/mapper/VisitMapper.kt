package org.springframework.samples.petclinic.mapper

import org.springframework.samples.petclinic.model.Visit
import org.springframework.samples.petclinic.rest.dto.VisitDto
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto
import java.time.LocalDate

/**
 * Map Visit & VisitDto using mapstruct
 */
object VisitMapper {
    fun toVisit(visitDto: VisitDto): Visit {
        return Visit().apply {
            this.id = visitDto.id
            this.description = visitDto.description
            this.date = visitDto.date ?: LocalDate.now()
        }
    }

    fun toVisit(visitFieldsDto: VisitFieldsDto): Visit {
        return Visit().apply {
            this.description = visitFieldsDto.description
            this.date = visitFieldsDto.date ?: LocalDate.now()
        }
    }

    fun toVisitDto(visit: Visit): VisitDto {
        return VisitDto(
            id = visit.id ?: 0,
            description = visit.description.orEmpty(),
            date = visit.date,
            petId = visit.pet?.id
        )
    }

    fun toVisitsDto(visits: List<Visit>): List<VisitDto> {
        return visits.map { toVisitDto(it) }
    }
}
