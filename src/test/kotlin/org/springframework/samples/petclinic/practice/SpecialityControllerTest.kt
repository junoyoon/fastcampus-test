package org.springframework.samples.petclinic.practice

import com.fasterxml.jackson.databind.ObjectMapper
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mapper.SpecialtyMapper
import org.springframework.samples.petclinic.model.Specialty
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice
import org.springframework.samples.petclinic.rest.controller.SpecialtyRestController
import org.springframework.samples.petclinic.rest.controller.VetRestController
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class SpecialityControllerTest(
    @Autowired val specialtyController: SpecialtyRestController,
) {

    private val mockMvc = MockMvcBuilders.standaloneSetup(specialtyController)
        .setControllerAdvice(ExceptionControllerAdvice())
        .build()


    @RepeatedTest(1000)
    @WithMockUser(roles = ["VET_ADMIN"])
    fun testCreateSpecialtySuccess() {
        // FIXME: 이부분을 FixtureMonkey 로 오류를 찾아보세요.
        //val newSpecialty = SpecialtyDto(id = 0, name = "hello")
        val newSpecialty = fixtureMonkey.giveMeOne<SpecialtyDto>().copy(id = 0)

        val mapper = ObjectMapper()
        val newSpecialtyAsJSON = mapper.writeValueAsString(newSpecialty)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/specialties/")
                .content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    companion object {
        val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
            .plugin(KotlinPlugin())
            .plugin(JakartaValidationPlugin())
            .build()
    }
}
