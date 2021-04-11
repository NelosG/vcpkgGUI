package ui;

import ui.install.InstallationWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AddVcpkgPathForPowerShell extends JDialog {
    private JTextArea textArea;
    private JPanel root;
    private JButton setPathButton;
    private JButton installButton;
    private boolean pathAdded = false;
    private String specifiedPath;

    public AddVcpkgPathForPowerShell(){
        setContentPane(root);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocation(300, 300);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setTitle("Specify path(or drag here vcpkg.exe)");
        textArea.setDropTarget(new DropTarget() {
            @Override
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt
                            .getTransferable().getTransferData(
                                    DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        textArea.setText(file.getAbsolutePath());
                    }
                    setPathButton.doClick();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }});
        setPathButton.addActionListener(e -> {
            Path path = Path.of(textArea.getText().replace("\"", ""));
            if(path.toFile().exists() && path.getFileName().toString().equals("vcpkg.exe")){
                pathAdded = true;
                specifiedPath = path.toAbsolutePath().toString();
                dispose();
            }
        });
        installButton.addActionListener(e -> {
            ProcessBuilder processBuilder = new ProcessBuilder();
            try {
                String shell = "powershell.exe";
                processBuilder.command(shell, "cd", System.getProperty("user.home")).start();
                processBuilder.command(shell, "git", "clone", "https://github.com/microsoft/vcpkg");
                processBuilder.command(shell, "cd", "vcpkg").start();
                InstallationWindow IW = new InstallationWindow();
                IW.setLocation(getX() + getWidth()/2, getY() + getHeight()/2);
                IW.installationStart();
                Thread thread = new Thread(() -> {
                    processBuilder.command(shell, "bootstrap-vcpkg.bat");
                    IW.installationEnd();
                });

                thread.start();
                IW.setVisible(true);
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                specifiedPath = Path.of(System.getProperty("user.home") + "/vcpkg/vcpkg.exe").toAbsolutePath().toString();
                pathAdded = true;
                dispose();
            } catch (IOException ioException) {
                System.err.println("Can't install vcpkg.");
                System.exit(1);
            }
        });
    }

    public String getSpecifiedPath() {
        return specifiedPath;
    }


    @Override
    public void dispose(){
        if(!pathAdded){
            System.err.println("Can't work without path to vcpkg.");
            System.exit(1);
        }
        super.dispose();
    }
}
