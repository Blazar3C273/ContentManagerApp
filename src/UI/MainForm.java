/*
 * Created by JFormDesigner on Wed Nov 26 22:39:34 AST 2014
 */

package UI;

import PropertiesLoader.PropertiesLoader;
import files.*;
import files.FileSerialization.JsonFormatStrings;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import network.NetworkConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Anatoliy Stepanenko
 */
public class MainForm extends JFrame {

    public static final String fileName = "properties.json";
    private static final String DEFAULT_CATEGORY_NAME = "Смени имя категории";
    public static final int BACKGROUND_COLOR = 0xFF62AD00;
    public static final String PNG = ".png";

    PropertiesLoader propertiesLoader;

    Item currentItem;
    private MyUndoManager undoManager;


    public static void main(String[] args) {
        MainForm mainForm = new MainForm(args);
        mainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            mainForm.statusPanel.setBorder(new TitledBorder(""));
        } catch (RuntimeException e) {
        }
        mainForm.statusPanel.add(mainForm.statusLabel, BorderLayout.WEST);
        mainForm.setVisible(true);
    }

    public MainForm(String[] args) {
        initComponents();
        HTMLEditorKit editorKit = new HTMLEditorKit();
        editorPane1.setEditorKitForContentType("text/html", editorKit);
        editorPane1.setContentType("text/html");
        editorPane1.setBackground(new Color(BACKGROUND_COLOR));

        itemTree.setTransferHandler(new MyTranferHandler());
        propertiesLoader = new PropertiesLoader(fileName);
        propertiesLoader.loadFromFile();
        if (args.length < 1)
        NetworkConnection.setServerURL(propertiesLoader.getServerAddress());
        else {
            try {
                URI.create(args[0]);
                NetworkConnection.setServerURL(args[0]);
            } catch (IllegalArgumentException e) {
                NetworkConnection.setServerURL(propertiesLoader.getServerAddress());
            }
        }


        DefaultTreeModel model = (DefaultTreeModel) itemTree.getModel();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) model.getRoot();
        Map<String, Long> categories = null;

        if (NetworkConnection.isServerOnline()) {
            try {
                categories = NetworkConnection.getCategoryNames();
            } catch (NetworkConnection.DataBaseError dataBaseError) {
                dataBaseError.printStackTrace();
            }

            if (categories != null) {
                for (String s : categories.keySet()) {
                    Map<String, String> ids = NetworkConnection.getIDsByCategory(s);
                    categoryComboBox.addItem(s);
                    if (s.equals("root")) {
                        for (String item_id : ids.keySet()) {
                            Item item = new Item();
                            item.set_id(item_id);
                            item.setItemName(ids.get(item_id));
                            item.setChanged(false);
                            DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
                            node.setAllowsChildren(false);
                            treeNode.add(node);
                        }
                    } else {
                        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(s);
                        newChild.setAllowsChildren(true);
                        for (String item_id : ids.keySet()) {
                            Item item = new Item();
                            item.set_id(item_id);
                            item.setItemName(ids.get(item_id));
                            item.setChanged(false);
                            DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
                            node.setAllowsChildren(false);
                            newChild.add(node);
                        }
                        treeNode.add(newChild);
                    }
                }
            }
        model.reload();
        itemTree.updateUI();
        statusLabelComponentShown(new ComponentEvent(statusLabel, 5));
        }
    }


    private void statusLabelComponentShown(ComponentEvent e) {
        if (NetworkConnection.isServerOnline())
            ((JLabel) (e.getComponent())).setText("Сервер Онлайн.");
        else
            ((JLabel) (e.getComponent())).setText("Сервер недоступен.");
    }

    private void ItemTreeValueChanged(TreeSelectionEvent e) {
        /*
        * Последовательность действий при выборе экспоната:
        *  Загрузить инфу из Item во все щели)
        *  Если в итеме пусто идем на сревер за инфой, парсим ее в итем м отображаем в гуи
        *  Устанавливаем обработчики
        *
        * */
        System.out.println("itemTreeValueChanged()");
        DefaultMutableTreeNode st = (DefaultMutableTreeNode) itemTree.getLastSelectedPathComponent();

        if (st != null && st.getUserObject().getClass().equals(Item.class)) {
            try {
                Item item;
                if (((Item) st.getUserObject()).isChanged()) {
                    item = (Item) st.getUserObject();
                    System.out.println("item is changed");
                } else {
                    item = NetworkConnection.getItemByID(((Item) st.getUserObject()).get_id(), "exibit");
                    st.setUserObject(item);
                    System.out.println("item load from server");
                }
                currentItem = item;
                itemNameTextField.setText(item.getItemName());
                editorPane1.setEnabled(true);
                editorPane1.setText(item.getText());
                undoManager = new MyUndoManager();
                editorPane1.getDocument().addUndoableEditListener(undoManager);
                contentTab.updateUI();
                categoryComboBox.setSelectedItem(item.getParent());
                textQR.setText(item.get_id());
                byte[] is = QRCode.from(item.get_id()).to(ImageType.PNG).withSize(qrPanel.getWidth() - 30, qrPanel.getHeight() - 30).stream().toByteArray();
                Image image = ImageIO.read(new ByteArrayInputStream(is));
                qrPanel.removeAll();
                JLabel label = new JLabel(new ImageIcon(image));
                qrPanel.add(label);
                qrPanel.updateUI();
                fillAttachmentTable(item);
            } catch (NetworkConnection.DataBaseError dataBaseError) {
                dataBaseError.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            itemTree.updateUI();
        }

    }

    private void fillAttachmentTable(Item item) {

        Object[] columnIdentifiers = new Object[attachmentTabel.getModel().getColumnCount()];
        for (int i = 0; i < columnIdentifiers.length; i++) {
            columnIdentifiers[i] = attachmentTabel.getColumnName(i);
        }
        Object[][] rows;
        ArrayList<AttacheFile> files = item.getAttachmentFiles();
        rows = new Object[files.size()][];
        for (int i = 0; i < rows.length; i++) {
            AttacheFile file = files.get(i);

            String info = "";
            switch (file.getAttachmentType()) {
                case JsonFormatStrings.ATTACHMENT_TYPE_VIDEO:
                    info = String.valueOf(((VideoFile) file).getTimeSec()) + "сек.";
                    break;
                case JsonFormatStrings.ATTACHMENT_TYPE_AUDIO:
                    info = String.valueOf(((AudioFile) file).getTimeSec()) + "сек.";
                    break;
                case JsonFormatStrings.ATTACHMENT_TYPE_PICTURE:
                    PictureFile file1 = (PictureFile) file;
                    info = String.valueOf(file1.getWidth()) + "х" + String.valueOf(file1.getHeight());
                    break;
            }

            rows[i] = new Object[]{file.getFilename(), file.getShortName(), file.getDescription(), file.getAttachmentType(), info};
        }
        TableCellEditor cellEditor = attachmentTabel.getCellEditor(0, 3);
        cellEditor.getCellEditorValue();
        ((DefaultTableModel) attachmentTabel.getModel()).setDataVector(rows, columnIdentifiers);
        attachmentTabel.getColumnModel().getColumn(3).setCellEditor(cellEditor);
    }

    private void addFileButtonActionPerformed(ActionEvent e) {

        ArrayList<AttacheFile> files = new ArrayList<>();
        if (files == null) {
            files = new ArrayList<>();
        }
        FileDialog dialog = new FileDialog(this, "Выбор файла", FileDialog.LOAD);
        dialog.setVisible(true);
        if (dialog.getFile() == null) {
            return;
        }
        File file = (dialog.getFiles()[0]);
        String contentType;
        AttacheFile newFile;
        try {
            contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                JOptionPane.showMessageDialog(this, "Системой принимаются файлы со следующих типов:\n" +
                        "Аудио,Видео,Изображения.", "Неверный тип файла!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!file.canRead()) {
                System.out.println("File " + file + " cant be read.");
            }
            switch (contentType.substring(0, 5)) {
                case "audio":
                    newFile = new AudioFile(file);
                    break;
                case "video":
                    newFile = new VideoFile(file);
                    break;
                case "image":
                    newFile = new PictureFile();
                    BufferedImage bufferedImage = ImageIO.read(file);
                    ((PictureFile) newFile).setHeight(bufferedImage.getHeight());
                    ((PictureFile) newFile).setWidth(bufferedImage.getWidth());
                    break;
                default:
                    System.out.println(contentType);
                    JOptionPane.showMessageDialog(this, "Системой принимаются файлы со следующих типов:\n" +
                            "Аудио,Видео,Изображения.", "Неверный тип файла!", JOptionPane.ERROR_MESSAGE);
                    return;
            }
            newFile.setShortName("Новый файл");
            newFile.setFilename(file.getName());
            files.add(newFile);
            currentItem.setAttachments(files);
            currentItem.setHasAttachments(true);
            try {
                NetworkConnection.putFileByItem(currentItem, file, "exibit");
            } catch (NetworkConnection.DataBaseError dataBaseError) {
                dataBaseError.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка отправки файла на сервер", "Не удалось отправить файл.", JOptionPane.ERROR_MESSAGE);
                //TODO откатить изменения в случае ошибки
                return;
            }
            fillAttachmentTable(currentItem);
            attachmentTabel.updateUI();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void attachmentTabelPropertyChange(PropertyChangeEvent e) {
        ArrayList<AttacheFile> attacheFiles = new ArrayList<>();
        for (int i = 0; i < attachmentTabel.getModel().getRowCount(); i++) {
            String type = (String) attachmentTabel.getModel().getValueAt(i, 3);

            if (type == null) {
                break;
            }

            System.out.println(type);
            AttacheFile file;
            TableModel model = attachmentTabel.getModel();
            //%time% сек
            //%w%х%h%
            //TODO check correct work after column format changing
            String valueAt = (String) model.getValueAt(i, 4);
            int index = valueAt.indexOf("с");//Russian 'с'(эс)
            switch (type) {
                case "Видео":
                    file = new VideoFile(null);
                    file.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_VIDEO);
                    ((VideoFile) file).setTimeSec(Integer.valueOf(valueAt.substring(0, index)));
                    break;
                case "Аудио":
                    file = new AudioFile(null);
                    file.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_AUDIO);
                    String substring = valueAt.substring(0, index);
                    Integer integer = Integer.valueOf(substring);
                    ((AudioFile) file).setTimeSec(integer);
                    break;
                case "Картинка":
                    file = new PictureFile();
                    file.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_PICTURE);
                    //TODO write normal input validator. This is crappy shit. btw this is not x, это русская х.
                    int xIndex = valueAt.lastIndexOf('х');
                    String substring2 = valueAt.substring(0, xIndex);
                    int width = Integer.valueOf(substring2);
                    String substring1 = valueAt.substring(xIndex + 1, valueAt.length());
                    int height = Integer.valueOf(substring1);
                    ((PictureFile) file).setWidth(width);
                    ((PictureFile) file).setHeight(height);
                    break;
                default:
                    file = new AttacheFile(null);
            }
            file.setFilename((String) model.getValueAt(i, 0));
            file.setShortName((String) model.getValueAt(i, 1));
            file.setDescription((String) model.getValueAt(i, 2));
            file.setAttachmentType((String) model.getValueAt(i, 3));
            attacheFiles.add(file);
        }
        if (currentItem != null) {
            currentItem.removeAllAttachments();
            currentItem.setAttachments(attacheFiles);
        }
    }

    private void itemNameTextFieldCaretUpdate(CaretEvent e) {
        System.out.println(e);
        if (currentItem != null) {
            currentItem.setItemName(itemNameTextField.getText());
            itemTree.updateUI();
        }
    }

    private void saveMenuActionPerformed(ActionEvent e) {

        DefaultMutableTreeNode fuu = (DefaultMutableTreeNode) itemTree.getModel().getRoot();
        Enumeration depthFirstEnumeration = fuu.depthFirstEnumeration();
        while (depthFirstEnumeration.hasMoreElements()) {
            DefaultMutableTreeNode element = (DefaultMutableTreeNode) depthFirstEnumeration.nextElement();
            element.toString();
            Item item;
            if (element.getUserObject().getClass().equals(Item.class)) {
                item = (Item) element.getUserObject();
                if (item.isChanged()) {
                    try {
                        NetworkConnection.putItemByID(item, "exibit");
                    } catch (NetworkConnection.DataBaseError dataBaseError) {
                        dataBaseError.printStackTrace();
                    }
                }
            }
        }

    }

    private void addItemMenuActionPerformed(ActionEvent e) {
        Item item = new Item();
        item.set_id(NetworkConnection.getUUID(1).get(0));
        item.setItemName("Новый экспонат.");
        item.setText(JsonFormatStrings.NEW_ITEM_DESCRIPTION_TEXT);
        DefaultMutableTreeNode newItemNode = new DefaultMutableTreeNode(item);
        ((DefaultMutableTreeNode) itemTree.getModel().getRoot()).add(newItemNode);
        try {
            NetworkConnection.putItemByID(item, "exibit");
        } catch (NetworkConnection.DataBaseError dataBaseError) {
            dataBaseError.printStackTrace();
        }
        itemTree.updateUI();
    }

    private void delFileButtonActionPerformed(ActionEvent e) {
        int index =
                attachmentTabel.getSelectedRow();
        ArrayList<AttacheFile> files = currentItem.getAttachmentFiles();
        files.remove(index);
        currentItem.removeAllAttachments();
        currentItem.setAttachments(files);
        ((DefaultTableModel) attachmentTabel.getModel()).removeRow(index);
        attachmentTabel.updateUI();
    }

    private void categoryComboBoxItemStateChanged(ItemEvent e) {
        if (currentItem == null) {
            return;
        }
        currentItem.setParent((String) categoryComboBox.getModel().getSelectedItem());

    }

    private void fontMenuActionPerformed(ActionEvent e) {
        //open form
        new Font(this).setVisible(true);
        //set new style from form
    }

    private void stringShiftingActionPerformed(ActionEvent e) {
        HTML.Tag tag = HTML.Tag.BR;
        String html = "<" + tag.toString() + ">";
        new HTMLEditorKit.InsertHTMLTextAction("", html, HTML.Tag.BODY, tag).actionPerformed(e);
        editorPane1.setText(editorPane1.getText());
    }

    private void paragraphActionPerformed(ActionEvent e) {
        String html;
        if (editorPane1.getSelectedText() == null) {
            html = "<p>Новый абзац</p>";
        } else
            html = "<p>" + editorPane1.getSelectedText() + "</p>";
        editorPane1.replaceSelection("");
        new HTMLEditorKit.InsertHTMLTextAction("paragraph", html, HTML.Tag.BODY, HTML.Tag.P).actionPerformed(e);
        editorPane1.setText(editorPane1.getText());

    }

    private void insertImgActionPerformed(ActionEvent e) {
        int cursorPosition = editorPane1.getSelectionStart();
        new ChooseImageDialog(this, currentItem, cursorPosition).setVisible(true);
        editorPane1.setText(editorPane1.getText());
    }

    private void aligmentActionPerformed(ActionEvent e) {
        HTML.Tag tag = HTML.Tag.DIV;
        int start = editorPane1.getSelectionStart();
        String string = editorPane1.getSelectedText();
        if (string == null) {
            string = "";
        }
        new HTMLEditorKit.CutAction().actionPerformed(e);
        String alignType = "center";
        switch (e.getActionCommand()) {
            case "align-type-left":
                alignType = "left";
                break;
            case "align-type-just":
                alignType = "justify";
                break;
        }
        String html = "<" + tag.toString() + " align=\"" + alignType + "\">" + string + "</" + tag.toString() + ">";
        new HTMLEditorKit.InsertHTMLTextAction("", html, HTML.Tag.BODY, tag).actionPerformed(e);
        editorPane1.setSelectionStart(start);
        editorPane1.setSelectionEnd(start);
        //new HTMLEditorKit.PasteAction().actionPerformed(e);
        //new HTMLEditorKit.Action
        editorPane1.setText(editorPane1.getText());

    }

    private void exitMenuItemActionPerformed(ActionEvent e) {
        //TODO add dialog "Вы действительно хотите выйти?"
        JOptionPane.showConfirmDialog(this, "Вы действительно хотие выйти?", "Вы действительно хотие выйти?", JOptionPane.OK_CANCEL_OPTION, EXIT_ON_CLOSE);
        //dispose();

    }

    private void aboutMenuActionPerformed(ActionEvent e) {
        // TODO add new window about program
    }

    private void qrPanelMouseClicked(MouseEvent e) {
        if (currentItem == null) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Введите осмысленное имя файла...");
        byte[] file = QRCode.from(currentItem.get_id()).to(ImageType.PNG).withSize(256, 256).stream().toByteArray();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setName(currentItem.getItemName() + PNG);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f.getName().endsWith(PNG) && f.canWrite());
            }

            @Override
            public String getDescription() {
                return "*.png";
            }
        });
        int userSelection = fileChooser.showSaveDialog(this);


        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().endsWith(PNG))
                fileToSave = new File(fileToSave.getAbsolutePath() + PNG);
            if (fileToSave.getName().isEmpty() || fileToSave.getName().equals("") || fileChooser.getName() == null)
                fileToSave = new File(fileToSave.getAbsolutePath() + currentItem.getItemName() + PNG);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileToSave);
                fileOutputStream.write(file);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
        }


    }

    public JEditorPane getEditorPane1() {
        return editorPane1;
    }

    private void editorPane1PropertyChange(PropertyChangeEvent e) {
        currentItem.setText(editorPane1.getText());
    }

    private void editorPane1CaretUpdate(CaretEvent e) {
        if (editorPane1.isEnabled() && currentItem != null) {
            currentItem.setText(editorPane1.getText().replace("\r", " "));
            currentItem.setText(editorPane1.getText().replace("\n", " "));
            currentItem.setText(editorPane1.getText().replace("\n \n", " "));
        }
        // System.out.println("CaretUpdate");
    }

    private void addCategoryItemMenuActionPerformed(ActionEvent e) {
        DefaultTreeModel model = ((DefaultTreeModel) itemTree.getModel());
        MutableTreeNode newCategory = new DefaultMutableTreeNode(DEFAULT_CATEGORY_NAME, true);
        model.insertNodeInto(newCategory, (MutableTreeNode) model.getRoot(), ((TreeNode) model.getRoot()).getChildCount());

    }

    private void cloneMenuItemActionPerformed(ActionEvent e) {
        Item newItem = new Item();
        newItem.set_id(NetworkConnection.getUUID(1).get(0));
        newItem.setItemName(currentItem.getItemName() + "_Копия");
        newItem.setText(currentItem.getText());
        newItem.setChanged(true);
        newItem.setParent(currentItem.getParent());
        try {
            NetworkConnection.putItemByID(newItem, "exibit");
            Enumeration rootChildren = ((DefaultMutableTreeNode) itemTree.getModel().getRoot()).children();
            while (rootChildren.hasMoreElements()) {
                DefaultMutableTreeNode node = ((DefaultMutableTreeNode) rootChildren.nextElement());
                if (node.getUserObject().toString().equals(newItem.getParent())) {
                    DefaultMutableTreeNode newItemNode = new DefaultMutableTreeNode(newItem);
                    newItemNode.setAllowsChildren(false);
                    ((DefaultTreeModel) itemTree.getModel()).insertNodeInto(newItemNode, node, node.getChildCount());
                }
            }

        } catch (NetworkConnection.DataBaseError dataBaseError) {
            dataBaseError.printStackTrace();
        }

    }

    private void copyQRButtonActionPerformed(ActionEvent e) {
        StringSelection contents = new StringSelection(currentItem.get_id());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, contents);
    }

    private void feedbackMenuActionPerformed(ActionEvent e) {
        System.out.println("feedback menu pressed");
    }

    private void feedbackMenuMousePressed(MouseEvent e) {
        new FeedbackForm().setVisible(true);
    }

    private void editorPane1KeyReleased(KeyEvent e) {
        //if backspace or delete is pressed - delete empty tags.

        if (e.getKeyCode() == 8 || e.getKeyCode() == 46) {
            currentItem.setText(editorPane1.getText());
            editorPane1.setText(currentItem.getText());
        }

        if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown() && undoManager.canUndo())
            undoManager.undo();


    }

    private void button4ActionPerformed(ActionEvent e) {
        if (undoManager.canUndo())
            undoManager.undo();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - gfic -snt-slt
        statusPanel = new JPanel();
        statusLabel = new JLabel();
        menuBarPanel = new JPanel();
        menuBar = new JMenuBar();
        menu1 = new JMenu();
        addINewItem = new JMenuItem();
        cloneMenuItem = new JMenuItem();
        saveMenu = new JMenuItem();
        addCategoryItemMenu = new JMenuItem();
        exitMenuItem = new JMenuItem();
        feedbackMenu = new JMenu();
        aboutMenu = new JMenu();
        mainPanel = new JPanel();
        itemTreePanel = new JPanel();
        scrollPane2 = new JScrollPane();
        itemTree = new JTree();
        qrPanel = new JPanel();
        panel4 = new JPanel();
        copyQRButton = new JButton();
        textQR = new JTextField();
        mainItemPanel = new JPanel();
        contentTab = new JScrollPane();
        itemPanel = new JPanel();
        panel1 = new JPanel();
        itemNameTextField = new JTextField();
        panel2 = new JPanel();
        categoryPanel = new JPanel();
        categoryComboBox = new JComboBox();
        toolBar1 = new JToolBar();
        fontMenu = new JButton();
        stringShifting = new JButton();
        paragraph = new JButton();
        insertImg = new JButton();
        aligment = new JButton();
        button3 = new JButton();
        button1 = new JButton();
        button2 = new JButton();
        button4 = new JButton();
        scrollPane1 = new JScrollPane();
        editorPane1 = new JEditorPane();
        attachPanel = new JPanel();
        scrollPane3 = new JScrollPane();
        attachmentTabel = new JTable();
        panel3 = new JPanel();
        delFIileButton = new JButton();
        addFileButton = new JButton();

        //======== this ========
        setMinimumSize(new Dimension(1116, 683));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== statusPanel ========
        {
            statusPanel.setBorder(new TitledBorder(""));
            statusPanel.setMinimumSize(new Dimension(21, 20));
            statusPanel.setPreferredSize(new Dimension(21, 23));

            // JFormDesigner evaluation mark
            statusPanel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), statusPanel.getBorder()));
            statusPanel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });

            statusPanel.setLayout(new BorderLayout());

            //---- statusLabel ----
            statusLabel.setText("\u0421\u0435\u0440\u0432\u0435\u0440 \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d");
            statusLabel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    statusLabelComponentShown(e);
                }
            });
            statusPanel.add(statusLabel, BorderLayout.WEST);
        }
        contentPane.add(statusPanel, BorderLayout.SOUTH);

        //======== menuBarPanel ========
        {
            menuBarPanel.setLayout(new BorderLayout());

            //======== menuBar ========
            {

                //======== menu1 ========
                {
                    menu1.setText("\u0424\u0430\u0439\u043b");

                    //---- addINewItem ----
                    addINewItem.setText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u044d\u043a\u0441\u043f\u043e\u043d\u0430\u0442");
                    addINewItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            addItemMenuActionPerformed(e);
                        }
                    });
                    menu1.add(addINewItem);

                    //---- cloneMenuItem ----
                    cloneMenuItem.setText("\u041a\u043b\u043e\u043d\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0442\u0435\u043a\u0443\u0449\u0438\u0439 \u044d\u043a\u0441\u043f\u043e\u043d\u0430\u0442");
                    cloneMenuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cloneMenuItemActionPerformed(e);
                        }
                    });
                    menu1.add(cloneMenuItem);

                    //---- saveMenu ----
                    saveMenu.setText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0432\u0441\u0435 \u044d\u043a\u0441\u043f\u043e\u043d\u0430\u0442\u044b");
                    saveMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            saveMenuActionPerformed(e);
                        }
                    });
                    menu1.add(saveMenu);
                    menu1.addSeparator();

                    //---- addCategoryItemMenu ----
                    addCategoryItemMenu.setText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044e");
                    addCategoryItemMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            addCategoryItemMenuActionPerformed(e);
                        }
                    });
                    menu1.add(addCategoryItemMenu);
                    menu1.addSeparator();

                    //---- exitMenuItem ----
                    exitMenuItem.setText("\u0412\u044b\u0445\u043e\u0434");
                    exitMenuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            exitMenuItemActionPerformed(e);
                        }
                    });
                    menu1.add(exitMenuItem);
                }
                menuBar.add(menu1);

                //======== feedbackMenu ========
                {
                    feedbackMenu.setText("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043e\u0431\u0440\u0430\u0442\u043d\u043e\u0439 \u0441\u0432\u044f\u0437\u044c\u044e");
                    feedbackMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            feedbackMenuActionPerformed(e);
                        }
                    });
                    feedbackMenu.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            feedbackMenuMousePressed(e);
                        }
                    });
                }
                menuBar.add(feedbackMenu);

                //======== aboutMenu ========
                {
                    aboutMenu.setText("\u041e \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u0435");
                    aboutMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            aboutMenuActionPerformed(e);
                        }
                    });
                }
                menuBar.add(aboutMenu);
            }
            menuBarPanel.add(menuBar, BorderLayout.CENTER);
        }
        contentPane.add(menuBarPanel, BorderLayout.NORTH);

        //======== mainPanel ========
        {
            mainPanel.setPreferredSize(new Dimension(1100, 600));
            mainPanel.setMinimumSize(new Dimension(1100, 600));
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

            //======== itemTreePanel ========
            {
                itemTreePanel.setMinimumSize(new Dimension(250, 600));
                itemTreePanel.setPreferredSize(new Dimension(275, 600));
                itemTreePanel.setMaximumSize(new Dimension(250, 34267));
                itemTreePanel.setLayout(new BoxLayout(itemTreePanel, BoxLayout.Y_AXIS));

                //======== scrollPane2 ========
                {
                    scrollPane2.setBorder(new TitledBorder(null, "\u0414\u0435\u0440\u0435\u0432\u043e \u044d\u043a\u0441\u043f\u043e\u043d\u0430\u0442\u043e\u0432", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black));

                    //---- itemTree ----
                    itemTree.setModel(new DefaultTreeModel(
                            new DefaultMutableTreeNode("root") {
                                {
                                }
                            }));
                    itemTree.setRootVisible(false);
                    itemTree.setEditable(true);
                    itemTree.setDragEnabled(true);
                    itemTree.addTreeSelectionListener(new TreeSelectionListener() {
                        @Override
                        public void valueChanged(TreeSelectionEvent e) {
                            ItemTreeValueChanged(e);
                        }
                    });
                    scrollPane2.setViewportView(itemTree);
                }
                itemTreePanel.add(scrollPane2);

                //======== qrPanel ========
                {
                    qrPanel.setPreferredSize(new Dimension(275, 200));
                    qrPanel.setBorder(new TitledBorder(null, "QR \u041a\u043e\u0434 \u0442\u0435\u043a\u0443\u0449\u0435\u0433\u043e \u044d\u043a\u0441\u043f\u043e\u043d\u0430\u0442\u0430", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black));
                    qrPanel.setMaximumSize(new Dimension(275, 250));
                    qrPanel.setMinimumSize(new Dimension(100, 100));
                    qrPanel.setBackground(Color.white);
                    qrPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            qrPanelMouseClicked(e);
                        }
                    });
                    qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.X_AXIS));
                }
                itemTreePanel.add(qrPanel);

                //======== panel4 ========
                {
                    panel4.setMaximumSize(new Dimension(2147483647, 100));
                    panel4.setLayout(new BorderLayout());

                    //---- copyQRButton ----
                    copyQRButton.setText("\u041a\u043e\u043f\u0438\u0440\u043e\u0432\u0430\u0442\u044c");
                    copyQRButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            copyQRButtonActionPerformed(e);
                        }
                    });
                    panel4.add(copyQRButton, BorderLayout.SOUTH);
                    panel4.add(textQR, BorderLayout.CENTER);
                }
                itemTreePanel.add(panel4);
            }
            mainPanel.add(itemTreePanel);

            //======== mainItemPanel ========
            {
                mainItemPanel.setAutoscrolls(true);
                mainItemPanel.setMinimumSize(new Dimension(550, 31));
                mainItemPanel.setPreferredSize(new Dimension(550, 540));
                mainItemPanel.setLayout(new BoxLayout(mainItemPanel, BoxLayout.Y_AXIS));

                //======== contentTab ========
                {
                    contentTab.setBorder(new EmptyBorder(5, 5, 5, 5));
                    contentTab.setAutoscrolls(true);
                    contentTab.setPreferredSize(new Dimension(577, 540));

                    //======== itemPanel ========
                    {
                        itemPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
                        itemPanel.setPreferredSize(new Dimension(540, 500));
                        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));

                        //======== panel1 ========
                        {
                            panel1.setBorder(new TitledBorder(null, "\u041a\u0440\u0430\u0442\u043a\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black));
                            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                            //---- itemNameTextField ----
                            itemNameTextField.setBorder(null);
                            itemNameTextField.setText("\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u044d\u043a\u0441\u043f\u043e\u043d\u0430\u0442");
                            itemNameTextField.setMaximumSize(new Dimension(2147483647, 37));
                            itemNameTextField.setFont(itemNameTextField.getFont().deriveFont(itemNameTextField.getFont().getSize() + 3f));
                            itemNameTextField.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    itemNameTextFieldCaretUpdate(e);
                                }
                            });
                            panel1.add(itemNameTextField);
                        }
                        itemPanel.add(panel1);

                        //======== panel2 ========
                        {
                            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                            //======== categoryPanel ========
                            {
                                categoryPanel.setBorder(new CompoundBorder(
                                        new TitledBorder(null, "\u041a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044f", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black),
                                        new EmptyBorder(5, 5, 5, 5)));
                                categoryPanel.setVisible(false);
                                categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));

                                //---- categoryComboBox ----
                                categoryComboBox.setBorder(null);
                                categoryComboBox.setMaximumSize(new Dimension(32767, 20));
                                categoryComboBox.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        categoryComboBoxItemStateChanged(e);
                                    }
                                });
                                categoryPanel.add(categoryComboBox);
                            }
                            panel2.add(categoryPanel);
                        }
                        itemPanel.add(panel2);

                        //======== toolBar1 ========
                        {
                            toolBar1.setFloatable(false);
                            toolBar1.setRollover(true);
                            toolBar1.setAutoscrolls(true);

                            //---- fontMenu ----
                            fontMenu.setText("\u0428\u0440\u0438\u0444\u0442");
                            fontMenu.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    fontMenuActionPerformed(e);
                                }
                            });
                            toolBar1.add(fontMenu);

                            //---- stringShifting ----
                            stringShifting.setText("\u041f\u0435\u0440\u0435\u0432\u043e\u0434 \u0441\u0442\u0440\u043e\u043a\u0438");
                            stringShifting.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    stringShiftingActionPerformed(e);
                                }
                            });
                            toolBar1.add(stringShifting);

                            //---- paragraph ----
                            paragraph.setText("\u0410\u0431\u0437\u0430\u0446");
                            paragraph.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    paragraphActionPerformed(e);
                                }
                            });
                            toolBar1.add(paragraph);

                            //---- insertImg ----
                            insertImg.setText("\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430");
                            insertImg.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    insertImgActionPerformed(e);
                                }
                            });
                            toolBar1.add(insertImg);

                            //---- aligment ----
                            aligment.setText("\u0412\u044b\u0440\u0430\u0432\u043d\u0438\u0432\u0430\u043d\u0438\u0435");
                            aligment.setVisible(false);
                            aligment.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    aligmentActionPerformed(e);
                                }
                            });
                            toolBar1.add(aligment);

                            //---- button3 ----
                            button3.setIcon(new ImageIcon("C:\\Users\\Anatoliy\\Documents\\MusemGuideSystem\\MusemGuideSystem\\ContentManagerApp\\src\\align_left.png"));
                            button3.setActionCommand("align-type-left");
                            button3.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    aligmentActionPerformed(e);
                                }
                            });
                            toolBar1.add(button3);

                            //---- button1 ----
                            button1.setIcon(new ImageIcon("C:\\Users\\Anatoliy\\Documents\\MusemGuideSystem\\MusemGuideSystem\\ContentManagerApp\\src\\align_center.png"));
                            button1.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    aligmentActionPerformed(e);
                                }
                            });
                            toolBar1.add(button1);

                            //---- button2 ----
                            button2.setIcon(new ImageIcon("C:\\Users\\Anatoliy\\Documents\\MusemGuideSystem\\MusemGuideSystem\\ContentManagerApp\\src\\align_just.png"));
                            button2.setActionCommand("align-type-just");
                            button2.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    aligmentActionPerformed(e);
                                }
                            });
                            toolBar1.add(button2);

                            //---- button4 ----
                            button4.setText("\u041e\u0442\u043c\u0435\u043d\u0430 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f");
                            button4.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    button4ActionPerformed(e);
                                }
                            });
                            toolBar1.add(button4);
                        }
                        itemPanel.add(toolBar1);

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setBorder(new TitledBorder(new SoftBevelBorder(SoftBevelBorder.RAISED, Color.black, null, null, null), "\u041f\u043e\u0434\u0440\u043e\u0431\u043d\u043e\u0435 \u043e\u043f\u0438\u0441\u0430\u043d\u0435\u0438", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black));

                            //---- editorPane1 ----
                            editorPane1.setContentType("text/html");
                            editorPane1.setText(" ");
                            editorPane1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            editorPane1.setDoubleBuffered(true);
                            editorPane1.setDragEnabled(true);
                            editorPane1.setDropMode(DropMode.INSERT);
                            editorPane1.setEnabled(false);
                            editorPane1.addPropertyChangeListener("text", new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent e) {
                                    editorPane1PropertyChange(e);
                                }
                            });
                            editorPane1.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    editorPane1CaretUpdate(e);
                                }
                            });
                            editorPane1.addKeyListener(new KeyAdapter() {
                                @Override
                                public void keyReleased(KeyEvent e) {
                                    editorPane1KeyReleased(e);
                                }
                            });
                            scrollPane1.setViewportView(editorPane1);
                        }
                        itemPanel.add(scrollPane1);

                        //======== attachPanel ========
                        {
                            attachPanel.setBorder(new CompoundBorder(
                                    new TitledBorder(null, "\u0412\u043b\u043e\u0436\u0435\u043d\u0438\u044f", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black),
                                    new EmptyBorder(5, 5, 5, 5)));
                            attachPanel.setPreferredSize(new Dimension(300, 180));
                            attachPanel.setLayout(new BoxLayout(attachPanel, BoxLayout.Y_AXIS));

                            //======== scrollPane3 ========
                            {

                                //---- attachmentTabel ----
                                attachmentTabel.setModel(new DefaultTableModel(
                                        new Object[][]{
                                                {null, null, null, null, null},
                                        },
                                        new String[]{
                                                "\u0418\u043c\u044f \u0444\u0430\u0439\u043b\u0430", "\u041a\u0440\u0430\u0442\u043a\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435", "\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435", "\u0422\u0438\u043f", "\u041f\u0440\u0438\u043c\u0435\u0447\u0430\u043d\u0438\u044f"
                                        }
                                ) {
                                    Class<?>[] columnTypes = new Class<?>[]{
                                            String.class, String.class, Object.class, String.class, String.class
                                    };
                                    boolean[] columnEditable = new boolean[]{
                                            false, true, true, false, true
                                    };
                                    @Override
                                    public Class<?> getColumnClass(int columnIndex) {
                                        return columnTypes[columnIndex];
                                    }
                                    @Override
                                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                                        return columnEditable[columnIndex];
                                    }
                                });
                                attachmentTabel.addPropertyChangeListener(new PropertyChangeListener() {
                                    @Override
                                    public void propertyChange(PropertyChangeEvent e) {
                                        attachmentTabelPropertyChange(e);
                                    }
                                });
                                scrollPane3.setViewportView(attachmentTabel);
                            }
                            attachPanel.add(scrollPane3);

                            //======== panel3 ========
                            {
                                panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                                //---- delFIileButton ----
                                delFIileButton.setText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0444\u0430\u0439\u043b");
                                delFIileButton.setHorizontalTextPosition(SwingConstants.CENTER);
                                delFIileButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        delFileButtonActionPerformed(e);
                                    }
                                });
                                panel3.add(delFIileButton);

                                //---- addFileButton ----
                                addFileButton.setText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0444\u0430\u0439\u043b");
                                addFileButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        addFileButtonActionPerformed(e);
                                    }
                                });
                                panel3.add(addFileButton);
                            }
                            attachPanel.add(panel3);
                        }
                        itemPanel.add(attachPanel);
                    }
                    contentTab.setViewportView(itemPanel);
                }
                mainItemPanel.add(contentTab);
            }
            mainPanel.add(mainItemPanel);
        }
        contentPane.add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - gfic -snt-slt
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JPanel menuBarPanel;
    private JMenuBar menuBar;
    private JMenu menu1;
    private JMenuItem addINewItem;
    private JMenuItem cloneMenuItem;
    private JMenuItem saveMenu;
    private JMenuItem addCategoryItemMenu;
    private JMenuItem exitMenuItem;
    private JMenu feedbackMenu;
    private JMenu aboutMenu;
    private JPanel mainPanel;
    private JPanel itemTreePanel;
    private JScrollPane scrollPane2;
    private JTree itemTree;
    private JPanel qrPanel;
    private JPanel panel4;
    private JButton copyQRButton;
    private JTextField textQR;
    private JPanel mainItemPanel;
    private JScrollPane contentTab;
    private JPanel itemPanel;
    private JPanel panel1;
    private JTextField itemNameTextField;
    private JPanel panel2;
    private JPanel categoryPanel;
    private JComboBox categoryComboBox;
    private JToolBar toolBar1;
    private JButton fontMenu;
    private JButton stringShifting;
    private JButton paragraph;
    private JButton insertImg;
    private JButton aligment;
    private JButton button3;
    private JButton button1;
    private JButton button2;
    private JButton button4;
    private JScrollPane scrollPane1;
    private JEditorPane editorPane1;
    private JPanel attachPanel;
    private JScrollPane scrollPane3;
    private JTable attachmentTabel;
    private JPanel panel3;
    private JButton delFIileButton;
    private JButton addFileButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
