package org.springframework.samples.petclinic.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import javax.sql.DataSource

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize method-level security
@ConditionalOnProperty(name = ["petclinic.security.enable"], havingValue = "true")
class BasicAuthenticationConfig(
    private val dataSource: DataSource
) {
    @Bean
    fun filterChain(http: HttpSecurity): DefaultSecurityFilterChain =
        http
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .httpBasic()
            .and()
            .csrf()
            .disable()
            .build()


    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder): JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> =
        auth
            .jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery("select username,password,enabled from users where username=?")
            .authoritiesByUsernameQuery("select username,role from roles where username=?")
}
