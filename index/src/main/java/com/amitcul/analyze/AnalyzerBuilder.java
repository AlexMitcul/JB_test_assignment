package com.amitcul.analyze;

import java.util.ArrayList;
import java.util.List;

public class AnalyzerBuilder {
    private Tokenizer tokenizer;
    private final List<TokenFilter> filters = new ArrayList<>();

    public AnalyzerBuilder setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        return this;
    }

    public AnalyzerBuilder addFilter(TokenFilter filter) {
        filters.add(filter);
        return this;
    }

    public Analyzer build() {
        return new Analyzer(tokenizer, filters);
    }
}
