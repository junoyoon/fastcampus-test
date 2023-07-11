package org.springframework.samples.petclinic.mapper

import org.springframework.samples.petclinic.model.Specialty
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto

/**
 * Map Specialty & SpecialtyDto using mapstruct
 */
object SpecialtyMapper {
    fun toSpecialty(specialtyDto: SpecialtyDto): Specialty {
        return Specialty(
            id = specialtyDto.id,
            name = specialtyDto.name,
        )
    }

    fun toSpecialtyDto(specialty: Specialty): SpecialtyDto {
        return SpecialtyDto(
            id = specialty.id ?: 0,
            name = specialty.name.orEmpty()
        )
    }

    fun toSpecialtyDtos(specialties: List<Specialty>): List<SpecialtyDto> {
        return specialties.map { toSpecialtyDto(it) }
    }

    fun toSpecialtys(specialties: List<SpecialtyDto>): List<Specialty> {
        return specialties.map { toSpecialty(it) }
    }
}
