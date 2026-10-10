package net.mahboub.ebankbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EbankBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankBotApplication.class, args);
    }

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(20).build();
    }

    @Bean
    OpenAiHttpClientBuilderCustomizer groqReasoningSanitizerCustomizer(ObjectMapper objectMapper) {
        return builder -> builder.interceptor(chain -> {
            Request request = chain.request();
            if (request.body() == null || !request.url().encodedPath().contains("/chat/completions")) {
                return chain.proceed(request);
            }

            try {
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                JsonNode root = objectMapper.readTree(buffer.readUtf8());
                JsonNode messages = root.get("messages");
                boolean modified = false;
                if (messages instanceof ArrayNode messagesArray) {
                    for (JsonNode message : messagesArray) {
                        if (message instanceof ObjectNode objectMessage && objectMessage.has("reasoning_content")) {
                            objectMessage.remove("reasoning_content");
                            modified = true;
                        }
                    }
                }
                if (modified) {
                    RequestBody body = RequestBody.create(
                            objectMapper.writeValueAsBytes(root), request.body().contentType());
                    request = request.newBuilder().method(request.method(), body).build();
                }
            } catch (Exception ignored) {
                // Preserve the original request if it cannot be parsed.
            }
            return chain.proceed(request);
        });
    }

}
