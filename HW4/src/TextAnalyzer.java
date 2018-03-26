import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.*;
import java.io.*;

// Do not change the signature of this class
public class TextAnalyzer extends Configured implements Tool {

    // Replace "?" with your own output key / value types
    // The four template data types are:
    //     <Input Key Type, Input Value Type, Output Key Type, Output Value Type>
    public static class TextMapper extends Mapper<LongWritable, Text, Text, Tuple> {

        @Override
        public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you mapper function
            String line = value.toString().toLowerCase();
            String[] split = line.split("\\W+");

            Set<String> contextWords = new HashSet<String>();
            for(int i = 0; i < split.length; i++) {
                if(split[i].length() > 0) {
                    if (!contextWords.contains(split[i])) {
                        contextWords.add(split[i]);
                        Tuple queryWords = new Tuple();
                        for (int j = 0; j < split.length; j++) {
                            if(i != j && split[j].length() > 0)
                                queryWords.add(new Text(split[j]));
                        }

                        context.write(new Text(split[i]), queryWords);
                    }

                }
            }
        }
    }

    // Replace "?" with your own key / value types
    // NOTE: combiner's output key / value types have to be the same as those of mapper
    public static class TextCombiner extends Reducer<LongWritable, Text, Text, Tuple> {
        public void reduce(Text key, Iterable<Tuple> tuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you combiner function
        }
    }

    // Replace "?" with your own input key / value types, i.e., the output
    // key / value types of your mapper function
    public static class TextReducer extends Reducer<Text, Tuple, Text, Text> {
        private final static Text emptyText = new Text("");

        @Override
        public void reduce(Text key, Iterable<Tuple> queryTuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you reducer function
            Tuple queryWords = new Tuple();                                 // will store all query word tuples for this context word
            for(Tuple t: queryTuples) {
                for(Writable queryWord: t.getMap().keySet())
                    queryWords.add((Text)queryWord, (IntWritable)t.getMap().get(queryWord));
            }

            Map<Writable, Writable> map = queryWords.getMap();
            List<String> queries = queryWords.sortQueries();

            Text queryWordText = new Text();
            // Write out the results; you may change the following example
            // code to fit with your reducer function.
            //   Write out the current context key
            context.write(key, emptyText);
            //   Write out query words and their count
            for(String queryWord: queries){
                String count = map.get(new Text(queryWord)).toString() + ">";
                queryWordText.set("<" + queryWord + ",");
                context.write(queryWordText, new Text(count));
            }
            //   Empty line for ending the current context key
            context.write(emptyText, emptyText);
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        // Create job
        Job job = new Job(conf, "EID1_EID2"); // Replace with your EIDs
        job.setJarByClass(TextAnalyzer.class);

        // Setup MapReduce job
        job.setMapperClass(TextMapper.class);
        //   Uncomment the following line if you want to use Combiner class
        // job.setCombinerClass(TextCombiner.class);
        job.setReducerClass(TextReducer.class);

        // Specify key / value types (Don't change them for the purpose of this assignment)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //   If your mapper and combiner's  output types are different from Text.class,
        //   then uncomment the following lines to specify the data types.
        //job.setMapOutputKeyClass(?.class);
        //job.setMapOutputValueClass(?.class);

        // Input
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Do not modify the main method
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TextAnalyzer(), args);
        System.exit(res);
    }

    // You may define sub-classes here. Example:
    // public static class MyClass {
    //
    // }

    public static class Tuple implements Writable {
        private Map<Writable, Writable> map;

        public Tuple() {
            map = new MapWritable();
        }

        public void add(Text word) {
            if(map.containsKey(word)) {
                IntWritable count = new IntWritable(((IntWritable) map.get(word)).get() + 1);
                map.put(word, count);
            }
            else
                map.put(word, new IntWritable(1));
        }

        public void add(Text word, IntWritable count) {
            if(map.containsKey(word)) {
                IntWritable totalCount = new IntWritable(((IntWritable) map.get(word)).get() + count.get());
                map.put(word, totalCount);
            }
            else
                map.put(word, count);
        }

        @Override
        public void write(DataOutput out) throws IOException {
            ((MapWritable)map).write(out);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            ((MapWritable)map).readFields(in);
        }

        public Map<Writable, Writable> getMap() {
            return map;
        }

        public List<String> sortQueries() {
            List<String> queryWords = new ArrayList<String>();
            String maxKey = "";
            int maxCount = Integer.MIN_VALUE;
            for(Writable word: map.keySet()) {
                if(((IntWritable)map.get(word)).get() > maxCount) {
                    maxKey = word.toString();
                    maxCount = ((IntWritable)map.get(word)).get();
                }

                if(!maxKey.equals(word.toString()))
                    queryWords.add(word.toString());
            }

            Collections.sort(queryWords);
            queryWords.add(0, maxKey);

            return queryWords;
        }
    }
}



