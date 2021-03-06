package parsergenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CFGRulesFile {

    private final Map<String, String> cfgRules;

    public CFGRulesFile(String fileName) {

        this.cfgRules = new LinkedHashMap<>();
        StringBuilder buffer = new StringBuilder();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    new File(fileName)
            ));
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();

            List<String> allRules = this.parseAllRules(buffer.toString());
            for (String rule : allRules) {
                putRuleToMap(rule);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> parseAllRules(String cfg) {
        // append '\0' at the end of cfg to be able to parse last rule
        String cfg1 = cfg + "\0";
        StringBuilder buffer = new StringBuilder();
        List<String> rules = new ArrayList<>();

        for (char curr : cfg1.toCharArray()) {

            // read all characters between '#' as new rules
            if (curr == '#' || curr == '\0') {
                String currCFG = buffer.toString();
                // this would be true when we reach the first '#' in the string
                if (!currCFG.isEmpty()) {
                    rules.add(currCFG);
                }
                buffer = new StringBuilder();
            } else {
                buffer.append(curr);
            }
        }

        return rules;
    }

    private void putRuleToMap(String cfgRule) {
        String cfgRuleName = "";
        boolean firstEqual = false;

        StringBuilder buffer = new StringBuilder();

        for (char curr : cfgRule.toCharArray()) {
            buffer.append(curr);

            // when we reach first equal we take the buffer string as the rule name
            if (curr == '=' && !firstEqual) {
                firstEqual = true;
                cfgRuleName = buffer.toString();
                // remove '='
                cfgRuleName = cfgRuleName.substring(0, cfgRuleName.length()-1).trim();
                buffer = new StringBuilder();
            }
        }
        String cfgExpression = buffer.toString();

        this.cfgRules.put(cfgRuleName, cfgExpression);
    }

    public Map<String, String> getCFGRules() {
        return cfgRules;
    }

    @Override
    public String toString() {
        return  "\n ===CFG===\n" + cfgRules.entrySet();
    }
}
