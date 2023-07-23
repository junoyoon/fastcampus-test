package org.springframework.samples.petclinic.facade

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.samples.petclinic.model.User
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.samples.petclinic.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceFacade(
    @Autowired val userService: UserService
) {
    //@Autowired lateinit var clinicService: ClinicService

    /** Intentional error */
    fun saveUserNull() {
        //println("한번 stdout 출력")
        return saveNull()
    }

    @Transactional
    fun saveNull() {
        return userService.saveUser(User())
    }
}
