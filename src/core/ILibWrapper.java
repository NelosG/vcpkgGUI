package core;


import java.io.IOException;
import java.util.ArrayList;

public interface ILibWrapper {

    String sanitize(String installationName);

    void installLib(String LibName) throws IOException, InterruptedException;

    String execute(ProcessBuilder processBuilder) throws IOException, InterruptedException;

    String removeLib(String LibName, boolean recursive) throws IOException, InterruptedException;

    String version() throws IOException, InterruptedException;

    ArrayList<String[]> installedPackagesList() throws IOException, InterruptedException;

    ArrayList<String[]> allPackagesList() throws IOException, InterruptedException;
}
