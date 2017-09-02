package compiler.Main;

import java.io.*;
import compiler.Lexer.*;
import compiler.Parser.*;
import compiler.AST.*;

public class Main
{
	private static String ok = "OK!".intern();
	private static String wtf = "WTF?".intern();

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
		boolean parsing_ok = cst != null && psr.exit_status();
		System.out.println(parsing_ok ? ok : wtf);

		// Semantic
		ASTBuilder ast_bdr = new ASTBuilder();
		Prog ast = ast_bdr.build(cst);
		boolean ast_ok = ast != null;
		System.out.println(ast_ok ? ok : wtf);
		ASTPrinter ap = new ASTPrinter(ast);
		ap.print();
		PrettyPrinter pp = new PrettyPrinter(ast);
		pp.print();
	}
}
