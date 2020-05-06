package parseranalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CFGUtil {

    /*  Example:
     *      convert TERM | SIGN TERM | SIMPLE_EXPRESSION 'addop' TERM
     *       to [[TERM], [SIGN, TERM], [SIMPLE_EXPRESSION, 'addop', TERM]]
     */
    public static List<List<String>> convertRuleToList(String rule) {
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

    /*  Example:
    *       ASSIGNMENT = [['id', 'assign', EXPRESSION, ';']]
    *       to ASSIGNMENT = [[id, assign, EXPRESSION, ;]]
     */
    public static Set<String> findTerminalsAndRemoveQuotations(List<CFGEntry> productions) {
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

    private static String removeQuotesFromTerminal(String element) {
        if (isTerminal(element)) {
            return element.substring(1, element.length() - 1);
        }
        return element;
    }

    private static boolean isTerminal(String str) {
        return str.startsWith("'")
                && str.endsWith("'");
    }
}
