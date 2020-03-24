import java.util.*;

public class Tokenizer {

    private final List<String> rules;
    private Map<String, List<String>> regularExpressions;
    private Map<String, List<String>> regularDefinitions;
    private List<String> keyWords;
    private List<String> punctuation;

    public Tokenizer(List<String> rules) {
        this.rules = rules;
        this.regularExpressions = new HashMap<>();
        this.regularDefinitions = new HashMap<>();
        this.keyWords = new ArrayList<>();
        this.punctuation = new ArrayList<>();
    }


    public void tokenize() {
        for (String s : this.rules) {
            // reached end
            if (s == null) break;

            // handle Punctuation
            if (s.startsWith("[")) {
                String punctuation = s.substring(1, s.length()-1);
                this.punctuation = Arrays.asList(punctuation.split(" "));
            }
            // handle Keywords
            else if (s.startsWith("{")) {
                String keyWords = s.substring(1, s.length()-1);
                this.keyWords = Arrays.asList(keyWords.split(" "));
            }

            else {
                String[] equalTokens = s.split("=", 2);
                String[] colonTokens = s.split(":", 2);

                // handle colon tokens
                if (colonTokens[0].split(" ").length == 1) {
                    List<String> tokens = new ArrayList<>();
                    String ruleName = colonTokens[0];

                    for (int i = 1; i < colonTokens.length; i++) {
                        tokens.add(colonTokens[i]);
                    }
                    regularExpressions.put(ruleName, tokens);
                }
                // handle equal tokens
                else if (equalTokens[0].split(" ").length == 1) {
                    List<String> tokens = new ArrayList<>();
                    String ruleName = equalTokens[0];

                    for (int i = 1; i < equalTokens.length; i++) {
                        tokens.add(equalTokens[i]);
                    }
                    regularDefinitions.put(ruleName, tokens);
                }
            }
        }
    }

    public Map<String, List<String>> getRegularExpressions() {
        return regularExpressions;
    }

    public Map<String, List<String>> getRegularDefinitions() {
        return regularDefinitions;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }

    public List<String> getPunctuation() {
        return punctuation;
    }

    @Override
    public String toString() {
        return "Tokenizer{" +
                "\n ===regularExpressions===\n" + regularExpressions.keySet() + "\n" + regularExpressions.values() +
                "\n regularDefinitions===\n" + regularDefinitions.keySet() + "\n" + regularDefinitions.values() +
                "\n ===keyWords===\n" + keyWords +
                "\n ===punctuation===\n" + punctuation +
                "\n}";
    }
}
