package com.template.clionplugin.sdkconfig

import com.alibaba.fastjson.JSONObject
import kotlin.reflect.KProperty

abstract class AbstractConfigItems {
    var jsonObject: JSONObject

    protected val delegate = Delegate()

    open var name: String? by delegate
    open var type: String? by delegate

    constructor() {
        jsonObject = JSONObject()
    }

    constructor(jsonObject: JSONObject) {
        this.jsonObject = jsonObject
    }

    override fun equals(other: Any?): Boolean {
        if (other is AbstractConfigItems) {
            return this.jsonObject == other.jsonObject
        }
        return false
    }

    override fun hashCode(): Int {
        return jsonObject.hashCode()
    }

    protected class Delegate {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return (thisRef as? AbstractConfigItems)?.let {
                it.jsonObject[property.name] as? String
            }
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            (thisRef as? AbstractConfigItems)?.let {
                it.jsonObject[property.name] = value
            }
        }
    }
}