package org.springframework.samples.petclinic.sample

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.Tags
import io.kotest.core.config.Defaults.blockingTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.SystemUtils

// @Ignored @EnabledIf(LinuxOnlyCondition::class)
@Tags("unit")
class KotestExample : FunSpec({
    // test 메소드 마다 새로 인스턴스를 생성
    isolationMode = IsolationMode.InstancePerTest

    val sut = Calculator()
    // 테스트 Spec 인스턴트가 생성 / 종료 될때마다 실행
    beforeSpec { println("called before spec instance") }
    afterSpec { println("called after spec instance") }

    // 각 테스트가 실행 될 때마다 실행
    beforeTest { println("called before test") }
    afterTest { println("called after test") }

    test("plus 정상 동작 테스트") {
        sut.plus(1, 2).shouldBe(3)
    }

    xtest("disabled") { /* no_op */ }

    table(
        headers("a", "b", "c"),
        row(1, 2, 3),
        row(2, 2, 4)
    ).forAll { a, b, c ->
        test("파라미터 테스트 - $a + $b = $c") {
            sut.plus(a, b) shouldBe c
        }
    }

    // 동일한 설정을 공유하는 테스트를 context 로 묶음
    context("nested") {
        repeat(10) {
            test("repeated $it").config(
                    enabled = !SystemUtils.IS_OS_WINDOWS,
                    tags = setOf(NamedTag("window"))
            ) { println("repeated") }
        }
    }
}) {
    class Calculator { fun plus(a: Int, b: Int) = a + b }
}

