package work;

import java.util.Objects;

public class Token {
    public enum Type { KEYWORD, IDENTIFIER, NUMBER, STRING, OPERATOR, PUNCTUATOR, COMMENT, ERROR, EOF }

    private final Type type;
    private final String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    // ✅ Fix: Add getType() method
    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "Token{" + "type=" + type + ", value='" + value + "'}";
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Token token = (Token) obj;
        return type == token.type && Objects.equals(value, token.value);
    }

    public int hashCode() {
        return Objects.hash(type, value);
    }
}
