package org.springframework.samples.petclinic.service

import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.collections.shouldNotContainNoNulls
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldStartWith
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.samples.petclinic.SpringFunSpec
import org.springframework.samples.petclinic.model.User

@SpringBootTest
class UserServiceSpringDataJpaKoTests(
    @Autowired userService: UserService
): SpringFunSpec({

    test("shouldAddUser") {
        val user = User(
            username = "username",
            password = "password",
            enabled = true,
        ).apply {
            this.addRole("OWNER_ADMIN")
        }
        userService.saveUser(user)
        user.roles.shouldNotBeNull()
            .shouldMatchEach({
                it.name.shouldStartWith("ROLE_")
                it.user.shouldNotBeNull()
            })
    }
})
