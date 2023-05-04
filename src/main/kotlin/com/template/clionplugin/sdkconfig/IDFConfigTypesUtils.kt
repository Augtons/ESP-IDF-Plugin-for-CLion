package com.template.clionplugin.sdkconfig

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

fun JSONObject.toConfigItems() =
    IDFConfigTypes.createConfigItemsByJSONObject(this)

fun JSONObject.toConfigurable() =
    IDFConfigTypes.createConfigurableByJSONObject(this)

fun JSONObject.getConfigTypeObject() =
    IDFConfigTypes.getConfigTypeObject(this)

fun String.getConfigTypeObject() =
    IDFConfigTypes.getConfigTypeObject(this)

fun JSONArray.filterType(type: String) =
    map { it as JSONObject }.filter { it.getString("type") == type }

fun JSONArray.filterType(typeClass: IDFConfigTypes) =
    map { it as JSONObject }.filter { it.getString("type") == typeClass.typeAnnotation.type }