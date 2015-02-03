package Models.GearSpec;

/**
 * Created by matthewyork on 3/31/14.
 */
public class DependencySpecAuthor {
    private String name;
    private String email;

    public DependencySpecAuthor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
