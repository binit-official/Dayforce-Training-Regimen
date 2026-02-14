package basics;

/*
===============================================================================
INTERFACE MASTER FILE: THE PURE CONTRACT
(100% Abstraction & Multiple Inheritance Architecture)
===============================================================================

1. WHAT IS AN INTERFACE?
    - An interface is a blueprint of a class that defines "What a class can do" 
      rather than "What a class is."
    - It is a 100% abstract entity (pre-Java 8) used to achieve loose coupling.
    - Keyword: 'interface'. Implementation Keyword: 'implements'.

2. ABSTRACT CLASS vs INTERFACE
    - Abstract Class: Partial abstraction (0-100%). Can have state (instance vars) 
      and constructors. Follows "Is-A" (Single Inheritance).
    - Interface: Historically 100% abstract. Cannot have instance variables or 
      constructors. Follows "Can-Do" (Multiple Inheritance).
===============================================================================
*/

// =========================================================================
// 1) INTERFACE INHERITANCE (Interface extends Interface)
// =========================================================================
/* * RULE: An interface can EXTEND another interface.
 * - It can even extend MULTIPLE interfaces simultaneously.
 * - interface A extends B, C { ... } // LEGAL
 */

interface PowerSource {
    void consumeEnergy();
}

interface SmartDevice extends PowerSource {
    // 4) INTERFACE VARIABLE BEHAVIOR
    /* * All variables are implicitly: public static final (Constants).
     * - Must be initialized immediately.
     * - Accessed via: SmartDevice.CATEGORY.
     */
    String CATEGORY = "Electronics"; 

    void powerOn(); 
}

interface Camera {
    void takePhoto();
}

// 4) CONSTANT NAME CONFLICT CASE
interface A { int MAX = 10; }
interface B { int MAX = 20; }

// =========================================================================
// 2) MULTIPLE INHERITANCE & STRUCTURAL RULES
// =========================================================================

abstract class Handheld {
    String brand;
    Handheld(String brand) { this.brand = brand; }
}

class SmartPhone extends Handheld implements SmartDevice, Camera, A, B {

    SmartPhone(String brand) {
        super(brand); // Constructor chaining only goes to Handheld class
    }

    // 5) CONSTRUCTOR CHAIN CLARIFICATION
    /* * RULE: Interface has NO constructor. 
     * - Even though SmartPhone implements SmartDevice, no constructor chaining 
     * happens for the interface. Only Handheld -> Object is chained.
     */

    @Override
    public void consumeEnergy() { System.out.println("Consuming battery..."); }

    @Override
    public void powerOn() { System.out.println("Smartphone powering on..."); }

    @Override
    public void takePhoto() { System.out.println("Photo captured."); }

    void showConstants() {
        // System.out.println(MAX); // ERROR: Reference to MAX is ambiguous
        System.out.println(A.MAX); // Must qualify using InterfaceName
        System.out.println(B.MAX);
    }
}

// =========================================================================
// 3) NESTING & REFERENCE TYPES
// =========================================================================

class Factory {
    interface QualityCheck {
        boolean isApproved();
    }

    // 6) INTERFACE AS RETURN TYPE
    SmartDevice getDevice() {
        return new SmartPhone("Apple");
    }

    // 3) INTERFACE AS PARAMETER (Loose Coupling)
    void activate(SmartDevice device) {
        device.powerOn(); // Can accept any class that implements SmartDevice
    }
}

// =========================================================================
// 4) INTERFACE RESTRICTIONS (THE "CANNOT" LIST)
// =========================================================================
/*
 * ❌ No Constructors (Interfaces cannot be instantiated)
 * ❌ No Instance Variables (Only static constants)
 * ❌ No Instance Initialization Blocks (IIB)
 * ❌ No Static Initialization Blocks (SIB)
 * ❌ No Private Methods (Pre-Java 9)
 * ❌ No Final/Native Methods
 */

public class Interface {
    public static void main(String[] args) {
        
        // 2) INSTANTIATION ERROR CASE
        // SmartDevice obj = new SmartDevice(); // ERROR: Interface cannot be instantiated
        /* Why? No constructor, no method bodies, it is only a contract. */

        // 1) INTERFACE REFERENCE LIMITATION
        SmartDevice d = new SmartPhone("Samsung");
        d.powerOn(); // WORKS: Defined in SmartDevice
        // d.takePhoto(); // COMPILE ERROR! 
        /* Why? Even if the object is a SmartPhone, the reference 'd' is 
           SmartDevice. Reference type controls visible methods at compile time. */

        // 8) WHY MULTIPLE INHERITANCE IS SAFE
        /* 1. No method bodies -> No logic ambiguity (Diamond Problem).
         * 2. No fields -> No memory state conflict.
         * 3. No constructors -> No initialization conflict.
         */
    }
}

/*
===============================================================================
CRYSTAL CLEAR CONCEPTS & VISUALIZATION
===============================================================================



1) COMPILER DEFAULT BEHAVIOR:
   - If a class implements an interface but skips a method, it MUST be 'abstract'.

2) THE VISIBILITY TRAP:
   - Interface methods are public by default. Overriding them without 
     the 'public' keyword causes a "weaker access" error.

3) INTERFACE VARIABLES MEMORY:
   - Stored in Metaspace (Class Metadata), NOT in the object heap memory.

===============================================================================
DETAILED PROGRAM FLOW
===============================================================================

1. SmartDevice loads: CATEGORY initialized in Metaspace.
2. new SmartPhone() starts: Heap memory allocated for 'brand'.
3. Handheld constructor runs (super call).
4. SmartPhone constructor runs.
5. Reference 'd' only sees methods declared in SmartDevice type.



===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: The Reference Wall
- Create 'SmartDevice sd = new SmartPhone("X");'
- Try calling 'sd.showConstants()'. Observe the error.
- Downcast '((SmartPhone)sd).showConstants()' to fix it.

DRILL 2: Structural Constraints
- Try to make an interface 'implements' another interface. (Should be extends).

DRILL 3: Constant Ambiguity
- Implement two interfaces with the same constant name and try to access 
  the constant without the interface name prefix.

===============================================================================
END
===============================================================================
*/