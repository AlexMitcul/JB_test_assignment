package com.amitcul.analyze;

import java.util.List;

public class Analyzer {
    private final Tokenizer tokenizer;
    private final List<TokenFilter> filters;

    public Analyzer(Tokenizer tokenizer, List<TokenFilter> filters) {
        this.tokenizer = tokenizer;
        this.filters = filters;
    }

    public List<String> analyze(String text) {
        List<String> stream = tokenizer.tokenize(text);

        for (TokenFilter filter : filters) {
            stream = filter.apply(stream);
        }

        return stream;
    }
}
