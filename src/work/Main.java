package work;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Main {
	public static void main(String[] args) {
        Lexer lexer = new Lexer();
       
        System.out.println("Creating NFA for Keywords...");
        NFA keywordNFA = buildKeywordNFA();
        keywordNFA.displayNFA();  

        System.out.println("\nCreating NFA for Identifiers...");
        NFA identifierNFA = buildIdentifierNFA();
        identifierNFA.displayNFA(); 
        
        System.out.println("\nCreating NFA for Punctuators...");
        NFA punctuatorNFA = buildPunctuatorNFA();
        punctuatorNFA.displayNFA();

        System.out.println("\nCreating NFA for Strings...");
        NFA stringNFA = buildStringNFA();
        stringNFA.displayNFA();
        
        // Call newly created functions
        System.out.println("\nCreating NFA for Numbers...");
        NFA numberNFA = buildSnumberNFA();
        numberNFA.displayNFA();  // Display the NFA for numbers
        
        System.out.println("\nCreating NFA for Operators...");
        NFA operatorNFA = buildSoperatorNFA();
        operatorNFA.displayNFA();  // Display the NFA for operators
        
        System.out.println("\nCreating NFA for Single-line Comments...");
        NFA singleCommentNFA = buildSinglecommentNFA();
        singleCommentNFA.displayNFA();  // Display the NFA for single-line comments
        
        System.out.println("\nCreating NFA for Multi-line Comments...");
        NFA multiCommentNFA = buildMulticommentNFA();
        multiCommentNFA.displayNFA();  // Display the NFA for multi-line comments
        
        System.out.println("\n✅ Creating Combined NFA for all tokens...");
        NFA combinedNFA = buildCombinedNFA();
        combinedNFA.displayNFA();  // Assuming the NFA class has a display method
        
        System.out.println("\n");

        // ✅ **New Step: Convert NFA to DFA**
        System.out.println("\n✅ Converting Combined NFA to DFA...");
        DFA dfa = new DFA(combinedNFA);

        // ✅ **New Step: Print DFA Transition Table**
        System.out.println("\n✅ Printing DFA Transition Table...");
        dfa.printDFA();

        // ✅ **Continue Existing Code Below**
        String relativePath = "D:\\eclispe\\Compiler-Construction\\src\\input.sn";
        File file = new File(relativePath);
        if (!file.exists()) {
            System.out.println("Error: File '" + relativePath + "' not found.");
            System.exit(1);
        }

        try {
            String content = readFile(relativePath);
            List<Token> fileTokens = lexer.tokenize(content);

            if (fileTokens == null || fileTokens.isEmpty()) {
                System.out.println("Error: No tokens generated.");
                return;
            }
            Set<Token> uniqueTokens = new HashSet<>();
            Set<String> globalVariables = new HashSet<>();
            Set<String> localVariables = new HashSet<>();
            boolean insideFunction = false;
            boolean foundMain = false;
            Set<String> functionNames = new HashSet<>();

            for (int i = 0; i < fileTokens.size(); i++) {
                Token token = fileTokens.get(i);
                System.out.println(token);
                uniqueTokens.add(token);

                if (token.getType() == Token.Type.KEYWORD && "poora".equals(token.getValue()) && i + 1 < fileTokens.size()) {
                    Token nextToken = fileTokens.get(i + 1);
                    if (nextToken.getType() == Token.Type.IDENTIFIER) {
                        functionNames.add(nextToken.getValue());
                        if (nextToken.getValue().equals("main")) {
                            foundMain = true; 
                        }
                    }
                }

                if (token.getValue().equals("{") && !functionNames.isEmpty()) {
                    insideFunction = true;
                }
                if (token.getValue().equals("}")) {
                    insideFunction = false;
                }
                if (isVariableDeclaration(token)) {
                    extractVariables(fileTokens, i + 1, foundMain, globalVariables, localVariables);
                }
            }

            System.out.println("\nTotal Tokens: " + fileTokens.size());
            System.out.println("Unique Tokens: " + uniqueTokens.size());
            System.out.println("Global Variables: " + globalVariables);
            System.out.println("Local Variables: " + localVariables);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
		
    // Build NFA for Keywords
    public static NFA buildKeywordNFA() {
        String keywordsPattern = Lexer.GEtKeyword(); // Using the getter method from Lexer
        NFA result = null;

        Pattern pattern = Pattern.compile(keywordsPattern);
        Matcher matcher = pattern.matcher(keywordsPattern);

        while (matcher.find()) {
            String keyword = matcher.group();
            NFA wordNFA = null;

            for (char c : keyword.toCharArray()) {
                if (wordNFA == null) {
                    wordNFA = NFA.createSimpleNFA(c,"keyword");
                } else {
                    wordNFA = NFA.concatenate(wordNFA, NFA.createSimpleNFA(c,"keyword"));
                }
            }

            result = (result == null) ? wordNFA : NFA.union(result, wordNFA);
        }
        return result;
    }

    // Build NFA for Identifiers
    public static NFA buildIdentifierNFA() {
        // Identifier pattern: [a-z][a-z]*
        NFA firstLetter = NFA.union(
            NFA.createSimpleNFA('a',"identifier"), NFA.createSimpleNFA('b',"identifier"),
            NFA.createSimpleNFA('c',"identifier"), NFA.createSimpleNFA('d',"identifier"),
            NFA.createSimpleNFA('e',"identifier"), NFA.createSimpleNFA('f',"identifier"),
            NFA.createSimpleNFA('g',"identifier"), NFA.createSimpleNFA('h',"identifier"),
            NFA.createSimpleNFA('i',"identifier"), NFA.createSimpleNFA('j',"identifier"),
            NFA.createSimpleNFA('k',"identifier"), NFA.createSimpleNFA('l',"identifier"),
            NFA.createSimpleNFA('m',"identifier"), NFA.createSimpleNFA('n',"identifier"),
            NFA.createSimpleNFA('o',"identifier"), NFA.createSimpleNFA('p',"identifier"),
            NFA.createSimpleNFA('q',"identifier"), NFA.createSimpleNFA('r',"identifier"),
            NFA.createSimpleNFA('s',"identifier"), NFA.createSimpleNFA('t',"identifier"),
            NFA.createSimpleNFA('u',"identifier"), NFA.createSimpleNFA('v',"identifier"),
            NFA.createSimpleNFA('w',"identifier"), NFA.createSimpleNFA('x',"identifier"),
            NFA.createSimpleNFA('y',"identifier"), NFA.createSimpleNFA('z',"identifier")
        );

        // Repeat for additional lowercase letters [a-z]*
        NFA repeat = NFA.kleeneStar(firstLetter);

        // Concatenating first letter + repeated letters
        return NFA.concatenate(firstLetter, repeat);
    }


    // Check if the token is a variable declaration
    private static boolean isVariableDeclaration(Token token) {
        return token.getType() == Token.Type.KEYWORD && "poora".equals(token.getValue());
    }

    // Extract global and local variables based on function scope
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

    // Method to read the file content
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
    
 // Build NFA for Punctuators
    public static NFA buildPunctuatorNFA() {
        // Punctuators pattern: We are considering a few common punctuation marks (can extend as needed)
        String punctuatorsPattern = "[+\\-*/%=<>!\\^&|,;:.(){}\\[\\]]";
        NFA result = null;

        // Iterate through each punctuator
        Pattern pattern = Pattern.compile(punctuatorsPattern);
        Matcher matcher = pattern.matcher(punctuatorsPattern);

        while (matcher.find()) {
            String punctuator = matcher.group();
            NFA punctuatorNFA = NFA.createSimpleNFA(punctuator.charAt(0),"punctuator"); // Each punctuator is a single character NFA

            result = (result == null) ? punctuatorNFA : NFA.union(result, punctuatorNFA);
        }

        return result;
    }

    // Build NFA for Strings
    public static NFA buildStringNFA() {
        // Start and End Quote
        NFA quote = NFA.createSimpleNFA('\"',"String");

        // Valid string characters: a-z, A-Z, 0-9, punctuation, space
        NFA validChar = NFA.union(
            NFA.createSimpleNFA('a',"String"), NFA.createSimpleNFA('b',"String"),
            NFA.createSimpleNFA('c',"String"), NFA.createSimpleNFA('d',"String"),
            NFA.createSimpleNFA('e',"String"), NFA.createSimpleNFA('f',"String"),
            NFA.createSimpleNFA('g',"String"), NFA.createSimpleNFA('h',"String"),
            NFA.createSimpleNFA('i',"String"), NFA.createSimpleNFA('j',"String"),
            NFA.createSimpleNFA('k',"String"), NFA.createSimpleNFA('l',"String"),
            NFA.createSimpleNFA('m',"String"), NFA.createSimpleNFA('n',"String"),
            NFA.createSimpleNFA('o',"String"), NFA.createSimpleNFA('p',"String"),
            NFA.createSimpleNFA('q',"String"), NFA.createSimpleNFA('r',"String"),
            NFA.createSimpleNFA('s',"String"), NFA.createSimpleNFA('t',"String"),
            NFA.createSimpleNFA('u',"String"), NFA.createSimpleNFA('v',"String"),
            NFA.createSimpleNFA('w',"String"), NFA.createSimpleNFA('x',"String"),
            NFA.createSimpleNFA('y',"String"), NFA.createSimpleNFA('z',"String"),
            NFA.createSimpleNFA('0',"String"), NFA.createSimpleNFA('1',"String"),
            NFA.createSimpleNFA('2',"String"), NFA.createSimpleNFA('3',"String"),
            NFA.createSimpleNFA('4',"String"), NFA.createSimpleNFA('5',"String"),
            NFA.createSimpleNFA('6',"String"), NFA.createSimpleNFA('7',"String"),
            NFA.createSimpleNFA('8',"String"), NFA.createSimpleNFA('9',"String"),
            NFA.createSimpleNFA('!',"String"), NFA.createSimpleNFA('@',"String"),
            NFA.createSimpleNFA('#',"String"), NFA.createSimpleNFA('$',"String"),
            NFA.createSimpleNFA('%',"String"), NFA.createSimpleNFA('^',"String"),
            NFA.createSimpleNFA('&',"String"), NFA.createSimpleNFA('*',"String"),
            NFA.createSimpleNFA('(',"String"), NFA.createSimpleNFA(')',"String"),
            NFA.createSimpleNFA('-',"String"), NFA.createSimpleNFA('_',"String"),
            NFA.createSimpleNFA('+',"String"), NFA.createSimpleNFA('=',"String"),
            NFA.createSimpleNFA('{',"String"), NFA.createSimpleNFA('}',"String"),
            NFA.createSimpleNFA('[',"String"), NFA.createSimpleNFA(']',"String"),
            NFA.createSimpleNFA(',',"String"), NFA.createSimpleNFA('.',"String"),
            NFA.createSimpleNFA('?',"String"), NFA.createSimpleNFA('/',"String"),
            NFA.createSimpleNFA('\\',"String"), NFA.createSimpleNFA('|',"String"),
            NFA.createSimpleNFA(' ',"String"),  // Space inside string
            NFA.createSimpleNFA(':',"String")   // Colon for file paths
        );

        // Repeat any valid character inside the string
        NFA repeatValidChar = NFA.kleeneStar(validChar);

        // Concatenating: Start quote → Content → End quote
        return NFA.concatenate(quote, NFA.concatenate(repeatValidChar, quote));
    }

//    public static NFA buildSnumberNFA() {
//        // Number pattern: [0-9]+(\\.[0-9]+)?([eE][-+]?[0-9]+)?
//        NFA digits = NFA.createSimpleNFA('0');  // Start with digit '0', can be extended to [0-9] or more digits
//        NFA decimalPart = NFA.createSimpleNFA('.');  // Decimal point
//        NFA exponent = NFA.createSimpleNFA('e');  // Exponent part ('e' or 'E')
//
//        NFA numberPart = NFA.createSimpleNFA('1');  // Basic representation of number part
//        NFA repeatDigits = NFA.kleeneStar(NFA.createSimpleNFA('0')); // Digits repetition
//        
//        NFA exponentPart = NFA.kleeneStar(NFA.createSimpleNFA('0')); // Exponent part
//        NFA numberNFA = NFA.concatenate(numberPart, repeatDigits);
//
//        // Concatenate Number -> Optional Decimal -> Optional Exponent
//        NFA result = NFA.concatenate(numberNFA, NFA.kleeneStar(decimalPart));
//        result = NFA.concatenate(result, NFA.kleeneStar(exponentPart));
//
//        return result;
//    }

    public static NFA buildSnumberNFA() {
        // ✅ Step 1: Build the NFA for `[0-9]+`
        NFA digitNFA = null;

        // ✅ Loop through all digits ('0' to '9') and create an NFA
        for (char digit = '0'; digit <= '9'; digit++) {
            if (digitNFA == null) {
                digitNFA = NFA.createSimpleNFA(digit,"Number");
            } else {
                digitNFA = NFA.union(digitNFA, NFA.createSimpleNFA(digit,"Number"));
            }
        }

        // ✅ Step 2: Allow repetition of digits (for `[0-9]+`)
        NFA numberNFA = NFA.kleenePlus(digitNFA);  // At least one digit required

        // ✅ Step 3: Handle optional decimal part `(\\.[0-9]+)?`
        NFA dot = NFA.createSimpleNFA('.',"Number");  // Decimal point
        NFA decimalPart = NFA.concatenate(dot, NFA.kleenePlus(digitNFA));  // `.123`, `3.14`
        
        // ✅ Step 4: Handle optional exponent part `([eE][-+]?[0-9]+)?`
        NFA ePart = NFA.union(NFA.createSimpleNFA('e',"Number"), NFA.createSimpleNFA('E',"Number")); // `e` or `E`
        
        NFA sign = NFA.union(NFA.createSimpleNFA('+',"Number"), NFA.createSimpleNFA('-',"Number"));  // `+` or `-`
        NFA signedExponent = NFA.concatenate(NFA.optional(sign), NFA.kleenePlus(digitNFA)); // `e+10`, `e-3`
        
        NFA exponentPart = NFA.concatenate(ePart, signedExponent);  // Full exponent handling

        // ✅ Step 5: Combine the entire number pattern
        NFA result = NFA.concatenate(numberNFA, NFA.optional(decimalPart));  // `[0-9]+(\.[0-9]+)?`
        result = NFA.concatenate(result, NFA.optional(exponentPart));  // `[0-9]+(\.[0-9]+)?([eE][-+]?[0-9]+)?`

        return result;
    }


    
    public static NFA buildSoperatorNFA() {
        // Operator pattern: [=+\\-*/%]
        NFA equal = NFA.createSimpleNFA('=',"Operator");
        NFA plus = NFA.createSimpleNFA('+',"Operator");
        NFA minus = NFA.createSimpleNFA('-',"Operator");
        NFA multiply = NFA.createSimpleNFA('*',"Operator");
        NFA divide = NFA.createSimpleNFA('/',"Operator");
        NFA modulus = NFA.createSimpleNFA('%',"Operator");

        // Union of all operators
        NFA operatorNFA = NFA.union(equal, NFA.union(plus, NFA.union(minus, NFA.union(multiply, NFA.union(divide, modulus)))));
        
        return operatorNFA;
    }

    public static NFA buildSinglecommentNFA() {
        // Single-line comment pattern: \\byap\\b.*
        NFA commentKeyword = NFA.createSimpleNFA('y',"Comment");  // Starting character of 'yap' 
        NFA moreChars = NFA.createSimpleNFA('a',"Comment");  // Subsequent 'a' 
        NFA finalChar = NFA.createSimpleNFA('p',"Comment");  // Final 'p' of 'yap'

        NFA commentStart = NFA.concatenate(NFA.concatenate(commentKeyword, moreChars), finalChar);
        NFA anyChar = NFA.createSimpleNFA('.',"Comment"); // Any character in the comment
        NFA repeat = NFA.kleeneStar(anyChar);  // Zero or more characters after the comment start

        // Concatenate comment start -> zero or more characters in the comment
        return NFA.concatenate(commentStart, repeat);
    }

    public static NFA buildMulticommentNFA() {
        // Multi-line comment pattern: :p[\\s\\S]*?:p
        NFA startComment = NFA.createSimpleNFA(':',"Comment");
        NFA pStart = NFA.createSimpleNFA('p',"Comment"); // Comment start ':p'

        NFA spaceOrAnyChar = NFA.createSimpleNFA('.',"Comment");  // Match any character or whitespace
        NFA commentBody = NFA.kleeneStar(spaceOrAnyChar);  // Zero or more characters in comment

        NFA endComment = NFA.createSimpleNFA(':',"Comment");
        NFA pEnd = NFA.createSimpleNFA('p',"Comment"); // Comment end ':p'

        // Concatenate startComment + commentBody + endComment
        NFA result = NFA.concatenate(startComment, NFA.concatenate(pStart, NFA.concatenate(commentBody, NFA.concatenate(endComment, pEnd))));
        
        return result;
    }
    
 
    public static NFA buildCombinedNFA() {
        // Create individual NFAs
        NFA keywordNFA = buildKeywordNFA();
        NFA identifierNFA = buildIdentifierNFA();
        NFA numberNFA = buildSnumberNFA();
        NFA operatorNFA = buildSoperatorNFA();
        NFA punctuatorNFA = buildPunctuatorNFA();
        NFA stringNFA = buildStringNFA();
        NFA singleCommentNFA = buildSinglecommentNFA();
        NFA multiCommentNFA = buildMulticommentNFA();
        
        // Combine all NFAs into one using the union operator
        NFA combinedNFA = NFA.union(
            keywordNFA,
            NFA.union(
                identifierNFA,
                NFA.union(
                    numberNFA,
                    NFA.union(
                        operatorNFA,
                        NFA.union(
                            punctuatorNFA,
                            NFA.union(
                                stringNFA,
                                NFA.union(
                                    singleCommentNFA,
                                    multiCommentNFA
                                )
                            )
                        )
                    )
                )
            )
        );

        return combinedNFA;
    }



}
