import core.WrapperFactory;
import ui.MainWindow;

import java.util.logging.Logger;

public class VCPKGGUI {
    private static final MainWindow mainWindow = new MainWindow(WrapperFactory.createWrapper());

    public static void main(String[] args) {
        getMainWindow().setVisible(true);
    }
    public static MainWindow getMainWindow() {
        return mainWindow;
    }

    public static Logger getLogger() {
        return Logger.getLogger("vcpkg");
    }
}
