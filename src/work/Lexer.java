package work;
import java.util.*;
import java.util.regex.*;

public class Lexer {
    private static final String KEYWORDS = "\\b(poora|out|in|adha|HN|bakk|ek|wafis)\\b";
    private static final String IDENTIFIER = "\\b[a-z][a-z0-9]*\\b";
    private static final String NUMBER = "\\b[0-9]+(\\.[0-9]{1,5})?\\b";
    private static final String STRING = "\"[^\"]*\"";
    private static final String OPERATOR = "[=+\\-*/%]";
    private static final String PUNCTUATOR = "[(){}!,]";
    private static final String COMMENT_SINGLE = "\\bbol\\b.*";  
    private static final String COMMENT_MULTI = ":p[\\s\\S]*?:p";  

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
        COMMENT_MULTI + "|" + COMMENT_SINGLE + "|" + KEYWORDS + "|" + IDENTIFIER + "|" + 
        NUMBER + "|" + STRING + "|" + OPERATOR + "|" + PUNCTUATOR
    );

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

        return tokens; // ✅ Fix: Ensure it never returns null
    }
}
