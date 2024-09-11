package com.amitcul.analyze;

import java.util.List;

public class SimpleTokenizer implements Tokenizer {

    @Override
    public List<String> tokenize(String text) {
        return List.of(text.split("\\W+"));
    }
}
