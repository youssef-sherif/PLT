import parsergenerator.CFG;
import parsergenerator.CFGRulesFile;
import parsergenerator.LeftFactoring;
import parsergenerator.LeftRecursion;

import java.util.List;

public class ParserGenerator {

    private final CFG cfg;

    public ParserGenerator(CFGRulesFile cfgRulesFile) {
        this.cfg = new CFG(
//                cfgRulesFile
                new LeftFactoring(
                    new LeftRecursion(cfgRulesFile)
                )
        );
    }

    public List<List<String>> parse(List<String> tokens) throws Exception {
        tokens.add("$");
        this.cfg.createFirstAndFollowSets();
        this.cfg.createLL1Table(this.cfg.getFirst(), this.cfg.getFollow());
        System.out.println(cfg.toString());

        return cfg.parse(tokens);
    }
}
