package com.template.clionplugin.settings

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.template.clionplugin.sdkconfig.IDFConfigTypes
import com.template.clionplugin.sdkconfig.LocalIDFConfigType

@State(
    name = "com.template.clionplugin.settings.IDFConfigComponent",
    storages = [
        Storage("idf_config.xml")
    ]
)
class IDFConfigComponent : PersistentStateComponent<IDFConfigComponent.SerializedIDFConfig> {
    val idfConfigTypes = arrayOf<IDFConfigTypes>(
        LocalIDFConfigType,
    )

    var allIDFConfigs: JSONArray = JSONArray()
    val names: MutableSet<String>
        get() = mutableSetOf<String>().apply {
            allIDFConfigs.forEach {
                (it as? JSONObject)?.let { add(it.getString("name")) }
            }
        }

    /**
     * size of all ESP-IDF configs
     */
    val size: Int
        get() = allIDFConfigs.size

    /**
     * The class is only used as a template class for creating serialize configurations.
     *
     * Generic type of this component class [IDFConfigComponent]
     */
    data class SerializedIDFConfig(
        var value: String = ""
    )

    /**
     * Loads all ESP-IDF configurations provided by Template class named [SerializedIDFConfig]
     */
    override fun loadState(state: SerializedIDFConfig) {
        allIDFConfigs = JSON.parseArray(state.value) ?: JSONArray()
        // remove name equals "" or not the "name" key
        allIDFConfigs.removeIf {
            val name = (it as? JSONObject)?.get("name")
            name == null || name == ""
        }
    }

    /**
     * Saves all ESP-IDF configurations to the config file.
     */
    override fun getState(): SerializedIDFConfig = allIDFConfigs.let {
        SerializedIDFConfig(it.toJSONString())
    }

    /**
     * Reads the config from the config list by type and name
     * @param type Type of the config
     * @param name Name of the config
     * @return [JSONObject]
     */
    fun get(type: String, name: String): JSONObject? {
        return allIDFConfigs.find {
            if (it is JSONObject) {
                it.getString("type") == type && it.getString("name") == name
            } else {
                false
            }
        } as JSONObject?
    }

    /**
     * Gets the config from the config list by name
     * @param name Name of the config
     * @return [JSONObject]
     */
    fun get(name: String): JSONObject? {
        return allIDFConfigs.find {
            if (it is JSONObject) {
                it.getString("name") == name
            } else {
                false
            }
        } as JSONObject?
    }

    /**
     * Adds a new config to the config list
     * @param config New configuration
     */
    private fun add(config: JSONObject) {
        allIDFConfigs.add(config)
    }

    /**
     * Replace the config with the same name
     * @param name Name of the configuration
     * @param config New configuration
     * @see [addOrReplace]
     */
    fun replace(name: String, config: JSONObject) {
        allIDFConfigs.replaceAll {
            if (it is JSONObject && it.getString("name") == name) {
                config
            } else {
                it
            }
        }
    }

    /**
     * Add a new config to the list. Or replace the existing name config to the new one.
     *
     * NOTICE: [name][String] might not equals to [config.name][String].
     * If so, the old one will be removed and the new one will be added.
     *
     * @see [replace]
     */
    fun addOrReplace(name: String, config: JSONObject) {
        allIDFConfigs.forEachIndexed { index, it ->
            if (it is JSONObject && it.getString("name") == name) {
                allIDFConfigs[index] = config
                return
            }
        }
        add(config)
    }

    /**
     * Remove the config with the same name
     * @param name Name of the configuration
     */
    fun remove(name: String) {
        allIDFConfigs.removeIf {
            if (it is JSONObject) {
                it.getString("name") == name
            } else {
                false
            }
        }
    }

    fun getIDFConfigItem(index: Int): JSONObject? {
        return allIDFConfigs.get(index) as JSONObject?
    }

}