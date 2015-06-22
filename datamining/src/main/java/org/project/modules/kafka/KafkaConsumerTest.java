package org.project.modules.kafka;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerTest.class);
	
	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, 
				"centos.master:9092,centos.slave1:9092,centos.slave2:9092");
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");            
		properties.put(ConsumerConfig.SESSION_TIMEOUT_MS, "1000");            
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY, "range");
//		properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY, "roundrobin");
		properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10000");  
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
				"org.apache.kafka.common.serialization.ByteArrayDeserializer");
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
				"org.apache.kafka.common.serialization.ByteArrayDeserializer");
		
		KafkaConsumer<byte[], byte[]> kafkaConsumer = new KafkaConsumer<byte[], byte[]>(properties);
		kafkaConsumer.subscribe("test");
//		kafkaConsumer.subscribe("*");
		boolean isRunning = true;            
		while(isRunning) {
			Map<String, ConsumerRecords<byte[], byte[]>> results = kafkaConsumer.poll(100);
			if (null != results) {
				for (Map.Entry<String, ConsumerRecords<byte[], byte[]>> entry : results.entrySet()) {
					LOG.info("topic {}", entry.getKey());
					ConsumerRecords<byte[], byte[]> consumerRecords = entry.getValue();
					List<ConsumerRecord<byte[], byte[]>> records = consumerRecords.records();
					for (int i = 0, len = records.size(); i < len; i++) {
						ConsumerRecord<byte[], byte[]> consumerRecord = records.get(i);
						LOG.info("topic {} partition {}", consumerRecord.topic(), consumerRecord.partition());
						try {
							LOG.info("offset {} value {}", consumerRecord.offset(), new String(consumerRecord.value()));
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		
		kafkaConsumer.close();  
		
	}

}
