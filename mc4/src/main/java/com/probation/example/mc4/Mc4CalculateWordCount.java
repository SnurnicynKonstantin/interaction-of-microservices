package com.probation.example.mc4;

import java.util.Properties;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

public class Mc4CalculateWordCount {

    public Topology createTopology(){
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream("word-count-input");
        KTable<String, Long> wordCounts = textLines
                .mapValues(textLine -> textLine.toLowerCase())
                .flatMapValues(textLine -> Arrays.asList(textLine.split("\\W+")))
                .selectKey((key, word) -> word)
                .groupByKey()
                .count(Materialized.as("Counts"));

        wordCounts.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        Mc4CalculateWordCount wordCountApp = new Mc4CalculateWordCount();

        KafkaStreams streams = new KafkaStreams(wordCountApp.createTopology(), config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
