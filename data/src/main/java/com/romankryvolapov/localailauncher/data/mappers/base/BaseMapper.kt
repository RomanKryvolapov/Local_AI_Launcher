/**
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created 2025 by Roman Kryvolapov
 *
 * Examples for MapStruct:
 *
 * data class From(
 *     val one: String,
 *     val two: String,
 * )
 *
 * data class To(
 *     val one: String,
 *     val two: String,
 * )
 *
 * class SomeModelMapper : BaseMapper<From, To>() {
 *
 *     @Mapper(config = StrictMapperConfig::class)
 *     fun interface ModelMapper {
 *
 *         fun map(from: From): To
 *
 *     }
 *
 *     override fun map(from: From): To {
 *         return Mappers.getMapper(ModelMapper::class.java).map(from)
 *     }
 *
 * }
 *
 * data class From(
 *     val one: String,
 *     val two: String,
 * )
 *
 * data class To(
 *     val oneNewName: String,
 *     val twoNewName: String,
 * )
 *
 * class SomeModelMapper : BaseMapper<From, To>() {
 *
 *     @Mapper(config = StrictMapperConfig::class)
 *     fun interface ModelMapper {
 *
 *         @Mapping(source = "one", target = "oneNewName")
 *         @Mapping(source = "two", target = "twoNewName")
 *         fun map(from: From): To
 *
 *     }
 *
 *     override fun map(from: From): To {
 *         return Mappers.getMapper(ModelMapper::class.java).map(from)
 *     }
 *
 * }
 *
 * data class From(
 *     val one: String,
 *     val two: String,
 * )
 *
 * data class To(
 *     val one: List<String>,
 *     val two: String,
 * )
 *
 * class SomeModelMapper : BaseMapper<From, To>() {
 *
 *     @Mapper(config = StrictMapperConfig::class)
 *     abstract class ModelMapper {
 *
 *         abstract fun map(from: From): To
 *
 *         fun mapOne(one: String): List<String> {
 *             return listOf(one)
 *         }
 *
 *     }
 *
 *     override fun map(from: From): To {
 *         return Mappers.getMapper(ModelMapper::class.java).map(from)
 *     }
 *
 * }
 *
 * data class From(
 *     val one: String,
 *     val two: String,
 * )
 *
 * data class To(
 *     val one: List<String>,
 *     val two: String,
 * )
 *
 * class SomeModelMapper : BaseMapper<From, To>() {
 *
 *     companion object {
 *         private const val MAP_ONE = "MAP_ONE"
 *     }
 *
 *     @Mapper(config = StrictMapperConfig::class)
 *     abstract class ModelMapper {
 *
 *         @Mapping(target = "one", source = "one", qualifiedByName = [MAP_ONE])
 *         abstract fun map(from: From): To
 *
 *         @Named(MAP_ONE)
 *         fun mapOne(one: String): List<String> {
 *             return listOf(one)
 *         }
 *
 *     }
 *
 *     override fun map(from: From): To {
 *         return Mappers.getMapper(ModelMapper::class.java).map(from)
 *     }
 *
 * }
 *
 * data class From(
 *     val one: String,
 *     val two: List<FromItem>,
 * )
 *
 * data class FromItem(
 *     val itemOne: String,
 *     val itemTwo: String,
 * )
 *
 * data class To(
 *     val one: String,
 *     val two: List<ToItem>,
 * )
 *
 * data class ToItem(
 *     val itemOne: List<String>,
 *     val itemTwo: String,
 * )
 *
 * class SomeModelMapper : BaseMapper<From, To>() {
 *
 *     @Mapper(config = StrictMapperConfig::class, uses = [ItemMapper::class])
 *     fun interface ModelMapper {
 *
 *         fun map(from: From): To
 *
 *     }
 *
 *     @Mapper(config = StrictMapperConfig::class)
 *     abstract class ItemMapper {
 *
 *         fun mapItemOne(itemOne: String): List<String> {
 *             return listOf(itemOne)
 *         }
 *
 *     }
 *
 *     override fun map(from: From): To {
 *         return Mappers.getMapper(ModelMapper::class.java).map(from)
 *     }
 *
 * }
 *
 **/
package com.romankryvolapov.localailauncher.data.mappers.base

abstract class BaseMapper<From, To> {

    abstract fun map(from: From): To

    open fun mapList(fromList: List<From>): List<To> {
        return fromList.map { map(it) }
    }

}