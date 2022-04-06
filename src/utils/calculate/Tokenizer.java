package utils.calculate;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import utils.tokens.SpecialToken;
import utils.tokens.Token;
import utils.tokens.TokenType;
import utils.tokens.TokensFactory;

/**
 * @author pesic
 *
 */
public class Tokenizer {

    private String expr;
    private int index = 0;
    private Token lastToken = null;
    private final int lenExpr;
    private final String variable;
    private final Set<String> constants;
    private final ExternData data = new ExternData();

    Tokenizer(final String expr) {
        this.expr = expr;
        this.lenExpr = expr.length();
        this.variable = data.getVariable();
        this.constants = data.getConstants();
    }

    /**
     * @return c
     */
    public boolean hasNextToken() {
        return this.index <= this.lenExpr - 1;
    }

    /**
     * @return c
     */
    public Token getNextToken() {
        System.out.println("Index: " + index);
        if (!hasNextToken()) {
            return null;
        }

        final char c = this.expr.charAt(index);
        if (Character.isDigit(c)) {
            if (lastToken != null) {
                System.out.println("a number");
                if (lastToken.getTypeToken().equals(TokenType.NUMBER)) {
                    throw new IllegalArgumentException("2 numbers can't stay near");
                }
                if (lastToken.getTypeToken() != TokenType.OPENPAR && lastToken.getTypeToken() != TokenType.FUNCTION
                        && lastToken.getTypeToken() != TokenType.OPERATOR) {
                    lastToken = TokensFactory.operatorToken(new Operator("*", 2, true));

                    return lastToken;
                }
            }
            System.out.println("The first character of the expression is a number");
            return getNumberToken();
        } else if (c == '(') {
            if (lastToken != null
                && lastToken.getTypeToken() != TokenType.OPENPAR && lastToken.getTypeToken() != TokenType.OPERATOR
                && lastToken.getTypeToken() != TokenType.FUNCTION) {
                    lastToken = TokensFactory.operatorToken(new Operator("*", 2, true));

                    return lastToken;
             }

            index++;
            return TokensFactory.openParToken();
        } else if (c == ')') {
            index++;
            return TokensFactory.closeParToken();
        } else if (Operator.isAllowedOperator(String.valueOf(c))) {
            return getOperationToken();
        } else if (Character.isLetter(c)) {
            System.out.println("a function or a variable");
            if (lastToken != null) {
                System.out.println("a function or a variable with something before it");
                if (lastToken.getTypeToken() != TokenType.OPERATOR && lastToken.getTypeToken() != TokenType.FUNCTION
                        && lastToken.getTypeToken() != TokenType.OPENPAR) {
                    lastToken = TokensFactory.operatorToken(Operator.getOperatorBySymbolAndArgs("*", 2));

                    return lastToken;
                }
            }
            System.out.println("the first element of the expression is a function or a variable");
            return getFunctionOrVariableToken();
        }
        throw new IllegalArgumentException("the character: " + c + " wasn't recognized ");
    }

    /**
     * @return c
     */
    public List<Token> getListToken() {
        final List<Token> out = new LinkedList<>();
        while (this.hasNextToken()) {
            out.add(this.getNextToken());
        }
        return out;
    }

    /**
     * @return c
     */
    public List<String> getListSymbol() {
        final List<Token> out = this.getListToken();
        return out.stream().map(t -> t.getSymbol()).collect(Collectors.toList());
    }
    
    public List<Token> convertToTokens(List<String> expression) {
        String newExpr = expression.stream().reduce("", (String res, String c)-> res+c);
        this.reset(newExpr);
        return this.getListToken();
    }

    /**
     * @param expr
     */
    public void reset(final String expr) {
        this.index = 0;
        this.lastToken = null;
        this.expr = expr;
    }

    private Token getNumberToken() {
        double num;
        final int ind = this.index;
        while (Character.isDigit(this.expr.charAt(index)) && index < this.lenExpr - 1) {
            index++;
        }
        if (this.expr.charAt(index) == '.') {
            index++;
        }
        while (Character.isDigit(this.expr.charAt(index)) && index < this.lenExpr - 1) {
            index++;
        }

        if (this.index - ind == 0) {
            num = Double.parseDouble(this.expr.substring(ind));
        } else {
            num = Double.parseDouble(this.expr.substring(ind, index));
        }

        final var number = TokensFactory.numberToken(num);
        lastToken = number;
        if (this.index == this.lenExpr - 1 && ind == this.index) {
            index++;
        }
        return number;
    }

    private Token getFunctionOrVariableToken() {
        int newIndex = index;
        int previousIndex = -1;
        char c = this.expr.charAt(newIndex);
        Token newToken = null;
        while (newIndex <= this.lenExpr - 1 && Character.isLetter(c)) {
            if (newIndex - index == 0) {
                if (String.valueOf(c).equals(this.variable)) {
                    newToken = TokensFactory.variableToken(this.variable);
                    previousIndex = newIndex + 1;
                } else if (constants.contains(String.valueOf(c))) {
                    newToken = TokensFactory.constantToken(String.valueOf(c));
                    previousIndex = newIndex + 1;
                }
            } else {
                if (this.expr.substring(index, newIndex + 1).equals(this.variable)) {
                    newToken = TokensFactory.variableToken(this.variable);
                    previousIndex = newIndex + 1;
                } else if (Function.isFunction(this.expr.substring(index, newIndex + 1))) {
                    newToken = TokensFactory.functionToken(Function.dictFunctions.get(this.expr.substring(index, newIndex + 1)));
                    previousIndex = newIndex + 1;
                } else if (constants.contains(this.expr.substring(index, newIndex + 1))) {
                    newToken = TokensFactory.constantToken(this.expr.substring(index, newIndex + 1));
                    previousIndex = newIndex + 1;
                }
            }
            newIndex++;
            if (newIndex <= this.lenExpr - 1) {
                c = this.expr.charAt(newIndex);
            }
        }
        if (previousIndex != -1) {
            this.index = previousIndex;

        } else {
            this.index = newIndex;
        }
        if (newToken == null) {
            throw new IllegalArgumentException("The variable name or the function doesn't exist");
        }
        lastToken = newToken;
        return newToken;

    }

    private Token getOperationToken() {
        final char c = this.expr.charAt(this.index++);
        int arguments = 2;

        if (lastToken == null) {
            arguments = 1;
        } else {
            if (lastToken.getTypeToken() == TokenType.OPENPAR) {
                arguments = 1;
            } else if (lastToken.getTypeToken() == TokenType.OPERATOR) {
                @SuppressWarnings("unchecked")
                final Operator op = ((SpecialToken<Operator>) lastToken).getObjectToken();
                if (op.getNumOperands() == 2 || op.getNumOperands() == 1 && !op.isLeftAssociative()) {
                    arguments = 1;
                }
            }
        }

        final var newOp = Operator.getOperatorBySymbolAndArgs(String.valueOf(c), arguments);
        lastToken = TokensFactory.operatorToken(newOp);
        return lastToken;
    }

    public static void main(String[] args) {
        var tok = new Tokenizer("sin(5)");
        while (tok.hasNextToken()) {
            Token t = tok.getNextToken();
            System.out.println(t.getTypeToken());
        }
    }

}
