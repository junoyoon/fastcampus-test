package org.springframework.samples.petclinic.practice.optimization.parentclass

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.UserMapper
import org.springframework.samples.petclinic.model.User
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.rest.controller.UserRestController
import org.springframework.samples.petclinic.service.UserService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class UserRestControllerTests(
    @Autowired userRestController: UserRestController,
    @Autowired @MockBean val userService: UserService,
) : AbstractRestControllerTest() {

    private val mockMvc = MockMvcBuilders.standaloneSetup(userRestController)
        .setControllerAdvice(ExceptionControllerAdvice()).build()

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun testCreateUserSuccess() {
        doNothing().whenever(userService).saveUser(any())
        val user = User(
            username = "username",
            password = "password",
            enabled = true,
        )

        user.addRole("OWNER_ADMIN")
        val mapper = ObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(UserMapper.toUserDto(user))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun testCreateUserError() {
        doNothing().whenever(userService).saveUser(any())
        val user = User(
            username = "username",
            enabled = true,
        )
        val mapper = ObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(UserMapper.toUserDto(user))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }
}
