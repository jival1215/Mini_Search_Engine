package com.jival.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class BasicTests {

    @Test
    public void testTokenizer() {
        Tokenizer t = new Tokenizer();
        List<String> toks = t.tokenize("The quick, brown fox jumps over the lazy dog.");
        assertTrue(toks.contains("quick"));
        assertFalse(toks.contains("the"));
    }

    @Test
    public void testTrieSuggest() {
        Trie trie = new Trie();
        trie.insert("hello");
        trie.insert("help");
        trie.insert("helmet");
        List<String> s = trie.suggest("hel", 3);
        assertTrue(s.size() >= 2);
    }

    @Test
    public void testIndexAndRank() {
        InvertedIndex idx = new InvertedIndex();
        Tokenizer tok = new Tokenizer();
        idx.addDocument(0, tok.tokenize("heart disease risk factors include cholesterol and age"));
        idx.addDocument(1, tok.tokenize("machine learning predicts heart disease"));
        Ranker ranker = new Ranker(idx);
        List<Ranker.Result> res = ranker.rank(tok.tokenize("heart disease"), 5);
        assertTrue(res.size() > 0);
        assertTrue(res.get(0).score >= res.get(res.size()-1).score);
    }
}
