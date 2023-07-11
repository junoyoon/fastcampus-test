package org.springframework.samples.petclinic.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.samples.petclinic.model.User

@SpringBootTest
class UserServiceSpringDataJpaTests(
    @Autowired private val userService: UserService
) {
    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun shouldAddUser() {
        val user = User().apply {
            this.username = "username"
            this.password = "password"
            this.enabled = true
            this.addRole("OWNER_ADMIN")
        }

        userService.saveUser(user)
        assertThat(user.roles).allMatch { it.name!!.startsWith("ROLE_") }
        assertThat(user.roles).allMatch { it.user != null }
    }
}
