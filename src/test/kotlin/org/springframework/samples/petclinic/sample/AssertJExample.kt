package org.springframework.samples.petclinic.sample

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.ObjectAssert
import org.assertj.core.data.Percentage.withPercentage
import org.junit.jupiter.api.Test
import org.springframework.samples.petclinic.sample.AssertJExample.Person.Name
import org.springframework.samples.petclinic.sample.AssertJExample.Person.Sex


@Suppress("UNUSED_VARIABLE")
class AssertJExample {
    inner class Calculator {
        fun plus(a: Int, b: Int) = a + b
        fun multiply(a: Float, b: Float) = a * b
    }

    fun <T> drop(from : List<T>?, n: Int): List<T>? {
        return from?.drop(n)
    }

    @Test
    fun method1() {
        val sut = Calculator()
        assertThat(sut.plus(1, 2)).isEqualTo(3)
        // 부정소수점 부정확성 처리
        assertThat(sut.multiply(100f, 0.3f))
            .isCloseTo(30f, withPercentage(0.01))
        assertThat(drop(listOf("Hello", "World"), 1))
            // describedAs 를 통해 에러 메시지를 출력 가능
            .describedAs("drop 이후에는 World 만 남아야 한다.")
            // assertion 을 체이닝 할 수 있음
            .isNotNull().isNotEmpty().hasSize(1)
            .doesNotContain("Hello").contains("World")
            .hasOnlyElementsOfType(String::class.java)
            // collection 의 각 항목에 대해 assertion
            .allSatisfy { assertThat(it).startsWith("W") }

        val actual = Person(Name("Yoon", "JunHo"), Sex.MALE, 0)
        val expected = Person(Name("Yoon", "JunHo"), Sex.MALE, 100)

        assertThat(actual)
            .isSex(Sex.MALE)
            // 재귀적으로 필드별 비교 가능, 필드 비교에서 제외할 필드 지정
            .usingRecursiveComparison()
            .ignoringFields("luck")
            .isEqualTo(expected)

        assertThatThrownBy { throw RuntimeException("error") }
            .isInstanceOf(RuntimeException::class.java)

    }

    class Person(val name : Name, val sex: Sex, val luck: Int) {
        enum class Sex { MALE, FEMALE }
        class Name(val firstName: String, val lastName: String)
    }

    // custom asserting 추가
    private fun ObjectAssert<Person>.isSex(sex: Sex): ObjectAssert<Person> {
        extracting(Person::sex).isEqualTo(sex)
        return this
    }
}





