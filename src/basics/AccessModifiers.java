package basics;

/*
===============================================================================
ACCESS MODIFIERS: THE CIRCLE OF TRUST
(Core Fundamentals - Pre-OOP Focus)
===============================================================================

This file covers the 4 Levels of Visibility:
1) Private: "Me Only" (Class Level)
2) Default: "My Roommates" (Package Level)
3) Protected: "My Family" (Package + Subclasses - Brief mention)
4) Public: "The World" (Global Level)

Corner Cases Covered:
- Top-level Class Restrictions (Why can't a class be private?)
- The "Default" keyword vs "Default" access.
- Accessing members from different packages.
===============================================================================
*/

// TOP-LEVEL RULE: 
// A top-level class can ONLY be 'public' or 'default'.
// You cannot mark a standalone class 'private' or 'protected'. 
// (Senior Note: Inner classes can be private, but that's for later).

public class AccessModifiers {

    // =========================================================================
    // 1) PRIVATE (-): THE BOLTED DOOR
    // =========================================================================
    /*
     * Visibility: ONLY within the same class.
     * Purpose: Hide sensitive data (Passwords, Internal counters).
     * Senior Tip: Always start with 'private' and only open it if necessary.
     */
    private int privateId = 101;

    private void privateSecret() {
        System.out.println("This is a private secret. Only I can call this.");
    }

    // =========================================================================
    // 2) DEFAULT (No Keyword): THE NEIGHBORHOOD
    // =========================================================================
    /*
     * Visibility: ONLY within the SAME PACKAGE (basics).
     * Also known as "Package-Private".
     * Note: There is NO 'default' keyword used for access. You just leave it blank.
     */
    int packageData = 200; 

    void packageMethod() {
        System.out.println("Anyone in the 'basics' package can see me.");
    }

    // =========================================================================
    // 3) PROTECTED (#): THE FAMILY LEGACY
    // =========================================================================
    /*
     * Visibility: Same package + Subclasses (even in different packages).
     * Since we aren't doing OOP/Inheritance yet, think of this as 
     * 'Default+' (Default + one extra superpower).
     */
    protected String legacyCode = "V1.0.0";

    // =========================================================================
    // 4) PUBLIC (+): THE BILLBOARD
    // =========================================================================
    /*
     * Visibility: EVERYWHERE. Any class in any package can access this.
     */
    public String website = "www.dayforce.com";

    public void showOff() {
        System.out.println("Global access granted.");
    }

    // =========================================================================
    // EXECUTION & ACCESS RULES
    // =========================================================================

    public static void main(String[] args) {
        AccessModifiers obj = new AccessModifiers();

        // 1. Internal Access: Everything is visible inside the same class.
        System.out.println("Private ID: " + obj.privateId); // WORKS
        obj.privateSecret(); // WORKS

        // 2. Cross-Class Access (Same Package):
        // If we created a class 'Test' in the same 'basics' package:
        // - Test can see: public, protected, default.
        // - Test CANNOT see: private.

        // 3. Cross-Package Access:
        // If we created a class 'External' in package 'advanced':
        // - External can see: public.
        // - External CANNOT see: private, default, protected*.
    }
}

/*
===============================================================================
THE ACCESS MATRIX (Visualized)
===============================================================================

| Modifier    | Class | Package | Subclass | World |
|-------------|-------|---------|----------|-------|
| public      |  Yes  |   Yes   |   Yes    |  Yes  |
| protected   |  Yes  |   Yes   |   Yes    |  No   |
| default     |  Yes  |   Yes   |   No     |  No   |
| private     |  Yes  |   No    |   No     |  No   |



===============================================================================
SENIOR ENGINEER NOTES: CORNER CASES
===============================================================================

1) THE DEFAULT CONFUSION: 
   Do not confuse 'default' access with the 'default' keyword used in Switch 
   statements or Java 8 Interfaces. For access modifiers, "default" means 
   "writing nothing."

2) CLASS-LEVEL RESTRICTION:
   If you try: private class MyClass { } -> COMPILE ERROR.
   Why? Because if a class is private, no one can ever instantiate it, 
   making it useless code.

3) SECURITY & ENCAPSULATION:
   Always use the most restrictive modifier possible. 
   "Need to know basis." This prevents "Tight Coupling" (where classes 
   depend too much on each other's internals).

===============================================================================
CODING DRILLS
===============================================================================

DRILL 1:
Create a class 'Heart' in package 'human'.
- Make 'bpm' private.
- Create a public method 'checkPulse()' that prints 'bpm'.
- Try accessing 'bpm' directly from a 'Hospital' class in the same package.

DRILL 2:
- Create two packages: 'company.hr' and 'company.tech'.
- In 'hr', create class 'Salary' with a default (package-private) variable 'amount'.
- Try to access 'amount' from a class in 'company.tech'. 
- Observe the "is not visible" Compile-time error.

DRILL 3:
- Declare a public class but give its constructor a 'private' modifier.
- Try to create an object of that class from another class.
- (Hint: This is how the "Singleton Pattern" begins!)

===============================================================================
END
===============================================================================
*/