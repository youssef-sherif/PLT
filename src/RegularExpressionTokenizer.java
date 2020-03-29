import java.util.*;

public class RegularExpressionTokenizer {

    private final String key;
    private final String[] regEx;
    private final Set<String> regularDefinitionsNames;

    private static final Character ASTERISK = '*';
    private static final Character PLUS = '+';

    public RegularExpressionTokenizer(String key, String regExInput, Set<String> regularDefinitionsNames) {
        this.key = key;
        this.regEx = regExInput.split(" ");
        this.regularDefinitionsNames = regularDefinitionsNames;
    }

    public void tokenizeRegEx() {

        System.out.println("===" + key + "===");
        ListIterator<String> iterator = Arrays.asList(regEx).listIterator();

        while (iterator.hasNext()) {
            String regEx = iterator.next();

            StringBuilder bracketBuffer = new StringBuilder();

            // if '(' found extract all the pattern inside '(' and ')' into a buffer
            if (regEx.startsWith("(")) {
                // remove '('
                regEx = regEx.substring(1);

                while (!regEx.contains(")")) {
                    bracketBuffer.append(" ").append(regEx);
                    regEx = iterator.next();
                }
                // remove ')'
                String regExInsideBracket = regEx.substring(0, regEx.indexOf(")"));
                bracketBuffer.append(" ").append(regExInsideBracket);
                System.out.println(bracketBuffer.toString());

                // Check if the bracket ends in '*' or '+'
                if (regEx.length() > regEx.indexOf(")")+1) {
                    if (regEx.charAt(regEx.indexOf(")") + 1) == '*') {
                        System.out.println("Bracket ends in '*'");
                    }
                    else if (regEx.charAt(regEx.indexOf(")") + 1) == '+') {
                        System.out.println("Bracket ends in '+'");
                    }
                }

                // tokenize the buffer between '(' and ')'
                tokenizePart(bracketBuffer.toString(), iterator);
            }
            else {
                tokenizePart(regEx, iterator);
            }
        }
    }


    private void tokenizePart(String regExPart, ListIterator<String> iterator) {
        /*
         * plainPattern is the current subpattern without '*' or '+'
         * for example digit* -> digit , digits+ -> digits ..
         */
        String plainPattern;

        // pattern ends with '*' and is not escaped
        if (regExPart.endsWith(String.valueOf(ASTERISK)) && !regExPart.startsWith("\\")) {

            System.out.println("Regular definition ending with '*' detected:");
            plainPattern = regExPart.substring(0, regExPart.length()-1);
            iterator.set(plainPattern);
            // Regular definition ending with '*' detected
            if (regularDefinitionsNames.contains(plainPattern.trim())) {
                // do nothing for now
            }
            System.out.println(plainPattern);
        }
        // pattern ends with '+' and is not escaped
        else if (regExPart.endsWith(String.valueOf(PLUS)) && !regExPart.startsWith("\\")) {

            System.out.println("Regular definition ending with '+' detected:");
            plainPattern = regExPart.substring(0, regExPart.length()-1);
            iterator.set(plainPattern);
            // Regular definition ending with '+' detected
            if (regularDefinitionsNames.contains(plainPattern.trim())) {
                // do nothing for now
            }
            System.out.println(plainPattern);
        }
        //Regular definition not ending with '+' or '*' detected
        else if (regularDefinitionsNames.contains(regExPart.trim())) {
            System.out.println("Regular definition not ending with '+' or '*' detected:");
            System.out.println(regExPart);
        }
        // Expression that is not a regular definition detected
        // could still contain '|' and regular definitions
        else {
            String[] ORedExpressions = regExPart.split("\\|");
            if (ORedExpressions.length > 1) {
                System.out.println("ORed expressions:");
                System.out.println(Arrays.toString(ORedExpressions));
            }
            else if (regExPart.equals("|")) {
                System.out.println(this.regEx[iterator.previousIndex()-1] + " OR " + this.regEx[iterator.nextIndex()]);
            }
            else {
                System.out.println(regExPart);
                // TODO: handle escape characters like '\*' and '\+' or
                // we can also compare directly using '\' prefix which I think is a better option

            }
        }

        // TODO: think of return type for this function to make integration with NFA easier
        //

    }

    /*
    TODO: create NFA State according to Thompson's construction
     */

}
