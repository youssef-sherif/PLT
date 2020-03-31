import java.util.*;

class RegularExpressionTokenizer {

    private static Set<String> regularDefinitionsNames;
    private static Set<String> keyWords;
    private static Set<String> punctuation;
    private static final Character ASTERISK = '*';
    private static final Character PLUS = '+';
    private Integer nfaId;


    public RegularExpressionTokenizer(String key, Set<String> regularDefinitionsNames, Set<String> keyWords, Set<String> punctuation) {
        System.out.println("===" + key + "===");
        RegularExpressionTokenizer.regularDefinitionsNames = regularDefinitionsNames;
        RegularExpressionTokenizer.keyWords = keyWords;
        RegularExpressionTokenizer.punctuation = punctuation;
    }

    public List<Part> tokenizeParenthesis(String regExString) {

        String[] regEx = (regExString + " |").split(" ");

        ListIterator<String> iterator = Arrays.asList(regEx).listIterator();
        List<Part> toReturn = new ArrayList<>();

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

                List<Part> bracketTokens = new ArrayList<>();
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
                // NOOP because we are still not sure what it is but it is not inside parenthesis
                toReturn.add(
                        noop(currRegEx)
                );
            }
        }

        return toReturn;
    }


    public List<List<Part>> tokenizeParts(List<Part> tokens) {

        List< List<Part>> toReturn = new ArrayList<>();
        List<Part> buffer = new ArrayList<>();

        for (Part part : tokens) {
            String currOp = part.getOperationType();
            String[] expressions = part.getExpressions();

            if (!part.isGroup()) {
                String[] ORedExpressions = expressions[0].trim().split("\\|");

                // fill buffer until we reach split. That means we should OR all ANDS together.
                if (ORedExpressions.length == 0) {
                    toReturn.add(buffer);
                    buffer = new ArrayList<>();
                }
                // parenthesis expression
                else {
                    buffer.add(new Part(currOp, ORedExpressions));
                }
            } else {
                buffer.add(part);
            }
        }

        return toReturn;
    }

    // TODO: this method returns empty NFA for now
    public NFA toNFA(List<List<Part>> andEdOperationsList) {
        for ( List<Part> operationsList: andEdOperationsList) {
            // NOTE: expressions with the same random number will be ANDed together
            int x =  (new Random()).nextInt(1000);
            for (Part e : operationsList) {
                if (e.isGroup()) {
                    System.out.println(x + " " + e.toString());
                    return toNFA(
                            tokenizeParts(
                                    tokenizeParenthesis(e.getExpressions()[0])
                            )
                    );
                }
                String[] operationExpressions = e.getExpressions();
                String operationType = e.getOperationType();
                // Part contains ORs
                // We need to split it to smaller parts.
                if (operationExpressions.length > 1) {
                    System.out.println("OR " +  Arrays.toString(operationExpressions));
                    for (String exp : operationExpressions) {
                        String[] andEd = exp.trim().split(" ");
                        if (andEd.length > 1) {
                            System.out.println("AND " + Arrays.toString(andEd));
                        } else {
                            System.out.println(x + " " + Arrays.toString(andEd));
//                            Part part = noop(andEd[0]);
//                            if (!part.getExpressions()[0].isEmpty())
//                                System.out.println(x + " " + part.toString());
                        }
                    }
                }
                // Part does not contain ORs.
                else {
                    System.out.println(x + " " + Arrays.toString(operationExpressions));
//                    Part part = noop(operationExpressions[0]);
//                    if (!part.getExpressions()[0].isEmpty())
//                        System.out.println(x + " " + part.toString());
                }
            }
        }

        return new NFA();
    }

    private Part noop(String expression) {

        if (expression.endsWith(String.valueOf(PLUS)) && !expression.startsWith("\\")) {
            String expression1 = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.contains(expression1)) {
                return new Part("DEF " + PLUS, new String[]{expression});
            } else {
                return new Part("NOOP " + String.valueOf(PLUS), new String[]{expression});
            }
        }
        else if (expression.endsWith(String.valueOf(ASTERISK)) && !expression.startsWith("\\")) {
            String expression1 = expression.substring(0, expression.length()-1);
            if (regularDefinitionsNames.contains(expression1)) {
                return new Part("DEF " + ASTERISK, new String[]{expression});
            } else {
                return new Part("NOOP " + String.valueOf(ASTERISK), new String[]{expression});
            }
        }
        else if (regularDefinitionsNames.contains(expression)) {
            return new Part("DEF", new String[]{expression});
        }

        return new Part("NOOP", new String[]{expression});
    }

    private Part asterisk(Part expression) {
        expression.setOperationType(expression.getOperationType() + " " + ASTERISK);
        return expression;
    }

    private Part plus(Part expression) {
        expression.setOperationType(expression.getOperationType() + " " + PLUS);
        return expression;
    }

    private Part group(String expression) {
        return new Part("GROUP", new String[]{expression});
    }


    /*
    TODO: create NFA State according to Thompson's construction
     */

}
