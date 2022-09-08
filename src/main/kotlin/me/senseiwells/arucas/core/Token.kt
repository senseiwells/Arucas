package me.senseiwells.arucas.core

import me.senseiwells.arucas.utils.LocatableTrace

class Token(val type: Type, val trace: LocatableTrace, val content: String = "") {
    override fun toString(): String {
        return "Token{type='${this.type}', content='${this.content}'}"
    }
}

sealed class TokenReader(private val tokens: List<Token>) {
    private var index = 0

    fun advance(amount: Int = 1): Token {
        this.index = this.getOffset(amount)
        return this.tokens[this.index]
    }

    fun recede(amount: Int = 1) = this.advance(-amount)

    fun peek(amount: Int = 0) = this.tokens[this.getOffset(amount)]

    fun peekType(amount: Int = 0) = this.peek(amount).type

    fun match(vararg types: Type): Token? {
        val token = this.peek()
        if (token.type in types) {
            this.advance()
            return token
        }
        return null
    }

    fun isMatch(vararg types: Type) = this.match(*types) != null

    fun isAtEnd() = this.peekType() == Type.EOF

    private fun getOffset(offset: Int): Int {
        val offsetIndex = this.index + offset
        require(offsetIndex < this.tokens.size && offsetIndex >= 0) { "Index $offsetIndex is out of bounds" }
        return offsetIndex
    }
}

enum class Type(private val asString: String? = null) {
    // Delimiters
    WHITESPACE,
    IDENTIFIER,
    EOF,
    SEMICOLON(";"),
    COLON(":"),
    COMMA(","),

    // Atoms
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    NUMBER,
    STRING,

    // Arithmetics
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    POWER("^"),

    // Boolean operators
    NOT("!"),
    AND("&&"),
    OR("||"),
    XOR("~"),

    // Bitwise
    SHIFT_LEFT("<<"),
    SHIFT_RIGHT(">>"),
    BIT_AND("&"),
    BIT_OR("|"),

    // Brackets
    LEFT_BRACKET("("),
    RIGHT_BRACKET(")"),
    LEFT_SQUARE_BRACKET("["),
    RIGHT_SQUARE_BRACKET("]"),
    LEFT_CURLY_BRACKET("{"),
    RIGHT_CURLY_BRACKET("}"),

    // Assignment operator
    ASSIGN_OPERATOR("="),
    INCREMENT("++"),
    DECREMENT("--"),

    // Comparisons
    EQUALS("=="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    MORE_THAN(">"),
    LESS_THAN_EQUAL("<="),
    MORE_THAN_EQUAL(">="),

    // Statements
    IF("if"),
    WHILE("while"),
    ELSE("else"),
    CONTINUE("continue"),
    BREAK("break"),
    VAR("var"),
    RETURN("return"),
    FUN("fun"),
    TRY("try"),
    CATCH("catch"),
    FINALLY("finally"),
    FOREACH("foreach"),
    FOR("for"),
    SWITCH("switch"),
    CASE("case"),
    DEFAULT("default"),
    CLASS("class"),
    ENUM("enum"),
    INTERFACE("interface"),
    THIS("this"),
    SUPER("super"),
    AS("as"),
    NEW("new"),
    STATIC("static"),
    READONLY("readonly"),
    OPERATOR("operator"),
    THROW("throw"),
    IMPORT("import"),
    FROM("from"),
    LOCAL("local"),

    // Dot
    DOT("."),
    POINTER("->"),
    ARBITRARY("...");

    override fun toString(): String {
        return this.asString ?: this.name
    }

    companion object {
        private val OVERRIDABLE_UNARY = setOf(
            NOT, PLUS, MINUS
        )
        private val OVERRIDABLE_BINARY = setOf(
            PLUS, MINUS, MULTIPLY, DIVIDE, POWER,
            LESS_THAN, LESS_THAN_EQUAL, MORE_THAN,
            MORE_THAN_EQUAL, EQUALS, NOT_EQUALS,
            AND, OR, XOR, SHIFT_LEFT, SHIFT_RIGHT,
            BIT_AND, BIT_OR, LEFT_SQUARE_BRACKET
        )

        fun isOperatorOverridable(parameters: Int, type: Type): Boolean {
            return when (parameters) {
                1 -> type in OVERRIDABLE_UNARY
                2 -> type in OVERRIDABLE_BINARY
                3 -> type == LEFT_SQUARE_BRACKET
                else -> false
            }
        }
    }
}