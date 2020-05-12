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
            List<String> tokens = lexicalAnalyzer.tokenize(programFile.getProgram());
            System.out.println("===LEXICAL ANALYZER OUTPUT===");
            System.out.println(tokens);

            ParserAnalyzer parserAnalyzer = new ParserAnalyzer(cfgRulesFile);
            List<List<String>> parserOutput = parserAnalyzer.parse(tokens);
            System.out.println("===PARSER GENERATOR OUTPUT===");
            System.out.println(parserOutput);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Syntax Error");
        }
    }
}
