package com.lhstack.actions.self;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lhstack.Icons;
import com.lhstack.components.TextFieldDialog;
import com.lhstack.utils.NotifyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

public class ShowDetailAction extends AnAction {

    private final Supplier<String> certificatePemSupplier;
    private final Project project;

    public ShowDetailAction(Supplier<String> certificatePemSupplier, Project project) {
        super(() -> "查看证书详情", Icons.SHOW);
        this.certificatePemSupplier = certificatePemSupplier;
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            String text = certificatePemSupplier.get();
            if(StringUtils.isBlank(text)) {
                NotifyUtils.notify("证书内容是空的,请先生成或者导入证书",project);
                return ;
            }
            X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
            new TextFieldDialog(certificate.getSubjectX500Principal().getName(),certificate.toString(),project).setVisible(true);
        } catch (Throwable e) {
            new TextFieldDialog("查看证书详情出错", e.getMessage(),project).setVisible(true);
        }
    }
}
