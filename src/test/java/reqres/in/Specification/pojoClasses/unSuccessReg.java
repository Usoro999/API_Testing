package reqres.in.Specification.pojoClasses;

public class unSuccessReg {
    private String  error;

    public unSuccessReg(String error) {
        this.error = error;
    }

    public unSuccessReg() {
    }

    public String getError() {
        return error;
    }
}
