//package basics;

import java.util.*;
import java.util.function.*;

/*
===============================================================================
JAVA 8 MASTER FILE: FUNCTIONAL INTERFACES & LAMBDAS
(The Engine of Modern Java - Architect Edition)
===============================================================================

1. [cite_start]WHY LAMBDA WAS INTRODUCED? [cite: 310-313, 347-350]
    - Before Java 8: Anonymous Inner Classes were verbose.
    - Functional Programming: Enables passing behavior (code) as parameters.
    - Concurrency: Safer for parallel execution (Streams).
    - Boilerplate Reduction: Concise code.

2. [cite_start]LAMBDA EXPRESSIONS (The "What") [cite: 351-352]
    - Definition: An anonymous function that allows you to treat code as data.
    - Syntax: (Parameters) -> { Body }
    - Target Type: Must be a Functional Interface.
===============================================================================
*/

// =========================================================================
// 1. EXPLICIT @FunctionalInterface EXAMPLE (The Rules) [cite: 390-397]
// =========================================================================
@FunctionalInterface
interface Calculator {
    // 1. EXACTLY ONE Abstract Method (SAM)
    int calculate(int a, int b);

    // 2. Allowed: Multiple Default Methods (Backward Compatibility)
    default void printInfo() {
        System.out.println("Calculator Interface v1");
    }

    // 3. Allowed: Multiple Static Methods (Utility)
    static void version() {
        System.out.println("Version 1.0");
    }

    // 4. Note on Object Class Methods:
    // Methods like toString(), equals(), hashCode() do NOT count toward the 
    // single abstract method rule, even if declared abstract here.
    // However, you cannot override them as 'default' methods.
}

public class Java8 {

    public static void main(String[] args) {

        // =====================================================================
        // 2. CUSTOM FUNCTIONAL INTERFACE USAGE
        // =====================================================================
        System.out.println("--- 2. Custom Interface Usage ---");
        
        // Implementing our custom Calculator interface
        Calculator add = (a, b) -> a + b;
        Calculator sub = (a, b) -> a - b;

        System.out.println("Calculator Add (5+5): " + add.calculate(5, 5));
        System.out.println("Calculator Sub (10-3): " + sub.calculate(10, 3));
        
        // Calling static/default methods
        add.printInfo();
        Calculator.version();

        // =====================================================================
        // 3. EFFECTIVELY FINAL DEMO (Scope Rules)
        // =====================================================================
        System.out.println("\n--- 3. Effectively Final Demo ---");
        
        int base = 10; // Local variable
        
        // Lambda captures 'base'. It must effectively be final.
        Function<Integer, Integer> adder = x -> x + base; 
        
        System.out.println("Base + 5: " + adder.apply(5));

        // base = 20; // ❌ ERROR: Local variable base defined in an enclosing scope must be final or effectively final
        // If you uncomment the line above, the lambda 'adder' will fail to compile.

        // =====================================================================
        // 4. TYPE INFERENCE IN LAMBDA
        // =====================================================================
        System.out.println("\n--- 4. Type Inference ---");
        
        /* THE COMPILER IS SMART:
         * It infers parameter types from the Functional Interface definition.
         */
        
        // Verbose Way (Explicit Types)
        BiFunction<Integer, Integer, Integer> verbose = (Integer x, Integer y) -> x + y;
        
        // Concise Way (Type Inference)
        // Compiler knows 'concise' is BiFunction<Integer, Integer, Integer>
        BiFunction<Integer, Integer, Integer> concise = (x, y) -> x + y;
        
        System.out.println("Inference Result: " + concise.apply(10, 20));

        // =====================================================================
        // 5. AMBIGUOUS LAMBDA PROBLEM (Advanced Interview Q)
        // =====================================================================
        System.out.println("\n--- 5. Ambiguity Problem ---");
        
        /* SCENARIO: 
         * Overloaded methods accepting different Functional Interfaces 
         * that look the same (e.g., both take a String).
         */
        
        // Call with explicit casting to solve ambiguity
        process((Function<String, String>) s -> s.toUpperCase()); 
        process((Consumer<String>) s -> System.out.println(s));

        // =====================================================================
        // 6. PREDICATE & BIPREDICATE (Boolean Logic) [cite: 412-430]
        // =====================================================================
        System.out.println("\n--- 6. Predicates (Boolean Logic) ---");
        
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        
        System.out.println("4 is Even & Positive? " + isEven.and(isPositive).test(4)); // true
        System.out.println("Is -2 NOT Positive? " + isPositive.negate().test(-2));     // true

        BiPredicate<String, Integer> checkLength = (str, len) -> str.length() == len;
        System.out.println("Length check: " + checkLength.test("Java", 4)); // true

        // =====================================================================
        // 7. FUNCTION & BIFUNCTION (Transformation) [cite: 431-457]
        // =====================================================================
        System.out.println("\n--- 7. Functions (Transformation) ---");

        Function<String, String> clean = str -> str.trim();
        Function<String, String> upper = str -> str.toUpperCase();
        
        // Composition: clean -> upper
        Function<String, String> pipeline = clean.andThen(upper);
        System.out.println("Cleaned: " + pipeline.apply("  hello java  ")); // "HELLO JAVA"
        
        BiFunction<String, Integer, String> formatUser = (name, age) -> "User: " + name + " (" + age + ")";
        System.out.println(formatUser.apply("Binit", 22));

        // =====================================================================
        // 8. UNARYOPERATOR & BINARYOPERATOR (Specialized Functions) [cite: 440-466]
        // =====================================================================
        System.out.println("\n--- 8. Operators (Same Type Input/Output) ---");

        // Input T -> Output T
        UnaryOperator<Integer> square = n -> n * n;
        System.out.println("Square of 5: " + square.apply(5));

        // Input T, T -> Output T
        BinaryOperator<Integer> addition = (a, b) -> a + b;
        System.out.println("Sum: " + addition.apply(10, 20));

        // Helper Method: maxBy
        BinaryOperator<Integer> maxOp = BinaryOperator.maxBy(Comparator.naturalOrder());
        System.out.println("Max of (10, 50): " + maxOp.apply(10, 50));

        // =====================================================================
        // 9. CONSUMER & BICONSUMER (The "Sink") [cite: 467-479]
        // =====================================================================
        System.out.println("\n--- 9. Consumers (Void Actions) ---");

        Consumer<String> logger = msg -> System.out.println("LOG: " + msg);
        Consumer<String> dbSave = msg -> System.out.println("DB: Saved " + msg);
        
        // Chaining
        logger.andThen(dbSave).accept("Transaction_1");

        BiConsumer<String, Integer> mapPut = (k, v) -> System.out.println("Putting Key:" + k + ", Val:" + v);
        mapPut.accept("ID", 101);

        // =====================================================================
        // 10. SUPPLIER (The "Source") [cite: 480-485]
        // =====================================================================
        System.out.println("\n--- 10. Supplier (Lazy Generation) ---");

        // Takes NO input, returns T
        Supplier<Double> randomGen = () -> Math.random();
        System.out.println("Random: " + randomGen.get());
        
        // Constructor Reference
        Supplier<List<String>> listFactory = ArrayList::new; 
        List<String> myList = listFactory.get(); 
        System.out.println("List Created. Size: " + myList.size());
    }

    // Helper methods for Ambiguity Demo
    static void process(Function<String, String> f) {
        System.out.println("Processing Function: " + f.apply("input"));
    }

    static void process(Consumer<String> c) {
        c.accept("input");
        System.out.println("Processing Consumer");
    }
}

/*
===============================================================================
ELITE CONCEPTS & INTERVIEW DEEP DIVE
===============================================================================



1. [cite_start]LAMBDA vs ANONYMOUS CLASS (CRITICAL DIFFERENCE) [cite: 358-359]
   | Feature            | Anonymous Class                          | Lambda Expression                      |
   |--------------------|------------------------------------------|----------------------------------------|
   | Compilation        | Generates a separate .class file (ClassName$1.class) | Does NOT generate a separate anonymous class file. Implemented using 'invokedynamic'. |
   | Implementation     | Normal object creation.                  | Uses 'invokedynamic' (JVM instruction).|
   | 'this' keyword     | Refers to the anonymous class instance.  | Refers to the enclosing class instance.|
   | Scope              | Creates a new scope.                     | Does NOT create a new scope (Variables must be effectively final).|
   | Overhead           | Higher (Memory + Loading).               | Lower (Lightweight function object).   |

2. FUNCTION vs UNARYOPERATOR
   - Function<Integer, Integer>: Generic transformation.
   - UnaryOperator<Integer>: Specific transformation where Input Type == Output Type.
   - Best Practice: Use UnaryOperator when T is same as R.

3. ARBITRARY OBJECT METHOD REFERENCE (Class::method)
   - Scenario: String::compareTo
   - Lambda: (a, b) -> a.compareTo(b)
   - Logic: The compiler takes the FIRST argument (a) as the "target" object, 
     and passes the SECOND argument (b) as the parameter to the method.

4. PRIMITIVE SPECIALIZATIONS (Performance)
   - Predicate<Integer> involves Boxing (int -> Integer).
   - IntPredicate avoids boxing. Always use 'Int...', 'Long...', 'Double...' 
     interfaces for performance-critical math.

5. AMBIGUITY RESOLUTION
   - If two overloaded methods accept functional interfaces with the same 
     structure (e.g., String -> String vs String -> void), the compiler 
     cannot guess the type of `s -> s.code()`.
   - Solution: Cast the lambda explicitly: `(Function<String, String>) s -> s.code()`.

===============================================================================
[cite_start]SUMMARY TABLE: FUNCTIONAL INTERFACES [cite: 401-411]
===============================================================================
| Interface         | Input      | Output    | Method       | Use Case                |
|-------------------|------------|-----------|--------------|-------------------------|
| Predicate<T>      | T          | boolean   | test(T)      | Filtering, Checks       |
| Function<T,R>     | T          | R         | apply(T)     | Transformation          |
| Consumer<T>       | T          | void      | accept(T)    | Printing, Side-effects  |
| Supplier<T>       | None       | T         | get()        | Factories, Lazy Load    |
| UnaryOperator<T>  | T          | T         | apply(T)     | Math, Logic on same type|
| BinaryOperator<T> | T, T       | T         | apply(T,T)   | Reduction (Sum/Max)     |
| BiFunction<T,U,R> | T, U       | R         | apply(T,U)   | Combining two inputs    |
===============================================================================
*/