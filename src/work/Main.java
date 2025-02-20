package work;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Main {
	public static void main(String[] args) {
        Lexer lexer = new Lexer();
       
        //System.out.println("Creating NFA for Keywords.");
        NFA keywordNFA = buildKeywordNFA();
        //keywordNFA.displayNFA();  

        //System.out.println("\nCreating NFA for Identifiers.");
        NFA identifierNFA = buildIdentifierNFA();
        //identifierNFA.displayNFA(); 
        
        //System.out.println("\nCreating NFA for Punctuators.");
        NFA punctuatorNFA = buildPunctuatorNFA();
        //punctuatorNFA.displayNFA();

        //System.out.println("\nCreating NFA for Strings.");
        NFA stringNFA = buildStringNFA();
        //stringNFA.displayNFA();
        
        // Call newly created functions
        //System.out.println("\nCreating NFA for Numbers.");
        NFA numberNFA = buildSnumberNFA();
        //numberNFA.displayNFA();  
        
        //System.out.println("\nCreating NFA for Operators.");
        NFA operatorNFA = buildSoperatorNFA();
        //operatorNFA.displayNFA();
        
        //System.out.println("\nCreating NFA for Single-line Comments.");
        NFA singleCommentNFA = buildSinglecommentNFA();
        //singleCommentNFA.displayNFA();  
        
        //System.out.println("\nCreating NFA for Multi-line Comments.");
        NFA multiCommentNFA = buildMulticommentNFA();
        //multiCommentNFA.displayNFA();  
        
        System.out.println("\nCreating Combined NFA for all tokens.");
        NFA combinedNFA = buildCombinedNFA();
        combinedNFA.displayNFA();
        
        System.out.println("\n");

        System.out.println("\n Converting Combined NFA --> DFA.");
        DFA dfa = new DFA(combinedNFA);

        System.out.println("\n Printing DFA Transition Table...");
        dfa.printDFA();
        System.out.println("\n ");
        
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
		
    public static NFA buildKeywordNFA() {
        String keywordsPattern = Lexer.GEtKeyword(); 
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

    public static NFA buildIdentifierNFA() {
       
        String identifierPattern = Lexer.GEtIdentifier(); 
        NFA firstLetter = null;
        for (char c = 'a'; c <= 'z'; c++) {
            if (String.valueOf(c).matches(identifierPattern)) { 
                NFA charNFA = NFA.createSimpleNFA(c, "identifier");
                firstLetter = (firstLetter == null) ? charNFA : NFA.union(firstLetter, charNFA);
            }
        }
        if (firstLetter == null) {
            System.out.println(" No valid first-letter characters found for Identifiers!");
            return null;
        }

        NFA repeatChar = null;
        for (char c = 'a'; c <= 'z'; c++) { 
            if (String.valueOf(c).matches(identifierPattern)) {
                NFA charNFA = NFA.createSimpleNFA(c, "identifier");
                repeatChar = (repeatChar == null) ? charNFA : NFA.union(repeatChar, charNFA);
            }
        }

        if (repeatChar == null) {
            System.out.println(" No valid repeatable characters found for Identifiers!");
            repeatChar = firstLetter; 
        }
 
        NFA repeat = NFA.kleeneStar(repeatChar);
        return NFA.concatenate(firstLetter, repeat);
    }
    private static boolean isVariableDeclaration(Token token) {
        return token.getType() == Token.Type.KEYWORD && "poora".equals(token.getValue());
    }
    private static void extractVariables(List<Token> tokens, int startIndex, boolean foundMain,
                                         Set<String> globalVariables, Set<String> localVariables) {
        int j = startIndex;
        while (j < tokens.size()) {
            Token currentToken = tokens.get(j);
            if (currentToken.getValue().equals("!")) {
                break;
            }

            if (currentToken.getType() == Token.Type.IDENTIFIER) {
                String varName = currentToken.getValue();

                if (!varName.equals("main")) {
                    if (!foundMain) { 
                        globalVariables.add(varName);
                    } else {
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
    
    public static NFA buildPunctuatorNFA() {
        //String punctuatorsPattern = "[+\\-*/%=<>!\\^&|,;:.(){}\\[\\]]";
        String punctuatorsPattern =Lexer.GEtPunctuator();
        NFA result = null;

        Pattern pattern = Pattern.compile(punctuatorsPattern);
        Matcher matcher = pattern.matcher(punctuatorsPattern);

        while (matcher.find()) {
            String punctuator = matcher.group();
            NFA punctuatorNFA = NFA.createSimpleNFA(punctuator.charAt(0),"punctuator"); 

            result = (result == null) ? punctuatorNFA : NFA.union(result, punctuatorNFA);
        }

        return result;
    }
    
    public static NFA buildStringNFA() {
        NFA quote = NFA.createSimpleNFA('\"',"String");
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
            NFA.createSimpleNFA(' ',"String"),  
            NFA.createSimpleNFA(':',"String")  
        );
        NFA repeatValidChar = NFA.kleeneStar(validChar);
        return NFA.concatenate(quote, NFA.concatenate(repeatValidChar, quote));
    }

    public static NFA buildSnumberNFA() {
  
    	
        String numberPattern = Lexer.GEtNumber();
        
        NFA digitNFA = null;
        for (char c = '0'; c <= '9'; c++) { 
            if (String.valueOf(c).matches(numberPattern)) { 
                NFA charNFA = NFA.createSimpleNFA(c, "Number");
                digitNFA = (digitNFA == null) ? charNFA : NFA.union(digitNFA, charNFA);
            }
        }
        if (digitNFA == null) {
            System.out.println("⚠️ Warning: No valid digits found for Number NFA!");
            return null;
        }

        NFA numberNFA = NFA.kleenePlus(digitNFA);
        NFA decimalPart = null;
        if (numberPattern.contains(".")) { 
            NFA dot = NFA.createSimpleNFA('.', "Number");
            decimalPart = NFA.concatenate(dot, NFA.kleenePlus(digitNFA)); 
        }

        NFA exponentPart = null;
        if (numberPattern.contains("e") || numberPattern.contains("E")) { 
            NFA ePart = NFA.union(NFA.createSimpleNFA('e', "Number"), NFA.createSimpleNFA('E', "Number"));
            NFA sign = null;
            if (numberPattern.contains("+") || numberPattern.contains("-")) 
                sign = NFA.union(NFA.createSimpleNFA('+', "Number"), NFA.createSimpleNFA('-', "Number"));
            
            NFA signedExponent = (sign == null) ? NFA.kleenePlus(digitNFA) :
                    NFA.concatenate(NFA.optional(sign), NFA.kleenePlus(digitNFA));
            exponentPart = NFA.concatenate(ePart, signedExponent);
        }
        NFA result = numberNFA;
        if (decimalPart != null) result = NFA.concatenate(result, NFA.optional(decimalPart)); 
        if (exponentPart != null) result = NFA.concatenate(result, NFA.optional(exponentPart)); 

        return result;
    }

    public static NFA buildSoperatorNFA() {
       
        String operatorPattern = Lexer.GEtOperator(); 
        NFA operatorNFA = null;
        for (char c = 33; c <= 126; c++) { 
            if (String.valueOf(c).matches(operatorPattern)) { 
                NFA charNFA = NFA.createSimpleNFA(c, "Operator");
                operatorNFA = (operatorNFA == null) ? charNFA : NFA.union(operatorNFA, charNFA);
            }
        }
        if (operatorNFA == null) {
            System.out.println("No valid operators found for Operator NFA!");
            return null;
        }

        return operatorNFA;
    }

    public static NFA buildSinglecommentNFA() {
        NFA commentKeyword = NFA.createSimpleNFA('y',"Comment");  
        NFA moreChars = NFA.createSimpleNFA('a',"Comment"); 
        NFA finalChar = NFA.createSimpleNFA('p',"Comment");  

        NFA commentStart = NFA.concatenate(NFA.concatenate(commentKeyword, moreChars), finalChar);
        NFA anyChar = NFA.createSimpleNFA('.',"Comment"); 
        NFA repeat = NFA.kleeneStar(anyChar);  
        return NFA.concatenate(commentStart, repeat);
    }

    public static NFA buildMulticommentNFA() {
        NFA startComment = NFA.createSimpleNFA(':',"Comment");
        NFA pStart = NFA.createSimpleNFA('p',"Comment");

        NFA spaceOrAnyChar = NFA.createSimpleNFA('.',"Comment");  
        NFA commentBody = NFA.kleeneStar(spaceOrAnyChar); 

        NFA endComment = NFA.createSimpleNFA(':',"Comment");
        NFA pEnd = NFA.createSimpleNFA('p',"Comment");

        // Concatenate startComment + commentBody + endComment
        NFA result = NFA.concatenate(startComment, NFA.concatenate(pStart, NFA.concatenate(commentBody, NFA.concatenate(endComment, pEnd))));
        
        return result;
    }

    public static NFA buildCombinedNFA() {
        NFA keywordNFA = buildKeywordNFA();
        NFA identifierNFA = buildIdentifierNFA();
        NFA numberNFA = buildSnumberNFA();
        NFA operatorNFA = buildSoperatorNFA();
        NFA punctuatorNFA = buildPunctuatorNFA();
        NFA stringNFA = buildStringNFA();
        NFA singleCommentNFA = buildSinglecommentNFA();
        NFA multiCommentNFA = buildMulticommentNFA();
        
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