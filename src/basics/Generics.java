//package basics;

import java.util.*;

/*
===============================================================================
JAVA MASTER FILE: GENERICS
(Type Safety & Reusability Architecture)
===============================================================================

1. INTRODUCTION
    - Definition: Generics allow types (classes and interfaces) to be parameters when defining classes, interfaces, and methods.
    - Purpose: To provide stronger type checks at compile time and to support generic programming.
    - History: Introduced in Java 5 to solve the type-safety issues of raw Collections.

2. THE PROBLEM (BEFORE GENERICS)
    - Collections stored 'Object'. You could accidentally add an Integer to a List of Strings.
    - No compile-time check. Errors appeared only at runtime (ClassCastException).
    - Required explicit type casting when retrieving elements.

3. THE SOLUTION (WITH GENERICS)
    - Compile-time safety: The compiler guarantees that if you ask for a List<String>, it only contains Strings.
    - No Casting: The compiler inserts casts automatically (Bytecode remains the same due to Type Erasure).
===============================================================================
*/

public class Generics {

    public static void main(String[] args) {

        // =====================================================================
        // 1. THE EVOLUTION: RAW vs GENERIC
        // =====================================================================
        System.out.println("--- 1. Raw vs Generic ---");

        // A. RAW TYPE (Pre-Java 5) - UNSAFE
        List rawList = new ArrayList(); 
        rawList.add("Hello");
        rawList.add(100); // ⚠️ DANGER: Compiles fine, but dangerous mixed types (Heap Pollution risk)
        
        // Manual Casting required (Risk of ClassCastException)
        String s1 = (String) rawList.get(0); 
        // String s2 = (String) rawList.get(1); // 💥 CRASH at runtime (Integer cannot be cast to String)

        // B. GENERIC TYPE (Java 5+) - SAFE
        // Diamond Operator (<>) [Java 7]: Compiler infers type from the left side.
        List<String> genericList = new ArrayList<>(); 
        genericList.add("Hello");
        // genericList.add(100); // ❌ COMPILE ERROR: Checks type immediately
        
        // No Casting required
        String s3 = genericList.get(0); 
        System.out.println("Generic Retrieval: " + s3);

        // =====================================================================
        // 2. GENERIC CLASSES & CONSTRUCTORS
        // =====================================================================
        System.out.println("\n--- 2. Generic Classes ---");

        // Usage: Box for Integer
        Box<Integer> intBox = new Box<>();
        intBox.setValue(10);
        System.out.println("Integer Box: " + intBox.getValue());

        // Usage: Generic Constructor
        // The constructor has its own type parameter <T>, independent of the class.
        Demo d = new Demo("Constructor Data"); 
        Demo d2 = new Demo(123);

        // =====================================================================
        // 3. GENERIC INTERFACES
        // =====================================================================
        System.out.println("\n--- 3. Generic Interfaces ---");
        
        // Implementation 1: Specifying the type (String)
        UserRepository userRepo = new UserRepository();
        userRepo.save("User123");
        System.out.println("User Repo Find: " + userRepo.find());

        // Implementation 2: Keeping it generic
        MemoryRepository<Integer> memRepo = new MemoryRepository<>();
        memRepo.save(999);
        System.out.println("Memory Repo Find: " + memRepo.find());

        // =====================================================================
        // 4. GENERIC METHODS (In Non-Generic Class)
        // =====================================================================
        System.out.println("\n--- 4. Generic Methods ---");
        
        List<String> names = Arrays.asList("Java", "Code");
        String first = Utility.getFirst(names); // Type inference works here
        System.out.println("First Element: " + first);

        // =====================================================================
        // 5. BOUNDED TYPES (Restricting Inputs)
        // =====================================================================
        System.out.println("\n--- 5. Bounded Types ---");
        
        // Only subclasses of Number are allowed
        Stats<Integer> iStats = new Stats<>(new Integer[]{1, 2, 3});
        System.out.println("Avg: " + iStats.average());
        
        Stats<Double> dStats = new Stats<>(new Double[]{1.1, 2.2});
        System.out.println("Avg: " + dStats.average());
        
        // Stats<String> sStats = new Stats<>(); // ❌ ERROR: String is not a Number

        // =====================================================================
        // 6. WILDCARDS (?) (The Flexible Receiver)
        // =====================================================================
        System.out.println("\n--- 6. Wildcards ---");
        
        List<Integer> intList = Arrays.asList(1, 2, 3);
        List<Double> doubleList = Arrays.asList(1.5, 2.5);
        List<Number> numList = new ArrayList<>();

        /* Unbounded Wildcard <?>: Accepts List of ANY type */
        printList(intList);
        printList(doubleList);

        /* Upper Bounded <? extends Number>: Accepts Number or its children (Read-Onlyish) */
        System.out.println("Sum Integers: " + sumOfList(intList));
        System.out.println("Sum Doubles: " + sumOfList(doubleList));

        /* Lower Bounded <? super Integer>: Accepts Integer or its parents (Write-Friendly) */
        addNumbers(numList); // Valid because Number is super of Integer
        System.out.println("Lower Bound List: " + numList);
        
        /* Wildcard Capture (Advanced) */
        // captureHelper(intList); // Helper method captures the wildcard type
        
        // =====================================================================
        // 7. ARRAYS vs GENERICS (Crucial Theory)
        // =====================================================================
        System.out.println("\n--- 7. Arrays vs Generics ---");
        
        // ARRAYS are COVARIANT (Unsafe at Runtime)
        String[] strArr = new String[5];
        Object[] objArr = strArr; // Allowed (String[] IS-A Object[])
        try {
            objArr[0] = 100; // ❌ Runtime Exception: ArrayStoreException
        } catch (ArrayStoreException e) {
            System.out.println("Array Error: " + e);
        }
        
        // GENERICS are INVARIANT (Safe at Compile Time)
        List<String> strList = new ArrayList<>();
        // List<Object> objList = strList; // ❌ Compile Error: List<String> is NOT List<Object>
        /* Why? Because if allowed, objList.add(100) would corrupt the underlying List<String>. */

        // =====================================================================
        // 8. COMPLEX NESTED GENERICS (The "Inception" Map)
        // =====================================================================
        System.out.println("\n--- 8. Complex Nested Generics ---");
        
        Map<String, Map<String, List<String>>> university = new HashMap<>();
        
        List<String> javaStudents = Arrays.asList("Sri", "Vas");
        Map<String, List<String>> javaBatches = new HashMap<>();
        javaBatches.put("B-31", javaStudents);
        
        university.put("Java", javaBatches);
        
        university.forEach((course, batches) -> {
            System.out.println("Course: " + course);
            batches.forEach((batch, students) -> {
                System.out.println("  Batch: " + batch + " -> " + students);
            });
        });
        
        // =====================================================================
        // 9. REIFIABLE vs NON-REIFIABLE TYPES (Advanced)
        // =====================================================================
        /* * Reifiable Types: Type info fully available at runtime (e.g., String, Integer, int[], List<?>).
         * Non-Reifiable Types: Type info erased at runtime (e.g., List<String>, List<T>).
         * * Consequence:
         * - if (obj instanceof List<String>) // ❌ Error: Type erased.
         * - if (obj instanceof List<?>) // ✅ OK: Just checks if it's a List.
         */
    }

    // --- HELPER METHODS ---

    // Generic Method inside non-generic class
    // <T> must be declared before return type
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }

    // Unbounded Wildcard
    public static void process(List<?> list) {
        // list.add("Hello"); // ❌ Compile Error: Compiler doesn't know type of list
        captureHelper(list); // Delegate to helper to capture type
    }

    // Wildcard Capture Helper
    private static <T> void captureHelper(List<T> list) {
        // Now we know the type is T, so we can work with it safely
        list.set(0, list.get(0)); 
    }

    public static void printList(List<?> list) {
        System.out.println("Wildcard Print: " + list);
    }

    // Upper Bounded (Producer)
    public static double sumOfList(List<? extends Number> list) {
        double s = 0.0;
        for (Number n : list) {
            s += n.doubleValue();
        }
        return s;
    }

    // Lower Bounded (Consumer)
    public static void addNumbers(List<? super Integer> list) {
        list.add(10);
        list.add(20);
    }
}

// =========================================================================
// CUSTOM GENERIC CLASSES & INTERFACES
// =========================================================================

/* 1. Generic Class Definition */
class Box<T> {
    private T value;
    public void setValue(T value) { this.value = value; }
    public T getValue() { return value; }
}

/* 2. Generic Interface Definition */
interface Repository<T> {
    void save(T data);
    T find();
}

/* 3. Implementation specifying Type */
class UserRepository implements Repository<String> {
    private String data;
    public void save(String data) { this.data = data; }
    public String find() { return data; }
}

/* 4. Implementation keeping it Generic */
class MemoryRepository<T> implements Repository<T> {
    private T data;
    public void save(T data) { this.data = data; }
    public T find() { return data; }
}

/* 5. Generic Constructor Example */
class Demo {
    // <T> before constructor name
    <T> Demo(T value) {
        System.out.println("Constructor Value: " + value);
    }
}

/* 6. Bounded Type Parameter */
class Stats<T extends Number> {
    T[] nums;
    Stats(T[] nums) { this.nums = nums; }
    double average() {
        double sum = 0.0;
        for (T num : nums) {
            sum += num.doubleValue();
        }
        return sum / nums.length;
    }
}

/* 7. Comparable Interface (Generic) */
class Student implements Comparable<Student> {
    int id;
    public int compareTo(Student s) {
        return this.id - s.id;
    }
}

/* 8. Utility Class with Generic Method */
class Utility {
    public static <T> T getFirst(List<T> list) {
        return list.get(0);
    }
}

/*
===============================================================================
ARCHITECTURAL DEEP DIVE & ADVANCED CONCEPTS
===============================================================================

1. TYPE ERASURE & BRIDGE METHODS
   - Generics exist ONLY at Compile Time. At Runtime, <T> is replaced by Object (or Bound).
   - Bridge Methods:
     class Parent<T> { T get() { return null; } }
     class Child extends Parent<String> { String get() { return "Hello"; } }
     
     The JVM sees two different methods signatures (Object get vs String get).
     To preserve polymorphism, the compiler generates a synthetic "Bridge Method" in Child:
     Object get() { return this.get(); } // Calls String get()

2. HEAP POLLUTION
   - Occurs when a variable of a parameterized type refers to an object that is not of that parameterized type.
   - Example:
     List raw = new ArrayList<String>();
     raw.add(100); // 💥 Heap Pollution: List<String> now contains an Integer!
   - This causes ClassCastException later when reading the list as List<String>.

3. WILDCARD RULES (PECS)
   - Producer Extends: List<? extends Number>. Use when reading. Cannot add.
   - Consumer Super: List<? super Integer>. Use when writing. Can add Integer.

4. RESTRICTIONS
   - Cannot instantiate generic types with primitives (List<int> ❌).
   - Cannot create instances of type parameters (new T() ❌).
   - Cannot declare static fields of type T (static T info ❌).
   - Enum cannot be generic (enum Test<T> ❌).

5. INVARIANCE vs COVARIANCE
   - Arrays are Covariant: String[] IS-A Object[]. (Unsafe).
   - Generics are Invariant: List<String> IS-NOT List<Object>. (Safe).

6. REIFIABLE TYPES
   - A type whose type information is fully available at runtime.
   - Reifiable: String, Integer, List<?>, Map<?,?>.
   - Non-Reifiable: List<String>, List<T>. (Type info erased).
===============================================================================
*/