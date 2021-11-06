package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;

public class StringNode extends Node {
	public final StringValue value;

	public StringNode(Token token) throws CodeError {
		super(token);
		try {
			this.value = new StringValue(StringUtils.unescapeString(token.content.substring(1, token.content.length() - 1)));
			this.value.setPos(this.startPos, this.endPos);
		}
		catch (RuntimeException e) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, e.getMessage(), this.startPos, this.endPos);
		}
	}

	@Override
	public Value<?> visit(Context context) {
		return this.value;
	}
}
