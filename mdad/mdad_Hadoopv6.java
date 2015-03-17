import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.ArrayList.*;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;

//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.mapreduce.*;
//import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.mapred.RawKeyValueIterator;
//import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapred.FileInputFormat;
//import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.*;

public class mdad_Hadoopv6{

	public static double Epslong=1.0;  //Energy well
	public static double delta=1.0;    //						
	public static double TimeStep=0.002;        //Time step for each simulation
	public static double mass=18.0;	    //Mass of atoms
	public static double Kai=2000;		//Boundary of the simulation box
	public static double Rc=10;			//Long range force cutoff radius
	public static int totalatom;				//Total number of atoms in simulating system
	public static int numIteration=1;	    
	public static String x,y,z;
	public static int exstatus;
	public static String moviePath=null;             //File of simulation movie in HDFS
	public static atomStorage atomstore=new atomStorage();
        public static int outputPeriod=0;
	public static int mdIteration;
// Mapper class

public static class Map extends MapReduceBase implements Mapper<LongWritable,Text,LongWritable,Text>{
	
	private double x_position;
	private double y_position;
	private double z_position;
	private double x_vel;
	private double y_vel;
	private double z_vel;
	private double x_acc;
	private double y_acc;
	private double z_acc;
	private double dist;
//        public static int mdIteration;

	//configure the setting of the hadoop job
    public void configure(JobConf job){
    	String filecontent=null;
    	int turnkeepper;
    	Path[] cachedFiles=new Path[0];
    	int atomnumber=0;
//		try{
	    mdad_Hadoopv6.mdIteration=Integer.parseInt(job.get("md.iteration"));
//		}catch(Exception num){
//		   System.out.println("Error in get the parameter from JobConf:"+job.get("md.iteration"));
//		}
//            if (mdad_Hadoopv6.mdIteration==0){
		FileSystem fs=null;
//		FileStatus fis=new fs.FileStatus();
//		Path movie=new Path(job.get("md.moviepath"));

		//First time DistributedCache read
		try{
    		cachedFiles=DistributedCache.getLocalCacheFiles(job);
		}catch(IOException ioe){
		System.err.println("Caught exception while getting cached files");
		}
		for (Path cachedFile:cachedFiles){
			try{
				BufferedReader fis=new BufferedReader(new FileReader(cachedFile.toString()));
				while((filecontent=fis.readLine())!=null){
					mdad_Hadoopv6.atomstore.atom1.add(filecontent);
//					mdad_Hadoopv6.atomstore.atom2.add(filecontent);
					atomnumber++;
				}
				fis.close();
			}catch(IOException ioe){
		    	System.out.println("Error in reading the cached files");
    	    	System.out.println(filecontent+"\t"+cachedFiles+"\t"+cachedFile);
			}
		}
		mdad_Hadoopv6.totalatom=atomnumber;
//	    }	
		
		//NeighborList check and update method, it should be here, because they need the information of all atoms
		if (mdad_Hadoopv6.mdIteration%5==0)
		{
		    try{
			neighborSelect(mdad_Hadoopv6.atomstore.atom1,mdad_Hadoopv6.mdIteration);
//		       System.out.println("DO YOU BE HERE?"+mdad_Hadoopv6.mdIteration);
		    }catch (Exception eee){
		        System.out.println("There is a exception in updating the neighborList!");
		    }
		}
	}	
    public void neighborSelect (ArrayList<String> atoms1, int iterators) throws Exception{
	    int iter;
	    int maxCubeNum_x=0;
	    int maxCubeNum_y=0;
	    int maxCubeNum_z=0;
	    double adjustment=Kai/2;   //Because the center of the simulation cube is (0,0,0), we need make a adjustment 
	    double x_accN,y_accN,z_accN;
	    double x_velN,y_velN,z_velN;
	    int seq=0;
	    int seqnumber=0;
	    int seqnumber2=0;
	    int area_x,area_y,area_z;
	    String areaStr2="";
	    String atomsymbol="";
	    String atomsymbol2="";
        double x,y,z;
	    String atomWithAreaNum="";
	    String areaNum="";
	    String areaAtom="";
	    String areaStr="";
	    HashMap aN =new HashMap();
	    HashMap aS=new HashMap();
	    double dist;
	    String atomLine="";
	    String newState="";
	    String areaWithAtomNum="";
	    StringTokenizer token;
	    StringTokenizer token2;
	    TreeSet ts=new TreeSet();
	    x=y=z=0.0;
	    area_x=area_y=area_z=0;
	    x_accN=y_accN=z_accN=0.0;
	    x_velN=y_velN=z_velN=0.0;
	//This segment of program is focus on giving every atom a area number according its position, 
//	try{
	    for(iter=1;iter<mdad_Hadoopv6.totalatom+1;iter++){
		seq=0;
//		if(iterators%2==0){
		    atomLine=atoms1.get(iter-1);
//		}
//		else{
//		    atomLine=atoms2.get(iter-1);
//		}
		token= new StringTokenizer(atomLine);
		while (token.hasMoreTokens()){
		    seq++;
		    switch(seq){
		        case 1: seqnumber=Integer.parseInt(token.nextToken());
		        break;
		        case 2: atomsymbol=token.nextToken();
		        break;
		        case 3: x_position=Double.parseDouble(token.nextToken());
		        break;
		        case 4: y_position=Double.parseDouble(token.nextToken());
		        break;
		        case 5: z_position=Double.parseDouble(token.nextToken());
		        break;
			default: break;
		   }
		   if (seq>5){
		       break;
		   }
	       }

	       x_position+=adjustment;
	       y_position+=adjustment;
	       z_position+=adjustment;
	       area_x=(int)java.lang.Math.floor(x_position/(2*Rc));
	       area_y=(int)java.lang.Math.floor(y_position/(2*Rc));
	       area_z=(int)java.lang.Math.floor(z_position/(2*Rc));
           maxCubeNum_x=maxCubeNum_y=maxCubeNum_z=(int)(Kai/(2.0*Rc));
/*	       if (maxCubeNum_x<area_x){
		       maxCubeNum_x=area_x;
	       }
	       if (maxCubeNum_z<area_z){
		       maxCubeNum_z=area_z;
	       }
	       if(maxCubeNum_y<area_y){
		       maxCubeNum_y=area_y;
	       }
*/
           areaNum=area_x+"_"+area_y+"_"+area_z;

	       if (aN.get(areaNum)==null){
	           areaAtom="";
	           areaAtom+=iter+" ";
	           aN.put(areaNum,areaAtom);
	       }
	       else{
	           areaAtom="";
	           areaAtom=(aN.get(areaNum)).toString()+" ";
		   areaAtom+=iter+" ";
		   aN.remove(areaNum);
		   aN.put(areaNum,areaAtom);
	       }
	       areaWithAtomNum= areaNum+" "+atomLine;
//	       if(iterators%2==0){
	           atoms1.set(iter-1,areaWithAtomNum);
//	       }
//	       else{
//	           atoms2.set(iter-1,areaWithAtomNum);
//	       }
	    }

	// This segment of program is focus on finding all the candidate atoms in the surrounding cube for every area number    
	    Iterator itt= aN.entrySet().iterator();
//	try{
	    while(itt.hasNext()){
	        java.util.Map.Entry entry=(java.util.Map.Entry)itt.next();
		Object key= entry.getKey();
		Object value=entry.getValue();
	        areaNum=key.toString();
		areaAtom=value.toString()+" ";
		String[] area=new String[3];
		area=areaNum.split("_");
		area_x=Integer.parseInt(area[0]);
		area_y=Integer.parseInt(area[1]);
		area_z=Integer.parseInt(area[2]);
		if ((area_x+1)>maxCubeNum_x){
		    areaAtom+=aN.get((0+"_"+area_y+"_"+area_z))+" ";
		    areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+area_z))+" ";
		    if ((area_y+1)>maxCubeNum_y){
		        areaAtom+=aN.get((0+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+area_z))+" ";
			    if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get((0+"_"+0+"_"+0))+" ";
				    areaAtom+=aN.get((0+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+0+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get((0+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get((0+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
				}
			}
			else if((area_y-1)<0){
				areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+area_z))+" ";
			    areaAtom+=aN.get((0+"_"+maxCubeNum_y+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+area_z))+" ";
				if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get((0+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get((0+"_"+maxCubeNum_y+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
				}
			}
			else{
				areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+area_z))+" ";
				if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get((0+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((0+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
				}
			}
		}
		else if ((area_x-1)<0) {
			areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+area_z))+" ";
		    areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+area_z))+" ";
		    if ((area_y+1)>maxCubeNum_y){
		        areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get((maxCubeNum_x+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+area_z))+" ";
			    if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+0))+" ";
				    areaAtom+=aN.get((maxCubeNum_x+"_"+0+"_"+0))+" ";
				    areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+0+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
				}
			}
			else if((area_y-1)<0){
				areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((maxCubeNum_x+"_"+maxCubeNum_y+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+area_z))+" ";
				if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+0))+" ";
				    areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+maxCubeNum_y+"_"+0))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
				}
			}
			else{
				areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+area_z))+" ";
				if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+0))+" ";
				    areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((maxCubeNum_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
				}
			}
		}
		else{
		    areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+area_z))+" ";
		    areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+area_z))+" ";
		    if ((area_y+1)>maxCubeNum_y){
		        areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+area_z))+" ";
			    if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+0+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+0+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
				}
			}
			else if((area_y-1)<0){
				areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+area_z))+" ";
				if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+maxCubeNum_y+"_"+(area_z-1)))+" ";
				}
			}
			else{
				areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+area_z))+" ";
			    areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+area_z))+" ";
				if ((area_z+1)>maxCubeNum_z){
				    areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+0))+" ";
				    areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+0))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
			        areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
			    }
			    else if ((area_z-1)<0){
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+maxCubeNum_z))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+maxCubeNum_z))+" ";
				}
				else {
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z+1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x-1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get(((area_x+1)+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y+1)+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+area_y+"_"+(area_z-1)))+" ";
					areaAtom+=aN.get((area_x+"_"+(area_y-1)+"_"+(area_z-1)))+" ";
				}
			}
		}
		areaAtom=areaAtom.replace("null","");
		aS.put(areaNum,areaAtom);
	    }
//	}catch(IOException ee){
//	    System.out.println("This candidates' area searching operation has exceptions:"+ee);
//	}

	    //This part of program is focus on generating the neighborlist of every atom in the simulation system.
//	try{
	    for(iter=1;iter<totalatom+1;iter++){
	       ts.clear();
	       areaAtom="";
//	       if(iterators%2==0){
	           atomLine=atoms1.get(iter-1);
//	       }
//	       else{
//	           atomLine=atoms2.get(iter-1);
//	       }	       
	       token=new StringTokenizer(atomLine);
	       seq=0;
	       while (token.hasMoreTokens()){
	           seq++;
	           switch(seq){
	               case 1: areaStr=token.nextToken();
	               break;
		       case 2: seqnumber=Integer.parseInt(token.nextToken());
		       break;
		       case 3: atomsymbol=token.nextToken();
		       break;
		       case 4: x_position=Double.parseDouble(token.nextToken());
		       break;
		       case 5: y_position=Double.parseDouble(token.nextToken());
		       break;
		       case 6: z_position=Double.parseDouble(token.nextToken());
		       break;
		       case 7: x_velN=Double.parseDouble(token.nextToken());
	               break;
		       case 8: y_velN=Double.parseDouble(token.nextToken());
		       break;
		       case 9: z_velN=Double.parseDouble(token.nextToken());
		       break;
		       case 10: x_accN=Double.parseDouble(token.nextToken());
		       break;
		       case 11: y_accN=Double.parseDouble(token.nextToken());
		       break;
		       case 12: z_accN=Double.parseDouble(token.nextToken());
	               default:break;
		   }
		   if (seq>12){
		       break;
		   }
	       }
	       token=new StringTokenizer((aS.get(areaStr)).toString());
		while(token.hasMoreTokens()){
		   seqnumber2=Integer.parseInt(token.nextToken());
//		   if(iterators%2==0){
		       atomLine=atoms1.get(seqnumber2-1);
//		   }
//		   else{
//		       atomLine=atoms2.get(seqnumber2-1);
//		   }
		   token2=new StringTokenizer(atomLine);
		   seq=0;
		   if (iter<=seqnumber2){
		       while(token2.hasMoreTokens()){
		           seq++;
		           switch(seq){
		               case 1: areaStr2=token2.nextToken();//System.out.println(areaStr2);
			       break;
			       case 2: seqnumber=Integer.parseInt(token2.nextToken());//System.out.println(seqnumber);
			       break;
			       case 3: atomsymbol=token2.nextToken();
			       break;
			       case 4: x=Double.parseDouble(token2.nextToken());
			       break;
			       case 5: y=Double.parseDouble(token2.nextToken());
			       break;
			       case 6: z=Double.parseDouble(token2.nextToken());
			       break;
			       default: break;
		           }
		           if(seq>6){
		               break;
		           }
		       }
		   }
		   else{
		       while(token2.hasMoreTokens()){
		           seq++;
		           switch(seq){
		               case 1: seqnumber=Integer.parseInt(token2.nextToken());//System.out.println(seqnumber);
		               break;
		               case 2: atomsymbol2=token2.nextToken();
		               break;
		               case 3: x=Double.parseDouble(token2.nextToken());
		               break;
		               case 4: y=Double.parseDouble(token2.nextToken());
		               break;
		               case 5: z=Double.parseDouble(token2.nextToken());
		               break;
		               default: break;
		           }
		           if(seq>5){
		               break;
		           }
		      }
		   }
                   if((fabs(x-x_position)<=2*Rc)&&(fabs(y-y_position)<2*Rc)&&(fabs(z-z_position)<2*Rc)){
		       dist=sqrt(pow(x-x_position,2)+pow(y-y_position,2)+pow(z-z_position,2));
		       if ((dist<2*Rc)&&(dist!=0)){
			   ts.add(seqnumber2);
		       }
		       else{
			   continue;
		       }
		   }
                   else{
		       continue;
		   }
	       }
	       Iterator it=ts.iterator();
	       while(it.hasNext()){
	           areaAtom+=(it.next()).toString()+" ";
	       }
               newState=iter+" "+atomsymbol+" "+x_position+" "+y_position+" "+z_position+" "+x_velN+" "+y_velN+" "+z_velN+" "+x_accN+" "+y_accN+" "+z_accN+" "+areaAtom;
//               System.out.println(newState);
//	       if(iterators%2==0){
	          mdad_Hadoopv6.atomstore.atom1.set(iter-1,newState);
//                 System.out.println(mdad_Hadoopv6.atomstore.atom1.size());
//                 System.out.println(mdad_Hadoopv6.atomstore.atom1.get(iter-1));
//	       }
//	       else{
//	          mdad_Hadoopv6.atomstore.atom2.set(iter-1,newState);
//	       }
	    }
    }
	public String mdvverlet(int keys ){
		double rx,ry,rz;
		double x2,y2,z2;
		double fsumx,fsumy,fsumz;
		double force;
		int j,seq;
		double xvel,yvel,zvel;
		double xacc,yacc,zacc;
		int atompos=0;
		String linevalue="";
		String atomsymbol="";
		String new_state;
		String neighbor="";
		rx=ry=rz=0;
		force=0;
		xacc=yacc=zacc=0;
		xvel=yvel=zvel=0;
		x2=y2=z2=0;
		seq=0;
		String line="";
		fsumx=fsumy=fsumz=0;

//		for(j=1;j<totalatom+1;j++){
		linevalue=mdad_Hadoopv6.atomstore.atom1.get(keys-1);
		System.out.println(linevalue);
		StringTokenizer token=new StringTokenizer(linevalue);
		while (token.hasMoreTokens())
		{
			seq++;
			switch(seq){
				case 1: atompos=Integer.parseInt(token.nextToken());
				break;
				case 2: atomsymbol=token.nextToken();
				break;
				case 3: x_position=Double.parseDouble(token.nextToken());
				break;
				case 4: y_position=Double.parseDouble(token.nextToken());
				break;
				case 5: z_position=Double.parseDouble(token.nextToken());
				break;
				case 6: xvel=Double.parseDouble(token.nextToken());
				break;
				case 7: yvel=Double.parseDouble(token.nextToken());
				break; 
				case 8: zvel=Double.parseDouble(token.nextToken());
				break;
				case 9: xacc=Double.parseDouble(token.nextToken());
				break;
				case 10: yacc=Double.parseDouble(token.nextToken());
				break;
				case 11: zacc=Double.parseDouble(token.nextToken());
				break;
				default: neighbor+=token.nextToken()+" ";
				break;
			}
		}
//		}
//	Read information of the atom which will interact with the selected one
//		System.out.println("This is neighbor"+neighbor);
		StringTokenizer tokenNeighbor=new StringTokenizer(neighbor);
			while (tokenNeighbor.hasMoreTokens())
			{
				int neighborNumber=Integer.parseInt(tokenNeighbor.nextToken());
				line=mdad_Hadoopv6.atomstore.atom1.get(neighborNumber-1);
				StringTokenizer token1=new StringTokenizer(line);
				seq=0;
				while (token1.hasMoreTokens()){
					String temp=token1.nextToken();
					seq++;
					switch (seq){
						case 3: x2=Double.parseDouble(temp);
						break;
						case 4: y2=Double.parseDouble(temp);
						break;
						case 5: z2=Double.parseDouble(temp);
						break;
					}
				}		
				rx=-(x2-x_position);
				ry=-(y2-y_position);
				rz=-(z2-z_position);
			
//Decide other atoms that the select atom will interact with, if this interaction includes the image/ghost atoms, the delta(x) will change the sign
			
				if(fabs(rx)>(Kai-Rc)){
					if (rx<0){
						rx=Kai-fabs(rx);
					}
					else {
						rx=fabs(rx)-Kai;
					}
				}
				if(fabs(ry)>(Kai-Rc)){
					if (ry<0){
						ry=Kai-fabs(ry);
					}
					else {
						ry=fabs(ry)-Kai;
					}
				}
				if(fabs(rz)>(Kai-Rc)){
					if (rz<=0){
						rz=Kai-fabs(rz);
					}
					else {
						rz=fabs(rz)-Kai;
					}
				}
				if (fabs(rx)<=Rc && fabs(ry)<=Rc && fabs(rz)<=Rc){
				    dist=sqrt(pow(rx,2.0)+pow(ry,2.0)+pow(rz,2.0));
				    if (dist<=Rc&&dist!=0){
				    	force=(24.0*(2.0*pow(dist,-13.0)-pow(dist,-7.0)));
				    	fsumx+=force*(rx/dist);
				    	fsumy+=force*(ry/dist);
				    	fsumz+=force*(rz/dist);
				    }
				}
				else{
				    continue;
				}
		}
		//generate the new acceleration
		x_acc=fsumx/mass;
		y_acc=fsumy/mass;
		z_acc=fsumz/mass;

		//get the new velocity
		x_vel=(xvel+TimeStep*(x_acc+xacc)/2.0);
		y_vel=(yvel+TimeStep*(y_acc+yacc)/2.0);
		z_vel=(zvel+TimeStep*(z_acc+zacc)/2.0);

		//get the next timestep position
		x_position=x_position+xvel*TimeStep+xacc*pow(TimeStep,2.0)/2.0;
		y_position=y_position+yvel*TimeStep+yacc*pow(TimeStep,2.0)/2.0;
		z_position=z_position+zvel*TimeStep+zacc*pow(TimeStep,2.0)/2.0;

		//periodic boundary check
		pboxcheck();
		new_state="  "+atomsymbol+"  "+x_position+"  "+y_position+"  "+z_position+"  "+x_vel+"   "+y_vel+"   "+z_vel+"   "+x_acc+"   "+y_acc+"   "+z_acc+" "+neighbor;
//		System.out.println("This is mdverlet output:"+new_state);
		return(new_state);
	}
	public void pboxcheck(){
		if (x_position<(-(Kai)/2)){
			this.x_position +=Kai;	
		}
		else if (x_position>((Kai)/2)){
			this.x_position -=Kai;
		}
		if (y_position<(-(Kai)/2)){
			this.y_position +=Kai;
		}
		else if (y_position>((Kai)/2)){
			this.y_position -=Kai;
		}
		if (z_position<(-(Kai)/2)){
			this.z_position +=Kai;
		}
		else if (z_position>((Kai)/2)){
			this.z_position -=Kai;
		}
	}

	public double sqrt(double d) {

		double squareroot=java.lang.Math.sqrt(d);
		return squareroot;
	}
	public double fabs(double f) {
		double re=java.lang.Math.abs(f);
		return re;
	}
	public double pow(double d, double exp) {
		double powerresult=java.lang.Math.pow(d, exp);
		return powerresult;
	}

    public void map(LongWritable key,Text value, OutputCollector<LongWritable,Text> output, Reporter reporter) throws IOException{
        int keys;
	String values="";
	StringTokenizer token=new StringTokenizer(value.toString());
	keys=Integer.parseInt(token.nextToken());
//	System.out.println("This is the map method:"+value);
	values=mdvverlet(keys);
	output.collect(new LongWritable(keys),new Text(values));
    }
}
/*
// Reduce class
//main() method which control the iteration and configuration of hadoop job

public static class Reduce extends MapReduceBase implements Reducer<LongWritable, Text, LongWritable, Text> {
//    public class Context extends ReduceContext<LongWritable,Text,LongWritable,Text> {
//	    public Context(Configuration conf, TaskAttemptID taskid,RawKeyValueIterator input,
//			   Counter inputCounter,RecordWriter<LongWritable,Text> output,OutputCommitter committer,
//			   StatusReporter reporter,RawComparator<LongWritable> comparator,Class<LongWritable> keyClass,
//		           Class<Text> valueClass ) throws IOException, InterruptedException {
            
//}

  public void reduce(LongWritable rkey, Iterable<Text> rvalues, Context context
                          ) throws IOException, InterruptedException {
	 for(Text rvalue: rvalues) {
	     if(mdad_Hadoopv6.mdIteration%2==0){
	         mdad_Hadoopv6.atomstore.atom1.set(Integer.parseInt(rkey.toString()),rvalue.toString());
	      }
	      else {
	          mdad_Hadoopv6.atomstore.atom2.set(Integer.parseInt(rkey.toString()),rvalue.toString());
	      }
	      if (mdad_Hadoopv6.outputPeriod==0){
	          mdad_Hadoopv6.outputPeriod=10;
	      }
	     context.write((LongWritable) rkey, (Text) rvalue);
	 }
  }

  public void run(Context context) throws IOException, InterruptedException {
      setup(context);
      while (context.nextKey()) {
          reduce(context.getCurrentKey(), context.getValues(), context);
      }
       cleanup(context);
  }

        public void reduce(LongWritable rkey, Iterator<Text> rvalues, OutputCollector<LongWritable, Text> routput, Reporter reporter) throws IOException {
// 	       int sum = 0;
// 	       int mdIteration;
//	       mdIteration=Integer.parseInt(job.get("md.iteration"));
//	       int outputPeriod;
	 while(rvalues.hasNext()){    
	     String rvalue=(rvalues.next()).toString();
	     if(mdad_Hadoopv6.mdIteration%2==0){
	           mdad_Hadoopv6.atomstore.atom1.set(Integer.parseInt(rkey.toString()),rvalue); 
	       }
	       else {
	           mdad_Hadoopv6.atomstore.atom2.set(Integer.parseInt(rkey.toString()),rvalue);
	       }
	       if (mdad_Hadoopv6.outputPeriod==0){
	           mdad_Hadoopv6.outputPeriod=10;
	       }
	       if (mdad_Hadoopv6.mdIteration%mdad_Hadoopv6.outputPeriod==0){
 	           routput.collect(rkey,new Text(rvalue));
 	       }
	       else{
	       	   rvalue="";
	           routput.collect(rkey,new Text(rvalue));
	       }
	 }	     
    }
}
*/
    public static void main(String[] args) throws Exception{
		int i=0;
        mdad_Hadoopv6 t1=new mdad_Hadoopv6();
		atomStorage atoms=new atomStorage();
		t1.atomstore=atoms;
	    String inputPath="/user/che/mdad/in";
	    String outputPath="/user/che/mdad/out";
	//Parameter input
	    for(i=0;i<args.length;i++){
		if ("-t".equals(args[i])){
			t1.TimeStep=Double.parseDouble(args[++i]);    
		}
		else if ("-T".equals(args[i])){
			t1.numIteration=Integer.parseInt(args[++i]);
		}
		else if ("-m".equals(args[i])){
			t1.moviePath=outputPath+args[++i];
		}
		else if ("-op".equals(args[i])){
		        t1.outputPeriod=Integer.parseInt(args[++i]);
		}
	    }

	//Simulation iteration
	java.util.Calendar cStart = java.util.Calendar.getInstance();
	    for(i=0;i<mdad_Hadoopv6.numIteration;i++){
	    	JobClient client=new JobClient();
	    	JobConf conf=new JobConf(mdad_Hadoopv6.class);
		
	    	conf.setOutputKeyClass(LongWritable.class);
	    	conf.setOutputValueClass(Text.class);
                conf.setMapSpeculativeExecution(true);
                conf.setMaxMapAttempts(3);
                conf.setMaxReduceAttempts(3);
//              conf.setProfileEnabled(true);
                conf.setSpeculativeExecution(true);
//                conf.setNumMapTasks(256);
                conf.setNumReduceTasks(1);
                conf.setNumTasksToExecutePerJvm(-1);
                conf.setMapperClass(Map.class);
//              conf.set("hadoop.job.history.user.location","/home/cse856/che/jobhistory/");
//              conf.set("hadoop.job.history.location","/home/cse856/che/jobhistory/");
//              conf.setInt("dfs.replication",6);	

                conf.set("md.iteration",Integer.toString(i));	
//                conf.set("md.moviepath",outputPath);

//            for(i=0;i<mdad_Hadoopv6.numIteration;i++){
	    	if (i==0){
//				the first step of the iteration 
//				movloader() should be called here
//				t1.movloader(inputPath);
	    		FileSystem fs =null;
	    		FileStatus[] filestatus=null;
	    		Path inPath=new Path(inputPath);
	    		try{
	    			fs=inPath.getFileSystem(conf);
	    			filestatus=fs.listStatus(inPath);	
	    		}catch(IOException ioe){
	    			System.out.println("can not use the filesystem to locate the cache file");
	    			System.exit(1);
	    		}
	    		for(FileStatus filestat : filestatus){
	    			Path fileToCache= filestat.getPath();
	    			DistributedCache.addCacheFile(fileToCache.toUri(),conf);
	    		}	
	    		FileInputFormat.setInputPaths(conf,new Path(inputPath));
			    FileOutputFormat.setOutputPath(conf,new Path(outputPath + Integer.toString(i)));
	    	}
	    	else{
	    		
	    		FileSystem fs= null;	
	    		FileStatus[] filestatus = null;
	    		Path outPath= new Path(outputPath + Integer.toString(i-1));			
	    		try{
	    			fs=outPath.getFileSystem(conf);
	    			filestatus = fs.listStatus(outPath);
	    		}catch (Exception e){
	    			System.out.println("Error getting the file status:"+e.getCause());
	    			System.exit(1);
	    		}
	    		for(FileStatus filestat : filestatus){
	    			Path fileToCache= filestat.getPath();
	    			if (filestat.getLen()!=0){
	    				DistributedCache.addCacheFile(fileToCache.toUri(),conf);
	    			}
	    		}	
			// Here, if use the same input file what will happen? 
	    		FileInputFormat.setInputPaths(conf,new Path(outputPath + Integer.toString(i-1)));
	    		FileOutputFormat.setOutputPath(conf,new Path(outputPath + Integer.toString(i)));
	    	}
		
	    	client.setConf(conf);

	    	try{
	    		JobClient.runJob(conf);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}		
	    }
	    java.util.Calendar cEnd = java.util.Calendar.getInstance();
	    System.out.println(cEnd.getTimeInMillis()-cStart.getTimeInMillis());
	}
	public mdad_Hadoopv6(){}
}

class atomStorage 
{
	public static ArrayList<String> atom1=new ArrayList<String>();
//	public static ArrayList<String> atom2=new ArrayList<String>();
        public atomStorage(){}
}
