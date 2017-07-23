package compiler.Parser;

import java.io.*;
import compiler.Lexer.*;
import compiler.Symbols.*;
import compiler.AST.*;
import compiler.Types.*;

//Recursive Decent Parser
public class Parser
{
	private Lexer lex;
	private Token look;

	public Parser(Lexer x)
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
		Program ret = new Program(); // program: (declaration | function_definition)+

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
		TypeSpecifier ret = null;
		if (match(Tag.VOID))
			ret = TypeSpecifier.TS_VOID; // type_specifier: 'void'
		else if (match(Tag.INT))
			ret = TypeSpecifier.TS_INT; // type_specifier: 'int'
		else if (match(Tag.CHAR))
			ret = TypeSpecifier.TS_CHAR; // type_specifier: 'char'
		else if (match(Tag.FLOAT))
			ret = TypeSpecifier.TS_FLOAT; // type_specifier: 'float'
		else if (match(Tag.STRUCT))
		{
			if (look.tag == Tag.ID)
			{
				String name = ((Identifier) look).name;
				ret = new TypeSpecifier(TypeSpecifier.ts_struct, name); // type_specifeir: struct identifier
				move();

				if (match(Tag.LBRACE)) // type_specifier: struct identifier { (type_specifier declarator+ ;)+ }
				{
					handle_record_entry(ret);
					match(Tag.RBRACE);
				}
			}
			else if (match(Tag.LBRACE)) // type_specifier: struct { (type_specifier declarator+ ;)+ }
			{
				ret = new TypeSpecifier(TypeSpecifier.ts_struct);
				handle_record_entry(ret);
				match(Tag.RBRACE);
			}
			else
				panic("Invalid struct declaration.");
		}
		else if (match(Tag.UNION))
		{
			if (look.tag == Tag.ID)
			{
				String name = ((Identifier) look).name;
				ret = new TypeSpecifier(TypeSpecifier.ts_union, name); // type_specifeir: union identifier
				move();

				if (match(Tag.LBRACE)) // type_specifier: union identifier { (type_specifier declarator+ ;)+ }
				{
					handle_record_entry(ret);
					match(Tag.RBRACE);
				}
			}
			else if (match(Tag.LBRACE)) // type_specifier: union { (type_specifier declarator+ ;)+ }
			{
				ret = new TypeSpecifier(TypeSpecifier.ts_union);
				handle_record_entry(ret);
				match(Tag.RBRACE);
			}
			else
				panic("Invalid union declaration.");
		}
		else
			panic("Unable to match a type.");

		return ret;
	}

	private void handle_record_entry(TypeSpecifier x) throws Exception
	{
		if (x.ts_type != TypeSpecifier.ts_struct || x.ts_type != TypeSpecifier.ts_union)
			panic("Internal error.");

		do
		{
			TypeSpecifier ct = type_specifier();
			if (ct == null)
				panic("Failed to match a type specifier in record.");

			RecordEntry re = new RecordEntry(ct);

			for (;;)
			{
				Declarator dlr = declarator();
				if (dlr == null)
					panic("Failed to match a declarator in record.");
				else
					re.add_elem(dlr);

				if (match(Tag.COMMA))
					break;
			}
			x.add_entry(re);

			if (!match(Tag.SEMI))
				panic("Missing \';\' at the end of record entry declaration.");

		} while (look.tag != Tag.RBRACE);
	}

	private InitDeclaratorList init_declarator_list() throws Exception
	{
		InitDeclaratorList ret = null;

		return ret;
	}

	private Declarator declarator() throws Exception
	{
		Declarator ret = null;

		return ret;
	}
}
