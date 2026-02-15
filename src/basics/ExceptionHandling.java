//package basics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

/*
===============================================================================
EXCEPTION HANDLING MASTER FILE: THE SAFETY NET
(Complete Technical Architecture & Runtime Error Management)
===============================================================================

1. WHAT IS EXCEPTION HANDLING?
    - Mechanism to handle runtime errors so the normal flow remains uninterrupted.
    - Prevents abrupt program termination (crashes).

2. THE HIERARCHY (The Throwable Family)
    - Throwable (Root)
        ├── Error (Critical system failure - No-catch zone)
        └── Exception (Application-level failure - Catchable)
            ├── RuntimeException (Unchecked - Logic flaws)
            └── Other Exceptions (Checked - Environment/Input flaws)
===============================================================================
*/

// =========================================================================
// 1) CUSTOM EXCEPTIONS (Checked & Unchecked)
// =========================================================================

// A. Checked Custom Exception (Inherits Exception)
class InvalidAgeException extends Exception {
    InvalidAgeException(String msg) {
        super(msg); 
    }
}

// B. Unchecked Custom Exception (Inherits RuntimeException)
class InvalidAgeRuntimeException extends RuntimeException {
    InvalidAgeRuntimeException(String msg) {
        super(msg);
    }
}

// =========================================================================
// 2) EXCEPTION OVERRIDING RULES (Inheritance)
// =========================================================================
/* * RULE: If the Superclass method throws a CHECKED exception:
 * - Subclass overridden method can throw the SAME exception, a SUBCLASS 
 * of that exception, or NO exception at all.
 * - Subclass CANNOT throw a broader or new checked exception.
 */
class Parent {
    void msg() throws IOException { System.out.println("Parent"); }
}

class Child extends Parent {
    @Override
    void msg() throws FileNotFoundException { // OK: FileNotFound is a subclass of IOException
        System.out.println("Child");
    }
}

public class ExceptionHandling {

    // 7) THROWING EXCEPTIONS FROM CONSTRUCTORS
    ExceptionHandling() throws IOException {
        System.out.println("Constructor might throw IOException during setup.");
    }

    // =========================================================================
    // 3) FINALLY-RETURN OVERRIDE (Corner Case)
    // =========================================================================
    /* * RULE: If both try and finally have return statements, the finally 
     * return overrides everything else.
     */
    static int returnTest() {
        try {
            return 10;
        } finally {
            return 20; // This value is what actually gets returned
        }
    }

    static void checkedExample() throws IOException {
        throw new IOException("Device failure");
    }

    public static void main(String[] args) {
        
        // =====================================================================
        // 4) TRY-WITH-RESOURCES & SUPPRESSED EXCEPTIONS
        // =====================================================================
        /* * SUPPRESSED EXPLANATION: If an exception occurs in the try block 
         * AND another occurs while auto-closing the resource, the try-block 
         * exception is thrown. The close() exception is "Suppressed".
         */
        try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
            System.out.println(br.readLine());
        } catch (IOException e) {
            System.out.println("Caught: " + e.getMessage());
            // Accessing suppressed exceptions:
            for (Throwable t : e.getSuppressed()) {
                System.out.println("Suppressed: " + t);
            }
        }

        // =====================================================================
        // 5) STACK TRACE & METHODS
        // =====================================================================
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            /* STACK TRACE MEANING: It is a report showing the list of method 
             * calls that the application was in the middle of when an 
             * Exception was thrown. It helps find the exact line of failure.
             */
            System.out.println("Message: " + e.getMessage()); // "by zero"
            System.out.println("ToString: " + e.toString()); // "java.lang.ArithmeticException: / by zero"
            e.printStackTrace(); // Full log of method calls
        }


        try {
            throw new SQLException();
        } catch (SQLException  e) {
            System.out.println("Handled: " + e.getClass().getSimpleName());
        }

        // =====================================================================
        // 7) ERROR PROPAGATION & CHAINING
        // =====================================================================
        try {
            methodA();
        } catch (IOException e) {
            // Chaining: Keeping the original 'e' as the cause
            throw new RuntimeException("High-level failure", e);
        }
    }

    static void methodA() throws IOException {
        try {
            checkedExample();
        } catch (IOException e) {
            System.out.println("Logging locally...");
            throw e; // Rethrowing
        }
    }
}

/*
===============================================================================
CRYSTAL CLEAR CONCEPTS (DETAILED)
===============================================================================

1) ERROR vs EXCEPTION:
   - Error: OutOfMemory, StackOverflow. Unrecoverable JVM level issues.
   - Exception: Recoverable code/environment level issues.


2) THE "NO TRY, NO FINALLY" RULE:
   - You can have 'try-finally' without 'catch'.
   - You CANNOT have 'try' alone.

3) FINALLY BLOCK EDGE CASES:
   Finally does NOT run if:
   - System.exit(0) is called.
   - JVM crashes or Power failure occurs.


4) THROW vs THROWS:
   - throw: Keyword to manually trigger an exception inside a method.
   - throws: Keyword in method signature to delegate checked exceptions to caller.

5) INTERNAL LOGIC: THE CALL STACK:
   JVM searches the stack for a handler. If none is found up to main(), the 
   Default Exception Handler terminates the program.


===============================================================================
CODING DRILLS
===============================================================================

DRILL 1: Custom Exception Chaining
- Create a Custom Exception. Catch a NullPointerException and throw your 
  Custom Exception while passing the original one to the constructor.

DRILL 2: Overriding Constraint
- Try to make a child method throw 'Exception' when the parent throws 'IOException'.
- Observe the compiler error regarding weaker access/incompatible throws.

DRILL 3: Suppressed Access
- Use a try-with-resources block. Manually throw an error in the try block 
  and use getSuppressed() in the catch to see if anything was hidden.

===============================================================================
END
===============================================================================
*/