package parsergenerator;

import java.util.*;

/*
 *   Decorator class for CFG:
 *       Use new CFG(new LeftFacCFG(productions))
 *       to perform left factoring
 */
public class LeftFacCFG implements CFGDecorator {

    private final List<CFGEntry> productions;

    public LeftFacCFG(LeftRecCFG leftRecCFG) {
        this.productions = leftRecCFG.solve();
    }

    public LeftFacCFG(CFGRulesFile cfgRulesFile) {
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
            if (shouldSolve(entry)) {
                CFGEntry entry1 = this.recreateEntry(entry);
                CFGEntry entry2 = this.createDashEntry(entry, entry.getRule());
                toReturn.add(entry1);
                toReturn.add(entry2);
            } else {
                toReturn.add(entry);
            }
        }

        return toReturn;
    }

    private boolean shouldSolve(CFGEntry entry) {
        if (entry.getRule().size() < 2) {
            return false;
        }
        boolean shouldSolve = true;
        String s1 = entry.getRule().get(0).get(0);
        for (int i = 1; i < entry.getRule().size(); i++) {
            String s2 = entry.getRule().get(i).get(0);
            if (!s1.equals(s2)) {
                shouldSolve = false;
                break;
            }
        }
        return shouldSolve;
    }

    private CFGEntry recreateEntry(CFGEntry entry) {
        String s1 = entry.getRule().get(0).get(0);
        List<List<String>> productionRule = new ArrayList<>();
        List<String> l = new ArrayList<>();
        l.add(s1);
        l.add(entry.getKey()+"_dash");
        productionRule.add(l);

        return new CFGEntry(entry.getKey(), productionRule);
    }

    private CFGEntry createDashEntry(CFGEntry entry, List<List<String>> context) {
        String dashRuleKey = entry.getKey() + "_dash";
        List<List<String>> dashRuleProduction = new ArrayList<>();

        for (List<String> l : context) {
            if (l.size() == 1) {
                dashRuleProduction.add(Collections.singletonList(Constants.EPSILON));
            } else {
                l.remove(0);
                dashRuleProduction.add(l);
            }
        }

        return new CFGEntry(dashRuleKey, dashRuleProduction);
    }

}
