package org.springframework.samples.petclinic.sample

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLengthBetween
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.junit.jupiter.api.Test


class FixtureMonkeyExample {
    data class Params(
        @field:Size(min=3, max=10) val firstName : String,
        @field:Size(min=3, max=10) val lastName: String,
        @field:Min(0) @field:Max(100) val age:Int
    )
    companion object {
        val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
            .plugin(KotlinPlugin())
            .plugin(JakartaValidationPlugin())
            .build()
    }
    @Test
    fun test() {
        val params = fixtureMonkey.giveMeOne<Params>()
        params.firstName.shouldHaveLengthBetween(3, 10)
        params.lastName.shouldHaveLengthBetween(3, 10)
        params.age.shouldBeBetween(0, 100)
        println(params)

        val fixedAgeParam = fixtureMonkey.giveMeOne<Params>().copy(age = 10)
        fixedAgeParam.age shouldBe 10
        println(fixedAgeParam)
    }
}
