import java.util.*;

class RegularExpressionTokenizer {

    private final String key;
    private final String regEx;

    private static Set<String> regularDefinitionsNames;
    private static final Character ASTERISK = '*';
    private static final Character PLUS = '+';


    public RegularExpressionTokenizer(String key, String regExInput, Set<String> regularDefinitionsNames) {
        this.key = key;
        this.regEx = regExInput + " |";
        RegularExpressionTokenizer.regularDefinitionsNames = regularDefinitionsNames;
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

                // TODO: use stack to handle parenthesis within other parenthesis
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
                    if (currRegEx.charAt(currRegEx.indexOf(")") + 1) == ASTERISK) {
                        // repeat OR / AND tokenization inside bracket
                        bracketTokens.add(asterisk(
                                        group(bracketGroup)
                                ));
                        toReturn.addAll(bracketTokens);
                    } else if (currRegEx.charAt(currRegEx.indexOf(")") + 1) == PLUS) {
                        bracketTokens.add(plus(
                                        group(bracketGroup)
                                ));
                        toReturn.addAll(bracketTokens);
                    }
                } else {
                    bracketTokens.add(
                            group(bracketGroup)
                    );
                    toReturn.addAll(bracketTokens);
                }
            } else {
                // NOOP because we are not sure what it is
                toReturn.add(
                        noop(currRegEx)
                );
            }
        }

        return tokenizeORs(toReturn);
    }


    private List<String[]> tokenizeORs(List<String[]> tokens) {

        List<String[]> toReturn = new ArrayList<>();
        List<Operation> buffer = new ArrayList<>();

        for (String[] curr : tokens) {
            String currOp = curr[0];
            String currExp = curr[1];

            String[] ORedExpressions = currExp.split("\\|");

            // fill buffer until we reach split. That means we should OR all ANDS together.
            if (ORedExpressions.length == 0) {
                toReturn.add(tokenizeAnds(buffer));
                buffer = new ArrayList<>();
            } else {
                buffer.add(new Operation(currOp, ORedExpressions));
            }
        }

        return toReturn;
    }

    // TODO: this function should be recursive
    private  String[] tokenizeAnds(List<Operation> expressions) {
        // expressions with the same random number are ANDed together
        double x = Math.random();
        for (Operation e : expressions) {
            String[] operationExpressions = e.getExpressions();
            String operationType = e.getOperationType();
            System.out.println(x + " " + operationType + " " + Arrays.toString(operationExpressions));
            if (operationExpressions.length > 1) {
                System.out.println("OR " +  Arrays.toString(operationExpressions));
                for (String exp :  operationExpressions) {
                    String[] andEd = exp.trim().split(" ");
                    if (andEd.length > 1) {
                        System.out.println("AND " + Arrays.toString(andEd));
                    } else {
                        System.out.println(Arrays.toString(noop(andEd[0])));
                    }
                }
            } else {
                System.out.println(Arrays.toString(noop(operationExpressions[0])));
            }
        }

        return new String[]{};
    }

    private String[] noop(String expression) {
        if (expression.endsWith(String.valueOf(PLUS)) && !expression.startsWith("\\")) {
            expression = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.contains(expression)) {
                return new String[]{"DEF " + PLUS, expression};
            } else {
                return new String[]{String.valueOf(PLUS), expression};
            }
        } else if (expression.endsWith(String.valueOf(ASTERISK)) && !expression.startsWith("\\")) {
            expression = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.contains(expression)) {
                return new String[]{"DEF " + ASTERISK, expression};
            } else {
                return new String[]{String.valueOf(ASTERISK), expression};
            }
        }

        return new String[]{"NOOP", expression};
    }

    private String[] asterisk(String[] expression) {
        expression[0] += " *";
        return expression;
    }

    private String[] plus(String[] expression) {
        expression[0] += " +";
        return expression;
    }

    private String[] group(String expression) {
        return new String[]{"GROUP", expression};
    }


    /*
    TODO: create NFA State according to Thompson's construction
     */

}
