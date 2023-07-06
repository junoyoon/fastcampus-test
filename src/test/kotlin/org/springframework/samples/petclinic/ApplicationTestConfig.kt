package org.springframework.samples.petclinic

import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.TestConfiguration

@TestConfiguration
class ApplicationTestConfig {
    init {
        MockitoAnnotations.openMocks(this)
    }
}
