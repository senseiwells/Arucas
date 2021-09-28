package me.senseiwells.core.tokens;

import me.senseiwells.core.lexer.Position;

public class KeyWordToken extends Token {

    public KeyWord keyWord;

    public KeyWordToken(KeyWord keyword, Position startPos, Position endPos) {
        super(Type.KEYWORD, startPos, endPos);
        this.keyWord = keyword;
    }

    public enum KeyWord {

        VAR("var"),
        AND("and"),
        OR("or"),
        NOT("not"),
        IF("if"),
        THEN("then"),
        //ELIF("elif"),
        ELSE("else"),
        WHILE("while");

        private final String name;

        KeyWord(String name) {
            this.name = name;
        }

        public static KeyWord stringToKeyWord(String word) {
            for (KeyWord keyWord : KeyWord.values())
                if (word.equals(keyWord.name))
                    return keyWord;
            return null;
        }
    }
}
