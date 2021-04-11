package ui.install;

import core.LibWrapper;
import ui.table.JTableContainer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class InstallWindow extends JDialog {
    private final LibWrapper libWrapper;
    private final Consumer<List<String>> consumer;
    private JTextField forInstall;
    private JPanel InstallPanel;
    private JButton installButton;
    private JTable table;
    private JTableContainer tableContainer;

    public InstallWindow(LibWrapper libWrapper, Consumer<List<String>> cons) {
        this.libWrapper = libWrapper;
        consumer = cons;
        setContentPane(InstallPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setTitle("Installation");
        updateList();
        InstallPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                table.clearSelection();
            }
        });

        installButton.addActionListener(new installButtonEventListener());
    }

    private void createUIComponents() {
        tableContainer = new JTableContainer(consumer, "Install package(s)");
        table = tableContainer.getTable();
        forInstall = tableContainer.getTextField();
    }

    private void updateList() {
        tableContainer.clear();
        try {
            tableContainer.addAll(libWrapper.allPackagesList());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    class installButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            tableContainer.runActionOnSelectedRow();
        }
    }
}
