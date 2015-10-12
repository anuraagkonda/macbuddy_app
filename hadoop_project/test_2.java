package test_hadoop;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.Job;
/* Anuraag Konda 1001051814
 * assignment 2
 * 6331 batch 6.00pm
 * References wwww.stackoverflow.com
 * www.apache.org
 */

public class test_2 {
	public static class Map extends MapReduceBase implements Mapper<LongWritable,Text,Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text("");
        private Text word2 = new Text("");
        private Text word3 = new Text("");
      
        public void map(LongWritable key, Text value, OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException {
        	
            String line=value.toString();
            String[] linearray=line.split(",");
        	word.set(linearray[2]+" "+linearray[4]+" "+linearray[3]+" "+linearray[6]);
            output.collect(word,one);
            word2.set(linearray[2]);
            output.collect(word2, one);
            
        }
        
}
	   public static class Map1 extends MapReduceBase implements Mapper<LongWritable,Text,Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        
        private Text word3 = new Text("");
      
        public void map(LongWritable key, Text value, OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException {
        	
            String line=value.toString();
            String[] linearray=line.split(",");
        	
            word3.set(linearray[16]);
            output.collect(word3, one);
        }
        
}
public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
                int sum = 0;
                while (values.hasNext()) {
                        sum += 1;
                        values.next();
                }
                output.collect(key, new IntWritable(sum));
        }
}

public static void main(String[] args) throws Exception {
	    long st = System.currentTimeMillis();
	    
        JobConf conf = new JobConf(test_2.class);
        conf.setJobName("assignment_2");
       
       // conf.setInt("mapred.max.split.size",64*1024*1024);
        
       // conf.setNumMapTasks(2);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);
        conf.setMapperClass(Map.class);
        
        conf.setReducerClass(Reduce.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        JobClient.runJob(conf);
        System.out.println("Number of mappers"+conf.get("mapred.map.tasks"));
        JobConf conf1 = new JobConf(test_2.class);
        conf1.setInt("dfs.block.size",64*1024*1024);
        conf1.setJobName("part-2");
        conf1.setOutputKeyClass(Text.class);
        conf1.setOutputValueClass(IntWritable.class);
        conf1.setMapperClass(Map1.class);
        conf1.setReducerClass(Reduce.class);
        conf1.setInputFormat(TextInputFormat.class);
        conf1.setOutputFormat(TextOutputFormat.class);
        FileInputFormat.setInputPaths(conf1, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf1, new Path(args[2]));
        JobClient.runJob(conf1);
        long et = System.currentTimeMillis();
        long comp = et-st;
        System.out.println("computation time"+comp);
        //System.out.println("Number of mappers"+conf.get("mapred.map.tasks"));
   }
}
