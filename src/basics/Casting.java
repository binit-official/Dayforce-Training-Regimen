// package basics;

/*
===============================================================================
MASTER FILE: CASTING (PRIMITIVE & OBJECT)
(Widening, Narrowing, Upcasting, and Downcasting)
===============================================================================

1. WHAT IS CASTING?
    - Conversion of a value from one data type to another.
    - Primitive: Size/precision adjustment.
    - Object: Perspective/reference adjustment.

2. WHY USE IT?
    - Type interoperability (int to double).
    - Polymorphism (Upcasting).
    - Accessing child-specific features (Downcasting).
===============================================================================
*/

class Parent {
    void show() { System.out.println("Parent's Show"); }
}

class Child extends Parent {
    void show() { System.out.println("Child's Show"); }
    void childSpecial() { System.out.println("Child's Unique Logic"); }
}

public class Casting {

    // 2) OBJECT AS PARAMETER IN FUNCTION
    /* * Loosely coupled: accepts Parent or any Child. 
     * * 3) Interface/Class used as abstraction boundary.
     */
    static void execute(Parent p) {
        p.show(); 
        
        // 5) UPCASTING & DOWNCASTING LOGIC
        if (p instanceof Child) {
            Child c = (Child) p; 
            c.childSpecial();
        }
    }

    // 5) METHOD OVERLOADING + CASTING DECISION
    static void test(Object o) { System.out.println("Object version called"); }
    static void test(String s) { System.out.println("String version called"); }

    public static void main(String[] args) {

        // =====================================================================
        // 1) PRIMITIVE DATA TYPE CASTING
        // =====================================================================

        // A. IMPLICIT CASTING (Widening)
        /* * UPCASTING/WIDENING = SAFE (Automatic).
         * * 2) char Promotion: char -> int -> long -> float -> double.
         */
        char ch = 'A';
        int num = ch;   // 65 (ASCII)
        double myDouble = num; 
        System.out.println("Implicit char to double: " + myDouble);

        // B. EXPLICIT CASTING (Narrowing)
        /* * DOWNCASTING/NARROWING = RISKY (Manual + Runtime/Precision check).
         */
        double pi = 3.14159;
        int roundedPi = (int) pi; // Manual cast required

        // 8) BOOLEAN RESTRICTION
        /* * boolean b = true; int x = (int) b; // COMPILE ERROR!
         * Boolean is a separate hierarchy; cannot be cast to numeric types.
         */

        // =====================================================================
        // 2) WRAPPER VS PRIMITIVE (BOXING)
        /* * 3) Wrapper conversion is NOT casting. It is Autoboxing/Unboxing.
         */
        Integer x = 10; // Autoboxing
        int y = x;      // Unboxing

        // =====================================================================
        // 3) THE "A a1 = new B()" CONCEPT
        // =====================================================================
        /* * UPCASTING does not need an operator because it is ALWAYS safe.
         * * Reference Type (Left): Controls visible methods (Compile-time).
         * * Object Type (Right): Controls version executed (Runtime).
         */
        Parent a1 = new Child(); // Upcasting
        a1.show(); 
        // 1) Reference Limitation: a1.childSpecial(); // COMPILE ERROR

        // 4) FINAL VARIABLE + CASTING
        final Parent pFixed = new Child();
        // pFixed = new Child(); // ERROR: cannot reassign reference
        ((Child)pFixed).childSpecial(); // ALLOWED: casting doesn't change reference

        // =====================================================================
        // 4) DOWNCASTING (Explicit Object Casting)
        // =====================================================================
        
        // 2) INSTANTIATION ERROR CASE
        // Parent p = new Parent(); Child c = (Child) p; // RUNTIME ERROR: ClassCastException

        // 1) INVALID HIERARCHY (Compile-time Error)
        /* * Downcasting is ONLY allowed within the same inheritance tree.
         * String s = "Hi"; Integer i = (Integer) s; // COMPILE ERROR
         */

        // 7) NULL CASTING
        Child cNull = (Child) null; // VALID: null fits any reference
        // cNull.show(); // NullPointerException at runtime

        // =====================================================================
        // 6) ARRAY CASTING
        /* * Arrays are objects. Type must match exactly.
         */
        Object objArray = new int[5];
        int[] intArr = (int[]) objArray; // WORKS

        // =====================================================================
        // 5) OVERLOADING RESOLUTION
        test(null);           // Prints "String" (Most specific match)
        test((Object) null);  // Prints "Object" (Casting forces specific overload)

        System.out.println("\n--- Function Parameter Test ---");
        execute(new Child());
    }
}

/*
===============================================================================
CRYSTAL CLEAR CONCEPTS & MEMORY
===============================================================================




1) MENTAL MODEL:
   Upcasting   = Safe (Automatic)
   Downcasting = Risky (Manual + Runtime Check)
   Widening    = Safe
   Narrowing   = Risky (Data Loss)

2) INTERFACE/PARENT REFERENCE LIMITATION:
   Even if the object is a 'Car', a 'Vehicle' reference cannot see 'Car-only' 
   methods at compile time.

3) WHY NO CONSTRUCTOR CHAINING IN INTERFACES?
   Even though a class implements an interface, interfaces have no 
   constructors, so no chaining occurs for that part of the hierarchy.

===============================================================================
INTERNAL LOGIC: JVM vs COMPILER
===============================================================================

1. Compiler checks: Are types in the same tree? (Prevents String to Integer).
2. JVM checks: Is the object in the heap actually what you are casting to?



===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: The Character Math
- char c = 'A'; int i = c + 1; char next = (char) i; // Print 'B'.

DRILL 2: Array Casting Error
- Object obj = new int[5]; double[] d = (double[]) obj; 
- Observe the Runtime ClassCastException.

DRILL 3: Instanceof Safety
- Wrap every downcast in 'if (ref instanceof TargetType)' to prevent crashes.

===============================================================================
END
===============================================================================
*/