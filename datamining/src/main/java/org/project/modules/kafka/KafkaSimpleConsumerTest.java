package org.project.modules.kafka;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;

public class KafkaSimpleConsumerTest {
	
	private List<String> borkerList = new ArrayList<String>();  
	  
    public KafkaSimpleConsumerTest() {  
        borkerList = new ArrayList<String>();  
    }  
  
    public static void main(String args[]) {  
        KafkaSimpleConsumerTest kafkaSimpleConsumer = new KafkaSimpleConsumerTest();  
        // 最大读取消息数量  
        long maxReadNum = Long.parseLong("3");  
        // 订阅的topic  
        String topic = "test";  
        // 查找的分区  
        int partition = Integer.parseInt("0");  
        // broker节点
        List<String> seeds = new ArrayList<String>();  
        seeds.add("centos.master");  
        seeds.add("centos.slave1");  
        seeds.add("centos.slave2");  
        // 端口  
        int port = Integer.parseInt("9092");  
        try {  
            kafkaSimpleConsumer.run(maxReadNum, topic, partition, seeds, port);  
        } catch (Exception e) {  
            System.out.println("Oops:" + e);  
            e.printStackTrace();  
        }  
    }  
  
    public void run(long maxReadNum, String topic, int partition, List<String> seedBrokers, int port) throws Exception {  
        // 获取指定topic partition的元数据  
        PartitionMetadata metadata = findLeader(seedBrokers, port, topic, partition);  
        if (metadata == null) {  
            System.out.println("can't find metadata for topic and partition. exit");  
            return;  
        }  
        if (metadata.leader() == null) {  
            System.out.println("can't find leader for topic and partition. exit");  
            return;  
        }  
        String leadBroker = metadata.leader().host();  
        String clientName = "client_" + topic + "_" + partition;  
  
        SimpleConsumer consumer = new SimpleConsumer(leadBroker, port, 100000, 64 * 1024, clientName);  
        long readOffset = getLastOffset(consumer, topic, partition, kafka.api.OffsetRequest.EarliestTime(), clientName);  
        int numErrors = 0;  
        while (maxReadNum > 0) {  
            if (consumer == null) {  
                consumer = new SimpleConsumer(leadBroker, port, 100000, 64 * 1024, clientName);  
            }  
            FetchRequest req = new FetchRequestBuilder().clientId(clientName).addFetch(topic, partition, readOffset, 100000).build();  
            FetchResponse fetchResponse = consumer.fetch(req);  
  
            if (fetchResponse.hasError()) {  
                numErrors++;  
                short code = fetchResponse.errorCode(topic, partition);  
                System.out.println("error fetching data from the broker:" + leadBroker + " reason: " + code);  
                if (numErrors > 5)  
                    break;  
                if (code == ErrorMapping.OffsetOutOfRangeCode()) {  
                    readOffset = getLastOffset(consumer, topic, partition, kafka.api.OffsetRequest.LatestTime(), clientName);  
                    continue;  
                }  
                consumer.close();  
                consumer = null;  
                leadBroker = findNewLeader(leadBroker, topic, partition, port);  
                continue;  
            }  
            numErrors = 0;  
  
            long numRead = 0;  
            for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(topic, partition)) {  
                long currentOffset = messageAndOffset.offset();  
                if (currentOffset < readOffset) {  
                    System.out.println("found an old offset: " + currentOffset + " expecting: " + readOffset);  
                    continue;  
                }  
  
                readOffset = messageAndOffset.nextOffset();  
                ByteBuffer payload = messageAndOffset.message().payload();  
  
                byte[] bytes = new byte[payload.limit()];  
                payload.get(bytes);  
                System.out.println(String.valueOf(messageAndOffset.offset()) + ": " + new String(bytes, "UTF-8"));  
                numRead++;  
                maxReadNum--;  
            }  
  
            if (numRead == 0) {  
                try {  
                    Thread.sleep(1000);  
                } catch (InterruptedException ie) {  
                }  
            }  
        }  
        if (consumer != null)  
            consumer.close();  
    }  
   
    /**
     * 从活跃的Broker列表中找出指定Topic、Partition中的Leader Broker
     * @param seedBrokers
     * @param port
     * @param topic
     * @param partition
     * @return
     */
    private PartitionMetadata findLeader(List<String> seedBrokers, int port, String topic, int partition) {  
        PartitionMetadata partitionMetadata = null;  
        loop: for (String seedBroker : seedBrokers) {  
            SimpleConsumer consumer = null;  
            try {  
                consumer = new SimpleConsumer(seedBroker, port, 100000, 64 * 1024, "leaderLookup");  
                List<String> topics = Collections.singletonList(topic);  
                TopicMetadataRequest topicMetadataRequest = new TopicMetadataRequest(topics);  
                TopicMetadataResponse topicMetadataResponse = consumer.send(topicMetadataRequest);  
  
                List<TopicMetadata> topicMetadatas = topicMetadataResponse.topicsMetadata();  
                for (TopicMetadata topicMetadata : topicMetadatas) {  
                    for (PartitionMetadata pMetadata : topicMetadata.partitionsMetadata()) {  
                        if (pMetadata.partitionId() == partition) {  
                            partitionMetadata = pMetadata;  
                            break loop;  
                        }  
                    }  
                }  
            } catch (Exception e) {  
                System.out.println("error communicating with broker [" + seedBroker + "] to find leader for [" + topic + ", " + partition + "] reason: " + e);  
            } finally {  
                if (consumer != null)  
                    consumer.close();  
            }  
        }  
        if (partitionMetadata != null) {  
            borkerList.clear();  
            for (Broker replica : partitionMetadata.replicas()) {  
                borkerList.add(replica.host());  
            }  
        }  
        return partitionMetadata;  
    }  
  
    public static long getLastOffset(SimpleConsumer consumer, String topic, int partition, long whichTime, String clientName) {  
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);  
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();  
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));  
        OffsetRequest request = new OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);  
        OffsetResponse response = consumer.getOffsetsBefore(request);  
        if (response.hasError()) {  
            System.out.println("error fetching data offset data the broker. reason: " + response.errorCode(topic, partition));  
            return 0;  
        }  
        long[] offsets = response.offsets(topic, partition);  
        return offsets[0];  
    }  
  
    private String findNewLeader(String oldLeader, String topic, int partition, int port) throws Exception {  
        for (int i = 0; i < 3; i++) {  
            boolean goToSleep = false;  
            PartitionMetadata metadata = findLeader(borkerList, port, topic, partition);  
            if (metadata == null) {  
                goToSleep = true;  
            } else if (metadata.leader() == null) {  
                goToSleep = true;  
            } else if (oldLeader.equalsIgnoreCase(metadata.leader().host()) && i == 0) {  
                goToSleep = true;  
            } else {  
                return metadata.leader().host();  
            }  
            if (goToSleep) {  
                try {  
                    Thread.sleep(1000);  
                } catch (InterruptedException ie) {  
                }  
            }  
        }  
        System.out.println("unable to find new leader after broker failure. exit");  
        throw new Exception("unable to find new leader after broker failure. exit");  
    }  
  
}  
