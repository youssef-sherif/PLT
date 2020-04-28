package lexicalanalyzer;

import static lexicalanalyzer.Constants.*;

public class Part {

    private final String expression;

    private String type;
    private Boolean asterisk = false;
    private Boolean plus = false;

    public Part(String type,
                String expression) {
        this.type = type;
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
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
