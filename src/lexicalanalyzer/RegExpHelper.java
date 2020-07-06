package lexicalanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class RegExpHelper {

    public static List<Part> findORedParts(String regExString, PartFactory partFactory) {
        List<Character> regExStream = regExString.chars()
                // Convert IntStream to Stream<Character>
                .mapToObj(e -> (char)e)
                // Collect the elements as a List Of Characters
                .collect(Collectors.toList());

        ListIterator<Character> iterator = regExStream.listIterator();
        StringBuilder buffer = new StringBuilder();
        List<Part> toReturn = new ArrayList<>();

        boolean ignoring = false;
        while (iterator.hasNext()) {
            char curr = iterator.next();

            buffer.append(curr);

            // we ignore any '|' symbol that is within parenthesis.
            // this function gets called again when we want to get ORed parts inside parenthesis.
            if (curr == '(') ignoring = true;
            if (curr == ')') ignoring = false;
            if ((curr == '|' || !iterator.hasNext())
                    && !ignoring) {
                buffer.deleteCharAt(buffer.length()-1);
                Part part = partFactory.createNoOpPart(buffer.toString());
                toReturn.add(part);
                buffer = new StringBuilder();
            }
        }

        return toReturn;
    }

    public static List<List<Part>> findGroupedParts(List<Part> parts, PartFactory partFactory) {

        List<List<Part>> toReturn = new ArrayList<>();

        for (Part part : parts) {
            String regExString1 = part.getExpression() + " ";
            List<Character> regExStream = regExString1.chars()
                    // Convert IntStream to Stream<Character>
                    .mapToObj(e -> (char) e)
                    // Collect the elements as a List Of Characters
                    .collect(Collectors.toList());

            ListIterator<Character> iterator = regExStream.listIterator();
            List<Part> temp = new ArrayList<>();

            StringBuilder buffer = new StringBuilder();
            int parenthesesCounter = 0;

            while (iterator.hasNext()) {
                char currRegEx = iterator.next();

                // if '(' found extract all the pattern inside '(' and ')' into a buffer
                if (currRegEx == '(' && iterator.hasNext()) {
                    // buffer that we append to all regEx within '(' and ')'
                    StringBuilder bracketBuffer = new StringBuilder();
                    char parenthesisPostfix = ' ';

                    // Combine all characters before '('
                    temp.add(
                            partFactory.createNoOpPart(buffer.toString())
                    );
                    buffer = new StringBuilder();

                    // skip '('
                    currRegEx = iterator.next();

                    parenthesesCounter++;
                    while (true) {
                        if (currRegEx == '(') {
                            parenthesesCounter++;
                        } else if (regExStream.get(iterator.previousIndex() - 1) == ')') {
                            parenthesesCounter--;
                        }
                        if (parenthesesCounter == 0) {
                            break;
                        }
                        bracketBuffer.append(currRegEx);
                        if (iterator.hasNext()) currRegEx = iterator.next();
                        else break;
                    }
                    // remove ')'
                    bracketBuffer.deleteCharAt(bracketBuffer.length() - 1);

                    List<Part> bracketParts = new ArrayList<>();

                    // check parenthesis post fix for '*' or '+'
                    while (iterator.hasNext()) {
                        if (currRegEx == '*' || currRegEx == '+') {
                            parenthesisPostfix = currRegEx;
                            break;
                        } else if (currRegEx != ' ') {
                            parenthesisPostfix = iterator.previous();
                            break;
                        }
                        currRegEx = iterator.next();
                    }
                    bracketParts.add(
                            partFactory.createGroupPart(bracketBuffer.toString(), parenthesisPostfix)
                    );
                    temp.addAll(bracketParts);
                }
                // Combine all characters after brackets
                else {
                    buffer = new StringBuilder();
                    while (iterator.hasNext() && regExStream.get(iterator.nextIndex()) != '(') {
                        buffer.append(currRegEx);
                        currRegEx = iterator.next();
                        if (currRegEx == '(') {
                            buffer.append(currRegEx);
                            Part part1 = partFactory.createNoOpPart(buffer.toString());
                            temp.add(part1);
                            break;
                        } else if (!iterator.hasNext()) {
                            buffer.append(currRegEx);
                            Part part1 = partFactory.createNoOpPart(buffer.toString());
                            temp.add(part1);
                            break;
                        }
                    }
                }
            }
            toReturn.add(temp);
        }

        return toReturn;
    }

    public static String replaceRange(String string) {

        char[] chars = (string + " | ").toCharArray();
        StringBuilder toReturn = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '-') {
                int j = i-1;
                int k = i+1;
                while (chars[j] == ' ') {
                    j--;
                }
                while (chars[k] == ' ') {
                    k++;
                }

                // note that we loop over chars[j] + 1 to chars[k] - 1
                // because chars[j] and chars[k] are already added
                // For Example: 0 - 4 would be turned into 0 + 1 | 2 | 3 | + 4
                int l;
                toReturn.append(" | ");
                for (l = chars[j]+1; l < chars[k]; l++) {
                    toReturn.append(Character.toString(l)).append(" | ");
                }
            }
            else {
                toReturn.append(chars[i]);
            }
        }

        return toReturn.toString();
    }

    // Top level visualization
    // This visualization does not include definitions
    // Expressions ending '+' and '*' are displayed as NOOP
    //      since they are not handled yet
    public static void visualizeTopLevel(String regEx, PartFactory partFactory) {
        List<List<Part>> partsOfParts = findGroupedParts(
                findORedParts(regEx, partFactory),
                partFactory
        );

        for ( List<Part> parts: partsOfParts) {
            // exp with different number will be ORed together
            // this is used for debugging only
            for (Part part1: parts) {
                System.out.println(part1);
            }
        }
    }
}
