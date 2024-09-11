package com.amitcul.analyze;

import java.util.List;
import java.util.stream.Collectors;

public class LowerCaseFilter implements TokenFilter {
    @Override
    public List<String> apply(List<String> tokens) {
        return tokens.stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
