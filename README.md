# CIT 5940 Final Project - Tech News Search Engine

## Part 1: Usage Instructions

### Compilation

From the project root directory, trigger command below to generate `target/FinalProject5940-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

```bash
mvn clean package
```
#### Execution: Run with Maven exec plugin (recommended)
```bash
mvn exec:java -Dexec.mainClass="edu.upenn.cit5940.Main"
# with arguments:
mvn exec:java -Dexec.mainClass="edu.upenn.cit5940.Main" -Dexec.args="data/articles.csv logs/app.log"
```

#### Run the JAR
```bash
java -jar target/FinalProject5940-0.0.1-SNAPSHOT-jar-with-dependencies.jar
java -jar target/FinalProject5940-0.0.1-SNAPSHOT-jar-with-dependencies.jar path/to/data.json
java -jar target/FinalProject5940-0.0.1-SNAPSHOT-jar-with-dependencies.jar path/to/data.json logs/my_run.log
```

**Command-line arguments (both optional):**
- **Arg 1  Data file:** Path to a .csv or .json articles file. Defaults to articles_small.csv in the working directory.
- **Arg 2  Log file:** Path for the log file. Defaults to tech_news_search.log in the working directory.

## Part 2: System Design

### System Architecture

The application follows a strict **N-Tier Architecture** with four logical layers:

| Tier | Package | Responsibility |
|------|---------|----------------|
| **Presentation** | `edu.upenn.cit5940.ui` | All console I/O (menus, prompts, output) |
| **Application/Logic** | `edu.upenn.cit5940.processor` | Business logic: search, autocomplete, trends, date filtering |
| **Data Management** | `edu.upenn.cit5940.datamanagement` | File parsing, in-memory data stores, all data structures |
| **Shared (DTOs)** | `edu.upenn.cit5940.common.dto` | Plain `Article` POJO used by all tiers |
| **Logging** | `edu.upenn.cit5940.logging` | Singleton logger, available to all tiers |

**Dependency rules enforced:**
- `UI` calls `Processor` only (never `DataManagement` directly)
- `Processor` uses `DataManagement` classes (never calls back up to UI)
- Dependencies flow strictly downward
- All inter-tier wiring is done in `Main.java` via **constructor injection** (dependency injection), so no tier creates objects from another tier internally

---

### Data Structures & Refactoring

#### HW6  Inverted Index (HashMap, O(1) lookup)

The original BST-based inverted index was refactored to use a `HashMap<String, Set<Article>>`.

**Performance benefit:** A BST gives O(log n) average lookup time, while `HashMap` gives **O(1) average** lookup. For a large corpus this is a critical improvement, since every search query triggers multiple index lookups. The `searchAll` method intersects result sets across all query tokens, so fast per-token lookup directly reduces total latency.

Stop words (loaded from `stop_words.txt`) and tokens of length <= 1 are filtered at index-build time. This shrinks the index and improves search precision.

#### HW8  Trie (Autocomplete)

`CustomTrie` stores all words from article titles. The `getWordsWithPrefix(prefix, limit)` method navigates to the prefix node, then performs a DFS to collect at most `limit` (10) completions. This gives **O(p + k)** time for a prefix of length `p` returning `k` results, far faster than scanning all titles.

#### HW8  TreeMap (Date range browsing)

`ArticleDateService` uses a `TreeMap<LocalDate, List<Article>>`. Date-range queries use `subMap(startDate, true, endDate, true)`, which runs in **O(log n + k)** time where k is the number of matching dates. This is much faster than a linear scan through all articles.

#### Heap (Priority Queue)  Top topics

`TopicService.getTopTopics()` builds a word-frequency map for a given period, then drains a max-heap (`PriorityQueue` with reverse comparator) to extract the top 10 in **O(n log 10) = O(n)** time. Using a heap avoids full sorting of the frequency map.

#### TreeMap  Trends

`TopicService` pre-builds a `TreeMap<String, Map<String, Integer>>` mapping each `YYYY-MM` period to its word frequencies at construction time. `getTrends()` then does a range scan over this TreeMap in **O(log n + k)** per query, which is fast for repeated trend queries.

---

### Design Patterns

#### 1. Singleton  Logger

**Class:** `edu.upenn.cit5940.logging.Logger`

**Why:** The logger must be a single shared instance across the entire application. Creating multiple loggers would result in race conditions, garbled log output, and resource leaks. The Singleton pattern guarantees exactly one `Logger` exists for the lifetime of the application.

**Implementation:**
```java
public class Logger {
    private static Logger instance;  // single instance

    private Logger(String filePath) throws IOException {
        this.writer = new PrintWriter(new FileWriter(filePath, true));
    }

    public static Logger getInstance(String filePath) throws IOException {
        if (instance == null) {
            instance = new Logger(filePath);  // lazy initialization
        }
        return instance;
    }
}
```

All tiers receive the same `Logger` instance injected via constructors.

---

#### 2. Strategy  Parser (CSV vs. JSON)

**Classes:** `Parser` (interface), `CSVParser`, `JSONParser`, `ParserFactory`

**Why:** The application must support both `.csv` and `.json` data files. The Strategy pattern lets us swap parsing algorithms at runtime without changing the calling code in `Main`. Adding a new format (e.g., XML) in the future only requires adding a new `Parser` implementation.

**Implementation:**
```java
// Parser.java — the Strategy interface
public interface Parser {
    List<Article> parse(String filePath);
}

// CSVParser.java — concrete strategy A
public class CSVParser implements Parser {
    @Override
    public List<Article> parse(String filePath) { /* FSM-based CSV parsing */ }
}

// JSONParser.java — concrete strategy B
public class JSONParser implements Parser {
    @Override
    public List<Article> parse(String filePath) { /* Gson-based JSON parsing */ }
}
```

```java
// Main.java — caller works only against the interface, unaware of the concrete type
Parser parser = parserFactory.getParser(dataFile);
List<Article> articles = parser.parse(dataFile);
```

`CSVParser` wraps the custom finite-state machine `ArticleCSVParser` from the Solo Project. `JSONParser` uses the Gson library to deserialize JSON records into `Article` objects.

---

#### 3. Factory  ParserFactory

**Class:** `edu.upenn.cit5940.datamanagement.ParserFactory`

**Why:** `Main.java` needs a `Parser` but should not need to know which concrete type to instantiate that decision depends on the file extension of the data file provided at runtime. The Factory pattern centralizes this creation logic in one place. If a new file format is ever added (e.g., `.xml`), only `ParserFactory` needs to change; `Main` and all other callers remain untouched.

**Implementation:**
```java
public class ParserFactory {

    private final Logger logger;

    public ParserFactory(Logger logger) {
        this.logger = logger;
    }

    public Parser getParser(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Data file path cannot be null or empty.");
        }

        String lower = filePath.toLowerCase();

        if (lower.endsWith(".csv")) {
            return new CSVParser(logger);   // concrete product A
        }

        if (lower.endsWith(".json")) {
            return new JSONParser(logger);  // concrete product B
        }

        throw new IllegalArgumentException("Unsupported file format. Use .csv or .json");
    }
}
```

**How it is used in `Main.java`:**
```java
ParserFactory parserFactory = new ParserFactory(logger);
Parser parser = parserFactory.getParser(dataFile);  // factory decides CSV or JSON
List<Article> articles = parser.parse(dataFile);    // caller only sees the interface
```

The Factory pattern works hand-in-hand with the Strategy pattern here: the Factory *creates* the right strategy, and the Strategy *executes* it.

---

### Challenges Faced

**Graceful handling of malformed CSV records:** The original `processRecord` method threw `IllegalArgumentException` on short records, which would crash the entire load. The fix was to silently skip records that are missing essential fields (URI or title) and log a warning, while continuing to process the rest of the file.

**JSON parsing without a library:** Maven could not resolve external dependencies in the sandbox environment, so a custom hand-written JSON parser was implemented. It correctly handles Unicode escape sequences, nested objects, and null values.

**Period validation for `topics` and `trends`:** The regex `\d{4}-\d{2}` alone does not reject invalid months like `2024-13`. An additional numeric month range check (1to12) was added to `isValidPeriod()` in the CLI.
