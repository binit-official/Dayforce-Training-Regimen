package basics;

/*
===============================================================================
MASTER FILE: CONSTRUCTOR CHAINING & THE FINAL KEYWORD
(The Lifecycle of Object Birth & The Rules of Immutability)
===============================================================================

This master file provides a senior-level breakdown of how Java objects are 
initialized and how the 'final' keyword locks down state and behavior.
===============================================================================
*/

// =========================================================================
// 1) CONSTRUCTOR CHAINING & THE 'super' / 'this' STATEMENTS
// =========================================================================

class GrandParent {
    GrandParent() {
        // Ultimate parent is java.lang.Object
        System.out.println("[4] GrandParent Constructor Body");
    }
}

class Parent extends GrandParent {
    // Variable initialization and IIB run before constructor body
    int pVar = initializeParentVar();

    {
        System.out.println("[6] Parent IIB Executed");
    }

    Parent() {
        // Compiler auto-inserts super() here pointing to GrandParent()
        System.out.println("[7] Parent No-arg Constructor Body");
    }

    Parent(String msg) {
        System.out.println("[7b] Parent Parameterized Body: " + msg);
    }

    private int initializeParentVar() {
        System.out.println("[5] Parent Instance Variable Initialized");
        return 100;
    }

    // 5) ADVANCED CONCEPT: FINAL + INHERITANCE BEHAVIOR
    /*
     * If a method is 'final', it cannot be overridden.
     * But if it is 'private final', it is NOT inherited, so a child can 
     * define a method with the same name without it being an "override".
     */
    private final void secureLogic() {
        System.out.println("Parent Private Final");
    }
}

class Child extends Parent {
    int x = initializeChildVar();

    {
        System.out.println("[9] Child IIB Executed");
    }

    /* * RULE 1: WHERE DOES super() GET PLACED?
     * - If you do not write a constructor call, the compiler automatically 
     * places 'super()' as the VERY FIRST line of the constructor.
     * - It triggers the parent's no-arg constructor.
     */

    Child() {
        // super(); // Implicitly here
        System.out.println("[10] Child No-arg Constructor Body");
    }

    /* * RULE 2: CAN WE PUT super() AND this() IN THE SAME CONSTRUCTOR?
     * - NO. Both demand to be the FIRST statement.
     * - Since only one can be first, they are mutually exclusive.
     */

    Child(int x) {
        this(); // Calls Child() -> which chains up to GrandParent
        this.x = x;
        System.out.println("[10b] Child Parameterized Constructor Body");
    }

    Child(String message) {
        super(message); // Calls Parent(String msg)
        System.out.println("[10c] Child calling explicit Parent constructor");
    }

    private int initializeChildVar() {
        System.out.println("[8] Child Instance Variable Initialized");
        return 50;
    }

    // This is NOT an override of Parent.secureLogic() because that was private.
    void secureLogic() {
        System.out.println("Child's own secureLogic");
    }
}

// =========================================================================
// 2) THE FINAL KEYWORD (Comprehensive Application)
// =========================================================================

final class FinalRules {

    // A. FINAL NON-STATIC VARIABLES (Instance Constants)
    final int varA = 10;      // 1. Direct Initialization
    final int varB;           // 2. Instance Initialization Block (IIB)
    final int varC;           // 3. Constructor (Blank Final)

    {
        varB = 20; 
        System.out.println("[IIB] varB initialized");
    }

    // 2) MISSING RULE: FINAL + CONSTRUCTOR CHAINING
    /*
     * If you have a blank final (varC), EVERY constructor must initialize it 
     * OR call another constructor (using this()) that does.
     */
    FinalRules() {
        this(30); // OK: Chains to the constructor that initializes varC
    }

    FinalRules(int val) {
        this.varC = val; 
        System.out.println("[Constructor] varC initialized");
    }

    // B. FINAL STATIC VARIABLES (Class Constants)
    /* * 4) MEMORY CLARIFICATION:
     * Static variables are part of class metadata (historically Metaspace).
     * Value may be inlined by the compiler if it's a primitive/String constant.
     * They are NOT stored inside individual object heap memory.
     */
    public static final double PI = 3.14159;
    public static final String COMPANY;

    static {
        COMPANY = "Dayforce";
        System.out.println("[SIB] Static Final COMPANY initialized");
    }

    // 3) FINAL METHOD BINDING
    /*
     * - Final methods use EARLY BINDING (Static Binding).
     * - They do NOT participate in runtime polymorphism.
     * - JVM does not need dynamic dispatch (vtable lookup) for them.
     */
    final void lockedMethod() {
        System.out.println("This behavior is fixed at compile-time.");
    }

    void process(final int data) {
        final int localLimit = 100;
        System.out.println("Processing: " + data + " with limit " + localLimit);
    }
}

// =========================================================================
// 3) EXECUTION ENGINE & VISUALIZATION
// =========================================================================

public class ConsturctorChainingAndFinalKeyword {
    public static void main(String[] args) {
        System.out.println("--- CONSTRUCTOR CHAINING START ---");
        
        // 1) CORRECT FULL EXECUTION ORDER (new Child(50)):
        /*
         * 1. Memory allocated (All instance vars get default values: 0, null, etc.)
         * 2. GrandParent instance variable initialization & IIB (Chained via super)
         * 3. GrandParent constructor body
         * 4. Parent instance variable initialization
         * 5. Parent IIB
         * 6. Parent constructor body
         * 7. Child instance variable initialization
         * 8. Child IIB
         * 9. Child constructor body
         * (Note: Static blocks ran earlier during class loading)
         */
        Child c = new Child(50);
        
        

        System.out.println("\n--- FINAL KEYWORD BEHAVIOR ---");
        FinalRules fr = new FinalRules(30);
        System.out.println("PI: " + FinalRules.PI);
        System.out.println("Company: " + FinalRules.COMPANY);
    }
}

/*
===============================================================================
CRYSTAL CLEAR CONCEPTS SUMMARY
===============================================================================

1) THE 'super' AUTOMATIC PLACEMENT:
   - Implicitly added only if no explicit super() or this() is present.
   - Always triggers parent's no-argument constructor.

2) THE 'final' INITIALIZATION GUARANTEE:
   - Blank finals must be initialized by the end of the constructor.
   - Compiler ensures no path exists where a final variable is left uninitialized.

3) SIB vs IIB WITH FINAL:
   - SIB: Perfect for 'static final'. Runs when class loads.
   - IIB: Runs during object creation, right after super() returns and 
     before the current constructor body.

4) MEMORY IMPACT:
   - Static constants are class-level. Primitive/String finals are often 
     optimized via "Constant Inlining" (replacing the name with the value).



===============================================================================
EDGE CASES & GOTCHAS (SENIOR LEVEL)
===============================================================================

1) THE CONSTRUCTOR CONUNDRUM:
   A blank final MUST be set in every constructor path. If you have 5 
   constructors, they all must either set the variable or chain to one that does.

2) FINAL IS NOT CONSTANT CONTENT:
   final int[] arr = {1, 2};
   arr[0] = 5; // LEGAL.
   // arr = new int[2]; // ILLEGAL.

3) PRIVATE FINAL OVERRIDE:
   You cannot override a final method, but if it is private final, the child 
   doesn't "see" it, so the child can have its own method with the same name 
   without error.

===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: The Chaining Flow
Create A -> B -> C. Add a print in every SIB, IIB, and Constructor. 
Observe the exact sequence: SIBs first, then interleaved IIBs/Constructors.

DRILL 2: Final in Constructor
Try to initialize a 'static final' variable inside a constructor. Observe 
the error (Static final must be initialized in SIB or at declaration).

DRILL 3: super/this Combo
Attempt to call super() on the second line of a constructor. Observe 
the error: "call to super must be first statement in constructor".

===============================================================================
END
===============================================================================
*/