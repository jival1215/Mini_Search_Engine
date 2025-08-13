package com.jival.search;

import java.util.*;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final Pattern SPLIT = Pattern.compile("[^a-zA-Z0-9]+");
    private final Set<String> stop;

    public Tokenizer() {
        this.stop = new HashSet<>(Arrays.asList(
                "a","an","and","are","as","at","be","but","by","for","if","in","into","is","it",
                "no","not","of","on","or","such","that","the","their","then","there","these",
                "they","this","to","was","will","with","from","were","you","your","we","our"
        ));
    }

    public List<String> tokenize(String text) {
        if (text == null) return Collections.emptyList();
        String lower = text.toLowerCase(Locale.ROOT);
        String[] raw = SPLIT.split(lower);
        List<String> out = new ArrayList<>();
        for (String t : raw) {
            if (t.isEmpty()) continue;
            if (stop.contains(t)) continue;
            out.add(t);
        }
        return out;
    }
}
