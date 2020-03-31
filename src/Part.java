import java.lang.reflect.Array;
import java.util.Arrays;

public class Part {
    private String type;
    private final String[] expressions;

    public Part(String type, String[] expressions) {
        this.type = type;
        this.expressions = expressions;
    }

    public String getOperationType() {
        return type;
    }

    public String[] getExpressions() {
        return expressions;
    }

    public void setOperationType(String newType) {
        this.type = newType;
    }

    public boolean isGroup() {
        return type.startsWith("GROUP");
    }

    @Override
    public String toString() {
        return type + " " + Arrays.toString(expressions);
    }
}
