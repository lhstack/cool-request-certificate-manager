package com.lhstack;

import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Optional;

public class FileChooser {

    public static FileSaverDialog chooseSaveFile(String title, Project project, String... extensions) {
        FileSaverDescriptor fileSaverDescriptor = new FileSaverDescriptor(title, "保存文件", extensions);
        return FileChooserFactory.getInstance().createSaveFileDialog(fileSaverDescriptor, project);
    }

    public static Optional<VirtualFile> chooseSingleFile(String title, Project project) {
        try {
            FileChooserDialog fileChooser = FileChooserFactory.getInstance().createFileChooser(FileChooserDescriptorFactory
                    .createSingleFileDescriptor()
                    .withTitle(title), project, null);
            VirtualFile[] choose = fileChooser.choose(project);
            if (choose.length > 0) {
                return Optional.of(choose[0]);
            }
            return Optional.empty();
        } catch (Throwable e) {
            Messages.showErrorDialog(e + "", "错误");
            return Optional.empty();
        }
    }
}
