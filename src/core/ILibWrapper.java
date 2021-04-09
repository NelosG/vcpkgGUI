package core;


import java.io.IOException;
import java.util.ArrayList;

public interface ILibWrapper {

    String sanitize(String installationName);

    String installLib(String LibName) throws IOException, InterruptedException;

    String execute(ProcessBuilder processBuilder) throws IOException, InterruptedException;

    String removeLib(String LibName, boolean recursive) throws IOException, InterruptedException;

    String version() throws IOException, InterruptedException;

    ArrayList<String[]> installedList() throws IOException, InterruptedException;

    String validateInstallation();

}
