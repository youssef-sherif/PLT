package parseranalyzer;

import java.util.*;

public class LL1 {

    private final CFG cfg;

    public LL1(CFG cfg) {
        this.cfg = cfg;
    }

    public Set<String> first(List<List<String>> rule) {
        Set<String> toReturn = new HashSet<>();
        // loop on orEd productions to get all firsts not just first first.
        for (List<String> orEd : rule) {
            String first = orEd.get(0);
            // if first is non terminal use first as key and recur on its productions.
            if (cfg.isNonTerminal(first)
                    && cfg.containsKey(first)) {
                toReturn.addAll(first(cfg.getRuleByKey(first)));
                return toReturn;
            }
            // if first is terminal add it to return Set.
            else {
                toReturn.add(
                        cfg.removeQuotesFromTerminal(first)
                );
            }
        }
        return toReturn;
    }

    public Set<String> follow(String nonTerminal,
                              Map<String, Set<String>> follow) {
        Set<String> toReturn = new HashSet<>();
        // Case 4
        // If key is of first production append '$' sign to follow set
        if (nonTerminal.equals(cfg.getProductions().get(0).getKey())) {
            toReturn.add(CFG.DOLLAR_SIGN);
        }
        for (CFGEntry cfgEntry : cfg.getProductions()) {
            // Example : A -> whatever
            String keyA = cfgEntry.getKey();
            for (List<String> orEd : cfgEntry.getRule()) {
                int firstOccurrenceOfNonTerminal = orEd.indexOf(nonTerminal);
                // if rule contains nonTerminal
                if (firstOccurrenceOfNonTerminal != -1) {
                    // Case 1
                    // nonTerminal is at the end
                    // Examples : Follow(Y) Y -> F Y
                    //            Follow(X) X -> T X
                    if (firstOccurrenceOfNonTerminal == orEd.size() - 1) {
                        if (!keyA.equals(nonTerminal)) {
                            if (follow.containsKey(keyA)) {
                                Set<String> temp = follow.get(keyA);
                                toReturn.addAll(
                                        cfg.removeQuotesFromSetOfTerminals(temp)
                                );
                            }
                        }
                    }
                    // Cases 2 and 3
                    // nonTerminal is between 2 other variables
                    // Examples : Case 2:  Follow(E) E -> '(' E ')'
                    //            Case 3:  Follow(T) T -> '+' T R where First(R) contains 'Ɛ'
                    else if (orEd.size() > 2) {
                        // Get the first set from the production that follows firstOccurrenceOfNonTerminal
                        // do that by taking sublist of firstOccurrenceOfNonTerminal+1 till the end of  the production
                        // use Collections.singletonList as first function takes List<List<String>>
                        Set<String> firstOfNext = first(
                                Collections.singletonList(
                                        orEd.subList(firstOccurrenceOfNonTerminal+1, orEd.size())
                                )
                        );
                        toReturn.addAll(firstOfNext);

                        // Case 2 :
                        // if rule contains epsilon we add the follow of LHS nonTerminal
                        // and remove 'Ɛ'
                        if (firstOfNext.contains(CFG.EPSILON)) {
                            if (!keyA.equals(nonTerminal)) {
                                if (follow.containsKey(keyA)) {
                                    Set<String> followA = follow.get(keyA);
                                    toReturn.addAll(
                                            cfg.removeQuotesFromSetOfTerminals(followA)
                                    );
                                }
                            }
                            toReturn.remove(CFG.EPSILON);
                        }
                        // Case 3 :
                        // if rule does contains epsilon we add the follow of RHS nonTerminal
                        else {
                            String keyB = cfg.getProductions().get(firstOccurrenceOfNonTerminal).getKey();
                            if (follow.containsKey(keyB)) {
                                Set<String> followB = follow.get(keyB);
                                toReturn.addAll(
                                        cfg.removeQuotesFromSetOfTerminals(followB)
                                );
                            }
                        }
                    }
                }
            }
        }
        return toReturn;
    }

}
