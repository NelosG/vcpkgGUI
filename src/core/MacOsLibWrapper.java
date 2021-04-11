package core;

public class MacOsLibWrapper extends UnixLibWrapper {

    MacOsLibWrapper(){
        super();
    }
    @Override
    public void checkVcpkg() {
        if (!checkInstallation()) {
            install("sh");
        }
    }
}
