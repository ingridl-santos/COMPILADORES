

public class Parser {
	public static final int _EOF = 0;
	public static final int _palavra = 1;
	public static final int _num = 2;
	public static final int _nome = 3;
	public static final int maxT = 17;

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
	
	void Familia() {
		Sobrenome();
		while (StartOf(1)) {
			Secao();
		}
	}

	void Sobrenome() {
		Expect(4);
		Expect(3);
		Expect(5);
	}

	void Secao() {
		if (la.kind == 6) {
			Pais();
		} else if (la.kind == 7) {
			Filhos();
		} else if (la.kind == 8) {
			Avos();
		} else if (la.kind == 9) {
			Netos();
		} else if (la.kind == 10) {
			Outros();
		} else SynErr(18);
	}

	void Pais() {
		Expect(6);
		ListaNomes();
		Expect(5);
	}

	void Filhos() {
		Expect(7);
		ListaNomes();
		Expect(5);
	}

	void Avos() {
		Expect(8);
		ListaNomes();
		Expect(5);
	}

	void Netos() {
		Expect(9);
		ListaNomes();
		Expect(5);
	}

	void Outros() {
		Expect(10);
		ListaFrases();
		Expect(5);
	}

	void ListaNomes() {
		NomeComentado();
		while (la.kind == 11) {
			Get();
			NomeComentado();
		}
	}

	void ListaFrases() {
		Frase();
		while (la.kind == 11) {
			Get();
			Frase();
		}
	}

	void NomeComentado() {
		NomeComposto();
		if (la.kind == 12) {
			Falecido();
		}
		if (la.kind == 15) {
			Comentario();
		}
	}

	void NomeComposto() {
		Expect(3);
		while (la.kind == 1 || la.kind == 3) {
			if (la.kind == 1) {
				Get();
			} else {
				Get();
			}
		}
	}

	void Falecido() {
		Expect(12);
		Expect(13);
		Expect(14);
	}

	void Comentario() {
		Expect(15);
		Frase();
		Expect(16);
	}

	void Frase() {
		if (la.kind == 1) {
			Get();
		} else if (la.kind == 3) {
			Get();
		} else if (la.kind == 2) {
			Get();
		} else SynErr(19);
		while (la.kind == 1 || la.kind == 2 || la.kind == 3) {
			if (la.kind == 1) {
				Get();
			} else if (la.kind == 3) {
				Get();
			} else {
				Get();
			}
		}
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Familia();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,x,x,x, x,x,T,T, T,T,T,x, x,x,x,x, x,x,x}

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
			case 2: s = "num expected"; break;
			case 3: s = "nome expected"; break;
			case 4: s = "\"Sobrenome:\" expected"; break;
			case 5: s = "\"\\n\" expected"; break;
			case 6: s = "\"Pais:\" expected"; break;
			case 7: s = "\"Filhos:\" expected"; break;
			case 8: s = "\"Avos:\" expected"; break;
			case 9: s = "\"Netos:\" expected"; break;
			case 10: s = "\"Outros:\" expected"; break;
			case 11: s = "\",\" expected"; break;
			case 12: s = "\"(\" expected"; break;
			case 13: s = "\"falecido\" expected"; break;
			case 14: s = "\")\" expected"; break;
			case 15: s = "\"[\" expected"; break;
			case 16: s = "\"]\" expected"; break;
			case 17: s = "??? expected"; break;
			case 18: s = "invalid Secao"; break;
			case 19: s = "invalid Frase"; break;
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
