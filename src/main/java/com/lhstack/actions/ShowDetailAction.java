package com.lhstack.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import com.lhstack.Icons;
import com.lhstack.Item;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class ShowDetailAction extends AnAction {

    private final TableView<Item> tableView;

    private final ListTableModel<Item> models;
    private final Project project;

    public ShowDetailAction(TableView<Item> tableView, ListTableModel<Item> models, Project project) {
        super(() -> "查看详情", Icons.DETAIL);
        this.tableView = tableView;
        this.models = models;
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        LanguageTextField languageTextField = new LanguageTextField(PlainTextLanguage.INSTANCE, project, "", false) {
            @Override
            protected @NotNull EditorEx createEditor() {
                EditorEx editor = super.createEditor();
                EditorSettings settings = editor.getSettings();
                settings.setLineMarkerAreaShown(true);
                settings.setLineNumbersShown(true);
                return editor;
            }
        };
        List<Item> items = this.tableView.getSelectedObjects();
        if (!items.isEmpty()) {
            Item firstItem = items.get(0);
            dialog.setTitle(String.format("%s %s %s", firstItem.getName(), firstItem.getType(), firstItem.getAlgorithm()));
            StringBuilder sb = new StringBuilder();
            for (Item item : items) {
                sb.append(String.format("证书名称: %s\r\n", item.getName()));
                sb.append(String.format("证书类型: %s\r\n", item.getType()));
                sb.append(String.format("证书算法: %s\r\n", item.getAlgorithm()));
                sb.append(String.format("证书内容: \r\n%s\r\n\r\n", item.getCertificate().toString()));
            }
            languageTextField.setText(sb.toString());
            languageTextField.setEnabled(false);
            dialog.getContentPane().add(new JBScrollPane(languageTextField));
            dialog.setVisible(true);
        }

    }
}
