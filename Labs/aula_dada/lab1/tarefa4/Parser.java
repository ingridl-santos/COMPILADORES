

public class Parser {
	public static final int _EOF = 0;
	public static final int _palavra = 1;
	public static final int _pontuacao = 2;
	public static final int maxT = 21;

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
	
	void Musica() {
		Linha();
		while (StartOf(1)) {
			Linha();
		}
	}

	void Linha() {
		if (StartOf(2)) {
			Notas();
		}
		Expect(3);
		Sentenca();
		Expect(3);
	}

	void Notas() {
		Nota();
		while (StartOf(2)) {
			Nota();
		}
	}

	void Sentenca() {
		if (la.kind == 1) {
			Get();
		} else if (la.kind == 2) {
			Get();
		} else SynErr(22);
		while (la.kind == 1 || la.kind == 2) {
			if (la.kind == 1) {
				Get();
			} else {
				Get();
			}
		}
	}

	void Nota() {
		switch (la.kind) {
		case 4: {
			Get();
			break;
		}
		case 5: {
			Get();
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
			break;
		}
		case 9: {
			Get();
			break;
		}
		case 10: {
			Get();
			break;
		}
		case 11: {
			Get();
			break;
		}
		case 12: {
			Get();
			break;
		}
		case 13: {
			Get();
			break;
		}
		case 14: {
			Get();
			break;
		}
		case 15: {
			Get();
			break;
		}
		case 16: {
			Get();
			break;
		}
		case 17: {
			Get();
			break;
		}
		case 18: {
			Get();
			break;
		}
		case 19: {
			Get();
			break;
		}
		case 20: {
			Get();
			break;
		}
		default: SynErr(23); break;
		}
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Musica();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,x,x,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x},
		{x,x,x,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x}

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
			case 1: s = "palavra expected"; break;
			case 2: s = "pontuacao expected"; break;
			case 3: s = "\"\\n\" expected"; break;
			case 4: s = "\"C\" expected"; break;
			case 5: s = "\"C#\" expected"; break;
			case 6: s = "\"Db\" expected"; break;
			case 7: s = "\"D\" expected"; break;
			case 8: s = "\"D#\" expected"; break;
			case 9: s = "\"Eb\" expected"; break;
			case 10: s = "\"E\" expected"; break;
			case 11: s = "\"F\" expected"; break;
			case 12: s = "\"F#\" expected"; break;
			case 13: s = "\"Gb\" expected"; break;
			case 14: s = "\"G\" expected"; break;
			case 15: s = "\"G#\" expected"; break;
			case 16: s = "\"Ab\" expected"; break;
			case 17: s = "\"A\" expected"; break;
			case 18: s = "\"A#\" expected"; break;
			case 19: s = "\"Bb\" expected"; break;
			case 20: s = "\"B\" expected"; break;
			case 21: s = "??? expected"; break;
			case 22: s = "invalid Sentenca"; break;
			case 23: s = "invalid Nota"; break;
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
