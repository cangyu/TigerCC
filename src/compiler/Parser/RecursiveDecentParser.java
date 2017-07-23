package compiler.Parser;

import java.io.*;
import compiler.Lexer.*;
import compiler.Symbols.*;
import compiler.AST.*;

public class RecursiveDecentParser
{
	private Lexer lex;
	private Token look;
	private Env top = null;
	private int used = 0;

	public RecursiveDecentParser(Lexer x)
	{
		lex = x;
	}

	private void panic(String msg) throws Exception
	{
		throw new Exception(String.format("(Line %d, Column %d): %s", look.line, look.column, msg));
	}

	private boolean match(Tag t) throws Exception
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
			look = lex.next_token();
			if (look.tag != Tag.LINECOMMENT && look.tag != Tag.BLKCOMMENT)
				break;
		}
	}

	public Program parse() throws Exception
	{
		move();
		return program();
	}

	private Program program() throws Exception
	{
		// program: (declaration | function_definition)+

		Program ret = new Program();

		for (;;)
		{
			if (look.tag == Tag.EOF)
				break;

			Declaration decl = declaration();
			if (decl != null)
				ret.add_elem(decl);
			else
			{
				FuncDef funcdef = function_definition();
				if (funcdef != null)
					ret.add_elem(funcdef);
				else
					panic("Unable to match a declaration nor a function definition.");
			}
		}

		return ret;
	}

	private Declaration declaration() throws Exception
	{
		Declaration ret = null;
		TypeSpecifier t = type_specifier();
		if (match(Tag.SEMI))
			ret = new Declaration(t, null);
		else
		{
			InitDeclaratorList ids = init_declarator_list();
			ret = new Declaration(t, ids);
		}

		return ret;
	}

	private FuncDef function_definition() throws Exception
	{
		return null;
	}

	private TypeSpecifier type_specifier() throws Exception
	{
		return null;
	}

	private InitDeclaratorList init_declarator_list() throws Exception
	{
		InitDeclaratorList ret = null;

		return ret;
	}
}
