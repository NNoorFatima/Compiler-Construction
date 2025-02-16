package work;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String relativePath = "D:\\eclispe\\compiler\\src\\input.sn";  

        File file = new File(relativePath);
        if (!file.exists()) {
            System.out.println("Error: File '" + relativePath + "' not found.");
            System.exit(1);
        }

        try {
            String content = readFile(relativePath);
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.tokenize(content);

            if (tokens == null || tokens.isEmpty()) {
                System.out.println("Error: No tokens generated.");
                return;
            }

            // ✅ Track global and local variables
            Set<Token> uniqueTokens = new HashSet<>();
            Set<String> globalVariables = new HashSet<>();
            Set<String> localVariables = new HashSet<>();

            int totalTokens = tokens.size(); // Count total tokens
            boolean insideFunction = false;
            boolean foundMain = false; // Track when we hit `poora main`
            Set<String> functionNames = new HashSet<>();  // Track functions

            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                System.out.println(token);
                uniqueTokens.add(token);

                // Detect function declaration like "poora main {"
                if (token.getType() == Token.Type.KEYWORD && "poora".equals(token.getValue()) && i + 1 < tokens.size()) {
                    Token nextToken = tokens.get(i + 1);
                    if (nextToken.getType() == Token.Type.IDENTIFIER) {
                        functionNames.add(nextToken.getValue()); // Register function name
                        if (nextToken.getValue().equals("main")) {
                            foundMain = true; // Mark that main function started
                        }
                    }
                }

                // Function starts when we hit '{' after function name
                if (token.getValue().equals("{") && !functionNames.isEmpty()) {
                    insideFunction = true;
                }

                // Function ends when we hit '}'
                if (token.getValue().equals("}")) {
                    insideFunction = false;
                }

                // Correctly classify variables
                if (isVariableDeclaration(token)) {
                    extractVariables(tokens, i + 1, foundMain, globalVariables, localVariables);
                }
            }

            // ✅ Display results
            System.out.println("\nTotal Tokens: " + totalTokens);
            System.out.println("Unique Tokens: " + uniqueTokens.size());
            System.out.println("Global Variables: " + globalVariables);
            System.out.println("Local Variables: " + localVariables);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static boolean isVariableDeclaration(Token token) {
        return token.getType() == Token.Type.KEYWORD && "poora".equals(token.getValue());
    }

    private static void extractVariables(List<Token> tokens, int startIndex, boolean foundMain,
            Set<String> globalVariables, Set<String> localVariables) {
        int j = startIndex;
        while (j < tokens.size()) {
            Token currentToken = tokens.get(j);

            // Stop on special characters like "!" (end of declaration)
            if (currentToken.getValue().equals("!")) {
                break;
            }

            // Only process IDENTIFIERS as variables
            if (currentToken.getType() == Token.Type.IDENTIFIER) {
                String varName = currentToken.getValue();

                // Don't classify function names as variables
                if (!varName.equals("main")) {
                    if (!foundMain) { // If we haven't reached main, it's GLOBAL
                        globalVariables.add(varName);
                    } else { // After main, it's LOCAL
                        localVariables.add(varName);
                    }
                }
            }
            j++;
        }
    }

    private static String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
