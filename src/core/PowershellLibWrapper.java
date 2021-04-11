package core;

import ui.AddVcpkgPathForPowerShell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PowershellLibWrapper extends LibWrapper {
    PowershellLibWrapper() {
        super();
        shell = "powershell.exe";
        runFile = Path.of(System.getProperty("user.home") + "/vcpkg/vcpkg.exe").toAbsolutePath().toString();
    }

    @Override
    public void checkVcpkg() {
        if(!new File(runFile).exists()) {
            Path path = Path.of("./configuration");
            if(path.toFile().exists()) {
                boolean err = false;
                try (BufferedReader br = Files.newBufferedReader(path)){
                    runFile = br.readLine();
                } catch (IOException e) {
                    err = true;
                }
                if(!err) {
                    return;
                }
            }
            AddVcpkgPathForPowerShell pathWindow = new AddVcpkgPathForPowerShell();
            pathWindow.setVisible(true);
            runFile = pathWindow.getSpecifiedPath();
            try (BufferedWriter bw = Files.newBufferedWriter(path)){
                bw.write(runFile);
            } catch (IOException ignored) {
            }
        }
    }
}
