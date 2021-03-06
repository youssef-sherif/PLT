package lexicalanalyzer;

import static lexicalanalyzer.Constants.*;

import java.util.Set;

public class PartFactory {


    private static Set<String> regularDefinitions;

    public PartFactory(Set<String> regularDefinitionsNames) {
        PartFactory.regularDefinitions = regularDefinitionsNames;
    }

    public Part createPart(String expression, char postfix) {
        String expression1 = expression.trim();
        if (regularDefinitions.contains(expression1)) {
            if (postfix == ASTERISK) {
                Part part = new Part(DEF, expression1);
                part.setAsterisk();
                return part;
            } else if (postfix == PLUS) {
                Part part = new Part(DEF, expression1);
                part.setPlus();
                return part;
            } else {
                return new Part(DEF, expression1);
            }
        }

        if (postfix == ASTERISK) {
            Part part = new Part(NOOP, expression1);
            part.setAsterisk();
            return part;
        } else if (postfix == PLUS) {
            Part part = new Part(NOOP, expression1);
            part.setPlus();
            return part;
        } else {
            return new Part(NOOP, expression1);
        }
    }

    public Part createGroupPart(String expression, char postfix) {
        // Check if the bracket ends in '*' or '+'
        if (postfix == ASTERISK) {
            Part part = new Part(GROUP, expression);
            part.setAsterisk();
            return part;
        } else if (postfix == PLUS) {
            Part part = new Part(GROUP, expression);
            part.setPlus();
            return part;
        } else {
            return new Part(GROUP, expression);
        }
    }

    public Part createNoOpPart(String expression) {
        return new Part(NOOP, expression);
    }
}
