package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PowershellLibWrapper extends LibWrapper {
    private final String default_con = System.getProperty("user.home") + "\\vcpkg\\vcpkg.exe";

    public PowershellLibWrapper() {
        File f = new File(default_con);
        if (f.exists()) {
            file = f;
        }
    }

    @Override
    public String installLib(String installationName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", sanitize(file.getAbsolutePath()), "install", sanitize(installationName));
        return execute(processBuilder);
    }


    @Override
    public String removeLib(String LibName, boolean recursive) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", sanitize(file.getAbsolutePath()),
                "remove", sanitize(LibName), recursive ? "--recurse" : "");
        return execute(processBuilder);
    }

    @Override
    public String version() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", sanitize(file.getAbsolutePath()), "version");
        String[] arr = execute(processBuilder).split("\n")[0].split(" ");
        return arr[arr.length - 1];
    }

    @Override
    public ArrayList<String[]> installedPackagesList() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", sanitize(file.getAbsolutePath()), "list --x-full-desc");
        return parsePackages(execute(processBuilder));
    }

    @Override
    public ArrayList<String[]> allPackagesList() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", sanitize(file.getAbsolutePath()), "search");
        ArrayList<String[]> list = parsePackages(execute(processBuilder));
        return list;
    }

    private ArrayList<String[]> parsePackages(String str) {
        return Arrays.stream(str.split("\n"))
                .parallel()
                .map(x -> Arrays.stream(x.split("  ")).parallel().filter(st -> !st.isEmpty() && !st.equals(System.lineSeparator()))
                        .toArray(String[]::new))
                .filter(s -> s.length > 1)
                .map(s -> {
                    if(s.length == 2) {
                        s = new String[]{s[0], "", s[1]};
                    }
                    return s;
                }).collect(Collectors.toCollection(ArrayList<String[]>::new));
    }

    public String validateInstallation() {
        return null;
    }

}
