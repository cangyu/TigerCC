package compiler.Parser;

import java.io.*;
import compiler.Lexer.*;
import compiler.Lexer.Char;
import compiler.Lexer.Int;
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

	private boolean cur_is(Tag t)
	{
		return look.tag == t;
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
			InitDeclarators ids = init_declarators();
			ret = new Declaration(t, ids);
		}

		return ret;
	}

	private FuncDef function_definition() throws Exception
	{
		return null;
	}

	private InitDeclarators init_declarators() throws Exception
	{
		InitDeclarators ret = null;

		return ret;
	}

	private InitDeclarator init_declarator() throws Exception
	{
		InitDeclarator ret = null;

		return ret;
	}

	private Initializer initializer() throws Exception
	{
		Initializer ret = null;

		return ret;
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

	private PlainDeclaration plain_declaration() throws Exception
	{
		PlainDeclaration ret = null;

		return ret;
	}

	private Declarator declarator() throws Exception
	{
		Declarator ret = null;
		PlainDeclarator pdlr = plain_declarator();
		if (pdlr == null)
			panic("Unable to match a plain declarator.");

		ret = new Declarator(pdlr);
		while (match(Tag.LMPAREN))
		{
			ConstantExpr e = const_expr();
			if (e == null)
				panic("Unable to match a const expression.");
			ret.add_expr(e);

			if (!match(Tag.RMPAREN))
				panic("Missing \']\'");
		}

		return ret;
	}

	private PlainDeclarator plain_declarator() throws Exception
	{
		PlainDeclarator ret = null;

		return ret;
	}

	private Stmt stmt() throws Exception
	{
		Stmt ret = null;

		return ret;
	}

	private ExpressionStmt expression_stmt() throws Exception
	{
		ExpressionStmt ret = null;

		return ret;
	}

	private CompoundStmt compound_stmt() throws Exception
	{
		CompoundStmt ret = null;

		return ret;
	}

	private SelectionStmt selection_stmt() throws Exception
	{
		SelectionStmt ret = null;

		return ret;
	}

	private IterationStmt iteration_stmt() throws Exception
	{
		IterationStmt ret = null;

		return ret;
	}

	private JumpStmt jump_stmt() throws Exception
	{
		JumpStmt ret = null;

		return ret;
	}

	private Expr expr() throws Exception
	{
		Expr ret = null;

		return ret;
	}

	private AssignmentExpr assignment_expr() throws Exception
	{
		AssignmentExpr ret = null;

		return ret;
	}

	private ConstantExpr const_expr() throws Exception
	{
		ConstantExpr ret = null;

		return ret;
	}

	private BinaryExpr binary_expr() throws Exception
	{
		BinaryExpr ret = null;

		return ret;
	}

	private CastExpr cast_expr() throws Exception
	{
		CastExpr ret = null;

		return ret;
	}

	private TypeName type_name() throws Exception
	{
		TypeSpecifier x = type_specifier();
		if (x == null)
			panic("Unable to match a type-specifier when parsing type-name");

		int cnt = 0;
		while (match(Tag.TIMES))
			++cnt;

		return new TypeName(x, cnt);
	}

	private UnaryExpr unary_expr() throws Exception
	{
		UnaryExpr ret = null;
		if (match(Tag.BIT_AND))
		{
			CastExpr x = cast_expr();
			if (x == null)
				panic("Unable to match a cast expr when parsing unary expr.");

			ret = new UnaryExpr(UnaryExpr.address, x);
		}
		else if (match(Tag.TIMES))
		{
			CastExpr x = cast_expr();
			if (x == null)
				panic("Unable to match a cast expr when parsing unary expr.");

			ret = new UnaryExpr(UnaryExpr.dereference, x);
		}
		else if (match(Tag.PLUS))
		{
			CastExpr x = cast_expr();
			if (x == null)
				panic("Unable to match a cast expr when parsing unary expr.");

			ret = new UnaryExpr(UnaryExpr.positive, x);
		}
		else if (match(Tag.MINUS))
		{
			CastExpr x = cast_expr();
			if (x == null)
				panic("Unable to match a cast expr when parsing unary expr.");

			ret = new UnaryExpr(UnaryExpr.negative, x);
		}
		else if (match(Tag.BIT_NOT))
		{
			CastExpr x = cast_expr();
			if (x == null)
				panic("Unable to match a cast expr when parsing unary expr.");

			ret = new UnaryExpr(UnaryExpr.bit_not, x);
		}
		else if (match(Tag.NOT))
		{
			CastExpr x = cast_expr();
			if (x == null)
				panic("Unable to match a cast expr when parsing unary expr.");

			ret = new UnaryExpr(UnaryExpr.not, x);
		}
		else if (match(Tag.INC))
		{
			UnaryExpr x = unary_expr();
			if (x == null)
				panic("Unable to match an unary expr when parsing unary expr");

			ret = new UnaryExpr(UnaryExpr.inc, x);
		}
		else if (match(Tag.DEC))
		{
			UnaryExpr x = unary_expr();
			if (x == null)
				panic("Unable to match an unary expr when parsing unary expr");

			ret = new UnaryExpr(UnaryExpr.dec, x);
		}
		else if (match(Tag.SIZEOF)) // TODO: sizeof unary_expr
		{
			if (!match(Tag.LPAREN))
				panic("Missing \'(\' when parsing unary expr.");

			TypeName x = type_name();
			if (x == null)
				panic("Unable to match a type-name when parsing unary expr.");

			if (!match(Tag.RPAREN))
				panic("Missing \')\' when parsing unary expr.");

			ret = new UnaryExpr(UnaryExpr.sizeof, x);
		}
		else
		{
			PostfixExpr x = postfix_expr();
			if (x == null)
				panic("Unable to match a postfix expr when parsing unary expr.");

			ret = new UnaryExpr(UnaryExpr.postfix, x);
		}

		return ret;
	}

	private PostfixExpr postfix_expr() throws Exception
	{
		PostfixExpr ret = null;

		PrimaryExpr pe = primary_expr();
		if (pe == null)
			panic("Unable to match a primary expr when parsing postfix expr.");

		ret = new PostfixExpr(pe);
		for (;;)
		{
			if (match(Tag.LMPAREN))
			{
				Expr e = expr();
				if (e == null)
					panic("Unable to match an expression when parsing postfix in postfix expr.");

				if (!match(Tag.RMPAREN))
					panic("Missing \']\'.");

				ret.add_elem(PostfixExpr.mparen, e);
			}
			else if (match(Tag.LPAREN))
			{
				if (cur_is(Tag.RPAREN))
				{
					ret.add_elem(PostfixExpr.paren, null);
					move();
				}
				else
				{
					Arguments arg = arguments();
					if (!match(Tag.RPAREN))
						panic("Missing \')\'.");

					ret.add_elem(PostfixExpr.paren, arg);
				}
			}
			else if (match(Tag.DOT))
			{
				if (!cur_is(Tag.ID))
					panic("Unable to match a identifier when parsing postfix.");

				String name = ((Identifier) look).name;
				ret.add_elem(PostfixExpr.dot, name);
				move();
			}
			else if (match(Tag.PTR))
			{
				if (!cur_is(Tag.ID))
					panic("Unable to match a identifier when parsing postfix.");

				String name = ((Identifier) look).name;
				ret.add_elem(PostfixExpr.ptr, name);
				move();
			}
			else if (match(Tag.INC))
				ret.add_elem(PostfixExpr.inc, null);
			else if (match(Tag.DEC))
				ret.add_elem(PostfixExpr.dec, null);
			else
				break;
		}

		return ret;
	}

	private Arguments arguments() throws Exception
	{
		Arguments ret = null;

		AssignmentExpr x = assignment_expr();
		if (x == null)
			panic("Unable to match the first asssignment expr when parsing arguments.");

		ret = new Arguments();
		ret.add_elem(x);

		int cnt = 1;
		while (match(Tag.COMMA))
		{
			x = assignment_expr();
			if (x == null)
				panic("Unable to match the " + num2idx(cnt++) + " asssignment expr when parsing arguments.");

			ret.add_elem(x);
		}

		return ret;
	}

	private PrimaryExpr primary_expr() throws Exception
	{
		PrimaryExpr ret = null;
		if (cur_is(Tag.ID) || cur_is(Tag.CH) || cur_is(Tag.NUM) || cur_is(Tag.REAL) || cur_is(Tag.STR))
		{
			ret = new PrimaryExpr(look);
			move();
		}
		else
		{
			if (!match(Tag.LPAREN))
				panic("Missing \'(\' when parsing primary expr.");

			Expr e = expr();
			if (e == null)
				panic("Failed to match an expr when parsing primary expr.");

			ret = new PrimaryExpr(e);
			if (!match(Tag.RPAREN))
				panic("Missing \')\' when parsing primary expr.");
		}

		return ret;
	}

	private String num2idx(int n)
	{
		if (n == 1)
			return "1st".intern();
		else if (n == 2)
			return "2nd".intern();
		else if (n == 3)
			return "3rd".intern();
		else
			return n + "th".intern();
	}
}
