package com.lhstack;

import com.intellij.openapi.ui.Messages;
import dev.coolrequest.tool.CoolToolPanel;
import dev.coolrequest.tool.ToolPanelFactory;

public class CertificateManagerPanelFactory implements ToolPanelFactory {
    @Override
    public CoolToolPanel createToolPanel() {
        try{
            return new CertificateManagerPanel();
        }catch (Throwable e){
            Messages.showErrorDialog(e.getMessage(),"安装错误");
            throw new RuntimeException(e);
        }
    }
}
