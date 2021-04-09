package ui;

import core.LibWrapper;
import ui.table.JTableContainer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
    private final LibWrapper libWrapper;
    private JTableContainer tableContainer;
    private JPanel root;
    private JButton installPackageButton;
    private JLabel version;
    private JTable table;
    private JButton updateButton;

    public MainWindow(LibWrapper libWrapper) {
        super("vcpkgGUI");
        this.libWrapper = libWrapper;
        setContentPane(root);
        setBounds(0, 0, 1080, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        root.addMouseListener(new MouseListener());
        installPackageButton.addActionListener(new installButtonEventListener());
        updateButton.addActionListener(new updateButtonEventListener());
        try {
            version.setText("Version of vcpkg: " + libWrapper.version());
            updateList();
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }

    private static String createDeleteMessage(List<String> listForDelete) {
        StringBuilder sb = new StringBuilder();
        int maxPackagesForMessage = 4;
        if (listForDelete.size() > 1) {
            listForDelete.subList(0, Math.min(maxPackagesForMessage, listForDelete.size() - 1))
                    .forEach(s -> sb.append(s).append(", \n"));
        }
        if (listForDelete.size() > maxPackagesForMessage) {
            sb.append(listForDelete.get(maxPackagesForMessage))
                    .append("...\nand ")
                    .append(listForDelete.size() - maxPackagesForMessage)
                    .append(" more");
        } else {
            sb.append(listForDelete.get(listForDelete.size() - 1));
        }
        return sb.toString();
    }

    private void updateList() throws IOException, InterruptedException {
        tableContainer.clear();
        tableContainer.addAll(libWrapper.installedList());
    }

    private void createUIComponents() {
        tableContainer = new JTableContainer(this::deletionFunc);
        table = tableContainer.getTable();
    }

    private void deletionFunc(List<String> listForDelete) {
        if (listForDelete.isEmpty()) {
            JOptionPane.showMessageDialog(null, """
                            No package selected to remove.
                            Please select one or more packages.""", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            if (JOptionPane.showConfirmDialog(null,
                    "Delete: \n" + createDeleteMessage(listForDelete) + "?", "Deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)

                    == JOptionPane.YES_OPTION) {
                try {
                    for (String s : listForDelete) {
                        if (libWrapper.removeLib(s, false) != null) {
                            updateList();
                        } else {
                            ArrayList<String> additionalList = Arrays.stream(libWrapper.output.toString().split("\n"))
                                    .filter(str -> str.contains("* "))
                                    .map(str -> {
                                        String[] mass = str.split(" ");
                                        return mass[mass.length - 1];
                                    })
                                    .collect(Collectors.toCollection(ArrayList::new));
                            if (JOptionPane.showConfirmDialog(null,
                                    "Removing a " + s + " will entail removing the following packages:\n" +
                                            createDeleteMessage(additionalList) +
                                            ".\nDelete they?", "Deletion additional packages",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)

                                    == JOptionPane.YES_OPTION) {
                                libWrapper.removeLib(s, true);
                                updateList();
                            }
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class updateButtonEventListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                updateList();
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent event) {

            table.clearSelection();
        }
    }


    /*::TODO переписать эту функцию
      ::TODO кастомное окно для ошибок и сообщений, вывод полного списка пакетов для удаления при нажатии на кнопку
     */

    class installButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            InstallWindow Inst = new InstallWindow(str -> {
                InstallationWindow IW = new InstallationWindow();
                IW.setLocation(getX() + getWidth() / 2, getY() + getHeight() / 2);
                IW.installationStart();
                AtomicBoolean err = new AtomicBoolean(false);
                Thread thread = new Thread(() -> {
                    try {
                        if (libWrapper.installLib(str) == null) {
                            err.set(true);
                        } else {
                            updateList();
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    IW.installationEnd();
                });

                thread.start();
                IW.setVisible(true);
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (err.get()) {
                    JOptionPane.showMessageDialog(null, "This package doesn't exist.", "Dialog",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            Inst.setLocation(getX() + getWidth() / 2, getY() + getHeight() / 2);
            Inst.setVisible(true);
        }
    }
}
