import java.util.*;
import java.util.stream.Collectors;

class RegularExpression {

    private final PartFactory partFactory;
    private final Map<String, String> regularDefinitions;
    private String key;


    public RegularExpression(String key, Map<String, String> regularDefinitions,
                             Set<String> keyWords,
                             Set<String> punctuation) {
        System.out.println("===" + key + "===");
        this.key = key;
        this.partFactory = new PartFactory(regularDefinitions, keyWords, punctuation);
        this.regularDefinitions = regularDefinitions;
    }

    public NFA toNFA(String regex) {
        List<List<Part>> andEdPartsList =  tokenize(regex);
        NFA nfa = NFA.getInstance();
//        andEdPartsList.forEach(e -> e.forEach(a -> System.out.println(a.toString())));
        for ( List<Part> operationsList: andEdPartsList) {
            // NOTE: expressions with the same random number will be ORed together
            // this is only used for debugging
            int x =  (new Random()).nextInt(1000);
//            operationsList.forEach(e -> System.out.println(e.toString()));
            List<NFA> edgesList = new ArrayList<>();
            for (Part part : operationsList) {

                // Recursively convert group Part to NFA
                if (part.isGroup()) {
//                    System.out.println(part.toString());
                    NFA groupNfa = toNFA(part.getExpression());
                    if (part.isAndGroup()) {
                        if (part.isAsterisk()) {
                            groupNfa = groupNfa.asterisk(groupNfa);
                        } else if (part.isPlus()) {
                            groupNfa = toNFA(part.getExpression());
                            groupNfa = groupNfa.plus(groupNfa);
                            edgesList.add(groupNfa);
                        }
                        groupNfa = groupNfa.concatenate(edgesList);
                        edgesList.add(groupNfa);
                    } else if (part.isOrGroup()){
                        if (part.isAsterisk()) {
                            groupNfa = toNFA(part.getExpression());
                            groupNfa = groupNfa.asterisk(groupNfa);
                        } else if (part.isPlus()) {
                            groupNfa = groupNfa.plus(groupNfa);
                        }
                        groupNfa = groupNfa.or(edgesList);
                        edgesList.add(groupNfa);
                    } else {
                        if (part.isAsterisk()) {
                            groupNfa = groupNfa.asterisk(groupNfa);
                        } else if (part.isPlus()) {
                            groupNfa = groupNfa.plus(groupNfa);
                        }
                        edgesList.add(groupNfa);
                    }
                } else {

                    String[] ANDedExpressions = part.getExpression().split(" ");
//                    System.out.println(Arrays.toString(ANDedExpressions));
                    // Part contains ANDed expressions
                    // create a list of NFAs and concatenate them at the end
                    if (ANDedExpressions.length > 1) {
                        List<NFA> ANDedNFAs = new ArrayList<>();
                        for (String exp : ANDedExpressions) {

                            Part ANDedPart = partFactory.createPart(exp);
//                            System.out.println("        "  + x + " " + ANDedPart.toString());

                            if (ANDedPart.isAsterisk()) {
                                // if part is a definitions recursively convert it to NFA
                                if (ANDedPart.isDefinition()) {
                                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(ANDedPart.getExpression())));
                                    ANDedNFAs.add(nfa.asterisk(edgeNfa));
                                } else {
                                    // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                                    NFA edgeNfa = nfa.edge(ANDedPart.getExpression().charAt(0));
                                    ANDedNFAs.add(nfa.asterisk(edgeNfa));
                                }
                            } else if (ANDedPart.isPlus()) {
                                // if part is a definitions recursively convert it to NFA
                                if (ANDedPart.isDefinition()) {
                                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(ANDedPart.getExpression())));
                                    ANDedNFAs.add(nfa.plus(edgeNfa));
                                } else {
                                    // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                                    NFA edgeNfa = nfa.edge(ANDedPart.getExpression().charAt(0));
                                    ANDedNFAs.add(nfa.plus(edgeNfa));
                                }
                            } else {
                                if (ANDedPart.isDefinition()) {
                                    // if part is a definitions recursively convert it to NFA
                                    NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(ANDedPart.getExpression())));
                                    ANDedNFAs.add(edgeNfa);
                                } else {
                                    // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                                    NFA edgeNfa = nfa.edge(ANDedPart.getNFACharacter());
                                    ANDedNFAs.add(edgeNfa);
                                }
                            }
                        }
                        edgesList.addAll(ANDedNFAs);
                        if (!edgesList.isEmpty()) {
                            nfa = nfa.concatenate(edgesList);
                        }

                    }
                    // Part does not contain ANDed expressions.
                    // Add a new edge to nfaList and perform NFA OR
                    else if (!ANDedExpressions[0].isEmpty()) {
//                        System.out.println(x + " " + part.toString());
                        if (part.isAsterisk()) {
                            // if part is a definitions recursively convert it to NFA
                            if (part.isDefinition()) {
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression())));
                                edgesList.add(edgeNfa.asterisk(edgeNfa));
                            } else {
                                // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                                NFA edgeNfa = nfa.edge(part.getExpression().charAt(0));
                                edgesList.add(edgeNfa.asterisk(edgeNfa));
                            }
                        } else if (part.isPlus()) {
                            // if part is a definitions recursively convert it to NFA
                            if (part.isDefinition()) {
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression())));
                                edgesList.add(edgeNfa.plus(edgeNfa));
                            } else {
                                // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                                NFA edgeNfa = nfa.edge(part.getExpression().charAt(0));
                                edgesList.add(edgeNfa.plus(edgeNfa));
                            }
                        } else {
                            if (part.isDefinition()) {
                                // if part is a definitions recursively convert it to NFA
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression())));
                                edgesList.add(edgeNfa);
                            } else {
                                // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                                NFA edgeNfa = nfa.edge(part.getNFACharacter());
                                edgesList.add(edgeNfa);
                            }

                        }
                        if(!edgesList.isEmpty()) {
                            nfa = nfa.or(edgesList);
                        }
                    }
                }
            }

        }

        return nfa;
    }

    private String replaceRange(String string) {

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

//        System.out.println(toReturn);
        return toReturn.toString();
    }

    private List<List<Part>> tokenize(String regExString) {
        /*
         simple pre-processing
         - append | to beginning (this does not change the meaning of the RegEx but it makes it work for RegExes
         containing 1 component)
         - replace all '\' (backslash) with ' \' (space backslash) to be able to concatenate them together
         */
        String regEx = " | " + regExString;
        if (regEx.contains("\\")) {
            regEx = regEx.replaceAll("\\\\", " \\\\");
        }
        return tokenizeParts(tokenizeParenthesis(regEx));
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

                // find out if the parenthesis is ANDed or ORed to previous.
                boolean isAndParenthesis = true;
                int i = 1;
                if (iterator.previousIndex()-1 > 0)
                    do {
                        if (regExStream.get(iterator.previousIndex()-i) == '|'
                                || regExStream.get(iterator.previousIndex()-i-1) == '|') {
                            isAndParenthesis = false;
                        }
                        i++;
                    } while (regExStream.get(iterator.previousIndex()-i) == ' ');

                // Combine all characters before '('
                toReturn.add(
                        partFactory.createPart(buffer.toString())
                );
                buffer = new StringBuilder();

                // skip '('
                currRegEx = iterator.next();

                parenthesesCounter++;
                while (true) {
                    if (currRegEx == '(') {
                        parenthesesCounter++;
                    }
                    else if (currRegEx == ')') {
                        parenthesesCounter--;
                    }
                    if (parenthesesCounter == 0){
                        break;
                    }
                    bracketBuffer.append(currRegEx);
                    currRegEx = iterator.next();
                }
                // remove ')'
                List<Part> bracketParts = new ArrayList<>();

                // check parenthesis post fix for '*' or '+'
                while (iterator.hasNext()) {
                    currRegEx = iterator.next();
                    if ( currRegEx == '*' || currRegEx == '+' ) {
                        parenthesisPostfix = currRegEx;
                        break;
                    } else if (currRegEx != ' ') {
                        parenthesisPostfix = iterator.previous();
                        break;
                    }
                }
                bracketParts.add(
                        partFactory.createGroupPart(bracketBuffer.toString(), parenthesisPostfix, isAndParenthesis)
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

//        toReturn.forEach(e -> System.out.println(e.toString()));
        return toReturn;
    }


    private List<List<Part>> tokenizeParts(List<Part> tokens) {

        List< List<Part>> toReturn = new ArrayList<>();
        List<Part> buffer = new ArrayList<>();

        for (Part part : tokens) {

            if (!part.isGroup()) {
                String[] ORedExpressions = part.getExpression().split("\\|");

                buffer = new ArrayList<>();
                // fill buffer until we reach split.
                for (String exp : ORedExpressions) {
                    buffer.add(partFactory.createPart(exp));
                }
            }
            // Part is a group Part. We will deal with it as a single part.
            else {
//                System.out.println(part.toString());
                buffer.add(part);
                buffer = new ArrayList<>();
            }
            toReturn.add(buffer);
        }

//        toReturn.forEach(e -> e.forEach(a -> System.out.println(a.toString())));
        return toReturn;
    }
}