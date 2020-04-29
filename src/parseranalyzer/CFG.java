package parseranalyzer;

import java.util.*;

public class CFG {

    private final Map<String, Set<String>> first;
    private final Map<String, Set<String>> follow;
    private final List<CFGEntry> productions;

    public CFG(Map<String, String> productions) {
        this.first = new LinkedHashMap<>();
        this.follow = new LinkedHashMap<>();
        this.productions = new ArrayList<>();

        for (Map.Entry<String, String> entry : productions.entrySet()) {
            this.productions.add(
                    new CFGEntry(
                            entry.getKey(),
                            this.convertProductionToList(entry.getValue())
                    )
            );
        }
    }

    public void computeFirstAndFollow() {
        for (CFGEntry cfgEntry : this.productions) {
            this.first.put(cfgEntry.getKey(), first(cfgEntry.getRule()));
            // TODO
//            this.follow.put(cfgEntry.getKey(), follow(cfgEntry.getKey()));
        }
    }

    private List<List<String>> convertProductionToList(String production) {
        List<List<String>> toReturn = new ArrayList<>();
        String[] orEd = production.split("\\|");

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

    private List<List<String>> getProductionByKey(String key) {
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

    private Set<String> first(List<List<String>> production) {
        Set<String> toReturn = new HashSet<>();
        // loop on orEd productions to get all firsts not just first first.
        for (List<String> orEd : production) {
            String first = orEd.get(0);
            // if first is non terminal use first as key and recur on its productions.
            if (isNonTerminal(first)
                    && containsKey(first)) {
                toReturn.addAll(first(getProductionByKey(first)));
                return toReturn;
            }
            // if first is terminal add it to return Set.
            else {
                toReturn.add(first);
            }
        }
        return toReturn;
    }

    private Set<String> follow(String key) {
        // TODO
        return null;
    }

    public Map<String, Set<String>> getFirst() {
        return this.first;
    }

    public List<CFGEntry> getProductions() {
        return productions;
    }

    @Override
    public String toString() {
        return productions.toString();
    }

}
