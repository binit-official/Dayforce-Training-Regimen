package basics;

import java.io.IOException;

/*
===============================================================================
ABSTRACTION MASTER FILE: THE ARCHITECT'S BLUEPRINT (ABSTRACT CLASSES)
(Complete Conceptual & Technical Breakdown - No Compromise)
===============================================================================

1. WHY AND WHEN TO DECLARE AN ABSTRACT CLASS?
    - WHY: To define a common template for a group of objects but prevent 
      anyone from creating a "generic" version of that object.
    - WHEN: When you have a "Base Concept" (e.g., Shape, Animal, Appliance) 
      that shouldn't exist by itself. You only want specific versions.
    - HOW: Use the 'abstract' keyword before the class definition.

2. ABSTRACT CLASS VS INTERFACE (PREVIEW)
    - Abstract Class: Can have state (instance variables) and constructors. 
      Supports both abstract & concrete methods. Single inheritance only.
    - Interface: Historically only abstract methods/constants. Supports multiple 
      inheritance. Used for "capabilities" (Can-Do).
===============================================================================
*/

// =========================================================================
// 1) THE ABSTRACT BASE (The Root)
// =========================================================================

abstract class Appliance {
    // 🧠 ULTRA-PRECISION MEMORY CLARIFICATION
    /* * Instance variables declared in an abstract class become part of the 
     * concrete subclass object’s memory layout in the HEAP.
     */
    String brand;
    boolean isOn;

    // 2) STATIC BLOCK IN ABSTRACT CLASS
    static {
        /* Runs once when class is loaded by the JVM, even if it's abstract. */
        System.out.println("[Appliance] Static Block: Class Loaded.");
    }

    // 1) INSTANCE INITIALIZATION BLOCK (IIB)
    {
        /* Runs when a child object is created, because the abstract class 
         * constructor executes during the instantiation of the concrete subclass.
         */
        System.out.println("[Appliance] IIB: Initializing instance segments.");
    }

    // -------------------------------------------------------------------------
    // A. CONSTRUCTORS (Chaining Logic)
    // -------------------------------------------------------------------------
    Appliance(String brand) {
        this.brand = brand;
        this.isOn = false;
        System.out.println("[Appliance] Parent constructor executed.");
    }

    // -------------------------------------------------------------------------
    // B. ABSTRACT METHODS (The "Mandate")
    // -------------------------------------------------------------------------
    /*
     * - 2) CANNOT be 'static': Static methods belong to class, not object.
     * - 3) CANNOT be 'final': final prevents overriding; abstract forces it.
     * - 4) CAN be 'synchronized': The child will implement the thread-safety logic.
     * - 4) CANNOT be 'native' or 'private'.
     * - 3) CAN throw Exceptions: Child can throw same or narrower exceptions.
     */
    public abstract void performFunction() throws IOException; 

    // -------------------------------------------------------------------------
    // C. CONCRETE, FINAL & STATIC METHODS
    // -------------------------------------------------------------------------
    /* 3) Abstract class CAN contain final methods (logic that cannot change).
     * 4) Abstract class CAN contain static methods (class-level helpers).
     */
    final void showBrand() { 
        System.out.println("Brand Identity: " + brand); 
    }

    static void globalSafetyWarning() {
        System.out.println("STATIC WARNING: Unplug before cleaning.");
    }

    void turnOn() {
        isOn = true;
        System.out.println(brand + " is now ON.");
    }

    // D. METHOD NAME VS CLASS NAME: Bad practice, but syntactically allowed.
    void Appliance() { 
        System.out.println("Method named after class - confusing but legal.");
    }
}

// =========================================================================
// 2) MULTILEVEL ABSTRACTION & CONSTRUCTOR PARAMETERS
// =========================================================================

/* 7) STRICT RULE: If a subclass does not implement all abstract methods, 
 * it MUST also be declared abstract.
 */
abstract class KitchenAppliance extends Appliance {
    KitchenAppliance(String brand) {
        super(brand);
    }
    
    abstract void clean(); 
}

class Processor {
    // 5) ABSTRACT CLASS AS CONSTRUCTOR/METHOD PARAMETER
    /* Even though abstract classes cannot be instantiated, they are used as 
     * reference types to pass any concrete child object (Polymorphism).
     */
    void process(Appliance app) throws IOException {
        System.out.println("Processing appliance reference...");
        app.performFunction();
    }
}

// =========================================================================
// 3) THE CONCRETE CLASS (Implementation)
// =========================================================================

class Oven extends KitchenAppliance {
    Oven(String brand) {
        super(brand); 
    }

    // 9) VISIBILITY RULE: Overriding cannot reduce visibility.
    @Override
    public void performFunction() throws IOException {
        System.out.println("Oven heating at 180°C.");
    }

    @Override
    void clean() {
        System.out.println("Oven cleaning cycle started.");
    }
}

// =========================================================================
// 5) THE EXECUTION ENGINE
// =========================================================================

public class AbstractClass {
    public static void main(String[] args) throws IOException {
        
        /* * CONSTRUCTOR EXECUTION ORDER:
         * 1. Memory Allocated in Heap.
         * 2. IIB of Abstract Class runs.
         * 3. Parent Constructor Runs.
         * 4. Child Constructor Runs.
         */

        // 10) REFERENCE POLYMORPHISM
        Appliance ref = new Oven("Samsung");
        
        Processor proc = new Processor();
        proc.process(ref); // Passing abstract reference to method
    }
}

/*
===============================================================================
CORE CONCEPTUAL SUMMARY
===============================================================================

1) 5) ABSTRACT + FINAL IS ILLEGAL:
   Logically opposite. One requires inheritance, the other forbids it.

2) SUPER VS THIS IN CONSTRUCTOR:
   - One MUST be the first statement.
   - You CANNOT use both in the same constructor.
   - If parent has no no-arg constructor, child MUST use explicit super(args).

3) OBJECT CLASS:
   - All abstract classes implicitly extend java.lang.Object.

4) MEMORY & STATIC BINDING:
   
   - Static methods and blocks run at class loading time.
   - Instance variables are part of the concrete subclass object's memory.

===============================================================================
EDGE CASES & GOTCHAS
===============================================================================

1) CAN WE HAVE AN ABSTRACT CLASS WITHOUT ABSTRACT METHODS?
   - YES. Prevents instantiation but allows inheritance of shared logic.

2) CAN WE HAVE AN ABSTRACT METHOD IN A NON-ABSTRACT CLASS?
   - NO. Compiler error.

3) SUPER VS THIS MEMORY:
   - 'this' refers to the current object. 'super' refers to the parent slice.

===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: The Multi-Step implementation
Create Abstract A (2 methods) -> Abstract B (implements 1) -> Concrete C (implements last).

DRILL 2: IIB and Static Block order
Create an abstract class with both. Create two child objects. 
Observe that static runs ONCE, IIB runs TWICE.

DRILL 3: Exception Handling
Try to make an abstract method throw IOException, and make the child 
throw a broader 'Exception'. Note the compile error.

===============================================================================
END
===============================================================================
*/