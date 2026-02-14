package basics;

/*
===============================================================================
POLYMORPHISM PART 1: OVERLOADING (COMPILE-TIME)
===============================================================================

1. What is Overloading?
   - It is the ability of a class to have multiple methods/constructors with 
     the SAME NAME but DIFFERENT SIGNATURES.
   - It is "Compile-time Polymorphism" because the compiler binds the call 
     to the specific method during compilation.

2. What Defines a "Unique Signature"?
   - The method name + the parameter list.
   - Return type, Access Modifiers, and Exceptions are NOT part of the signature.
===============================================================================
*/

class OverloadingMaster {

    // =========================================================================
    // 1) METHOD OVERLOADING MECHANICS
    // =========================================================================
    /*
     * How it works: Java distinguishes methods based on the "Parameter List."
     * You MUST change:
     * a) Number of parameters OR
     * b) Data types of parameters OR
     * c) Sequence of parameters
     */

    void execute(int a) { System.out.println("Single Int: " + a); }

    // Overloaded by Number
    void execute(int a, int b) { System.out.println("Two Ints: " + a + ", " + b); }

    // Overloaded by Type
    void execute(String s) { System.out.println("String type: " + s); }

    // Overloaded by Sequence
    void execute(int a, double b) { System.out.println("Int-Double"); }
    void execute(double a, int b) { System.out.println("Double-Int"); }

    // =========================================================================
    // 2) CONSTRUCTOR OVERLOADING & CHAINING
    // =========================================================================
    /*
     * How it works: Allows creating objects with different levels of data.
     * The 'this()' call is used to avoid code duplication (Constructor Chaining).
     */

    String name;
    int age;

    // Default Constructor
    OverloadingMaster() {
        this("Guest User", 0); // Chains to the 2-arg constructor
        System.out.println("Default Constructor Executed");
    }

    // Overloaded Constructor
    OverloadingMaster(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("Parameterized Constructor Executed");
    }

    // =========================================================================
    // 3) TYPE PROMOTION (The Decision Logic)
    // =========================================================================
    /*
     * How Java Decides:
     * Step 1: Look for an EXACT match.
     * Step 2: If no match, perform Implicit Type Promotion (Widening).
     * Promotion Order: byte -> short -> int -> long -> float -> double
     * char -> int
     */

    void calculate(long l) { System.out.println("Promoted to Long"); }
    void calculate(double d) { System.out.println("Promoted to Double"); }

    // =========================================================================
    // 4) THE AMBIGUITY TRAP (Corner Case)
    // =========================================================================
    /*
     * Occurs when the compiler finds two or more methods that could equally 
     * fit a call after promotion.
     */

    static void sum(int a, long b) { System.out.println("A"); }
    static void sum(long a, int b) { System.out.println("B"); }

    public static void main(String[] args) {
        OverloadingMaster obj = new OverloadingMaster();

        // Type Promotion Example
        obj.calculate(10); // 10 is 'int'. No calculate(int) exists.
                           // It promotes to calculate(long) first.

        // Ambiguity Example
        // obj.sum(10, 10); // COMPILE ERROR: Ambiguous call. 
        // Both (int, long) and (long, int) require exactly one promotion.
    }
}

/*
===============================================================================
DETAILED CONCEPT BREAKDOWN
===============================================================================

1) HOW JAVA DECIDES (Resolution Algorithm):
   When you call obj.method(arg), the compiler:
   1. Identifies all methods with the name 'method'.
   2. Discards methods that are not accessible (private) or have wrong arg counts.
   3. Looks for the "Most Specific" match. 
      - If you pass an 'int' and have method(int) and method(long), method(int) 
        is chosen because it is more specific.

2) COMPILE-TIME RESOLUTION:
   Unlike Overriding (which uses the Object type at Runtime), Overloading uses 
   the Reference Type at Compile-time. This is why it's called "Static Binding."



3) WHY RETURN TYPE DOESN'T COUNT:
   Consider: 
   int func() { return 1; }
   void func() { }
   
   If you call: "func();", the compiler has no way of knowing which one you 
   wanted based on the return value. Therefore, it's illegal to overload 
   based on return type alone.

4) MEMORY IMPACT:
   Overloading has zero runtime memory overhead. The decision is "baked into" 
   the bytecode. The .class file literally points to the specific method 
   address before the program even starts.



===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: Ambiguity Check
- Create method test(String s) and test(Object o).
- Call test(null). Which one runs? (Hint: String is more specific than Object).

DRILL 2: Constructor Chaining
- Create a class 'Task' with a final variable 'id'.
- Create 3 overloaded constructors. 
- Ensure 'id' is initialized only in ONE constructor, and the others chain 
  to it using this().

DRILL 3: Array vs Var-args
- Create method print(int[] a) and print(int... a).
- Can they coexist in the same class? Try it and observe the error.
- (Hint: In bytecode, var-args are treated as arrays).

===============================================================================
END
===============================================================================
*/