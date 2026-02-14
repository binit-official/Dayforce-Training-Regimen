package basics;

/*
===============================================================================
INHERITANCE MASTER FILE: THE "IS-A" RELATIONSHIP & MEMORY ARCHITECTURE
(Complete Technical Breakdown - No Compromise)
===============================================================================

This file covers the absolute logic of Inheritance, from memory allocation 
to the strict rules governing what can and cannot be inherited or overridden.
===============================================================================
*/

// =========================================================================
// 1) THE ULTIMATE PARENT: java.lang.Object
// =========================================================================
/* * RULE: Every class in Java implicitly extends java.lang.Object.
 * - If you don’t write 'extends', it automatically extends Object.
 * - Methods like toString(), hashCode(), and equals() come from here.
 */

// 9) FINAL CLASS CONSTRAINT
/*
 * If a class is declared 'final', it cannot be extended.
 * Example: final class Secret { } -> class Hack extends Secret { } // ERROR
 */

class Vehicle extends Object { 
    public String brand;
    protected int topSpeed = 100;

    // 1) EXPLICIT “WHAT IS INHERITED” SECTION
    /*
     * - Public/Protected members: Inherited always.
     * - Default members: Inherited ONLY within the same package.
     * - Private members: NOT inherited.
     * - Constructors: NOT inherited.
     * - Static members: Inherited (visible) but not polymorphic.
     * *PRECISION: Static members belong to the class, not the object. They are
     * accessible in the child class but are resolved using the class name,
     * not the object identity.
     */

    // 3) SUPER() CONSTRUCTOR RULES
    /*
     * - super() or this() must be the FIRST statement.
     * - You cannot use both in the same constructor.
     * - If Parent has NO no-arg constructor, child MUST call super(args) explicitly.
     */
    Vehicle() {
        System.out.println("[Vehicle] Parent No-arg Constructor Executed");
        this.brand = "Generic";
    }

    Vehicle(String brand) {
        this.brand = brand;
        System.out.println("[Vehicle] Parent Parameterized Constructor Executed");
    }

    static void checkStatus() {
        System.out.println("Vehicle Status: OK (Static)");
    }

    final void showIdentity() {
        System.out.println("I am a Vehicle.");
    }

    void move() {
        System.out.println("Vehicle is moving...");
    }
}

// =========================================================================
// 2) THE SUB CLASS (CHILD)
// =========================================================================
class Car extends Vehicle {
    
    // 8) FIELDS ARE NEVER OVERRIDDEN
    /* * Fields are hidden (shadowed).
     * Resolution is always based on the Reference Type.
     */
    int topSpeed = 200; 
    String model;

    // 2) CONSTRUCTOR EXECUTION ORDER INTERNALLY
    /*
     * Execution when 'new Car()' happens:
     * 1. Memory is allocated for the object in Heap.
     * 2. Parent constructor runs (via super call).
     * 3. Child constructor runs.
     */
    Car() {
        super(); 
        System.out.println("[Car] Child No-arg Constructor Executed");
    }

    Car(String brand, String model) {
        super(brand); 
        this.model = model;
        System.out.println("[Car] Child Parameterized Constructor Executed");
    }

    // 6) EXPLICIT “METHOD SIGNATURE VS METHOD BODY” RULE
    /* * Overriding requires: Same method name, same parameter list, compatible return type.
     * NOTE: Changing parameters = OVERLOADING, not overriding.
     */

    // 4) ACCESS LEVEL CONSTRAINT
    /*
     * RULE: Access level can stay the same or become more permissive, but never more restrictive.
     * - Parent: protected -> Child: public (OK)
     * - Parent: public -> Child: protected (NOT OK)
     */
    @Override
    public void move() {
        System.out.println("Car is driving...");
    }

    static void checkStatus() {
        System.out.println("Car Status: Racing (Static)");
    }

    void display() {
        // 7) SUPER VS THIS MEMORY MEANING
        /*
         * this  -> reference to the current object instance.
         * super -> reference to the parent portion of the current object instance.
         */
        System.out.println("Parent Speed: " + super.topSpeed); 
        System.out.println("Child Speed: " + this.topSpeed); 
        
        super.move(); 
    }
}

// =========================================================================
// 3) EXECUTION ENGINE
// =========================================================================
public class Inheritance {

    /*
     * 1) EXPLICIT DUAL-STAGE RESOLUTION EXPLANATION
     * ---------------------------------------------------------------------
     * RUNTIME POLYMORPHISM PROCESS:
     * Step 1: Compiler checks REFERENCE TYPE.
     * Step 2: JVM checks ACTUAL OBJECT TYPE.
     * ---------------------------------------------------------------------
     */

    public static void main(String[] args) {
        
        // 5) UPCASTING VS DOWNCASTING
        Vehicle v = new Car("Toyota", "Supra"); // Upcasting (Implicit/Automatic)

        // 10) PARENT REFERENCE LIMITATION
        /* * Parent reference can ONLY access members defined in the parent type.
         * It restricts visible methods even if the object is a child.
         */
        
        // 4) INSTANCEOF SAFETY
        /* * RULE: Always check with 'instanceof' before downcasting to avoid ClassCastException.
         */
        if (v instanceof Car) {
            Car c = (Car) v; // Downcasting (Explicit/Manual)
            System.out.println("Model: " + c.model);
        }

        System.out.println("\n--- Resolution Tests ---");
        System.out.println("Speed (Ref Type): " + v.topSpeed); // Fields are not polymorphic (100)
        v.checkStatus(); // Static is not polymorphic (Vehicle Version)
        v.move();        // Instance IS polymorphic (Car version)
    }
}

/*
===============================================================================
INTERNAL FUNCTIONING & ARCHITECTURE
===============================================================================

1) 6) WHY MULTIPLE INHERITANCE IS NOT ALLOWED:
   - Ambiguity in method resolution (Diamond Problem).
   - Memory layout complexity.
   - Constructor conflict.


2) PRIVATE METHOD BEHAVIOR:
   - Private methods are NOT overridden and NOT inherited.
   - They use static binding.

3) MEMORY LAYOUT:
   
   One block in the HEAP contains segments for both Vehicle and Car. 

===============================================================================
SUMMARY TABLE: DISPATCH LOGIC
===============================================================================

| Component       | Binding      | Resolution Basis | Inherited?       |
|-----------------|--------------|------------------|------------------|
| Instance Method | Dynamic      | Object Type      | Yes              |
| Static Method   | Static       | Reference Type   | Yes (Accessible) |
| Fields (Vars)   | Static       | Reference Type   | Yes (Shadowed)   |
| Private Method  | Static       | Reference Type   | NO               |
| Constructor     | Static       | Class Type       | NO               |

===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: The "No-Arg" Error
Remove Vehicle() and add Vehicle(String b). Observe the 'Car' error.

DRILL 2: Downcasting Safety
Try '(Car) new Vehicle()' without an 'instanceof' check.

DRILL 3: Visibility Reduction
Try changing 'public' move in 'Car' to 'protected'. Note the compiler error.

===============================================================================
END
===============================================================================
*/