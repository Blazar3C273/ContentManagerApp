package UI;

import files.Item;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by Tolik on 22.12.2014.
 */
public class MyTranferHandler extends TransferHandler {
    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        System.out.println("exportAsDrag()");
        super.exportAsDrag(comp, e, action);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        System.out.println("importData(comp,t)");
        return super.importData(comp, t);
    }

    @Override
    public boolean importData(TransferSupport support) {
        //чтобы переместить Item нужно:
        //1-Изменить item.parent на новый.
        //2-добавить лист в новую категорию
        //3-удалить лист из старой категории
        Transferable transferable = support.getTransferable();
        System.out.println("importData(support)");
        JTree jTree = (JTree) support.getComponent();
        synchronized (jTree.getModel().getRoot()) {
            try {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) transferable.getTransferData(new DataFlavor(DefaultMutableTreeNode.class, "application/x-www-form-urlencoded; class=<javax.swing.tree.DefaultMutableTreeNode>;"));
                JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
                if (dropLocation == null) {
                    return false;
                }
                TreePath path = dropLocation.getPath();

                DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode) treeNode.getParent();
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path.getLastPathComponent();

                if (oldParent.getParent() == null && parent.getParent() == null) {
                    return false;
                }
                if (oldParent.getUserObject().toString().equals(parent.getUserObject().toString()))
                    return false;

                Item item = (Item) treeNode.getUserObject();
                item.setParent((path.getLastPathComponent()).toString());
                DefaultTreeModel defaultTreeModel = (DefaultTreeModel) jTree.getModel();
                if (oldParent.getParent() == null) {
                    //заменить анологичный parent
                    oldParent.remove(treeNode);
                    parent.add(treeNode);
                    Enumeration children = oldParent.children();

                    while (children.hasMoreElements()) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
                        if (node.getUserObject().toString().equals(parent.getUserObject().toString()))
                            oldParent.remove(node);
                        oldParent.add(parent);
                    }
                    //сделать рутом oldParent
                    defaultTreeModel.setRoot(oldParent);
                } else {
                    parent.add(treeNode);

                    Enumeration someCilds = ((DefaultMutableTreeNode) defaultTreeModel.getRoot()).children();
                    while (someCilds.hasMoreElements()) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) someCilds.nextElement();
                        if (node.getUserObject().toString().equals(oldParent.getUserObject().toString())) {
                            ((DefaultMutableTreeNode) defaultTreeModel.getRoot()).remove(node);
                        }
                    }
                    ((DefaultMutableTreeNode) defaultTreeModel.getRoot()).add(oldParent);
                }

                defaultTreeModel.nodeChanged((DefaultMutableTreeNode) defaultTreeModel.getRoot());


                return false;
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        //решить можно ли вставить
        System.out.println("canImport(support)");
        JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
        if (dropLocation == null) {
            return false;
        }
        TreePath path = dropLocation.getPath();
        //проверяем категория ли это. Если можно добавлять дочерние элементы-значит категория.
        return ((DefaultMutableTreeNode) path.getLastPathComponent()).getAllowsChildren();
    }


    @Override
    public int getSourceActions(JComponent c) {
        System.out.println("getSourceActions(c)");
        JTree tree = (JTree) c;
        DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (mutableTreeNode == null || mutableTreeNode.getAllowsChildren())
            return NONE;
        else return MOVE;//пока только перемещение. TODO сделать копирование
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        tree.getSelectionPath().toString();
        System.out.println("createTransferable(c)");
        return new MyTransferable((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
    }


    private class MyTransferable implements Transferable {
        DefaultMutableTreeNode treeNode;

        public MyTransferable(DefaultMutableTreeNode _treeNode) {
            this.treeNode = _treeNode;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            System.out.println("getTransferData()");

            DataFlavor[] dataFlavors = new DataFlavor[0];

            return dataFlavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            System.out.println("isDataFlavorsSupported()");
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            try {
                if (flavor.equals(new DataFlavor(DefaultMutableTreeNode.class, "application/x-www-form-urlencoded; class=<javax.swing.tree.DefaultMutableTreeNode>;")))
                    return treeNode;
                else throw new UnsupportedFlavorException(flavor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
