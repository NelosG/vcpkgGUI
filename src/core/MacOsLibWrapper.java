package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MacOsLibWrapper extends UnixLibWrapper {

    MacOsLibWrapper(){
        super();
    }
    @Override
    public void checkVcpkg() {
        if (!checkInstallation()) {
            try {
                Files.createDirectory(Path.of("./macos"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            install("sh", "./macos");
        }
    }
}
