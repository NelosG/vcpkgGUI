import core.WrapperFactory;
import ui.MainWindow;


public class VcpkgGui {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new MainWindow(WrapperFactory.createWrapper()).setVisible(true));
    }

}
