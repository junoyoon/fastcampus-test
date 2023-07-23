package org.springframework.samples.petclinic.practice

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AnalyzeClasses(
    packages = ["org.springframework.samples.petclinic.rest.api"],
    // 테스트 코드를 제외하고 싶다면 추가
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class ArchUnitTest {
    @ArchTest
    fun testSwaggerAnnotationOnController(javaClasses: JavaClasses) {
//        assertSoftly {
//            javaClasses.forEach { clazz ->
//                clazz.methods.forEach { method ->
//                    withClue(method) {
//                        method.tryGetAnnotationOfType(Operation::class.java).shouldNotBeEmpty()
//                    }
//                }
//            }
//        }
    }

    @ArchTest
    fun testValidAnnotationOnControllerPostMethodParameter(javaClasses: JavaClasses) {
//        assertSoftly {
//            javaClasses.forEach { clazz ->
//                for (method in clazz.methods) {
//                    val requestMapping = method.tryGetAnnotationOfType(RequestMapping::class.java)
//                    if (requestMapping.isEmpty || !requestMapping.get().method.contains(RequestMethod.POST)) {
//                        continue
//                    }
//                    method.parameters.forEach { parameter ->
//                        withClue(parameter) {
//                            if (parameter.type.name.endsWith("Dto")) {
//                                parameter.tryGetAnnotationOfType(Valid::class.java).shouldNotBeEmpty()
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}
