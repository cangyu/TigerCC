package compiler.Lexer;

import java.io.*;

//TODO: 解析科学计数；越界处理；忽略#include
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
			ret = build_token(Token.EOF);
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
		if (s.equals("if".intern()))
			ret = build_token(Token.IF);
		else if (s.equals("else".intern()))
			ret = build_token(Token.ELSE);
		else if (s.equals("while".intern()))
			ret = build_token(Token.WHILE);
		else if (s.equals("for".intern()))
			ret = build_token(Token.FOR);
		else if (s.equals("continue".intern()))
			ret = build_token(Token.CONTINUE);
		else if (s.equals("break".intern()))
			ret = build_token(Token.BREAK);
		else if (s.equals("return".intern()))
			ret = build_token(Token.RETURN);
		else if (s.equals("sizeof".intern()))
			ret = build_token(Token.SIZEOF);
		else if (s.equals("typedef".intern()))
			ret = build_token(Token.TYPEDEF);
		else if (s.equals("void".intern()))
			ret = build_token(Token.VOID);
		else if (s.equals("int".intern()))
			ret = build_token(Token.INT);
		else if (s.equals("double".intern()))
			ret = build_token(Token.DOUBLE);
		else if (s.equals("float".intern()))
			ret = build_token(Token.FLOAT);
		else if (s.equals("char".intern()))
			ret = build_token(Token.CHAR);
		else if (s.equals("struct".intern()))
			ret = build_token(Token.STRUCT);
		else if (s.equals("union".intern()))
			ret = build_token(Token.UNION);
		else
			ret = Token.from_identifier(s, line, column);

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
				return Token.from_integer(val, line, column); // Hex integer
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
					return Token.from_integer(val, line, column);// Octal integer
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
					return Token.from_real(x, line, column); // Decimal fraction
				}
				else
				{
					push_back(peek);
					return Token.from_integer(0, line, column); // Constant 0
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
				return Token.from_real(x, line, column); // Decimal float
			}
			else
			{
				push_back(peek);
				return Token.from_integer(val, line, column); // Decimal integer
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

		return Token.from_character((char) tmp, line, column);
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

		return Token.from_string(b.toString(), line, column);
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
				ret = build_token(Token.LE);
			else if (peek == '<')
			{
				read_char();
				if (peek == '=')
					ret = build_token(Token.SHL_ASSIGN);
				else
				{
					push_back(peek);
					ret = build_token(Token.SHL);
				}
			}
			else
			{
				push_back(peek);
				ret = build_token(Token.LT);
			}
			break;
		}
		case '>':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.GE);
			else if (peek == '>')
			{
				read_char();
				if (peek == '=')
					ret = build_token(Token.SHR_ASSIGN);
				else
				{
					push_back(peek);
					ret = build_token(Token.SHR);
				}
			}
			else
			{
				push_back(peek);
				ret = build_token(Token.GT);
			}
			break;
		}
		case '-':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.SUB_ASSIGN);
			else if (peek == '-')
				ret = build_token(Token.DEC);
			else if (peek == '>')
				ret = build_token(Token.PTR);
			else
			{
				push_back(peek);
				ret = build_token(Token.MINUS);
			}
			break;
		}
		case '&':
		{
			read_char();
			if (peek == '&')
				ret = build_token(Token.AND);
			else if (peek == '=')
				ret = build_token(Token.AND_ASSIGN);
			else
			{
				push_back(peek);
				ret = build_token(Token.BIT_AND);
			}
			break;
		}
		case '+':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.ADD_ASSIGN);
			else if (peek == '+')
				ret = build_token(Token.INC);
			else
			{
				push_back(peek);
				ret = build_token(Token.PLUS);
			}
			break;
		}
		case '|':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.OR_ASSIGN);
			else if (peek == '|')
				ret = build_token(Token.OR);
			else
			{
				push_back(peek);
				ret = build_token(Token.BIT_OR);
			}
			break;
		}
		case '=':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.EQ);
			else
			{
				push_back(peek);
				ret = build_token(Token.ASSIGN);
			}
			break;
		}

		case '^':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.XOR_ASSIGN);
			else
			{
				push_back(peek);
				ret = build_token(Token.BIT_XOR);
			}
			break;
		}

		case '/':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.DIV_ASSIGN);
			else if (peek == '/')
			{
				for (;;)
				{
					if (column == 0 || peek == -1)
						break;

					read_char();
				}
				ret = build_token(Token.LINECOMMENT);
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

				ret = build_token(Token.BLKCOMMENT);
			}
			else
			{
				push_back(peek);
				ret = build_token(Token.DIVIDE);
			}
			break;
		}
		case '!':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.NE);
			else
			{
				push_back(peek);
				ret = build_token(Token.NOT);
			}
			break;
		}
		case '*':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.MUL_ASSIGN);
			else
			{
				push_back(peek);
				ret = build_token(Token.TIMES);
			}
			break;
		}
		case '%':
		{
			read_char();
			if (peek == '=')
				ret = build_token(Token.MOD_ASSIGN);
			else
			{
				push_back(peek);
				ret = build_token(Token.MODULE);
			}
			break;
		}
		case '~':
			ret = build_token(Token.BIT_NOT);
			break;
		case ',':
			ret = build_token(Token.COMMA);
			break;
		case ';':
			ret = build_token(Token.SEMI);
			break;
		case '.':
			ret = build_token(Token.DOT);
			break;
		case '{':
			ret = build_token(Token.LBRACE);
			break;
		case '}':
			ret = build_token(Token.RBRACE);
			break;
		case '[':
			ret = build_token(Token.LMPAREN);
			break;
		case ']':
			ret = build_token(Token.RMPAREN);
			break;
		case '(':
			ret = build_token(Token.LPAREN);
			break;
		case ')':
			ret = build_token(Token.RPAREN);
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

	private Token build_token(int type)
	{
		return new Token(type, line, column);
	}

}
