package com.example.mcpserver;

import com.example.mcpserver.service.AiSqlService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

class AiSqlServiceTest {

    @Test
    void fallbackProducesCityQuery() {
        AiSqlService service = new AiSqlService(new EmptyObjectProvider());
        StepVerifier.create(service.generateSql("show all customers by city"))
                .expectNext("SELECT id, first_name, last_name, email, city FROM customers ORDER BY city")
                .verifyComplete();
    }

    private static class EmptyObjectProvider implements ObjectProvider<ChatClient> {

        @Override
        public ChatClient getObject(Object... args) throws BeansException {
            throw new NoSuchBeanDefinitionException(ChatClient.class);
        }

        @Override
        public ChatClient getIfAvailable() throws BeansException {
            return null;
        }

        @Override
        public ChatClient getIfUnique() throws BeansException {
            return null;
        }

        @Override
        public ChatClient getObject() throws BeansException {
            throw new NoSuchBeanDefinitionException(ChatClient.class);
        }

        @Override
        public Stream<ChatClient> stream() {
            return Stream.empty();
        }

        @Override
        public Stream<ChatClient> orderedStream() {
            return Stream.empty();
        }

        @Override
        public Iterator<ChatClient> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public void forEach(Consumer<? super ChatClient> action) {
        }

        @Override
        public Spliterator<ChatClient> spliterator() {
            return Spliterator.<ChatClient>emptySpliterator();
        }
    }
}
