package compiler.Syntactic;

import java.io.*;
import java.util.*;
import java.lang.Math.*;

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
			if (peek == ' ' || peek == '\t' || peek == '\f' || peek == '\r' || peek == '\n')
				continue;
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
		do
		{
			b.append(peek);
			read_char();
		} while (peek == '_' || Character.isLetterOrDigit(peek));
		return b.toString();
	}

	private Token handle_hex_int() throws IOException
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

	private Token handle_dec_fraction() throws IOException
	{
		return null;
	}

	private Token handle_oct_int() throws IOException
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

	private Token handle_dec_int() throws IOException
	{
		int val = 0;
		while (Character.isDigit(peek))
		{
			val *= 10;
			val += Character.digit(peek, 10);
			read_char();
		}

		return new Int(val);
	}

	private Token handle_num() throws IOException
	{
		if (peek == '0')
		{
			read_char();
			if (peek == 'x' || peek == 'X')
				return handle_hex_int();
			else if (peek == '.')
				return handle_dec_fraction();
			else if (Character.isDigit(peek))
			{
				if (isOctCh(peek))
					return handle_oct_int();
				else
					throw new IOException(String.format("(%d, %d): Invalid octal suffix.", line, column));
			}
			else
				return new Int(0);
		}
		else
		{
			Int v1 = (Int) handle_dec_int();

			if (peek == '.')
			{
				read_char();
				Real v2 = (Real) handle_dec_fraction();
				if (peek == 'e' || peek == 'E')
				{
					read_char();
					Int v3 = (Int) handle_dec_int();
					double factor = 10;
					int times = v3.value;
					if (times < 0)
					{
						times = -times;
						factor = 0.1;
					}

					double val = v1.value + v2.value;
					for (int i = 0; i < times; i++)
						val *= factor;

					return new Real(val);
				}
				else
					return new Real(v1.value + v2.value);

			}
			else if (peek == 'e' || peek == 'E')
			{
				read_char();
				Int v2 = (Int) handle_dec_int();
				if (v2.value >= 0)
				{
					int val = v1.value;
					int times = v2.value;
					for (int i = 0; i < times; i++)
						val *= 10;
					return new Int(val);
				}
				else
				{
					double val = v1.value;
					int times = -v2.value;
					for (int i = 0; i < times; i++)
						val *= 0.1;
					return new Real(val);
				}
			}
			else
				return v1;
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

		return null;
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
