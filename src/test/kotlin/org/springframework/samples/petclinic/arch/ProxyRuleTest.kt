package org.springframework.samples.petclinic.arch

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.ProxyRules
import org.junit.jupiter.api.TestInstance
import org.springframework.transaction.annotation.Transactional

@ArchIgnore(reason = "테스트 할때 제거")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AnalyzeClasses(
    packages = ["org.springframework.samples.petclinic.facade"],
    // 테스트 코드를 제외하고 싶다면 추가
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class ProxyRuleTest {

    @ArchTest
    val transactionalProxyRule: ArchRule =
        ProxyRules.no_classes_should_directly_call_other_methods_declared_in_the_same_class_that_are_annotated_with(
            Transactional::class.java
        ).`as`("\n\nDO NOT MAKE SELF CALL FOR transactional\n\n").because("It will ignore proxy")
}
