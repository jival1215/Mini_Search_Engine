package com.jival.search;

import java.util.*;

public class InvertedIndex {
    public static class Posting {
        public final int docId;
        public final int tf;
        public Posting(int docId, int tf) { this.docId = docId; this.tf = tf; }
    }

    private final Map<String, List<Posting>> index = new HashMap<>();
    private final Map<Integer, Integer> docLengths = new HashMap<>();
    private int numDocs = 0;

    public int getNumDocs() { return numDocs; }

    public void addDocument(int docId, List<String> tokens) {
        numDocs = Math.max(numDocs, docId + 1);
        Map<String, Integer> tfMap = new HashMap<>();
        for (String t : tokens) {
            tfMap.put(t, tfMap.getOrDefault(t, 0) + 1);
        }
        docLengths.put(docId, tokens.size());
        for (Map.Entry<String, Integer> e : tfMap.entrySet()) {
            String term = e.getKey();
            int tf = e.getValue();
            index.computeIfAbsent(term, k -> new ArrayList<>()).add(new Posting(docId, tf));
        }
    }

    public List<Posting> getPostings(String term) {
        return index.getOrDefault(term, Collections.emptyList());
    }

    public int df(String term) {
        return index.getOrDefault(term, Collections.emptyList()).size();
    }

    public Set<String> terms() { return index.keySet(); }
}
