package org.springframework.samples.petclinic.sample

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.intArray
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
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

