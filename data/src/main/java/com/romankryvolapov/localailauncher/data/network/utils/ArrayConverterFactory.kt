/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.network.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ArrayConverterFactory : Converter.Factory(), KoinComponent {

    companion object {
        private const val TAG = "ArrayConverterFactoryTag"
    }

    val gson: Gson by inject()

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return Converter<ResponseBody, Any?> { body ->
            when {
//                type is Class<*> && type == EmpowermentCreateResponse::class.java -> {
//                    logDebug("EmpowermentCreateResponse", TAG)
//                    EmpowermentCreateResponse(
//                        data = generateList(
//                            body = body,
//                            type = object : TypeToken<String>() {}.type,
//                        )
//                    )
//                }

                else -> {
                    retrofit.nextResponseBodyConverter<Any?>(
                        this@ArrayConverterFactory,
                        type,
                        annotations
                    ).convert(body)
                }
            }
        }
    }

    private fun <T> generateList(body: ResponseBody, type: Type): List<T> {
        val listType = TypeToken.getParameterized(List::class.java, type).type
        return gson.fromJson(body.charStream(), listType)
    }

}