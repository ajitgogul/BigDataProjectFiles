package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.StringTokenizer;


@SuppressWarnings("serial")
public class HashtagExtractionBolt extends BaseRichBolt {
    private OutputCollector _collector;


    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        _collector = outputCollector;

    }

    @Override
    public void execute(Tuple tuple) {
        String text = tuple.getStringByField("message");
        StringTokenizer st = new StringTokenizer(text);

        System.out.println("- Split -");
        while (st.hasMoreElements()) {
            String term = (String) st.nextElement();
            if (StringUtils.startsWith(term, "@"))
                _collector.emit(new Values(term, tuple.getDoubleByField("sentiment-value")));
            	break;
        }

        // Confirm that this tuple has been treated.
        _collector.ack(tuple);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("entity","sentiment" ));
    }

    @Override
    public void cleanup() {
        super.cleanup();

    }
}
