package work;

import java.util.*;
import java.util.regex.*;
		
public class Lexer {
    public static final String KEYWORDS = "\\b(poora|out|in|adha|HN|bakk|ek|wafis)\\b";
    public static final String IDENTIFIER = "\\b[a-z][a-z]*\\b";
    public static final String NUMBER = "[0-9]+(\\.[0-9]+)?([eE][-+]?[0-9]+)?"; 
    public static final String OPERATOR = "[=+\\-*/%]";
    public static final String STRING = "\"[^\"]*\"";
    public static final String PUNCTUATOR = "[(){}!,]";
    public static final String COMMENT_SINGLE = "\\byap\\b.*";  
    public static final String COMMENT_MULTI = ":p[\\s\\S]*?:p";  

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
        COMMENT_MULTI + "|" + COMMENT_SINGLE + "|" + KEYWORDS + "|" + IDENTIFIER + "|" + 
        NUMBER + "|" + STRING + "|" + OPERATOR + "|" + PUNCTUATOR
    );

    // Getter methods for each constant
    public static String GEtKeyword() {
        return KEYWORDS;
    }

    public static String GEtIdentifier() {
        return IDENTIFIER;
    }

    public static String GEtNumber() {
        return NUMBER;
    }

    public static String GEtOperator() {
        return OPERATOR;
    }

    public static String GEtString() {
        return STRING;
    }

    public static String GEtPunctuator() {
        return PUNCTUATOR;
    }

    public static String GEtCommentSingle() {
        return COMMENT_SINGLE;
    }

    public static String GEtCommentMulti() {
        return COMMENT_MULTI;
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);

        while (matcher.find()) {
            String token = matcher.group();

            if (token.matches(COMMENT_MULTI) || token.matches(COMMENT_SINGLE)) {
                tokens.add(new Token(Token.Type.COMMENT, "COMMENT"));
            } else if (token.matches(KEYWORDS)) {
                tokens.add(new Token(Token.Type.KEYWORD, token));
            } else if (token.matches(IDENTIFIER)) {
                tokens.add(new Token(Token.Type.IDENTIFIER, token));
            } else if (token.matches(NUMBER)) {
                tokens.add(new Token(Token.Type.NUMBER, token));
            } else if (token.matches(STRING)) {
                tokens.add(new Token(Token.Type.STRING, token));
            } else if (token.matches(OPERATOR)) {
                tokens.add(new Token(Token.Type.OPERATOR, token));
            } else if (token.matches(PUNCTUATOR)) {
                tokens.add(new Token(Token.Type.PUNCTUATOR, token));
            } else {
                tokens.add(new Token(Token.Type.ERROR, token));
            }
        }

        return tokens; 
    }
}
