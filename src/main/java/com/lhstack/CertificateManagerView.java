package com.lhstack;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.lhstack.actions.DeleteCertificateAction;
import com.lhstack.actions.ExportCertificateAction;
import com.lhstack.actions.ShowDetailAction;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class CertificateManagerView extends JPanel {


    private final Project project;

    private KeyStore keyStore;

    private ListTableModel<Item> models;

    private TableView<Item> tableView;

    private final AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * 证书虚拟文件,isNew = false时存在
     */
    private VirtualFile certificateVirtualFile;

    /**
     * 是否新建
     */
    private boolean isNew = false;
    //证书密码
    private char[] passwordArray;

    public CertificateManagerView(Project project) {
        this.project = project;
        this.init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(createImportCertificateAction());
        group.add(createEmptyCertificateAction());
        group.add(createAddCertificateAction());
        group.add(createSaveCertificateAction());
        group.add(createReSaveCertificateAction());
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("CertificateManager", group, true);
        panel.setToolbar(actionToolbar.getComponent());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createMainPanel(), BorderLayout.CENTER);
        panel.setContent(mainPanel);
        this.add(panel, BorderLayout.CENTER);
    }

    /**
     * 另存为证书
     *
     * @return
     */
    private AnAction createReSaveCertificateAction() {
        return new AnAction(() -> "另存为", Icons.RESAVE) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                //不是新建
                if (!isNew) {
                    VirtualFile virtualFile = null;
                    try {
                        FileSaverDialog fileSaverDialog = FileChooser.chooseSaveFile("另存为证书", project, "jks", "pfx", "pkcs12", "p12");
                        virtualFile = fileSaverDialog.save("newCertificate").getVirtualFile(true);
                        String password = JOptionPane.showInputDialog("请输入证书密码,不输入或者点取消则默认无密码");
                        char[] newPassword;
                        if (StringUtils.isNotBlank(password)) {
                            newPassword = password.toCharArray();
                        } else {
                            newPassword = new char[0];
                        }
                        String presentableUrl = virtualFile.getPresentableUrl();
                        FileOutputStream fos = new FileOutputStream(presentableUrl);
                        KeyStore targetKeyStore = CertificateUtils.convert(keyStore, virtualFile.getExtension());
                        targetKeyStore.store(fos, newPassword);
                        fos.close();
                        NotifyUtils.notify("另存为证书成功", project);
                    } catch (Throwable err) {
                        if (virtualFile != null) {
                            FileUtil.delete(new File(virtualFile.getPresentableUrl()));
                        }
                        NotifyUtils.notify("另存为证书错误: " + err.getMessage(), project);
                    }
                } else {
                    VirtualFile virtualFile = null;
                    try {
                        FileSaverDialog fileSaverDialog = FileChooser.chooseSaveFile("保存证书", project, "jks");
                        virtualFile = fileSaverDialog.save("newCertificate").getVirtualFile(true);
                        String password = JOptionPane.showInputDialog("请输入证书密码,不输入或者点取消则默认无密码");
                        char[] newPassword;
                        if (StringUtils.isNotBlank(password)) {
                            newPassword = password.toCharArray();
                        } else {
                            newPassword = new char[0];
                        }
                        String presentableUrl = virtualFile.getPresentableUrl();
                        FileOutputStream fos = new FileOutputStream(presentableUrl);
                        KeyStore targetKeyStore = CertificateUtils.convert(keyStore, virtualFile.getExtension());
                        targetKeyStore.store(fos, newPassword);
                        fos.close();
                        NotifyUtils.notify("另存为证书成功", project);
                    } catch (Throwable err) {
                        if (virtualFile != null) {
                            FileUtil.delete(new File(virtualFile.getPresentableUrl()));
                        }
                        NotifyUtils.notify("另存为证书错误: " + err.getMessage(), project);
                    }
                }
            }
        };
    }

    /**
     * 保存证书
     *
     * @return
     */
    private AnAction createSaveCertificateAction() {
        return new AnAction(() -> "保存", Icons.SAVE) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                //不是新建
                if (!isNew) {
                    try (FileOutputStream fos = new FileOutputStream(certificateVirtualFile.getPresentableUrl())) {
                        keyStore.store(fos, passwordArray);
                        NotifyUtils.notify("证书保存成功", project);
                    } catch (Throwable err) {
                        Messages.showErrorDialog(err.getMessage(), "保存证书错误");
                    }
                } else {
                    try {
                        FileSaverDialog fileSaverDialog = FileChooser.chooseSaveFile("保存证书", project, "jks");
                        VirtualFile virtualFile = fileSaverDialog.save("newCertificate").getVirtualFile(true);
                        String password = JOptionPane.showInputDialog("请输入证书密码,不输入或者点取消则默认无密码");
                        if (StringUtils.isNotBlank(password)) {
                            passwordArray = password.toCharArray();
                        } else {
                            passwordArray = new char[0];
                        }
                        String presentableUrl = virtualFile.getPresentableUrl();
                        FileOutputStream fos = new FileOutputStream(presentableUrl);
                        keyStore.store(fos, passwordArray);
                        fos.close();
                        certificateVirtualFile = virtualFile;
                        isNew = false;
                        NotifyUtils.notify("证书保存成功", project);
                    } catch (Throwable err) {
                        NotifyUtils.notify("保存证书错误: " + err.getMessage(), project);
                    }
                }
            }
        };
    }

    /**
     * 添加证书
     *
     * @return
     */
    private AnAction createAddCertificateAction() {
        return new AnAction(() -> "添加证书", Icons.ADD) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (keyStore == null) {
                    Messages.showErrorDialog("请先导入或者创建空证书", "错误提示");
                } else {
                    FileChooser.chooseSingleFile("请选择需要添加的证书", project).ifPresent(virtualFile -> {
                        try {
                            Certificate certificate = CertificateUtils.load(virtualFile);
                            String certificateName = JOptionPane.showInputDialog("请设置添加的证书名称");
                            if (StringUtils.isEmpty(certificateName)) {
                                throw new RuntimeException("证书名字不能为空");
                            }
                            Certificate existCertificate = keyStore.getCertificate(certificateName);
                            if (existCertificate != null) {
                                int result = JOptionPane.showConfirmDialog(null, "相同名字的证书已经存在,点击是,覆盖已有证书,点击取消,不做任何修改", "警告", JOptionPane.OK_CANCEL_OPTION);
                                if (result == JOptionPane.OK_OPTION) {
                                    keyStore.setCertificateEntry(certificateName, certificate);
                                    for (Item item : models.getItems()) {
                                        if (StringUtils.equals(item.getName(), certificateName)) {
                                            item.setType(certificate.getType());
                                            item.setCertificate(certificate)
                                                    .setName(certificateName)
                                                    .setContent(new String(certificate.getEncoded(), virtualFile.getCharset()))
                                                    .setPublicKey(certificate.getPublicKey(), virtualFile.getCharset());
                                            models.fireTableDataChanged();
                                            NotifyUtils.notify("替换已有证书成功", project);
                                        }
                                    }
                                }
                            } else {
                                keyStore.setCertificateEntry(certificateName, certificate);
                                Item item = new Item()
                                        .setId(idGenerator.incrementAndGet())
                                        .setType(certificate.getType())
                                        .setName(certificateName)
                                        .setCertificate(certificate)
                                        .setContent(new String(certificate.getEncoded(), virtualFile.getCharset()))
                                        .setPublicKey(certificate.getPublicKey(), virtualFile.getCharset());
                                models.addRow(item);
                                NotifyUtils.notify("添加证书成功", project);
                            }
                        } catch (Throwable err) {
                            NotifyUtils.notify("添加证书错误: " + err.getMessage(), project);
                        }
                    });
                }
            }
        };
    }

    /**
     * 创建空证书
     *
     * @return
     */
    private AnAction createEmptyCertificateAction() {
        return new AnAction(() -> "创建空证书", Icons.EMPTY_CERTIFICATE) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (keyStore != null) {
                    int result = JOptionPane.showConfirmDialog(null, "点击创建空证书会覆盖已导入的证书,是否确认创建空证书", "警告", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        loadCertificate(null);
                        NotifyUtils.notify("创建空证书成功", project);
                    }
                } else {
                    loadCertificate(null);
                    NotifyUtils.notify("创建空证书成功", project);
                }
            }
        };
    }

    /**
     * 导入证书
     *
     * @return
     */
    private AnAction createImportCertificateAction() {
        return new AnAction(() -> "导入证书", Icons.IMPORT) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (keyStore != null) {
                    int result = JOptionPane.showConfirmDialog(null, "点击导入会覆盖已导入的证书,是否确认导入", "警告", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        FileChooser.chooseSingleFile("选择证书", project).ifPresent(virtualFile -> {
                            certificateVirtualFile = virtualFile;
                            CertificateManagerView.this.loadCertificate(virtualFile);
                        });
                    }
                } else {
                    FileChooser.chooseSingleFile("选择证书", project).ifPresent(virtualFile -> {
                        certificateVirtualFile = virtualFile;
                        CertificateManagerView.this.loadCertificate(virtualFile);
                    });
                }
            }

        };
    }

    /**
     * 加载证书
     *
     * @param virtualFile
     */
    private void loadCertificate(VirtualFile virtualFile) {
        try {
            idGenerator.set(0);
            this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            if (virtualFile == null) {
                isNew = true;
                this.keyStore.load(null, null);
                this.models.setItems(new ArrayList<>());
            } else {
                isNew = false;
                String password = JOptionPane.showInputDialog(null, "证书密码,如果有,请输入,如果没有,请点确认或者取消", "证书密码", JOptionPane.PLAIN_MESSAGE);
                char[] passwordArray = password == null || StringUtils.isEmpty(password) ? new char[0] : password.toCharArray();
                this.passwordArray = passwordArray;
                this.keyStore.load(virtualFile.getInputStream(), passwordArray);
                List<String> list = EnumerationUtils.toList(this.keyStore.aliases());
                list.sort(Comparator.comparing(Function.identity()));
                this.models.setItems(new ArrayList<>());
                for (String alias : list) {
                    Certificate certificate = this.keyStore.getCertificate(alias);
                    Item item = new Item()
                            .setId(idGenerator.incrementAndGet())
                            .setContent(new String(certificate.getEncoded(), virtualFile.getCharset()))
                            .setName(alias)
                            .setType(certificate.getType())
                            .setPublicKey(certificate.getPublicKey(), virtualFile.getCharset())
                            .setCertificate(certificate);
                    this.models.addRow(item);
                }
            }
            NotifyUtils.notify("加载证书成功", project);
        } catch (Throwable e) {
            this.keyStore = null;
            NotifyUtils.notify("加载证书错误: " + e.getMessage(), project);
        }
    }

    private JComponent createMainPanel() {
        this.models = new ListTableModel<>(
                ItemColumn.create("Id", Item::getId),
                ItemColumn.create("证书名称", Item::getName),
                ItemColumn.create("证书类型", Item::getType),
                ItemColumn.create("加密算法", Item::getAlgorithm)
        );
        this.tableView = new TableView<>(this.models) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (!(this.getModel() instanceof ListTableModel)) {
                    return super.getCellRenderer(row, column);
                } else {
                    ColumnInfo<Item, ?> columnInfo = this.getListTableModel().getColumnInfos()[this.convertColumnIndexToModel(column)];
                    Item item = this.getRow(row);
                    TableCellRenderer renderer = columnInfo.getCustomizedRenderer(item, columnInfo.getRenderer(item));
                    return renderer == null ? wrapperRender(super.getCellRenderer(row, column)) : renderer;
                }
            }

            private TableCellRenderer wrapperRender(TableCellRenderer cellRenderer) {

                return new TableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component component = cellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (component instanceof JLabel) {
                            ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
                        }
                        return component;
                    }
                };
            }
        };
        JTableHeader tableHeader = this.tableView.getTableHeader();
        TableCellRenderer defaultRenderer = tableHeader.getDefaultRenderer();
        tableHeader.setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JComponent component = (JComponent) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (component instanceof JLabel) {
                ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
            }
            return component;
        });

//        this.tableView.setCellSelectionEnabled(false);
//        this.tableView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tableView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    CertificateManagerView.this.createPopupMenus(e);
                }
            }
        });
        return new JBScrollPane(tableView);
    }

    /**
     * 创建右键菜单
     *
     * @param e
     */
    private void createPopupMenus(MouseEvent e) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ShowDetailAction(tableView, models, project));
        group.add(new DeleteCertificateAction(tableView, models, project, this.keyStore));
        group.add(new ExportCertificateAction(tableView, models, project, () -> this.keyStore, () -> passwordArray));
        ListPopup listPopup = JBPopupFactory.getInstance().createActionGroupPopup("操作", group, DataContext.EMPTY_CONTEXT, JBPopupFactory.ActionSelectionAid.MNEMONICS, true);
        listPopup.show(new RelativePoint(e.getComponent(), new Point(e.getX(), e.getY())));
    }

}
