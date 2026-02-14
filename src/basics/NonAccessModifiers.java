package basics;

/*
===============================================================================
NON-ACCESS MODIFIERS MASTER FILE
(Behavioral Definitions)
===============================================================================

This file covers:
1) final (Variables, Methods, Classes)
2) abstract (Classes, Methods)
3) strictfp (Floating point consistency)
4) Static (Revisited briefly for context)

Important: Unlike Access Modifiers (where you can only pick ONE), you can 
combine multiple Non-Access modifiers (e.g., public static final).
===============================================================================
*/

// =========================================================================
// 1) THE 'final' MODIFIER: IMMUTABILITY
// =========================================================================
/*
 * CORE DEFINITION: 'final' means "Restriction." It prevents modification.
 * * - Final Variable: Value cannot be changed (Constant).
 * - Final Method: Cannot be Overridden by a subclass.
 * - Final Class: Cannot be Inherited (Extended).
 */

final class FinalBase { // This class cannot be extended by any other class
    
    // Final Instance Variable
    final int THRESHOLD = 5;
    
    // Blank Final Variable (Must be initialized in constructor)
    final int BLANK_FINAL;

    FinalBase() {
        BLANK_FINAL = 10; // Illegal to leave uninitialized if declared final
    }

    final void securityCheck() {
        // This logic is "locked." No subclass can change how this works.
        System.out.println("Executing fixed security logic...");
    }
}

// =========================================================================
// 2) THE 'abstract' MODIFIER: INCOMPLETENESS
// =========================================================================
/*
 * CORE DEFINITION: 'abstract' means "Idea but no Implementation."
 * * - Abstract Class: Cannot be instantiated (cannot use 'new'). 
 * It exists only to be a blueprint.
 * - Abstract Method: Has no body. Forces the child class to provide logic.
 */

abstract class Blueprint {
    
    // Abstract Method: No curly braces!
    abstract void calculateTax();

    // Can an abstract class have regular methods? Yes!
    void commonLogic() {
        System.out.println("This logic is shared by all children.");
    }
}

// =========================================================================
// 3) THE 'strictfp' MODIFIER: PRECISION (Corner Case)
// =========================================================================
/*
 * CORE DEFINITION: "Strict Floating Point."
 * * Floating-point math (double/float) can vary slightly between different 
 * CPU architectures (Intel vs ARM). 'strictfp' ensures the math result 
 * is EXACTLY the same on every platform.
 */

strictfp class ScientificCalculator {
    double calculateOrbit() {
        return 0.0000000000007 * 1.5; // Results will be identical everywhere
    }
}

// =========================================================================
// INTEGRATED CONCEPT: FINAL + STATIC
// =========================================================================

class Constants {
    // This is how "Constants" are created in Java.
    // Static = One copy in memory.
    // Final = Cannot be changed.
    public static final double PI = 3.141592653589793;
}

/*
===============================================================================
CORNER CASES & COMPILE-TIME ERRORS
===============================================================================

1) THE ABSTRACT-FINAL PARADOX:
   A class or method CANNOT be both 'abstract' and 'final'.
   - Abstract says: "Please inherit/override me."
   - Final says: "You are forbidden from inheriting/overriding me."
   Result: Compile-time Error.

2) THE PRIVATE-ABSTRACT CONFLICT:
   Methods cannot be 'private abstract'. 
   Since a private method isn't visible to child classes, they can't 
   override it. But 'abstract' forces them to override it. 
   Result: Compile-time Error.

3) FINAL REFERENCE vs FINAL CONTENT:
   If an object reference is final:
   final StringBuilder sb = new StringBuilder("Hello");
   sb.append(" World"); // WORKS (The content inside the object changed)
   sb = new StringBuilder("New"); // ERROR (The reference itself is locked)

4) INITIALIZATION RULE:
   A final variable does NOT get a default value (like 0 or null). 
   You MUST initialize it explicitly.

===============================================================================
EXECUTION FLOW & MEMORY
===============================================================================



- Final variables are often optimized by the JVM (Inlining). Since the 
  compiler knows the value will never change, it replaces the variable name 
  with the actual value in the bytecode for speed.
- Abstract classes stay in the Metaspace as "Templates" but never occupy 
  Heap space because they can never be instantiated.

===============================================================================
CODING DRILLS
===============================================================================

DRILL 1:
Create a class 'Universe'.
- Define a 'static final String GALAXY = "Milky Way";'
- Try to change GALAXY in the main method. Observe the error.

DRILL 2:
Create an abstract class 'Vehicle' with an abstract method 'move()'.
- Create a class 'Car' that extends 'Vehicle'.
- Try to compile 'Car' WITHOUT implementing 'move()'.
- Observe the error: "Car is not abstract and does not override abstract method."

DRILL 3:
Try to create a class that extends 'java.lang.String'.
- (Spoiler: String is a 'final' class in Java).
- Observe the error: "Cannot inherit from final String."

===============================================================================
END
===============================================================================
*/