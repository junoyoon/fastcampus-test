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
        return Role(name = roleDto.name)
    }

    fun toRoleDto(role: Role): RoleDto {
        return RoleDto(name = role.name.orEmpty())
    }

    fun toUser(userDto: UserDto): User {
        return User(
            enabled = userDto.enabled,
            password = userDto.password,
            username = userDto.username,
            roles = userDto.roles?.map { toRole(it) }?.toMutableSet(),
        )
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
