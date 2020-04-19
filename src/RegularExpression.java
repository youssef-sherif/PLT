import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class RegularExpression {

    private final PartFactory partFactory;
    private final Map<String, String> regularDefinitions;
    private final String regEx;
    private final String key;


    public RegularExpression(String key,
                             String regEx,
                             Map<String, String> regularDefinitions,
                             Set<String> keyWords,
                             Set<String> punctuation) {
        this.regEx = regEx;
        this.key = key;
        this.partFactory = new PartFactory(regularDefinitions, keyWords, punctuation);
        this.regularDefinitions = regularDefinitions;
    }

    public String preProcess(String regExString) {
        System.out.println("===" + key + "===");
        /*
         simple pre-processing
         - append | to beginning (this does not change the meaning of the RegEx but it makes it work for RegExes
         containing 1 component)
         - replace all '\' (backslash) with ' \' (space backslash) to be able to combineNFAsConcatenate them together
         */

        String regEx = " | " + regExString + " | ";
        if (regEx.contains("\\")) {
            regEx = regEx.replaceAll("\\\\", " \\\\");
        }

        return regEx;
    }

    public NFA toNFA() {
        String regEx = preProcess(this.regEx);
        return toNFA(regEx);
    }

    public NFA toNFA(String regExString) {

        List<Part> oRedPartsList = findORedParts(regExString);
        List<NFA> edgesList = new ArrayList<>();

        NFA nfa = null;
        
        for ( Part part: oRedPartsList) {
            // exp with different number will be ORed together
            // this is used for debugging only
            int x = (new Random()).nextInt(1000);
            List<Part> groupedParts = findGroupedParts(part.getExpression());
            List<NFA> concatenatedNFAs = new ArrayList<>();
            for (Part part1: groupedParts) {
                if (part1.isGroup()) {
                    // Recursively convert group Part to NFA
                    NFA groupNfa = toNFA(preProcess(part1.getExpression()));
                    if (part1.isAsterisk()) {
                        concatenatedNFAs.add(groupNfa.asterisk());
                    } else if (part1.isPlus()) {
                        NFA groupNfa2 = toNFA(preProcess(part1.getExpression()));
                        concatenatedNFAs.add(groupNfa);
                        concatenatedNFAs.add(groupNfa2.asterisk());
                    } else {
                        concatenatedNFAs.add(groupNfa);
                    }
                } else {
                    concatenatedNFAs.addAll(getConcatenatedNFAsList(part1.getExpression()));
                }
            }

            if (!concatenatedNFAs.isEmpty()) {
                edgesList.add(NFA.combineNFAsConcatenate(concatenatedNFAs));
            }
        }

        if (!edgesList.isEmpty()) {
             nfa = NFA.combineNFAsOr(edgesList);
        }

        NFA.getInstance().getFinalState().setRuleName(this.key);

        return nfa;
    }

    private List<NFA> getConcatenatedNFAsList(String expression) {

        // TODO: handle special characters like '\L'
        List<NFA> andEdNFAs = new ArrayList<>();

        List<Character> regExStream = expression.chars()
                // Convert IntStream to Stream<Character>
                .mapToObj(e -> (char)e)
                // Collect the elements as a List Of Characters
                .collect(Collectors.toList());

        ListIterator<Character> iterator = regExStream.listIterator();
        StringBuilder buffer = new StringBuilder();

        while (iterator.hasNext()) {
            char currRegEx = iterator.next();

            if (currRegEx == '\\') {
                // escape character
                currRegEx = iterator.next();
                buffer.append(currRegEx);
            }

            else if (currRegEx == '*') {
                String exp = buffer.toString();

                if (exp.isEmpty()) {
                    buffer = new StringBuilder();
                    continue;
                }

                Part andEdPart = partFactory.createPart(exp);

                // if part is a definitions recursively convert it to NFA
                if (andEdPart.isDefinition()) {
                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                    andEdNFAs.add(edgeNfa.asterisk());
                } else {
                    // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                    for (char nfaChar : andEdPart.getExpression().toCharArray()) {
                        NFA edgeNfa = NFA.edge(nfaChar);
                        andEdNFAs.add(edgeNfa.asterisk());
                        andEdNFAs.add(NFA.edge(NFA.EPSILON));
                    }
                }

                andEdNFAs.add(NFA.edge(NFA.EPSILON));

                buffer = new StringBuilder();
            }
            else if (currRegEx == '+') {
                String exp = buffer.toString();

                if (exp.isEmpty()) {
                    buffer = new StringBuilder();
                    continue;
                }

                Part andEdPart = partFactory.createPart(exp);

                // if part is a definitions recursively convert it to NFA
                if (andEdPart.isDefinition()) {
                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                    NFA edgeNfa2 = toNFA(replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                    andEdNFAs.add(edgeNfa);
                    andEdNFAs.add(edgeNfa2.asterisk());
                } else {
                    // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                    for (char nfaChar : andEdPart.getExpression().toCharArray()) {
                        NFA edgeNfa = NFA.edge(nfaChar);
                        NFA edgeNfa2 = NFA.edge(nfaChar);
                        andEdNFAs.add(edgeNfa);
                        andEdNFAs.add(edgeNfa2.asterisk());
                        andEdNFAs.add(NFA.edge(NFA.EPSILON));
                    }
                }

                andEdNFAs.add(NFA.edge(NFA.EPSILON));

                buffer = new StringBuilder();
            }

            else if (currRegEx != ' ') {
                buffer.append(currRegEx);
            }

            else {
                String exp = buffer.toString();

                if (exp.isEmpty()) {
                    buffer = new StringBuilder();
                    continue;
                }

                Part andEdPart = partFactory.createPart(exp);

                if (andEdPart.isDefinition()) {
                    // if part is a definitions recursively convert it to NFA
                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                    andEdNFAs.add(edgeNfa);
                } else {
                    // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                    for (char nfaChar : andEdPart.getExpression().toCharArray()) {
                        NFA edgeNfa = NFA.edge(nfaChar);
                        andEdNFAs.add(edgeNfa);
                        andEdNFAs.add(NFA.edge(NFA.EPSILON));
                    }
                }

                andEdNFAs.add(NFA.edge(NFA.EPSILON));

                buffer = new StringBuilder();
            }
        }

        return andEdNFAs;
    }

    private String replaceRange(String string) {

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
                toReturn.append(Character.toString(chars[i]));
            }
        }

        return toReturn.toString();
    }


    private List<Part> findORedParts(String regExString) {
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

    private List<Part> findGroupedParts(String regExString) {

        String regExString1 = regExString + " ";
        List<Character> regExStream = regExString1.chars()
                // Convert IntStream to Stream<Character>
                .mapToObj(e -> (char) e)
                // Collect the elements as a List Of Characters
                .collect(Collectors.toList());

        ListIterator<Character> iterator = regExStream.listIterator();
        List<Part> toReturn = new ArrayList<>();

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
                toReturn.add(
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
                bracketBuffer.deleteCharAt(bracketBuffer.length()-1);

                List<Part> bracketParts = new ArrayList<>();

                // check parenthesis post fix for '*' combineNFAsOr '+'
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
                toReturn.addAll(bracketParts);
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
                        toReturn.add(part1);
                        break;
                    } else if (!iterator.hasNext()) {
                        buffer.append(currRegEx);
                        Part part1 = partFactory.createNoOpPart(buffer.toString());
                        toReturn.add(part1);
                        break;
                    }
                }
            }
        }

        return toReturn;
    }
}