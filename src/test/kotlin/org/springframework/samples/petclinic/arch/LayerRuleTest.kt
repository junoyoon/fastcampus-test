package org.springframework.samples.petclinic.arch

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.Architectures

@ArchIgnore(reason = "테스트 할때 제거")
@AnalyzeClasses(
    packages = ["org.springframework.samples.petclinic"],
    // 테스트 코드를 제외하고 싶다면 추가
    importOptions = [DoNotIncludeTests::class]
)
class LayerRuleTest {
    @ArchTest
    val serviceLayerRule: ArchRule = classes()
        .that().resideInAPackage("..service..")
        .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..")

    @ArchTest
    val layerArchitectureRule : ArchRule =
        Architectures.layeredArchitecture().consideringAllDependencies()
            .layer("Controller").definedBy("..rest..")
            .layer("Service").definedBy("..service..")
            .layer("Persistence").definedBy("..repository..")
            .layer("Mapper").definedBy("..mapper..")
            .whereLayer("Controller").mayOnlyBeAccessedByLayers("Mapper")
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")

}

