package compiler.Syntactic;

import java.io.*;
import java.util.*;

public class Lexer {
    private int line, column;
    private InputStream inp;
    private int peek, prev;

    private void read_char() throws IOException {
	if (prev != -1) {
	    peek = prev;
	    prev = -1;
	} else
	    peek = inp.read();

	if (peek == '\n') {
	    column = 0;
	    ++line;
	} else if (peek == -1)
	    inp.close();
	else
	    ++column;
    }

    private void skip_white() throws IOException {
	for (;;) {
	    read_char();
	    if (peek != ' ' && peek != '\t' && peek != '\n')
		break;
	}
    }

    private String get_str() throws IOException {
	StringBuffer b = new StringBuffer();
	while (Character.isLetter(peek)) {
	    b.append(peek);
	    read_char();
	}

	return b.toString();
    }

    private Token handle_str() throws IOException {
	String s = get_str();
	switch (s) {
	case "if":
	    return new Token(Tag.IF);
	case "else":
	    return new Token(Tag.ELSE);
	case "while":
	    return new Token(Tag.WHILE);
	case "for":
	    return new Token(Tag.FOR);
	case "continue":
	    return new Token(Tag.CONTINUE);
	case "break":
	    return new Token(Tag.BREAK);
	case "return":
	    return new Token(Tag.RETURN);
	case "sizeof":
	    return new Token(Tag.SIZEOF);
	case "typedef":
	    return new Token(Tag.TYPEDEF);
	case "void":
	    return new Token(Tag.VOID);
	case "int":
	    return new Token(Tag.INT);
	case "double":
	    return new Token(Tag.DOUBLE);
	case "float":
	    return new Token(Tag.FLOAT);
	case "char":
	    return new Token(Tag.CHAR);
	case "struct":
	    return new Token(Tag.STRUCT);
	case "union":
	    return new Token(Tag.UNION);
	default:
	    return new Identifier(s);
	}
    }

    private Token handle_num() throws IOException {
	if (peek == '0') {
	    read_char();
	    if (peek == 'x' || peek == 'X') // Hex
	    {

	    } else // Oct
	    {

	    }
	} else {
	    int val = 0;
	    do {
		val *= 10;
		val += Character.digit(peek, 10);
		read_char();
	    } while (Character.isDigit(peek)); // Integer

	    if (peek == '.') {
		float x = val;
		float d = 10;
		for (;;) {
		    read_char();
		    if (Character.isDigit(peek)) {
			x += Character.digit(peek, 10) / d;
			d *= 10;
		    } else
			break;
		}
		return new Real(x);
	    } else
		return new Int(val);
	}

	return null;
    }

    private Token handle_misc() throws IOException {
	// operators
	switch (peek) {
	case '<': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.LE);
	    else if (peek == '<') {
		read_char();
		if (peek == '=')
		    return new Token(Tag.SHL_ASSIGN);
		else
		    return new Token(Tag.SHL);
	    } else
		return new Token(Tag.LT);
	}
	case '>': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.GE);
	    else if (peek == '>') {
		read_char();
		if (peek == '=')
		    return new Token(Tag.SHR_ASSIGN);
		else
		    return new Token(Tag.SHR);
	    } else
		return new Token(Tag.GT);
	}
	case '-': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.SUB_ASSIGN);
	    else if (peek == '-')
		return new Token(Tag.DEC);
	    else if (peek == '>')
		return new Token(Tag.PTR);
	    else
		return new Token(Tag.MINUS);
	}
	case '&': {
	    read_char();
	    if (peek == '&')
		return new Token(Tag.AND);
	    else if (peek == '=')
		return new Token(Tag.AND_ASSIGN);
	    else
		return new Token(Tag.BIT_AND);
	}
	case '+': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.ADD_ASSIGN);
	    else if (peek == '+')
		return new Token(Tag.INC);
	    else
		return new Token(Tag.PLUS);
	}
	case '|': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.OR_ASSIGN);
	    else if (peek == '|')
		return new Token(Tag.OR);
	    else
		return new Token(Tag.BIT_OR);
	}
	case '=': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.EQ);
	    else
		return new Token(Tag.ASSIGN);
	}

	case '^': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.XOR_ASSIGN);
	    else
		return new Token(Tag.BIT_XOR);
	}

	case '/': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.DIV_ASSIGN);
	    else
		return new Token(Tag.DIVIDE);
	}
	case '!': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.NE);
	    else
		return new Token(Tag.NOT);
	}
	case '*': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.MUL_ASSIGN);
	    else
		return new Token(Tag.TIMES);
	}
	case '%': {
	    read_char();
	    if (peek == '=')
		return new Token(Tag.MOD_ASSIGN);
	    else
		return new Token(Tag.MODULE);
	}
	case '~':
	    return new Token(Tag.BIT_NOT);
	case ',':
	    return new Token(Tag.COMMA);
	case ';':
	    return new Token(Tag.SEMI);
	case '.':
	    return new Token(Tag.DOT);
	case '{':
	    return new Token(Tag.LBRACE);
	case '}':
	    return new Token(Tag.RBRACE);
	case '[':
	    return new Token(Tag.LMPAREN);
	case ']':
	    return new Token(Tag.RMPAREN);
	case '(':
	    return new Token(Tag.LPAREN);
	case ')':
	    return new Token(Tag.RPAREN);
	default:
	    return null;
	}
    }

    private Token next_token() throws IOException {
	skip_white();

	if (Character.isLetter(peek))// keywords or identifiers
	    return handle_str();
	else if (Character.isDigit(peek))// numbers
	    return handle_num();
	else
	    return handle_misc();// operator, string, char, signed-number...
    }

    public Lexer(InputStream x) {
	inp = x;
	line = 0;
	column = 0;
	peek = -1;
	prev = -1;
    }
}
