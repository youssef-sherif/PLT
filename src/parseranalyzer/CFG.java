package parseranalyzer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

public class CFG {

    private final List<CFGEntry> productions;

    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;
    private Table<String, String, List<String>> parsingTable;

    public CFG(Map<String, String> productions) {
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

    public void createLL1Table(Map<String, Set<String >> first,
                               Map<String, Set<String>> follow) {

         this.parsingTable = HashBasedTable.create();

        for (CFGEntry cfgEntry : this.productions) {
            String key = cfgEntry.getKey();
            for (List<String> value : cfgEntry.getRule()) {
                System.out.println(value);
                if (!value.get(0).equals("@")) {
                    for (String element : first.get(key)) {
                        if (!element.equals("@"))
                            this.parsingTable.put(key, element, value);
                    }
                } else {
                    for (String element : follow.get(key)) {
                        if (!element.equals("@"))
                            this.parsingTable.put(key, element, value);
                    }
                }
            }
        }
    }

    public void createFirstAndFollowSets() {
        this.first = new LinkedHashMap<>();
        this.follow = new LinkedHashMap<>();

        // first we need to initialize follow sets
        for (CFGEntry cfgEntry : this.productions) {
            this.follow.put(cfgEntry.getKey(), new HashSet<>());
        }

        // add '$' sign to first set in follow
        this.follow.get(this.productions.get(0).getKey()).add("$");

        for (CFGEntry cfgEntry : this.productions) {
            this.first.put(cfgEntry.getKey(), first(cfgEntry.getRule()));
            follow(cfgEntry.getKey(), this.follow);
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
                toReturn.add(first);
            }
        }
        return toReturn;
    }

    private void follow(String nonTerminal,
                        Map<String, Set<String>> follow) {
        for (CFGEntry cfgEntry : this.productions) {
            String key = cfgEntry.getKey();
            for (List<String> value : cfgEntry.getRule()) {
                int firstOccurrenceOfNonTerminal = value.indexOf(nonTerminal);
                // if rule contains nonTerminal
                if (firstOccurrenceOfNonTerminal != -1) {
                    if (firstOccurrenceOfNonTerminal == value.size() - 1) {
                        if (!key.equals(nonTerminal)) {
                            if (follow.containsKey(key)) {
                                Set<String> temp = follow.get(key);
                                follow.get(nonTerminal).addAll(temp);
                            }
                        }
                    } else  {
                        // Get the first set from the production that follows firstOccurrenceOfNonTerminal
                        // do that by taking sublist of firstOccurrenceOfNonTerminal+1 till the end of  the production
                        // use Collections.singletonList as first function takes List<List<String>>
                        Set<String> firstOfNext = first(
                                Collections.singletonList(value.subList(firstOccurrenceOfNonTerminal+1, value.size()))
                        );

                        if (firstOfNext.contains("@")) {
                            if (!key.equals(nonTerminal)) {
                                if (follow.containsKey(key)) {
                                    Set<String> temp = follow.get(key);
                                    follow.get(nonTerminal).addAll(temp);
                                }
                            }
                            follow.get(nonTerminal).addAll(firstOfNext);
                            follow.get(nonTerminal).remove("@");
                        } else {
                            follow.get(nonTerminal).addAll(firstOfNext);
                        }
                    }
                }
            }
        }
    }

    public Map<String, Set<String>> getFirst() {
        return this.first;
    }

    public Map<String, Set<String>> getFollow() {
        return this.follow;
    }

    public List<CFGEntry> getProductions() {
        return productions;
    }

    public Table<String, String, List<String>> getLL1ParsingTable() {
        return this.parsingTable;
    }

    @Override
    public String toString() {
        return productions.toString();
    }
}
