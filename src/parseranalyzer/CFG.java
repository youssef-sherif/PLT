package parseranalyzer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

public class CFG {

    private static final String EPSILON = "Ɛ";
    private static final String DOLLAR_SIGN = "$";

    private final List<CFGEntry> productions;

    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;
    private Table<String, String, List<String>> parsingTable;

    public CFG(Map<String, String> productions) {
        // convert Map<String, String> productions to List<CFGEntry>
        // for each raw productionRule convert it to List of Lists
        // and create a CFGEntry with key and rule
        this.productions = new ArrayList<>();
        for (Map.Entry<String, String> entry : productions.entrySet()) {
            this.productions.add(
                    new CFGEntry(
                            entry.getKey(),
                            this.convertRuleToList(entry.getValue())
                    )
            );
        }
    }

    public boolean parse(List<String> tokens) {
        // TODO
        return true;
    }

    public void createLL1Table(Map<String, Set<String>> first,
                               Map<String, Set<String>> follow) {

         this.parsingTable = HashBasedTable.create();

        for (CFGEntry cfgEntry : this.productions) {
            String key = cfgEntry.getKey();
            for (List<String> value : cfgEntry.getRule()) {
                if (!value.get(0).equals(EPSILON)) {
                    for (String element : first.get(key)) {
                        if (!element.equals(EPSILON))
                            this.parsingTable.put(key, element, value);
                    }
                } else {
                    for (String element : follow.get(key)) {
                        if (!element.equals(EPSILON))
                            this.parsingTable.put(key, element, value);
                    }
                }
            }
        }
    }

    public void createFirstAndFollowSets() {

        this.first = new LinkedHashMap<>();
        this.follow = new LinkedHashMap<>();

        for (CFGEntry cfgEntry : this.productions) {
            this.first.put(cfgEntry.getKey(), first(cfgEntry.getRule()));
            this.follow.put(cfgEntry.getKey(), follow(cfgEntry.getKey()));
        }
    }

    /*  Example:
     *      convert TERM | SIGN TERM | SIMPLE_EXPRESSION 'addop' TERM
    *       to [[TERM], [SIGN, TERM], [SIMPLE_EXPRESSION, 'addop', TERM]]
     */
    private List<List<String>> convertRuleToList(String rule) {
        List<List<String>> toReturn = new ArrayList<>();
        String[] orEd = rule.split("\\|");

        for (String s : orEd) {
            List<String> temp = new ArrayList<>();
            String[] andEd = s.split(" ");
            for (String s1 : andEd) {
                if(!s1.isEmpty()) {
                    temp.add(s1.trim());
                }
            }

            toReturn.add(temp);
        }

        return toReturn;
    }

    private String removeQuotesFromTerminal(String element) {
        if (!isNonTerminal(element)) {
            return element.substring(1, element.length() - 1);
        }
        return element;
    }

    private Set<String> removeQuotesFromSetOfTerminals(Set<String> terminals) {
        Set<String> cleanedTerminals = new HashSet<>();
        for (String s : terminals) {
            cleanedTerminals.add(removeQuotesFromTerminal(s));
        }
        return cleanedTerminals;
    }

    private List<List<String>> getRuleByKey(String key) {
        List<List<String>> found = null;
        for (CFGEntry cfgEntry : this.productions) {
            if (cfgEntry.getKey().equals(key)) {
                found = cfgEntry.getRule();
            }
        }
        return found;
    }

    private boolean isNonTerminal(String str) {
        return !str.startsWith("'")
                || !str.endsWith("'");
    }

    private boolean containsKey(String key) {
        for (CFGEntry cfgEntry : this.productions) {
            if (cfgEntry.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> first(List<List<String>> rule) {
        Set<String> toReturn = new HashSet<>();
        // loop on orEd productions to get all firsts not just first first.
        for (List<String> orEd : rule) {
            String first = orEd.get(0);
            // if first is non terminal use first as key and recur on its productions.
            if (isNonTerminal(first)
                    && containsKey(first)) {
                toReturn.addAll(first(getRuleByKey(first)));
                return toReturn;
            }
            // if first is terminal add it to return Set.
            else {
                toReturn.add(
                        removeQuotesFromTerminal(first)
                );
            }
        }
        return toReturn;
    }

    private Set<String> follow(String nonTerminal) {
        Set<String> toReturn = new HashSet<>();
        // Case 4
        // If key is of first production append '$' sign to follow set
        if (nonTerminal.equals(this.productions.get(0).getKey())) {
            toReturn.add(DOLLAR_SIGN);
        }
        for (CFGEntry cfgEntry : this.productions) {
            // Example : A -> whatever
            String keyA = cfgEntry.getKey();
            for (List<String> value : cfgEntry.getRule()) {
                int firstOccurrenceOfNonTerminal = value.indexOf(nonTerminal);
                // if rule contains nonTerminal
                if (firstOccurrenceOfNonTerminal != -1) {
                    // Case 1
                    // nonTerminal is at the end
                    // Examples : Follow(Y) Y -> F Y
                    //            Follow(X) X -> T X
                    if (firstOccurrenceOfNonTerminal == value.size() - 1) {
                        if (!keyA.equals(nonTerminal)) {
                            if (this.follow.containsKey(keyA)) {
                                Set<String> temp = this.follow.get(keyA);
                                toReturn.addAll(
                                        removeQuotesFromSetOfTerminals(temp)
                                );
                            }
                        }
                    }
                    // Cases 2 and 3
                    // nonTerminal is between 2 other variables
                    // Examples :  Follow(E) E -> '(' E ')'
                    //             Follow(T) T -> '+' T X
                    else if (value.size() > 2) {
                        // Get the first set from the production that follows firstOccurrenceOfNonTerminal
                        // do that by taking sublist of firstOccurrenceOfNonTerminal+1 till the end of  the production
                        // use Collections.singletonList as first function takes List<List<String>>
                        Set<String> firstOfNext = first(
                                Collections.singletonList(
                                        value.subList(firstOccurrenceOfNonTerminal+1, value.size())
                                )
                        );
                        toReturn.addAll(firstOfNext);

                        // Case 2 :
                        // if rule contains epsilon we add the follow of LHS nonTerminal
                        // and remove 'Ɛ'
                        if (firstOfNext.contains(EPSILON)) {
                            if (!keyA.equals(nonTerminal)) {
                                if (this.follow.containsKey(keyA)) {
                                    Set<String> followA = this.follow.get(keyA);
                                    toReturn.addAll(
                                            removeQuotesFromSetOfTerminals(followA)
                                    );
                                }
                            }
                            toReturn.remove(EPSILON);
                        }
                        // Case 3 :
                        // if rule does contains epsilon we add the follow of RHS nonTerminal
                        else {
                            String keyB = productions.get(firstOccurrenceOfNonTerminal).getKey();
                            if (this.follow.containsKey(keyB)) {
                                Set<String> followB = this.follow.get(keyB);
                                toReturn.addAll(
                                        removeQuotesFromSetOfTerminals(followB)
                                );
                            }
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    public Map<String, Set<String>> getFirst() {
        return this.first;
    }

    public Map<String, Set<String>> getFollow() {
        return this.follow;
    }

    public List<CFGEntry> getProductions() {
        return this.productions;
    }

    public Table<String, String, List<String>> getLL1ParsingTable() {
        return this.parsingTable;
    }

    @Override
    public String toString() {
        return  "===PRODUCTIONS===\n" + productions.toString() + "\n" +
                "===FIRST===\n" + first + "\n" +
                "===FOLLOW===\n" + follow + "\n" +
                "===PARSING TABLE===\n" + parsingTable;
    }
}
