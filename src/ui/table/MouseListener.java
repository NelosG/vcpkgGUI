package ui.table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter {

    private JTable table;

    public MouseListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mousePressed( MouseEvent event) {
        Point point = event.getPoint();
        int currentRow = table.rowAtPoint(point);
        if(currentRow != -1){
            table.addRowSelectionInterval(currentRow, currentRow);
        } else {
            table.clearSelection();
        }
    }
}