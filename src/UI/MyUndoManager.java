package UI;

import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Created by Anatoliy on 05.04.2015.
 */
public class MyUndoManager extends UndoManager {

//    @Override
//    public void undoableEditHappened(UndoableEditEvent e) {
//        System.out.println(e.getSource() +"isSignificant: " + Boolean.toString(e.getEdit().isSignificant()));
//        //System.out.println(editorPane.getText());
//        System.out.println(e.getEdit().getPresentationName());
//        System.out.println(e.getEdit().getUndoPresentationName());
//        super.undoableEditHappened(e);
//
//    }

    @Override
    public synchronized void undo() throws CannotUndoException {
        //Костыль. Нужен для корректной отмены. Вызыван  тем, что editPane.setText() производит не одно изменение а сразу 5.
        boolean delFlag = editToBeUndone().getPresentationName().equals("deletion");
        super.undo();
        if (editToBeUndone().getPresentationName().equals("style change") && delFlag)
            for (int i = 0; i < 4; i++) {
                super.undo();
            }

    }
}
