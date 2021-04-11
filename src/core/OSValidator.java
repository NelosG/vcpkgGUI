package core;


public class OSValidator {
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static int validate() throws UnsupportedOperationException {
        if (isWindows()) {
            return 0;
        } else if (isUnix()) {
            return 1;
        } if(isMac()){
            return 2;
        }else {
            throw new UnsupportedOperationException("Can't run on that System.");
        }
    }

    private static boolean isWindows() {
        return OS.contains("win");
    }

    private static boolean isMac() {
        return OS.contains("mac");
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }
}