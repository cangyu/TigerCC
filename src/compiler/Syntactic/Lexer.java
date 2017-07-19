package compiler.Syntactic;

import java.io.*;
import java.util.*;

public class Lexer
{
	private int line, column;
	private InputStream inp;
	private int peek, prev, next;
	public static final Hashtable<String, Token> kwtbl = new Hashtable<String, Token>(16);

	public Lexer(InputStream x)
	{
		inp = x;
		line = 1;
		column = 0;
		peek = prev = next = -1;

		kwtbl.clear();
		kwtbl.put("if", new Token(Tag.IF));
		kwtbl.put("else", new Token(Tag.ELSE));
		kwtbl.put("while", new Token(Tag.WHILE));
		kwtbl.put("for", new Token(Tag.FOR));
		kwtbl.put("continue", new Token(Tag.CONTINUE));
		kwtbl.put("break", new Token(Tag.BREAK));
		kwtbl.put("return", new Token(Tag.RETURN));
		kwtbl.put("sizeof", new Token(Tag.SIZEOF));
		kwtbl.put("typedef", new Token(Tag.TYPEDEF));
		kwtbl.put("void", new Token(Tag.VOID));
		kwtbl.put("int", new Token(Tag.INT));
		kwtbl.put("double", new Token(Tag.DOUBLE));
		kwtbl.put("float", new Token(Tag.FLOAT));
		kwtbl.put("char", new Token(Tag.CHAR));
		kwtbl.put("struct", new Token(Tag.STRUCT));
		kwtbl.put("union", new Token(Tag.UNION));
	}

	public Token next_token() throws IOException
	{
		skip_white();

		Token ret = null;
		if (peek == -1)
			ret = new Token(Tag.EOF);
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
		if (kwtbl.containsKey(s))
			ret = kwtbl.get(s);
		else
			ret = new Identifier(s);

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

	private void push_back(int ch) throws IOException
	{
		if (next == -1)
			next = ch;
		else
			throw new IOException(String.format("(%d, %d): Buffer \'next\' has been filled.", line, column));
	}

	private Token handle_num() throws IOException
	{
		if (peek == '0')
		{
			read_char();
			if (peek == 'x' || peek == 'X')
			{
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
				return new Int(val); // Hex integer
			}
			else
			{
				if (Character.isDigit(peek))
				{
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
					return new Int(val);// Octal integer
				}
				else if (peek == '.')
				{
					double x = 0, d = 10;
					for (;;)
					{
						read_char();
						if (!Character.isDigit(peek))
							break;

						x += Character.digit(peek, 10) / d;
						d *= 10;
					}
					push_back(peek);
					return new Real(x); // Decimal fraction
				}
				else
				{
					push_back(peek);
					return new Int(0); // Constant 0
				}
			}
		}
		else
		{
			int val = Character.digit(peek, 10);
			read_char();
			while (Character.isDigit(peek))
			{
				val *= 10;
				val += Character.digit(peek, 10);
				read_char();
			}

			if (peek == '.')
			{
				double x = val, d = 10;
				for (;;)
				{
					read_char();
					if (!Character.isDigit(peek))
						break;

					x += Character.digit(peek, 10) / d;
					d *= 10;
				}
				push_back(peek);
				return new Real(x); // Decimal float
			}
			else
			{
				push_back(peek);
				return new Int(val); // Decimal integer
			}
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
		Token ret = null;

		switch (peek)
		{
		case '<':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.LE);
			else if (peek == '<')
			{
				read_char();
				if (peek == '=')
					ret = new Token(Tag.SHL_ASSIGN);
				else
				{
					push_back(peek);
					ret = new Token(Tag.SHL);
				}
			}
			else
			{
				push_back(peek);
				ret = new Token(Tag.LT);
			}
			break;
		}
		case '>':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.GE);
			else if (peek == '>')
			{
				read_char();
				if (peek == '=')
					ret = new Token(Tag.SHR_ASSIGN);
				else
				{
					push_back(peek);
					ret = new Token(Tag.SHR);
				}
			}
			else
			{
				push_back(peek);
				ret = new Token(Tag.GT);
			}
			break;
		}
		case '-':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.SUB_ASSIGN);
			else if (peek == '-')
				ret = new Token(Tag.DEC);
			else if (peek == '>')
				ret = new Token(Tag.PTR);
			else
			{
				push_back(peek);
				ret = new Token(Tag.MINUS);
			}
			break;
		}
		case '&':
		{
			read_char();
			if (peek == '&')
				ret = new Token(Tag.AND);
			else if (peek == '=')
				ret = new Token(Tag.AND_ASSIGN);
			else
			{
				push_back(peek);
				ret = new Token(Tag.BIT_AND);
			}
			break;
		}
		case '+':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.ADD_ASSIGN);
			else if (peek == '+')
				ret = new Token(Tag.INC);
			else
			{
				push_back(peek);
				ret = new Token(Tag.PLUS);
			}
			break;
		}
		case '|':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.OR_ASSIGN);
			else if (peek == '|')
				ret = new Token(Tag.OR);
			else
			{
				push_back(peek);
				ret = new Token(Tag.BIT_OR);
			}
			break;
		}
		case '=':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.EQ);
			else
			{
				push_back(peek);
				ret = new Token(Tag.ASSIGN);
			}
			break;
		}

		case '^':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.XOR_ASSIGN);
			else
			{
				push_back(peek);
				ret = new Token(Tag.BIT_XOR);
			}
			break;
		}

		case '/':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.DIV_ASSIGN);
			else if (peek == '/')
			{
				for (;;)
				{
					if (column == 0 || peek == -1)
						break;

					read_char();
				}
				ret = new Token(Tag.LINECOMMENT);
			}
			else if (peek == '*')
			{
				int flag = 0;
				for (;;)
				{
					if (flag == 2 || peek == -1)
						break;

					read_char();
					if (flag == 0 && peek == '*')
						flag = 1;
					else if (flag == 1 && peek == '/')
						flag = 2;
					else
						flag = 0;
				}

				if (peek == -1 && flag != 2)
					panic("Block comment symbol doesn't match on EOF.");

				ret = new Token(Tag.BLKCOMMENT);
			}
			else
			{
				push_back(peek);
				ret = new Token(Tag.DIVIDE);
			}
			break;
		}
		case '!':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.NE);
			else
			{
				push_back(peek);
				ret = new Token(Tag.NOT);
			}
			break;
		}
		case '*':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.MUL_ASSIGN);
			else
			{
				push_back(peek);
				ret = new Token(Tag.TIMES);
			}
			break;
		}
		case '%':
		{
			read_char();
			if (peek == '=')
				ret = new Token(Tag.MOD_ASSIGN);
			else
			{
				push_back(peek);
				ret = new Token(Tag.MODULE);
			}
			break;
		}
		case '~':
			ret = new Token(Tag.BIT_NOT);
			break;
		case ',':
			ret = new Token(Tag.COMMA);
			break;
		case ';':
			ret = new Token(Tag.SEMI);
			break;
		case '.':
			ret = new Token(Tag.DOT);
			break;
		case '{':
			ret = new Token(Tag.LBRACE);
			break;
		case '}':
			ret = new Token(Tag.RBRACE);
			break;
		case '[':
			ret = new Token(Tag.LMPAREN);
			break;
		case ']':
			ret = new Token(Tag.RMPAREN);
			break;
		case '(':
			ret = new Token(Tag.LPAREN);
			break;
		case ')':
			ret = new Token(Tag.RPAREN);
			break;
		default:
			panic("Invalid operator symbol.");
		}

		return ret;
	}

	private void panic(String msg) throws IOException
	{
		throw new IOException(String.format("(Line %d, Column %d): " + msg, line, column));
	}
}
