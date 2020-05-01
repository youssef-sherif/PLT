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
        System.out.println("===CFG PRODUCTIONS===");
        List<CFGEntry> productions = this.cfg.getProductions();
        System.out.println(productions);

        this.cfg.createFirstAndFollowSets();
        Map<String, Set<String>> first = this.cfg.getFirst();
        Map<String, Set<String>> follow = this.cfg.getFollow();

        System.out.println("===FIRST===");
        System.out.println(first);

        System.out.println("===FOLLOW===");
        System.out.println(follow);

        this.cfg.createLL1Table(first, follow);
        Table<String, String, List<String>> ll1 = this.cfg.getLL1ParsingTable();

        System.out.println("===PARSING TABLE===");
        System.out.println(ll1);

        System.out.println(cfg.parse(this.tokens));
    }
}
