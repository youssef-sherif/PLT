import lexicalanalyzer.LexicalRulesFile;
import parseranalyzer.CFGRulesFile;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        try{
            LexicalRulesFile rulesFile = new LexicalRulesFile("lexical_analyzer_test_cases/lexical_rules_0.txt");
            CFGRulesFile cfgRulesFile = new CFGRulesFile("parser_analyzer_test_cases/cfg_0.txt");
            ProgramFile programFile = new ProgramFile("test_programs/program_0.txt");

            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(
                    rulesFile,
                    programFile
            );

            List<String> tokens = lexicalAnalyzer.getTokens();

            System.out.println(tokens);

            ParserAnalyzer parserAnalyzer = new ParserAnalyzer(
                    tokens,
                    cfgRulesFile
            );

            parserAnalyzer.parse(programFile.getProgram());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Syntax Error");
        }
    }
}
