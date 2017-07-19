package compiler.Syntactic;

import java.io.*;

public class Parser
{
	private Lexer lexer;
	private Token ctk;

	public Parser(Lexer x)
	{
		lexer = x;
		ctk = null;
	}

	public void parse() throws IOException
	{
		for (;;)
		{
			ctk = lexer.next_token();
			if (ctk.tag == Tag.LINECOMMENT || ctk.tag == Tag.BLKCOMMENT)
				continue;

			if (ctk.tag == Tag.EOF)
				break;
		}
	}
}
