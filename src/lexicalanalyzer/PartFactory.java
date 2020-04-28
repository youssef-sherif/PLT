package lexicalanalyzer;

import java.util.Map;

public class PartFactory {

    private static final Character ASTERISK = '*';
    private static final Character PLUS = '+';
    private static Map<String, String> regularDefinitions;

    public PartFactory(Map<String, String> regularDefinitionsNames) {
        PartFactory.regularDefinitions = regularDefinitionsNames;
    }

    public Part createPart(String expression, char postfix) {
        String expression1 = expression.trim();
        if (regularDefinitions.containsKey(expression1)) {
            if (postfix == ASTERISK) {
                Part part = new Part(Part.DEF, expression1);
                part.setAsterisk();
                return part;
            } else if (postfix == PLUS) {
                Part part = new Part(Part.DEF, expression1);
                part.setPlus();
                return part;
            } else {
                return new Part(Part.DEF, expression1);
            }
        }

        if (postfix == ASTERISK) {
            Part part = new Part(Part.NOOP, expression1);
            part.setAsterisk();
            return part;
        } else if (postfix == PLUS) {
            Part part = new Part(Part.NOOP, expression1);
            part.setPlus();
            return part;
        } else {
            return new Part(Part.NOOP, expression1);
        }
    }

    public Part createGroupPart(String expression, char postfix) {
        // Check if the bracket ends in '*' or '+'
        if (postfix == ASTERISK) {
            Part part = new Part(Part.GROUP, expression);
            part.setAsterisk();
            return part;
        } else if (postfix == PLUS) {
            Part part = new Part(Part.GROUP, expression);
            part.setPlus();
            return part;
        } else {
            return new Part(Part.GROUP, expression);
        }
    }

    public Part createNoOpPart(String expression) {
        return new Part(Part.NOOP, expression);
    }
}
