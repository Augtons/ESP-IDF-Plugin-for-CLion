package com.template.clionplugin

import com.intellij.openapi.components.service
import com.template.clionplugin.sdkconfig.LocalIDFConfigType
import com.template.clionplugin.sdkconfig.filterType
import com.template.clionplugin.sdkconfig.getConfigTypeObject
import com.template.clionplugin.sdkconfig.toConfigItems
import com.template.clionplugin.settings.IDFConfigComponent


val service = service<IDFConfigComponent>()

fun main(args: Array<String>) {
    val configs = service.allIDFConfigs.filterType(LocalIDFConfigType.TYPE)

    configs.forEach {
        it.getConfigTypeObject().icon
        val configItems = it.toConfigItems()
        val configItems2 = it.toConfigItems() as LocalIDFConfigType.ConfigItems
        configItems as LocalIDFConfigType.ConfigItems
        configItems.name
        configItems.idfPath
        configItems2.idfPath

    }

}