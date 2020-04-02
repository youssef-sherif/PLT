import java.util.*;
import java.util.stream.Collectors;

class RegularExpressionTokenizer {

    private final PartFactory partFactory;
    private String key;


    public RegularExpressionTokenizer(String key, Map<String, String> regularDefinitionsNames,
                                      Set<String> keyWords, Set<String> punctuation) {
        this.key = key;
        System.out.println("===" + this.key + "===");
        this.partFactory = new PartFactory(regularDefinitionsNames, keyWords, punctuation);
    }

    public NFA toNFA(String regex) {
        List<List<Part>> andEdPartsList =  tokenizeParts(
                tokenizeParenthesis(regex)
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
                            System.out.println(e.toString());
                            NFA groupNfa = toNFA(e.getExpression());
                            groupNfa = groupNfa.asterisk(groupNfa);
                            groupNfa = groupNfa.concatenate(edgesList);
                            return groupNfa;
                        } else if (e.isPlus()) {
                            System.out.println(e.toString());
                            NFA groupNfa = toNFA(e.getExpression());
                            groupNfa = groupNfa.plus(groupNfa);
                            groupNfa = groupNfa.concatenate(edgesList);
                            return groupNfa;
                        }
                    } else {
                        if (e.isAsterisk()) {
                            System.out.println(e.toString());
                            NFA groupNfa = toNFA(e.getExpression());
                            groupNfa = groupNfa.asterisk(groupNfa);
                            groupNfa = groupNfa.or(edgesList);
                            return groupNfa;
                        } else if (e.isPlus()) {
                            System.out.println(e.toString());
                            NFA groupNfa = toNFA(e.getExpression());
                            groupNfa = groupNfa.plus(groupNfa);
                            groupNfa = groupNfa.or(edgesList);
                            return groupNfa;
                        }
                    }

                    System.out.println(e.toString());
                    return toNFA(e.getExpression());
                }
                String [] ANDedExpressions = e.getExpression().trim().split(" ");
                // Part contains ANDed expressions
                // create a list of NFAs and concatenate them at the end
                if (ANDedExpressions.length > 1) {
                    List<NFA> nfas = new ArrayList<>();
                    System.out.print(x);
                    for (String exp : ANDedExpressions) {
                        Part part = partFactory.createPart(exp);
                        System.out.print(" " + part.toString() + " AND");
                        NFA edgeNfa = nfa.edgeNfa(part.getExpression());
                        if (part.isAsterisk()) {
                            nfas.add(nfa.asterisk(edgeNfa));
                        }
                        else if(part.isPlus()) {
                            nfas.add(nfa.plus(edgeNfa));
                        }
                        else {
                            nfas.add(edgeNfa);
                        }
                    }
                    nfa = nfa.concatenate(nfas);
                    System.out.println();
                }
                // Part does not contain ANDed expressions.
                // Add a new edge to nfaList and perform NFA OR
                else {
                    System.out.println(x + " " + e.toString());
                    NFA edgeNfa = nfa.edgeNfa(e.getExpression());
                    if (e.isAsterisk()) {
                        edgesList.add(
                                nfa.asterisk(edgeNfa));
                    }
                    else if (e.isPlus()) {
                        edgesList.add(
                                nfa.plus(edgeNfa));
                    }
                    else {
                        edgesList.add(edgeNfa);
                    }
                    nfa = nfa.or(edgesList);
                }
            }
        }

        return nfa;
    }

    private List<Part> tokenizeParenthesis(String regExString) {

        List<Character> regExStream = (regExString + " ").chars()
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
                char parenthesisPostfix;

                // find out if the parenthesis is ANDed or ORed to previous.
                boolean isAndParenthesis = true;
                int i = 1;
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
                parenthesisPostfix = regExStream.get(iterator.nextIndex());
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
