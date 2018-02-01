package ru.kotlin.tracker.common

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class AppContext private constructor() {

    companion object {
        val instance: AppContext by lazy { AppContext() }
    }

    private val defaultQualifier = "default"
    private val bindings: MutableMap<KClass<Any>, MutableMap<String, Any>> = mutableMapOf()

    fun <T : Any> register(clazz: KClass<T>, qualifier: String, inst: T): T {
        bindings.computeIfAbsent(clazz as KClass<Any>, { _ -> mutableMapOf() }).put(qualifier, inst)
        return inst
    }

    fun <T : Any> register(clazz: KClass<T>, inst: T): T {
        bindings.computeIfAbsent(clazz as KClass<Any>, { _ -> mutableMapOf() }).put(defaultQualifier, inst)
        return inst
    }

    fun <T : Any> get(clazz: KClass<T>, qualifier: String): T {
        return bindings[clazz as KClass<Any>]?.get(qualifier) as T
    }

    fun <T : Any> get(clazz: KClass<T>): T {
        return bindings[clazz as KClass<Any>]?.get(defaultQualifier) as T
    }
}