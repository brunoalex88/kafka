package com.obruno.service;

import com.obruno.avro.MessageAvro;
import com.obruno.model.Message;
import io.confluent.kafka.serializers.*;
import lombok.extern.java.Log;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;

@Service
@Log
public class MessageService {
    public static final String TOPIC = "queue.message.created";

    public void produce(Message message) {
        Producer<Long, Message> producer = createProducer();
        producer.send(new ProducerRecord(TOPIC, message.getId(), buildMessage(message)), (metadata, exception) -> {
            if (exception != null) {
                log.warning(String.format("Erro ao produzir mensagem: %s", exception.getLocalizedMessage()));
            } else {
                log.info(String.format("Puta que pariu...foiii: %s", message));
            }
        });
        producer.flush();
        producer.close();
    }

    public Message consume() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "MessageConsumer");

        KafkaConsumer<Long, Message> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(MessageService.TOPIC));
        ConsumerRecords<Long, Message> records = consumer.poll(100);

        for (ConsumerRecord<Long, Message> record: records) {
            log.info(String.format("Key: %s\nValue: %s", record.key(), record.value()));

            Message message = new Message(record.value().getId(), record.value().getText());
            return message;
        }
        return null;
    }

    private MessageAvro buildMessage(Message message) {
        return MessageAvro.newBuilder().setId(message.getId()).setText(message.getText()).build();
    }

    private static Producer<Long, Message> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "AvroProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        return new KafkaProducer<>(props);
    }

}
