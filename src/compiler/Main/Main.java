package compiler.Main;

import java.io.*;
import compiler.Lexer.*;
import compiler.Parser.*;
import compiler.AST.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		InputStream inp = new FileInputStream(args[0]);
		compile(inp);
		inp.close();
	}

	private static void compile(InputStream ips) throws IOException
	{
		Lexer lex = new Lexer(ips);
		Parser psr = new Parser(lex);
		Program cst = psr.parse();
		if (cst != null && psr.exit_status())
			System.out.println("OK!");
		else
			System.out.println("WTF?");

		ASTBuilder bdr = new ASTBuilder(cst);
		Prog ast = bdr.get_ast();
	}
}
