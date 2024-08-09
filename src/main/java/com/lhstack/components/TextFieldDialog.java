package com.lhstack.components;

import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TextFieldDialog extends JDialog {

    public TextFieldDialog(String title, String text, Project project) {
        this.setTitle(title);
        this.setModal(true);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        LanguageTextField languageTextField = new LanguageTextField(PlainTextLanguage.INSTANCE, project, text,false) {
            @Override
            protected @NotNull EditorEx createEditor() {
                EditorEx editor = super.createEditor();
                EditorSettings settings = editor.getSettings();
                settings.setLineMarkerAreaShown(true);
                settings.setLineNumbersShown(true);
                return editor;
            }
        };
        this.setEnabled(false);
        JBScrollPane scrollPane = new JBScrollPane(languageTextField);
        this.setContentPane(scrollPane);
    }
}
