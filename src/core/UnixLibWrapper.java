package core;

import ui.install.InstallationWindow;

import java.io.IOException;

public abstract class UnixLibWrapper extends LibWrapper {
    protected void getAndInstall() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("git", "clone", "https://github.com/microsoft/vcpkg");
        processBuilder.command("cd", "vcpkg").start();

        InstallationWindow IW = new InstallationWindow();
        IW.setLocation(300, 300);
        IW.installationStart();
        Thread thread = new Thread(() -> {
            processBuilder.command("./bootstrap-vcpkg.sh");
            IW.installationEnd();
        });

        thread.start();
        IW.setVisible(true);
        try {
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    protected boolean checkInstallation() {
        try {
            if(version() == null) {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return true;
    }
}
