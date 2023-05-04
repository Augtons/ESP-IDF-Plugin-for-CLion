package com.template.clionplugin.settings.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.template.clionplugin.sdkconfig.LocalIDFConfigType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class LocalIDFConfigView {
    JPanel panel;
    private JTextField nameTextField;
    private TextFieldWithBrowseButton idfPathTextField;

    public LocalIDFConfigView(LocalIDFConfigType.ConfigItems configItems) {
        fillConfigItemsIntoUI(configItems);

        idfPathTextField.addActionListener(e -> {
            FileChooser.chooseFile(
                    FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null, virtualFile -> {
                        idfPathTextField.setText(virtualFile.getCanonicalPath());
                    }
            );
        });

        nameTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ComponentValidator.getInstance(nameTextField).ifPresent(v -> v.revalidate());
            }
        });

    }

    /**
     * Fill the UI with the config items.
     */
    public void fillConfigItemsIntoUI(LocalIDFConfigType.ConfigItems configItems) {
        if (configItems == null) {
            return;
        }
        nameTextField.setText(configItems.getName());
        idfPathTextField.setText(configItems.getIdfPath());
    }

    /**
     * Update the config items from the UI.
     */
    public LocalIDFConfigType.ConfigItems getConfigItemsFromUI() {
        String type = LocalIDFConfigType.TYPE;
        String name = getName();
        String idfPath = getIdfPath();

        LocalIDFConfigType.ConfigItems configItems = new LocalIDFConfigType.ConfigItems();
        configItems.setType(type);
        configItems.setName(name);
        configItems.setIdfPath(idfPath);
        return configItems;

    }

    // view getters and setters
    public JPanel getPanel() {
        return panel;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public TextFieldWithBrowseButton getIdfPathTextField() {
        return idfPathTextField;
    }

    //get name
    public String getName() {
        return nameTextField.getText();
    }

    //get idf path
    public String getIdfPath() {
        return idfPathTextField.getText();
    }

}
