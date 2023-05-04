package com.template.clionplugin.settings;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

public class MyActionGroupWrapperWithoutTitle extends AnAction implements DumbAware {
    private final ActionGroup myActionGroup;
    private MasterDetailsComponent.ActionGroupWithPreselection myPreselection;
    private final Tree myTree;

    public MyActionGroupWrapperWithoutTitle(final MasterDetailsComponent.ActionGroupWithPreselection actionGroup, Tree myTree) {
        this(actionGroup.getActionGroup(), myTree);
        myPreselection = actionGroup;
    }

    public MyActionGroupWrapperWithoutTitle(final ActionGroup actionGroup, Tree myTree) {
        super(actionGroup.getTemplatePresentation().getText(), actionGroup.getTemplatePresentation().getDescription(),
                actionGroup.getTemplatePresentation().getIcon());
        myActionGroup = actionGroup;
        this.myTree = myTree;
        registerCustomShortcutSet(actionGroup.getShortcutSet(), myTree);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JBPopupFactory popupFactory = JBPopupFactory.getInstance();
        DataContext dataContext = e.getDataContext();
//        ListPopupStep step = popupFactory.createActionsStep(
//                myActionGroup, dataContext, null, false,
//                false, null, myTree,
//                true, myPreselection != null ? myPreselection.getDefaultIndex() : 0, false);
//        final ListPopup listPopup = popupFactory.createListPopup(step);

        final ListPopup listPopup = popupFactory.createActionGroupPopup(
                null, myActionGroup, dataContext,
                null, false, null
        );
        listPopup.setHandleAutoSelectionBeforeShow(true);
        if (e instanceof AnActionButton.AnActionEventWrapper) {
            ((AnActionButton.AnActionEventWrapper) e).showPopup(listPopup);
        } else {
            listPopup.showInBestPositionFor(dataContext);
        }
    }

}
