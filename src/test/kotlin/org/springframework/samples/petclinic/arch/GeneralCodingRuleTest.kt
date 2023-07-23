@file:Suppress("unused")

package org.springframework.samples.petclinic.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.GeneralCodingRules
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ArchIgnore(reason = "테스트 할때 제거")
@Suppress("PropertyName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AnalyzeClasses(
    packages = ["org.springframework.samples.petclinic.facade"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class GeneralCodingRuleTest {

    @ArchTest
    val noStdStream: ArchRule =
        GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS

    @ArchTest
    val shouldNotUseJavaUtilLogging: ArchRule =
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING

    @ArchTest
    fun noFieldInjection(javaClasses: JavaClasses) {
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.
        `as`("필드 Injection 사용됨").because("필드 Injection 은 사용 하면 않됨").check(javaClasses)
    }
}
