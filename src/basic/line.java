package basic;

public class line {
	
	int linenumber;
	line nextline;
	String line=null;
	
	public static line createLine(String pnum, String s){
		line n = new line();
		int num=0;
		try{
    		num = Integer.parseInt(lex.Token);
    		
    	}catch(Exception ex){
    	}
		n.linenumber = num;
		n.line = s;
		return n;
	}

}
