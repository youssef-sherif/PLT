import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        RulesTokenizer rulesTokenizer = new RulesTokenizer("lexical_rules.txt");
        rulesTokenizer.tokenize();

        List<NFA> NFAList = new ArrayList<>();

        System.out.println(rulesTokenizer.getRegularDefinitionsNames());

        rulesTokenizer.getRegularExpressions().forEach((key, value) -> {
            RegularExpressionTokenizer regularExpressionTokenizer = new RegularExpressionTokenizer(
                    key, value,
                    rulesTokenizer.getRegularDefinitionsNames());

            regularExpressionTokenizer.tokenizeRegEx();

        });


//        NFA combinedNfa = NFA.combineNFAs(NFAList);
    }
}
