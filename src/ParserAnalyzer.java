import parseranalyzer.CFG;
import parseranalyzer.CFGRulesFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParserAnalyzer {

    private final List<String> tokens;
    private final List<CFG> cfgList;

    public List<CFG> getCfgList() {
        return cfgList;
    }

    public ParserAnalyzer(List<String> tokens,
                          CFGRulesFile cfgRulesFile,
                          ProgramFile programFile) {

        this.tokens = tokens;
        this.cfgList = new ArrayList<>();

        for (Map.Entry<String, String> entry : cfgRulesFile.getCFGRules().entrySet()) {

            CFG cfg = new CFG(
                    tokens,
                    entry.getKey(),
                    entry.getValue()
            );

            this.cfgList.add(cfg);
        }
    }


}
