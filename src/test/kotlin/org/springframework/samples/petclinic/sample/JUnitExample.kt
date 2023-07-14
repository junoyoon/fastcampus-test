package org.springframework.samples.petclinic.sample

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("unit")
annotation class UnitTest

@UnitTest
class JUnitExample {
    private val sut = Calculator()

    companion object {
        @BeforeAll @JvmStatic
        fun beforeAll() { println("called before JUnitExample")}

        @AfterAll @JvmStatic
        fun afterAll() { println("called after JUnitExample") }
    }

    @BeforeEach
    fun beforeEach() = println("called before test method")

    @AfterEach
    fun afterEach() = println("called after test method")

    @DisplayName("plus 정상 동작 테스트")
    @Test
    fun method1() {
        val actual = sut.plus(1, 2)
        assertEquals(1 + 2, actual) { "$actual should be same as 3" }
    }

    @ParameterizedTest
    @CsvSource(value = ["1,2,3", "2,2,4"])
    fun method1_withParam(a: Int, b: Int, c: Int) {
        val actual = sut.plus(a, b)
        assertTrue(actual == c) { "$actual should be same to $c" }
    }

    @Disabled("그냥 disable 시켰음") @Test
    fun method1_disabled() { /* no-op */ }

    @Nested
    inner class NestedTest {
        @DisabledOnOs(OS.WINDOWS)
        @RepeatedTest(10)
        fun method1_repeated() { println("repeated") }
    }

    class Calculator {
        fun plus(a: Int, b: Int) = a + b
    }
}



