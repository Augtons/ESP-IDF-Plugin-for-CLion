package com.template.clionplugin.localization

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.MainStringBundle"

object mainBundle : DynamicBundle(BUNDLE) {

    @Suppress("SpreadOperator", "unused")
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)


    @Suppress("SpreadOperator", "unused")
    fun messagePointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)

}
