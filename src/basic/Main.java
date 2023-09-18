package basic;

public class Main {
  public static void main(String args[]) throws Exception{
	  boolean bEnd=false;
	  String s;
	  parse p;
	  _.ok();
	  
	  while(bEnd==false){
		  s=_.r();
		  if(s.trim().length()==0){
			  _.ok();
			  continue;
		  }
		  try{
			  p=new parse(s,false);
		  	  bEnd=p.Stmt();
		  }catch(Exception ex){
			  _.pl(ex.getMessage());
		  }
	  }
  }
}
