 	 import java.io.IOException;
 	import java.util.*;
	import java.lang.*;
	
 	import org.apache.hadoop.fs.Path;
 	import org.apache.hadoop.conf.*;
 	import org.apache.hadoop.io.*;
 	import org.apache.hadoop.mapred.*;
 	import org.apache.hadoop.util.*;
 	
 	public class CharactorCount {
 	
 	   public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
 	     private final static IntWritable one = new IntWritable(1);
 	     private Text word = new Text();
 	
 	     public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
                        char[] cArray=new char[256];
			int len=value.toString().length();
			cArray=value.toString().toCharArray();
			int i=0;
			String out="";
			while (i<len) {
			    if (cArray[i]!=' '&&cArray[i]!='\t'&&cArray[i]!='\0'&&cArray[i]!='\n'){
                                 out=Character.toString(cArray[i]);
				 word.set(out);
                                 output.collect(word, one);
 	       		    }
			    i++;
 	     	       }
 	   	}
	  }
 	
 	   public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
 	     public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
 	       int sum = 0;
 	       while (values.hasNext()) {
 	         sum += values.next().get();
 	       }
 	       output.collect(key, new IntWritable(sum));
 	     }
 	   }
 	
 	   public static void main(String[] args) throws Exception {
	     
	    int TT_Num=1;	
	    for(int i=0;i<args.length;i++){
			if ("-TT".equals(args[i])){
				TT_Num=Integer.parseInt(args[++i]);    
			}
		}
 	     JobConf conf = new JobConf(CharactorCount.class);
 	     conf.setJobName("CharactorCount");
 	
 	     conf.setOutputKeyClass(Text.class);
 	     conf.setOutputValueClass(IntWritable.class);
 	
 	     conf.setMapperClass(Map.class);
 	     conf.setCombinerClass(Reduce.class);
 	     conf.setReducerClass(Reduce.class);
 	
 	     conf.setInputFormat(TextInputFormat.class);
 	     conf.setOutputFormat(TextOutputFormat.class);
 	
 	     FileInputFormat.setInputPaths(conf, new Path(args[0]));
 	     FileOutputFormat.setOutputPath(conf, new Path(args[1]));

	     conf.setMaxMapAttempts(2);
             //conf.setNumMapTasks(TT_Num);

 	     JobClient.runJob(conf);
 	   }
 	}
