package core;

import java.io.IOException;
import java.util.ArrayList;

public class BashILibWrapper extends LibWrapper {


    public String installLib(String LibName) {
        return null;
    }

    @Override
    public String removeLib(String LibName, boolean recursive) {
        return null;
    }

    @Override
    public String version() throws IOException, InterruptedException {
        return null;
    }

    @Override
    public ArrayList<String[]> installedList() throws IOException, InterruptedException {
        return null;
    }

    public String validateInstallation() {
        return null;
    }

}
