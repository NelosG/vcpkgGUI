package ui;

import core.LibWrapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
    private final LibWrapper libWrapper;
    private final DefaultListModel<String> defaultListModel = new DefaultListModel<>();
    private JPanel root;
    private JButton installPackageButton;
    private JButton deletePackageButton;
    private JLabel version;
    private JList<String> list;

    public MainWindow(LibWrapper libWrapper) {
        super("vcpkgGUI");
        this.libWrapper = libWrapper;
        setContentPane(root);
        setBounds(0, 0, 1080, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        installPackageButton.addActionListener(new installButtonEventListener());
        deletePackageButton.addActionListener(new deleteButtonEventListener());
        try {
            version.setText("Version of vcpkg: " + libWrapper.version());
            updateList();
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
        list.setModel(defaultListModel);
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
        defaultListModel.clear();
        defaultListModel.addAll(libWrapper.installedList());
    }

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


    /*::TODO переписать эту функцию
      ::TODO кастомное окно для ошибок и сообщений, вывод полного списка пакетов для удаления при нажатии на кнопку
     */

    class deleteButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            List<String> listForDelete = list.getSelectedValuesList();
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
                                defaultListModel.removeElement(s);
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
    }
}
