package org.springframework.samples.petclinic.mapper

import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto

/**
 * Maps Owner & OwnerDto using Mapstruct
 */
object OwnerMapper {
    fun toOwnerDto(owner: Owner): OwnerDto {
        return OwnerDto(
            firstName = owner.firstName.orEmpty(),
            lastName = owner.lastName.orEmpty(),
            address = owner.address,
            city = owner.city,
            telephone = owner.telephone,
            pets = PetMapper.toPetsDto(owner.pets.toList()),
            id = owner.id
        )
    }

    fun toOwner(ownerDto: OwnerDto): Owner {
        return Owner().apply {
            id = ownerDto.id
            firstName = ownerDto.firstName
            lastName = ownerDto.lastName
            address = ownerDto.address
            city = ownerDto.city
            telephone = ownerDto.telephone
            pets = PetMapper.toPets(ownerDto.pets.orEmpty()).toMutableSet()
        }
    }

    fun toOwner(ownerDto: OwnerFieldsDto): Owner {
        return Owner().apply {
            firstName = ownerDto.firstName
            lastName = ownerDto.lastName
            address = ownerDto.address
            city = ownerDto.city
            telephone = ownerDto.telephone
        }
    }

    fun toOwnerDtoCollection(ownerCollection: List<Owner>): List<OwnerDto> {
        return ownerCollection.map { toOwnerDto(it) }
    }

    fun toOwners(ownerDtos: List<OwnerDto>): List<Owner> {
        return ownerDtos.map { toOwner(it) }
    }
}
