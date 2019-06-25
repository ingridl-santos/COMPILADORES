import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Compile 
{
   public static void main(String argv[]) 
   {
      String str;
      System.out.println("Calculadora (digite 's' para sair)");
      System.out.println("Exemplos de expressoes: abs(-1), 2+5-3*4, 3*(1+4), 3!!, 3.2/4");
      while (true) {
         try {
            System.out.print("? ");
            BufferedReader bufferRead = new 
                           BufferedReader(new InputStreamReader(System.in));
            str = bufferRead.readLine();
            if (str.equalsIgnoreCase("s"))
                    System.exit(0);
            InputStream stream = new 
                                 ByteArrayInputStream(str.getBytes("UTF-8"));
            Scanner s = new Scanner(stream);
            Parser p = new Parser(s);
            p.Parse();
            
        } catch(IOException e) {
            e.printStackTrace();
        }
     }
  }
}
