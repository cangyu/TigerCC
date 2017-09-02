package compiler.Main;

import java.io.*;
import java.util.*;
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

	private static void compile(InputStream ips) throws Exception
	{
		// Syntactic
		Lexer lex = new Lexer(ips);
		Parser psr = new Parser(lex);
		Program cst = psr.parse();
		if (cst != null && psr.exit_status())
			System.out.println("OK!");
		else
			System.out.println("WTF?");

		// Semantic
		ASTBuilder ast_bdr = new ASTBuilder();
		Prog ast = ast_bdr.build(cst);
		System.out.println(ast != null ? "OK!" : "WTF?");
		ASTPrinter ap = new ASTPrinter(ast);
		ap.print();
		PrettyPrinter pp = new PrettyPrinter(ast);
		pp.print();
	}
}
