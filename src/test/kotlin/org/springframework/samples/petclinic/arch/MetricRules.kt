package org.springframework.samples.petclinic.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchIgnore
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.metrics.ArchitectureMetrics
import com.tngtech.archunit.library.metrics.MetricsComponents
import com.tngtech.archunit.library.metrics.MetricsComponents.fromClasses

@ArchIgnore(reason = "테스트 할때 제거")
@AnalyzeClasses(
    packages = ["org.springframework.samples.petclinic"],
    // 테스트 코드를 제외하고 싶다면 추가
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class MetricRules {

    @ArchTest
    fun metrics(javaClasses: JavaClasses) {
        ArchitectureMetrics.lakosMetrics(fromClasses(javaClasses)).apply {
            println("CCD : $cumulativeComponentDependency")
            println("ACD : $averageComponentDependency")
            println("RACD : $relativeAverageComponentDependency")
            println("NCCD : $normalizedCumulativeComponentDependency")
        }
        val packageBase = "org.springframework.samples.petclinic"
        val components =
            MetricsComponents.fromPackages(
                javaClasses.getPackage(packageBase).subpackages
            )

        ArchitectureMetrics.componentDependencyMetrics(components).apply {
            components.forEach {
                val subpackage = it.identifier;
                println("$subpackage - Ce: ${getEfferentCoupling(subpackage)}")
                println("$subpackage - Ca: ${getAfferentCoupling(subpackage)}")
                println("$subpackage - I: ${getInstability(subpackage)}")
                println("$subpackage - A: ${getAbstractness(subpackage)}")
                println("$subpackage - D: ${getNormalizedDistanceFromMainSequence(subpackage)}")
            }
        }
    }
}
