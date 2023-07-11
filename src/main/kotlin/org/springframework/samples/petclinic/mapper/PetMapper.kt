package org.springframework.samples.petclinic.mapper

import org.springframework.samples.petclinic.model.Pet
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto
import java.time.LocalDate

/**
 * Map Pet & PetDto using mapstruct
 */
object PetMapper {
    fun toNullablePetDto(pet: Pet?): PetDto? {
        if (pet == null) {
            return null
        }
        return toPetDto(pet)
    }

    fun toPetDto(pet: Pet): PetDto {
        return PetDto(
            id = pet.id ?: 0,
            name = pet.name.orEmpty(),
            birthDate = pet.birthDate ?: LocalDate.now(),
            type = PetTypeMapper.toPetTypeDto(pet.type)!!,
            visits = pet.getVisits().map { VisitMapper.toVisitDto(it) },
            ownerId = pet.owner?.id
        )
    }

    fun toPetsDto(pets: List<Pet>): List<PetDto> {
        return pets.map { toPetDto(it) }
    }

    fun toPets(pets: List<PetDto>): List<Pet> {
        return pets.map { toPet(it) }
    }

    fun toPet(petDto: PetDto): Pet {
        return Pet(
            id = petDto.id,
            name = petDto.name,
            type = PetTypeMapper.toPetType(petDto.type),
            birthDate = petDto.birthDate,
        ).apply {
            this.setVisits(petDto.visits?.map { VisitMapper.toVisit(it) })
        }
    }

    fun toPet(petFieldsDto: PetFieldsDto): Pet {
        return Pet(
            name = petFieldsDto.name,
            type = PetTypeMapper.toPetType(petFieldsDto.type),
            birthDate = petFieldsDto.birthDate,
        )
    }
}
