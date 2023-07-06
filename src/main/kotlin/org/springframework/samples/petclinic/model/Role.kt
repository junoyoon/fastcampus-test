package org.springframework.samples.petclinic.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "roles", uniqueConstraints = [UniqueConstraint(columnNames = ["username", "role"])])
class Role : BaseEntity() {
    @ManyToOne
    @JoinColumn(name = "username")
    var user: User? = null

    @Column(name = "role")
    var name: String? = null
}
