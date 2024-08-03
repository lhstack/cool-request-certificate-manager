package com.lhstack;

import dev.coolrequest.tool.CoolToolPanel;
import dev.coolrequest.tool.ToolPanelFactory;

public class CertificateManagerPanelFactory implements ToolPanelFactory {
    @Override
    public CoolToolPanel createToolPanel() {
        return new CertificateManagerPanel();
    }
}
