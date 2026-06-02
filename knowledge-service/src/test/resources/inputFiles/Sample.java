/**
 * Sample Java class for testing Tika code file parsing.
 */
public class Sample {

    private String name;

    public Sample(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        Sample sample = new Sample("Test");
        System.out.println("Name: " + sample.getName());
    }
}
