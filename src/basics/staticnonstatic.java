package basics;

/*
===============================================================================
PURE STATIC vs NON-STATIC MASTER FILE
(No Inheritance, No OOP Complexity)
===============================================================================

This file covers:

1) Static Variable (Memory + Lifecycle)
2) Instance Variable (Memory + Lifecycle)
3) Static Block (SIB)
4) Instance Initialization Block (IIB)
5) Constructor Execution Order
6) Static vs Non-Static Access Rules
7) this keyword behavior
8) Compile-time Error Cases
9) Execution Flow Step-by-Step
===============================================================================
*/

class StaticNonStaticCore {

    // =========================================================================
    // 1) STATIC VARIABLES (Class Level)
    // =========================================================================

    /*
     * Stored in Method Area (Metaspace in Java 8+)
     * Only ONE copy exists per class
     * Created when class loads
     */

    static int staticCounter = initializeStatic();

    static String companyName = "Dayforce";

    // =========================================================================
    // 2) INSTANCE VARIABLES (Object Level)
    // =========================================================================

    /*
     * Stored in HEAP
     * Each object gets its own copy
     * Created when object is created
     */

    int employeeId = initializeInstance();
    String employeeName;

    // =========================================================================
    // 3) STATIC BLOCK (SIB)
    // =========================================================================

    /*
     * Runs ONLY ONCE
     * Runs when class is loaded
     * Runs BEFORE main()
     */

    static {
        System.out.println("[SIB] Static Block Executed");
        staticCounter = 10;
    }

    // =========================================================================
    // 4) INSTANCE INITIALIZATION BLOCK (IIB)
    // =========================================================================

    /*
     * Runs BEFORE constructor
     * Runs EVERY TIME object is created
     */

    {
        System.out.println("[IIB] Instance Block Executed");
    }

    // =========================================================================
    // 5) CONSTRUCTOR
    // =========================================================================

    StaticNonStaticCore(int id, String name) {

        System.out.println("[Constructor] Executed");

        this.employeeId = id;
        this.employeeName = name;

        staticCounter++;
    }

    // =========================================================================
    // 6) STATIC VARIABLE INITIALIZER METHOD
    // =========================================================================

    static int initializeStatic() {
        System.out.println("[Static Variable Initialized]");
        return 5;
    }

    // =========================================================================
    // 7) INSTANCE VARIABLE INITIALIZER METHOD
    // =========================================================================

    int initializeInstance() {
        System.out.println("[Instance Variable Initialized]");
        return -1;
    }

    // =========================================================================
    // 8) STATIC METHOD
    // =========================================================================

    static void staticMethod() {

        System.out.println("Inside staticMethod");

        System.out.println("Company: " + companyName);
        System.out.println("StaticCounter: " + staticCounter);

        // ERROR CASE 1:
        // System.out.println(employeeId);
        // Cannot make a static reference to non-static variable

        // ERROR CASE 2:
        // System.out.println(this.employeeId);
        // Cannot use 'this' in static context

        // INDIRECT ACCESS (Allowed)
        StaticNonStaticCore obj = new StaticNonStaticCore(999, "Temp");
        System.out.println("Access via object: " + obj.employeeId);
    }

    // =========================================================================
    // 9) NON-STATIC METHOD
    // =========================================================================

    void nonStaticMethod() {

        System.out.println("Inside nonStaticMethod");

        // Can access static directly
        System.out.println("Company: " + companyName);

        // Can access instance directly
        System.out.println("EmployeeId: " + employeeId);

        // Internally compiler treats it as:
        // System.out.println(this.employeeId);
    }

    // =========================================================================
    // 10) MAIN METHOD
    // =========================================================================

    public static void main(String[] args) {

        System.out.println("===== MAIN START =====");

        /*
         * EXECUTION ORDER:
         * 1) Static variables initialized
         * 2) Static blocks executed
         * 3) main() runs
         */

        System.out.println("Access static without object:");
        System.out.println(companyName);
        System.out.println(staticCounter);

        System.out.println("\n===== CREATE OBJECT 1 =====");
        StaticNonStaticCore obj1 = new StaticNonStaticCore(101, "Binit");
        obj1.nonStaticMethod();

        System.out.println("\n===== CREATE OBJECT 2 =====");
        StaticNonStaticCore obj2 = new StaticNonStaticCore(102, "Alex");
        obj2.nonStaticMethod();

        System.out.println("\n===== STATIC METHOD CALL =====");
        staticMethod();

        System.out.println("\n===== MODIFY STATIC =====");
        companyName = "Ceridian";
        obj1.nonStaticMethod();
        obj2.nonStaticMethod();

        System.out.println("\n===== NULL STATIC CALL =====");

        StaticNonStaticCore nullRef = null;
        nullRef.staticMethod();
        // Works because static resolved at compile-time

        // nullRef.nonStaticMethod(); // NullPointerException
    }
}

/*
===============================================================================
IMPORTANT CONCEPT SUMMARY
===============================================================================

1) Static variable:
   - ONE copy
   - Created when class loads
   - Stored in Method Area

2) Instance variable:
   - Multiple copies
   - Created when object created
   - Stored in Heap

3) Static method:
   - Cannot use this
   - Cannot directly access instance variables

4) Non-static method:
   - Can access both static and instance

5) SIB runs once
6) IIB runs every object creation
7) Constructor runs after IIB
8) Static exists without object
9) Static method can be called using null reference

===============================================================================
CODING DRILLS (YOU MUST IMPLEMENT)
===============================================================================

DRILL 1:
Create class CounterTest
- static int count
- increment in constructor
- create 3 objects
- print count

DRILL 2:
Create class Car
- static String brand
- instance String model
- change brand using one object
- check if other object sees change

DRILL 3:
Create class TestAccess
- static int a = 10
- int b = 20
- write static method and try accessing b
- observe compile error

DRILL 4:
Print execution order clearly:
Add multiple static blocks and instance blocks
Observe order carefully

===============================================================================
END
===============================================================================
*/