package org.springframework.samples.petclinic.sample

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.forAll

class KotestPropertyBasedTestExample: FunSpec({
    context("list.sort()") {
        test("list is sorted") {
            forAll<String, String> { a, b ->
                (a + b).length == a.length + b.length
            }
        }
    }
})

