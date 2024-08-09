package com.lhstack.actions.self;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.lhstack.FileChooser;
import com.lhstack.Icons;
import com.lhstack.utils.NotifyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Supplier;

public class ExportCertificateAction extends AnAction {

    private final Supplier<String> pemSupplier;
    private final Project project;
    private final String exportFileName;

    public ExportCertificateAction(String exportFileName,Supplier<String> pemSupplier, Project project) {
        super(() -> "导出证书内容", Icons.EXPORT);
        this.pemSupplier = pemSupplier;
        this.project = project;
        this.exportFileName = exportFileName;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String text = this.pemSupplier.get();
        if(StringUtils.isBlank(text)) {
            NotifyUtils.notify("导出证书内容为空,请先生成或者导入证书",project);
            return ;
        }
        FileSaverDialog fileSaverDialog = FileChooser.chooseSaveFile("导出证书", project, "pem");
        VirtualFileWrapper virtualFileWrapper = fileSaverDialog.save(this.exportFileName);
        if(virtualFileWrapper != null) {
            VirtualFile virtualFile = null;
            if (virtualFileWrapper.exists()) {
                int result = JOptionPane.showConfirmDialog(null, "文件已存在,是否覆盖继续导出","警告",JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.CANCEL_OPTION) {
                    return ;
                }
                virtualFile = virtualFileWrapper.getVirtualFile();
            }else {
                virtualFile = virtualFileWrapper.getVirtualFile(true);
            }
            try{
                Files.write(virtualFile.toNioPath(),text.getBytes(StandardCharsets.UTF_8));
            }catch (Throwable e){
                NotifyUtils.notify("导出证书失败,错误信息: " + e.getMessage(),project);
            }
        }
    }
}
