package org.springframework.samples.petclinic.service

import org.springframework.samples.petclinic.model.User
import org.springframework.samples.petclinic.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun saveUser(user: User) {
        require(!user.roles.isNullOrEmpty()) { "User must have at least a role set!" }
        for (role in user.roles.orEmpty()) {
            if (!role.name.orEmpty().startsWith("ROLE_")) {
                role.name = "ROLE_" + role.name
            }
            if (role.user == null) {
                role.user = user
            }
        }
        userRepository.save(user)
    }
}
