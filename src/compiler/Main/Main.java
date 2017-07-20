package compiler.Main;

import java.io.*;

import compiler.Lexer.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		InputStream inp = new FileInputStream(args[0]);
		tokenize(inp);
		inp.close();
	}

	private static void tokenize(InputStream ips) throws IOException
	{
		Lexer lex = new Lexer(ips);
		for (;;)
		{
			Token tk = lex.next_token();
			System.out.println(tk.toString());
			if (tk.tag == Tag.EOF)
				break;
		}
	}
}
