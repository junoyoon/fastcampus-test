package org.springframework.samples.petclinic.etc

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.samples.petclinic.service.UserService

@AutoConfigureMockMvc
@SpringBootTest(
    // 일부 프로퍼티를 통합 테스트용으로 교체
    properties = ["spring.sql.init.data-locations: classpath*:db/h2/populateDB-integration.sql"],
    // 실제 Tomcat 서버로 실행 (디폴트는 포트 노출 없는 MockMvc)
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class SpringBootTestExample(
    // 일부 빈을 MockBean 또는 SpyBean 으로 교체
    @MockBean val userService: UserService
) {
    //...
}
