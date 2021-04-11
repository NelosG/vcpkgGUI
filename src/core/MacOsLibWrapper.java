package core;

import ui.install.InstallationWindow;

import java.io.IOException;

public class MacOsLibWrapper extends UnixLibWrapper  {

    @Override
    public void checkVcpkg() {
        if(!checkInstallation()) {
            try {
            getAndInstall();
            } catch (IOException e) {
                System.err.println("Can't install vcpkg.");
                System.exit(1);
            }
        }
    }


}
