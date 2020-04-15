import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LexicalRulesFile {

    private final List<String> rules;
    private Map<String, String> regularExpressions;
    private Map<String, String> regularDefinitions;
    private Set<String> keyWords;
    private Set<String> punctuation;

    public LexicalRulesFile(String fileName) {
        BufferedReader reader;
        this.rules = new ArrayList<>();
        this.regularExpressions = new HashMap<>();
        this.regularDefinitions = new HashMap<>();
        this.keyWords = new HashSet<>();
        this.punctuation = new HashSet<>();

        try {
            reader = new BufferedReader(new FileReader(
                    new File(fileName)
            ));
            String line = reader.readLine();
            while (line != null) {
                rules.add(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tokenize();
    }


    private void tokenize() {
        for (String s : this.rules) {

            // handle Punctuation
            if (s.startsWith("[")) {
                String punctuation = s.substring(1, s.length()-1);
                String[] split = punctuation.split(" ");
                for (String s1 : split) {
                    if (s1.length() == 2 && s1.startsWith("\\")) {
                        Collections.addAll(this.punctuation, Character.toString(s1.charAt(1)));
                    } else if (s1.length() == 1) {
                        Collections.addAll(this.punctuation, Character.toString(s1.charAt(0)));
                    }
                }
            }
            // handle Keywords
            else if (s.startsWith("{")) {
                String keyWords = s.substring(1, s.length()-1);
                Collections.addAll(this.keyWords, keyWords.split(" "));
            }

            else {
                String[] equalTokens = s.split("=", 2);
                String[] colonTokens = s.split(":", 2);

                // handle colon tokens
                if (colonTokens[0].split(" ").length == 1) {
                    String ruleName = colonTokens[0];
                    regularExpressions.put(ruleName.trim(), colonTokens[1]);
                }
                // handle equal tokens
                else if (equalTokens[0].split(" ").length == 1) {
                    String ruleName = equalTokens[0];
                    regularDefinitions.put(ruleName.trim(), equalTokens[1]);
                }
            }
        }
    }

    public Map<String, String> getRegularExpressions() {
        return regularExpressions;
    }

    public Map<String, String> getRegularDefinitions() {
        return regularDefinitions;
    }

    public Set<String> getKeyWords() {
        return keyWords;
    }

    public Set<String> getPunctuation() {
        return punctuation;
    }

    @Override
    public String toString() {
        return  "\n ===regularExpressions===\n" + regularExpressions.keySet() + "\n" + regularExpressions.values() +
                "\n regularDefinitions===\n" + regularDefinitions.keySet() + "\n" + regularDefinitions.values() +
                "\n ===keyWords===\n" + keyWords +
                "\n ===punctuation===\n" + punctuation;
    }
}
