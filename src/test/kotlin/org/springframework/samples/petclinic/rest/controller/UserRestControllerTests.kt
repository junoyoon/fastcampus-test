package org.springframework.samples.petclinic.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.UserMapper
import org.springframework.samples.petclinic.model.User
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.service.UserService
import org.springframework.samples.petclinic.ApplicationTestConfig
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@ContextConfiguration(classes = [ApplicationTestConfig::class])
@WebAppConfiguration
class UserRestControllerTests(
    @Autowired userRestController: UserRestController
) {

    @MockBean
    lateinit var userService: UserService
    
    private val mockMvc = MockMvcBuilders.standaloneSetup(userRestController)
        .setControllerAdvice(ExceptionControllerAdvice()).build()

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun testCreateUserSuccess() {
        val user = User().apply {
            username = "username"
            password = "password"
            enabled = true
        }

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
        val user = User().apply {
            username = "username"
            enabled = true
        }
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
