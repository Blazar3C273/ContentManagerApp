/*
 * Created by JFormDesigner on Tue Dec 09 23:12:14 AST 2014
 */

package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.util.Stack;

/**
 * @author Alan Evalis
 */
public class Font extends JDialog {


    public Font(Frame owner) {
        super(owner);
        initComponents();
    }

    public Font(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void okButtonActionPerformed(ActionEvent e) {
        //getAll
        JEditorPane editorPane1 = ((MainForm) getOwner()).getEditorPane1();
        clearFormatting(editorPane1);

        if (underlineButton.isSelected()) {
            new HTMLEditorKit.UnderlineAction().actionPerformed(new ActionEvent(contentPanel, 0, ""));
        }

        String index = fontTydes.getSelectedValue();

        switch (index) {
            case "Жирный":
                new HTMLEditorKit.BoldAction().actionPerformed(e);
                break;
            case "Курсив":
                new HTMLEditorKit.ItalicAction().actionPerformed(e);
                break;
            case "Жирный курсив":
                new HTMLEditorKit.BoldAction().actionPerformed(e);
                new HTMLEditorKit.ItalicAction().actionPerformed(e);
                break;
        }

        // TODO setting size

        String tag = null;
        switch (fontSize.getSelectedValue()) {
            case "Маленький":
                tag = "small";
                break;
            case "Большой":
                tag = "big";
                break;
        }
        if (tag != null) {
            int start, end;
            start = editorPane1.getSelectionStart();
            end = editorPane1.getSelectionEnd();
            String selectedText = editorPane1.getSelectedText();
            String newContent = "<" + tag + ">" + selectedText + "</" + tag + ">";
            editorPane1.replaceSelection("");
            if (tag.equals("big")) {
                new HTMLEditorKit.InsertHTMLTextAction("Big text", newContent, HTML.Tag.BODY, HTML.Tag.BIG).actionPerformed(new ActionEvent(contentPanel, 0, ""));
            } else {
                new HTMLEditorKit.InsertHTMLTextAction("Small text", newContent, HTML.Tag.BODY, HTML.Tag.SMALL).actionPerformed(new ActionEvent(contentPanel, 0, ""));
            }

            editorPane1.setText(editorPane1.getText());
            editorPane1.setSelectionStart(start);
            editorPane1.setSelectionEnd(end);

            if (underlineButton.isSelected()) {
                new HTMLEditorKit.UnderlineAction().actionPerformed(new ActionEvent(contentPanel, 0, ""));
            }
        }

        new HTMLEditorKit.ForegroundAction(String.valueOf(colorChooser1.getColor().getRGB()), colorChooser1.getColor()).actionPerformed(new ActionEvent(contentPanel, 0, ""));


        PropertyChangeListener[] listener = ((MainForm) getOwner()).getEditorPane1().getPropertyChangeListeners();
        for (PropertyChangeListener changeListener : listener) {
            if (changeListener instanceof PropertyChangeListenerProxy)
                if (((PropertyChangeListenerProxy) changeListener).getPropertyName().equals("text")) {
                    changeListener.propertyChange(null);
                }
        }


        dispose();
    }

    private void clearFormatting(JEditorPane editorPane1) {
        int start, end;
        start = editorPane1.getSelectionStart();
        end = editorPane1.getSelectionEnd();
        editorPane1.replaceSelection(editorPane1.getSelectedText());
        editorPane1.setSelectionStart(start);
        editorPane1.setSelectionEnd(end);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Some Fuu
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        panel3 = new JPanel();
        scrollPane1 = new JScrollPane();
        fontTydes = new JList<>();
        scrollPane3 = new JScrollPane();
        fontSize = new JList<>();
        panel4 = new JPanel();
        underlineButton = new JToggleButton();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        colorChooser1 = new JColorChooser();

        //======== this ========
        setResizable(false);
        setMinimumSize(new Dimension(400, 300));
        setTitle("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u0448\u0440\u0438\u0444\u0442\u0430");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            // JFormDesigner evaluation mark
            dialogPane.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), dialogPane.getBorder()));
            dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });

            dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.Y_AXIS));

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

                //======== panel1 ========
                {
                    panel1.setBorder(null);
                    panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

                    //======== panel3 ========
                    {
                        panel3.setBorder(new EmptyBorder(0, 0, 0, 5));
                        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));

                        //======== scrollPane1 ========
                        {

                            //---- fontTydes ----
                            fontTydes.setBorder(new TitledBorder(null, "\u041d\u0430\u0447\u0435\u0440\u0442\u0430\u043d\u0438\u0435", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black));
                            fontTydes.setModel(new AbstractListModel<String>() {
                                String[] values = {
                                        "\u041e\u0431\u044b\u0447\u043d\u044b\u0439",
                                        "\u0416\u0438\u0440\u043d\u044b\u0439",
                                        "\u041a\u0443\u0440\u0441\u0438\u0432",
                                        "\u0416\u0438\u0440\u043d\u044b\u0439 \u043a\u0443\u0440\u0441\u0438\u0432"
                                };

                                @Override
                                public int getSize() {
                                    return values.length;
                                }

                                @Override
                                public String getElementAt(int i) {
                                    return values[i];
                                }
                            });
                            fontTydes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            fontTydes.setSelectedIndex(0);
                            scrollPane1.setViewportView(fontTydes);
                        }
                        panel3.add(scrollPane1);

                        //======== scrollPane3 ========
                        {
                            scrollPane3.setMinimumSize(new Dimension(70, 71));
                            scrollPane3.setPreferredSize(new Dimension(80, 100));

                            //---- fontSize ----
                            fontSize.setBorder(new TitledBorder(null, "\u0420\u0430\u0437\u043c\u0435\u0440", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black));
                            fontSize.setModel(new AbstractListModel<String>() {
                                String[] values = {
                                        "\u041d\u043e\u0440\u043c\u0430\u043b\u044c\u043d\u044b\u0439",
                                        "\u0411\u043e\u043b\u044c\u0448\u043e\u0439",
                                        "\u041c\u0430\u043b\u0435\u043d\u044c\u043a\u0438\u0439"
                                };

                                @Override
                                public int getSize() {
                                    return values.length;
                                }

                                @Override
                                public String getElementAt(int i) {
                                    return values[i];
                                }
                            });
                            fontSize.setPreferredSize(new Dimension(70, 50));
                            fontSize.setMinimumSize(new Dimension(70, 50));
                            fontSize.setMaximumSize(new Dimension(70, 119));
                            fontSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            fontSize.setSelectedIndex(0);
                            scrollPane3.setViewportView(fontSize);
                        }
                        panel3.add(scrollPane3);

                        //======== panel4 ========
                        {
                            panel4.setMinimumSize(new Dimension(5, 23));
                            panel4.setPreferredSize(new Dimension(5, 23));
                            panel4.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[]{116, 0};
                            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[]{15, 0};
                            ((GridBagLayout) panel4.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
                            ((GridBagLayout) panel4.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

                            //---- underlineButton ----
                            underlineButton.setText("\u041f\u043e\u0434\u0447\u0435\u0440\u043a\u0438\u0432\u0430\u043d\u0438\u0435");
                            underlineButton.setPreferredSize(new Dimension(50, 23));
                            panel4.add(underlineButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        panel3.add(panel4);

                        //======== buttonBar ========
                        {
                            buttonBar.setBorder(null);
                            buttonBar.setLayout(new BoxLayout(buttonBar, BoxLayout.LINE_AXIS));

                            //---- okButton ----
                            okButton.setText("OK");
                            okButton.setMaximumSize(new Dimension(65, 23));
                            okButton.setMinimumSize(new Dimension(65, 23));
                            okButton.setPreferredSize(new Dimension(65, 23));
                            okButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    okButtonActionPerformed(e);
                                }
                            });
                            buttonBar.add(okButton);

                            //---- cancelButton ----
                            cancelButton.setText("Cancel");
                            cancelButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    cancelButtonActionPerformed(e);
                                }
                            });
                            buttonBar.add(cancelButton);
                        }
                        panel3.add(buttonBar);
                    }
                    panel1.add(panel3);

                    //---- colorChooser1 ----
                    colorChooser1.setColor(Color.black);
                    colorChooser1.setBorder(new LineBorder(Color.lightGray, 1, true));
                    panel1.add(colorChooser1);
                }
                contentPanel.add(panel1);
            }
            dialogPane.add(contentPanel);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(805, 425);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Some Fuu
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JPanel panel3;
    private JScrollPane scrollPane1;
    private JList<String> fontTydes;
    private JScrollPane scrollPane3;
    private JList<String> fontSize;
    private JPanel panel4;
    private JToggleButton underlineButton;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    private JColorChooser colorChooser1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
