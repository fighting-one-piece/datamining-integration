package org.project.modules.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducerTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerTest.class);
	
	private static Properties properties = null;
	
	static {
		properties = new Properties();
		properties.put("bootstrap.servers", "centos.master:9092,centos.slave1:9092,centos.slave2:9092");
		properties.put("producer.type", "sync");
		properties.put("request.required.acks", "1");
		properties.put("serializer.class", "kafka.serializer.DefaultEncoder");
		properties.put("partitioner.class", "kafka.producer.DefaultPartitioner");
		properties.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
//		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
//		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	}
	
	public void produce() {
		KafkaProducer<byte[], byte[]> kafkaProducer = new KafkaProducer<byte[],byte[]>(properties);
		ProducerRecord<byte[],byte[]> kafkaRecord = new ProducerRecord<byte[],byte[]>(
				"test", "kkk".getBytes(), "vvv".getBytes());
		kafkaProducer.send(kafkaRecord, new Callback() {
			public void onCompletion(RecordMetadata metadata, Exception e) {
				if(null != e) {
					LOG.info("the offset of the send record is {}", metadata.offset());
					LOG.error(e.getMessage(), e);
				}
				LOG.info("complete!");
			}
		});
		kafkaProducer.close();
	}

	public static void main(String[] args) {
		KafkaProducerTest kafkaProducerTest = new KafkaProducerTest();
		for (int i = 0; i < 10; i++) {
			kafkaProducerTest.produce();
		}
	}
}
