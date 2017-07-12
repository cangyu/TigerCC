package compiler.Main;

import java.io.*;
import compiler.Syntactic.Lexer;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		String fn = args[0];
		InputStream inp = new FileInputStream(fn);
		char c = ' ';

		for (;;)
		{
			int tmp = inp.read();
			if (tmp == -1)
				break;
			else
				c = (char) tmp;

			System.out.print(c);
		}
		
		inp.close();
	}
}
