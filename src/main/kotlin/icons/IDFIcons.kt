package icons

import com.intellij.openapi.util.IconLoader

interface IDFIcons {
    companion object {
        val ESPRESSIF_ICON = icon<IDFIcons>("/icons/espressif-logo.svg")
        val ESPRESSIF_ICON_32 = icon<IDFIcons>("/icons/espressif-logo-32.svg")
        val ESPRESSIF_ICON_16 = icon<IDFIcons>("/icons/espressif-logo-16.svg")
    }
}

private inline fun <reified T> icon(path: String) =
    IconLoader.getIcon(path, T::class.java)