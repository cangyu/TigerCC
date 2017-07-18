package compiler.Main;

import java.io.*;
import compiler.Syntactic.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		String fn = "test/simple0.c";
		InputStream inp = new FileInputStream(fn);
		Lexer lex = new Lexer(inp);

		for (;;)
		{
			Token tk = lex.next_token();
			System.out.println(tk.toString());

			if (tk.tag == Tag.EOF)
				break;
		}
		
		inp.close();
	}
}
