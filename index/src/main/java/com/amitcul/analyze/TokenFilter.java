package com.amitcul.analyze;

import java.util.List;

public interface TokenFilter {
    List<String> apply(List<String> tokens);
}
