package org.springframework.samples.petclinic.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @Column(name = "username")
    var username: String? = null,

    @Column(name = "password")
    var password: String? = null,

    @Column(name = "enabled")
    var enabled: Boolean? = null,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", fetch = FetchType.EAGER)
    var roles: MutableSet<Role>? = null,
) {


    fun addRole(roleName: String?) {
        if (roles == null) {
            roles = mutableSetOf()
        }
        val role = Role().apply { name = roleName }
        roles!!.add(role)
    }
}
