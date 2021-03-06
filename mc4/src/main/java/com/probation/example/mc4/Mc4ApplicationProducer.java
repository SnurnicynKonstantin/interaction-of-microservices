package com.probation.example.mc4;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Mc4ApplicationProducer {

	public static void main(String[] args) {
		Properties properties = new Properties();

		// kafka bootstrap server
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		// producer acks
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

		Producer<String, String> producer = new KafkaProducer<>(properties);

		int i = 0;
		while (true) {
			System.out.println("Producing batch: " + i);
			try {
				producer.send(newRandomTransaction(getName()));
				Thread.sleep(1000);
				i += 1;
			} catch (InterruptedException e) {
				break;
			}
		}
		producer.close();
	}

	public static ProducerRecord<String, String> newRandomTransaction(String name) {
		// creates an empty json {}
		ObjectNode transaction = JsonNodeFactory.instance.objectNode();

		// { "amount" : 46 } (46 is a random number between 0 and 100 excluded)
		Integer amount = ThreadLocalRandom.current().nextInt(0, 100);
		Instant now = Instant.now();

		transaction.put("name", name);
		transaction.put("amount", amount);
		transaction.put("time", now.toString());
		return new ProducerRecord<>("bank-transactions", name, transaction.toString());
	}

	private static String getName() {
		String[] arrOfNames = {"Mark", "John", "Alice", "Bob", "Alfred", "Susan"};
		int randIdx = ThreadLocalRandom.current().nextInt(arrOfNames.length);
		return arrOfNames[randIdx];
	}

}
