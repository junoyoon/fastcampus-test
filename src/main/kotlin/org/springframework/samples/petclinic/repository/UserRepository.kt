package org.springframework.samples.petclinic.repository

import org.springframework.data.repository.Repository
import org.springframework.samples.petclinic.model.User

interface UserRepository : Repository<User, Int> {
    fun save(user: User)
}
