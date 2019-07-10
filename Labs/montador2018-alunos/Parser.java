

import java.util.Hashtable;
import java.util.Enumeration;



public class Parser {
	public static final int _EOF = 0;
	public static final int _rotulo = 1;
	public static final int _strzConst = 2;
	public static final int _num = 3;
	public static final int maxT = 36;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	Hashtable vrotulos;	/* rotulo -> adr */
Hashtable vpcsToFix;	/* pc -> rotulo */
int maxGlobalEnd;	/* apenas para verificar maior endereco global enderecado */
Code objCode;



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
	
	void Montador() {
		vrotulos = new Hashtable();
		vpcsToFix = new Hashtable();
		maxGlobalEnd = -1;
		objCode = new Code();
		objCode.setMainPC();	/* se label "_main_" nao encontrada, mainPc = 0 */        		
		
		while (StartOf(1)) {
			Instrucao();
		}
		Enumeration em = vpcsToFix.keys();
		while(em.hasMoreElements())
		{
						Integer pctofix = (Integer) em.nextElement();
						String r = (String) vpcsToFix.get(pctofix);
						Integer correctpc = (Integer) vrotulos.get(r);
		//System.out.println(">>> " + r + " " + pctofix + " " + correctpc);
		objCode.put2(pctofix, correctpc);
		}
		/* define global area */
		if (maxGlobalEnd > -1)
		objCode.setDataSize(maxGlobalEnd + 1);			
		
	}

	void Instrucao() {
		if (la.kind == 1) {
			Get();
			String r = t.val;
			if (r.equals("_main_")) 
			objCode.setMainPC();
			vrotulos.put(r, new Integer(objCode.getPC()));
			//System.out.println(">>> " + r + " " + objCode.getPC());					
			
			Expect(4);
		}
		CodMaquina();
	}

	void CodMaquina() {
		switch (la.kind) {
		case 5: {
			Get();
			objCode.put(Code.load); 
			Byte();
			break;
		}
		case 6: {
			Get();
			objCode.put(Code.store); 
			Byte();
			break;
		}
		case 7: {
			Get();
			objCode.put(Code.getstatic); 
			Short();
			break;
		}
		case 8: {
			Get();
			objCode.put(Code.putstatic); 
			Short();
			Integer end = Integer.parseInt(t.val); 
			if (end > maxGlobalEnd)
			maxGlobalEnd = end;
			
			break;
		}
		case 9: {
			Get();
			objCode.put(Code.const_); 
			Word();
			break;
		}
		case 10: {
			Get();
			objCode.put(Code.add); 
			break;
		}
		case 11: {
			Get();
			objCode.put(Code.sub); 
			break;
		}
		case 12: {
			Get();
			objCode.put(Code.mul); 
			break;
		}
		case 13: {
			Get();
			objCode.put(Code.div); 
			break;
		}
		case 14: {
			Get();
			objCode.put(Code.rem); 
			break;
		}
		case 15: {
			Get();
			objCode.put(Code.neg); 
			break;
		}
		case 16: {
			Get();
			objCode.put(Code.newarray); 
			break;
		}
		case 17: {
			Get();
			objCode.put(Code.aload); 
			break;
		}
		case 18: {
			Get();
			objCode.put(Code.astore); 
			break;
		}
		case 19: {
			Get();
			objCode.put(Code.arraylength); 
			break;
		}
		case 20: {
			Get();
			objCode.put(Code.pop); 
			break;
		}
		case 21: {
			Get();
			objCode.put(Code.jmp); 
			Rotulo();
			break;
		}
		case 22: {
			Get();
			objCode.put(Code.jeq); 
			Rotulo();
			break;
		}
		case 23: {
			Get();
			objCode.put(Code.jne); 
			Rotulo();
			break;
		}
		case 24: {
			Get();
			objCode.put(Code.jlt); 
			Rotulo();
			break;
		}
		case 25: {
			Get();
			objCode.put(Code.jle); 
			Rotulo();
			break;
		}
		case 26: {
			Get();
			objCode.put(Code.jgt); 
			Rotulo();
			break;
		}
		case 27: {
			Get();
			objCode.put(Code.jge); 
			Rotulo();
			break;
		}
		case 28: {
			Get();
			objCode.put(Code.call); 
			Rotulo();
			break;
		}
		case 29: {
			Get();
			objCode.put(Code.return_); 
			break;
		}
		case 30: {
			Get();
			objCode.put(Code.enter); 
			Byte();
			Byte();
			break;
		}
		case 31: {
			Get();
			objCode.put(Code.exit); 
			break;
		}
		case 32: {
			Get();
			objCode.put(Code.scani); 
			break;
		}
		case 33: {
			Get();
			objCode.put(Code.printi); 
			break;
		}
		case 34: {
			Get();
			Expect(2);
			String s = t.val.substring(1, t.val.length() - 1);
			objCode.putPrintStrz(s);
			
			break;
		}
		case 35: {
			Get();
			objCode.put(Code.trap); 
			Byte();
			break;
		}
		default: SynErr(37); break;
		}
	}

	void Byte() {
		Expect(3);
		objCode.put(Integer.parseInt(t.val)); 
	}

	void Short() {
		Expect(3);
		objCode.put2(Integer.parseInt(t.val)); 
	}

	void Word() {
		Expect(3);
		objCode.put4(Integer.parseInt(t.val)); 
	}

	void Rotulo() {
		Expect(1);
		vpcsToFix.put(new Integer(objCode.getPC()), t.val);
		objCode.put2(-1); /* address has to be fixed */
		
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Montador();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x}

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
			case 1: s = "rotulo expected"; break;
			case 2: s = "strzConst expected"; break;
			case 3: s = "num expected"; break;
			case 4: s = "\":\" expected"; break;
			case 5: s = "\"load\" expected"; break;
			case 6: s = "\"store\" expected"; break;
			case 7: s = "\"getstatic\" expected"; break;
			case 8: s = "\"putstatic\" expected"; break;
			case 9: s = "\"const\" expected"; break;
			case 10: s = "\"add\" expected"; break;
			case 11: s = "\"sub\" expected"; break;
			case 12: s = "\"mul\" expected"; break;
			case 13: s = "\"div\" expected"; break;
			case 14: s = "\"rem\" expected"; break;
			case 15: s = "\"neg\" expected"; break;
			case 16: s = "\"newarray\" expected"; break;
			case 17: s = "\"aload\" expected"; break;
			case 18: s = "\"astore\" expected"; break;
			case 19: s = "\"arraylength\" expected"; break;
			case 20: s = "\"pop\" expected"; break;
			case 21: s = "\"jmp\" expected"; break;
			case 22: s = "\"jeq\" expected"; break;
			case 23: s = "\"jne\" expected"; break;
			case 24: s = "\"jlt\" expected"; break;
			case 25: s = "\"jle\" expected"; break;
			case 26: s = "\"jgt\" expected"; break;
			case 27: s = "\"jge\" expected"; break;
			case 28: s = "\"call\" expected"; break;
			case 29: s = "\"return\" expected"; break;
			case 30: s = "\"enter\" expected"; break;
			case 31: s = "\"exit\" expected"; break;
			case 32: s = "\"scani\" expected"; break;
			case 33: s = "\"printi\" expected"; break;
			case 34: s = "\"prints\" expected"; break;
			case 35: s = "\"trap\" expected"; break;
			case 36: s = "??? expected"; break;
			case 37: s = "invalid CodMaquina"; break;
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
