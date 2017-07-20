package compiler.Lexer;

import java.io.*;

//TODO: 解析科学计数；越界处理；负数；忽略#include
public class Lexer
{
	private int line, column;
	private InputStream inp;
	private int peek, prev, next;

	public Lexer(InputStream x)
	{
		inp = x;
		line = 1;
		column = 0;
		peek = prev = next = -1;
	}

	public Token next_token() throws IOException
	{
		skip_white();

		Token ret = null;
		if (peek == -1)
			ret = build_token(Tag.EOF);
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
		if (s == "if")
			ret = build_token(Tag.IF);
		else if (s == "else")
			ret = build_token(Tag.ELSE);
		else if (s == "while")
			ret = build_token(Tag.WHILE);
		else if (s == "for")
			ret = build_token(Tag.FOR);
		else if (s == "continue")
			ret = build_token(Tag.CONTINUE);
		else if (s == "break")
			ret = build_token(Tag.BREAK);
		else if (s == "return")
			ret = build_token(Tag.RETURN);
		else if (s == "sizeof")
			ret = build_token(Tag.SIZEOF);
		else if (s == "typedef")
			ret = build_token(Tag.TYPEDEF);
		else if (s == "void")
			ret = build_token(Tag.VOID);
		else if (s == "int")
			ret = build_token(Tag.INT);
		else if (s == "double")
			ret = build_token(Tag.DOUBLE);
		else if (s == "float")
			ret = build_token(Tag.FLOAT);
		else if (s == "char")
			ret = build_token(Tag.CHAR);
		else if (s == "struct")
			ret = build_token(Tag.STRUCT);
		else if (s == "union")
			ret = build_token(Tag.UNION);
		else
			ret = new Identifier(s, line, column);

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
				return new Int(val, line, column); // Hex integer
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
					return new Int(val, line, column);// Octal integer
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
					return new Real(x, line, column); // Decimal fraction
				}
				else
				{
					push_back(peek);
					return new Int(0, line, column); // Constant 0
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
				return new Real(x, line, column); // Decimal float
			}
			else
			{
				push_back(peek);
				return new Int(val, line, column); // Decimal integer
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

		return new Char((char) tmp, line, column);
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

		return new Str(b.toString(), line, column);
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
				ret = build_token(Tag.LE);
			else if (peek == '<')
			{
				read_char();
				if (peek == '=')
					ret = build_token(Tag.SHL_ASSIGN);
				else
				{
					push_back(peek);
					ret = build_token(Tag.SHL);
				}
			}
			else
			{
				push_back(peek);
				ret = build_token(Tag.LT);
			}
			break;
		}
		case '>':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.GE);
			else if (peek == '>')
			{
				read_char();
				if (peek == '=')
					ret = build_token(Tag.SHR_ASSIGN);
				else
				{
					push_back(peek);
					ret = build_token(Tag.SHR);
				}
			}
			else
			{
				push_back(peek);
				ret = build_token(Tag.GT);
			}
			break;
		}
		case '-':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.SUB_ASSIGN);
			else if (peek == '-')
				ret = build_token(Tag.DEC);
			else if (peek == '>')
				ret = build_token(Tag.PTR);
			else
			{
				push_back(peek);
				ret = build_token(Tag.MINUS);
			}
			break;
		}
		case '&':
		{
			read_char();
			if (peek == '&')
				ret = build_token(Tag.AND);
			else if (peek == '=')
				ret = build_token(Tag.AND_ASSIGN);
			else
			{
				push_back(peek);
				ret = build_token(Tag.BIT_AND);
			}
			break;
		}
		case '+':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.ADD_ASSIGN);
			else if (peek == '+')
				ret = build_token(Tag.INC);
			else
			{
				push_back(peek);
				ret = build_token(Tag.PLUS);
			}
			break;
		}
		case '|':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.OR_ASSIGN);
			else if (peek == '|')
				ret = build_token(Tag.OR);
			else
			{
				push_back(peek);
				ret = build_token(Tag.BIT_OR);
			}
			break;
		}
		case '=':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.EQ);
			else
			{
				push_back(peek);
				ret = build_token(Tag.ASSIGN);
			}
			break;
		}

		case '^':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.XOR_ASSIGN);
			else
			{
				push_back(peek);
				ret = build_token(Tag.BIT_XOR);
			}
			break;
		}

		case '/':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.DIV_ASSIGN);
			else if (peek == '/')
			{
				for (;;)
				{
					if (column == 0 || peek == -1)
						break;

					read_char();
				}
				ret = build_token(Tag.LINECOMMENT);
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

				ret = build_token(Tag.BLKCOMMENT);
			}
			else
			{
				push_back(peek);
				ret = build_token(Tag.DIVIDE);
			}
			break;
		}
		case '!':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.NE);
			else
			{
				push_back(peek);
				ret = build_token(Tag.NOT);
			}
			break;
		}
		case '*':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.MUL_ASSIGN);
			else
			{
				push_back(peek);
				ret = build_token(Tag.TIMES);
			}
			break;
		}
		case '%':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Tag.MOD_ASSIGN);
			else
			{
				push_back(peek);
				ret = build_token(Tag.MODULE);
			}
			break;
		}
		case '~':
			ret = build_token(Tag.BIT_NOT);
			break;
		case ',':
			ret = build_token(Tag.COMMA);
			break;
		case ';':
			ret = build_token(Tag.SEMI);
			break;
		case '.':
			ret = build_token(Tag.DOT);
			break;
		case '{':
			ret = build_token(Tag.LBRACE);
			break;
		case '}':
			ret = build_token(Tag.RBRACE);
			break;
		case '[':
			ret = build_token(Tag.LMPAREN);
			break;
		case ']':
			ret = build_token(Tag.RMPAREN);
			break;
		case '(':
			ret = build_token(Tag.LPAREN);
			break;
		case ')':
			ret = build_token(Tag.RPAREN);
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

	private Token build_token(Tag type)
	{
		return new Token(type, line, column);
	}

}
