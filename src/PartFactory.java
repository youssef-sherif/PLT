import java.util.Set;

public class PartFactory {

    private static final Character ASTERISK = '*';
    private static final Character PLUS = '+';
    private static Set<String> regularDefinitionsNames;
    private static Set<String> keyWords;
    private static Set<String> punctuation;

    public PartFactory(Set<String> regularDefinitionsNames, Set<String> keyWords, Set<String> punctuation) {
        PartFactory.regularDefinitionsNames = regularDefinitionsNames;
        PartFactory.keyWords = keyWords;
        PartFactory.punctuation = punctuation;
    }

    public Part createPart(String expression) {

        if (expression.endsWith(String.valueOf(PLUS)) && !expression.equals("\\" + PLUS)) {
            String expression1 = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.contains(expression1)) {
                Part part = new Part(Part.DEF, expression1);
                part.setAsterisk();
                return part;
            } else {
                Part part = new Part(Part.NOOP, expression);
                part.setAsterisk();
                return part;
            }
        }
        else if (expression.endsWith(String.valueOf(ASTERISK)) && !expression.equals("\\" + ASTERISK)) {
            String expression1 = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.contains(expression1)) {
                Part part = new Part(Part.DEF, expression1);
                part.setAsterisk();
                return part;
            } else {
                Part part = new Part(Part.NOOP, expression);
                part.setAsterisk();
                return part;
            }
        }
        else if (regularDefinitionsNames.contains(expression)) {
            return new Part(Part.DEF, expression);
        }

        return new Part(Part.NOOP, expression);
    }

    public Part createGroupPart(String expression, String currRegEx) {
        // Check if the bracket ends in '*' or '+'
        if (currRegEx.length() > currRegEx.indexOf(")") + 1) {
            if (currRegEx.charAt(currRegEx.indexOf(")") + 1) == ASTERISK) {
                Part part = new Part(Part.GROUP, expression);
                part.setAsterisk();

            } else if (currRegEx.charAt(currRegEx.indexOf(")") + 1) == PLUS) {
                Part part = new Part(Part.GROUP, expression);
                part.setPlus();
            }
        }

        return new Part(Part.GROUP, expression);
    }

}
