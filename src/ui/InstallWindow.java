package ui;

import core.LibWrapper;
import ui.table.JTableContainer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class InstallWindow extends JDialog {
    private JTextField forInstall;
    private final LibWrapper libWrapper;
    private JPanel InstallPanel;
    private JButton installButton;
    private JButton exitButton;
    private JTable table;
    private JTableContainer tableContainer;
    private final Consumer<List<String>> consumer;

    InstallWindow(LibWrapper libWrapper, Consumer<List<String>> cons){
        this.libWrapper = libWrapper;
        consumer = cons;
        setContentPane(InstallPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setTitle("Installation");

        installButton.addActionListener(new installButtonEventListener());
        exitButton.addActionListener(new exitButtonEventListener());
        try {
            updateList();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createUIComponents() {
        tableContainer = new JTableContainer(consumer);
        table = tableContainer.getTable();
        forInstall = tableContainer.getTextField();
    }

    class installButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            exit();
        }
    }

    class exitButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            dispose();
        }
    }

    private void updateList() throws IOException, InterruptedException {
        tableContainer.clear();
        tableContainer.addAll(libWrapper.allPackagesList());
    }

    private void exit() {
        setModalityType(ModalityType.MODELESS);
        setVisible(false);
        dispose();
    }
}
