package spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("serial")
public class TwitterSpout extends BaseRichSpout {

    public static final String MESSAGE = "message";
    private final String _accessTokenSecret;
    private final String _accessToken;
    private final String _consumerSecret;
    private final String _consumerKey;
    private SpoutOutputCollector _collector;
    private TwitterStream _twitterStream;
    @SuppressWarnings("rawtypes")
	private LinkedBlockingQueue _msgs;
    private FilterQuery _tweetFilterQuery;

    public TwitterSpout(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        if (consumerKey == null ||
                consumerSecret == null ||
                accessToken == null ||
                accessTokenSecret == null) {
            throw new RuntimeException("Twitter4j OAuth field cannot be null");
        }

        _consumerKey = consumerKey;
        _consumerSecret = consumerSecret;
        _accessToken = accessToken;
        _accessTokenSecret = accessTokenSecret;

    }

    public TwitterSpout(String arg, String arg1, String arg2, String arg3, FilterQuery filterQuery) {
        this(arg,arg1,arg2,arg3);
        _tweetFilterQuery = filterQuery;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(MESSAGE));
    }

     //Creates a twitter stream listener which adds messages to a LinkedBlockingQueue. Starts to listen to streams
     
    @SuppressWarnings("rawtypes")
	@Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        _msgs = new LinkedBlockingQueue();
        _collector = spoutOutputCollector;
        
        //Authentication
        ConfigurationBuilder _configurationBuilder = new ConfigurationBuilder();
        _configurationBuilder.setOAuthConsumerKey(_consumerKey)
                .setOAuthConsumerSecret(_consumerSecret)
                .setOAuthAccessToken(_accessToken)
                .setOAuthAccessTokenSecret(_accessTokenSecret);
        
        //Initiate twitter stream using authentication
        _twitterStream = new TwitterStreamFactory(_configurationBuilder.build()).getInstance();
       
        //Listener for stream reader
        _twitterStream.addListener(new StatusListener() {
            @SuppressWarnings("unchecked")
			@Override
            public void onStatus(Status status) {
                if (meetsConditions(status))
                
                // gets username as well as tweets.
                _msgs.offer("@" + status.getUser().getScreenName() + " - " + status.getText());
             
                // Print tweet text to console
                // System.out.println(status.getUser().getScreenName() + " - " + status.getText());
                
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                
            }

            @Override
            public void onException(Exception ex) {
                
            }
        });
        
        // Filter Query
        if (_tweetFilterQuery == null) {
            _twitterStream.sample();
        }
        else {
            _twitterStream.filter(_tweetFilterQuery);
        }
    }

    private boolean meetsConditions(Status status) {
        return true;
    }

    @Override
    public void nextTuple() {
        // emit tweets
        Object s = _msgs.poll();
        if (s == null) {
            Utils.sleep(1000);
        } else {
            _collector.emit(new Values(s));

        }
    }

    @Override
    public void close() {
        _twitterStream.shutdown();
        super.close();
    }

}
