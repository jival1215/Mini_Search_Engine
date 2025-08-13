package com.jival.search;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SearchEngine {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java -jar mini-search-engine.jar <docs-folder>");
            System.exit(1);
        }
        Path folder = Paths.get(args[0]);
        if (!Files.isDirectory(folder)) {
            System.err.println("Not a folder: " + folder);
            System.exit(1);
        }

        // Load & index
        Tokenizer tok = new Tokenizer();
        Trie trie = new Trie();
        InvertedIndex index = new InvertedIndex();

        List<String> docPaths = new ArrayList<>();
        int docId = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.txt")) {
            for (Path p : stream) {
                String text = Files.readString(p);
                List<String> tokens = tok.tokenize(text);
                index.addDocument(docId, tokens);
                for (String t : tokens) trie.insert(t);
                docPaths.add(p.toString());
                docId++;
            }
        }

        Ranker ranker = new Ranker(index);
        System.out.println("Indexed docs=" + index.getNumDocs() + ", unique terms=" + index.terms().size());
        long t0 = System.nanoTime();
        ranker.rank(tok.tokenize("heart disease"), 10);
        long ms = Math.round((System.nanoTime() - t0) / 1e6);
        System.out.println("Query latency=" + ms + " ms");
        System.out.println("Commands: 'auto <prefix>' | 'search <terms>' | 'exit'");

        // REPL
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String line = br.readLine();
            if (line == null) break;
            line = line.trim();
            if (line.equalsIgnoreCase("exit")) break;
            if (line.startsWith("auto ")) {
                String prefix = line.substring(5).trim().toLowerCase(Locale.ROOT);
                List<String> suggestions = trie.suggest(prefix, 10);
                System.out.println("Suggestions: " + suggestions);
            } else if (line.startsWith("search ")) {
                String q = line.substring(7);
                List<String> qTokens = tok.tokenize(q);
                List<Ranker.Result> results = ranker.rank(qTokens, 10);
                for (Ranker.Result r : results) {
                    System.out.printf(Locale.ROOT, "%.4f  %s%n", r.score, docPaths.get(r.docId));
                }
            } else {
                System.out.println("Unknown command. Try 'auto <prefix>' or 'search <terms>'.");
            }
        }
        System.out.println("Goodbye!");
    }
}
