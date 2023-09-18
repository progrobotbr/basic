package basic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Stack;

public class parse {
	
	public String  linha; //linha de execu��o
	public lex     lexn;
	public boolean brun;
	
	public parse(String s, boolean b) throws Exception{
		brun = !b;
		linha=s;
		lexn = new lex();
		lexn.parselinhainit(b, linha);
	}
	public boolean Stmt() throws Exception{
		
		boolean bEnd=false;
		boolean bErr=false;
		
		do{
	
			lexn.nextToken();
		
			if(lex.typeToken == lex.VARIABLE){
				Assign();
			
			}else if(lex.typeToken == lex.NUM){
				basic.prg.addline(lex.Token, linha);
				return(bEnd);
			
			}else if(lex.typeToken == lex.COMMAND){
			
				//Comandos de linha
				if(lex.Token.equals("RUN")){
					this.run();
			
				}else if(lex.Token.equals("DIR")){
					this.cdir();
					
				}else if(lex.Token.equals("LOAD")){
					this.cload();
				
				}else if(lex.Token.equals("SAVE")){
					this.csave();
					
				}else if(lex.Token.equals("EXIT")){
					bEnd=true;
			
				}else if(lex.Token.equals("NEW")){
					lexn.nextToken();
					basic.newprg();
				
				}else if(lex.Token.equals("LIST")){
					this.list();
				
				}else if(lex.Token.equals("CONT")){
					lexn.nextToken();
					this.bcont();
			
				}else if(lex.Token.equals("STEP")){
					lexn.nextToken();
					this.bstep();
			
				}else if(lex.Token.equals("CLEAR")){
					this.bclear();
				
				//comandos da linguagem
				}else if(lex.Token.equals("PRINT") || lex.Token.equals("?")){
					this.print();

				}else if(lex.Token.equals("DIM")){
					this.dim();
				
				}else if(lex.Token.equals("LET")){
					lexn.nextToken();
					Assign();
				
				}else if(lex.Token.equals("IF")){
					this.bif();
				
				}else if(lex.Token.equals("FOR")){
					this.bfor();
				
				}else if(lex.Token.equals("NEXT")){
					this.next();
				
				}else if(lex.Token.equals("END")){
					basic.hasJump=true;
					basic.lineexec=null;
					lexn.nextToken();
			
				}else if(lex.Token.equals("STOP")){
					basic.stop=true;
					lexn.nextToken();
					//_.halt();
				
				}else if(lex.Token.equals("GOTO")){
					var lvar;
					lexn.nextToken();
					lvar = Bool();
					this.bgoto(lvar);
			
				}else if(lex.Token.equals("GOSUB")){
					var lvar;
					lexn.nextToken();
					lvar = Bool();
					this.bgosub(lvar);
				
				}else if(lex.Token.equals("RETURN")){
					this.breturn();
				
				}else if(lex.Token.equals("DATA")){
					this.bdata();
				
				}else if(lex.Token.equals("READ")){
					this.bread();	
			
				}else if(lex.Token.equals("REM")){
					lex.typeToken=lex.FIMDELINHA;
					lex.Token="";
					return(false);
				
				}else if(lex.Token.equals("DEF")){
					this.bdef();
			
				}else if(lex.Token.equals("INPUT")){
					this.input();
				}
			
				if(lex.typeToken!=lex.FIMDELINHA && !lex.Token.equals(":")){
				bErr=true;
				}
			
			}else{
				bErr=true;
			}
	
		}while(lex.Token.equals(":"));
		
		if(bErr==true){
			_.nok(lex.Token);
		}else{
			if(brun==true){
				_.ok();
			}
		}
		return(bEnd);
		
	}
	
	//execu��o
	public void run() throws Exception{
		program prg = basic.prg;
	    basic.lineexec = prg.firstline;
	    lexn.nextToken();
	    
	    this.runbase(false);
	}
	
	private void runbase(boolean pstep) throws Exception{
		parse p;
		if(basic.lineexec==null){
			_.empty();
			return;
		}
		basic.stop=false;
		while(basic.lineexec!=null && basic.stop==false){
			basic.hasJump=false;
			p=new parse(basic.lineexec.line,true);
			p.Stmt();
			if(basic.lineexec!=null && basic.hasJump==false){
				basic.lineexec = basic.lineexec.nextline;
			}
			if(basic.stop==true || pstep==true){
				_.halt(basic.lineexec);
				break;
			}
		}
		
		if(basic.lineexec==null){
			//_.ok();
			return;
		}
		
	}

	//debug
	public void bstep() throws Exception{
		if(passCommand()){
			return;
		}
		this.runbase(true);
	}
	
	public void bcont() throws Exception{
		if(passCommand()){
			return;
		}
		this.runbase(false);
	}
	
    //comandos de linha
	public void list(){
		program prg = basic.prg;
		line l;
		lexn.nextToken();
		l=prg.firstline;
		while(l!=null){
			_.pl(l.line);
			l=l.nextline;
		}
	}

    public void bclear(){
    	boolean b=false;
    	String s;
    	lexn.nextToken();
    	
    	while(lex.typeToken!=lex.FIMDELINHA){
    		b=true;
        	s=lex.Token;
    		if(lex.typeToken==lex.VARIABLE){
    			if(basic.mvar.containsKey(s)){
    				basic.mvar.remove(s);
    			}
    		}
    		lexn.nextToken();
    	}
    	if(b==false){
    		basic.initVar();
    	}
    }
	
	//evita a execu��o do comando dentro de uma execu��o de programa
	private boolean passCommand(){
		int i;
		if(linha!=null){
			byte b[] = linha.getBytes();
			for(i=0; i<b.length; i++){
				if(b[i]==' '){
					continue;
				}
				if(b[i]>='0' && b[i]<='9'){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	
    public void bread() throws Exception{
    	lexn.nextToken();
    	this.breadbase();
    	lexn.nextToken();
    	while(lex.Token.equals(",")){
    		lexn.nextToken();
    		this.breadbase();
    		lexn.nextToken();
    	}
    }
    
    private void breadbase() throws Exception{
    	if(lex.typeToken!=lex.VARIABLE){
    		throw new Exception("Esperado elemento nome variavel");
    	}
    	if(basic.idatapoint>=basic.data.size()){
    		throw new Exception("Estouro da lista de Data");
    	}
    	var v = basic.data.get(basic.idatapoint);
    	this.putValue(lex.Token, v);
    	basic.idatapoint++;
    }
    
    public void bdata() throws Exception{
    	lexn.nextToken();
    	this.bdatabase();
    	lexn.nextToken();
    	while(lex.Token.equals(",")){
    		lexn.nextToken();
    		this.bdatabase();
    		lexn.nextToken();
    	}
    }
    
    public void bdatabase() throws Exception{
    	if(lex.typeToken!=lex.STRING && lex.typeToken!=lex.VARIABLE && lex.typeToken!=lex.NUM){
    		throw new Exception("Esperado elemento na definicao do data");
    	}
    	ArrayList<var> al=basic.data;
    	var v=new var();
    	if(lex.typeToken==lex.STRING){
    		v.tipo=var.STRING;
    		v.s=lex.Token;
    	}else if(lex.typeToken==lex.NUM){
    		v.tipo=var.DOUBLE;
    		v.d=Float.parseFloat(lex.Token);
    	}else if(lex.typeToken==lex.VARIABLE){
    		v=getVar(lex.Token);
    		if(v==null){
    			throw new Exception("Variavel nao encontrada");
    		}
    	}
    	al.add(v);
    }
    
    //sintaxe=> def vv1(x)=x*x+1
	public void bdef() throws Exception{
		String sf, sfname, spar1;
		//var lvar, pvar;
		lexn.nextToken();
		if(lex.typeToken!=lex.DEFFUNCAO){
			if(lex.typeToken!=lex.VARIABLE){
				throw new Exception("Identificador invalido");
			}
		}
		
		sfname = lex.Token;
		sf=sfname;
		lexn.nextToken();
		lexn.match("(");
		if(lex.typeToken!=lex.VARIABLE){
			throw new Exception("Esperado variavel");
		}
		spar1 = lex.Token;
		lexn.nextToken();
		lexn.match(")");
		sf = sfname+"("+spar1+")";
		while(lex.typeToken!=lex.FIMDELINHA && !lex.Token.equals(":")){
			sf+= " "+lex.Token;
			lexn.nextToken();
		}
		basic.mdef.put(sfname, sf);		
	}
	
	public var bdefrun() throws Exception{
		String sfun = lex.Token;
		String sline = basic.mdef.get(sfun);
		lex llex;
		lexn.nextToken();
		lexn.match("(");
		//String spar1 = lexn.Token;
		var v1 = new var();
		if(lex.typeToken==lex.STRING){
			v1.tipo=var.STRING;
			v1.s = lex.Token;		
		}else if(lex.typeToken==lex.NUM){
			v1.tipo=var.DOUBLE;
			v1.d = Float.parseFloat(lex.Token);
		}else if(lex.typeToken==lex.VARIABLE){
			v1 = getVar(lex.Token);
			if(v1==null){
				throw new Exception("Variavel nao definida");
			}
		}
		lexn.nextToken();
		int ipos = lexn.getPosition()-1; // ordem e o -1 s�o importantes, tem que ficar antes do match(')')
		lexn.match(")");
		parse p = new parse(sline,true);
		llex = p.lexn;
		llex.nextToken();
		llex.match("(");
		String sold = lex.Token; //salva valor anterior caso exista
		var vold = getVar(sold);
		putValue(lex.Token,v1);
		llex.nextToken();
		llex.match(")");
		llex.match("=");
		var vres = p.Bool();
		//putValue(sfun,vres);
		lexn.setPosition(ipos);
		lexn.nextToken();
		if(vold!=null);
		putValue(sold, vold);
		return(vres);
	}

	//dim x(10) 'somente uma dimens�o
	public void dim() throws Exception{
		int idim;
		String svar;String sdim="";
		var lvar, nvar;
		lexn.nextToken();
		if(lex.typeToken!=lex.VARIABLE){
			throw new Exception("Identificador invalido");
		}
		svar = lex.Token;
		lexn.nextToken();
		lexn.match("(");
		lvar = Bool();
		if(lvar.tipo!=var.DOUBLE){
			throw new Exception("Definicacao da matriz deve ser numerica");
		}
		idim=(int)lvar.d;
		sdim=""+(int)lvar.d;
		while(lex.Token.equals(",")){
			lexn.nextToken();
			lvar = Bool();
			if(lvar.tipo!=var.DOUBLE){
				throw new Exception("Definicacao da matriz deve ser numerica");
			}
			idim*=(int)lvar.d;
			sdim+=","+(int)lvar.d;
		}
		lexn.match(")");
		nvar  = getVar(svar);
		if(nvar!=null){
			throw new Exception("Variavel ja definida");
		}
		nvar=new var();
		nvar.is_matriz = 'X';
		nvar.sdimensao=sdim;
		nvar.m = new var[idim];
    	
    	putValue(svar, nvar);
	}
	
	public void print() throws Exception{
		boolean besp;
		lexn.nextToken();
		var lvar  = Bool();
		if(lex.Token.equals(";")){
			besp=false;
		}else{
			besp=true;
		}
		this.printbase(lvar, besp);
		while(lex.Token.equals(",") || lex.Token.equals(";")){
			lexn.nextToken();
			if(lex.typeToken==lex.FIMDELINHA){
				_.pl("");
				return;
			}
			lvar  = Bool();
			if(lex.Token.equals(";")){
				besp=false;
			}else{
				besp=true;
			}
			this.printbase(lvar, besp);
		}
		_.pl("");
	}
	
	private void printbase(var v, boolean pesp){
		if(v==null){
			return;
		}
		if(v.tipo==var.STRING){
			_.p(v.s);
		}else if(v.tipo==var.DOUBLE){
			_.p(v.d);
		}
		if(pesp==true){
			_.p("      ");
		}
	}
	
	private void input() throws Exception{
		lexn.nextToken();
		if(lex.typeToken == lex.STRING){
			var lvar = new var();
			lvar.tipo = var.STRING;
			lvar.s = lex.Token;
			this.printbase(lvar, true);
			lexn.nextToken();
		}
		if(lex.typeToken!=lex.VARIABLE){
			throw new Exception("Esperada vari�vel");
		}
		var lvar = new var();
		lvar.tipo = var.STRING;
		putValue(lex.Token, lvar);
		lexn.nextToken();
		
		this.inputbase(lvar);
	}
	
	private void inputbase(var pvar) throws Exception{
		String s=_.r();
		pvar.tipo = var.STRING;
		pvar.s = s;
	}
	
	public void bif() throws Exception{
		int i;
		lexn.nextToken();
		var lvar = Bool();
		//lexn.nextToken();
		if(lex.Token.equals("THEN")){
			lexn.nextToken();
			i=lexn.getPosition();
			if(lex.typeToken==lex.NUM){
				var lgoto = getTerm();
				this.bifbase(lvar, lgoto);
			}else{
				if(lvar.d>0){
					i-=2;
					lexn.setPosition(i);
					this.Stmt();
				}else{
					while(lex.typeToken!=lex.FIMDELINHA && !lex.Token.equals(":")){
						lexn.nextToken();
					}
				}
			}
		}else if(lex.Token.equals("GOTO")){
			lexn.nextToken();
			if(lex.typeToken!=lex.NUM && lex.typeToken!=lex.VARIABLE){
				throw new Exception("Esperado tipo numerico");
			}
			var lgoto = getTerm();
			this.bifbase(lvar, lgoto);
		}else{
			throw new Exception("Esperado comando THEN ou GOTO");
		}
		/*
		if(lex.Token.equals("THEN") || lex.Token.equals("GOTO")){
			lexn.nextToken();
			if(lex.typeToken!=lex.NUM && lex.typeToken!=lex.VARIABLE){
				throw new Exception("Esperado tipo numerico");
			}
			var lgoto = getTerm();
			this.bifbase(lvar, lgoto);
		}else{
			throw new Exception("Esperado comando THEN ou GOTO");
		}
		*/
	}
	
	private void bifbase(var vbool, var vgoto) throws Exception{
		line lline;
		int ilinha;
		if(vbool.d>0){
			ilinha = (int)vgoto.d;
			lline = basic.prg.findline(ilinha);
			if(lline==null){
				throw new Exception("Linha n�o encontrada ["+ilinha+"]");
			}
			basic.lineexec = lline;
			basic.hasJump=true;
			lex.typeToken=lex.FIMDELINHA;
			lex.Token="";
		}
	}
	
	public void bgoto(var vgoto) throws Exception{
		int ilinha;
		line lline;
		ilinha = (int)vgoto.d;
		lline = basic.prg.findline(ilinha);
		if(lline==null){
			throw new Exception("Linha n�o encontrada ["+ilinha+"]");
		}
		basic.stack.clear();
		basic.lineexec = lline;
		basic.hasJump=true;
		lex.typeToken=lex.FIMDELINHA;
		lex.Token="";
	}
	
	public void bgosub(var vgoto) throws Exception{
		int ilinha;
		line lline;
		ilinha = (int)vgoto.d;
		lline = basic.prg.findline(ilinha);
		
		int idx=lexn.getPosition();
		if(lex.Token.equals(":")){
			idx--;
		}
		
		if(lline==null){
			throw new Exception("Linha n�o encontrada ["+ilinha+"]");
		}
		ilinha = basic.lineexec.linenumber;
		//basic.stack.clear();
		basic.lineexec = lline;
		basic.hasJump=true;
		
		var vidx = new var();
		vidx.d = idx;
		vidx.tipo = var.DOUBLE;
		
		this.push(ilinha, "GOSUB", null, null, null, vidx);
	}
	
	public void bfor() throws Exception{
		var lvar, lvarstep=null, lvarto;
		lexn.nextToken();
		if(lex.typeToken!=lex.VARIABLE){
			throw new Exception("Esperado variavel no lugar de ["+lex.Token+"]");
		}
	
		lvar = this.Assign();
	
		if(!lex.Token.equals("TO")){
			throw new Exception("Esperado comando TO");
		}
		lexn.nextToken();
		lvarto = Bool();
		if(lex.Token.equals("STEP")){
			lexn.nextToken();
			//if(lex.typeToken!=lex.NUM && lex.typeToken!=lex.VARIABLE){
				//throw new Exception("Esperado variavel ou numero");
			//}
			lvarstep = Bool();
		}
	
		this.bforbase(lvar, lvarstep, lvarto);
	}
	
	public void bforbase(var pvar, var pstep, var pto) throws Exception{
		//boolean bfind;
		int cfor=0; //, cnext;
		//String sline;
		//
		//pega posi��o lexica na linha
		//para tratamento de v�rios comandos na mesma linha
		//
		int idx=lexn.getPosition();
		if(lex.Token.equals(":")){
			idx--;
		}
		if(pstep==null){
			pstep = new var();
			pstep.tipo = var.DOUBLE;
			pstep.d = 1;
		}
		if(pstep.d == 0){
			pstep.d = 1;
		}
		//line l;
		if( ( pvar.d > pto.d && pstep.d > 0 ) || ( pvar.d < pto.d && pstep.d < 0 )){
			//
			//procura next para salto no seguinte contexto
			//  quando a contagem n�o for poss�vel
			//
			do{
				while(lex.typeToken!=lex.FIMDELINHA){
					if(lex.typeToken == lex.COMMAND && lex.Token.equals("NEXT")){
						cfor--;
						if(cfor<0){
							lexn.nextToken();
							return;
						}
					}else if(lex.typeToken == lex.COMMAND && lex.Token.equals("FOR")){
						cfor++;
					}
					lexn.nextToken();
				}
				
				if(basic.lineexec==null){
					break;
				}
				basic.lineexec = basic.lineexec.nextline;
				
				lexn = new lex();
				lexn.parselinhainit(true, basic.lineexec.line);	
				lexn.nextToken();
				
			}while(basic.lineexec!=null);
			
			if(basic.lineexec==null){
				throw new Exception("Comando Next nao encontrado");
			}
		}else{
			var vidx = new var();
			vidx.d = idx;
			vidx.tipo = var.DOUBLE;
			int iline;
			if(basic.lineexec==null){
				iline=-1;
			}else{
				iline=basic.lineexec.linenumber;
			}
			this.push(iline, "FOR", pvar, pstep, pto, vidx);
		}
	}
	
	public void next() throws Exception{
		boolean b=false;
		bstackobj so;
		
		lexn.nextToken();
		if(basic.stack.size()==0){
			throw new Exception("Comando NEXT nao esperado");
		}
		so = basic.stack.pop();
				
		if(!so.command.equals("FOR")){
			throw new Exception("Comando NEXT nao esperado");
		}else{
			so.var1.d = so.var1.d + so.var2.d;
			if(so.var2.d<0){
				if(so.var1.d>=so.var3.d){
					b=true;
				}
			}else{
				if(so.var1.d<=so.var3.d){
					b=true;
				}
			}
		}
		if(b==true){
			if(so.linenumber!=-1){
				basic.lineexec = basic.prg.findline(so.linenumber);
				//basic.lineexec = basic.lineexec.nextline;
				//basic.hasJump = true;
				lexn = new lex();
				lexn.parselinhainit(true, basic.lineexec.line);
			}
		    lexn.setPosition((int)so.var4.d);
			basic.stack.push(so);
		}
		//throw new Exception("N�o encontrado comando FOR");
	}
	
	public void breturn() throws Exception{
		bstackobj so;
		
		lexn.nextToken();
		if(basic.stack.size()==0){
			return;
		}
		so=basic.stack.pop();
		if(so==null){
			throw new Exception("Comando RETURN nao esperado");
		}
		if(!so.command.equals("GOSUB")){
			throw new Exception("Comando RETURN nao esperado");
		}else{
			//procura na linha
			while(lex.typeToken!=lex.FIMDELINHA){
				if(lex.typeToken == lex.COMMAND && lex.Token.equals("RETURN")){
					return;
				}
				lexn.nextToken();
			}
			//n�o achou, faz o jump
			basic.lineexec = basic.prg.findline(so.linenumber);
			//basic.lineexec = basic.lineexec.nextline;
			//basic.hasJump = true;
		}
	}
	
	public var Assign() throws Exception{
		boolean bm=false;
		int x=0;
		var oldvar, newvar, lvar, lidx, lvarm[];
		String svar = lex.Token;
		String sdim="";
		lexn.nextToken();
		if(lex.Token.equals("(")){
			lexn.nextToken();
			lidx = Bool();
			if(lidx.tipo!=var.DOUBLE){
				throw new Exception("Identificador invalido");
			}
			//x += (int)lidx.d;
			sdim+=(int)lidx.d;
			while(lex.Token.equals(",")){
				lexn.nextToken();
				lidx = Bool();
				if(lidx.tipo!=var.DOUBLE){
					throw new Exception("Identificador invalido");
				}
				//x *= (int)lidx.d;
				sdim+=","+(int)lidx.d;
			}
			lexn.match(")");
			bm=true;
		}
		oldvar = getVar(svar);
		lexn.match("=");
		lvar = Bool();
		if(oldvar != null){
			newvar = oldvar;
		}else{
			newvar = lvar;
		}
		
	    if(bm==true){
	    	x=idxcalc(newvar.sdimensao,sdim);    	
	    	newvar.is_matriz = 'X';
	    	if(newvar.m==null ){
	    	  newvar.m = new var[x+1];
	    	}else{
	    		if(x >= newvar.m.length){
	    			lvarm = newvar.m;
	    			newvar.m = new var[x+1];
	    			for(int i=0;i<lvarm.length;i++){
	    				newvar.m[i] = lvarm[i];
	    			}
	    		}
	    	}
	    	newvar.m[x] = lvar;
	    }else{
	    	if(newvar.is_matriz == 'X' && lvar.is_matriz != 'X'){
	    		throw new Exception("Variavel � matriz");
	    	}
	    	newvar = lvar;
	    }
		putValue(svar, newvar);
		
		return( lvar );
	}
	
	public var Bool() throws Exception{
		var ret = calcOR();
		return(ret);
	}
	
	public var calcOR() throws Exception{
		var ret=null, cm1=null;
		var cm2 = calcAND();
		String sinal = lex.Token;
		ret = cm2;
				
		while(sinal.equals("OR") ){
			lexn.nextToken();
			cm1 = calcAND();
			if(cm1.d > 1 || cm2.d >1) {
				ret.d = 1;
			}else{
				ret.d = 0;
			}
			sinal=lex.Token;
		}
		return(ret);
	}
	
	public var calcAND() throws Exception{
		var ret=null, cm1=null;
		var cm2 = calcEQ();
		String sinal = lex.Token;
		ret = cm2;
				
		while(sinal.equals("AND") ){
			lexn.nextToken();
			cm1 = calcEQ();
			if(cm1.d == cm2.d) {
				ret.d = 1;
			}else{
				ret.d = 0;
			}
			sinal=lex.Token;
		}
		return(ret);
	}
	
	public var calcEQ() throws Exception{
		var ret=null, cm1=null;
		var cm2 = calcMais();
		String sinal = lex.Token;
		ret = cm2;
				
		while(sinal.equals("=") || sinal.equals("<") || sinal.equals(">") || sinal.equals("<>") ||  sinal.equals("<=") || sinal.equals(">=")){
			lexn.nextToken();
			ret = new var();
			ret.tipo  = var.DOUBLE;
			cm1 = calcMais();
			if(sinal.equals("=")){
				if(cm1.tipo == var.STRING ) { if(cm1.s.equals(cm2.s)) {  ret.d = 1; }else{ ret.d = 0; } } //compara��o de string
				else if(cm1.tipo == var.DOUBLE ) { if(cm1.d == cm2.d) {  ret.d = 1; }else{ ret.d = 0; } }
				else throw new Exception("Elemento nao entendido");
				
			}else if(sinal.equals("<")){
				if(cm1.tipo == var.DOUBLE ) { if(cm2.d < cm1.d) { ret.d = 1; }else{ ret.d = 0; } } else { ret.d = 0; }
				
			}else if(sinal.equals(">")){
				if(cm1.tipo == var.DOUBLE ) { if(cm2.d > cm1.d) { ret.d = 1; }else{ ret.d = 0; } }  else { ret.d = 0; }
				
			}else if(sinal.equals("<>")){
				if(cm1.tipo == var.DOUBLE ) { if(cm2.d != cm1.d) { ret.d = 1; }else{ ret.d = 0; } } 
				else if(cm1.tipo == var.STRING ) { if(!cm1.s.equals(cm2.s)) {  ret.d = 1; }else{ ret.d = 0; } }
				else { ret.d = 0; }
			
			}else if(sinal.equals("<=")){
				if(cm1.tipo == var.DOUBLE ) { if(cm2.d <= cm1.d) { ret.d = 1; }else{ ret.d = 0; } }  else { ret.d = 0; }
			
			}else if(sinal.equals(">=")){
				if(cm1.tipo == var.DOUBLE ) { if(cm2.d >= cm1.d) { ret.d = 1; }else{ ret.d = 0; } }  else { ret.d = 0; }
			
			}
			cm2 = ret;
			sinal=lex.Token;
		}
		return(ret);
	}
	
	
	public var calcMais() throws Exception{
		String s1, s2;
		var ret=null, cm1=null;
		var cm2 = calcVezes();
		String sinal = lex.Token;
		while(sinal.equals("+") || sinal.equals("-") ){
			lexn.nextToken();
			cm1 = calcVezes();
			if(sinal.equals("+")){
				if(cm2.tipo == var.STRING || cm1.tipo == var.STRING){ //Concatena String
					s1=s2="";
					if(cm2.tipo == var.STRING){ s2 = cm2.s; } else if(cm2.tipo == var.DOUBLE){ s2 = ""+cm2.d; }
					if(cm1.tipo == var.STRING){ s1 = cm1.s; } else if(cm1.tipo == var.DOUBLE){ s1 = ""+cm1.d; }
					cm2.tipo = var.STRING;
					cm2.s = s2 + s1;
				}else{
					cm2.d = cm2.d + cm1.d;
				}
			}else{
				cm2.d = cm2.d - cm1.d;
			}
			sinal=lex.Token;
		}
		ret = cm2;
		return(ret);
	}
	
	public var calcVezes() throws Exception{
		var ret=null, cm1=null;
		var cm2 = getTerm();
		String sinal = lex.Token;
		
		while(sinal.equals("*") || sinal.equals("/")){
			lexn.nextToken();
			cm1 = getTerm();
			if(sinal.equals("*")){
				cm2.d = cm2.d * cm1.d;
			}else{
				cm2.d = cm2.d / cm1.d;
			}
			sinal=lex.Token;
		}
		ret = cm2;
		return(ret);
	}
	
	public var getTerm() throws Exception{	
		var lvar = new var(), lvar2;
		
		if(lex.typeToken == lex.NUM){
			lvar.tipo = var.DOUBLE;
			lvar.d = num(lex.Token);
			lexn.nextToken();
			
		}else if(lex.typeToken == lex.STRING){
			lvar.tipo = var.STRING;
			lvar.s = lex.Token;
			lexn.nextToken();
			
		}else if(lex.typeToken == lex.VARIABLE){
			int x;
			lvar2 = getVar(lex.Token);
			if(lvar2==null){
				throw new Exception("Variavel ["+lex.Token+"] nao encontrada");
			}
			copyVar(lvar2, lvar);
			lexn.nextToken();
			
			if(lex.Token.equals("(")){
				
				if(lvar.is_matriz!='X'){
					throw new Exception("Nao e matriz"); //lvar.is_matriz = 'X';
				}
				lexn.nextToken();
				var varx = Bool();
				if(varx.tipo!=var.DOUBLE){
					throw new Exception("Identificador invalido");
				}
				String sdim = ""+(int) varx.d;
				while(lex.Token.equals(",")){
					lexn.nextToken();
					varx = Bool();
					if(varx.tipo!=var.DOUBLE){
						throw new Exception("Identificador invalido");
					}
					sdim+=","+(int)varx.d;
				}
				
				x=idxcalc(lvar.sdimensao, sdim);
				if(lvar.m!=null && x<lvar.m.length){
					lvar = lvar.m[x];
				}
				lexn.match(")");
	
			}
		
		}else if(lex.typeToken == lex.DEFFUNCAO){
			lvar = this.bdefrun();
			
		}else if(lex.typeToken == lex.FUNCAO){
			if(lex.Token.equals("INT")){
				lvar = this.bint();
				
			}else if(lex.Token.equals("MID$")){
				lvar = this.bmid();
				
			}else if(lex.Token.equals("LEFT$")){
				lvar = this.bleft();
				
			}else if(lex.Token.equals("RIGHT$")){
				lvar = this.bright();
				
			}else if(lex.Token.equals("LEN")){
				lvar = this.blen();
				
			}else if(lex.Token.equals("ABS")){
				lvar = this.babs();
				
			}else if(lex.Token.equals("CINT")){
				lvar = this.bcint();
			
			}else if(lex.Token.equals("CHR$")){
				lvar = this.bchr();
			
			}else if(lex.Token.equals("ASC")){
				lvar = this.basc();
				
			}else if(lex.Token.equals("TAB")){
				this.btab();
			
			//}else if(lex.Token.equals("VAL")){
				//lvar = this.bcal();
				
			}
		
		}else if( lex.Token.equals("(")){
			lexn.nextToken();
			lvar = Bool();
			lexn.match(")");
			
		}else if( lex.Token.equals("-")){
			lexn.nextToken();
			lvar = Bool();
			if(lvar.tipo!=var.DOUBLE){
				throw new Exception("Elemento nao numerico ["+lex.Token+"]");
			}
			lvar.d = lvar.d* -1;
			
		}else if( lex.Token.equals("+")){
			lexn.nextToken();
			lvar = Bool();
			if(lvar.tipo!=var.DOUBLE){
				throw new Exception("Elemento nao numerico ["+lex.Token+"]");
			}

		}else if( lex.Token.equals("NOT")){
			lexn.nextToken();
			lvar = Bool();
			if(lvar.tipo!=var.DOUBLE){
				throw new Exception("Elemento nao e funcao logica");
			}
			if(lvar.d>0){
				lvar.d = 0;
			}else{
				lvar.d = 1;
			}
			
		}else if( lex.typeToken == lex.FIMDELINHA){
			throw new Exception ("Esperado elemento");
		
		}else{
			throw new Exception("Elemento n�o entendido ["+lex.Token+"]");
		}
		
		return(lvar);
	}
	
	public double num(String s){
		double d;
		try{
			d = Double.parseDouble(s);
		}catch(Exception ex){
			d = 0;
		}
		return(d);
	}
	public boolean isNum(String s){
		boolean b;
		try{
			Double.parseDouble(s);
			b=true;
		}catch(Exception ex){
			b=false;
		}
		return(b);
	}
	
	public var getVar(String s){
		var lvar=null;
		if(basic.mvar.containsKey(s)){
			lvar = basic.mvar.get(s);
		}
		return(lvar);
	}
	
	private void copyVar(var or, var ds){
		ds.tipo = or.tipo;
		ds.d = or.d;
		ds.m = or.m;
		ds.s = or.s;
		ds.i = or.i;
		ds.is_matriz = or.is_matriz;
		ds.matrix_y = or.matrix_y;
		ds.matriz_x = or.matriz_x;
		ds.sdimensao = or.sdimensao;
	}
	
	private void putValue(String name, var value){
		basic.mvar.put(name, value);
	}
	
	private void push(int linha, String pCommand, var var1, var var2, var var3, var var4){
		Stack<bstackobj> st = basic.stack;
		bstackobj ob = new bstackobj();
		ob.linenumber=linha;
		ob.command = pCommand;
		ob.var1 = var1;
		ob.var2 = var2;
		ob.var3 = var3;
		ob.var4 = var4;
		st.push(ob);
	}
	
	//FUN��ES
	public var basc() throws Exception{
		//int i;
		byte b[];
		lexn.nextToken();
		lexn.match("(");
		var v = Bool();
		lexn.match(")");
		if(v.tipo != var.STRING){
			throw new Exception("Parametro deve ser um numero");
		}
		if(v.s.length()>1){
			throw new Exception("Deve ser passado apenas um caracter como parametro");
		}
		
		b = v.s.getBytes();
		
		v.tipo = var.DOUBLE;
		v.d=(double) b[0];
		v.s = null;
		return(v);
	}
	
	public var bchr() throws Exception{
		//int i;
		byte b[] = new byte[1];
		lexn.nextToken();
		lexn.match("(");
		var v = Bool();
		lexn.match(")");
		if(v.tipo != var.DOUBLE){
			throw new Exception("Parametro deve ser um numero");
		}
		if(v.d>255 || v.d<0){
			throw new Exception("Valor deve estar entre 0 e 255");
		}
		b[0] = (byte) v.d;
		v.tipo = var.STRING;
		v.d=0;
		v.s = new String(b);
		return(v);
	}
		
	public var bcint() throws Exception{
		//int i;
		lexn.nextToken();
		lexn.match("(");
		var v = Bool();
		lexn.match(")");
		if(v.tipo == var.STRING){
			try{
				v.d = Float.parseFloat(v.s);
				v.tipo = var.DOUBLE;
				v.s=null;
			}catch(Exception ex){
				throw new Exception("Nao � possivel converter ["+v.s+"] para numero");
			}
		}
		v.d = (double) (int)  v.d;
		return(v);
	}
	
	public void btab() throws Exception{
		//int i;
		lexn.nextToken();
		lexn.match("(");
		var v = Bool();
		lexn.match(")");
		if(v.tipo == var.STRING){
			try{
				v.d = Float.parseFloat(v.s);
				v.tipo = var.DOUBLE;
				v.s=null;
				
				for(int i=0; i<v.d; i++) {
					System.out.print(" ");
				}
					
			}catch(Exception ex){
				throw new Exception("Nao � possivel converter ["+v.s+"] para numero");
			}
		}
		return;
	}
	
	/*
	public var bval() throws Exception{
		//int i;
		lexn.nextToken();
		lexn.match("(");
		var v = Bool();
		lexn.match(")");
		if(v.tipo == var.STRING){
			try{
				v.d = Float.parseFloat(v.s);
				v.tipo = var.DOUBLE;
				v.s=null;
			}catch(Exception ex){
				throw new Exception("Nao � possivel converter ["+v.s+"] para numero");
			}
		}
		v.d = (double) (int)  v.d;
		return(v);
	}
	*/
	
	public var bint() throws Exception{
		lexn.nextToken();
		lexn.match("(");
		var v = Bool();
		lexn.match(")");
		if(v.tipo!=var.DOUBLE){
			throw new Exception("Tipo invalido");
		}
		v.d = (double) (int)  v.d;
		return(v);
	}
	
	public var bmid() throws Exception{
		int i,t;
		var ret;
		lexn.nextToken();
		lexn.match("(");
		var v1 = Bool();
		lexn.match(",");
		var v2 = Bool();
		lexn.match(",");
		var v3 = Bool();
		lexn.match(")");
		if(v1.tipo!=var.STRING){
			throw new Exception("Tipo 1 deve ser string");
		}
		if(v2.tipo!=var.DOUBLE || v3.tipo!=var.DOUBLE){
			throw new Exception("Tipo 2 e 3 devem ser double");
		}
		i = (int) v2.d;
		i--;
		t = (int) (v2.d + v3.d);
		t--;
		if(i<0){
			throw new Exception("Parametro 2 deve ser maior que 0");
		}
		if(i>v1.s.length()){
			ret=new var();
			ret.tipo=var.STRING;
			ret.s = "";
			return(ret);
		}
		if(t>v1.s.length()){
			t=v1.s.length();
		}
		ret=new var();
		ret.tipo=var.STRING;
		ret.s = v1.s.substring(i,t);
		return(ret);
	}
	
	public var bleft() throws Exception{
		int i,t;
		var ret;
		lexn.nextToken();
		lexn.match("(");
		var v1 = Bool();
		lexn.match(",");
		var v2 = Bool();
		lexn.match(")");
		if(v1.tipo!=var.STRING){
			throw new Exception("Tipo 1 deve ser string");
		}
		if(v2.tipo!=var.DOUBLE){
			throw new Exception("Tipo 2 devem ser double");
		}
		i = 0;
		t = (int) (v2.d);
		//t--;
		if(t<=0){
			throw new Exception("Parametro 2 deve ser maior que 0");
		}
		if(t>v1.s.length()){
			t=v1.s.length();
		}
		ret=new var();
		ret.tipo=var.STRING;
		ret.s = v1.s.substring(i,t);
		return(ret);
	}
	
	public var bright() throws Exception{
		int i,t;
		var ret;
		lexn.nextToken();
		lexn.match("(");
		var v1 = Bool();
		lexn.match(",");
		var v2 = Bool();
		lexn.match(")");
		if(v1.tipo!=var.STRING){
			throw new Exception("Tipo 1 deve ser string");
		}
		if(v2.tipo!=var.DOUBLE){
			throw new Exception("Tipo 2 devem ser double");
		}
		t = (int) (v2.d);
		t--;
		if(t<0){
			throw new Exception("Parametro 2 deve ser maior que 0");
		}
		
		if(t>v1.s.length()){
			t=v1.s.length();
		}
		
		i = v1.s.length() - t;
		i--;
		if(i<0){
			i=0;
		}
		t = v1.s.length() ;
		
		ret=new var();
		ret.tipo=var.STRING;
		ret.s = v1.s.substring(i,t);
		return(ret);
	}
	
	public var blen() throws Exception{
		var ret;
		lexn.nextToken();
		lexn.match("(");
		var v1 = Bool();
		lexn.match(")");
		if(v1.tipo!=var.STRING){
			throw new Exception("Tipo 1 deve ser string");
		}
		ret=new var();
		ret.tipo=var.DOUBLE;
		ret.d = v1.s.length();
		return(ret);
	}
	
	public var babs() throws Exception{
		var ret;
		lexn.nextToken();
		lexn.match("(");
		var v1 = Bool();
		lexn.match(")");
		if(v1.tipo!=var.DOUBLE){
			throw new Exception("Tipo 1 deve ser numero");
		}
		ret=new var();
		ret.tipo=var.DOUBLE;
		if(v1.d<0){
			ret.d = v1.d * 1;
		}else{
			ret.d = v1.d;
		}
		return(ret);
	}

	//
	// ARQUIVO
	//
	public void cdir() throws Exception{
		int i;
		String spath = System.getProperty("user.dir");
		lexn.nextToken();
		File fd = new File(spath);
		i=1;
		_.pl("Diretorio: "+spath);
		for(File ff: fd.listFiles()){
			if(ff.isFile() && ff.getName().endsWith(".BAS")){
				_.p(ff.getName()+"      ");
			}
			if(i>=4){
				_.pl("");
				i=0;
			}
			i++;
		}
		
	}
	
	public void cload() throws Exception{
		String nomefile, sline;
		parse p;
		lexn.nextToken(); 
		if(lex.typeToken!=lex.VARIABLE){
			throw new Exception("Informe o nome do arquivo");
		}
		nomefile = lex.Token;
		lexn.nextToken(); 
		File file = new File(nomefile+".BAS");
		BufferedReader input = new BufferedReader(new FileReader(file));
		basic.newprg();
		while((sline = input.readLine())!=null){
			 System.out.println(sline);
			 p=new parse(sline,false);
		  	 p.Stmt();
		}
		input.close();
		lex.Token="";
		lex.typeToken=lex.FIMDELINHA;
		
	}
	
	public void csave() throws Exception{
		String nomefile;
		line ln;
		lexn.nextToken(); 
		if(lex.typeToken!=lex.VARIABLE){
			throw new Exception("Informe o nome do arquivo");
		}
		nomefile = lex.Token;
		lexn.nextToken(); 
		File file = new File(nomefile+".BAS");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		
		ln=basic.prg.firstline;
		while(ln!=null){
			output.write(ln.line+"\n");
			ln = ln.nextline;
		}
		
		output.close();
	}
	
	//
	//calc index array
	// no excel
	// d e f
	// 2 2 5
	//
	// formula
	// =(D8-1)*($E$4*$F$4)+(E8-1)*$F$4+F8
	// entao:
	// col1-1*(pos+1*pos+2*pos+n)+col2-1*...
	private int idxcalc(String pdim, String parr) throws Exception{
		int i,i1,i2,i3,z,n1;
		
		n1=1;
		i3=0;
		
		if(pdim==null){
			pdim=parr;
		}
		String sdim[] = pdim.split(",");
		String sarr[] = parr.split(",");
		i1 = sdim.length;
		i2 = sarr.length;
		if(i1!=i2){
			throw new Exception("dimensao veio ["+parr+"] para ["+pdim+"]");
		}
       
		for(i=0;i<sdim.length-1;i++){
			i1=lctoi(sarr[i])-1;
			i2=1;
			for(z=n1;z<sdim.length;z++){
				i2*=lctoi(sdim[z]);
			}
			n1++;
			i3 += i1*i2;
		}
		i3+=lctoi(sarr[sarr.length-1]);
		return(i3);
	}
	
	private int lctoi(String s){
		int i=0;
		try{
			i=Integer.parseInt(s);
		}catch(Exception ex){}
		return(i);
	}
	
	
}
