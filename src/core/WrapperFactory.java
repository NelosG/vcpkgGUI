package core;


public class WrapperFactory {
    public static LibWrapper createWrapper() throws UnsupportedOperationException {
        return OSValidator.validate() ? new PowershellLibWrapper() : new BashILibWrapper();
    }
}
