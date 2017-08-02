package compiler.Parser;

import java.io.*;
import java.util.*;
import compiler.Lexer.*;

//Recursive Decent Parser
public class Parser
{
	private int look;
	private ArrayList<Token> token_buf;
	private Stack<Integer> start_pos;

	public Parser(Lexer lex) throws IOException
	{
		look = -1;
		token_buf = new ArrayList<Token>();
		start_pos = new Stack<Integer>();

		for (;;)
		{
			Token tmp = lex.next_token();
			if (tmp.tag != Tag.LINECOMMENT && tmp.tag != Tag.BLKCOMMENT)
				token_buf.add(tmp);

			if (tmp.tag == Tag.EOF)
				break;
		}
	}

	private void advance()
	{
		++look;
	}

	private boolean match(Tag t)
	{
		return token_buf.get(look).tag == t;
	}

	public Program parse()
	{
		advance();
		Program ret = program();
		if (ret != null)
		{
			start_pos.pop();
			return ret;
		}
		else
		{
			look = start_pos.pop();
			panic("Unable to parse the program.");
			return null;
		}
	}

	private Program program()
	{
		start_pos.push(look);
		Program ret = new Program(); // program: (declaration | function-definition)+

		for (;;)
		{
			if (match(Tag.EOF))
				break;

			Declaration decl = declaration();
			if (decl != null)
			{
				ret.add_elem(decl);
				start_pos.pop();
			}
			else
			{
				look = start_pos.pop();
				FuncDef funcdef = function_definition();
				if (funcdef != null)
				{
					ret.add_elem(funcdef);
					start_pos.pop();
				}
				else
				{
					look = start_pos.pop();
					panic("Unable to match a declaration nor a function-definition.");
					return null;
				}
			}
		}

		return ret;
	}

	private Declaration declaration()
	{
		start_pos.push(look);
		TypeSpecifier t = type_specifier();
		if (t == null)
		{
			look = start_pos.pop();
			panic("Unable to match the type-specifier when parsing declaration.");
			return null;
		}
		else
			start_pos.pop();

		if (match(Tag.SEMI))
		{
			advance();
			return new Declaration(t);// declaration ::= type-specifier;
		}

		Declaration ret = new Declaration(t);
		int cnt = 0;
		for (;;)
		{
			InitDeclarator x = init_declarator();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " init-declarator when parsing declaration.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret.add_elem(x);
			}

			if (match(Tag.COMMA))
				advance();
			else
				break;
		}

		if (match(Tag.SEMI))
		{
			advance();
			return ret;
		}
		else
		{
			panic_missing(';');
			return null;
		}
	}

	private FuncDef function_definition()
	{
		start_pos.push(look);
		TypeSpecifier ts = type_specifier();
		if (ts == null)
		{
			look = start_pos.pop();
			panic("Unable to match the typs-specifier when parsing function-definition.");
			return null;
		}
		else
			start_pos.pop();

		PlainDeclarator pd = plain_declarator();
		if (pd == null)
		{
			look = start_pos.pop();
			panic("Unable to match the plain-declarator when parsing function-definition.");
			return null;
		}
		else
			start_pos.pop();

		if (match(Tag.LPAREN))
			advance();
		else
		{
			panic_missing('(');
			return null;
		}

		FuncDef ret = new FuncDef(ts, pd);

		if (!match(Tag.RPAREN))
		{
			int cnt = 0;
			for (;;)
			{
				PlainDeclaration x = plain_declaration();
				++cnt;
				if (x == null)
				{
					look = start_pos.pop();
					panic("Unable to match the " + num2idx(cnt) + " parameter when parsing function-definition.");
					return null;
				}
				else
				{
					start_pos.pop();
					ret.add_param(x);
				}

				if (match(Tag.COMMA))
					advance();
				else
					break;
			}
		}

		if (match(Tag.RPAREN))
			advance();
		else
		{
			panic_missing(')');
			return null;
		}

		CompoundStmt y = compound_stmt();
		if (y == null)
		{
			look = start_pos.pop();
			panic("Unable to match the compound-statement when parsing function-definition.");
			return null;
		}
		else
		{
			start_pos.pop();
			ret.add_body(y);
			return ret;
		}
	}

	private InitDeclarator init_declarator()
	{
		start_pos.push(look);
		Declarator x = declarator();
		if (x == null)
		{
			look = start_pos.pop();
			panic("Unable to match the declarator when parsing init-declarator.");
			return null;
		}
		else
			start_pos.pop();

		Initializer y = null;
		if (match(Tag.ASSIGN))
		{
			advance();
			y = initializer();
			if (y == null)
			{
				look = start_pos.pop();
				panic("Unable to match the initializer when parsing init-declarator.");
				return null;
			}
			else
				start_pos.pop();
		}

		return new InitDeclarator(x, y);
	}

	private Initializer initializer()
	{
		start_pos.push(look);
		if (match(Tag.LBRACE))
		{
			advance();
			Initializer ret = new Initializer();
			int cnt = 0;

			for (;;)
			{
				Initializer x = initializer();
				++cnt;
				if (x == null)
				{
					look = start_pos.pop();
					panic("Unable to match the " + num2idx(cnt) + " initilaizer when parsing \'listed\' initilalizer.");
					return null;
				}
				else
				{
					start_pos.pop();
					ret.add_initializer(x);
				}

				if (match(Tag.COMMA))
					advance();
				else
					break;
			}

			if (match(Tag.RBRACE))
			{
				advance();
				return ret;
			}
			else
			{
				panic_missing('}');
				return null;
			}
		}
		else
		{
			AssignmentExpr ae = assignment_expr();
			if (ae == null)
			{
				look = start_pos.pop();
				panic("Unable to match the assignment-expr when parsing initializer.");
				return null;
			}
			else
			{
				start_pos.pop();
				return new Initializer(ae);
			}
		}
	}

	private TypeSpecifier type_specifier()
	{
		start_pos.push(look);
		if (match(Tag.VOID))
		{
			advance();
			return TypeSpecifier.TS_VOID;
		}
		else if (match(Tag.INT))
		{
			advance();
			return TypeSpecifier.TS_INT;
		}
		else if (match(Tag.CHAR))
		{
			advance();
			return TypeSpecifier.TS_CHAR;
		}
		else if (match(Tag.FLOAT))
		{
			advance();
			return TypeSpecifier.TS_FLOAT;
		}
		else if (match(Tag.DOUBLE))
		{
			advance();
			return TypeSpecifier.TS_DOUBLE;
		}
		else if (match(Tag.STRUCT))
		{
			advance();
			if (match(Tag.ID))
			{
				String name = ((Identifier) token_buf.get(look)).name;
				advance();

				TypeSpecifier ret = new TypeSpecifier(TypeSpecifier.ts_struct, name);

				if (match(Tag.LBRACE))
				{
					advance();
					if (handle_record_entry(ret) == null)
					{
						panic("Unable to match entries.");
						return null;
					}

					if (match(Tag.RBRACE))
					{
						advance();
						return ret;
					}
					else
					{
						panic_missing('}');
						return null;
					}
				}
				else
					return ret;
			}
			else if (match(Tag.LBRACE)) // type_specifier: struct { (type_specifier declarator+ ;)+ }
			{
				advance();
				TypeSpecifier ret = new TypeSpecifier(TypeSpecifier.ts_struct);
				if (handle_record_entry(ret) == null)
				{
					panic("Unable to match entries.");
					return null;
				}

				if (match(Tag.RBRACE))
				{
					advance();
					return ret;
				}
				else
				{
					panic_missing('}');
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
			advance();
			if (match(Tag.ID))
			{
				String name = ((Identifier) token_buf.get(look)).name;
				advance();

				TypeSpecifier ret = new TypeSpecifier(TypeSpecifier.ts_union, name); // type_specifeir: union identifier

				if (match(Tag.LBRACE)) // type_specifier: union identifier { (type_specifier declarator+ ;)+ }
				{
					advance();
					if (handle_record_entry(ret) == null)
					{
						panic("Unable to match entries.");
						return null;
					}

					if (match(Tag.RBRACE))
					{
						advance();
						return ret;
					}
					else
					{
						panic_missing('}');
						return null;
					}
				}
				else
					return ret;
			}
			else if (match(Tag.LBRACE)) // type_specifier: union { (type_specifier declarator+ ;)+ }
			{
				advance();
				TypeSpecifier ret = new TypeSpecifier(TypeSpecifier.ts_union);
				if (handle_record_entry(ret) == null)
				{
					panic("Unable to match entries.");
					return null;
				}

				if (match(Tag.RBRACE))
				{
					advance();
					return ret;
				}
				else
				{
					panic_missing('}');
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
	}

	private TypeSpecifier handle_record_entry(TypeSpecifier x)
	{
		for (;;)
		{
			RecordEntry re = null;

			// type-specifier
			TypeSpecifier ct = type_specifier();
			if (ct == null)
			{
				look = start_pos.pop();
				panic("Failed to match a type specifier in record.");
				return null;
			}
			else
			{
				start_pos.pop();
				re = new RecordEntry(ct);
			}

			// declarators
			int cnt = 0;
			for (;;)
			{
				Declarator dlr = declarator();
				++cnt;
				if (dlr == null)
				{
					look = start_pos.pop();
					panic("Failed to match the " + num2idx(cnt) + " declarator in record.");
					return null;
				}
				else
				{
					start_pos.pop();
					re.add_elem(dlr);
				}

				if (match(Tag.COMMA))
					advance();
				else
					break;
			}

			// entry ending
			if (match(Tag.SEMI))
			{
				advance();
				x.add_entry(re);
			}
			else
			{
				panic_missing(';');
				return null;
			}

			// block ending
			if (match(Tag.RBRACE))
			{
				advance();
				return x;
			}
		}
	}

	private PlainDeclaration plain_declaration()
	{
		start_pos.push(look);
		TypeSpecifier x = type_specifier();
		if (x == null)
		{
			look = start_pos.pop();
			panic("Unable to match the type-specifier when parsing plain-declaration.");
			return null;
		}
		else
			start_pos.pop();

		Declarator y = declarator();
		if (y == null)
		{
			look = start_pos.pop();
			panic("Unable to match the declarator when parsing plain-declaration.");
			return null;
		}
		else
			start_pos.pop();

		return new PlainDeclaration(x, y);
	}

	private Declarator declarator()
	{
		start_pos.push(look);
		PlainDeclarator pdlr = plain_declarator();
		if (pdlr == null)
		{
			look = start_pos.pop();
			panic("Unable to match a plain declarator.");
			return null;
		}
		else
			start_pos.pop();

		Declarator ret = new Declarator(pdlr);
		while (match(Tag.LMPAREN))
		{
			advance();
			ConstantExpr e = const_expr();
			if (e == null)
			{
				look = start_pos.pop();
				panic("Unable to match a const expression.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret.add_expr(e);
			}

			if (match(Tag.RMPAREN))
				advance();
			else
			{
				panic_missing(']');
				return null;
			}
		}

		return ret;
	}

	private PlainDeclarator plain_declarator()
	{
		start_pos.push(look);

		int n = 0;
		while (match(Tag.TIMES))
		{
			++n;
			advance();
		}

		if (match(Tag.ID))
		{
			String name = ((Identifier) token_buf.get(look)).name;
			advance();
			return new PlainDeclarator(n, name);
		}
		else
		{
			panic("Unable to match the identifier when parsing plain-declarator.");
			return null;
		}
	}

	private Stmt stmt()
	{
		start_pos.push(look);
		Stmt ret = null;
		if (match(Tag.CONTINUE) || match(Tag.BREAK) || match(Tag.RETURN))
		{
			ret = jump_stmt();
			if (ret == null)
			{
				look = start_pos.pop();
				panic("Unable to match a jump-stmt.");
				return null;
			}
			else
			{
				start_pos.pop();
				return ret;
			}
		}
		else if (match(Tag.WHILE) || match(Tag.FOR))
		{
			ret = iteration_stmt();
			if (ret == null)
			{
				look = start_pos.pop();
				panic("Unable to match a iteration-stmt.");
				return null;
			}
			else
			{
				start_pos.pop();
				return ret;
			}
		}
		else if (match(Tag.IF))
		{
			ret = selection_stmt();
			if (ret == null)
			{
				look = start_pos.pop();
				panic("Unable to match a selection-stmt.");
				return null;
			}
			else
			{
				start_pos.pop();
				return ret;
			}
		}
		else if (match(Tag.LBRACE))
		{
			ret = compound_stmt();
			if (ret == null)
			{
				look = start_pos.pop();
				panic("Unable to match a compound-stmt.");
				return null;
			}
			else
			{
				start_pos.pop();
				return ret;
			}
		}
		else
		{
			ret = expression_stmt();
			if (ret == null)
			{
				look = start_pos.pop();
				panic("Unable to match a statement.");
				return null;
			}
			else
			{
				start_pos.pop();
				return ret;
			}
		}
	}

	private ExpressionStmt expression_stmt()
	{
		start_pos.push(look);
		if (match(Tag.SEMI))
		{
			advance();
			return new ExpressionStmt(null);
		}
		else
		{
			Expression x = expression();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the expression when parsing expression-statement.");
				return null;
			}
			else
				start_pos.pop();

			if (match(Tag.SEMI))
			{
				advance();
				return new ExpressionStmt(x);
			}
			else
			{
				panic_missing(';');
				return null;
			}
		}
	}

	private CompoundStmt compound_stmt()
	{
		start_pos.push(look);
		CompoundStmt ret = new CompoundStmt();

		if (match(Tag.LBRACE))
			advance();
		else
		{
			panic_missing('{');
			return null;
		}

		for (;;)
		{
			Declaration x = declaration();
			if (x == null)
			{
				look = start_pos.pop();
				break;
			}
			else
			{
				start_pos.pop();
				ret.add_decl(x);
			}
		}

		for (;;)
		{
			Stmt x = stmt();
			if (x == null)
			{
				look = start_pos.pop();
				break;
			}
			else
			{
				start_pos.pop();
				ret.add_stmt(x);
			}
		}

		if (match(Tag.RBRACE))
		{
			advance();
			return ret;
		}
		else
		{
			panic_missing('}');
			return null;
		}
	}

	private SelectionStmt selection_stmt()
	{
		start_pos.push(look);
		SelectionStmt ret = null;

		if (match(Tag.IF))
			advance();
		else
		{
			panic("Unable to match \'if\' keyword.");
			return null;
		}

		if (match(Tag.LPAREN))
			advance();
		else
		{
			panic_missing('(');
			return null;
		}

		Expression cond = expression();
		if (cond == null)
		{
			look = start_pos.pop();
			panic("Unable to match the condition expr when parsing selection-statement.");
			return null;
		}
		else
			start_pos.pop();

		if (match(Tag.RPAREN))
			advance();
		else
		{
			panic_missing(')');
			return null;
		}

		Stmt if_clause = stmt();
		if (if_clause == null)
		{
			look = start_pos.pop();
			panic("Unable to match the 1st clause when parsing selection-statement.");
			return null;
		}
		else
			start_pos.pop();

		if (match(Tag.ELSE))
		{
			advance();
			Stmt else_clause = stmt();
			if (else_clause == null)
			{
				look = start_pos.pop();
				panic("Unable to match the 2nd clause when parsing selection-statement.");
				return null;
			}
			else
				start_pos.pop();

			ret = new SelectionStmt(cond, if_clause, else_clause);
		}
		else
			ret = new SelectionStmt(cond, if_clause, null);

		return ret;
	}

	private IterationStmt iteration_stmt()
	{
		start_pos.push(look);
		if (match(Tag.WHILE))
		{
			advance();
			if (match(Tag.LPAREN))
				advance();
			else
			{
				panic_missing('(');
				return null;
			}

			Expression x = expression();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the expression when parsing \'while\' iteration-statement.");
				return null;
			}
			else
				start_pos.pop();

			if (match(Tag.RPAREN))
				advance();
			else
			{
				panic_missing(')');
				return null;
			}

			Stmt y = stmt();
			if (y == null)
			{
				look = start_pos.pop();
				panic("Unable to match a statement when parsing \'while\' iteration-statement.");
				return null;
			}
			else
				start_pos.pop();

			return new IterationStmt(x, y);
		}
		else if (match(Tag.FOR))
		{
			advance();
			if (match(Tag.LPAREN))
				advance();
			else
			{
				panic_missing('(');
				return null;
			}

			Expression init = null, judge = null, next = null;
			if (!match(Tag.SEMI)) // init-expr
			{
				init = expression();
				if (init == null)
				{
					look = start_pos.pop();
					panic("Unable to match the 1st expression when parsing \'for\' iteration-statement.");
					return null;
				}
				else
					start_pos.pop();
			}

			if (match(Tag.SEMI)) // first SEMI
				advance();
			else
			{
				panic_missing(';');
				return null;
			}

			if (!match(Tag.SEMI)) // judge-expr
			{
				judge = expression();
				if (judge == null)
				{
					look = start_pos.pop();
					panic("Unable to match the 2nd expression when parsing \'for\' iteration-statemen");
					return null;
				}
				else
					start_pos.pop();
			}

			if (match(Tag.SEMI)) // second SEMI
				advance();
			else
			{
				panic_missing(';');
				return null;
			}

			if (!match(Tag.RPAREN)) // next-expr
			{
				next = expression();
				if (next == null)
				{
					look = start_pos.pop();
					panic("Unable to match the 3rd expression when parsing \'for\' iteration-statemen");
					return null;
				}
				else
					start_pos.pop();
			}

			if (match(Tag.RPAREN))
				advance();
			else
			{
				panic_missing(')');
				return null;
			}

			Stmt y = stmt();
			if (y == null)
			{
				look = start_pos.pop();
				panic("Unable to match a statement when parsing \'for\' iteration-statement.");
				return null;
			}
			else
				start_pos.pop();

			return new IterationStmt(init, judge, next, y);
		}
		else
		{
			panic("Unable to match a valid iteration keyword when parsing iteration-statement.");
			return null;
		}
	}

	private JumpStmt jump_stmt()
	{
		start_pos.push(look);
		JumpStmt ret = null;
		if (match(Tag.CONTINUE))
		{
			advance();
			ret = new JumpStmt(JumpStmt.CTNU, null);
		}
		else if (match(Tag.BREAK))
		{
			advance();
			ret = new JumpStmt(JumpStmt.BRK, null);
		}
		else if (match(Tag.RETURN))
		{
			advance();
			if (match(Tag.SEMI))
				ret = new JumpStmt(JumpStmt.RET, null); // check SEMI later
			else
			{
				Expression x = expression();
				if (x == null)
				{
					look = start_pos.pop();
					panic("Unable to match the expression when parsing \'return\' jump-statement");
					return null;
				}
				else
				{
					start_pos.pop();
					ret = new JumpStmt(JumpStmt.RET, x);
				}
			}
		}
		else
		{
			panic("Unable to match valid jump keywords when parsing jump-statement.");
			return null;
		}

		if (match(Tag.SEMI))
		{
			advance();
			return ret;
		}
		else
		{
			panic_missing(';');
			return null;
		}
	}

	private Expression expression()
	{
		start_pos.push(look);
		Expression ret = new Expression();
		int cnt = 0;

		for (;;)
		{
			AssignmentExpr x = assignment_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " assignment-expr when parsing expression.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret.add_expr(x);
			}

			if (match(Tag.COMMA))
				advance();
			else
				break;
		}

		return ret;
	}

	private AssignmentExpr assignment_expr()
	{
		start_pos.push(look);
		AssignmentExpr ret = new AssignmentExpr();

		for (;;)
		{
			int loe_end = -1;
			LogicalOrExpr loe = logical_or_expr();
			if (loe == null)
			{
				look = start_pos.pop();
				panic("Unable to match the 1st expr when parsing assignment-expr.");
				return null;
			}
			else
			{
				loe_end = look;
				look = start_pos.pop(); // mark and backtrack to test another possibility
			}

			UnaryExpr ue = unary_expr();
			if (ue == null)
			{
				start_pos.pop();
				look = loe_end;
				ret.set_origin(loe);
				break;
			}
			else
				start_pos.pop();

			if (match(Tag.ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.ASSIGN);
			}
			else if (match(Tag.MUL_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.MUL_ASSIGN);
			}
			else if (match(Tag.DIV_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.DIV_ASSIGN);
			}
			else if (match(Tag.ADD_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.ADD_ASSIGN);
			}
			else if (match(Tag.SUB_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.SUB_ASSIGN);
			}
			else if (match(Tag.MOD_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.MOD_ASSIGN);
			}
			else if (match(Tag.SHL_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.SHL_ASSIGN);
			}
			else if (match(Tag.SHR_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.SHR_ASSIGN);
			}
			else if (match(Tag.AND_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.AND_ASSIGN);
			}
			else if (match(Tag.XOR_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.XOR_ASSIGN);
			}
			else if (match(Tag.OR_ASSIGN))
			{
				advance();
				ret.add_left_expr(ue, AssignmentExpr.OR_ASSIGN);
			}
			else
			{
				look = loe_end;
				ret.set_origin(loe); // special case: assignment-expr ::= unary-expr
				break;
			}
		}

		return ret;
	}

	private ConstantExpr const_expr()
	{
		start_pos.push(look);
		LogicalOrExpr x = logical_or_expr();
		if (x == null)
		{
			look = start_pos.pop();
			panic("Unable to match the logic-or-expr when parsing constant-expr.");
			return null;
		}
		else
		{
			start_pos.pop();
			return new ConstantExpr(x);
		}
	}

	private LogicalOrExpr logical_or_expr()
	{
		start_pos.push(look);
		LogicalOrExpr ret = new LogicalOrExpr();
		int cnt = 0;

		for (;;)
		{
			LogicalAndExpr x = logical_and_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " logical-and-expr when parsing logical-or-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret.add_expr(x);
			}

			if (match(Tag.OR))
				advance();
			else
				break;
		}

		return ret;
	}

	private LogicalAndExpr logical_and_expr()
	{
		start_pos.push(look);
		LogicalAndExpr ret = new LogicalAndExpr();
		int cnt = 0;

		for (;;)
		{
			InclusiveOrExpr x = inclusive_or_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " inclusive-or-expr when parsing logical-and-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret.add_expr(x);
			}

			if (match(Tag.AND))
				advance();
			else
				break;
		}

		return ret;
	}

	private InclusiveOrExpr inclusive_or_expr()
	{
		start_pos.push(look);
		InclusiveOrExpr ret = new InclusiveOrExpr();
		int cnt = 0;

		for (;;)
		{
			ExclusiveOrExpr x = exclusive_or_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " exclusive-or-expr when parsing inclusive-or-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret.add_expr(x);
			}

			if (match(Tag.BIT_OR))
				advance();
			else
				break;
		}

		return ret;
	}

	private ExclusiveOrExpr exclusive_or_expr()
	{
		start_pos.push(look);
		ExclusiveOrExpr ret = new ExclusiveOrExpr();
		int cnt = 0;

		for (;;)
		{
			AndExpr x = and_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " and-expr when parsing exclusive-or-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret.add_expr(x);
			}

			if (match(Tag.BIT_XOR))
				advance();
			else
				break;
		}

		return ret;
	}

	private AndExpr and_expr()
	{
		start_pos.push(look);
		AndExpr ret = new AndExpr();
		int cnt = 0;

		for (;;)
		{
			EqualityExpr x = equality_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " equality-expr when parsing and-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret.add_expr(x);
			}

			if (match(Tag.BIT_AND))
				advance();
			else
				break;
		}

		return ret;
	}

	private EqualityExpr equality_expr()
	{
		start_pos.push(look);
		EqualityExpr ret = new EqualityExpr();
		int cnt = 0;

		for (;;)
		{
			RelationalExpr x = relational_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " relational-expr when parsing equality-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				if (match(Tag.EQ))
				{
					advance();
					ret.add_expr(x, BinaryExpr.EQ);
				}
				else if (match(Tag.NE))
				{
					advance();
					ret.add_expr(x, BinaryExpr.NE);
				}
				else
				{
					ret.add_expr(x);
					break;
				}
			}
		}

		return ret;
	}

	private RelationalExpr relational_expr()
	{
		start_pos.push(look);
		RelationalExpr ret = new RelationalExpr();
		int cnt = 0;

		for (;;)
		{
			ShiftExpr x = shift_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " shift-expr when parsing relational-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				if (match(Tag.LT))
				{
					advance();
					ret.add_expr(x, BinaryExpr.LT);
				}
				else if (match(Tag.GT))
				{
					advance();
					ret.add_expr(x, BinaryExpr.GT);
				}
				else if (match(Tag.LE))
				{
					advance();
					ret.add_expr(x, BinaryExpr.LE);
				}
				else if (match(Tag.GE))
				{
					advance();
					ret.add_expr(x, BinaryExpr.GE);
				}
				else
				{
					ret.add_expr(x);
					break;
				}
			}
		}

		return ret;
	}

	private ShiftExpr shift_expr()
	{
		start_pos.push(look);
		ShiftExpr ret = new ShiftExpr();
		int cnt = 0;

		for (;;)
		{
			AdditiveExpr x = additive_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " additive-expr when parsing shift-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				if (match(Tag.SHL))
				{
					advance();
					ret.add_expr(x, BinaryExpr.SHL);
				}
				else if (match(Tag.SHR))
				{
					advance();
					ret.add_expr(x, BinaryExpr.SHR);
				}
				else
				{
					ret.add_expr(x);
					break;
				}
			}
		}

		return ret;
	}

	private AdditiveExpr additive_expr()
	{
		start_pos.push(look);
		AdditiveExpr ret = new AdditiveExpr();
		int cnt = 0;

		for (;;)
		{
			MultiplicativeExpr x = multiplicative_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " multiplicative-expr when parsing additive-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				if (match(Tag.PLUS))
				{
					advance();
					ret.add_expr(x, BinaryExpr.PLUS);
				}
				else if (match(Tag.MINUS))
				{
					advance();
					ret.add_expr(x, BinaryExpr.MINUS);
				}
				else
				{
					ret.add_expr(x);
					break;
				}
			}
		}

		return ret;
	}

	private MultiplicativeExpr multiplicative_expr()
	{
		start_pos.push(look);
		MultiplicativeExpr ret = new MultiplicativeExpr();
		int cnt = 0;

		for (;;)
		{
			CastExpr x = cast_expr();
			++cnt;
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match the " + num2idx(cnt) + " cast-expr when parsing multiplicative-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				if (match(Tag.TIMES))
				{
					advance();
					ret.add_expr(x, BinaryExpr.TIMES);
				}
				else if (match(Tag.DIVIDE))
				{
					advance();
					ret.add_expr(x, BinaryExpr.DIVIDE);
				}
				else if (match(Tag.MODULE))
				{
					advance();
					ret.add_expr(x, BinaryExpr.MODULE);
				}
				else
				{
					ret.add_expr(x);
					break;
				}
			}
		}

		return ret;
	}

	private CastExpr cast_expr()
	{
		start_pos.push(look);
		CastExpr ret = new CastExpr();
		for (;;)
		{
			UnaryExpr ue = unary_expr();
			if (ue == null)
			{
				look = start_pos.pop(); // backtrack
				if (match(Tag.LPAREN))
				{
					advance();
					TypeName tn = type_name();
					if (tn == null)
					{
						look = start_pos.pop();
						panic("Unable to match type-name when parsing cast-expr.");
						return null;
					}
					else
					{
						start_pos.pop();
						ret.add_type(tn);
					}

					if (match(Tag.RPAREN))
						advance();
					else
					{
						panic_missing(')');
						return null;
					}
				}
				else
				{
					panic("Unable to match a valid cast-expr.");
					return null;
				}
			}
			else
			{
				start_pos.pop();
				ret.set_origin(ue);
				break;
			}
		}

		return ret;
	}

	private TypeName type_name()
	{
		start_pos.push(look);
		TypeSpecifier x = type_specifier();
		if (x == null)
		{
			look = start_pos.pop();
			panic("Unable to match a type-specifier when parsing type-name");
			return null;
		}
		else
			start_pos.pop();

		int cnt = 0;
		while (match(Tag.TIMES))
		{
			++cnt;
			advance();
		}

		return new TypeName(x, cnt);
	}

	private UnaryExpr unary_expr()
	{
		start_pos.push(look);
		UnaryExpr ret = null;
		if (match(Tag.BIT_AND))
		{
			advance();
			CastExpr x = cast_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret = new UnaryExpr(UnaryExpr.address, x);
			}
		}
		else if (match(Tag.TIMES))
		{
			advance();
			CastExpr x = cast_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret = new UnaryExpr(UnaryExpr.dereference, x);
			}
		}
		else if (match(Tag.PLUS))
		{
			advance();
			CastExpr x = cast_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret = new UnaryExpr(UnaryExpr.positive, x);
			}
		}
		else if (match(Tag.MINUS))
		{
			advance();
			CastExpr x = cast_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret = new UnaryExpr(UnaryExpr.negative, x);
			}
		}
		else if (match(Tag.BIT_NOT))
		{
			advance();
			CastExpr x = cast_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret = new UnaryExpr(UnaryExpr.bit_not, x);
			}
		}
		else if (match(Tag.NOT))
		{
			advance();
			CastExpr x = cast_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match a cast expr when parsing unary-expr.");
				return null;
			}
			else
			{
				start_pos.pop();
				ret = new UnaryExpr(UnaryExpr.not, x);
			}
		}
		else if (match(Tag.INC))
		{
			advance();
			UnaryExpr x = unary_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match an unary expr when parsing unary-expr");
				return null;
			}
			else
			{
				start_pos.pop();
				ret = new UnaryExpr(UnaryExpr.inc, x);
			}
		}
		else if (match(Tag.DEC))
		{
			advance();
			UnaryExpr x = unary_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match an unary expr when parsing unary-expr");
				return null;
			}
			else
			{
				start_pos.pop();
				ret = new UnaryExpr(UnaryExpr.dec, x);
			}
		}
		else if (match(Tag.SIZEOF))
		{
			advance();
			UnaryExpr ue = unary_expr();// firstly, try unary-expr ::= sizeof unary-expr
			if (ue == null)
				look = start_pos.pop(); // if fail, backtrack
			else
			{
				start_pos.pop();
				return new UnaryExpr(UnaryExpr.sizeof, ue);
			}

			if (match(Tag.LPAREN))
				advance();
			else
			{
				panic_missing('(');
				return null;
			}

			TypeName x = type_name(); // try unary-expr ::= sizeof(type-name)
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match a type-name when parsing \"\".");
				return null;
			}
			else
				start_pos.pop();

			if (match(Tag.RPAREN))
			{
				advance();
				return new UnaryExpr(UnaryExpr.sizeof, x);
			}
			else
			{
				panic_missing(')');
				return null;
			}
		}
		else
		{
			PostfixExpr x = postfix_expr();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Unable to match a postfix expr when parsing unary-expr.");
				return null;
			}
			else
				start_pos.pop();

			ret = new UnaryExpr(UnaryExpr.postfix, x);
		}

		return ret;
	}

	private PostfixExpr postfix_expr()
	{
		start_pos.push(look);
		PrimaryExpr pe = primary_expr();
		if (pe == null)
		{
			look = start_pos.pop();
			panic("Unable to match a primary expr when parsing postfix expr.");
			return null;
		}
		else
			start_pos.pop();

		PostfixExpr ret = new PostfixExpr(pe);
		for (;;)
		{
			if (match(Tag.LMPAREN))
			{
				advance();
				Expression x = expression();
				if (x == null)
				{
					look = start_pos.pop();
					panic("Unable to match an expression when parsing postfix in postfix expr.");
					return null;
				}
				else
					start_pos.pop();

				if (match(Tag.RMPAREN))
				{
					advance();
					ret.add_elem(PostfixExpr.mparen, x);
				}
				else
				{
					panic_missing(']');
					return null;
				}
			}
			else if (match(Tag.LPAREN))
			{
				advance();
				if (match(Tag.RPAREN))
				{
					ret.add_elem(PostfixExpr.paren, null);
					advance();
				}
				else
				{
					LinkedList<AssignmentExpr> arg = new LinkedList<AssignmentExpr>(); // arguments
					int cnt = 0;
					for (;;)
					{
						AssignmentExpr x = assignment_expr();
						++cnt;
						if (x == null)
						{
							look = start_pos.pop();
							panic("Unable to match the " + num2idx(cnt) + " asssignment-expr when parsing arguments.");
							return null;
						}
						else
						{
							start_pos.pop();
							arg.add(x);
						}

						if (match(Tag.COMMA))
							advance();
						else
							break;
					}

					if (match(Tag.RPAREN))
					{
						advance();
						ret.add_elem(PostfixExpr.paren, arg);
					}
					else
					{
						panic_missing(')');
						return null;
					}
				}
			}
			else if (match(Tag.DOT))
			{
				advance();
				if (match(Tag.ID))
				{
					String name = ((Identifier) token_buf.get(look)).name;
					advance();
					ret.add_elem(PostfixExpr.dot, name);
				}
				else
				{
					panic("Unable to match the identifier when parsing postfix.");
					return null;
				}
			}
			else if (match(Tag.PTR))
			{
				advance();
				if (match(Tag.ID))
				{
					String name = ((Identifier) token_buf.get(look)).name;
					advance();
					ret.add_elem(PostfixExpr.ptr, name);
				}
				else
				{
					panic("Unable to match a identifier when parsing postfix.");
					return null;
				}
			}
			else if (match(Tag.INC))
			{
				advance();
				ret.add_elem(PostfixExpr.inc, null);
			}
			else if (match(Tag.DEC))
			{
				advance();
				ret.add_elem(PostfixExpr.dec, null);
			}
			else
				break;
		}

		return ret;
	}

	private PrimaryExpr primary_expr()
	{
		start_pos.push(look);
		if (match(Tag.ID) || match(Tag.CH) || match(Tag.NUM) || match(Tag.REAL) || match(Tag.STR))
		{
			PrimaryExpr ret = new PrimaryExpr(token_buf.get(look));
			advance();
			return ret;
		}
		else
		{
			if (match(Tag.LPAREN))
				advance();
			else
			{
				panic_missing('(');
				return null;
			}

			Expression x = expression();
			if (x == null)
			{
				look = start_pos.pop();
				panic("Failed to match an expr when parsing primary-expr.");
				return null;
			}
			else
				start_pos.pop();

			if (match(Tag.RPAREN))
			{
				advance();
				return new PrimaryExpr(x);
			}
			else
			{
				panic_missing(')');
				return null;
			}
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
		Token tmp = token_buf.get(look);
		String info = String.format("(Line %d, Column %d): %s", tmp.line, tmp.column, msg);
		System.out.println(info);
	}

	private void panic_missing(Character ch)
	{
		panic("Missing \'" + ch + "\'.");
	}
}
