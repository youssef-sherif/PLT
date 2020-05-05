package parseranalyzer;

import static parseranalyzer.Constants.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

public class CFG {
    private final List<CFGEntry> productions;
    private final LL1 parser;
    private final Set<String> terminals;

    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;
    private Table<String, String, List<String>> parsingTable;

    public CFG(Map<String, String> productions) {
        // convert Map<String, String> productions to List<CFGEntry>
        // for each raw productionRule convert it to List of Lists
        // and create a CFGEntry with key and rule
        List<CFGEntry> temp = new ArrayList<>();
        for (Map.Entry<String, String> entry : productions.entrySet()) {
            temp.add(
                    new CFGEntry(
                            entry.getKey(),
                            this.convertRuleToList(entry.getValue())
                    )
            );
        }
        this.terminals = findTerminalsAndRemoveQuotations(temp);
        this.productions = removeLeftRecursion(temp);
        this.parser = new LL1(this);
    }

    private List<CFGEntry> removeLeftRecursion(List<CFGEntry> productions) {
        List<CFGEntry> toReturn = new ArrayList<>();
        for (CFGEntry entry : productions) {
            boolean add = true;
            for (List<String> l : entry.getRule()) {
                // left recursion found
                if (entry.getKey().equals(l.get(0))) {
                    CFGEntry newProduction = recreateProduction(entry);
                    CFGEntry newProductionDash = createProductionDash(entry, l);

                    toReturn.remove(entry);
                    toReturn.add(newProduction);
                    toReturn.add(newProductionDash);
                    add = false;
                }
            }
            if (add) {
                toReturn.add(entry);
            }
        }

        return toReturn;
    }

    private CFGEntry recreateProduction(CFGEntry entry) {
        String productionKey = entry.getKey();
        List<List<String>> productionRule = new ArrayList<>();
        for (List<String> l : entry.getRule()) {
            if (!l.get(0).equals(productionKey)) {
                List<String> l1 = new ArrayList<>(l);
                productionRule.add(l1);
            }
        }
        for (List<String> l : productionRule) {
            l.add(entry.getKey() + "_dash");
        }
        return new CFGEntry(productionKey, productionRule);
    }

    private CFGEntry createProductionDash(CFGEntry entry, List<String> context) {
        String dashRuleKey = entry.getKey() + "_dash";
        List<List<String>> dashRuleProduction = new ArrayList<>();

        String s = context.remove(0);
        context.add(s+"_dash");
        dashRuleProduction.add(context);
        dashRuleProduction.add(Collections.singletonList(EPSILON));

        return new CFGEntry(dashRuleKey, dashRuleProduction);
    }

    private Set<String> findTerminalsAndRemoveQuotations(List<CFGEntry> productions) {
        Set<String> terminals = new HashSet<>();
        for (CFGEntry entry : productions) {
            int i = 0;
            for (List<String> l : entry.getRule()) {
                int j = 0;
                for (String s : l) {
                    if (isTerminal(s)) {
                        String cleanedTerminal = removeQuotesFromTerminal(s);
                        terminals.add(cleanedTerminal);
                        entry.getRule().get(i).set(j, cleanedTerminal);
                    }
                    j++;
                }
                i++;
            }
        }
        return terminals;
    }

    public boolean parse(List<String> tokens) throws Exception {
        return this.parser.parse(
                tokens,
                parsingTable,
                productions,
                terminals
        );
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
        if (isTerminal(element)) {
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

    public boolean isTerminal(String str) {
        return str.startsWith("'")
                && str.endsWith("'");
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

    public Set<String> getTerminals() {
        return this.terminals;
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
                "===TERMINALS===\n" + terminals + "\n" +
                "===FIRST===\n" + first + "\n" +
                "===FOLLOW===\n" + follow + "\n" +
                "===PARSING TABLE===\n" + parsingTable;
    }

}
