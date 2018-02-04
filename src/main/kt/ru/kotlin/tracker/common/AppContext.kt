package ru.kotlin.tracker.common

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class AppContext private constructor() {

    companion object {
        val instance: AppContext by lazy { AppContext() }
    }

    private val defaultQualifier = "default"
    private val bindings: MutableMap<KClass<Any>, MutableMap<String, Any>> = ConcurrentHashMap()

    fun <T : Any> register(clazz: KClass<T>,
                           inst: T,
                           qualifier: String = defaultQualifier): T {
        bindings.computeIfAbsent(clazz as KClass<Any>, { _ -> ConcurrentHashMap() })
                .put(qualifier, inst)
        return inst
    }

    fun <T : Any> get(clazz: KClass<T>,
                      qualifier: String = defaultQualifier): T {
        return bindings[clazz as KClass<Any>]?.get(qualifier) as T
    }

}