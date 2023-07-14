package org.springframework.samples.petclinic.sample

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.doubles.percent
import io.kotest.matchers.equality.FieldsEqualityCheckConfig
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainOnlyDigits
import io.kotest.matchers.string.shouldStartWith
import org.springframework.samples.petclinic.sample.Person.*
import java.lang.RuntimeException

class Calculator {
    fun plus(a: Int, b: Int) = a + b
    fun multiply(a: Float, b: Float) = a * b
}

fun <T> drop(from : List<T>?, n: Int): List<T>? {
    return from?.drop(n)
}

class KotestAssertionExample : FunSpec({
    test("method1") {

        val sut = Calculator()
        sut.plus(1, 2) shouldBe 3 // infix style

        sut.multiply(100f, 0.3f).shouldBe(30f plusOrMinus 0.01f)

        drop(listOf("Hello", "World"), 1) // assertion 을 chaining
            // 이후 null 조건 제거
            .shouldNotBeNull()
            .shouldNotBeEmpty()
            .shouldHaveSize(1)
            // 내부 모든 element 탐색
            .forAll {it shouldStartWith "W" }
            .shouldNotContain("Hello").shouldContain("World")

        shouldThrow<RuntimeException> { throw RuntimeException("error") }
            .message shouldBe "error"

        val actual = Person(Name("Yoon", "JunHo"), Sex.MALE, 0)
        val expected = Person(Name("Yoon", "JunHo"), Sex.MALE, 100)

        actual.shouldBeSexOf(Sex.MALE) // extension
            // 재귀적으로 필드별 비교 가능, 필드 비교에서 제외할 필드 지정
            .shouldBeEqualToComparingFields(expected,
                FieldsEqualityCheckConfig(propertiesToExclude = listOf(Person::luck))
            )

        assertSoftly(actual) {
            sex shouldBe Sex.MALE
            luck shouldBe 0
        }
    }
})

class Person(val name : Name, val sex: Sex, val luck: Int) {
    enum class Sex { MALE, FEMALE }
    class Name(val firstName: String, val lastName: String)
}

// custom asserting 추가
fun Person.shouldBeSexOf(sex: Sex): Person {
    this.sex shouldBe sex
    return this
}



