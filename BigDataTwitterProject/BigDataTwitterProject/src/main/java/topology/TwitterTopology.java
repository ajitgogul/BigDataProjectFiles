package topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import bolt.FileWriterBolt;
import bolt.HashtagExtractionBolt;
import bolt.SentimentBolt;
import spout.TwitterSpout;
import twitter4j.FilterQuery;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
public class TwitterTopology {

	private static String consumerKey = "MEwbq5CnXxXFHtAlndjOuNnRK";
    private static String consumerSecret = "7VHBgGqnHJJjKyYJUl30V7dTFSexcUitgHcZchPZXtYD8Am8Uo";
    private static String accessToken = "71502445-JEo070hh14IC0qBcDt8CHrFe1RlQdDzG4wOcoEbBI";
    private static String accessTokenSecret = "CvKf7dpXvxHWPzVgnOq5Vj1GZCDosPxQ3p1qfSe67Ldr7";

    public static void main(String[] args) throws Exception {
       
    	LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.INFO);
       
        String remoteClusterTopologyName = null;
        if (args!=null) {
            if (args.length==1) {
                remoteClusterTopologyName = args[0];
            }
           
            // If credentials are provided as command	line arguments
            else if (args.length==4) {
                consumerKey =args[0];
                consumerSecret =args[1];
                accessToken =args[2];
                accessTokenSecret =args[3];
            }
        }

        TopologyBuilder builder = new TopologyBuilder();

        // Read
        FilterQuery tweetFilterQuery = new FilterQuery();
       
        // Filter close to San Francisco
      //  tweetFilterQuery.locations(new double[][]{new double[]{-122.75,36.8},new double[]{-121.75,37.8}});
        
        // Filter on keyword
        tweetFilterQuery.track(new String[]{"death","darkness","decease","dying","demise","ruination","afterlife","fatality","expiration","downfall","ruin",
        		"tomb","termination","grave","passing","ending","destruction","loss","extinction","eradication","extermination"});

        builder.setSpout("spout", new TwitterSpout(consumerKey, consumerSecret, accessToken, accessTokenSecret, tweetFilterQuery),1);
        builder.setBolt("file-writer", new FileWriterBolt("tweets.txt"),1).shuffleGrouping("spout");
       	builder.setBolt("sentiment", new SentimentBolt(), 4).shuffleGrouping("spout");
        builder.setBolt("hashtags", new HashtagExtractionBolt(), 4).shuffleGrouping("sentiment");
        builder.setBolt("final_users", new FileWriterBolt("users.txt")).shuffleGrouping("sentiment");
        
        Config conf = new Config();
        conf.setDebug(false);

        if (remoteClusterTopologyName!=null) {
            conf.setNumWorkers(3);

            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        }
        else {
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("twitter-fun", conf, builder.createTopology());
            
            // set time to 30 seconds.
            Utils.sleep(30000);

            cluster.shutdown();
        }
    }
}
