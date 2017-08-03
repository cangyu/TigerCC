package compiler.Main;

import java.io.*;
import compiler.Lexer.*;
import compiler.Parser.*;

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
		Parser psr = new Parser(lex);
		Program parse_tree = psr.parse();
		if (parse_tree != null && psr.exit_status())
			System.out.println("OK!");
		else
			System.out.println("WTF?");
	}
}
