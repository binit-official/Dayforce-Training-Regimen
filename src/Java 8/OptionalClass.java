//package basics;

import java.util.*;

/*
===============================================================================
JAVA MASTER FILE: OPTIONAL CLASS
(The Null-Safety Container & Functional Patterns)
===============================================================================

1. INTRODUCTION
    - Definition: Optional is a container object which may or may not contain a non-null value.
    - Purpose: To provide a type-level solution for representing optional values instead of null references.
    - It forces the programmer to handle the case where a value might be missing, avoiding NullPointerException.

2. CORE CONCEPTS
    - Empty: An Optional containing no value.
    - Present: An Optional containing a non-null value.
    - Unwrapping: Safely extracting the value or providing a default.

3. OPTIONAL vs NULL (Mental Model)
    - NULL: Dangerous, causes NullPointerException, no enforced handling.
    - OPTIONAL: Forces explicit handling, prevents accidental NPE, makes absence part of the API contract.
===============================================================================
*/

public class OptionalClass {

    public static void main(String[] args) {

        // =====================================================================
        // 1. CREATING OPTIONALS
        // =====================================================================
        System.out.println("--- 1. Creation Strategies ---");

        /* A. Optional.empty()
         * Creates an empty Optional instance.
         */
        Optional<String> emptyOpt = Optional.empty();
        System.out.println("Empty: " + emptyOpt); // Output: Optional.empty

        /* B. Optional.of(T value)
         * Creates an Optional with a NON-NULL value.
         * ⚠️ Throws NullPointerException immediately if value is null.
         */
        String name = "Srinivas";
        Optional<String> ofOpt = Optional.of(name);
        System.out.println("Of: " + ofOpt); // Output: Optional[Srinivas]

        // EDGE CASE: Passing null to of()
        try {
            String nullStr = null;
            Optional.of(nullStr); // CRASH
        } catch (NullPointerException e) {
            System.out.println("Optional.of(null) threw NPE as expected.");
        }

        /* C. Optional.ofNullable(T value)
         * The safest way.
         * If value is non-null -> Returns Optional[value].
         * If value is null     -> Returns Optional.empty.
         */
        Optional<String> nullableOpt = Optional.ofNullable(null);
        System.out.println("OfNullable (null): " + nullableOpt); // Output: Optional.empty

        // =====================================================================
        // 2. CHECKING & ACCESSING VALUES
        // =====================================================================
        System.out.println("\n--- 2. Checking & Accessing ---");
        
        Optional<String> checkOpt = Optional.of("Java");

        /* isPresent()
         * Returns true if value is present, otherwise false.
         */
        if (checkOpt.isPresent()) {
            /* get()
             * Returns the value if present. 
             * ⚠️ Throws NoSuchElementException if empty. Always guard with isPresent().
             */
            System.out.println("Value Found: " + checkOpt.get());
        }

        /* isEmpty() (Java 11+)
         * Returns true if value is NOT present.
         * Cleaner than using !isPresent().
         */
        Optional<String> emptyCheck = Optional.empty();
        System.out.println("Is Empty? " + emptyCheck.isEmpty()); // true

        /* ifPresent(Consumer)
         * Functional style. Executes the Consumer only if value is present.
         * Replaces the classic "if (x != null)" check.
         */
        checkOpt.ifPresent(val -> System.out.println("Consumer Executed: " + val));

        // =====================================================================
        // 3. JAVA 9+ MODERN API FEATURES
        // =====================================================================
        System.out.println("\n--- 3. Modern API (Java 9+) ---");

        /* ifPresentOrElse(Consumer, Runnable) (Java 9+)
         * Executes Consumer if value exists, otherwise executes Runnable.
         * Removes the need for if/else blocks.
         */
        Optional<String> modernOpt = Optional.ofNullable(null);
        modernOpt.ifPresentOrElse(
            val -> System.out.println("Value: " + val),
            () -> System.out.println("ifPresentOrElse: No Value Present")
        );

        /* or(Supplier) (Java 9+)
         * Returns another Optional if the current one is empty.
         * Difference: orElse() returns raw value T, or() returns Optional<T>.
         */
        Optional<String> backup = modernOpt.or(() -> Optional.of("Backup Plan"));
        System.out.println("Or Result: " + backup); // Optional[Backup Plan]

        /* stream() (Java 9+)
         * Converts Optional to a Stream.
         * If present -> Stream of 1 element.
         * If empty   -> Empty Stream.
         * Useful for flatMapping in Stream pipelines.
         */
        Optional<String> streamOpt = Optional.of("StreamItem");
        streamOpt.stream().forEach(val -> System.out.println("Streamed: " + val));

        // =====================================================================
        // 4. DEFAULT VALUES & PERFORMANCE (CRITICAL)
        // =====================================================================
        System.out.println("\n--- 4. Handling Absence & Performance ---");
        
        Optional<String> missing = Optional.of("I am Present");

        /* A. orElse(T other) vs orElseGet(Supplier)
         * CRITICAL PERFORMANCE DIFFERENCE:
         * - orElse(): Eager Evaluation. The argument is created/executed EVEN IF value is present.
         * - orElseGet(): Lazy Evaluation. The Supplier runs ONLY if value is empty.
         */
        
        System.out.println("--- Testing orElse ---");
        // 'expensiveOperation()' RUNS even though 'missing' has a value!
        String val1 = missing.orElse(expensiveOperation()); 
        
        System.out.println("--- Testing orElseGet ---");
        // 'expensiveOperation()' DOES NOT RUN because 'missing' has a value.
        String val2 = missing.orElseGet(() -> expensiveOperation());

        /* C. orElseThrow(Supplier<? extends X>)
         * Returns value if present, otherwise throws the exception.
         */
        try {
            Optional.empty().orElseThrow(NoSuchElementException::new);
        } catch (Exception e) {
            System.out.println("orElseThrow Caught: " + e);
        }

        // =====================================================================
        // 5. FILTERING & MAPPING (Functional Operations)
        // =====================================================================
        System.out.println("\n--- 5. Filter & Map ---");

        Optional<String> data = Optional.of("Srinivas");

        /* filter(Predicate)
         * If value satisfies condition -> Returns Optional[value].
         * If value fails condition or is empty -> Returns Optional.empty.
         */
        Optional<String> filtered = data.filter(s -> s.contains("Sri"));
        System.out.println("Filter Match: " + filtered.isPresent()); // true

        /* map(Function)
         * Transforms the value if present.
         * Automatically wraps the result in an Optional.
         */
        Optional<String> upper = data.map(String::toUpperCase);
        System.out.println("Map (Upper): " + upper.orElse(""));

        // =====================================================================
        // 6. FLATMAP (Deep Unwrapping)
        // =====================================================================
        System.out.println("\n--- 6. FlatMap (Deep Unwrapping) ---");

        /* Problem: Nested Optionals
         * If the mapping function ITSELF returns an Optional, map() wraps it again.
         * Result: Optional<Optional<String>>
         */
        Optional<String> deepData = Optional.of("Deep");
        
        // Using map() -> Results in nested Optional
        Optional<Optional<String>> nested = deepData.map(s -> Optional.of(s.toUpperCase())); 
        System.out.println("Nested Map: " + nested); 

        /* Using flatMap()
         * If the mapping function returns an Optional, flatMap "flattens" it.
         */
        Optional<String> flat = deepData.flatMap(s -> Optional.of(s.toUpperCase()));
        System.out.println("FlatMap: " + flat); 

        // =====================================================================
        // 7. PRIMITIVE OPTIONALS (Performance Optimization)
        // =====================================================================
        System.out.println("\n--- 7. Primitive Optionals ---");

        /* Why? To avoid boxing overhead (int -> Integer).
         * Specialized classes: OptionalInt, OptionalDouble, OptionalLong.
         */
        OptionalInt optInt = OptionalInt.of(100);
        
        if (optInt.isPresent()) {
            // Note: Method is getAsInt(), not get()
            System.out.println("Primitive Int: " + optInt.getAsInt());
        } else {
            System.out.println("No int value");
        }

        // =====================================================================
        // 8. OBJECT METHODS (equals, hashCode, toString)
        // =====================================================================
        System.out.println("\n--- 8. Object Methods ---");

        Optional<String> a = Optional.of("Java");
        Optional<String> b = Optional.of("Java");

        System.out.println("Equals: " + a.equals(b));   // true (Compares content)
        System.out.println("HashCode: " + a.hashCode()); 
        System.out.println("ToString: " + a.toString()); // Optional[Java]
    }

    // Helper method for Performance Demo
    static String expensiveOperation() {
        System.out.println("$$$ Expensive operation executed! $$$");
        return "Fallback";
    }
}

/*
===============================================================================
ARCHITECTURAL GUIDELINES & BEST PRACTICES
===============================================================================

1. BEST PRACTICE RULES
   - Rule 1: Optional is meant for RETURN TYPES only.
   - Rule 2: Do NOT use Optional as a Class Field (It is not Serializable).
   - Rule 3: Do NOT use Optional as a Method Parameter (Overloads methods unnecessarily).
   - Rule 4: Do NOT use Optional in Collections (Map<Optional<String>, Integer> is bad design).

2. COMMON MISTAKES
   - Mistake 1: Calling get() without checking isPresent(). (Risk: NoSuchElementException).
   - Mistake 2: Using Optional.get() in production code. (Always prefer orElse/orElseThrow).
   - Mistake 3: Returning null from an Optional-returning method. (Always return Optional.empty()).
   - Mistake 4: Wrapping Collections in Optional. (Prefer returning empty List instead).

3. SUMMARY: KEY METHODS API
| Method | Description |
| :--- | :--- |
| **empty()** | Returns an empty Optional instance. |
| **of(T)** | Returns Optional with value. Throws NPE if null. |
| **ofNullable(T)** | Returns Optional with value. Returns empty if null. |
| **get()** | Returns value. Throws Exception if empty. |
| **isPresent()** | Returns true if value exists. |
| **isEmpty()** | (Java 11+) Returns true if value does NOT exist. |
| **ifPresent(Consumer)** | Executes action if value exists. |
| **ifPresentOrElse()** | (Java 9+) Executes action if exists, else runs runnable. |
| **filter(Predicate)** | Returns Optional if matches condition, else empty. |
| **map(Function)** | Transforms value. |
| **flatMap(Function)** | Transforms value and flattens nested Optionals. |
| **orElse(T)** | Returns value or default (Eager evaluation). |
| **orElseGet(Supplier)** | Returns value or default (Lazy evaluation). |
| **orElseThrow()** | Returns value or throws exception. |
===============================================================================
*/