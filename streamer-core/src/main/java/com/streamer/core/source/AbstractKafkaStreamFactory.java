package com.streamer.core.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streamer.core.message.KafkaMessage;
import com.streamer.core.support.Signal;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.consumer.TopicFilter;
import kafka.consumer.Whitelist;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public abstract class AbstractKafkaStreamFactory {

	private Logger logger = LoggerFactory.getLogger(getClass());

	protected ConsumerConnector consumer;

	public abstract void onReceive(List<KafkaMessage> messages);

	protected void start(String token, String zookeeper, String topic, String group, String offset, int batch) {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeper);
		props.put("group.id", group);
		props.put("auto.commit.enable", "false");
		props.put("auto.offset.reset", (offset.equalsIgnoreCase("smallest") ? "smallest" : "largest"));
		props.put("zookeeper.session.timeout.ms", "100000");
		props.put("rebalance.backoff.ms", "20000");
		props.put("rebalance.max.retries", "10");
		props.put("zookeeper.connection.timeout.ms", "100000");

		ConsumerConfig config = new ConsumerConfig(props);

		consumer = Consumer.createJavaConsumerConnector(config);

		TopicFilter topicFilter = new Whitelist(topic.replaceAll(",", "|"));

		List<KafkaStream<byte[], byte[]>> streams = consumer.createMessageStreamsByFilter(topicFilter);

		ExecutorService executor = new ThreadPoolExecutor(0, streams.size(), 0, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		for (KafkaStream<byte[], byte[]> kafkaStream : streams) {

			executor.execute(new Runnable() {

				public void run() {

					if (Signal.map.get(token) != null && Signal.map.get(token).get() > 0) {

						ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
						List<KafkaMessage> messages = new ArrayList<KafkaMessage>();

						while (iterator.hasNext()) {

							if (Signal.map.get(token) != null && Signal.map.get(token).get() > 0) {

								if (messages.size() == batch) {

									if (Signal.map.get(token) != null) {
										onReceive(messages);
										messages.clear();
										Signal.map.get(token).decrementAndGet();
									} else {
										logger.warn("Could not found token:{} in Signal map, The Signal:{}", token,
												Signal.map);
									}
								} else {

									MessageAndMetadata<byte[], byte[]> metadata = iterator.next();

									KafkaMessage msg = new KafkaMessage();
									msg.setOffset(metadata.offset());
									msg.setTopic(metadata.topic());
									msg.setPartition(metadata.partition());
									msg.setTimestamp(metadata.timestamp());
									msg.setKey((metadata.key() == null) ? null : new String(metadata.key()));
									msg.setValue(new String(metadata.message()));

									messages.add(msg);
								}

							} else {
								logger.info("The kafka consumer will be shutdown...");
								consumer.shutdown();
								break;
							}
						}
					} else {
						logger.info("The kafka consumer will be shutdown...");
						consumer.shutdown();
					}
				}
			});
		}

	}

	protected void commit(String token) {
		if (consumer != null) {
			if (Signal.map.get(token) != null) {
				consumer.commitOffsets(true);
				Signal.map.get(token).incrementAndGet();
			} else {
				logger.warn("Could not found token:{} in Signal map, The Signal:{}", token, Signal.map);
				logger.info("The kafka consumer will be shutdown...");
				consumer.shutdown();
			}
		} else {
			logger.warn("The kafka consumer is null !");
		}
	}
}
