package core;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class LibWrapper implements ILibWrapper {
    public StringBuilder output;
    File file;
    private final String default_con = "test";

    public LibWrapper() {
        if (new File(default_con).exists()) {
            file = new File(default_con);
        }
    }

    @Override
    public String sanitize(String installationName) {
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
}
