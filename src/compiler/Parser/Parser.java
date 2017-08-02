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

	private Token next_token()
	{
		return token_buf.get(look + 1);
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

		if (match(Tag.SEMI))
		{
			advance();
			return new Declaration(t);// declaration ::= type-specifier;
		}

		Declaration ret = new Declaration(t);
		int cnt = 0;
		do
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
		} while (match(Tag.COMMA));

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

		PlainDeclarator pd = plain_declarator();
		if (pd == null)
		{
			look = start_pos.pop();
			panic("Unable to match the plain-declarator when parsing function-definition.");
			return null;
		}

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
			do
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
			} while (match(Tag.COMMA));
		}

		if (!match(Tag.RPAREN))
		{
			panic_missing(')');
			return null;
		}

		advance();
		CompoundStmt y = compound_stmt();
		if (y == null)
		{
			look = start_pos.pop();
			panic("Unable to match the compound-statement when parsing function-definition.");
			return null;
		}

		ret.add_body(y);
		return ret;
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
			Initializer ret = new Initializer();
			int cnt = 0;

			do
			{
				advance();
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
			} while (match(Tag.COMMA));

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
		if (!match(Tag.LBRACE))
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

	private IterationStmt iteration_stmt()
	{
		start_pos.push(look);
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
			if (!match(Tag.SEMI))
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

			if (!match(Tag.SEMI))
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

			if (!match(Tag.RPAREN))
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

	private JumpStmt jump_stmt()
	{
		start_pos.push(look);
		JumpStmt ret = null;
		if (match(Tag.CONTINUE))
			ret = new JumpStmt(JumpStmt.CTNU, null);
		else if (match(Tag.BREAK))
			ret = new JumpStmt(JumpStmt.BRK, null);
		else if (match(Tag.RETURN))
		{
			if (match(Tag.SEMI))
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

	private Expression expression()
	{
		start_pos.push(look);
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

	private AssignmentExpr assignment_expr()
	{
		start_pos.push(look);
		AssignmentExpr ret = new AssignmentExpr();
		for (;;)
		{
			LogicalOrExpr loe = logical_or_expr();
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

	private ConstantExpr const_expr()
	{
		start_pos.push(look);
		LogicalOrExpr x = logical_or_expr();
		if (x == null)
		{
			panic("Unable to match the logic-or-expr when parsing constant-expr.");
			return null;
		}
		else
			return new ConstantExpr(x);
	}

	private LogicalOrExpr logical_or_expr()
	{
		start_pos.push(look);
		LogicalOrExpr ret = new LogicalOrExpr();
		int cnt = 0;
		do
		{
			LogicalAndExpr x = logical_and_expr();
			++cnt;
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " logical-and-expr when parsing logical-or-expr.");
				return null;
			}
			else
				ret.add_expr(x);

		} while (match(Tag.OR));

		return ret;
	}

	private LogicalAndExpr logical_and_expr()
	{
		start_pos.push(look);
		LogicalAndExpr ret = new LogicalAndExpr();
		int cnt = 0;
		do
		{
			InclusiveOrExpr x = inclusive_or_expr();
			++cnt;
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " inclusive-or-expr when parsing logical-and-expr.");
				return null;
			}
			else
				ret.add_expr(x);
		} while (match(Tag.AND));

		return ret;
	}

	private InclusiveOrExpr inclusive_or_expr()
	{
		start_pos.push(look);
		InclusiveOrExpr ret = new InclusiveOrExpr();
		int cnt = 0;
		do
		{
			ExclusiveOrExpr x = exclusive_or_expr();
			++cnt;
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " exclusive-or-expr when parsing inclusive-or-expr.");
				return null;
			}
			else
				ret.add_expr(x);
		} while (match(Tag.AND));

		return ret;
	}

	private ExclusiveOrExpr exclusive_or_expr()
	{
		start_pos.push(look);
		ExclusiveOrExpr ret = new ExclusiveOrExpr();
		int cnt = 0;
		do
		{
			AndExpr x = and_expr();
			++cnt;
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " and-expr when parsing exclusive-or-expr.");
				return null;
			}
			else
				ret.add_expr(x);

		} while (match(Tag.AND));

		return ret;
	}

	private AndExpr and_expr()
	{
		start_pos.push(look);
		AndExpr ret = new AndExpr();
		int cnt = 0;
		do
		{
			EqualityExpr x = equality_expr();
			++cnt;
			if (x == null)
			{
				panic("Unable to match the " + num2idx(cnt) + " equality-expr when parsing and-expr.");
				return null;
			}
			else
				ret.add_expr(x);
		} while (match(Tag.AND));

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
				panic("Unable to match the " + num2idx(cnt) + " relational-expr when parsing equality-expr.");
				return null;
			}
			else
			{
				if (match(Tag.EQ))
					ret.add_expr(x, BinaryExpr.EQ);
				else if (match(Tag.NE))
					ret.add_expr(x, BinaryExpr.NE);
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
				panic("Unable to match the " + num2idx(cnt) + " shift-expr when parsing relational-expr.");
				return null;
			}
			else
			{
				if (match(Tag.LT))
					ret.add_expr(x, BinaryExpr.LT);
				else if (match(Tag.GT))
					ret.add_expr(x, BinaryExpr.GT);
				else if (match(Tag.LE))
					ret.add_expr(x, BinaryExpr.LE);
				else if (match(Tag.GE))
					ret.add_expr(x, BinaryExpr.GE);
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
				panic("Unable to match the " + num2idx(cnt) + " additive-expr when parsing shift-expr.");
				return null;
			}
			else
			{
				if (match(Tag.SHL))
					ret.add_expr(x, BinaryExpr.SHL);
				else if (match(Tag.SHR))
					ret.add_expr(x, BinaryExpr.SHR);
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
				panic("Unable to match the " + num2idx(cnt) + " multiplicative-expr when parsing additive-expr.");
				return null;
			}
			else
			{
				if (match(Tag.PLUS))
					ret.add_expr(x, BinaryExpr.PLUS);
				else if (match(Tag.MINUS))
					ret.add_expr(x, BinaryExpr.MINUS);
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
				panic("Unable to match the " + num2idx(cnt) + " cast-expr when parsing multiplicative-expr.");
				return null;
			}
			else
			{
				if (match(Tag.TIMES))
					ret.add_expr(x, BinaryExpr.TIMES);
				else if (match(Tag.DIVIDE))
					ret.add_expr(x, BinaryExpr.DIVIDE);
				else if (match(Tag.MODULE))
					ret.add_expr(x, BinaryExpr.MODULE);
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

	private TypeName type_name()
	{
		start_pos.push(look);
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

	private UnaryExpr unary_expr()
	{
		start_pos.push(look);
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

	private PostfixExpr postfix_expr()
	{
		start_pos.push(look);
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
				if (match(Tag.RPAREN))
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
				if (!match(Tag.ID))
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
				if (!match(Tag.ID))
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

	private PrimaryExpr primary_expr()
	{
		start_pos.push(look);
		if (match(Tag.ID) || match(Tag.CH) || match(Tag.NUM) || match(Tag.REAL) || match(Tag.STR))
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
		Token tmp = token_buf.get(look);
		String info = String.format("(Line %d, Column %d): %s", tmp.line, tmp.column, msg);
		System.out.println(info);
	}

	private void panic_missing(Character ch)
	{
		panic("Missing \'" + ch + "\'.");
	}
}
