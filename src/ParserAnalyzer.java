import parseranalyzer.CFG;
import parseranalyzer.CFGRulesFile;

import java.util.ArrayList;
import java.util.List;

public class ParserAnalyzer {

    private final List<String> tokens;
    private final List<CFG> cfgList;
    private final String program;

    public ParserAnalyzer(List<String> tokens,
                          CFGRulesFile cfgRulesFile,
                          ProgramFile programFile) {

        this.tokens = tokens;
        this.cfgList = new ArrayList<>();
        this.program = programFile.toString();

        CFG cfg = new CFG(
                cfgRulesFile.getCFGRules()
        );

        System.out.println(cfg.getRules());
    }
}
