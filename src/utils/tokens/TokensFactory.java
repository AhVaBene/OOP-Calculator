package utils.tokens;

import utils.calculate.Function;
import utils.calculate.Operator;

/**
 * A static factory of tokens.
 *
 */
public final class TokensFactory {
    private TokensFactory() {
    }

    /**
     * @return close Parantehesis token
     */
    public static Token closeParToken() {
        return new Token() {

            @Override
            public TokenType getTypeToken() {
                return TokenType.CLOSEPAR;
            }

            @Override
            public String getSymbol() {
                return ")";
            }

        };
    }

    /**
     * @return open Parenthesis token.
     */
    public static Token openParToken() {
        return new Token() {

            @Override
            public TokenType getTypeToken() {
                return TokenType.OPENPAR;
            }

            @Override
            public String getSymbol() {
                return "(";
            }

        };
    }

    /**
     * @param func
     * @return a function token given the function name
     */
    public static Token functionToken(final Function func) {
        return new SpecialToken<Function>() {

            @Override
            public TokenType getTypeToken() {
                return TokenType.FUNCTION;
            }

            @Override
            public String getSymbol() {
                return func.getName();
            }

            @Override
            public Function getObjectToken() {
                return func;
            }

        };
    }

    /**
     * @param value
     * @return a number token given its value
     */
    public static Token numberToken(final double value) {
        return new SpecialToken<Double>() {

            @Override
            public TokenType getTypeToken() {
                return TokenType.NUMBER;
            }

            @Override
            public String getSymbol() {
                return String.valueOf(value);
            }

            @Override
            public Double getObjectToken() {
                return value;
            }

        };
    }

    /**
     * @param constant
     * @return a constant Token
     */
    public static Token constantToken(final String constant) {
        return new Token() {

            @Override
            public TokenType getTypeToken() {
                return TokenType.CONSTANT;
            }

            @Override
            public String getSymbol() {
                return constant;
            }

        };
    }

    /**
     * @param operator
     * @return an Operator token given the operator
     */
    public static Token operatorToken(final Operator operator) {
        return new SpecialToken<Operator>() {

            @Override
            public TokenType getTypeToken() {
                return TokenType.OPERATOR;
            }

            @Override
            public String getSymbol() {
                return operator.getSymbol();
            }

            @Override
            public Operator getObjectToken() {
                return operator;
            }

        };
    }

    /**
     * @param variable
     * @return the variable token with the name
     */
    public static Token variableToken(final String variable) {
        return new Token() {

            @Override
            public TokenType getTypeToken() {
                return TokenType.VARIABLE;
            }

            @Override
            public String getSymbol() {
                return variable;
            }

        };
    }
}
