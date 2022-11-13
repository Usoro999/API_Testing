package reqres.in.Specification.pojoClasses;

public class UserTimeResponse extends UserTime{

    private String updatedAt;

    public UserTimeResponse(String name, String job, String updatedAt) {
        super(name, job);
        this.updatedAt = updatedAt;
    }

    public UserTimeResponse(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserTimeResponse(String name, String job) {
        super(name, job);
    }

    public UserTimeResponse() {
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
