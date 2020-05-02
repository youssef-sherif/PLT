package parseranalyzer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

public class CFG {

    public static final String EPSILON = "Ɛ";
    public static final String DOLLAR_SIGN = "$";

    private final List<CFGEntry> productions;
    private final LL1 parser;

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
        this.parser = new LL1(this);
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
            this.first.put(cfgEntry.getKey(), parser.first(cfgEntry.getRule()));
            this.follow.put(cfgEntry.getKey(), parser.follow(cfgEntry.getKey(), this.follow));
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

    public String removeQuotesFromTerminal(String element) {
        if (!isNonTerminal(element)) {
            return element.substring(1, element.length() - 1);
        }
        return element;
    }

    public Set<String> removeQuotesFromSetOfTerminals(Set<String> terminals) {
        Set<String> cleanedTerminals = new HashSet<>();
        for (String s : terminals) {
            cleanedTerminals.add(removeQuotesFromTerminal(s));
        }
        return cleanedTerminals;
    }

    public List<List<String>> getRuleByKey(String key) {
        List<List<String>> found = null;
        for (CFGEntry cfgEntry : this.productions) {
            if (cfgEntry.getKey().equals(key)) {
                found = cfgEntry.getRule();
            }
        }
        return found;
    }

    public boolean isNonTerminal(String str) {
        return !str.startsWith("'")
                || !str.endsWith("'");
    }

    public boolean containsKey(String key) {
        for (CFGEntry cfgEntry : this.productions) {
            if (cfgEntry.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public List<CFGEntry> getProductions() {
        return this.productions;
    }

    public Map<String, Set<String>> getFirst() {
        return this.first;
    }

    public Map<String, Set<String>> getFollow() {
        return this.follow;
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
