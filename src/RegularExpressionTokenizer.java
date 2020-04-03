import java.util.*;
import java.util.stream.Collectors;

class RegularExpressionTokenizer {

    private final PartFactory partFactory;
    private final Map<String, String> regularDefinitions;


    public RegularExpressionTokenizer(String key, Map<String, String> regularDefinitions,
                                      Set<String> keyWords, Set<String> punctuation) {
        System.out.println("===" + key + "===");
        this.partFactory = new PartFactory(regularDefinitions, keyWords, punctuation);
        this.regularDefinitions = regularDefinitions;
    }

    public NFA toNFA(String regex) {
        List<List<Part>> andEdPartsList =  tokenizeParts(
                tokenizeParenthesis(regex + " ")
        );
        NFA nfa = new NFA();
        for ( List<Part> operationsList: andEdPartsList) {
            // NOTE: expressions with the same random number will be ORed together
            // this is only used for debugging
            int x =  (new Random()).nextInt(1000);
            List<NFA> edgesList = new ArrayList<>();
            for (Part e : operationsList) {

                // Recursively convert group Part to NFA
                if (e.isGroup()) {
                    if (e.isAndGroup()) {
                        if (e.isAsterisk()) {
                            NFA groupNfa = toNFA(e.getExpression());
                            groupNfa = groupNfa.asterisk(groupNfa);
                            groupNfa = groupNfa.concatenate(edgesList);
                            return groupNfa;
                        } else if (e.isPlus()) {
                            NFA groupNfa = toNFA(e.getExpression());
                            groupNfa = groupNfa.plus(groupNfa);
                            groupNfa = groupNfa.concatenate(edgesList);
                            return groupNfa;
                        }
                    } else {
                        if (e.isAsterisk()) {
                            NFA groupNfa = toNFA(e.getExpression());
                            groupNfa = groupNfa.asterisk(groupNfa);
                            groupNfa = groupNfa.or(edgesList);
                            return groupNfa;
                        } else if (e.isPlus()) {
                            NFA groupNfa = toNFA(e.getExpression());
                            groupNfa = groupNfa.plus(groupNfa);
                            groupNfa = groupNfa.or(edgesList);
                            return groupNfa;
                        }
                    }

                    System.out.println(x + " " + e.toString());
                    return toNFA(e.getExpression());
                }

                String [] ANDedExpressions = e.getExpression().split(" ");
                System.out.println(x + " " + Arrays.toString(ANDedExpressions));
                // Part contains ANDed expressions
                // create a list of NFAs and concatenate them at the end
                if (ANDedExpressions.length > 1) {
                    List<NFA> nfas = new ArrayList<>();
                    for (String exp : ANDedExpressions) {
                        System.out.println();
                        Part part = partFactory.createPart(exp);
                        System.out.println("        "  + x + " " + part.toString());

                        if (part.isAsterisk()) {
                            if (e.isDefinition()) {
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression())));
//                                NFA edgeNfa = toNFA(e.getExpression());
                                nfas.add(nfa.asterisk(edgeNfa));
                            } else {
                                NFA edgeNfa = nfa.edgeNfa(e.getExpression());
                                nfas.add(nfa.asterisk(edgeNfa));
                            }
                        } else if (part.isPlus()) {
                            if (e.isDefinition()) {
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression())));
                                nfas.add(nfa.plus(edgeNfa));
                            } else {
                                NFA edgeNfa = nfa.edgeNfa(e.getExpression());
                                nfas.add(nfa.plus(edgeNfa));
                            }
                        } else {
                            if (part.isDefinition()) {
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(part.getExpression().trim())));
                                nfas.add(edgeNfa);
                            } else {
                                NFA edgeNfa = nfa.edgeNfa(e.getExpression());
                                nfas.add(edgeNfa);
                            }
                        }
                    }
                    edgesList.addAll(nfas);
                }
                // Part does not contain ANDed expressions.
                // Add a new edge to nfaList and perform NFA OR
                else if (!e.getExpression().isEmpty()) {
                        System.out.println(x + " " + e.toString());
                        if (e.isAsterisk()) {
                            if (e.isDefinition()) {
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(e.getExpression())));
                                edgesList.add(edgeNfa);
                            } else {
                                NFA edgeNfa = nfa.edgeNfa(e.getExpression());
                                edgesList.add(
                                        nfa.asterisk(edgeNfa));
                            }
                        } else if (e.isPlus()) {
                            if (e.isDefinition()) {
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(e.getExpression())));
                                edgesList.add(edgeNfa);
                            } else {
                                NFA edgeNfa = nfa.edgeNfa(e.getExpression());
                                edgesList.add(
                                        nfa.plus(edgeNfa));
                            }
                        } else {
                            if (e.isDefinition()) {
                                NFA edgeNfa = toNFA(replaceRange(this.regularDefinitions.get(e.getExpression())));
                                edgesList.add(edgeNfa);
                            } else {
                                NFA edgeNfa = nfa.edgeNfa(e.getExpression());
                                edgesList.add(edgeNfa);
                            }

                        }
                    nfa = nfa.or(edgesList);
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

        return toReturn.append(" ").toString();
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
                if (iterator.hasNext()) {
                    parenthesisPostfix = regExStream.get(iterator.nextIndex());
                }
                bracketParts.add(
                        partFactory.createGroupPart(bracketBuffer.toString(), parenthesisPostfix, isAndParenthesis)
                );
                toReturn.addAll(bracketParts);
            } else {
                // Combine all characters after brackets
                buffer.append(currRegEx);
                if (!iterator.hasNext()) {
                    toReturn.add(
                            partFactory.createPart(buffer.toString())
                    );
                }
            }
        }

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
            }
            toReturn.add(buffer);
        }

//        toReturn.forEach(e -> e.forEach(a -> System.out.println(a.toString())));
        return toReturn;
    }
}
