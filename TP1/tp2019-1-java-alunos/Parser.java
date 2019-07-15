

public class Parser {
	public static final int _EOF = 0;
	public static final int _id = 1;
	public static final int _strConst = 2;
	public static final int _num = 3;
	public static final int maxT = 32;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	

	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void microc() {
		while (la.kind == 4) {
			DeclConst();
		}
		while (la.kind == 7 || la.kind == 11) {
			Definicao();
		}
	}

	void DeclConst() {
		Expect(4);
		Tipo();
		Expect(1);
		Expect(5);
		Expect(3);
		Expect(6);
	}

	void Definicao() {
		if (la.kind == 11) {
			Tipo();
		} else if (la.kind == 7) {
			Get();
		} else SynErr(33);
		DesigI();
		if (la.kind == 6 || la.kind == 8) {
			DeclVar();
		} else if (la.kind == 9) {
			DeclFuncao();
		} else SynErr(34);
	}

	void Tipo() {
		Expect(11);
	}

	void DesigI() {
		Expect(1);
	}

	void DeclVar() {
		while (la.kind == 8) {
			Get();
			DesigI();
		}
		Expect(6);
	}

	void DeclFuncao() {
		Expect(9);
		if (la.kind == 11) {
			Tipo();
			DesigI();
			while (la.kind == 8) {
				Get();
				Tipo();
				DesigI();
			}
		}
		Expect(10);
		CBlock();
	}

	void CBlock() {
		Expect(12);
		while (StartOf(1)) {
			if (la.kind == 7 || la.kind == 11) {
				Definicao();
			} else {
				Instrucao();
			}
		}
		Expect(13);
	}

	void Instrucao() {
		switch (la.kind) {
		case 1: {
			Designador();
			if (la.kind == 5) {
				Atrib();
			} else if (la.kind == 9) {
				Parametros();
			} else SynErr(35);
			Expect(6);
			break;
		}
		case 14: {
			While();
			break;
		}
		case 15: {
			For();
			break;
		}
		case 16: {
			IfElse();
			break;
		}
		case 18: {
			Return();
			break;
		}
		case 19: {
			Printf();
			break;
		}
		case 20: {
			Scanf();
			break;
		}
		case 12: {
			CBlock();
			break;
		}
		case 6: {
			Get();
			break;
		}
		default: SynErr(36); break;
		}
	}

	void Designador() {
		Expect(1);
	}

	void Atrib() {
		Expect(5);
		Expr();
	}

	void Parametros() {
		Expect(9);
		if (StartOf(2)) {
			Expr();
			while (la.kind == 8) {
				Get();
				Expr();
			}
		}
		Expect(10);
	}

	void While() {
		Expect(14);
		Expect(9);
		Condicao();
		Expect(10);
		Instrucao();
	}

	void For() {
		Expect(15);
		Expect(9);
		Designador();
		Atrib();
		Expect(6);
		Condicao();
		Expect(6);
		Designador();
		Atrib();
		Expect(10);
		Instrucao();
	}

	void IfElse() {
		Expect(16);
		Expect(9);
		Condicao();
		Expect(10);
		Instrucao();
		if (la.kind == 17) {
			Get();
			Instrucao();
		} else if (StartOf(3)) {
		} else SynErr(37);
	}

	void Return() {
		Expect(18);
		if (StartOf(2)) {
			Expr();
		} else if (la.kind == 6) {
		} else SynErr(38);
		Expect(6);
	}

	void Printf() {
		Expect(19);
		Expect(9);
		if (la.kind == 2) {
			Get();
		} else if (StartOf(2)) {
			Expr();
		} else SynErr(39);
		while (la.kind == 8) {
			Get();
			if (la.kind == 2) {
				Get();
			} else if (StartOf(2)) {
				Expr();
			} else SynErr(40);
		}
		Expect(10);
		Expect(6);
	}

	void Scanf() {
		Expect(20);
		Expect(9);
		Designador();
		Expect(6);
	}

	void Expr() {
		if (la.kind == 1 || la.kind == 3 || la.kind == 9) {
			Termo();
		} else if (la.kind == 27) {
			Get();
			Termo();
		} else SynErr(41);
		while (la.kind == 27 || la.kind == 28) {
			if (la.kind == 28) {
				Get();
			} else {
				Get();
			}
			Termo();
		}
	}

	void Condicao() {
		Expr();
		OpRel();
		Expr();
	}

	void OpRel() {
		switch (la.kind) {
		case 21: {
			Get();
			break;
		}
		case 22: {
			Get();
			break;
		}
		case 23: {
			Get();
			break;
		}
		case 24: {
			Get();
			break;
		}
		case 25: {
			Get();
			break;
		}
		case 26: {
			Get();
			break;
		}
		default: SynErr(42); break;
		}
	}

	void Termo() {
		Fator();
		while (la.kind == 29 || la.kind == 30 || la.kind == 31) {
			if (la.kind == 29) {
				Get();
			} else if (la.kind == 30) {
				Get();
			} else {
				Get();
			}
			Fator();
		}
	}

	void Fator() {
		if (la.kind == 1) {
			Designador();
			if (la.kind == 9) {
				Parametros();
			}
		} else if (la.kind == 3) {
			Get();
		} else if (la.kind == 9) {
			Get();
			Expr();
			Expect(10);
		} else SynErr(43);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		microc();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,T,T, x,x,x,T, T,x,T,T, T,x,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,T, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x},
		{x,T,x,x, x,x,T,T, x,x,x,T, T,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "id expected"; break;
			case 2: s = "strConst expected"; break;
			case 3: s = "num expected"; break;
			case 4: s = "\"const\" expected"; break;
			case 5: s = "\"=\" expected"; break;
			case 6: s = "\";\" expected"; break;
			case 7: s = "\"void\" expected"; break;
			case 8: s = "\",\" expected"; break;
			case 9: s = "\"(\" expected"; break;
			case 10: s = "\")\" expected"; break;
			case 11: s = "\"int\" expected"; break;
			case 12: s = "\"{\" expected"; break;
			case 13: s = "\"}\" expected"; break;
			case 14: s = "\"while\" expected"; break;
			case 15: s = "\"for\" expected"; break;
			case 16: s = "\"if\" expected"; break;
			case 17: s = "\"else\" expected"; break;
			case 18: s = "\"return\" expected"; break;
			case 19: s = "\"printf\" expected"; break;
			case 20: s = "\"scanf\" expected"; break;
			case 21: s = "\"==\" expected"; break;
			case 22: s = "\"!=\" expected"; break;
			case 23: s = "\">\" expected"; break;
			case 24: s = "\">=\" expected"; break;
			case 25: s = "\"<\" expected"; break;
			case 26: s = "\"<=\" expected"; break;
			case 27: s = "\"-\" expected"; break;
			case 28: s = "\"+\" expected"; break;
			case 29: s = "\"*\" expected"; break;
			case 30: s = "\"/\" expected"; break;
			case 31: s = "\"%\" expected"; break;
			case 32: s = "??? expected"; break;
			case 33: s = "invalid Definicao"; break;
			case 34: s = "invalid Definicao"; break;
			case 35: s = "invalid Instrucao"; break;
			case 36: s = "invalid Instrucao"; break;
			case 37: s = "invalid IfElse"; break;
			case 38: s = "invalid Return"; break;
			case 39: s = "invalid Printf"; break;
			case 40: s = "invalid Printf"; break;
			case 41: s = "invalid Expr"; break;
			case 42: s = "invalid OpRel"; break;
			case 43: s = "invalid Fator"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
