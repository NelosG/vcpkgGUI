import core.WrapperFactory;
import ui.MainWindow;


public class VCPKGGUI {

    public static void main(String[] args) {
        new MainWindow(WrapperFactory.createWrapper()).setVisible(true);
    }

}
