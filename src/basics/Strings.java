//package basics;

import java.util.Arrays;

/*
===============================================================================
STRING MASTER FILE: THE IMMUTABLE GIANT
(Memory Management, Optimization & Manipulation)
===============================================================================

1. WHAT IS A STRING?
    - A String is a sequence of characters. In Java, it is an OBJECT, not a primitive.
    - Class: java.lang.String
    - MODIFIER: public final class String (Cannot be extended/subclassed).
    - NATURE: IMMUTABLE. Once created, the internal state cannot be changed.

2. WHY IS STRING IMMUTABLE? (The "Why")
    - Security: Strings carry sensitive data (URL, DB user, password). If mutable, 
      a hacker could change the reference after authentication check.
    - Thread Safety: Multiple threads can share the same String object without 
      synchronization because the value never changes.
    - String Pool Efficiency: Pooling is only possible if the object is unchangeable.
    - Hashcode Caching: String calculates hashcode ONCE and caches it. This makes 
      it the perfect key for HashMaps (fast lookup).
===============================================================================
*/

public class Strings {

    public static void main(String[] args) {

        // =====================================================================
        // 1) STRING ALLOCATION & MEMORY (POOL vs HEAP)
        // =====================================================================
        /* * MEMORY CLARIFICATION:
         * - Variables (references) live on the STACK.
         * - Objects live in the HEAP.
         * - The String Constant Pool (SCP) is a special managed area INSIDE the Heap.
         */
        
        String s1 = "Java";            // Literal -> Goes to SCP (Reuse eligible)
        String s2 = "Java";            // Literal -> Points to existing "Java" in SCP
        String s3 = new String("Java"); // 'new' -> Forces NEW object in main Heap
        
        // 2) HASHCODE CACHING (Internal optimization)
        /* String has a field 'private int hash'. It computes hash once. 
         * Future calls return the cached value instantly. */
        System.out.println("Hash s1: " + s1.hashCode()); 

        // =====================================================================
        // 2) COMPARISON: == vs .equals() vs .compareTo()
        // =====================================================================
        
        System.out.println("\n--- Comparison ---");
        System.out.println(s1 == s2);       // true (Same Reference in SCP)
        System.out.println(s1 == s3);       // false (Pool Ref vs Heap Ref)
        System.out.println(s1.equals(s3));  // true (Content Comparison - Logical Equality)
        
        // compareTo(): Lexicographical comparison (Dictionary order)
        // Returns: Negative (A < B), Zero (A == B), Positive (A > B)
        System.out.println("Apple vs Banana: " + "Apple".compareTo("Banana")); // -1

        // =====================================================================
        // 3) CONCATENATION: COMPILE-TIME vs RUNTIME
        // =====================================================================
        
        System.out.println("\n--- Concatenation Optimization ---");
        
        // Case A: Compile-Time Constant Folding (Optimization)
        String a = "Hello" + "World"; 
        String b = "HelloWorld";
        System.out.println("Compile-time check: " + (a == b)); // true (Both point to same SCP literal)
        
        // Case B: Runtime Concatenation
        String x = "Hello";
        String y = x + "World"; // Variable 'x' makes this happen at runtime
        String z = "HelloWorld";
        System.out.println("Runtime check: " + (y == z)); // false (y is a new object in Heap)

        // Case C: Null Concatenation
        String strNull = null;
        // String crash = strNull.concat("Test"); // THROWS NullPointerException
        String magic = strNull + "Test"; // "nullTest" (Safe: converts null to string "null")
        System.out.println("Magic Null: " + magic);

        // =====================================================================
        // 4) EXTRACTION & SEARCH
        // =====================================================================
        
        String sentence = "Java is fun and Java is powerful";
        
        // indexOf / lastIndexOf
        System.out.println("First 'Java': " + sentence.indexOf("Java")); // 0
        System.out.println("Last 'Java': " + sentence.lastIndexOf("Java")); // 16
        
        // substring()
        /* HISTORICAL NOTE:
         * Pre-Java 7: substring shared the original char array (Memory Leak risk).
         * Post-Java 7: substring creates a NEW char array (Safe).
         */
        System.out.println("Sub (8-end): " + sentence.substring(8)); // "fun and..."
        System.out.println("Sub (0-4): " + sentence.substring(0, 4)); // "Java" (End exclusive)

        // =====================================================================
        // 5) MODIFICATION (REPLACE & REGEX)
        // =====================================================================
        
        String raw = "User: Binit, ID: 101, Code: 999";
        
        // replace() -> Literal replacement
        System.out.println(raw.replace("User", "Admin")); 
        
        // replaceAll() -> Regex replacement (Powerful)
        System.out.println("Hide Digits: " + raw.replaceAll("\\d", "*")); 
        
        // replaceFirst() -> Only first match
        System.out.println("First ID: " + raw.replaceFirst("\\d+", "###")); 

        // =====================================================================
        // 6) MODERN CHECKS & CONVERSIONS
        // =====================================================================
        
        String blank = "   "; // Unicode whitespace
        
        // trim() vs strip()
        System.out.println("Trim: '" + blank.trim() + "'");   // Removes ASCII whitespace (Classic)
        System.out.println("Strip: '" + blank.strip() + "'"); // Removes Unicode whitespace (Java 11+)
        
        // isBlank() vs isEmpty()
        System.out.println("isEmpty (length 0?): " + blank.isEmpty()); // false (length is 3)
        System.out.println("isBlank (whitespace?): " + blank.isBlank()); // true
        
        // valueOf vs parseInt
        String numStr = String.valueOf(100);       // int -> String
        int numInt = Integer.parseInt("100");      // String -> int

        // =====================================================================
        // 7) SPLIT & VALIDATION
        // =====================================================================
        
        String data = "Apple,Banana,Cherry";
        String[] fruits = data.split(","); 
        System.out.println("Split: " + Arrays.toString(fruits));
        
        // matches(): Validates WHOLE string against regex
        boolean isEmail = "student@soa.ac.in".matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$");
        System.out.println("Valid Email? " + isEmail);
    }
}

/*
===============================================================================
STRINGBUILDER vs STRINGBUFFER (MUTABILITY)
===============================================================================

Since String is immutable, using it in loops (s += "a") creates garbage objects.
Solution: Use Mutable classes.

| Feature         | String           | StringBuffer      | StringBuilder      |
|-----------------|------------------|-------------------|--------------------|
| Mutability      | Immutable        | Mutable           | Mutable            |
| Thread Safety   | Yes (Implicit)   | Yes (Synchronized)| No (Not Sync)      |
| Performance     | Fast (Read)      | Slow (Write)      | Fast (Write)       |
| Use Case        | Constants/Keys   | Multi-thread env  | Single-thread loops|



===============================================================================
DEEP DIVE: INTERNAL CONCEPTS
===============================================================================

1) IMMUTABILITY MECHANISM:
   - The class is 'final' so methods cannot be overridden.
   - The internal 'byte[] value' (or char[]) is 'private final'.
   - No setters provided.

2) STRING POOL (INTERNING):
   - You can manually move a heap string to the pool using .intern().
   - String s = new String("Hello");
   - String poolRef = s.intern(); // Returns reference from SCP

3) + OPERATOR vs CONCAT:
   - + Operator: Can handle non-strings (converts 10 to "10"). Slower in loops.
   - concat(): Strict. Arguments must be String. Throws NPE on null.

===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: The Palindrome Check
- Write a method that reverses a string (using StringBuilder) and compares 
  it with the original using .equals().

DRILL 2: Email Extractor
- Given "Contact: test@gmail.com for info", use split() or substring() 
  to extract just the email address.

DRILL 3: The Pool Test
- String a = "A"; String b = new String("A");
- Compare (a==b). Then compare (a == b.intern()). Explain the result.

===============================================================================
END
===============================================================================
*/