

public class Parser {
	public static final int _EOF = 0;
	public static final int _numero = 1;
	public static final int maxT = 20;

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
	
	void Radio() {
		while (la.kind == 2 || la.kind == 3 || la.kind == 10) {
			if (la.kind == 10) {
				Entrevista();
			} else if (la.kind == 2) {
				Get();
			} else {
				Get();
				if (StartOf(1)) {
					BoletimNoticias();
				}
			}
		}
		Expect(0);
	}

	void Entrevista() {
		Expect(10);
		while (la.kind == 11) {
			Get();
			Expect(10);
		}
	}

	void BoletimNoticias() {
		Noticia();
		while (StartOf(1)) {
			Noticia();
		}
		while (StartOf(2)) {
			Clima();
		}
		Preenchimento();
	}

	void Noticia() {
		switch (la.kind) {
		case 4: {
			Get();
			if (la.kind == 5) {
				Get();
			}
			break;
		}
		case 5: {
			Get();
			Expect(4);
			break;
		}
		case 6: {
			Get();
			break;
		}
		case 7: {
			Get();
			break;
		}
		case 8: {
			Get();
			Expect(2);
			break;
		}
		case 9: {
			Get();
			break;
		}
		case 12: {
			Acidente();
			break;
		}
		default: SynErr(21); break;
		}
	}

	void Clima() {
		if (la.kind == 16) {
			Get();
		} else if (la.kind == 17) {
			Get();
		} else if (la.kind == 18) {
			Get();
		} else if (la.kind == 19) {
			Get();
		} else SynErr(22);
	}

	void Preenchimento() {
		if (la.kind == 2) {
			Get();
		} else if (la.kind == 3) {
			Get();
		} else SynErr(23);
	}

	void Acidente() {
		Expect(12);
		Expect(13);
		Expect(1);
		Expect(14);
		Expect(15);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Radio();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, T,T,T,T, T,T,x,x, T,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x}

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
			case 1: s = "numero expected"; break;
			case 2: s = "\"musica\" expected"; break;
			case 3: s = "\"propaganda\" expected"; break;
			case 4: s = "\"Republicanos\" expected"; break;
			case 5: s = "\"Trump\" expected"; break;
			case 6: s = "\"xenofobia\" expected"; break;
			case 7: s = "\"corrup\u00c3\u00a7\u00c3\u00a3o\" expected"; break;
			case 8: s = "\"comentario\" expected"; break;
			case 9: s = "\"racismo\" expected"; break;
			case 10: s = "\"apresentador\" expected"; break;
			case 11: s = "\"entrevistado\" expected"; break;
			case 12: s = "\"colis\u00c3\u00a3o\" expected"; break;
			case 13: s = "\"com\" expected"; break;
			case 14: s = "\"de\" expected"; break;
			case 15: s = "\"vitimas\" expected"; break;
			case 16: s = "\"chuva\" expected"; break;
			case 17: s = "\"nublado\" expected"; break;
			case 18: s = "\"ensolarado\" expected"; break;
			case 19: s = "\"seco\" expected"; break;
			case 20: s = "??? expected"; break;
			case 21: s = "invalid Noticia"; break;
			case 22: s = "invalid Clima"; break;
			case 23: s = "invalid Preenchimento"; break;
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
