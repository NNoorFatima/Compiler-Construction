package work;

public class REtoNFA {
    
    // Build NFA for Keywords
    public static NFA buildKeywordNFA() {
        String keywordsPattern = Lexer.GEtKeyword(); // Assuming Lexer.GEtKeyword() returns the regex for keywords
        
        String[] keywords = keywordsPattern.substring(3, keywordsPattern.length() - 2).split("\\|");
        NFA result = null;

        for (String word : keywords) {
            NFA wordNFA = null;
            for (char c : word.toCharArray()) {
                if (wordNFA == null) {
                    wordNFA = NFA.createSimpleNFA(c);
                } else {
                    wordNFA = NFA.concatenate(wordNFA, NFA.createSimpleNFA(c));
                }
            }
            if (result == null) {
                result = wordNFA;
            } else {
                result = NFA.union(result, wordNFA);
            }
        }
        return result;
    }

    // Build NFA for Identifiers (lowercase letters followed by more lowercase letters)
    public static NFA buildIdentifierNFA() {
        NFA firstLetter = NFA.createSimpleNFA('a');  // Matches lowercase 'a', adjust for the full range [a-z]
        NFA repeat = NFA.kleeneStar(NFA.createSimpleNFA('a'));  // Matches any lowercase letter a-z
        return NFA.concatenate(firstLetter, repeat); // Concatenates the first letter with the repeat pattern
    }

    // Build NFA for Numbers (matches integers, decimals, and scientific notation)
    public static NFA buildNumberNFA() {
        NFA integerPart = NFA.createSimpleNFA('0'); // Matches the leading 0 (or you could add more logic for [1-9])
        NFA decimalPart = NFA.createSimpleNFA('.'); // Matches decimal part
        NFA exponentPart = NFA.createSimpleNFA('e');  // Matches scientific notation part
        
        // Build the number NFA with multiple states and transitions
        NFA result = NFA.concatenate(integerPart, decimalPart); // For example, "0." or "10."
        return result; // Expand this to cover other cases of number patterns.
    }

    // Build NFA for Operators (matches arithmetic operators)
    public static NFA buildOperatorNFA() {
        NFA plus = NFA.createSimpleNFA('+');
        NFA minus = NFA.createSimpleNFA('-');
        NFA multiply = NFA.createSimpleNFA('*');
        NFA divide = NFA.createSimpleNFA('/');
        NFA modulo = NFA.createSimpleNFA('%');
        NFA equals = NFA.createSimpleNFA('=');

        // Union of all operators
        NFA result = NFA.union(plus, NFA.union(minus, NFA.union(multiply, NFA.union(divide, NFA.union(modulo, equals)))));
        return result;
    }

    // Build NFA for Strings (matches text inside double quotes)
    public static NFA buildStringNFA() {
        NFA quote = NFA.createSimpleNFA('"');
        NFA content = NFA.createSimpleNFA('a');  // Here you can add the logic for any character inside the quotes
        NFA result = NFA.concatenate(quote, NFA.concatenate(content, quote)); // Matches a string enclosed in quotes
        return result;
    }

    // Build NFA for Punctuators (matches characters like (, ), {, }, etc.)
    public static NFA buildPunctuatorNFA() {
        NFA leftParen = NFA.createSimpleNFA('(');
        NFA rightParen = NFA.createSimpleNFA(')');
        NFA leftBrace = NFA.createSimpleNFA('{');
        NFA rightBrace = NFA.createSimpleNFA('}');
        NFA comma = NFA.createSimpleNFA(',');

        // Union of all punctuators
        NFA result = NFA.union(leftParen, NFA.union(rightParen, NFA.union(leftBrace, NFA.union(rightBrace, comma))));
        return result;
    }

    // Build NFA for Single-line Comments (matches "yap" followed by any characters)
    public static NFA buildSingleLineCommentNFA() {
        NFA yap = NFA.createSimpleNFA('y');
        NFA rest = NFA.kleeneStar(NFA.createSimpleNFA(' '));  // Any characters after "yap"
        return NFA.concatenate(yap, rest);
    }

    // Build NFA for Multi-line Comments (matches ":p" followed by any characters)
    public static NFA buildMultiLineCommentNFA() {
        NFA start = NFA.createSimpleNFA(':');
        NFA end = NFA.createSimpleNFA('p');
        NFA content = NFA.kleeneStar(NFA.createSimpleNFA(' '));  // Matches any characters inside the comment

        return NFA.concatenate(start, NFA.concatenate(end, content));  // Matches ":p" followed by any content
    }
    
    
}
