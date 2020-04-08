public class Part {

    public static final String GROUP = "GROUP";
    public static final String DEF = "DEF";
    public static final String NOOP = "NOOP";

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

    public char getNFACharacter() {
        if (expression.length() == 2 && expression.startsWith("\\")) {
            if (expression.charAt(1) == 'L') {
                return 'λ';
            }
            return expression.charAt(1);
        } else {
            return expression.charAt(0);
        }
    }

    public boolean isGroup() {
        return type.equals(GROUP);
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

    @Override
    public String toString() {
        return  type + " " + (asterisk ? "*" : "")
                + (plus ? "+" : "")
                + " " + expression;
    }
}
