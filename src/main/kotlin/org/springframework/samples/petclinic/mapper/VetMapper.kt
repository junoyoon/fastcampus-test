package org.springframework.samples.petclinic.mapper


import org.springframework.samples.petclinic.model.Vet
import org.springframework.samples.petclinic.rest.dto.VetDto

/**
 * Map Vet & VetoDto using mapstruct
 */
object VetMapper {
    fun toVet(vetDto: VetDto): Vet {
        return Vet(
            id = vetDto.id,
            lastName = vetDto.lastName,
            firstName = vetDto.firstName,
        ).apply {
            this.setSpecialties(SpecialtyMapper.toSpecialtys(vetDto.specialties))
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
