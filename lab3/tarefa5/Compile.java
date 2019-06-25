import java.io.*;

public class Compile 
{
	public static void main(String argv[]) 
	{
		Scanner s = new Scanner(argv[0]);
		Parser p = new Parser(s);
		p.Parse();
		//System.out.println("Numero de erros: " + p.errors.count);
	}
}

