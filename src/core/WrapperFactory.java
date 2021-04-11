package core;


public class WrapperFactory {
    public static LibWrapper createWrapper() throws UnsupportedOperationException {
        switch (OSValidator.validate()) {
            case 0 -> {
                return new PowershellLibWrapper();
            }
            case 1 -> {
                return new BashILibWrapper();
            }
        }
        return null;
    }
}
