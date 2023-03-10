package me.senseiwells.arucas.compiler.token

open class TokenReader<T: TokenLike>(private val tokens: List<T>) {
    protected var index = 0

    open fun advance(amount: Int = 1): T {
        this.index = this.getOffset(amount)
        return this.tokens[this.index]
    }

    fun recede(amount: Int = 1) = this.advance(-amount)

    fun peek(amount: Int = 0) = this.tokens[this.getOffset(amount)]

    fun peekType(amount: Int = 0) = this.peek(amount).type

    fun match(vararg types: Type): T? {
        val token = this.peek()
        if (token.type in types) {
            this.advance()
            return token
        }
        return null
    }

    fun isMatch(vararg types: Type) = this.match(*types) != null

    fun isAtEnd() = this.peekType() == Type.EOF

    fun isInBounds(offset: Int): Boolean {
        val offsetIndex = this.index + offset
        return offsetIndex < this.tokens.size && offsetIndex >= 0
    }

    private fun getOffset(offset: Int): Int {
        val offsetIndex = this.index + offset
        require(offsetIndex < this.tokens.size && offsetIndex >= 0) { "Index $offsetIndex is out of bounds" }
        return offsetIndex
    }
}