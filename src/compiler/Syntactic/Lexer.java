package compiler.Syntactic;

import java.io.*;
import java.util.*;

public class Lexer
{
	private int line, column;
	private InputStream inp;
	private int peek, prev;

	public Token next_token() throws IOException
	{
		skip_white();
		if (peek == -1)
			return new Token(Tag.EOF);
		else if (peek == '_' || Character.isLetter(peek))
			return handle_word();
		else if (Character.isDigit(peek)) // numbers
			return handle_num();
		else if (peek == '\'') // char literal
			return handle_char();
		else if (peek == '\"') // string literal
			return handle_str();
		else
			return handle_misc();
	}

	private void skip_white() throws IOException
	{
		for (;;)
		{
			read_char();
			if (peek != ' ' && peek != '\t' && peek != '\f' && peek != '\r' && peek != '\n')
				break;
		}
	}

	private void read_char() throws IOException
	{
		prev = peek;
		peek = inp.read();

		// Handle line terminator or EOF
		if (peek == -1)
			inp.close();
		else if (peek == '\r')
			set_counter_to_newline();
		else if (peek == '\n')
		{
			if (prev != '\r')
				set_counter_to_newline();
		}
		else
			++column;
	}

	private void set_counter_to_newline()
	{
		++line;
		column = 0;
	}

	private Token handle_word() throws IOException
	{
		// keywords or identifiers
		String s = get_word();
		switch (s)
		{
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

	private String get_word() throws IOException
	{
		StringBuffer b = new StringBuffer();
		while (peek == '_' || Character.isLetterOrDigit(peek))
		{
			b.append((char) peek);
			read_char();
		}
		return b.toString();
	}

	private Token handle_num() throws IOException
	{
		if (peek == '0')
		{
			read_char();
			if (peek == 'x' || peek == 'X') // Hex integer
			{
				read_char();
				if (!isHexCh(peek))
					throw new IOException(String.format("(%d, %d): Invalid hex integer suffix.", line, column));

				int val = 0;
				while (isHexCh(peek))
				{
					val *= 16;
					val += Character.digit(peek, 16);
					read_char();
				}
				return new Int(val);
			}
			else // Octal integer
			{
				int val = 0;
				while (isOctCh(peek))
				{
					val *= 8;
					val += Character.digit(peek, 8);
					read_char();
				}
				return new Int(val);
			}
		}
		else // Decimal integer
		{
			int val = Character.digit(peek, 10);
			read_char();
			while (Character.isDigit(peek))
			{
				val *= 10;
				val += Character.digit(peek, 10);
				read_char();
			}
			return new Int(val);
		}
	}

	private boolean isHexCh(int ch)
	{
		return (ch >= 48 && ch < 58) || (ch >= 65 && ch < 71) || (ch >= 97 && ch < 103);
	}

	private boolean isOctCh(int ch)
	{
		return (ch >= 48 && ch < 56);
	}

	private Token handle_misc() throws IOException
	{
		// operators, comments, annotations
		switch (peek)
		{
		case '<':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.LE);
			else if (peek == '<')
			{
				read_char();
				if (peek == '=')
					return new Token(Tag.SHL_ASSIGN);
				else
					return new Token(Tag.SHL);
			}
			else
				return new Token(Tag.LT);
		}
		case '>':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.GE);
			else if (peek == '>')
			{
				read_char();
				if (peek == '=')
					return new Token(Tag.SHR_ASSIGN);
				else
					return new Token(Tag.SHR);
			}
			else
				return new Token(Tag.GT);
		}
		case '-':
		{
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
		case '&':
		{
			read_char();
			if (peek == '&')
				return new Token(Tag.AND);
			else if (peek == '=')
				return new Token(Tag.AND_ASSIGN);
			else
				return new Token(Tag.BIT_AND);
		}
		case '+':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.ADD_ASSIGN);
			else if (peek == '+')
				return new Token(Tag.INC);
			else
				return new Token(Tag.PLUS);
		}
		case '|':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.OR_ASSIGN);
			else if (peek == '|')
				return new Token(Tag.OR);
			else
				return new Token(Tag.BIT_OR);
		}
		case '=':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.EQ);
			else
				return new Token(Tag.ASSIGN);
		}

		case '^':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.XOR_ASSIGN);
			else
				return new Token(Tag.BIT_XOR);
		}

		case '/':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.DIV_ASSIGN);
			else
				return new Token(Tag.DIVIDE);
		}
		case '!':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.NE);
			else
				return new Token(Tag.NOT);
		}
		case '*':
		{
			read_char();
			if (peek == '=')
				return new Token(Tag.MUL_ASSIGN);
			else
				return new Token(Tag.TIMES);
		}
		case '%':
		{
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

	private Token handle_char() throws IOException
	{
		read_char();
		char tmp = (char) peek;
		read_char();
		if (peek != '\'')
			throw new IOException("missing terminating ' character");

		return new Char(tmp);
	}

	private Token handle_str() throws IOException
	{
		read_char();
		String tmp = "";
		while (peek != '\"')
		{
			tmp += peek;
			read_char();
		}

		return new Str(tmp);
	}

	public Lexer(InputStream x)
	{
		inp = x;
		line = 1;
		column = 0;
		peek = -1;
		prev = -1;
	}
}
