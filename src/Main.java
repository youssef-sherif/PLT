import lexicalanalyzer.LexicalRulesFile;
import parseranalyzer.CFGRulesFile;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try{
            LexicalRulesFile lexicalRulesFile = new LexicalRulesFile("lexical_analyzer_test_cases/lexical_rules_0.txt");
            CFGRulesFile cfgRulesFile = new CFGRulesFile("parser_analyzer_test_cases/cfg_0.txt");
            ProgramFile programFile = new ProgramFile("test_programs/program_0.txt");

            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(lexicalRulesFile);
            ParserAnalyzer parserAnalyzer = new ParserAnalyzer(cfgRulesFile);

//            List<String> tokens = new ArrayList<>();
            List<String> tokens = lexicalAnalyzer.tokenize(programFile.getProgram());
            System.out.println("===TOKENS===");
            System.out.println(tokens);
            parserAnalyzer.parse(tokens);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Syntax Error");
        }
    }
}
