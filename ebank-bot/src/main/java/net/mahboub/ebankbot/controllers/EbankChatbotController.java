package net.mahboub.ebankbot.controllers;

import net.mahboub.ebankbot.agents.EbankAIAgent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

@RestController
public class EbankChatbotController {
    private EbankAIAgent ebankAIAgent;

    public EbankChatbotController(EbankAIAgent ebankAIAgent) {
        this.ebankAIAgent = ebankAIAgent;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(
            @RequestParam(name = "query", defaultValue = "Bonjour") String query) {
        return ebankAIAgent.chat(new Prompt(query));
    }

    @GetMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(
            @RequestParam(name = "query", defaultValue = "Bonjour") String query) {
        return ebankAIAgent.chatStream(new Prompt(query));
    }
}
