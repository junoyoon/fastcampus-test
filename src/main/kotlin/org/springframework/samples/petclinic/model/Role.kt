package org.springframework.samples.petclinic.model

import jakarta.persistence.*

@Entity
@Table(name = "roles", uniqueConstraints = [UniqueConstraint(columnNames = ["username", "role"])])
class Role(
    id: Int? = null,

    @ManyToOne
    @JoinColumn(name = "username")
    var user: User? = null,

    @Column(name = "role")
    var name: String? = null
) : BaseEntity(id)
