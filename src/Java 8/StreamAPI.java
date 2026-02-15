//package basics;

import java.util.*;
import java.util.stream.*;

/*
===============================================================================
JAVA 8 MASTER FILE: STREAM API
(Declarative Data Processing & Pipeline Architecture)
===============================================================================

1. INTRODUCTION
    - Definition: A sequence of elements supporting sequential and parallel aggregate operations.
    - Purpose: Process collections declaratively (Internal Iteration).
    - Key Benefit: Parallelism is virtually free (fork/join framework).

2. STREAM LIFECYCLE
    - Source: Creation (Collection, Array, I/O).
    - Intermediate Operations: Transform/Filter (Lazy - executed only when terminal op is called).
    - Terminal Operation: Produce Result (List, Integer, void). Triggers the pipeline.

3. STREAM vs COLLECTION
    - Collection: Stores data (In-Memory).
    - Stream: Computes data (CPU-bound). Streams do NOT store elements.
    - One-Time Use: A Stream cannot be reused after a terminal operation.
===============================================================================
*/

public class StreamAPI {

    public static void main(String[] args) {

        // =====================================================================
        // 1. STREAM CREATION STRATEGIES
        // =====================================================================
        System.out.println("--- 1. Creation ---");

        List<String> list = Arrays.asList("Java", "Python", "C++", "JavaScript", "Java");
        
        // From Collection
        Stream<String> streamFromList = list.stream();
        
        // From Values
        Stream<Integer> streamOf = Stream.of(1, 2, 3, 4, 5);
        
        // Infinite Stream (Generator)
        Stream<Double> randoms = Stream.generate(Math::random).limit(5);

        // =====================================================================
        // 2. INTERMEDIATE OPERATIONS (Lazy & Short-Circuiting)
        // =====================================================================
        System.out.println("\n--- 2. Intermediate Operations ---");

        /* filter(Predicate) & map(Function) */
        List<String> transformed = list.stream()
            .filter(s -> s.startsWith("J")) 
            .map(String::toUpperCase)       
            .collect(Collectors.toList());
        System.out.println("Filter+Map: " + transformed);

        /* distinct() & sorted() */
        List<String> uniqueSorted = list.stream()
            .distinct()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
        System.out.println("Distinct+Sorted: " + uniqueSorted);

        /* peek() (Debugging Tool)
         * - Intermediate op that performs an action on each element.
         * - Mainly used for logging/debugging to see flow.
         * - Do NOT modify state here.
         */
        long count = list.stream()
            .filter(s -> s.length() > 3)
            .peek(s -> System.out.println("Processing: " + s)) // Side-effect for debug
            .count();

        /* Java 9+: takeWhile() / dropWhile()
         * takeWhile: Takes elements AS LONG AS condition is true (stops at first false).
         * dropWhile: Skips elements AS LONG AS condition is true (starts at first false).
         */
        System.out.print("TakeWhile (<4): ");
        Stream.of(1, 2, 3, 4, 5, 1, 2)
            .takeWhile(n -> n < 4) // Stops at '4'
            .forEach(n -> System.out.print(n + " ")); // Output: 1 2 3
        System.out.println();

        // =====================================================================
        // 3. PRIMITIVE STREAMS (Performance Optimization)
        // =====================================================================
        System.out.println("\n--- 3. Primitive Streams (No Boxing) ---");

        /* mapToInt(), mapToDouble(), mapToLong()
         * - Converts Stream<T> to IntStream, DoubleStream, etc.
         * - Avoids overhead of Integer wrapper objects (Boxing/Unboxing).
         * - Provides specialized methods: sum(), average(), summaryStatistics().
         */
        int totalLength = list.stream()
            .mapToInt(String::length) // Returns IntStream
            .sum();
        System.out.println("Total Char Length: " + totalLength);

        /* IntStream Ranges
         * range(start, end)       -> Exclusive (1, 5) => 1, 2, 3, 4
         * rangeClosed(start, end) -> Inclusive (1, 5) => 1, 2, 3, 4, 5
         */
        System.out.print("RangeClosed: ");
        IntStream.rangeClosed(1, 5).forEach(n -> System.out.print(n + " "));
        System.out.println();

        // =====================================================================
        // 4. TERMINAL OPERATIONS (Finding & Matching)
        // =====================================================================
        System.out.println("\n--- 4. Finding & Matching ---");

        /* findFirst() vs findAny()
         * - findFirst(): Returns first element (Deterministic). Respects encounter order.
         * - findAny(): Returns ANY element (Non-Deterministic). Faster in Parallel.
         */
        Optional<String> first = list.stream().filter(s -> s.startsWith("J")).findFirst();
        Optional<String> any   = list.parallelStream().filter(s -> s.startsWith("J")).findAny();
        
        System.out.println("First: " + first.orElse("None"));
        System.out.println("Any (Parallel): " + any.orElse("None"));

        /* min() & max()
         * - Accepts a Comparator.
         * - Returns Optional (in case stream is empty).
         */
        Optional<String> minVal = list.stream().min(Comparator.naturalOrder());
        Optional<String> maxVal = list.stream().max(Comparator.naturalOrder());
        System.out.println("Max String: " + maxVal.orElse("Empty"));

        // =====================================================================
        // 5. ADVANCED COLLECTORS (Grouping, Partitioning, Maps)
        // =====================================================================
        System.out.println("\n--- 5. Advanced Collectors ---");

        /* groupingBy(Function classifier)
         * - Similar to SQL "GROUP BY".
         * - Returns Map<K, List<V>>.
         */
        Map<Integer, List<String>> groupedByLen = list.stream()
            .collect(Collectors.groupingBy(String::length));
        System.out.println("Grouped by Length: " + groupedByLen);

        /* partitioningBy(Predicate)
         * - Special case of grouping. Key is Boolean (true/false).
         * - Returns Map<Boolean, List<V>>.
         */
        Map<Boolean, List<String>> partitioned = list.stream()
            .collect(Collectors.partitioningBy(s -> s.length() > 4));
        System.out.println("Partitioned (>4 chars): " + partitioned);

        /* toMap(KeyMapper, ValueMapper, MergeFunction)
         * - Converts stream to Map.
         * - MergeFunction handles duplicate keys (mandatory if duplicates exist).
         */
        Map<String, Integer> map = list.stream()
            .distinct()
            .collect(Collectors.toMap(
                s -> s,              // Key
                String::length,      // Value
                (oldVal, newVal) -> oldVal // Merge: Keep existing if duplicate
            ));
        System.out.println("ToMap: " + map);

        /* joining(), counting(), summarizingInt()
         * - joining: Concatenates strings.
         * - summarizingInt: Returns count, sum, min, average, max all at once.
         */
        String joined = list.stream().collect(Collectors.joining(", "));
        System.out.println("Joined: " + joined);

        IntSummaryStatistics stats = list.stream()
            .collect(Collectors.summarizingInt(String::length));
        System.out.println("Stats: " + stats); // IntSummaryStatistics{count=5, sum=24, min=3, ...}

        // =====================================================================
        // 6. PARALLEL STREAMS & ORDERING
        // =====================================================================
        System.out.println("\n--- 6. Parallel Streams ---");

        /* forEach vs forEachOrdered
         * - forEach: Execution order NOT guaranteed in parallel (Faster).
         * - forEachOrdered: Forces stream order (Slower).
         */
        System.out.print("Parallel forEach: ");
        list.parallelStream().forEach(s -> System.out.print(s + " ")); // Random order
        
        System.out.print("\nParallel forEachOrdered: ");
        list.parallelStream().forEachOrdered(s -> System.out.print(s + " ")); // List order
        System.out.println();

        // =====================================================================
        // 7. COLLECTION API ENHANCEMENTS (Java 8+) [cite: 753-755]
        // =====================================================================
        System.out.println("\n--- 7. Collection Enhancements (In-Place) ---");

        /* Note: These are methods on the Collection interface, NOT Stream.
         * They modify the collection directly.
         */
        List<String> mutableList = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        // removeIf(Predicate) [cite: 753]
        mutableList.removeIf(s -> s.equals("B")); // Removes "B"
        
        // replaceAll(UnaryOperator) [cite: 754]
        mutableList.replaceAll(String::toLowerCase); // "a", "c", "d"
        
        // sort(Comparator) [cite: 755]
        mutableList.sort(Comparator.reverseOrder()); // "d", "c", "a"

        System.out.println("Modified List: " + mutableList);
    }
}

/*
===============================================================================
DEEP DIVE: STREAM ARCHITECTURE & CHEAT SHEET
===============================================================================



1. KEY DIFFERENCES
   | Feature | findFirst() | findAny() |
   |---------|------------|-----------|
   | Returns | Optional<T> | Optional<T> |
   | Logic   | First element in encounter order | Any element found |
   | Parallel| Slower (Synchronization needed) | Faster (Returns first thread result) |

   | Feature | map() | mapToInt() |
   |---------|-------|------------|
   | Output  | Stream<R> | IntStream |
   | Boxing  | Auto-Boxing (Integer) | No Boxing (int) |
   | Methods | Generic | sum(), average(), range() |

2. ADVANCED COLLECTORS
   - Collectors.groupingBy(): The SQL "GROUP BY" for Java objects.
   - Collectors.partitioningBy(): Splits data into two lists (true/false).
   - Collectors.mapping(): Adapts a collector to a different type.

3. COLLECTION vs STREAM METHODS
   - list.stream().filter(...): Creates a NEW stream. Original list UNTOUCHED.
   - list.removeIf(...): Modifies the list IN-PLACE. Returns boolean.

4. LAZY EVALUATION TRAP
   - Stream<String> s = list.stream().filter(x -> { print("Filtering"); return true; });
   - s.map(...);
   - RESULT: Nothing prints!
   - WHY? No terminal operation (collect, count, forEach) was called.

===============================================================================
CHEAT SHEET: STREAM OPERATIONS
===============================================================================
| Operation | Type | Description |
| :--- | :--- | :--- |
| **filter(Predicate)** | Intermediate | Keeps elements matching logic. |
| **map(Function)** | Intermediate | Transforms each element. |
| **flatMap(Function)** | Intermediate | Flattens nested streams (1-to-Many). |
| **peek(Consumer)** | Intermediate | Debugging (view element without modifying). |
| **takeWhile/dropWhile** | Short-Circuit | (Java 9) Conditionally take/drop slice. |
| **collect(Collector)** | Terminal | Gathers result (List, Map, Set). |
| **reduce(BinaryOp)** | Terminal | Aggregates elements (Sum, Min, Max). |
| **findFirst/findAny** | Short-Circuit | Returns Optional of match. |
| **allMatch/anyMatch** | Short-Circuit | Returns boolean. |
| **forEachOrdered** | Terminal | Iterates preserving order (Parallel safe). |
*/