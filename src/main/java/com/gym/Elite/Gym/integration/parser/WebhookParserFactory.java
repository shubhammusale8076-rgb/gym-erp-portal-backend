package com.gym.Elite.Gym.integration.parser;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebhookParserFactory {

    private final Map<String, WebhookParser> parserMap = new HashMap<>();

    public WebhookParserFactory(List<WebhookParser> parsers) {
        for (WebhookParser parser : parsers) {
            parserMap.put(parser.getProvider(), parser);
        }
    }

    public WebhookParser getParser(String provider) {
        WebhookParser parser = parserMap.get(provider.toLowerCase());

        if (parser == null) {
            throw new RuntimeException("No parser found for provider: " + provider);
        }

        return parser;
    }
}
