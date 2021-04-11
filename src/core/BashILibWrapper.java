package core;

import ui.install.InstallationWindow;

import java.io.IOException;

public class BashILibWrapper extends UnixLibWrapper {

    BashILibWrapper(){
        super();
        shell = "vcpkg";
        runFile = "";
    }

    @Override
    public void checkVcpkg() {
        if(!checkInstallation()) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            try {

            processBuilder.command("sudo", "apt-get", "update").start();
            processBuilder.command("sudo", "apt-get", "install", "build-essential",
                    "tar", "curl", "zip", "unzip").start();

            getAndInstall();
            } catch (IOException e) {
                System.err.println("Can't install vcpkg.");
                System.exit(1);
            }
        }
    }
}
