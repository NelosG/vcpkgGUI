package core;

import java.io.IOException;

public abstract class UnixLibWrapper extends LibWrapper {

    UnixLibWrapper(){
        super();
        shell = "";
        runFile = "vcpkg";
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
