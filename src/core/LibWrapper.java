package core;

import ui.install.InstallationWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class LibWrapper implements ILibWrapper {
    public StringBuilder output;
    protected String runFile;
    protected String shell;

    public LibWrapper() {
    }

    @Override
    public String sanitize(String installationName) {
        if (installationName.isEmpty())
            return installationName;
        return "\"" + installationName + "\"";
    }

    @Override
    public String execute(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        Process process = processBuilder.start();

        output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }

        int exitVal = process.waitFor();
        if (exitVal == 0) {
            return output.toString();
        } else {
            return null;
        }
    }

    @Override
    public void installLib(String installationName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(shell, sanitize(runFile), "install", sanitize(installationName));
        execute(processBuilder);
    }


    @Override
    public String removeLib(String LibName, boolean recursive) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(shell, sanitize(runFile),
                "remove", sanitize(LibName), recursive ? "--recurse" : "");
        return execute(processBuilder);
    }

    @Override
    public String version() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(shell, sanitize(runFile), "version");
        String[] arr = execute(processBuilder).split("\n")[0].split(" ");
        return arr[arr.length - 1];
    }

    @Override
    public ArrayList<String[]> installedPackagesList() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(shell, sanitize(runFile), "list --x-full-desc");
        return parsePackages(execute(processBuilder));
    }

    @Override
    public ArrayList<String[]> allPackagesList() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(shell, sanitize(runFile), "search");
        return parsePackages(execute(processBuilder));
    }

    private ArrayList<String[]> parsePackages(String str) {
        return Arrays.stream(str.split("\n"))
                .parallel()
                .map(x -> Arrays.stream(x.split("  ")).filter(st -> !st.isEmpty() && !st.equals(System.lineSeparator()))
                        .toArray(String[]::new))
                .filter(s -> s.length > 1)
                .map(s -> {
                    if (s.length == 2) {
                        s = new String[]{s[0], "", s[1]};
                    }
                    return s;
                }).collect(Collectors.toCollection(ArrayList<String[]>::new));
    }

    protected void install(String batORsh, String path) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            processBuilder.command(shell, "cd", path).start();
            processBuilder.command(shell, "git", "clone", "https://github.com/microsoft/vcpkg");
            processBuilder.command(shell, "cd", "vcpkg").start();
            InstallationWindow IW = new InstallationWindow();
            IW.setLocation(300, 300);
            IW.installationStart();
            Thread thread = new Thread(() -> {
                processBuilder.command(shell, "./bootstrap-vcpkg." + batORsh);
                processBuilder.command(shell, "vcpkg", "integrate", "install");
                IW.installationEnd();
            });

            thread.start();
            IW.setVisible(true);
            try {
                thread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ioException) {
            System.err.println("Can't install vcpkg.");
            System.exit(1);
        }
    }

    public abstract void checkVcpkg();
}
