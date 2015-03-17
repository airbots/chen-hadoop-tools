import java.io.*;
import java.lang.*;

public class genmd{
    public static int totalatom;
    public void generate(){
	int i=1;
	int j=1;
    	try{
    		FileWriter ffw=new FileWriter("./argon.xyz");
    		BufferedWriter bbw=new BufferedWriter(ffw);
		bbw.append("1    Ar   0.0   0.0   0.0   0.0   0.0   0.0   0.0   0.0   0.0\n");
		bbw.flush();
		i+=1;
    		while(i<=totalatom){
//			bbw.append((i+"   Ar  "+((i-1)*0.7)+"   0   0   0   0   0   0   0   0\n"));
//			bbw.flush();
//    			bbw.append(((i+1)+"   Ar  "+(-(i-1)*0.7)+"   0   0   0   0   0   0   0   0\n"));
//			bbw.flush();
//			bbw.append(((i+2)+"   Ar  0   "+((i-1)*0.7)+"   0   0   0   0   0   0   0\n"));
//			bbw.flush();
//			bbw.append(((i+3)+"   Ar  0   "+(-(i-1)*0.7)+"   0   0   0   0   0   0   0\n"));
//			bbw.flush();
//			bbw.append(((i+4)+"   Ar  0   0   "+((i-1)*0.7)+"   0   0   0   0   0   0\n"));
  //  			bbw.flush();
//			bbw.append(((i+5)+"   Ar  0   0   "+(-(i-1)*0.7)+"   0   0   0   0   0   0\n"));
//			bbw.flush();
                        bbw.append(((i)+"   Ar  "+(j*0.7)+"   "+(j*0.7)+"   "+(j*0.7)+"   0.0   0.0   0.0   0.0   0.0   0.0\n"));
                        bbw.flush();
                        bbw.append(((i+1)+"   Ar  "+(-j*0.7)+"   "+(j*0.7)+"   "+(j*0.7)+"   0.0   0.0   0.0   0.0   0.0   0.0\n"));
                        bbw.flush();
                        bbw.append(((i+2)+"   Ar  "+(j*0.7)+"   "+(-j*0.7)+"   "+(j*0.7)+"   0.0   0.0   0.0   0.0   0.0   0.0\n"));
                        bbw.flush();
                        bbw.append(((i+3)+"   Ar  "+(j*0.7)+"   "+(j*0.7)+"   "+(-j*0.7)+"   0.0   0.0   0.0   0.0   0.0   0.0\n"));
                        bbw.flush();
                        bbw.append(((i+4)+"   Ar  "+(-j*0.7)+"   "+(-j*0.7)+"   "+(j*0.7)+"   0.0   0.0   0.0   0.0   0.0   0.0\n"));
                        bbw.flush();
                        bbw.append(((i+5)+"   Ar  "+(j*0.7)+"   "+(-j*0.7)+"   "+(-j*0.7)+"   0.0   0.0   0.0   0.0   0.0   0.0\n"));
                        bbw.flush();
                        bbw.append(((i+6)+"   Ar  "+(-j*0.7)+"   "+(j*0.7)+"   "+(-j*0.7)+"   0.0   0.0   0.0   0.0   0.0   0.0\n"));
                        bbw.flush();
                        bbw.append(((i+7)+"   Ar  "+(-j*0.7)+"   "+(-j*0.7)+"   "+(-j*0.7)+"   0.0   0.0   0.0   0.0   0.0   0.0\n"));
                        bbw.flush();
			i=i+8;
			j++;
//			System.out.println(totalatom+"\t"+i+"\n");
		}
    	}catch(Exception e){
	}	
    }
    public static void main(String[] args){
	genmd mdsys=new genmd();
	for(int i=0;i<args.length;i++){
	    if ("-N".equals(args[i])){
		totalatom=Integer.parseInt(args[++i]);
	    }
    	}
	mdsys.generate();
   }
}
