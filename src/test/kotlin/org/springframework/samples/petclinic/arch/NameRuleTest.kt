package org.springframework.samples.petclinic.arch

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.junit.jupiter.api.TestInstance
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@ArchIgnore(reason = "테스트 할때 제거")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AnalyzeClasses(
    packages = ["org.springframework.samples.petclinic"],
    // 테스트 코드를 제외하고 싶다면 추가
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class NameRuleTest {

    @ArchTest
    val serviceNameRule: ArchRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Service::class.java)
        .should().haveSimpleNameEndingWith("Service")

    @ArchTest
    val controllerNameRule: ArchRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Controller::class.java).or().areAnnotatedWith(RestController::class.java)
        .should().haveSimpleNameEndingWith("Controller")
}
