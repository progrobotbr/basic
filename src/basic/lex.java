package basic;

import java.util.ArrayList;
import java.util.HashMap;

public class lex {
	
	//palavras com espaco... 10 print a * 1 , a * 2
	String[] mwords;
	int pos;
	int tam;
	
	public static char COMMAND  ='C';
	public static char NUM      ='N';
	public static char STRING   ='S';
	public static char VARIABLE ='V';
	public static char FUNCAO   ='F';
	public static char DEFFUNCAO ='D';
	public static char MATRIZ   ='M';
	public static char NOTDEFIN = ' ';
	public static char FIMDELINHA = 'E';
	
	public static String   Token ="";
	public static char typeToken =' ';
	public static HashMap<String,String> commands = null;
	public static HashMap<String,String> functions = null;

	public lex(){
		if(commands==null){
		  commands = new HashMap<String,String>();
		  commands.put("PRINT", "");
		  commands.put("RUN", "");
		  commands.put("LIST", "");
		  commands.put("EXIT", "");
		  commands.put("NEW", "");
		  commands.put("IF", "");
		  commands.put("GOTO", "");
		  commands.put("FOR", "");
		  commands.put("TO", "");
		  commands.put("STEP", "");
		  commands.put("NEXT", "");
		  commands.put("STEP", "");
		  commands.put("STOP", "");
		  commands.put("END", "");
		  commands.put("GOSUB", "");
		  commands.put("RETURN", "");
		  commands.put("REM", "");
		  commands.put("INPUT", "");
		  commands.put("LET", "");
		  commands.put("DEF", "");
		  commands.put("CONT", "");
		  commands.put("STEP", "");
		  commands.put("NOT", "");
		  commands.put("DIM", "");
		  commands.put("DATA", "");
		  commands.put("READ", "");
		  commands.put("CLEAR", "");
		  commands.put("DIR", "");
		  commands.put("LOAD", "");
		  commands.put("SAVE", "");
		  commands.put("?", "");
		  functions = new HashMap<String,String>();
		  functions.put("INT", "");
		  functions.put("SQR", "");
		  functions.put("MID$", "");
		  functions.put("LEFT$", "");
		  functions.put("RIGHT$", "");
		  functions.put("LEN", "");
		  functions.put("ABS", "");
		  functions.put("CINT", "");
		  functions.put("CHR$", "");
		  functions.put("ASC", "");
		  functions.put("TAB", "");
		}
	}
	
    public void match(String s) throws Exception{
    	if(Token.equals(s)){
          nextToken();
          return;
    	}
    	throw new Exception("Comando esperado ["+s+"] e veio ["+Token+"]");
    }
    
    public boolean nextToken(){
    	Token = "";
    	typeToken = lex.FIMDELINHA;
    	if(pos<tam){
    		Token = mwords[pos];
    		classify();
    		pos++;
    		return(true);
    	}
    	return(false);
    }
    
    public int getPosition(){
    	return pos;
    }
    public void setPosition(int idx){
    	pos=idx;
    	//pos--;
    	nextToken();
    }
    
    public void classify(){
    	boolean b;
    	byte palavra[];
    	try{
    		Double.parseDouble(Token);
    		b=true;
    	}catch(Exception ex){
    		b=false;
    	}
    	if(b==true){
    		typeToken=NUM;
    		return;
    	}
    	if(Token.startsWith("\"")){
    		Token = Token.substring(1,Token.length()-1);
    		typeToken=STRING;
    		return;
    	}
    	palavra = Token.getBytes();
    	
    	if(commands.containsKey(Token.toUpperCase())){
    		typeToken=COMMAND;
    		Token=Token.toUpperCase();
    		return;
    	}
    	
    	if(functions.containsKey(Token.toUpperCase())){
    		typeToken=FUNCAO;
    		Token=Token.toUpperCase();
    		return;
    	}
    	
    	if(basic.mdef.containsKey(Token.toUpperCase())){
    		typeToken=DEFFUNCAO;
    		Token=Token.toUpperCase();
    		return;
    	}
    	
    	if((palavra[0]>=65 && palavra[0] <=90) || (palavra[0]>=97 && palavra[0] <=122)){
    		typeToken=VARIABLE;
    		Token=Token.toUpperCase();
    		return;
    	}
    	
    	typeToken = NOTDEFIN;
    	return;
    }
    
	public void parselinhainit(boolean pula, String plinha) throws Exception{
		//mwords = plinha.split(" ");
		mwords = pp2(plinha);
		tam = mwords.length;
		pos = 0;
		if(pula==true){
			nextToken();
		}
		//if(tam>0){
			//pos++;
			//return(mwords[0]);
		//}
		//return(null);
	}
	
	public String[] pp2(String plinha) throws Exception{
		int i, t;
		byte bl[] = plinha.getBytes();
		String s="";
		ArrayList<String> al = new ArrayList<String>();
		t=bl.length-1;
		for(i=0;i<=t;i++){
			
			if(bl[i]==' '){
				continue;
			}
			
			//STRING
			if(bl[i]=='\"'){
				s = s + new String(bl,i,1);
				i++;
				for(; i<=t; i++){
					s = s + new String(bl,i,1);
					if(bl[i]=='\"'){
						break;
					}
					if(i==t){
						if(i==t){
							throw new Exception("faltou \" no fim da string");
						}
					}
				}
				al.add(s);
				s="";
				continue;
			}
			
			//PALAVRA OU PALAVRA$
			if((bl[i]>='a' && bl[i] <='z') || (bl[i]>='A' && bl[i] <='Z')){
				for(; i<=t; i++){
					if((bl[i]>='a' && bl[i] <='z') || (bl[i]>='A' && bl[i] <='Z') || bl[i]=='$' ||
					   (bl[i]>='0' && bl[i] <='9')	){
						s = s + new String(bl,i,1);
					}else{
						break;
					}
				}
				i--;
				if(!s.equals("")){
					al.add(s);
					s="";
					continue;
				}
			}
			
	    	if(bl[i]=='<'){
	    		s = s + new String(bl,i,1);
	    		i++;
	    		for(; i<=t; i++){
	    			if(bl[i]==' ') continue;
	    			if(bl[i]=='>'){
	    				s = s + new String(bl,i,1);
	    				break;
	    			}else if(bl[i]=='='){
	    				s = s + new String(bl,i,1);
	    				break;
	    			}else{
	    				i--;
	    				break;
	    			}
	    		}
	    		//i--;
	    		al.add(s);
				s="";
				continue;
			}else if(bl[i]=='>'){
				s = s + new String(bl,i,1);
	    		i++;
	    		for(; i<=t; i++){
	    			if(bl[i]==' ') continue;
	    			if(bl[i]=='='){
	    				s = s + new String(bl,i,1);
	    				break;
	    			}else{
	    				i--;
	    				break;
	    			}
	    		}
	    		//i--;
	    		al.add(s);
				s="";
				continue;
			}else if(bl[i]=='?' || bl[i]==',' || bl[i]==';' || bl[i]=='=' || bl[i]=='(' ||  bl[i]==')' || bl[i]=='*' || bl[i]=='/' || bl[i]=='-' || bl[i]=='+'){
				s = s + new String(bl,i,1);
				al.add(s);
				s="";
				continue;
			//NUMERO	
			}else if((bl[i]>='0' && bl[i]<='9')){
				s = s + new String(bl,i,1);
	    		i++;
	    		for(; i<=t; i++){
	    			if((bl[i]>='0' && bl[i]<='9') || bl[i]=='.'){
	    				s = s + new String(bl,i,1);
	    			}else{
	    				break;
	    			}
	    		}
	    		i--;
	    		al.add(s);
				s="";
				continue;
			}else if(bl[i]==':'){
				s = s + new String(bl,i,1);
				al.add(s);
				s="";
				continue;
			}else if(bl[i]=='.' || bl[i]=='\''){ //Caracteres estranhos 
				s = s + new String(bl,i,1);
				al.add(s);
				s="";
				continue;
			}
	    	
	    	s = s + new String(bl,i,1);
	    	throw new Exception("Elemento nao entendido " + s);
            
		}
		
		t = al.size();
		if(t>0){
			mwords = new String[t];
			i=0;
			for(String ss: al){
				mwords[i] = ss;
				i++;
			}
		}
		return(mwords);
	}
		
}
