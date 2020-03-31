import java.util.*;

class RegularExpression {

    private final String key;
    private final String regEx;
    private static Set<String> regularDefinitionsNames;


    public RegularExpression(String key, String regExInput, Set<String> regularDefinitionsNames) {
        this.key = key;
        this.regEx = regExInput;
        this.regularDefinitionsNames = regularDefinitionsNames;
    }

    public List<String[]> tokenize() {

        System.out.println("===" + key + "===");

        String[] regEx = this.regEx.split(" ");

        ListIterator<String> iterator = Arrays.asList(regEx).listIterator();
        List<String[]> toReturn = new ArrayList<>();

        while (iterator.hasNext()) {
            String currRegEx = iterator.next();

            // if '(' found extract all the pattern inside '(' and ')' into a buffer
            if (currRegEx.startsWith("(")) {
                // buffer that we append to all regEx within '(' and ')'
                StringBuilder bracketBuffer = new StringBuilder();

                // remove '('
                currRegEx = currRegEx.substring(1);

                while (!currRegEx.contains(")")) {
                    bracketBuffer.append(" ").append(currRegEx);
                    currRegEx = iterator.next();
                }
                // remove ')'
                String regExInsideBracket = currRegEx.substring(0, currRegEx.indexOf(")"));
                bracketBuffer.append(" ").append(regExInsideBracket);

                List<String[]> bracketTokens = new ArrayList<>();
                String bracketGroup = bracketBuffer.toString();
                // Check if the bracket ends in '*' or '+'
                if (currRegEx.length() > currRegEx.indexOf(")") + 1) {
                    if (currRegEx.charAt(currRegEx.indexOf(")") + 1) == '*') {
                        // repeat OR / AND tokenization inside bracket
                        bracketTokens.add(asterisk(
                                        group(bracketGroup)
                                ));
                        toReturn.addAll(bracketTokens);
                    } else if (currRegEx.charAt(currRegEx.indexOf(")") + 1) == '+') {
                        // repeat OR / AND tokenization inside bracket
                        bracketTokens.add(plus(
                                        group(bracketGroup)
                                ));
                        toReturn.addAll(bracketTokens);
                    }
                } else {
                    // repeat OR / AND tokenization inside bracket
                    bracketTokens.add(
                            group(bracketGroup)
                    );
                    toReturn.addAll(bracketTokens);
                }
            } else {
                toReturn.add(
                        noop(currRegEx)
                );
            }
        }

        return tokenizeORs(toReturn);
    }


    private List<String[]> tokenizeORs(List<String[]> tokens) {

        List<String[]> toReturn = new ArrayList<>();

        for (String[] curr : tokens) {
            String currOp = curr[0];
            String currExp = curr[1];

            System.out.println(currExp);
            String[] ORedExpressions = currExp.split("\\|");

            if (ORedExpressions.length > 0) {
                toReturn.add(or(currOp, ORedExpressions));
            }
        }

        return toReturn;
    }

    public static String[] asterisk(String[] expression) {
        expression[0] += " *";
        return expression;
    }

    public static String[] plus(String[] expression) {
        expression[0] += " +";
        return expression;
    }

    public static String[] group(String expression) {
        return new String[]{"GROUP", expression};
    }

    public static String[] noop(String expression) {
        if (expression.endsWith("+")) {
            expression = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.contains(expression)) {
                return new String[]{"DEF +", expression};
            } else {
                return new String[]{"+", expression};
            }
        }
        else if (expression.endsWith("*")) {
            expression = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.contains(expression)) {
                return new String[]{"DEF *", expression};
            } else {
                return new String[]{"*", expression};
            }
        }
        return new String[]{"NOOP", expression};
    }

    public static String[] or(String currOp, String[] oRedExpressions) {
        List<String> orFormat = new ArrayList<>();
        orFormat.add(currOp);
        orFormat.addAll(Arrays.asList(oRedExpressions));
        return orFormat.toArray(new String[0]);
    }

    /*
    TODO: create NFA State according to Thompson's construction
     */

}
