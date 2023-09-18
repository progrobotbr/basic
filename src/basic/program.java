package basic;

public class program {
	
	line firstline=null;
	
	public void addline(String snum, String linha){
		int num = ctoi(snum);
		line n = createline(num, linha+"");
		line mline = findposput(n.linenumber);
		if(mline==null){
			firstline = n;
		}else{
			if(mline.nextline!=null && n.linenumber == mline.nextline.linenumber){
				n.nextline = mline.nextline.nextline;
				mline.nextline = n;
			}else if(firstline.linenumber == n.linenumber){
				n.nextline = firstline.nextline;
				firstline = n;
			}else if( n.linenumber < mline.linenumber){
				n.nextline = mline;
				firstline = n;
			}else{
				n.nextline = mline.nextline;
				mline.nextline = n;
			}
		}
	}
	
	public line findline(int plinenumber){
		
		line mline = firstline;
		while(mline!=null){
			if(mline.linenumber == plinenumber){
				return mline;
			}
			mline = mline.nextline;
		}
		return(null);
	}
	
    public line findposput(int plinenumber){
		
		line mline = firstline;
		line mold  = firstline;
		
		while(mline!=null){
			if(mline.linenumber >= plinenumber){
				return mold;
			}
			mold = mline;
			mline = mline.nextline;
		}
		return(mold);
	}
    
    public line createline(int plinenumber, String pline){
    	line mline = new line();
    	mline.linenumber = plinenumber;
    	mline.nextline=null;
    	mline.line = pline;
    	return(mline);
    }
    
    public int ctoi(String s){
    	int num=0;
    	try{
    		num = Integer.parseInt(s);
    	}catch(Exception ex){
    	}
    	return(num);
    }

}
