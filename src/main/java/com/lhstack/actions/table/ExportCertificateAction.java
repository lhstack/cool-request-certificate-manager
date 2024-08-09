package com.lhstack.actions.table;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import com.lhstack.FileChooser;
import com.lhstack.Icons;
import com.lhstack.Item;
import com.lhstack.utils.CertificateUtils;
import com.lhstack.utils.NotifyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.security.KeyStore;
import java.util.List;
import java.util.function.Supplier;

public class ExportCertificateAction extends AnAction {

    private final TableView<Item> tableView;

    private final ListTableModel<Item> models;

    private final Project project;

    private final Supplier<KeyStore> keyStoreSupplier;
    private final Supplier<char[]> passwordSupplier;

    public ExportCertificateAction(TableView<Item> tableView, ListTableModel<Item> models, Project project, Supplier<KeyStore> keyStoreSupplier, Supplier<char[]> passwordSupplier) {
        super(() -> "导出证书", Icons.EXPORT);
        this.tableView = tableView;
        this.models = models;
        this.project = project;
        this.keyStoreSupplier = keyStoreSupplier;
        this.passwordSupplier = passwordSupplier;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<Item> items = this.tableView.getSelectedObjects();
        if (CollectionUtils.isNotEmpty(items)) {
            FileSaverDialog fileSaverDialog = FileChooser.chooseSaveFile("导出证书", project, "pem", "jks", "crt", "cer","pkcs8");
            VirtualFileWrapper virtualFileWrapper;
            VirtualFile virtualFile;
            if (items.size() == 1) {
                Item item = items.get(0);
                virtualFileWrapper = fileSaverDialog.save(item.getName());
            } else {
                virtualFileWrapper = fileSaverDialog.save("multi-certificate");
            }
            if(virtualFileWrapper != null){
                if(virtualFileWrapper.exists()){
                    virtualFile = virtualFileWrapper.getVirtualFile();
                }else {
                    virtualFile = virtualFileWrapper.getVirtualFile(true);
                }
                //如果没有文件,则创建
                try {
                    virtualFile = virtualFileWrapper.getVirtualFile(true);
                    String extension = virtualFile.getExtension();
                    FileUtils.writeByteArrayToFile(virtualFileWrapper.getFile(), CertificateUtils.export(keyStoreSupplier,passwordSupplier,items, extension));
                    NotifyUtils.notify("导出已选证书成功", project);
                } catch (Throwable err) {
                    FileUtil.delete(new File(virtualFile.getPresentableUrl()));
                    NotifyUtils.notify("证书导出错误: " + err.getMessage(), project);
                }
            }

        }
    }
}
