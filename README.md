# Mini Search Engine (Java)

A lightweight, educational search engine implemented in Java. It indexes a folder of text files and supports:

- `search <terms>` — returns the top-k ranked documents using TF–IDF + cosine similarity
- `auto <prefix>` — trie‑based autocomplete for quick term suggestions
- `exit` — quit the REPL

This project is designed to demonstrate core information retrieval concepts (tokenization, inverted indexes, ranking) with clean, modular OOP.

---

## Features

- Tokenization with stopword removal
- Inverted index with per‑document term frequencies
- TF–IDF weighting (log‑scaled TF, smoothed IDF) and cosine similarity
- Trie‑based autocomplete
- Simple interactive CLI (REPL)
- Small, readable codebase

---

## Architecture

```
com.jival.search
├── Tokenizer.java        # Split text, normalize, remove stopwords
├── Trie.java             # Prefix tree for autocomplete
├── InvertedIndex.java    # term -> list of (docId, tf), doc lengths
├── Ranker.java           # TF–IDF + cosine similarity ranking
└── SearchEngine.java     # Wiring, indexing, REPL (auto/search/exit)
```

- **Tokenizer** — lowercases input, splits on non‑alphanumerics, filters stopwords
- **Trie** — inserts tokens and returns top‑k completions for a prefix
- **InvertedIndex** — stores postings (docId, tf) and doc statistics
- **Ranker** — computes query vector and document vectors, then cosine similarity
- **SearchEngine** — orchestrates indexing, prints usage, and handles the interactive loop

---

## Quick Start

### Requirements
- Java 17+
- Maven 3.8+

### Clone & Build
```bash
git clone <your-repo-url>.git
cd mini-search-engine
mvn -q clean package
```

### Prepare a corpus
Put a few `.txt` files in a folder, e.g. `data/docs/`:
```
data/
└── docs/
    ├── doc1.txt
    ├── doc2.txt
    └── doc3.txt
```

### Run (two options)

**A) Run via Maven (exec plugin)**
```bash
mvn -q exec:java -Dexec.mainClass=com.jival.search.SearchEngine -Dexec.args="data/docs"
```

**B) Run from the built JAR**  
If your build produces a runnable JAR (via shade/assembly), run:
```bash
java -jar target/mini-search-engine.jar data/docs
```
If your artifact name differs, replace it with the built JAR path in `target/`.

---

## Usage

Inside the REPL:
```
Commands: 'auto <prefix>' | 'search <terms>' | 'exit'

> auto hea
Suggestions: [heart, health, healthy, …]

> search heart disease
0.8123  /absolute/path/to/data/docs/doc2.txt
0.6115  /absolute/path/to/data/docs/doc1.txt
0.4027  /absolute/path/to/data/docs/doc3.txt

> exit
Goodbye!
```

---

## Performance (sample, local corpus)

- Indexed docs: **3**
- Unique terms: **28** (after tokenization and stopword removal)
- Query latency: **~1 ms** for the query `"heart disease"`

How it’s measured:
- Corpus stats via `index.getNumDocs()` and `index.terms().size()`
- Latency via `System.nanoTime()` and `Math.round((System.nanoTime() - t0) / 1e6)`

> Note: These numbers reflect a tiny local corpus for demo purposes. Indexing more files (e.g., ~1k `.txt` docs) will give more meaningful metrics.

---

## Testing

If you add tests under `src/test/java`, run:
```bash
mvn -q test
```

---

## Extending

- Add stemming/lemmatization to Tokenizer
- Add phrase queries (bi/tri-grams)
- Persist the index to disk (serialize/load)
- Add a REST API (Spring Boot or JAX‑RS) or a simple web UI
- Evaluate relevance (precision@k / NDCG@k) on a small labeled set

---

