package ui;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class AddVcpkgPathForPowerShell extends JDialog {
    private JTextArea textArea;
    private JPanel root;
    private JButton setPathButton;
    private JButton installButton;
    private boolean pathAdded = false;
    private String specifiedPath;
    private final Consumer<String> consumer;

    public AddVcpkgPathForPowerShell(Consumer<String> cons) {
        consumer = cons;
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
            }
        });
        setPathButton.addActionListener(e -> {
            Path path = Path.of(textArea.getText().replace("\"", ""));
            if (path.toFile().exists() && path.getFileName().toString().equals("vcpkg.exe")) {
                pathAdded = true;
                specifiedPath = path.toAbsolutePath().toString();
                dispose();
            }
        });
        installButton.addActionListener(e -> {
            consumer.accept("bat");
            specifiedPath = Path.of(System.getProperty("user.home") + "/vcpkg/vcpkg.exe").toAbsolutePath().toString();
            pathAdded = true;
            dispose();
        });
    }

    public String getSpecifiedPath() {
        return specifiedPath;
    }


    @Override
    public void dispose() {
        if (!pathAdded) {
            System.err.println("Can't work without path to vcpkg.");
            System.exit(1);
        }
        super.dispose();
    }
}
