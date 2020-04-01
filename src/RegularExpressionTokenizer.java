import java.util.*;

class RegularExpressionTokenizer {

    private final PartFactory partFactory;
    private String key;


    public RegularExpressionTokenizer(String key, Set<String> regularDefinitionsNames, Set<String> keyWords, Set<String> punctuation) {
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
            // NOTE: expressions with the same random number will be ANDed together
            int x =  (new Random()).nextInt(1000);
            List<NFA> edgesList = new ArrayList<>();
            for (Part e : operationsList) {

                if (e.isGroup()) {
                    System.out.println(e.toString());
                    return toNFA(e.getExpression());
                }
                // TODO: use character level parsing instead to avoid repeating this again...
                String [] ORedExpressions = e.getExpression().split("\\|");
                // Part contains ORed expressions
                // create a list of NFAs and or them
                if (ORedExpressions.length > 1) {
                    List<NFA> nfas = new ArrayList<>();
                    for (String exp : ORedExpressions) {
                        Part part = partFactory.createPart(exp);
                        System.out.print(part.toString() + " OR ");
                        NFA edgeNfa = nfa.edgeNfa(part.getExpression());
                        if (part.isAsterisk()) {
                            nfas.add(nfa.asterisk(edgeNfa));
                        } else if(part.isPlus()) {
                            nfas.add(nfa.plus(edgeNfa));
                        } else {
                            nfas.add(edgeNfa);
                        }
                    }
                    nfa = nfa.or(nfas);
                    System.out.println();
                }
                // Part does not contain ORed expressions.
                // create a new edge
                else {
                    System.out.println(x + " " + e.toString());
                    NFA edgeNfa = nfa.edgeNfa(e.getExpression());
                    if (e.isAsterisk()) {
                        edgesList.add(
                                nfa.asterisk(edgeNfa));
                    } else if (e.isAsterisk()) {
                        edgesList.add(
                                nfa.plus(edgeNfa));
                    } else {
                        edgesList.add(edgeNfa);
                    }
                }
                nfa = nfa.concatenate(edgesList);
            }
            nfa = nfa.or(edgesList);
        }

        return nfa;
    }

    private List<Part> tokenizeParenthesis(String regExString) {

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

                // TODO: use stack or a counter? to handle parenthesis within other parenthesis
                while (!currRegEx.contains(")")) {
                    bracketBuffer.append(" ").append(currRegEx);
                    currRegEx = iterator.next();
                }
                // remove ')'
                String regExInsideBracket = currRegEx.substring(0, currRegEx.indexOf(")"));
                bracketBuffer.append(" ").append(regExInsideBracket);

                List<Part> bracketParts = new ArrayList<>();
                String bracketGroup = bracketBuffer.toString();
                bracketParts.add(
                        partFactory.createGroupPart(bracketGroup, currRegEx)
                );
                toReturn.addAll(bracketParts);
            } else {
                // NOOP because we are still not sure what it is but it is not inside parenthesis
                toReturn.add(
                        partFactory.createPart(currRegEx)
                );
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

                // fill buffer until we reach split. That means we should OR all ANDS together.
                if (ORedExpressions.length == 0) {
                    toReturn.add(buffer);
                    buffer = new ArrayList<>();
                }
                else {
                    buffer.add(part);
                }
            }
            // Part is a createGroupPart. We will deal with it as a single part.
            else {
                buffer.add(part);
            }
        }
        return toReturn;
    }
}
