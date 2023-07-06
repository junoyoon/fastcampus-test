package org.springframework.samples.petclinic.mapper

import org.springframework.samples.petclinic.model.Role
import org.springframework.samples.petclinic.model.User
import org.springframework.samples.petclinic.rest.dto.RoleDto
import org.springframework.samples.petclinic.rest.dto.UserDto

/**
 * Map User/Role & UserDto/RoleDto using mapstruct
 */
object UserMapper {
    fun toRole(roleDto: RoleDto): Role {
        return Role().apply {
            this.name = roleDto.name
        }
    }

    fun toRoleDto(role: Role): RoleDto {
        return RoleDto(
            name = role.name.orEmpty()
        )
    }

    fun toUser(userDto: UserDto): User {
        return User().apply {
            this.enabled = userDto.enabled
            this.password = userDto.password
            this.username = userDto.username
            this.roles = userDto.roles?.map { toRole(it) }?.toMutableSet()
        }
    }

    fun toUserDto(user: User): UserDto {
        return UserDto(
            username = user.username.orEmpty(),
            password = user.password.orEmpty(),
            enabled = user.enabled,
            roles = user.roles?.map { toRoleDto(it) }
        )
    }
}
