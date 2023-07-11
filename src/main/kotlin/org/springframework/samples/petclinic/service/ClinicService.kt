/*
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.samples.petclinic.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.samples.petclinic.model.*
import org.springframework.samples.petclinic.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Mostly used as a facade for all Petclinic controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@Service
class ClinicService(
    private val petRepository: PetRepository,
    private val vetRepository: VetRepository,
    private val ownerRepository: OwnerRepository,
    private val visitRepository: VisitRepository,
    private val specialtyRepository: SpecialtyRepository,
    private val petTypeRepository: PetTypeRepository
) {
    @Transactional(readOnly = true)
    fun findAllPets(): List<Pet> {
        return petRepository.findAll()
    }

    @Transactional
    fun deletePet(pet: Pet) {
        petRepository.delete(pet)
    }

    @Transactional(readOnly = true)
    fun findVisitById(visitId: Int): Visit? {
        return try {
            visitRepository.findById(visitId)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @Transactional(readOnly = true)
    fun findAllVisits(): List<Visit> {
        return visitRepository.findAll()
    }

    @Transactional
    fun deleteVisit(visit: Visit) {
        visitRepository.delete(visit)
    }

    @Transactional(readOnly = true)
    fun findVetById(id: Int): Vet? {
        return try {
            vetRepository.findById(id)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @Transactional(readOnly = true)
    fun findAllVets(): List<Vet> {
        return vetRepository.findAll()
    }

    @Transactional
    fun saveVet(vet: Vet) {
        vetRepository.save(vet)
    }

    @Transactional
    fun deleteVet(vet: Vet) {
        vetRepository.delete(vet)
    }

    @Transactional(readOnly = true)
    fun findAllOwners(): List<Owner> {
        return ownerRepository.findAll()
    }

    @Transactional
    fun deleteOwner(owner: Owner) {
        ownerRepository.delete(owner)
    }

    @Transactional(readOnly = true)
    fun findPetTypeById(petTypeId: Int): PetType? {
        return try {
            petTypeRepository.findById(petTypeId)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @Transactional(readOnly = true)
    fun findAllPetTypes(): List<PetType> {
        return petTypeRepository.findAll()
    }

    @Transactional
    fun savePetType(petType: PetType) {
        petTypeRepository.save(petType)
    }

    @Transactional
    fun deletePetType(petType: PetType) {
        petTypeRepository.delete(petType)
    }

    @Transactional(readOnly = true)
    fun findSpecialtyById(specialtyId: Int): Specialty? {
        return try {
            specialtyRepository.findById(specialtyId)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @Transactional(readOnly = true)

    fun findAllSpecialties(): List<Specialty> {
        return specialtyRepository.findAll()
    }

    @Transactional
    fun saveSpecialty(specialty: Specialty) {
        specialtyRepository.save(specialty)
    }

    @Transactional
    fun deleteSpecialty(specialty: Specialty) {
        specialtyRepository.delete(specialty)
    }

    @Transactional(readOnly = true)
    fun findPetTypes(): List<PetType> {
        return petRepository.findPetTypes()
    }

    @Transactional(readOnly = true)
    fun findOwnerById(id: Int): Owner? {
        return try {
            ownerRepository.findById(id)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            e.printStackTrace()
            return null
        }
    }

    @Transactional(readOnly = true)
    fun findPetById(id: Int): Pet? {
        return try {
            petRepository.findById(id)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @Transactional
    fun savePet(pet: Pet) {
        petRepository.save(pet)
    }

    @Transactional
    fun saveVisit(visit: Visit) {
        visitRepository.save(visit)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["vets"])
    fun findVets(): List<Vet> {
        return vetRepository.findAll()
    }

    @Transactional
    fun saveOwner(owner: Owner) {
        ownerRepository.save(owner)
    }

    @Transactional(readOnly = true)
    fun findOwnerByLastName(lastName: String): List<Owner> {
        return ownerRepository.findByLastName(lastName)
    }

    @Transactional(readOnly = true)
    fun findVisitsByPetId(petId: Int): List<Visit> {
        return visitRepository.findByPetId(petId)
    }
}
