package basics;

/*
===============================================================================
INHERITANCE & POLYMORPHISM MASTER FILE
(Foundations of Reusability & Dynamic Dispatch)
===============================================================================

This file covers:
1) Core Inheritance (extends keyword)
2) The 'super' Keyword (Variables, Methods, Constructors)
3) Runtime Polymorphism Process (The Dual-Stage Resolution)
4) Static vs Instance vs Private vs Final Behaviors
5) Memory Layout & Variable Shadowing
6) Method Signature vs. Method Body Rules
===============================================================================
*/


class Vehicle {
    String brand = "Generic Vehicle";
    int topSpeed = 100;

    Vehicle() {
        System.out.println("[Vehicle] Constructor Executed");
    }

    Vehicle(String brand) {
        this.brand = brand;
        System.out.println("[Vehicle] Parameterized Constructor Executed");
    }

    // Static Method: Bound to class, not object
    static void staticMethod() {
        System.out.println("Static Method: Vehicle Version");
    }

    // Final Method: Cannot be overridden
    final void safetyRating() {
        System.out.println("Vehicle Safety: 5 Stars");
    }

    // Private Method: Invisible to Child
    private void fuelFormula() {
        System.out.println("Secret Fuel Formula");
    }

    void startEngine() {
        System.out.println("Vehicle Engine starting...");
    }
}

class Car extends Vehicle {
    String model;
    int topSpeed = 200; // 7) VARIABLE RESOLUTION: Shadowing/Hiding

    Car() {
        // Step 1: Implicit call to super() happens here
        super(); 
        System.out.println("[Car] No-arg Constructor Executed");
    }

    Car(String brand, String model) {
        // Step 2: Explicit call to Parent's Parameterized Constructor
        super(brand); 
        this.model = model;
        System.out.println("[Car] Parameterized Constructor Executed");
    }

    // =========================================================================
    // 6) EXPLICIT “METHOD SIGNATURE VS METHOD BODY” RULE
    // =========================================================================
    /* * Overriding requires:
     * - Same method name
     * - Same parameter list
     * - Compatible (Covariant) return type
     * * CRITICAL: Changing parameters = OVERLOADING, not overriding.
     */
    @Override
    void startEngine() {
        System.out.println("Car Engine starting with push-button...");
    }

    // Static Method Hiding (Not Overriding)
    static void staticMethod() {
        System.out.println("Static Method: Car Version");
    }

    void displayInfo() {
        System.out.println("Brand: " + brand);            
        System.out.println("Model: " + model);            
        System.out.println("Car Speed: " + this.topSpeed); 
        System.out.println("Vehicle Speed: " + super.topSpeed); 
    }

    // =========================================================================
    // 5) SUPER KEYWORD INTERACTION
    // =========================================================================
    /* * Inside child method, super.method() calls parent version explicitly.
     * This bypasses dynamic dispatch.
     */
    void launch() {
        System.out.print("Launching: ");
        super.startEngine(); // Bypasses Car's override
    }
}

// =========================================================================
// 3) THE CORE LOGIC ENGINE (POLYMORPHISM)
// =========================================================================

public class Polymorphism {

    /*
     * 1) EXPLICIT DUAL-STAGE RESOLUTION EXPLANATION
     * ---------------------------------------------------------------------
     * RUNTIME POLYMORPHISM PROCESS:
     * * Step 1: Compiler checks REFERENCE TYPE.
     * Step 2: JVM checks ACTUAL OBJECT TYPE.
     * * - If reference type does not contain method → COMPILE ERROR.
     * - If object overrides method → CHILD version executes.
     * ---------------------------------------------------------------------
     */

    public static void main(String[] args) {
        
        System.out.println("--- POLYMORPHIC REFERENCE ---");
        Vehicle ref = new Car("Tesla", "Model S");

        // ---------------------------------------------------------------------
        // 2) CLEAR STATIC VS INSTANCE DIFFERENCE
        // ---------------------------------------------------------------------
        /* * STATIC METHODS:
         * - Do NOT participate in dynamic polymorphism.
         * - Resolved using reference type.
         * - Called using compile-time binding.
         */
        ref.staticMethod(); // Calls Vehicle version (Reference is Vehicle)

        // ---------------------------------------------------------------------
        // 7) CLEAR VARIABLE RESOLUTION STATEMENT
        // ---------------------------------------------------------------------
        /* * - Variable resolution is based on REFERENCE TYPE.
         * - Variables are NOT part of dynamic dispatch.
         * - They are NOT polymorphic.
         */
        System.out.println("Resolved Variable: " + ref.topSpeed); // Prints 100

        // ---------------------------------------------------------------------
        // DYNAMIC DISPATCH (Instance Method)
        // ---------------------------------------------------------------------
        ref.startEngine(); // Step 1: Vehicle has it? Yes. Step 2: Car overrides? Yes. -> Prints Car version

        System.out.println("\n--- SUPER & EXPLICIT ACCESS ---");
        Car myCar = (Car) ref; 
        myCar.launch();
    }
}

/*
===============================================================================
INTERNAL FUNCTIONING & ARCHITECTURE
===============================================================================

1) TYPES OF INHERITANCE:
   - Single (A->B), Multilevel (A->B->C), Hierarchical (A->B, A->C).
   - Multiple Inheritance (A,B -> C) is FORBIDDEN for classes to avoid the 
     "Diamond Problem" (ambiguity in which method to inherit).

2) CONSTRUCTOR CHAINING (super()):
   - Parent constructor MUST run first.
   - super() is automatically inserted if not present.
   - Logic: You cannot initialize a child before its foundation (parent).

3) PRIVATE METHOD BEHAVIOR:
   - Private methods are NOT overridden and NOT inherited.
   - They use static binding.

4) FINAL METHOD BEHAVIOR:
   - Final methods CANNOT be overridden.
   - They do NOT participate in runtime polymorphism.

5) MEMORY LAYOUT:
   
   - One object is created in the HEAP.
   - Contains "Parent Space" and "Child Space".
   - Shadowed variables coexist; 'super' and 'this' act as pointers.

===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: Ambiguity Check
- Create 'Parent p = new Child();'. 
- Try to call a method that exists only in Child. Observe Step 1 Compile Error.

DRILL 2: Final/Static Constraint
- Attempt to @Override a static or final method in Car. Observe the error.

DRILL 3: Shadowing Practice
- In Child, print 'topSpeed', 'this.topSpeed', and 'super.topSpeed'. 
- Confirm which one matches the Reference Type vs Object Type.

===============================================================================
END
===============================================================================
*/