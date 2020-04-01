import java.lang.reflect.Array;
import java.util.Arrays;

public class Part {

    public static String DEF = "DEF";
    public static String GROUP = "GROUP";
    public static String NOOP = "NOOP";

    private String type;
    private final String expression;
    private Boolean asterisk = false;
    private Boolean plus = false;

    public Part(String type, String expression) {
        this.type = type;
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public boolean isGroup() {
        return type.startsWith(GROUP);
    }

    public Boolean isAsterisk() {
        return asterisk;
    }

    public Boolean isPlus() {
        return plus;
    }

    public void setAsterisk() {
        this.asterisk = true;
    }

    public void setPlus() {
        this.plus = true;
    }

    @Override
    public String toString() {
        return type + " " + expression;
    }
}
