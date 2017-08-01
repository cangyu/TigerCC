package compiler.Parser;

import java.io.*;
import java.util.*;
import compiler.Lexer.*;
import compiler.Symbols.*;
import compiler.AST.*;
import compiler.Types.*;

//Recursive Decent Parser
public class Parser
{
	private Lexer lex;
	private Token look;
	private LinkedList<Token> token_stream_buf, derivation_buf;

	public Parser(Lexer x)
	{
		lex = x;
		token_stream_buf = new LinkedList<Token>();
		derivation_buf = new LinkedList<Token>();
	}

	private Token next() throws IOException
	{
		if (!token_stream_buf.isEmpty())
			return token_stream_buf.pop();

		for (;;)
		{
			Token tmp = lex.next_token();
			if (tmp.tag != Tag.LINECOMMENT && tmp.tag != Tag.BLKCOMMENT)
				return tmp;
		}
	}

	private void advance() throws IOException
	{
		look = next();
		derivation_buf.add(look);
	}

	private void dump_cur_derivation()
	{
		while (!derivation_buf.isEmpty())
		{
			Token tmp = derivation_buf.removeLast();
			token_stream_buf.push(tmp);
		}
	}

	private void clear_cur_derivation()
	{
		derivation_buf.clear();
	}

	private boolean match(Tag t) throws IOException
	{
		if (look.tag == t)
		{
			advance();
			return true;
		}
		else
			return false;
	}

	private boolean peek(Tag t)
	{
		return look.tag == t;
	}

	public Program parse() throws Exception
	{
		advance();
		return program();
	}

	private Program program() throws Exception
	{
		Program ret = new Program(); // program: (declaration | function-definition)+

		for (;;)
		{
			if (peek(Tag.EOF))
				break;

			Declaration decl = declaration();
			if (decl != null)
			{
				ret.add_elem(decl);
				clear_cur_derivation();
			}
			else
			{
				dump_cur_derivation();
				FuncDef funcdef = function_definition();
				if (funcdef != null)
				{
					ret.add_elem(funcdef);
					clear_cur_derivation();
				}
				else
				{
					dump_cur_derivation();
					panic("Unable to match a declaration nor a function-definition.");
					return null;
				}
			}
		}

		return ret;
	}

	private Declaration declaration() throws Exception
	{
		TypeSpecifier t = type_specifier();
		if (t == null)
		{
			panic("Unable to match the type-specifier when parsing declaration.");
			return null;
		}

		if (match(Tag.SEMI))
			return new Declaration(t);

		int cnt = 1;
		InitDeclarator x = init_declarator();
		if (x == null)
		{
			panic("Unable to match the " + num2idx(cnt) + " init-declarator when parsing declaration.");
			return null;
		}

		Declaration ret = new Declaration(t);
		ret.add_elem(x);
		while (match(Tag.COMMA))
		{
			x = init_declarator();
			++cnt;
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " init-declarator when parsing declaration.");
				return null;
			}
			ret.add_elem(x);
		}

		return ret;
	}

	private FuncDef function_definition() throws Exception
	{
		TypeSpecifier ts = type_specifier();
		if (ts == null)
		{
			panic("Unable to match the typs-specifier when parsing function-definition.");
			return null;
		}

		PlainDeclarator pd = plain_declarator();
		if (pd == null)
		{
			panic("Unable to match the plain-declarator when parsing function-definition.");
			return null;
		}

		if (!match(Tag.LPAREN))
		{
			panic("Missing \'(\'.");
			return null;
		}

		FuncDef ret = new FuncDef(ts, pd);

		if (!peek(Tag.RPAREN))
		{
			int cnt = 1;
			PlainDeclaration x = plain_declaration();
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " parameter when parsing function-definition.");
				return null;
			}

			ret.add_param(x);
			while (match(Tag.COMMA))
			{
				x = plain_declaration();
				++cnt;
				if (x == null)
				{
					panic("Unable to match the " + num2idx(cnt++) + " parameter when parsing function-definition.");
					return null;
				}

				ret.add_param(x);
			}
		}

		if (!match(Tag.RPAREN))
		{
			panic("Missing \')\'.");
			return null;
		}

		CompoundStmt y = compound_stmt();
		if (y == null)
		{
			panic("Unable to match the compound-statement when parsing function-definition.");
			return null;
		}

		ret.add_body(y);
		return ret;
	}

	private InitDeclarator init_declarator() throws Exception
	{
		Declarator x = declarator();
		if (x == null)
		{
			panic("Unable to match the declarator when parsing init-declarator.");
			return null;
		}

		Initializer y = null;
		if (match(Tag.ASSIGN))
		{
			y = initializer();
			if (y == null)
			{
				panic("Unable to match the initializer when parsing init-declarator.");
				return null;
			}
		}

		return new InitDeclarator(x, y);
	}

	private Initializer initializer() throws Exception
	{
		if (match(Tag.LBRACE))
		{
			Initializer ret = new Initializer();

			int cnt = 1;
			Initializer x = initializer();
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " initilaizer when parsing \'listed\' initilalizer.");
				return null;
			}
			else
				ret.add_initializer(x);

			while (match(Tag.COMMA))
			{
				x = initializer();
				++cnt;
				if (x == null)
				{
					panic("Unable to match the " + num2idx(cnt) + " initilaizer when parsing \'listed\' initilalizer.");
					return null;
				}
				else
					ret.add_initializer(x);
			}

			if (!match(Tag.RBRACE))
			{
				panic("Missing \'}\'.");
				return null;
			}
			else
				return ret;
		}
		else
		{
			AssignmentExpr ae = assignment_expr();
			if (ae == null)
			{
				panic("Unable to match the assignment when parsing initializer.");
				return null;
			}
			else
				return new Initializer(ae);
		}
	}

	private TypeSpecifier type_specifier() throws Exception
	{
		TypeSpecifier ret = null;

		if (match(Tag.VOID))
			return TypeSpecifier.TS_VOID;
		else if (match(Tag.INT))
			return TypeSpecifier.TS_INT;
		else if (match(Tag.CHAR))
			return TypeSpecifier.TS_CHAR;
		else if (match(Tag.FLOAT))
			return TypeSpecifier.TS_FLOAT;
		else if (match(Tag.STRUCT))
		{
			if (peek(Tag.ID))
			{
				String name = ((Identifier) look).name;
				ret = new TypeSpecifier(TypeSpecifier.ts_struct, name); // type_specifeir: struct identifier
				advance();

				if (match(Tag.LBRACE)) // type_specifier: struct identifier { (type_specifier declarator+ ;)+ }
				{
					handle_record_entry(ret);
					if (!match(Tag.RBRACE))
					{
						panic("Missing \'}\'.");
						return null;
					}
				}
			}
			else if (match(Tag.LBRACE)) // type_specifier: struct { (type_specifier declarator+ ;)+ }
			{
				ret = new TypeSpecifier(TypeSpecifier.ts_struct);
				handle_record_entry(ret);
				if (!match(Tag.RBRACE))
				{
					panic("Missing \'}\'.");
					return null;
				}
			}
			else
			{
				panic("Invalid struct declaration.");
				return null;
			}
		}
		else if (match(Tag.UNION))
		{
			if (peek(Tag.ID))
			{
				String name = ((Identifier) look).name;
				ret = new TypeSpecifier(TypeSpecifier.ts_union, name); // type_specifeir: union identifier
				advance();

				if (match(Tag.LBRACE)) // type_specifier: union identifier { (type_specifier declarator+ ;)+ }
				{
					handle_record_entry(ret);
					if (!match(Tag.RBRACE))
					{
						panic("Missing \'}\'.");
						return null;
					}
				}
			}
			else if (match(Tag.LBRACE)) // type_specifier: union { (type_specifier declarator+ ;)+ }
			{
				ret = new TypeSpecifier(TypeSpecifier.ts_union);
				handle_record_entry(ret);
				if (!match(Tag.RBRACE))
				{
					panic("Missing \'}\'.");
					return null;
				}
			}
			else
			{
				panic("Invalid union declaration.");
				return null;
			}
		}
		else
		{
			panic("Unable to match a type.");
			return null;
		}

		return ret;
	}

	private void handle_record_entry(TypeSpecifier x) throws Exception
	{
		do
		{
			// type-specifier
			TypeSpecifier ct = type_specifier();
			if (ct == null)
			{
				panic("Failed to match a type specifier in record.");
				x = null;
				return;
			}
			RecordEntry re = new RecordEntry(ct);

			// declarators
			Declarator dlr = null;
			int cnt = 0;
			do
			{
				dlr = declarator();
				++cnt;
				if (dlr == null)
				{
					panic("Failed to match the " + num2idx(cnt) + " declarator in record.");
					x = null;
					return;
				}
				else
					re.add_elem(dlr);

			} while (match(Tag.COMMA));

			if (!match(Tag.SEMI))
			{
				panic("Missing \';\' at the end of record entry.");
				x = null;
				return;
			}
			else
				x.add_entry(re);

		} while (!peek(Tag.RBRACE));
	}

	private PlainDeclaration plain_declaration() throws Exception
	{
		TypeSpecifier x = type_specifier();
		if (x == null)
		{
			panic("Unable to match the type-specifier when parsing plain-declaration.");
			return null;
		}

		Declarator y = declarator();
		if (y == null)
		{
			panic("Unable to match the declarator when parsing plain-declaration.");
			return null;
		}

		return new PlainDeclaration(x, y);
	}

	private Declarator declarator() throws Exception
	{
		PlainDeclarator pdlr = plain_declarator();
		if (pdlr == null)
		{
			panic("Unable to match a plain declarator.");
			return null;
		}

		Declarator ret = new Declarator(pdlr);
		while (match(Tag.LMPAREN))
		{
			ConstantExpr e = const_expr();
			if (e == null)
			{
				panic("Unable to match a const expression.");
				return null;
			}
			else
				ret.add_expr(e);

			if (!match(Tag.RMPAREN))
			{
				panic("Missing \']\'.");
				return null;
			}
		}

		return ret;
	}

	private PlainDeclarator plain_declarator() throws Exception
	{
		int n = 0;
		while (match(Tag.TIMES))
			++n;

		if (!peek(Tag.ID))
		{
			panic("Unable to match the identifier when parsing plain-declarator.");
			return null;
		}
		else
		{
			String name = ((Identifier) look).name;
			advance();
			return new PlainDeclarator(n, name);
		}
	}

	private Stmt stmt() throws Exception
	{
		Stmt ret = null;
		if (peek(Tag.CONTINUE) || peek(Tag.BREAK) || peek(Tag.RETURN))
			ret = jump_stmt();
		else if (peek(Tag.WHILE) || peek(Tag.FOR))
			ret = iteration_stmt();
		else if (peek(Tag.IF))
			ret = selection_stmt();
		else if (peek(Tag.LBRACE))
			ret = compound_stmt();
		else
			ret = expression_stmt();

		if (ret == null)
			panic("Unable to match a statement.");

		return ret;
	}

	private ExpressionStmt expression_stmt() throws Exception
	{
		if (match(Tag.SEMI))
			return new ExpressionStmt(null);
		else
		{
			Expression x = expression();
			if (x == null)
			{
				panic("Unable to match the expression when parsing expression-statement.");
				return null;
			}

			if (!match(Tag.SEMI))
			{
				panic("Missing \';\'.");
				return null;
			}

			return new ExpressionStmt(x);
		}
	}

	private CompoundStmt compound_stmt() throws Exception
	{
		CompoundStmt ret = new CompoundStmt();
		if (!match(Tag.LBRACE))
		{
			panic("Missing \'{\'.");
			return null;
		}

		for (;;)
		{
			Declaration x = declaration();
			if (x == null)
			{
				dump_cur_derivation();
				break;
			}

			ret.add_decl(x);
		}

		for (;;)
		{
			Stmt x = stmt();
			if (x == null)
			{
				dump_cur_derivation();
				break;
			}

			ret.add_stmt(x);
		}

		if (!match(Tag.RBRACE))
		{
			panic("Missing \'}\'.");
			return null;
		}

		return ret;
	}

	private SelectionStmt selection_stmt() throws Exception
	{
		SelectionStmt ret = null;
		if (!match(Tag.IF))
		{
			panic("Unable to match \'if\' keyword.");
			return null;
		}

		if (!match(Tag.LPAREN))
		{
			panic("Missing \'(\'.");
			return null;
		}

		Expression cond = expression();
		if (cond == null)
		{
			panic("Unable to match the condition expr when parsing selection-statement.");
			return null;
		}

		if (!match(Tag.RPAREN))
		{
			panic("Missing \')\'.");
			return null;
		}

		Stmt if_clause = stmt();
		if (if_clause == null)
		{
			panic("Unable to match the 1st clause when parsing selection-statement.");
			return null;
		}

		if (!match(Tag.ELSE))
			ret = new SelectionStmt(cond, if_clause, null);
		else
		{
			Stmt else_clause = stmt();
			if (else_clause == null)
			{
				panic("Unable to match the 2nd clause when parsing selection-statement.");
				return null;
			}

			ret = new SelectionStmt(cond, if_clause, else_clause);
		}

		return ret;
	}

	private IterationStmt iteration_stmt() throws Exception
	{
		if (match(Tag.WHILE))
		{
			if (!match(Tag.LPAREN))
			{
				panic("Missing \'(\'.");
				return null;
			}

			Expression x = expression();
			if (x == null)
			{
				panic("Unable to match the expression when parsing \'while\' iteration-statement.");
				return null;
			}

			if (!match(Tag.RPAREN))
			{
				panic("Missing \')\'.");
				return null;
			}

			Stmt y = stmt();
			if (y == null)
			{
				panic("Unable to match a statement when parsing \'while\' iteration-statement.");
				return null;
			}

			return new IterationStmt(x, y);
		}
		else if (match(Tag.FOR))
		{
			if (!match(Tag.LPAREN))
			{
				panic("Missing \'(\'.");
				return null;
			}

			Expression init = null, judge = null, next = null;
			if (!peek(Tag.SEMI))
			{
				init = expression();
				if (init == null)
				{
					panic("Unable to match the 1st expression when parsing \'for\' iteration-statement.");
					return null;
				}
			}

			if (!match(Tag.SEMI))
			{
				panic("Missing \';\'.");
				return null;
			}

			if (!peek(Tag.SEMI))
			{
				judge = expression();
				if (judge == null)
				{
					panic("Unable to match the 2nd expression when parsing \'for\' iteration-statemen");
					return null;
				}
			}

			if (!match(Tag.SEMI))
			{
				panic("Missing \';\'.");
				return null;
			}

			if (!peek(Tag.RPAREN))
			{
				next = expression();
				if (next == null)
				{
					panic("Unable to match the 3rd expression when parsing \'for\' iteration-statemen");
					return null;
				}
			}
			if (!match(Tag.RPAREN))
			{
				panic("Missing \')\'.");
				return null;
			}

			Stmt y = stmt();
			if (y == null)
			{
				panic("Unable to match a statement when parsing \'for\' iteration-statement.");
				return null;
			}

			return new IterationStmt(init, judge, next, y);
		}
		else
		{
			panic("Unable to match a valid iteration keyword when parsing iteration-statement.");
			return null;
		}
	}

	private JumpStmt jump_stmt() throws Exception
	{
		JumpStmt ret = null;
		if (match(Tag.CONTINUE))
			ret = new JumpStmt(JumpStmt.CTNU, null);
		else if (match(Tag.BREAK))
			ret = new JumpStmt(JumpStmt.BRK, null);
		else if (match(Tag.RETURN))
		{
			if (peek(Tag.SEMI))
				ret = new JumpStmt(JumpStmt.RET, null);
			else
			{
				Expression x = expression();
				if (x == null)
				{
					panic("Unable to match the expression when parsing \'return\' jump-statement");
					return null;
				}
				ret = new JumpStmt(JumpStmt.RET, x);
			}
		}
		else
		{
			panic("Unable to match valid jump keywords when parsing jump-statement.");
			return null;
		}

		if (!match(Tag.SEMI))
		{
			panic("Missing \';\'.");
			return null;
		}

		return ret;
	}

	private Expression expression() throws Exception
	{
		Expression ret = new Expression();
		int cnt = 0;

		do
		{
			AssignmentExpr x = assignment_expr();
			++cnt;
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " assignment-expr when parsing expression.");
				return null;
			}
			else
				ret.add_expr(x);
		} while (match(Tag.COMMA));

		return ret;
	}

	private AssignmentExpr assignment_expr() throws Exception
	{
		AssignmentExpr ret = new AssignmentExpr();
		for (;;)
		{
			BinaryExpr loe = binary_expr();
			if (loe == null)
			{
				dump_cur_derivation();
				UnaryExpr ue = unary_expr();
				if (ue == null)
				{
					panic("Unable to match the unary-expr when parsing assignment-expr.");
					return null;
				}

				if (match(Tag.ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.ASSIGN);
				else if (match(Tag.MUL_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.MUL_ASSIGN);
				else if (match(Tag.DIV_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.DIV_ASSIGN);
				else if (match(Tag.ADD_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.ADD_ASSIGN);
				else if (match(Tag.SUB_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.SUB_ASSIGN);
				else if (match(Tag.MOD_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.MOD_ASSIGN);
				else if (match(Tag.SHL_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.SHL_ASSIGN);
				else if (match(Tag.SHR_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.SHR_ASSIGN);
				else if (match(Tag.AND_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.AND_ASSIGN);
				else if (match(Tag.XOR_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.XOR_ASSIGN);
				else if (match(Tag.OR_ASSIGN))
					ret.add_left_expr(ue, AssignmentExpr.OR_ASSIGN);
				else
				{
					panic("Unable to match a valid assignment-operator when parsing assignment-expr.");
					return null;
				}
			}
			else
			{
				ret.set_origin(loe);
				break;
			}
		}

		return ret;
	}

	private ConstantExpr const_expr() throws Exception
	{
		BinaryExpr be = binary_expr();
		if (be == null)
		{
			panic("Unable to match the logic-or-expr when parsing constant-expr.");
			return null;
		}
		else
			return new ConstantExpr(be);
	}

	private BinaryExpr binary_expr() throws Exception
	{
		BinaryExpr ret = null;

		return ret;
	}

	private CastExpr cast_expr() throws Exception
	{
		CastExpr ret = new CastExpr();
		for (;;)
		{
			UnaryExpr ue = unary_expr();
			if (ue == null)
			{
				dump_cur_derivation();
				if (match(Tag.LPAREN))
				{
					TypeName tn = type_name();
					if (tn == null)
					{
						panic("Unable to match type-name when parsing cast-expr.");
						return null;
					}
					else
						ret.add_type(tn);

					if (!match(Tag.RPAREN))
					{
						panic_missing(')');
						return null;
					}
				}
				else
				{
					panic_missing('(');
					return null;
				}
			}
			else
			{
				ret.set_origin(ue);
				clear_cur_derivation();
				break;
			}
		}

		return ret;
	}

	private TypeName type_name() throws Exception
	{
		TypeSpecifier x = type_specifier();
		if (x == null)
		{
			panic("Unable to match a type-specifier when parsing type-name");
			return null;
		}

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
			{
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.address, x);
		}
		else if (match(Tag.TIMES))
		{
			CastExpr x = cast_expr();
			if (x == null)
			{
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.dereference, x);
		}
		else if (match(Tag.PLUS))
		{
			CastExpr x = cast_expr();
			if (x == null)
			{
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.positive, x);
		}
		else if (match(Tag.MINUS))
		{
			CastExpr x = cast_expr();
			if (x == null)
			{
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.negative, x);
		}
		else if (match(Tag.BIT_NOT))
		{
			CastExpr x = cast_expr();
			if (x == null)
			{
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.bit_not, x);
		}
		else if (match(Tag.NOT))
		{
			CastExpr x = cast_expr();
			if (x == null)
			{
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.not, x);
		}
		else if (match(Tag.INC))
		{
			UnaryExpr x = unary_expr();
			if (x == null)
			{
				panic("Unable to match an unary expr when parsing unary-expr");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.inc, x);
		}
		else if (match(Tag.DEC))
		{
			UnaryExpr x = unary_expr();
			if (x == null)
			{
				panic("Unable to match an unary expr when parsing unary-expr");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.dec, x);
		}
		else if (match(Tag.SIZEOF)) // TODO: sizeof unary_expr
		{
			if (!match(Tag.LPAREN))
			{
				panic("Missing \'(\' when parsing unary-expr.");
				return null;
			}

			TypeName x = type_name();
			if (x == null)
			{
				panic("Unable to match a type-name when parsing unary-expr.");
				return null;
			}

			if (!match(Tag.RPAREN))
			{
				panic("Missing \')\' when parsing unary-expr.");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.sizeof, x);
		}
		else
		{
			PostfixExpr x = postfix_expr();
			if (x == null)
			{
				panic("Unable to match a postfix expr when parsing unary-expr.");
				return null;
			}

			ret = new UnaryExpr(UnaryExpr.postfix, x);
		}

		return ret;
	}

	private PostfixExpr postfix_expr() throws Exception
	{
		PrimaryExpr pe = primary_expr();
		if (pe == null)
		{
			panic("Unable to match a primary expr when parsing postfix expr.");
			return null;
		}

		PostfixExpr ret = new PostfixExpr(pe);
		for (;;)
		{
			if (match(Tag.LMPAREN))
			{
				Expression x = expression();
				if (x == null)
				{
					panic("Unable to match an expression when parsing postfix in postfix expr.");
					return null;
				}

				if (!match(Tag.RMPAREN))
				{
					panic("Missing \']\'.");
					return null;
				}

				ret.add_elem(PostfixExpr.mparen, x);
			}
			else if (match(Tag.LPAREN))
			{
				if (peek(Tag.RPAREN))
				{
					ret.add_elem(PostfixExpr.paren, null);
					advance();
				}
				else
				{
					LinkedList<AssignmentExpr> arg = new LinkedList<AssignmentExpr>();
					int cnt = 0;
					do
					{
						AssignmentExpr x = assignment_expr();
						++cnt;
						if (x == null)
						{
							panic("Unable to match the " + num2idx(cnt) + " asssignment-expr when parsing arguments.");
							return null;
						}
						else
							arg.add(x);
					} while (match(Tag.COMMA));

					if (!match(Tag.RPAREN))
					{
						panic("Missing \')\'.");
						return null;
					}

					ret.add_elem(PostfixExpr.paren, arg);
				}
			}
			else if (match(Tag.DOT))
			{
				if (!peek(Tag.ID))
				{
					panic("Unable to match the identifier when parsing postfix.");
					return null;
				}

				String name = ((Identifier) look).name;
				ret.add_elem(PostfixExpr.dot, name);
				advance();
			}
			else if (match(Tag.PTR))
			{
				if (!peek(Tag.ID))
				{
					panic("Unable to match a identifier when parsing postfix.");
					return null;
				}

				String name = ((Identifier) look).name;
				ret.add_elem(PostfixExpr.ptr, name);
				advance();
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

	private PrimaryExpr primary_expr() throws Exception
	{
		if (peek(Tag.ID) || peek(Tag.CH) || peek(Tag.NUM) || peek(Tag.REAL) || peek(Tag.STR))
		{
			PrimaryExpr ret = new PrimaryExpr(look);
			advance();
			return ret;
		}
		else
		{
			if (!match(Tag.LPAREN))
			{
				panic("Missing \'(\'.");
				return null;
			}

			Expression x = expression();
			if (x == null)
			{
				panic("Failed to match an expr when parsing primary-expr.");
				return null;
			}

			if (!match(Tag.RPAREN))
			{
				panic("Missing \')\'.");
				return null;
			}

			return new PrimaryExpr(x);
		}
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

	private void panic(String msg)
	{
		String info = String.format("(Line %d, Column %d): %s", look.line, look.column, msg);
		System.out.println(info);
	}

	private void panic_missing(Character ch)
	{
		panic("Missing \'" + ch + "\'.");
	}
}
