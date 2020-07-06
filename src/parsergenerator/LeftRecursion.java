package parsergenerator;

import java.util.*;

/*
*   Decorator class for CFG:
*       Use new CFG(new LeftRecursion(productions))
*       to check for and remove left recursion
 */
public class LeftRecursion implements CFGAmbiguity {

    private final List<CFGEntry> productions;

    public LeftRecursion(CFGAmbiguity ambiguity) {
        this.productions = ambiguity.solve();
    }

    public LeftRecursion(CFGRulesFile cfgRulesFile) {
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
    }

    @Override
    public List<CFGEntry> solve() {
        List<CFGEntry> toReturn = new ArrayList<>();
        for (CFGEntry entry : this.productions) {
            boolean flushToReturnList = true;
            for (List<String> l : entry.getRule()) {
                // left recursion found
                if (entry.getKey().equals(l.get(0))) {
                    CFGEntry newProduction = this.recreateEntry(entry);
                    CFGEntry newProductionDash = this.createDashEntry(entry, l);

                    toReturn.remove(entry);
                    toReturn.add(newProduction);
                    toReturn.add(newProductionDash);
                    flushToReturnList = false;
                }
            }
            if (flushToReturnList) {
                toReturn.add(entry);
            }
        }

        return toReturn;
    }

    private CFGEntry recreateEntry(CFGEntry entry) {
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

    private CFGEntry createDashEntry(CFGEntry entry, List<String> context) {
        String dashRuleKey = entry.getKey() + "_dash";
        List<List<String>> dashRuleProduction = new ArrayList<>();

        String s = context.remove(0);
        context.add(s+"_dash");
        dashRuleProduction.add(context);
        dashRuleProduction.add(Collections.singletonList(Constants.EPSILON));

        return new CFGEntry(dashRuleKey, dashRuleProduction);
    }
}
