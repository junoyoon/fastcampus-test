package org.springframework.samples.petclinic.practice.optimization2

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.samples.petclinic.service.UserService

abstract class AbstractRestControllerTest {

    @SpyBean
    lateinit var userService: UserService

    @SpyBean
    lateinit var clinicService: ClinicService
}

