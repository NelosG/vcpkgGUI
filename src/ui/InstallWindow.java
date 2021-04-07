package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class InstallWindow extends JDialog {
    private JTextField forInstall;
    private JPanel InstallPanel;
    private JButton installButton;
    private JButton exitButton;
    private final Consumer<String> consumer;

    //::TODO выбирать кнопку install по умолчанию

    InstallWindow(Consumer<String> cons){
        consumer = cons;
        setContentPane(InstallPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(250, 125);
        setResizable(false);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setTitle("Installation");

        installButton.addActionListener(new installButtonEventListener());
        exitButton.addActionListener(new exitButtonEventListener());
    }

    class installButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            exit(false);
        }
    }

    class exitButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            exit(true);
        }
    }

    private void exit(boolean exit) {
        setModalityType(ModalityType.MODELESS);
        setVisible(false);
        if(!exit) {
            consumer.accept(forInstall.getText());
        }
        dispose();
    }
}
