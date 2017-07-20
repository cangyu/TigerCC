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

	public void parse() throws IOException
	{
		
	}

	private void match(Tag t) throws IOException
	{
		if (look.tag == t)
			move();
		else
			panic("Syntax error.");
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
}
