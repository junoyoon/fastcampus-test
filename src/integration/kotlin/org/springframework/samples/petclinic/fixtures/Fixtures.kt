package org.springframework.samples.petclinic.fixtures

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto

object Fixtures {
    private val fixtureMonkey = FixtureMonkey.builder().plugin(KotlinPlugin()).plugin(JakartaValidationPlugin()).build()

    fun giveMeOneFreshOwner() : OwnerFieldsDto {
        return fixtureMonkey.giveMeOne<OwnerFieldsDto>()
    }
}
