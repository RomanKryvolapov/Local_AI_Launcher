/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.mappers.network.user

import com.romankryvolapov.localailauncher.data.mappers.base.BaseMapper
import com.romankryvolapov.localailauncher.data.models.network.user.UserJsonData
import com.romankryvolapov.localailauncher.data.utils.StrictMapperConfig
import com.romankryvolapov.localailauncher.domain.models.user.UserModel
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

class UserMapper : BaseMapper<UserJsonData, UserModel>() {

    @Mapper(config = StrictMapperConfig::class)
    fun interface ModelMapper {
        fun map(from: UserJsonData): UserModel
    }

    override fun map(from: UserJsonData): UserModel {
        return Mappers.getMapper(ModelMapper::class.java).map(from)
    }

}