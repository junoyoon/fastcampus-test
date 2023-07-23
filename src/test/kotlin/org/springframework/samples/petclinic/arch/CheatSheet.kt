package org.springframework.samples.petclinic.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.GeneralCodingRules
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.optional.shouldNotBeEmpty
import io.swagger.v3.oas.annotations.media.Schema
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.stereotype.Service

@ArchIgnore(reason = "테스트 할때 제거")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AnalyzeClasses(
    // 분석 클래스 패키지 범위 지정
    packages = ["org.springframework.samples.petclinic"],
    // 테스트 코드를 제외하고 싶다면 추가
    importOptions = [DoNotIncludeTests::class]
)
class CheatSheet {

    // service 패키지의 메소드들은 controller 과 service 내의 클래스에서만 호출되어야 함
    @ArchTest
    val serviceRule: ArchRule = ArchRuleDefinition.classes()
        .that().resideInAPackage("..service..")
        .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..")

    // @Service 가 기재된 클래스명은 Service 로 끝나야 함
    @ArchTest
    val serviceNameRule: ArchRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Service::class.java)
        .should().haveSimpleNameEndingWith("Service")

    // spring 에서 필드 injection 은 사용하지 말아야 함
    @ArchTest
    fun noFieldInjection(javaClasses: JavaClasses) {
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.
            `as`("필드 Injection 사용됨").because("필드 Injection 은 사용 하면 않됨")
            .check(javaClasses)
    }

    // @Schema 가 붙지 않은 field 를 검출 (kotlin data class)
    @Test
    fun shouldHaveSchemaAnnotationOnDto() {
        val javaClasses = ClassFileImporter().withImportOption(DO_NOT_INCLUDE_TESTS)
            .importPackages("org.springframework.samples.petclinic")
        assertSoftly {
            for (clazz in javaClasses) {
                withClue(clazz) {
                    clazz.tryGetAnnotationOfType(Schema::class.java).shouldNotBeEmpty()
                }
                for (constructor in clazz.constructors) {
                    if (constructor.parameters.isNotEmpty() &&
                        // 코틀린 자동 생성 코드 제거
                        JavaModifier.SYNTHETIC !in constructor.modifiers) {
                        continue
                    }
                    for (parameter in constructor.parameters) {
                        // @Schema 가 없는 parameter 검출
                        withClue(parameter) {
                            parameter.tryGetAnnotationOfType(Schema::class.java)
                                .shouldNotBeEmpty()
                        }
                    }
                }
            }
        }
    }




}
