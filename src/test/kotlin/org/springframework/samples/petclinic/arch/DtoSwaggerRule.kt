@file:Suppress("unused")

package org.springframework.samples.petclinic.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.optional.shouldNotBeEmpty
import io.swagger.v3.oas.annotations.media.Schema
import org.junit.jupiter.api.TestInstance

@ArchIgnore(reason = "테스트 할때 제거")
@Suppress("PropertyName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AnalyzeClasses(
    packages = ["org.springframework.samples.petclinic.rest.dto"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class DtoSwaggerRule {

    @ArchTest
    fun shouldHaveSchemaAnnotationOnDto(javaClasses: JavaClasses) {
        assertSoftly {
            for (clazz in javaClasses) {
                withClue(clazz) {
                        clazz.tryGetAnnotationOfType(Schema::class.java)
                            .shouldNotBeEmpty()
                }
                for (constructor in clazz.constructors) {
                    if (constructor.parameters.isNotEmpty() &&
                        !constructor.modifiers.contains(JavaModifier.SYNTHETIC)) {
                        continue
                    }
                    for (parameter in constructor.parameters) {
                        withClue(parameter) {
                            parameter
                                .tryGetAnnotationOfType(Schema::class.java)
                                .shouldNotBeEmpty()
                        }
                    }
                }
            }
        }
    }
}
