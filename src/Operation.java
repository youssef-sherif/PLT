public class Operation {
    private final String type;
    private final String[] expressions;

    public Operation(String type, String[] expressions) {
        this.type = type;
        this.expressions = expressions;
    }

    public String getOperationType() {
        return type;
    }

    public String[] getExpressions() {
        return expressions;
    }
}
