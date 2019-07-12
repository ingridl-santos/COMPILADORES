

import java.io.*;

class Erro {
	boolean bErro; /* estado de erro */
	String status; /* qual o erro */

	public Erro()
	{
		bErro = false;
		status = "";
	}

	public void setDivZero()
	{
		bErro = true;
		status = "divisao por zero!";
	}

	public void setNegFat()
	{
		bErro = true;
		status = "fatorial de numero negativo!";
	}

	public String getStatus()
	{
		return status;
	}

	public boolean getErro()
	{
		return bErro;
	}
}



public class Parser {
	public static final int _EOF = 0;
	public static final int _numero = 1;
	public static final int maxT = 10;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	Erro e;

float abs(float v) {
	return v >= 0? v: -v;
}

int fat(int v) {
	return v == 0? 1: v * fat(v-1);
}

void show(float v) {
	if (e.getErro())
		System.out.println("  ERRO: " + e.getStatus());
	else if (v%1 == 0)
		System.out.println("  " + (int)v);
	else
		System.out.println("  " + v);
}



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
	
	void Calc() {
		float v = 0; e = new Erro(); 
		v = Exp();
		show(v); 
		Expect(0);
	}

	float  Exp() {
		float  v;
		float v1, v2, sinal; v1 = v2 = 0; sinal = 1; 
		if (la.kind == 2) {
			Get();
			sinal = -1; 
		}
		v1 = T();
		v = sinal * v1; 
		while (la.kind == 2 || la.kind == 3) {
			if (la.kind == 3) {
				Get();
				v2 = T();
				v += v2; 
			} else {
				Get();
				v2 = T();
				v -= v2; 
			}
		}
		return v;
	}

	float  T() {
		float  v;
		float v1, v2; v1 = v2 = 0; 
		v1 = U();
		v = v1; 
		while (la.kind == 4 || la.kind == 5) {
			if (la.kind == 4) {
				Get();
				v2 = U();
				v *= v2; 
			} else {
				Get();
				v2 = U();
				if (v2 == 0)
				e.setDivZero();
				else 
				v /= v2; 
				
			}
		}
		return v;
	}

	float  U() {
		float  vu;
		float v = 0; vu = 0; 
		v = F();
		vu = v; 
		while (la.kind == 6) {
			Get();
			if (vu < 0) e.setNegFat(); else vu = fat((int)vu); 
		}
		return vu;
	}

	float  F() {
		float  v;
		float vexp = 0; v = 0; 
		if (la.kind == 1) {
			Get();
			v = Float.parseFloat(t.val); 
		} else if (la.kind == 7) {
			Get();
			vexp = Exp();
			v = vexp; 
			Expect(8);
		} else if (la.kind == 9) {
			Get();
			Expect(7);
			vexp = Exp();
			v = abs(vexp); 
			Expect(8);
		} else SynErr(11);
		return v;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Calc();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x}

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
			case 2: s = "\"-\" expected"; break;
			case 3: s = "\"+\" expected"; break;
			case 4: s = "\"*\" expected"; break;
			case 5: s = "\"/\" expected"; break;
			case 6: s = "\"!\" expected"; break;
			case 7: s = "\"(\" expected"; break;
			case 8: s = "\")\" expected"; break;
			case 9: s = "\"abs\" expected"; break;
			case 10: s = "??? expected"; break;
			case 11: s = "invalid F"; break;
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
