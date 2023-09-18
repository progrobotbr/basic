package basic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class _ {
	
	public static void empty(){
		pl("EMPTY");
	}
	public static void halt(line pl){
		String s="";
		if(pl==null){
			return;
		}
		s=" line:"+pl.line;
		pl("<HALT"+s+">");
	}
	public static void ok(){
		pl("\nOK");
	}
	
	public static void done(){
		pl("DONE");
	}
	
	public static void nok(String s){
		pl("Elemento nao entedido: "+s);
	}
	
	public static void p(String s){
		System.out.print(s);
	}
	public static void p(double d){
		String s=""+d;
		if(s.endsWith(".0")){
			s=s.substring(0, s.length()-2);
		}
		System.out.print(s);
	}
	
	public static void pl(String s){
		System.out.println(s);
	}
	public static void pl(double d){
		System.out.println(""+d);
	}
	
	public static String r() throws Exception{
		BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
		String s = rd.readLine();
		//s=s.toUpperCase();
		return(s);
	}
}
