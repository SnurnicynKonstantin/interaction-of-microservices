package com.probation.example.mc4;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

public class Mc4CalculatePersonTransactionFrequency {
    public Topology createTopology(){
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Bytes> textLines = builder.stream("bank-transactions", Consumed.with(Serdes.String(), Serdes.Bytes()));
        KTable<String, Long> wordCounts = textLines
                .mapValues(v -> 1L)
                .groupByKey(Serialized.with(Serdes.String(), Serdes.Long()))
                .count();

        wordCounts.toStream().to("person-transaction-frequency", Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-person-transaction-frequency-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        Mc4CalculatePersonTransactionFrequency wordCountApp = new Mc4CalculatePersonTransactionFrequency();

        KafkaStreams streams = new KafkaStreams(wordCountApp.createTopology(), config);
        streams.start();

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
