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
    void fallbackProducesContractQuery() {
        AiSqlService service = new AiSqlService(new EmptyObjectProvider());
        StepVerifier.create(service.generateSql("查询所有合同"))
                .expectNext("SELECT contract_order_number, contract_name, contract_party_a, contract_party_b, contract_amount_cny, signing_date, province, region FROM ed_cdi_contract ORDER BY signing_date DESC NULLS LAST LIMIT 100")
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
