import lexicalanalyzer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try{
            String rulesFileName = "phase1_test_cases/lexical_rules_1.txt";
            String programFileName = "phase1_test_cases/program1.txt";

            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(
                    rulesFileName,
                    programFileName
            );

            List<String> tokens = lexicalAnalyzer.getTokens();

            System.out.println(tokens);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Syntax Error");
        }
    }
}
