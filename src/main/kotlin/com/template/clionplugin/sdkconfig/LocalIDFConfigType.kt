package com.template.clionplugin.sdkconfig

import com.alibaba.fastjson.JSONObject
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.NamedConfigurable
import com.intellij.openapi.ui.ValidationInfo
import com.template.clionplugin.localization.mainBundle
import com.template.clionplugin.sdkconfig.annotations.IDFConfigType
import com.template.clionplugin.settings.IDFConfigComponent
import com.template.clionplugin.settings.IDFSdkConfigSettings
import com.template.clionplugin.settings.ui.LocalIDFConfigView
import icons.IDFIcons
import java.util.function.Supplier
import javax.swing.Icon
import javax.swing.JComponent

/**
 * A configuration type of "Local ESP-IDF", Let users set a local ESP-IDF instance.
 */
@IDFConfigType(
    type = "LOCAL_IDF"
)
object LocalIDFConfigType : IDFConfigTypes() {
    @JvmField
    val TYPE = typeAnnotation.type

    //Description of this configuration type, the icon, name and description will be shown in a popup List.
    override val icon: Icon = IDFIcons.ESPRESSIF_ICON
    override val name: String = mainBundle.message("settings.localIDF")
    override val description: String = mainBundle.message("settings.localIDF")

    /**
     * Create a configurable object for this configuration type.
     */
    override fun createConfigurable() = Configurable()

    override fun createConfigurable(name: String) = Configurable(name)

    override fun createConfigurable(jsonObject: JSONObject): Any {
        if (jsonObject["type"] != TYPE) {
            throw RuntimeException("This config type is not \"$TYPE\"(LocalIDFConfigType).")
        }
        return Configurable(jsonObject)
    }

    override fun getConfigItems(): Any = ConfigItems()

    override fun getConfigItems(jsonObject: JSONObject): Any {
        if (jsonObject["type"] != TYPE) {
            throw RuntimeException("This config type is not \"$TYPE\"(LocalIDFConfigType).")
        }
        return ConfigItems(jsonObject)
    }

    /**
     * Class about what items to configure.
     * Will be edited in UI.
     */
    class ConfigItems : AbstractConfigItems {
        override var name: String? by delegate
        override var type: String? by delegate

        var idfPath: String? by delegate
        var idfPythonEnvPath: String? by delegate
        var idfToolsPath: String? by delegate

        constructor() : super()

        constructor(jsonObject: JSONObject) : super(jsonObject)

    }

    /**
     * Configurable type for local ESP-IDF. Contains a UI and all logics for editing.
     */
    class Configurable : NamedConfigurable<ConfigItems> {
        private val idfConfigComponent = service<IDFConfigComponent>()
        private var configItems: ConfigItems
        var uiView: LocalIDFConfigView

        // To evaluate the validity of the input, if the input is invalid, the configurable can't be applied.
        private var canApply: Boolean = true

        constructor() : super() {
            configItems = ConfigItems().apply {
                name = "Local ESP-IDF ${idfConfigComponent.size + 1}"
                type = TYPE
            }
            uiView = LocalIDFConfigView(configItems)
            addValidatorsToUI()
        }

        constructor(name: String) : super() {
            configItems = ConfigItems().apply {
                this.name = name
                type = TYPE
            }
            uiView = LocalIDFConfigView(configItems)
            addValidatorsToUI()
        }

        constructor(configItems: ConfigItems) : super() {
            this.configItems = configItems
            uiView = LocalIDFConfigView(configItems)
            addValidatorsToUI()
        }

        constructor(configJsonObject: JSONObject) : super() {
            this.configItems = ConfigItems(configJsonObject)
            uiView = LocalIDFConfigView(configItems)
            addValidatorsToUI()
        }

        override fun getIcon(expanded: Boolean): Icon = IDFIcons.ESPRESSIF_ICON

        override fun isModified(): Boolean = uiView.configItemsFromUI != configItems

        override fun reset() {
            uiView.fillConfigItemsIntoUI(configItems)
        }

        override fun apply() {
            if (!canApply) {
                Messages.showMessageDialog(
                    mainBundle.message("message.invalid_input") + mainBundle.message(
                        "message.not_saved",
                        configItems.name ?: ""
                    ),    // message
                    mainBundle.message("message.check_input"),  // title
                    Messages.getErrorIcon()
                )
                return
            }
            val oldName = displayName
            configItems = uiView.configItemsFromUI
            idfConfigComponent.addOrReplace(oldName, configItems.jsonObject)
            displayName = configItems.name!!
        }

        override fun getDisplayName(): String = configItems.name!!

        override fun setDisplayName(name: String?) {
            configItems.name = name
        }

        override fun getEditableObject(): ConfigItems = configItems

        override fun getBannerSlogan(): String = "Slogan"

        override fun createOptionsPanel(): JComponent = uiView.panel

        fun addValidatorsToUI() {
            val application = ApplicationManager.getApplication()
            // For the nameTextField, it should be unique and not null.
            ComponentValidator(application).withValidator(Supplier<ValidationInfo?> {
                when (uiView.name) {
                    null, "" -> {
                        ValidationInfo(mainBundle.message("error.empty"), uiView.nameTextField)
                    }
                    in IDFSdkConfigSettings.namesNotSelected -> ValidationInfo(
                        mainBundle.message("error.name.duplicate"),
                        uiView.nameTextField
                    )
                    else -> null
                }.apply {
                    //"null" means the input is valid and can be applied。
                    // So when "null" is returned, the canApply is true, aka "this == null".
                    canApply = this == null
                }
            }).installOn(uiView.nameTextField)
        }
    }
}