package parseranalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CFG {
    private final List<CFGEntry> rules;

    public CFG(Map<String, String> rules) {

        this.rules = new ArrayList<>();

        for (Map.Entry<String, String> entry : rules.entrySet()) {
            this.rules.add(
                    new CFGEntry(
                            entry.getKey(),
                            this.asList(entry.getValue())
                    )
            );
        }
    }

    private List<List<String>> asList(String expression) {
        List<List<String>> toReturn = new ArrayList<>();
        String[] orEd = expression.split("\\|");

        for (String s : orEd) {
            List<String> temp = new ArrayList<>();
            String[] andEd = s.split(" ");
            for (String s1 : andEd) {
                if(!s1.isEmpty()) {
                    temp.add(s1);
                }
            }

            toReturn.add(temp);
        }

        return toReturn;
    }

    public List<CFGEntry> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return rules.toString();
    }
}
