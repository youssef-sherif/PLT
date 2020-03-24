import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        BufferedReader reader;
        List<String> rules = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(
                    new File("lexical_rules.txt")
            ));
            String line = reader.readLine();
            while (line != null) {
                rules.add(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();

            Tokenizer tokenizer = new Tokenizer(rules);
            tokenizer.tokenize();
            System.out.println(tokenizer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
