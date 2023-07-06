package org.springframework.samples.petclinic.mapper


import org.springframework.samples.petclinic.model.Vet
import org.springframework.samples.petclinic.rest.dto.VetDto
import org.springframework.samples.petclinic.rest.dto.VetFieldsDto

/**
 * Map Vet & VetoDto using mapstruct
 */
object VetMapper {
    fun toVet(vetDto: VetDto): Vet {
        return Vet().apply {
            this.id = vetDto.id
            this.lastName = vetDto.lastName
            this.firstName = vetDto.firstName
            this.setSpecialties(SpecialtyMapper.toSpecialtys(vetDto.specialties))
        }
    }

    fun toVet(vetFieldsDto: VetFieldsDto): Vet {
        return Vet().apply {
            this.lastName = vetFieldsDto.lastName
            this.firstName = vetFieldsDto.firstName
            this.setSpecialties(SpecialtyMapper.toSpecialtys(vetFieldsDto.specialties))
        }
    }

    fun toVetDto(vet: Vet): VetDto {
        return VetDto(
            id = vet.id ?: 0,
            firstName = vet.firstName.orEmpty(),
            lastName = vet.lastName.orEmpty(),
            specialties = SpecialtyMapper.toSpecialtyDtos(vet.getSpecialties())
        )
    }

    fun toVetDtos(vets: List<Vet>): List<VetDto> {
        return vets.map { toVetDto(it) }
    }
}
