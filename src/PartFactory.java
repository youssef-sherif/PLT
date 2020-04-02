import java.util.Map;
import java.util.Set;

public class PartFactory {

    private static final Character ASTERISK = '*';
    private static final Character PLUS = '+';
    private static Map<String, String> regularDefinitionsNames;
    private static Set<String> keyWords;
    private static Set<String> punctuation;

    public PartFactory(Map<String, String> regularDefinitionsNames, Set<String> keyWords, Set<String> punctuation) {
        PartFactory.regularDefinitionsNames = regularDefinitionsNames;
        PartFactory.keyWords = keyWords;
        PartFactory.punctuation = punctuation;
    }

    public Part createPart(String expression) {
        expression = expression.trim();
        if (expression.endsWith(String.valueOf(PLUS)) && !expression.equals("\\" + PLUS)) {
            String expression1 = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.keySet().contains(expression1)) {
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
            String expression1 = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.keySet().contains(expression1)) {
                Part part = new Part(Part.DEF, expression1);
                part.setAsterisk();
                return part;
            } else {
                Part part = new Part(Part.NOOP, expression);
                part.setAsterisk();
                return part;
            }
        }
        else if (regularDefinitionsNames.keySet().contains(expression)) {
            return new Part(Part.DEF, expression);
        }

        return new Part(Part.NOOP, expression);
    }

    public Part createGroupPart(String expression, char parenthesisPostfix, boolean and) {
        // Check if the bracket ends in '*' or '+'

        if (parenthesisPostfix == ASTERISK) {
            Part part;
            if (and) {
                part = new Part(Part.AND_GROUP, expression);
            } else {
                part = new Part(Part.OR_GROUP, expression);
            }
            part.setAsterisk();
            return part;
        } else if (parenthesisPostfix == PLUS) {
            Part part;
            if (and) {
                part = new Part(Part.AND_GROUP, expression);
            } else {
                part = new Part(Part.OR_GROUP, expression);
            }
            part.setPlus();
            return part;
        }

        Part part;
        if (and) {
            part = new Part(Part.AND_GROUP, expression);
        } else {
            part = new Part(Part.OR_GROUP, expression);
        }
        return part;
    }

}
