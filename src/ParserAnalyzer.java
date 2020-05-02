import com.google.common.collect.Table;
import parseranalyzer.CFG;
import parseranalyzer.CFGEntry;
import parseranalyzer.CFGRulesFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParserAnalyzer {

    private final List<String> tokens;
    private final CFG cfg;

    public ParserAnalyzer(List<String> tokens,
                          CFGRulesFile cfgRulesFile) {
        this.tokens = tokens;
        this.cfg = new CFG(cfgRulesFile.getCFGRules());
    }

    public void parse() {
        this.cfg.createFirstAndFollowSets();
        Map<String, Set<String>> first = this.cfg.getFirst();
        Map<String, Set<String>> follow = this.cfg.getFollow();

        this.cfg.createLL1Table(first, follow);

        System.out.println(cfg.toString());
    }
}
