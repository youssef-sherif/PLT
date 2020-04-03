import java.lang.reflect.Array;
import java.util.Arrays;

public class Part {

    public static final String OR_GROUP = "OR GROUP";
    public static final String AND_GROUP = "AND_GROUP";
    public static final String DEF = "DEF";
    public static final String NOOP = "NOOP";

    private String type;
    private final String expression;
    private Boolean asterisk = false;
    private Boolean plus = false;
    private Boolean isCompleted = false;

    public Part(String type, String expression) {
        this.type = type;
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public boolean isGroup() {
        return type.equals(AND_GROUP)
                || type.equals(OR_GROUP);
    }

    public boolean isAndGroup() {
        return type.equals(AND_GROUP);
    }

    public boolean isOrGroup() {
        return type.equals(OR_GROUP);
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

    public boolean isDefinition() {
        return type.equals(DEF);
    }

    public Boolean isCompleted() {
        return isCompleted;
    }

    public void markCompleted() {
        isCompleted = true;
    }

    @Override
    public String toString() {
        return  type + " " + (asterisk ? "*" : "")
                + (plus ? "+" : "")
                + " " + expression;
    }
}
