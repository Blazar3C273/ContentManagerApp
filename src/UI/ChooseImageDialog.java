/*
 * Created by JFormDesigner on Mon Dec 22 01:34:08 AST 2014
 */

package UI;

import files.Item;
import files.PictureFile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Some Fuu
 */
public class ChooseImageDialog extends JDialog {

    Item item;
    int cursorPos;

    public ChooseImageDialog(Frame owner, Item _item, int cursorPosition) {
        super(owner);
        this.cursorPos = cursorPosition;
        this.item = _item;
        initComponents();
        try {
            dialogPane.setBorder(new TitledBorder(""));
        } catch (RuntimeException e) {
        }

        AbstractListModel<PictureFile> alm = new AbstractListModel<PictureFile>() {
            @Override
            public int getSize() {
                if (item == null || item.getPictureFiles() == null) {
                    return 0;
                } else return item.getPictureFiles().size();
            }

            @Override
            public PictureFile getElementAt(int index) {
                if (item == null || item.getPictureFiles() == null) {
                    return null;
                } else return item.getPictureFiles().get(index);
            }
        };
        list1.setModel(alm);
    }

    private void okButtonActionPerformed(ActionEvent e) {
        PictureFile selectedValue = (PictureFile) list1.getSelectedValue();
        if (selectedValue == null) {
            dispose();
            return;
        }
        String href = "view:" + item.get_id() + "/" + selectedValue.getFilename();
        String html = "<a href=\"" + href + "\"><img align=\"center\" src=\"" + selectedValue.getFilename() + "\" width=\"100%\" alt=\"" + selectedValue.getShortName() + "\" >";
        String div = "<div align=\"center\">" + html + "</div>";
        html = div;
        ((MainForm) getOwner()).getEditorPane1().setSelectionStart(cursorPos);
        ((MainForm) getOwner()).getEditorPane1().setSelectionEnd(cursorPos);
        new HTMLEditorKit.InsertHTMLTextAction("insert img", html, HTML.Tag.BODY, HTML.Tag.DIV).actionPerformed(e);
        //((MainForm) getOwner()).getEditorPane1().setText(((MainForm) getOwner()).getEditorPane1().getText());
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Someth Sanders
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
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

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

                //======== scrollPane1 ========
                {

                    //---- list1 ----
                    list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    scrollPane1.setViewportView(list1);
                }
                contentPanel.add(scrollPane1);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Someth Sanders
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane scrollPane1;
    private JList list1;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
