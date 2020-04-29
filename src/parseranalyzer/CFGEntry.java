package parseranalyzer;

import java.util.List;

public class CFGEntry {
    private final String key;
    private final List<List<String>> rule;

    public CFGEntry(String key,
                    List<List<String>> rule) {
        this.key = key;
        this.rule = rule;
    }

    public String getKey() {
        return key;
    }

    public List<List<String>> getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return  "===key===" + key + "\n" +
                "===rule===" + rule + "\n";
    }
}
