package compiler.Syntactic;

import java.io.*;
import java.util.*;

public class Lexer
{
	private int line, column;
	private InputStream inp;
	private int peek, prev, next;
	private boolean inBlkComment;

	private void panic(String msg) throws IOException
	{
		throw new IOException(String.format("(%d, %d): " + msg, line, column));
	}

	private void push_back(int ch) throws IOException
	{
		if (next == -1)
			next = ch;
		else
			throw new IOException(String.format("(%d, %d): Buffer \'next\' has been filled.", line, column));
	}

	private void read_char() throws IOException
	{
		// Previous
		prev = peek;

		// Buffered character
		if (next == -1)
			peek = inp.read();
		else
		{
			peek = next;
			next = -1;
		}

		// Handle line terminator or EOF
		if (peek == '\r')
			set_counter_to_newline();
		else if (peek == '\n')
		{
			if (prev != '\r')
				set_counter_to_newline();
		}
		else if (peek != -1)
			++column;
		else
			return;
	}

	public Token next_token() throws IOException
	{
		skip_white();

		Token ret = null;
		if (peek == -1)
		{
			if (inBlkComment)
				panic("Block comment symbol doesn't match on EOF!");
			else
				ret = new Token(Tag.EOF);
		}
		else if (peek == '_' || Character.isLetter(peek))
			ret = handle_word();
		else if (Character.isDigit(peek))
			ret = handle_num();
		else if (peek == '\'')
			ret = handle_char();
		else if (peek == '\"')
			ret = handle_str();
		else
			ret = handle_misc();

		return ret;
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

	private void set_counter_to_newline()
	{
		++line;
		column = 0;
	}

	private Token handle_word() throws IOException
	{
		// keywords or identifiers
		String s = get_word();

		Token ret = null;
		switch (s)
		{
		case "if":
			ret = new Token(Tag.IF);
		case "else":
			ret = new Token(Tag.ELSE);
		case "while":
			ret = new Token(Tag.WHILE);
		case "for":
			ret = new Token(Tag.FOR);
		case "continue":
			ret = new Token(Tag.CONTINUE);
		case "break":
			ret = new Token(Tag.BREAK);
		case "return":
			ret = new Token(Tag.RETURN);
		case "sizeof":
			ret = new Token(Tag.SIZEOF);
		case "typedef":
			ret = new Token(Tag.TYPEDEF);
		case "void":
			ret = new Token(Tag.VOID);
		case "int":
			ret = new Token(Tag.INT);
		case "double":
			ret = new Token(Tag.DOUBLE);
		case "float":
			ret = new Token(Tag.FLOAT);
		case "char":
			ret = new Token(Tag.CHAR);
		case "struct":
			ret = new Token(Tag.STRUCT);
		case "union":
			ret = new Token(Tag.UNION);
		default:
			ret = new Identifier(s);
		}

		return ret;
	}

	private String get_word() throws IOException
	{
		StringBuffer b = new StringBuffer();

		while (peek == '_' || Character.isLetterOrDigit(peek))
		{
			b.append((char) peek);
			read_char();
		}

		push_back(peek);
		return b.toString();
	}

	private Token handle_num() throws IOException
	{
		if (peek == '0')
		{
			read_char();
			if (peek == 'x' || peek == 'X')
			{
				// Hex integer
				read_char();
				if (!isHexCh(peek))
					panic("Invalid hex integer suffix.");

				int val = 0;
				while (isHexCh(peek))
				{
					val *= 16;
					val += Character.digit(peek, 16);
					read_char();
				}

				push_back(peek);
				return new Int(val);
			}
			else
			{
				// Octal integer
				int val = 0;
				while (isOctCh(peek))
				{
					val *= 8;
					val += Character.digit(peek, 8);
					read_char();
				}

				if (Character.isDigit(peek) && !isOctCh(peek))
					panic("Invalid octal constant.");

				push_back(peek);
				return new Int(val);
			}
		}
		else
		{
			// Decimal integer
			int val = Character.digit(peek, 10);
			read_char();
			while (Character.isDigit(peek))
			{
				val *= 10;
				val += Character.digit(peek, 10);
				read_char();
			}

			push_back(peek);
			return new Int(val);
		}
	}

	private boolean isHexCh(int ch)
	{
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
	}

	private boolean isOctCh(int ch)
	{
		return (ch >= '0' && ch <= '7');
	}

	private Token handle_char() throws IOException
	{
		read_char();

		int tmp = 0;
		if (peek == -1)
			panic("String symbol doesn't match on EOF.");
		else if (peek == '\'')
			panic("Empty character constant.");
		else
		{
			tmp = peek;
			read_char();
			if (peek != '\'')
				panic("Missing character terminating symbol.");
		}

		return new Char((char) tmp);
	}

	private Token handle_str() throws IOException
	{
		read_char();

		StringBuffer b = new StringBuffer();
		while (peek != -1 && peek != '\"')
		{
			b.append((char) peek);
			read_char();
		}

		if (peek == -1)
			panic("String symbol doesn't match on EOF.");

		return new Str(b.toString());
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

	public Lexer(InputStream x)
	{
		inp = x;
		line = 1;
		column = 0;
		peek = prev = next = -1;
		inBlkComment = false;
	}
}
