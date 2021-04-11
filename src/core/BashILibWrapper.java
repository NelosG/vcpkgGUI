package core;

public class BashILibWrapper extends LibWrapper {

    BashILibWrapper(){
        super();
        shell = "vcpkg";
        runFile = "";
    }

    //::TODO add installation for bash if vcpkg doesn't exist.
    @Override
    public void checkVcpkg() {
    }
}
