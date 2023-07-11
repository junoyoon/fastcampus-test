package org.springframework.samples.petclinic.mapper

import org.springframework.samples.petclinic.model.PetType
import org.springframework.samples.petclinic.rest.dto.PetTypeDto

/**
 * Map PetType & PetTypeDto using mapstruct
 */
object PetTypeMapper {
    fun toPetType(petTypeDto: PetTypeDto?): PetType? {
        if (petTypeDto == null) {
            return null
        }
        return PetType(
            id = petTypeDto.id,
            name = petTypeDto.name,
        )
    }

    fun toPetTypeDto(petType: PetType?): PetTypeDto? {
        if (petType == null) {
            return null
        }
        return toNotNullPetTypeDto(petType)
    }


    fun toNotNullPetTypeDto(petType: PetType): PetTypeDto {
        return PetTypeDto(
            id = petType.id ?: 0,
            name = petType.name.orEmpty(),
        )
    }

    fun toPetTypeDtos(petTypes: List<PetType>): List<PetTypeDto> {
        return petTypes.map { toNotNullPetTypeDto(it) }
    }
}
