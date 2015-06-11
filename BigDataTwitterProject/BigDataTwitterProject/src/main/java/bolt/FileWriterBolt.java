package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@SuppressWarnings("serial")
public class FileWriterBolt extends BaseRichBolt {
    PrintWriter writer;
    int count = 0;
    private OutputCollector _collector;

    private String filename;

    public FileWriterBolt(String filename){
        this.filename = filename;
    }

    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        _collector = outputCollector;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  
        }

    }

    @Override
    public void execute(Tuple tuple) {
        writer.println((count++)+":"+tuple);
        writer.flush();
        // Confirm that this tuple has been treated.
        _collector.ack(tuple);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public void cleanup() {
        writer.close();
        super.cleanup();

    }
}
