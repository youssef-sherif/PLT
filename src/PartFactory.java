import java.util.Map;
import java.util.Set;

public class PartFactory {

    private static final Character ASTERISK = '*';
    private static final Character PLUS = '+';
    private static Map<String, String> regularDefinitions;
    private static Set<String> keyWords;
    private static Set<String> punctuation;

    public PartFactory(Map<String, String> regularDefinitionsNames,
                       Set<String> keyWords,
                       Set<String> punctuation) {
        PartFactory.regularDefinitions = regularDefinitionsNames;
        PartFactory.keyWords = keyWords;
        PartFactory.punctuation = punctuation;
    }

    public Part createPart(String expression) {
        expression = expression.trim();
        if (expression.endsWith(String.valueOf(PLUS)) && !expression.equals("\\" + PLUS)) {
            String expression1 = expression.substring(0, expression.length()-1).trim();
            if (regularDefinitions.keySet().contains(expression1)) {
                Part part = new Part(Part.DEF, expression1);
                part.setPlus();
                return part;
            } else {
                Part part = new Part(Part.NOOP, expression);
                part.setPlus();
                return part;
            }
        }
        else if (expression.endsWith(String.valueOf(ASTERISK)) && !expression.equals("\\" + ASTERISK)) {
            String expression1 = expression.substring(0, expression.length()-1).trim();
            if (regularDefinitions.keySet().contains(expression1)) {
                Part part = new Part(Part.DEF, expression1);
                part.setAsterisk();
                return part;
            } else {
                Part part = new Part(Part.NOOP, expression);
                part.setAsterisk();
                return part;
            }
        }
        else if (regularDefinitions.keySet().contains(expression)) {
            return new Part(Part.DEF, expression);
        }

        return new Part(Part.NOOP, expression);
    }

    public Part createGroupPart(String expression, char parenthesisPostfix) {
        // Check if the bracket ends in '*' or '+'
        if (parenthesisPostfix == ASTERISK) {
            Part part = new Part(Part.GROUP, expression);
            part.setAsterisk();
            return part;
        } else if (parenthesisPostfix == PLUS) {
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
