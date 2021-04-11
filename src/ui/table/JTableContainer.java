package ui.table;

import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JTableContainer implements ActionListener {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JMenuItem menuItemDelete;
    private final JTextField searchField;
    private final Consumer<List<String>> Action;
    private final TableRowSorter<TableModel> rowSorter;

    public JTableContainer(Consumer<List<String>> action, String name) {

        this.Action = action;

        String[] columnNames = new String[]{"Package", "Version", "Description"};

        tableModel = new MyTableModel(new String[0][0], columnNames);
        table = new JTable(tableModel);
        searchField = new JTextField();
        JPopupMenu popupMenu = new JPopupMenu();
        menuItemDelete = new JMenuItem(name);

        menuItemDelete.addActionListener(this);

        popupMenu.add(menuItemDelete);

        table.setComponentPopupMenu(popupMenu);


        table.setShowGrid(true);

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        table.getColumnModel().getColumn(0).setMinWidth(200);
        table.getColumnModel().getColumn(0).setMaxWidth(200);

        table.getColumnModel().getColumn(1).setMinWidth(120);
        table.getColumnModel().getColumn(1).setMaxWidth(120);

        table.getColumnModel().getColumn(2).sizeWidthToFit();

        table.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new CellRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new CellRenderer());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        table.addMouseListener(new MouseListener(table));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setFocusable(false);
        rowSorter = new TableRowSorter<>(table.getModel());
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            private final StringBuilder sb = new StringBuilder();
            private final String exclude = "\\(){}[]&|";

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = searchField.getText();
                Character c = text.charAt(text.length() - 1);
                if (!exclude.contains(c.toString())) {
                    sb.append(c);
                    setFilter();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!exclude.contains(String.valueOf(sb.charAt(sb.length() - 1)))) {
                    sb.deleteCharAt(sb.length() - 1);
                    setFilter();
                }
            }

            private void setFilter() {
                if (sb.length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + sb, 0));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported.");
            }

        });
        table.setRowSorter(rowSorter);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemDelete) {
            runActionOnSelectedRow();
        }
    }

    public void runActionOnSelectedRow() {
        int[] selectedRow = table.getSelectedRows();
        Action.accept(
                Arrays.stream(selectedRow)
                        .boxed()
                        .map(i -> table.getValueAt(i, 0).toString())
                        .collect(Collectors.toList())
        );
    }

    public void addAll(ArrayList<String[]> installedList) {
        installedList.forEach(tableModel::addRow);
    }

    public JTable getTable() {
        return table;
    }

    public JTextField getTextField() {
        return searchField;
    }

    public void clear() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
    }

    private static class CellRenderer extends JTextPane implements TableCellRenderer {


        private CellRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (table == null) {
                return this;
            }

            Color fg = null;
            Color bg = null;

            JTable.DropLocation dropLocation = table.getDropLocation();
            if (dropLocation != null
                    && !dropLocation.isInsertRow()
                    && !dropLocation.isInsertColumn()
                    && dropLocation.getRow() == row
                    && dropLocation.getColumn() == column) {

                fg = DefaultLookup.getColor(this, ui, "Table.dropCellForeground");
                bg = DefaultLookup.getColor(this, ui, "Table.dropCellBackground");

                isSelected = true;
            }

            if (isSelected) {
                super.setForeground(fg == null ? table.getSelectionForeground()
                        : fg);
                super.setBackground(bg == null ? table.getSelectionBackground()
                        : bg);
            } else {
                Color background = table.getBackground();
                if (background == null || background instanceof javax.swing.plaf.UIResource) {
                    Color alternateColor = DefaultLookup.getColor(this, ui, "Table.alternateRowColor");
                    if (alternateColor != null && row % 2 != 0) {
                        background = alternateColor;
                    }
                }
                super.setForeground(table.getForeground());
                super.setBackground(background);
            }

            setFont(table.getFont());

            if (hasFocus) {
                Border border = null;
                if (isSelected) {
                    border = DefaultLookup.getBorder(this, ui, "Table.focusSelectedCellHighlightBorder");
                }
                if (border == null) {
                    border = DefaultLookup.getBorder(this, ui, "Table.focusCellHighlightBorder");
                }
                setBorder(border);

                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = DefaultLookup.getColor(this, ui, "Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = DefaultLookup.getColor(this, ui, "Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(new EmptyBorder(1, 1, 1, 1));
            }

            String data = value == null ? "" : value.toString();
            int lineWidth = this.getFontMetrics(this.getFont()).stringWidth(data);
            int lineHeight = this.getFontMetrics(this.getFont()).getHeight();
            int rowWidth = table.getCellRect(row, column, true).width;

            int newRowHeight = ((lineWidth / rowWidth) * (lineHeight)) + lineHeight * 2;
            if (table.getRowHeight(row) != newRowHeight) {
                if (lineWidth / rowWidth < 5) {
                    table.setRowHeight(row, newRowHeight);
                }
            }
            this.setText(data);
            return this;
        }
    }

    private static class MyTableModel extends DefaultTableModel {
        public MyTableModel(String[][] strings, String[] columnNames) {
            super(strings, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
