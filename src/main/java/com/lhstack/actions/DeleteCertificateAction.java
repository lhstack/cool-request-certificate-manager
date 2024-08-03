package com.lhstack.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import com.lhstack.Icons;
import com.lhstack.Item;
import com.lhstack.NotifyUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.security.KeyStore;
import java.util.List;

public class DeleteCertificateAction extends AnAction {


    private final TableView<Item> tableView;
    private final ListTableModel<Item> models;
    private final Project project;
    private final KeyStore keyStore;

    public DeleteCertificateAction(TableView<Item> tableView, ListTableModel<Item> models, Project project, KeyStore keyStore) {
        super(() -> "删除证书", Icons.DELETE);
        this.tableView = tableView;
        this.models = models;
        this.project = project;
        this.keyStore = keyStore;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        int result = JOptionPane.showConfirmDialog(null, "你确定要删除证书吗", "警告", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                List<Item> items = this.tableView.getSelectedObjects();
                deleteItem();
                for (Item item : items) {
                    keyStore.deleteEntry(item.getName());
                }
                NotifyUtils.notify("删除已选证书成功", project);
            } catch (Throwable err) {
                NotifyUtils.notify("删除证书失败: " + err.getMessage(), project);
            }
        }
    }

    private void deleteItem() {
        int[] rows = this.tableView.getSelectedRows();
        if (rows.length > 0) {
            models.removeRow(rows[0]);
            deleteItem();
        }
    }
}
