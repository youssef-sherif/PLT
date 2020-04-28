package parseranalyzer;

import java.util.List;

public class CFG {

    private final List<String> tokens;
    private final String key;
    private final String expression;

    public CFG(List<String> tokens,
               String key,
               String expression) {
        
        this.tokens = tokens;
        this.key = key;
        this.expression = expression;
    }
}
