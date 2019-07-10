

public class Parser {
	public static final int _EOF = 0;
	public static final int _num = 1;
	public static final int maxT = 12;

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
	
	void Calc4() {
		Linha();
		LinhaR();
	}

	void Linha() {
		Expect(2);
		Exp();
		Expect(3);
	}

	void LinhaR() {
		if (la.kind == 2) {
			Linha();
			LinhaR();
		} else if (la.kind == 0) {
		} else SynErr(13);
	}

	void Exp() {
		Termo();
		ExpR();
	}

	void Termo() {
		Unario();
		TermoR();
	}

	void ExpR() {
		if (la.kind == 8 || la.kind == 9) {
			SomaSub();
			Termo();
			ExpR();
		} else if (la.kind == 3 || la.kind == 6) {
		} else SynErr(14);
	}

	void SomaSub() {
		if (la.kind == 8) {
			Get();
		} else if (la.kind == 9) {
			Get();
		} else SynErr(15);
	}

	void Unario() {
		Fator();
		UnarioR();
	}

	void TermoR() {
		if (la.kind == 10 || la.kind == 11) {
			ProdutoDiv();
			Unario();
			TermoR();
		} else if (StartOf(1)) {
		} else SynErr(16);
	}

	void ProdutoDiv() {
		if (la.kind == 10) {
			Get();
		} else if (la.kind == 11) {
			Get();
		} else SynErr(17);
	}

	void Fator() {
		if (la.kind == 1) {
			Get();
		} else if (la.kind == 5) {
			Get();
			Exp();
			Expect(6);
		} else if (la.kind == 7) {
			Get();
			Expect(5);
			Exp();
			Expect(6);
		} else SynErr(18);
	}

	void UnarioR() {
		if (la.kind == 4) {
			Get();
			UnarioR();
		} else if (StartOf(2)) {
		} else SynErr(19);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Calc4();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,T, x,x,T,x, T,T,x,x, x,x},
		{x,x,x,T, x,x,T,x, T,T,T,T, x,x}

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
			case 1: s = "num expected"; break;
			case 2: s = "\"calc\" expected"; break;
			case 3: s = "\"\\n\" expected"; break;
			case 4: s = "\"!\" expected"; break;
			case 5: s = "\"(\" expected"; break;
			case 6: s = "\")\" expected"; break;
			case 7: s = "\"abs\" expected"; break;
			case 8: s = "\"+\" expected"; break;
			case 9: s = "\"-\" expected"; break;
			case 10: s = "\"*\" expected"; break;
			case 11: s = "\"/\" expected"; break;
			case 12: s = "??? expected"; break;
			case 13: s = "invalid LinhaR"; break;
			case 14: s = "invalid ExpR"; break;
			case 15: s = "invalid SomaSub"; break;
			case 16: s = "invalid TermoR"; break;
			case 17: s = "invalid ProdutoDiv"; break;
			case 18: s = "invalid Fator"; break;
			case 19: s = "invalid UnarioR"; break;
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
