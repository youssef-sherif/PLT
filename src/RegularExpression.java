import java.util.*;
import java.util.stream.Collectors;

class RegularExpression {

    private final PartFactory partFactory;
    private final Map<String, String> regularDefinitions;
    private final String regEx;
    private String key;


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
         - replace all '\' (backslash) with ' \' (space backslash) to be able to concatenate them together
         */

        String regEx = " | " + regExString + " | ";
        if (regEx.contains("\\")) {
            regEx = regEx.replaceAll("\\\\", " \\\\");
        }

        return regEx;
    }

    public NFA toNFA() {
        return toNFA(preProcess(this.regEx));
    }

    public NFA toNFA(String regExString) {

        List<Part> oRedParts = tokenizeOrs(regExString);
        List<NFA> edgesList = new ArrayList<>();
        NFA nfa = NFA.getInstance();

        for (Part part : oRedParts) {
            System.out.println(part.toString());
            if (part.isAsterisk()) {
                // if part is a definitions recursively convert it to NFA
                if (part.isDefinition()) {
                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression())));
                    edgesList.add(edgeNfa.asterisk(edgeNfa));
                } else {
                    NFA edgeNfa = toNFA(tokenizeParts(tokenizeParenthesis(part.getExpression())));
                    edgesList.add(edgeNfa.asterisk(edgeNfa));
                }
            } else if (part.isPlus()) {
                // if part is a definitions recursively convert it to NFA
                if (part.isDefinition()) {
                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression())));
                    edgesList.add(edgeNfa.plus(edgeNfa));
                } else {
                    NFA edgeNfa = toNFA(tokenizeParts(tokenizeParenthesis(part.getExpression())));
                    edgesList.add(edgeNfa.plus(edgeNfa));
                }
            } else {
                if (part.isDefinition()) {
                    // if part is a definitions recursively convert it to NFA
                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression())));
                    edgesList.add(edgeNfa);
                } else {
                    NFA edgeNfa = toNFA(tokenizeParts(tokenizeParenthesis(part.getExpression())));
                    edgesList.add(edgeNfa);
                }
            }
        }
        nfa = nfa.or(edgesList);

        return nfa;
    }

    private NFA toNFA(List<List<Part>> andEdPartsList) {
        NFA nfa = NFA.getInstance();
        for (List<Part> parts: andEdPartsList)
        for ( Part part: parts) {
            // NOTE: expressions with the same random number will be ORed together
            // this is only used for debugging
            int x =  (new Random()).nextInt(1000);
//            operationsList.forEach(e -> System.out.println(e.toString()));
            List<NFA> edgesList = new ArrayList<>();

            // Recursively convert group Part to NFA
            if (part.isGroup()) {
                System.out.println(part.toString());
                NFA groupNfa = toNFA(part.getExpression());
                if (part.isAsterisk()) {
                    groupNfa = groupNfa.asterisk(groupNfa);
                } else if (part.isPlus()) {
                    groupNfa = groupNfa.plus(groupNfa);
                }
                edgesList.add(groupNfa);
            } else {

                String[] andEdExpressions = part.getExpression().split(" ");

                System.out.println(x + " " + Arrays.toString(andEdExpressions));
                // Part contains ANDed expressions
                // create a list of NFAs and concatenate them at the end
                List<NFA> andEdNFAs = new ArrayList<>();
                for (String exp : andEdExpressions) {
                    if (exp.isEmpty()) continue;
                    Part andEdPart = partFactory.createPart(exp);
                    System.out.println("        "  + x + " " + andEdPart.toString());

                    if (andEdPart.isAsterisk()) {
                        // if part is a definitions recursively convert it to NFA
                        if (andEdPart.isDefinition()) {
                            NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                            andEdNFAs.add(nfa.asterisk(edgeNfa));
                        } else {
                            // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                            NFA edgeNfa = nfa.edge(andEdPart.getNFACharacter());
                            andEdNFAs.add(nfa.asterisk(edgeNfa));
                        }
                    } else if (andEdPart.isPlus()) {
                        // if part is a definitions recursively convert it to NFA
                        if (andEdPart.isDefinition()) {
                            NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                            andEdNFAs.add(nfa.plus(edgeNfa));
                        } else {
                            // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                            NFA edgeNfa = nfa.edge(andEdPart.getNFACharacter());
                            andEdNFAs.add(nfa.plus(edgeNfa));
                        }
                    } else {
                        if (andEdPart.isDefinition()) {
                            // if part is a definitions recursively convert it to NFA
                            NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                            andEdNFAs.add(edgeNfa);
                        } else {
                            // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                            NFA edgeNfa = nfa.edge(andEdPart.getNFACharacter());
                            andEdNFAs.add(edgeNfa);
                        }
                    }
                }
                edgesList.addAll(andEdNFAs);
                nfa.concatenate(edgesList);
            }
        }

        return nfa;
    }

    private String replaceRange(String string) {

        string += " | ";
        char[] chars = string.toCharArray();
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


    private List<Part> tokenizeOrs(String regExString) {
        List<Character> regExStream = regExString.chars()
                // Convert IntStream to Stream<Character>
                .mapToObj(e -> (char)e)
                // Collect the elements as a List Of Characters
                .collect(Collectors.toList());

        ListIterator<Character> iterator = regExStream.listIterator();
        StringBuilder buffer = new StringBuilder();
        List<Part> parts = new ArrayList<>();

        boolean ignoring = false;
        while (iterator.hasNext()) {
            char curr = iterator.next();

            buffer.append(curr);

            if (curr == '(') ignoring = true;
            if (curr == ')') ignoring = false;
            if ((curr == '|' || !iterator.hasNext())
                    && !ignoring) {
                buffer.deleteCharAt(buffer.length()-1);
                Part part = partFactory.createNoOpPart(buffer.toString());
                parts.add(part);
                buffer = new StringBuilder();
            }
        }

        return parts;
    }

    private List<Part> tokenizeParenthesis(String regExString) {

        List<Character> regExStream = regExString.chars()
                // Convert IntStream to Stream<Character>
                .mapToObj(e -> (char)e)
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
                    }
                    else if (regExStream.get(iterator.previousIndex()-1) == ')') {
                        parenthesesCounter--;
                    }
                    if (parenthesesCounter == 0){
                        break;
                    }
                    bracketBuffer.append(currRegEx);
                    if (iterator.hasNext()) currRegEx = iterator.next();
                    else break;
                }
                // remove ')'
                List<Part> bracketParts = new ArrayList<>();

                // check parenthesis post fix for '*' or '+'
                while (iterator.hasNext()) {
                    if ( currRegEx == '*' || currRegEx == '+' ) {
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
                        Part part = partFactory.createNoOpPart(buffer.toString());
                        toReturn.add(part);
                        break;
                    } else if (!iterator.hasNext()) {
                        buffer.append(currRegEx);
                        Part part = partFactory.createNoOpPart(buffer.toString());
                        toReturn.add(part);
                        break;
                    }
                }
            }
        }

//        System.out.println("====================================");
//        toReturn.forEach(e -> System.out.println(e.toString()));
//        System.out.println("====================================");
        return toReturn;
    }


    private List<List<Part>> tokenizeParts(List<Part> tokens) {

        List<List<Part>> toReturn = new ArrayList<>();
        List<Part> buffer = new ArrayList<>();

        for (Part part : tokens) {

            if (!part.isGroup()) {
                // fill buffer until we reach split.
                buffer.add(partFactory.createNoOpPart(part.getExpression()));
            }
            // Part is a group Part. We will deal with it as a single part.
            else {
//                System.out.println(part.toString());
                buffer.add(part);
                buffer = new ArrayList<>();
            }
            toReturn.add(buffer);
        }

//        System.out.println("====================================");
//        toReturn.forEach(e -> e.forEach(a -> System.out.println(a.toString())));
//        System.out.println("====================================");
        return toReturn;
    }
}