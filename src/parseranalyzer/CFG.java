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

    public CFG(CFGRulesFile cfgRulesFile) {
        // convert Map<String, String> productions to List<CFGEntry>
        // for each raw productionRule convert it to List of Lists
        // and create a CFGEntry with key and rule
        this.productions = new ArrayList<>();
        for (Map.Entry<String, String> entry : cfgRulesFile.getCFGRules().entrySet()) {
            this.productions.add(
                    new CFGEntry(
                            entry.getKey(),
                            CFGUtil.convertRuleToList(entry.getValue())
                    )
            );
        }
        this.terminals = CFGUtil.findTerminalsAndRemoveQuotations(this.productions);
        this.parser = new LL1(this);
    }

    public CFG(LeftRecCFG leftRecCFG) {
        this.productions = leftRecCFG.removeLeftRecursion();
        this.terminals = CFGUtil.findTerminalsAndRemoveQuotations(this.productions);
        this.parser = new LL1(this);
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

    public List<List<String>> getRuleByKey(String key) {
        List<List<String>> found = null;
        for (CFGEntry cfgEntry : this.productions) {
            if (cfgEntry.getKey().equals(key)) {
                found = cfgEntry.getRule();
            }
        }
        return found;
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
