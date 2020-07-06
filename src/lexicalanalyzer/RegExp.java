package lexicalanalyzer;

import static lexicalanalyzer.Constants.*;
import static lexicalanalyzer.RegExpHelper.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RegExp {

    private final PartFactory partFactory;
    private final Map<String, String> regularExpressions;
    private final Map<String, String> regularDefinitions;

    public RegExp(LexicalRulesFile lexicalRulesFile) {
        this.regularExpressions = lexicalRulesFile.getRegularExpressions();
        this.regularDefinitions = lexicalRulesFile.getRegularDefinitions();
        this.partFactory = new PartFactory(this.regularDefinitions.keySet());
    }

    public NFA toNFAAll() {
        List<NFA> nfaList = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.regularExpressions.entrySet()) {
            NFA currentNfa = this.toNFAByKey(
                    entry.getKey(),
                    this.preProcess(entry.getValue())
            );
            nfaList.add(currentNfa);
        }
        NFA nfa = NFA.getInstance().or(nfaList);
        nfa.getFinalState().setRuleName("nfa");

        return NFA.getInstance();
    }

    public String preProcess(String regExString) {
        /*
         simple pre-processing
         - append | to beginning (this does not change the meaning of the RegEx but it makes it work for RegExes
         containing 1 component)
         */

        return String.format(" | %s | ", regExString);
    }

    public NFA toNFAByKey(String key, String regEx) {

        List<List<Part>> oRedPartsList = findGroupedParts(
                                            findORedParts(regEx, partFactory),
                                            partFactory
                                         );

        List<NFA> edgesList = new ArrayList<>();

        NFA nfa = NFA.getInstance();
        
        for ( List<Part> parts: oRedPartsList) {
            List<NFA> concatenatedNFAs = new ArrayList<>();
            for (Part part1: parts) {
                if (part1.isGroup()) {
                    // Recursively convert group Part to NFA
                    NFA groupNfa = toNFAByKey(key, preProcess(part1.getExpression()));
                    if (part1.isAsterisk()) {
                        concatenatedNFAs.add(nfa.asterisk(groupNfa));
                    } else if (part1.isPlus()) {
                        NFA groupNfa2 = toNFAByKey(key, preProcess(part1.getExpression()));
                        concatenatedNFAs.add(groupNfa);
                        concatenatedNFAs.add(nfa.asterisk(groupNfa2));
                    } else {
                        concatenatedNFAs.add(groupNfa);
                    }
                } else {
                    concatenatedNFAs.addAll(getConcatenatedNFAsList(nfa, key, part1.getExpression()));
                }
            }

            if (!concatenatedNFAs.isEmpty()) {
                edgesList.add(nfa.concatenate(concatenatedNFAs));
            }
        }

        if (!edgesList.isEmpty()) {
             nfa = nfa.or(edgesList);
        }

        nfa.getFinalState().setRuleName(key);

        return nfa;
    }

    private List<NFA> getConcatenatedNFAsList(NFA nfa, String key, String expression) {

        String expression1 = expression + " ";
        // TODO: handle special characters like '\L'
        List<NFA> andEdNFAs = new ArrayList<>();

        List<Character> regExStream = expression1.chars()
                // Convert IntStream to Stream<Character>
                .mapToObj(e -> (char)e)
                // Collect the elements as a List Of Characters
                .collect(Collectors.toList());

        ListIterator<Character> iterator = regExStream.listIterator();
        StringBuilder buffer = new StringBuilder();

        while (iterator.hasNext()) {
            char currRegEx = iterator.next();

            // escape character starting with '\'
            if (currRegEx == '\\') {
                currRegEx = iterator.next();
                buffer.append(currRegEx);
            }

            // add all characters that are not ' ', '*' or '+' to buffer
            else if (currRegEx != ' '
                    && currRegEx != ASTERISK
                    && currRegEx != PLUS) {
                buffer.append(currRegEx);
            }

            else {
                String exp = buffer.toString().trim();

                if (exp.isEmpty()) {
                    buffer = new StringBuilder();
                    continue;
                }

                if (currRegEx == ASTERISK) {

                    Part andEdPart = partFactory.createPart(exp, ASTERISK);

                    // if part is a definitions recursively convert it to NFA
                    if (andEdPart.isDefinition()) {
                        NFA edgeNfa = toNFAByKey(key, replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                        andEdNFAs.add(nfa.asterisk(edgeNfa));
                    } else {
                        for (char nfaChar : andEdPart.getExpression().toCharArray()) {
                            // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                            NFA edgeNfa = nfa.edge(nfaChar);
                            andEdNFAs.add(nfa.asterisk(edgeNfa));
                            andEdNFAs.add(nfa.edge(EPSILON));
                        }
                    }

                    andEdNFAs.add(nfa.edge(EPSILON));

                    buffer = new StringBuilder();
                } else if (currRegEx == PLUS) {

                    Part andEdPart = partFactory.createPart(exp, PLUS);

                    // if part is a definitions recursively convert it to lexicalanalyzer.NFA
                    if (andEdPart.isDefinition()) {
                        NFA edgeNfa = toNFAByKey(key, replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                        NFA edgeNfa2 = toNFAByKey(key, replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                        andEdNFAs.add(edgeNfa);
                        andEdNFAs.add(nfa.asterisk(edgeNfa2));
                    } else {
                        for (char nfaChar : andEdPart.getExpression().toCharArray()) {
                            // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                            NFA edgeNfa = nfa.edge(nfaChar);
                            NFA edgeNfa2 = nfa.edge(nfaChar);
                            andEdNFAs.add(edgeNfa);
                            andEdNFAs.add(nfa.asterisk(edgeNfa2));
                            andEdNFAs.add(nfa.edge(EPSILON));
                        }
                    }

                    andEdNFAs.add(nfa.edge(EPSILON));

                    buffer = new StringBuilder();
                } else {

                    Part andEdPart = partFactory.createPart(exp, ' ');

                    if (andEdPart.isDefinition()) {
                        // if part is a definitions recursively convert it to NFA
                        NFA edgeNfa = toNFAByKey(key, replaceRange(this.regularDefinitions.get(andEdPart.getExpression())));
                        andEdNFAs.add(edgeNfa);
                    } else {
                        for (char nfaChar : andEdPart.getExpression().toCharArray()) {
                            // We reached the smallest part and it is definitely of length 1. Add it to NFA Edge
                            NFA edgeNfa = nfa.edge(nfaChar);
                            andEdNFAs.add(edgeNfa);
                            andEdNFAs.add(nfa.edge(EPSILON));
                        }
                    }

                    andEdNFAs.add(nfa.edge(EPSILON));

                    buffer = new StringBuilder();
                }
            }
        }

        return andEdNFAs;
    }
}