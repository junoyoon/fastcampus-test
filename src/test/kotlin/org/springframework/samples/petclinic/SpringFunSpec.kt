package org.springframework.samples.petclinic

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension

open abstract class SpringFunSpec(body: FunSpec.() -> Unit = {}) : FunSpec(body) {
    override fun extensions() = listOf(SpringExtension)
    override fun isolationMode(): IsolationMode? {
        return IsolationMode.InstancePerTest
    }
}
