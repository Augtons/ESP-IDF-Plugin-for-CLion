package com.template.clionplugin.settings

import com.alibaba.fastjson.JSONObject
import com.intellij.CommonBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.MasterDetailsComponent
import com.intellij.openapi.ui.NamedConfigurable
import com.intellij.ui.CommonActionsPanel
import com.intellij.util.ui.tree.TreeUtil
import com.template.clionplugin.localization.mainBundle
import com.template.clionplugin.sdkconfig.AbstractConfigItems
import com.template.clionplugin.sdkconfig.IDFConfigTypes
import com.template.clionplugin.sdkconfig.toConfigurable
import javax.swing.tree.DefaultTreeModel

class IDFSdkConfigSettings : MasterDetailsComponent() {
    override fun getDisplayName() = mainBundle.message("settings.IDFConfig")
    private var addListActionGroup: ActionGroup
    private val idfConfigComponent = service<IDFConfigComponent>()
    private val logger = logger<IDFSdkConfigSettings>()

    private val namesToDelete = mutableListOf<String>()

    companion object {
        var namesNotSelected: MutableSet<String> = mutableSetOf()
            private set
        var allNamesInList: MutableSet<String> = mutableSetOf()
            private set
    }

    /**
     * Initializes the master details component, hides the root handles
     */
    init {
        addListActionGroup = createAddListActionGroup()
        initTree()
        tree.showsRootHandles = false
        addPreviouslySavedSettingsToTree()

        tree.addTreeSelectionListener {
            updateNames(selectedNode)
        }
    }

    /**
     * Create Toolbar Buttons on Tree
     */
    override fun createActions(fromPopup: Boolean): MutableList<AnAction> {
        return mutableListOf(
            // Add List
            MyActionGroupWrapperWithoutTitle(addListActionGroup, myTree),
            MyDeleteAction()
        )
    }

    /**
     * Called back when item deleted from tree, remove it from the list of saved settings when applying changes.
     */
    override fun onItemDeleted(item: Any?) {
        (item as? AbstractConfigItems)?.name?.let {
            namesToDelete.add(it)
        }
    }

    override fun reset() {
        super.reset()
        selectedNode.configurable?.reset()
    }

    override fun apply() {
        super.apply()
        namesToDelete.forEach {
            idfConfigComponent.remove(it)
        }
        namesToDelete.clear()
    }

    override fun isModified(): Boolean =
        namesToDelete.isNotEmpty() || super.isModified()


    /**
     * Get previously saved settings and add them to the tree.
     */
    private fun addPreviouslySavedSettingsToTree() {
        idfConfigComponent.allIDFConfigs.forEach {
            (it as JSONObject).toConfigurable().let {
                addNode(MyNode(it as NamedConfigurable<*>), myRoot)
            }
        }
        (tree.model as DefaultTreeModel).reload()
    }

    private fun updateNames(selected: MyNode?) {
        allNamesInList = TreeUtil.treeNodeTraverser(myRoot)
            .filter { it is MyNode }
            .map { (it as MyNode).displayName }
            .toMutableSet()

        namesNotSelected = mutableSetOf<String>().apply {
            selected?.displayName?.let { name ->
                addAll(allNamesInList.filter { it != name })
            }
        }
    }

    /**
     * "Add list" action group
     */
    private fun createAddListActionGroup(): ActionGroup {
        val ret = object : ActionGroup(
            CommonBundle.messagePointer("button.add"),
            CommonBundle.messagePointer("button.add"),
            AllIcons.General.Add
        ) {

            init {
                shortcutSet = CommonActionsPanel.getCommonShortcut(CommonActionsPanel.Buttons.ADD)
            }

            override fun getChildren(e: AnActionEvent?): Array<AnAction> {
                return IDFConfigTypes.getAllConfigTypes().map {
                    object : DumbAwareAction(it.name + " ...", it.description, it.icon) {

                        override fun actionPerformed(e: AnActionEvent) {
                            var count = tree.model.getChildCount(myRoot)
                            var name: String
                            // Get a unique name
                            do {
                                name = "${it.name} ${++count}"
                            } while (name in allNamesInList)
                            // add the new item to the tree.
                            val node = MyNode(it.createConfigurable(name))
                            addNode(node, myRoot)
                            (tree.model as DefaultTreeModel).reload()
                            selectNodeInTree(node)
                        }

                    }
                }.toTypedArray()
            }
        }
        return ret
    }
}