package com.jival.search;

import java.util.*;

public class Ranker {
    private final InvertedIndex index;

    public Ranker(InvertedIndex index) {
        this.index = index;
    }

    private double idf(String term) {
        int df = index.df(term);
        if (df == 0) return 0.0;
        return Math.log((index.getNumDocs() + 1.0) / (df + 1.0)) + 1.0; // smooth idf
    }

    public List<Result> rank(List<String> queryTokens, int topK) {
        Map<Integer, Double> docScores = new HashMap<>();
        Map<String, Double> qtf = new HashMap<>();
        for (String q : queryTokens) qtf.put(q, qtf.getOrDefault(q, 0.0) + 1.0);

        // Build query vector magnitudes
        Map<String, Double> qWeights = new HashMap<>();
        double qNormSq = 0.0;
        for (Map.Entry<String, Double> e : qtf.entrySet()) {
            double w = (1.0 + Math.log(e.getValue())) * idf(e.getKey());
            qWeights.put(e.getKey(), w);
            qNormSq += w * w;
        }
        double qNorm = Math.sqrt(qNormSq) + 1e-12;

        // Accumulate doc dot-products
        for (String term : qWeights.keySet()) {
            double qW = qWeights.get(term);
            for (InvertedIndex.Posting p : index.getPostings(term)) {
                double dW = (1.0 + Math.log(p.tf)) * idf(term);
                docScores.put(p.docId, docScores.getOrDefault(p.docId, 0.0) + qW * dW);
            }
        }

        // Convert to cosine similarity by dividing by norms (doc norm approximated by sum of squared weights)
        Map<Integer, Double> docNorms = new HashMap<>();
        for (String term : index.terms()) {
            double idf = idf(term);
            for (InvertedIndex.Posting p : index.getPostings(term)) {
                double w = (1.0 + Math.log(p.tf)) * idf;
                docNorms.put(p.docId, docNorms.getOrDefault(p.docId, 0.0) + w * w);
            }
        }

        List<Result> results = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : docScores.entrySet()) {
            int docId = e.getKey();
            double dot = e.getValue();
            double dNorm = Math.sqrt(docNorms.getOrDefault(docId, 1e-12)) + 1e-12;
            double cos = dot / (qNorm * dNorm);
            results.add(new Result(docId, cos));
        }
        results.sort((a,b) -> Double.compare(b.score, a.score));
        if (results.size() > topK) results = results.subList(0, topK);
        return results;
    }

    public static class Result {
        public final int docId;
        public final double score;
        public Result(int docId, double score) { this.docId = docId; this.score = score; }
    }
}
