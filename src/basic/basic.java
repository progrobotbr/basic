package basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class basic {
	
	public static HashMap<String,var> mvar = new HashMap<String,var>();
	public static Stack<bstackobj> stack = new Stack<bstackobj>();
	public static ArrayList<var>   data  = new ArrayList<var>();
	public static int idatapoint=0;
	public static HashMap<String,String> mdef = new HashMap<String,String>();
	public static program    prg = new program();
	public static boolean hasJump=false;
	public static boolean    stop=false;
	public static line   lineexec=null;
	
	public static void newprg(){
		prg   = new program();
		mvar  = new HashMap<String,var>();
		stack = new Stack<bstackobj>();
		data  = new ArrayList<var>();
		mdef  = new HashMap<String,String>();
		idatapoint = 0;
	}
	public static void initVar(){
		mvar  = new HashMap<String,var>();
	}
}
