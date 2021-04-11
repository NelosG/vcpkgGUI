package ui.install;

import javax.swing.*;

public class InstallationWindow extends JDialog {
    private JPanel installationPanel;
    private boolean revoked = false;
    private Thread thread;

    private final String[] Installing = new String[]{
            "Installing",
            "Installing.",
            "Installing..",
            "Installing..."
    };


    //::TODO удалить кнопку закрытия окна

    public InstallationWindow(){
        setContentPane(installationPanel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(200, 0);
        setResizable(false);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setTitle(Installing[0]);
    }

    public void installationStart() {
        thread = new Thread(() -> {
            int k = 1;
            while (!revoked) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setTitle(Installing[k]);
                k++;
                if(k > 3) {
                    k = 0;
                }
            }
        });
        thread.start();
    }

    public void installationEnd(){
        revoked = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dispose();
    }
}
