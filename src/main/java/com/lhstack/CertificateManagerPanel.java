package com.lhstack;

import com.intellij.openapi.project.Project;
import dev.coolrequest.tool.CoolToolPanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class CertificateManagerPanel implements CoolToolPanel {

    private Project project;

    private static final Map<String, CertificateManagerView> panels = new HashMap<>();

    @Override
    public JPanel createPanel() {
        return panels.computeIfAbsent(project.getLocationHash(),key -> new CertificateManagerView(project));
    }


    @Override
    public void showTool() {

    }

    @Override
    public void closeTool() {

    }
}
