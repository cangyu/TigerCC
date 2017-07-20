package compiler.Parser;

import java.io.*;

import compiler.Lexer.*;
import compiler.Symbols.*;

public class Parser
{
	private Lexer lexer;
	private Token look;
	private Env top = null;
	private int used = 0;

	public Parser(Lexer x) throws IOException
	{
		lexer = x;
		move();
	}

	private boolean match(Tag t) throws IOException
	{
		if (look.tag == t)
		{
			move();
			return true;
		}
		else
			return false;
	}

	private void move() throws IOException
	{
		for (;;)
		{
			look = lexer.next_token();
			if (look.tag != Tag.LINECOMMENT && look.tag != Tag.BLKCOMMENT)
				break;
		}
	}

	private void panic(String msg) throws IOException
	{
		throw new IOException(String.format("(Line %d, Column %d): %s", look.line, look.column, msg));
	}

	public void parse() throws IOException
	{
		 // program: (declaration | function_definition)+
		
		if (look.tag == Tag.EOF)
			return;

		boolean t1 = declaration();
		if (!t1)
		{
			boolean t2 = function_definition();
			if (!t2)
				panic("Unable to match a declaration nor a function_definition.");
		}
	}

	private boolean declaration() throws IOException
	{

		return true;
	}

	private boolean function_definition() throws IOException
	{
		return true;
	}
}
