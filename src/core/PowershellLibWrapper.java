package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PowershellLibWrapper extends LibWrapper {
    private final String default_con = System.getProperty("user.home") + "\\vcpkg\\vcpkg.exe";

    public PowershellLibWrapper() {
        if (new File(default_con).exists()) {
            file = new File(default_con);
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
    public ArrayList<String[]> installedList() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("powershell.exe", sanitize(file.getAbsolutePath()), "list --x-full-desc");
        return Arrays.stream(execute(processBuilder).split("\n"))
                .map(x -> Arrays.stream(x.split("  ")).filter(st -> !st.isEmpty()).toArray(String[]::new))
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