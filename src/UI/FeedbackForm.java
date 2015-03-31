/*
 * Created by JFormDesigner on Sun Feb 22 21:59:05 MSK 2015
 */

package UI;

import network.NetworkConnection;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Someth Sanders
 */
public class FeedbackForm extends JFrame {
    ArrayList<FeedbackNote> notes = null;

    public FeedbackForm() {
        initComponents();
        try {
            panel1.setBorder(new TitledBorder(""));
        } catch (RuntimeException e) {
        }
        //fill feedback list
        DefaultListModel<String> model = new DefaultListModel<String>();
        list1.setModel(model);
        try {
            notes = NetworkConnection.getFeedbacks();
        } catch (NetworkConnection.DataBaseError dataBaseError) {
            new Dialog(this, "Ошибка при обращении к базе данных.").setVisible(true);
        }

        if (notes != null) {
            for (FeedbackNote note : notes) {
                model.addElement(note.getItemName());
            }
            list1.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {

                    textPane1.setText(notes.get(((JList) e.getSource()).getSelectedIndex()).toString());
                }
            });
        }
        //get list of all records
        //fill list model
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Anton Anton
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        panel1 = new JPanel();
        scrollPane2 = new JScrollPane();
        textPane1 = new JTextPane();
        checkBox1 = new JCheckBox();

        //======== this ========
        setMinimumSize(new Dimension(850, 630));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== scrollPane1 ========
        {
            scrollPane1.setMaximumSize(new Dimension(200, 32767));
            scrollPane1.setBorder(new TitledBorder(null, "\u0421\u043f\u0438\u0441\u043e\u043a \u043e\u0442\u0437\u044b\u0432\u043e\u0432", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.black));

            //---- list1 ----
            list1.setMaximumSize(new Dimension(250, 300));
            list1.setVisibleRowCount(10);
            list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list1.setMinimumSize(new Dimension(250, 300));
            list1.setPreferredSize(new Dimension(250, 160));
            scrollPane1.setViewportView(list1);
        }
        contentPane.add(scrollPane1);

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("\u041e\u0442\u0437\u044b\u0432"));

            // JFormDesigner evaluation mark
            panel1.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), panel1.getBorder()));
            panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });

            panel1.setLayout(new BorderLayout());

            //======== scrollPane2 ========
            {
                scrollPane2.setPreferredSize(new Dimension(8, 150));

                //---- textPane1 ----
                textPane1.setBorder(new TitledBorder("\u0422\u0435\u043a\u0441\u0442 \u043e\u0442\u0437\u044b\u0432\u0430"));
                textPane1.setText("\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u043e\u0442\u0437\u044b\u0432");
                scrollPane2.setViewportView(textPane1);
            }
            panel1.add(scrollPane2, BorderLayout.CENTER);

            //---- checkBox1 ----
            checkBox1.setText("\u041e\u0442\u0437\u044b\u0432 \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u043d?");
            checkBox1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            checkBox1.setHorizontalTextPosition(SwingConstants.LEADING);
            panel1.add(checkBox1, BorderLayout.SOUTH);
        }
        contentPane.add(panel1);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Anton Anton
    private JScrollPane scrollPane1;
    private JList list1;
    private JPanel panel1;
    private JScrollPane scrollPane2;
    private JTextPane textPane1;
    private JCheckBox checkBox1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
