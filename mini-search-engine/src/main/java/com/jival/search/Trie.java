package com.jival.search;

import java.util.*;

public class Trie {
    private static class Node {
        Map<Character, Node> next = new HashMap<>();
        boolean terminal = false;
    }

    private final Node root = new Node();

    public void insert(String word) {
        Node cur = root;
        for (char ch : word.toCharArray()) {
            cur = cur.next.computeIfAbsent(ch, k -> new Node());
        }
        cur.terminal = true;
    }

    public List<String> suggest(String prefix, int k) {
        List<String> out = new ArrayList<>();
        Node node = root;
        for (char ch : prefix.toCharArray()) {
            node = node.next.get(ch);
            if (node == null) return out;
        }
        dfs(prefix, node, out, k);
        return out;
    }

    private void dfs(String prefix, Node node, List<String> out, int k) {
        if (out.size() >= k) return;
        if (node.terminal) out.add(prefix);
        for (Map.Entry<Character, Node> e : node.next.entrySet()) {
            if (out.size() >= k) break;
            dfs(prefix + e.getKey(), e.getValue(), out, k);
        }
    }
}
