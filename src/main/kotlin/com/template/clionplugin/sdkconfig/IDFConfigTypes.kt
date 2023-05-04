package com.template.clionplugin.sdkconfig

import com.alibaba.fastjson.JSONObject
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.NamedConfigurable
import com.template.clionplugin.sdkconfig.annotations.IDFConfigType
import com.template.clionplugin.settings.IDFConfigComponent
import javax.swing.Icon

/**
 * abstract class for all configurable types
 */
abstract class IDFConfigTypes {
    val typeAnnotation = this::class.annotations.find { it is IDFConfigType } as IDFConfigType?
        ?: throw ClassNotFoundException("This class was not annotated with @IDFConfigType")

    abstract val icon: Icon?
    abstract val name: String
    abstract val description: String


    abstract fun createConfigurable(jsonObject: JSONObject): Any
    abstract fun createConfigurable(name: String): NamedConfigurable<*>
    abstract fun createConfigurable(): NamedConfigurable<*>

    abstract fun getConfigItems(jsonObject: JSONObject): Any
    abstract fun getConfigItems(): Any

    companion object {
        private val idfConfigComponent by lazy { service<IDFConfigComponent>() }

        /**
         * Get list of all configurable types that will be shown in a popup list.
         */
        fun getAllConfigTypes(): Array<IDFConfigTypes> {
            return idfConfigComponent.idfConfigTypes
        }

        fun getConfigTypeObject(type: String): IDFConfigTypes {
            return getAllConfigTypes().find { it.typeAnnotation.type == type }
                ?: throw IllegalArgumentException("Unknown config type: $type")
        }

        fun getConfigTypeObject(jsonObject: JSONObject): IDFConfigTypes {
            return jsonObject.getString("type")?.let {
                getConfigTypeObject(it)
            } ?: throw IllegalArgumentException("JSON Object has no type field")
        }

        fun createConfigItemsByJSONObject(jsonObject: JSONObject): Any {
            val type = jsonObject.getString("type")
            val typeObj = getConfigTypeObject(type)

            return typeObj.getConfigItems(jsonObject)
        }

        fun createConfigurableByJSONObject(jsonObject: JSONObject): Any {
            val type = jsonObject.getString("type")
            val typeObj = getConfigTypeObject(type)

            return typeObj.createConfigurable(jsonObject)
        }
    }
}