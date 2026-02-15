//package basics;

/*
===============================================================================
THREAD MASTER FILE - PART 2: SYNCHRONIZATION & CONCURRENCY
(Memory Visibility, Locking, Inter-Thread Communication & Safety)
===============================================================================

1. THREAD NAME & PRIORITY 
    - Naming: Custom names help debugging. Default: Thread-0, Thread-1.
    - Priority: 1 (Min) to 10 (Max). Default: 5. Hints to OS scheduler.
    - Inheritance: Child threads inherit priority from parent.

2. THREAD GROUPS 
    - Tree structure to manage batches of threads.
    - Allows bulk operations (e.g., interrupt all).

3. THE THREE DIMENSIONS OF THREAD SAFETY
    A) Atomicity: Operation completes fully or not at all (e.g., synchronized).
    B) Visibility: Changes made by one thread are visible to others (e.g., volatile).
    C) Ordering: Execution order consistency (prevents JVM instruction reordering).

4. VOLATILE KEYWORD
    - Ensures visibility of variable across threads.
    - Prevents thread-local caching (CPU cache) of the variable.
    - Does NOT guarantee atomicity (e.g., count++ is still unsafe with volatile).
===============================================================================
*/

public class ThreadsPart2 {

    public static void main(String[] args) throws InterruptedException {

        // =====================================================================
        // 1) THREAD NAMES & PRIORITY DEMO
        // =====================================================================
        Thread t1 = new Thread(() -> {
            System.out.println("Running: " + Thread.currentThread().getName());
        }, "Worker-1"); 

        t1.setPriority(Thread.MAX_PRIORITY); // Priority 10
        t1.start();

        // =====================================================================
        // 2) THREAD GROUPS DEMO
        // =====================================================================
        ThreadGroup group = new ThreadGroup("Database-Threads"); 
        Thread t2 = new Thread(group, "DB-Connection-1"); 
        t2.start();
        System.out.println("Active Threads in Group: " + group.activeCount()); 

        // =====================================================================
        // 3) SYNCHRONIZATION & REENTRANCY DEMO
        // =====================================================================
        BankAccount account = new BankAccount();
        
        Thread user1 = new Thread(() -> account.deposit(100), "User-1");
        Thread user2 = new Thread(() -> account.withdraw(50), "User-2");
        
        user1.start();
        user2.start();
        
        user1.join();
        user2.join();
        System.out.println("Final Balance: " + account.getBalance());

        // =====================================================================
        // 4) STATIC vs INSTANCE LOCKING (They don't block each other)
        // =====================================================================
        new Thread(() -> account.instanceMethod()).start();
        new Thread(() -> BankAccount.staticMethod()).start(); 
        // These run simultaneously because locks are on different objects.

        // =====================================================================
        // 5) INTERRUPTION MECHANISM
        // =====================================================================
        Thread sleeper = new Thread(() -> {
            try {
                System.out.println("Sleeper going to sleep...");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted! Stopping gracefully.");
                // Best Practice: Re-interrupt if you catch it but don't stop
                // Thread.currentThread().interrupt(); 
            }
        });
        sleeper.start();
        Thread.sleep(1000);
        sleeper.interrupt(); // Signals the thread to wake up/stop

        // =====================================================================
        // 6) INTER-THREAD COMMUNICATION (wait/notify)
        // =====================================================================
        Chat chat = new Chat();
        new Thread(() -> chat.Answer("Hi"), "Person-2").start();
        new Thread(() -> chat.Question("Hello"), "Person-1").start();
    }
}

// =========================================================================
// SYNCHRONIZATION ARCHITECTURE [cite: 183-193]
// =========================================================================

/* * CONCEPT: THE MONITOR (LOCK)
 * - Every Object has a lock. 'synchronized' acquires it.
 * - REENTRANT NATURE: If a thread holds a lock, it can enter other 
 * synchronized methods of the SAME object without blocking.
 */

class BankAccount {
    private int balance = 100;

// A. METHOD LEVEL SYNCHRONIZATION [cite: 194-206]
    /* Lock is on 'this' (Current Object). */
    public synchronized void deposit(int amount) {
        System.out.println("Depositing...");
        balance += amount;
        // REENTRANCY PROOF: Calling another synchronized method
        checkBalanceInternal(); 
    }

    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
        }
    }

    // Reentrant method (Called from inside deposit)
    private synchronized void checkBalanceInternal() {
        System.out.println("Internal Check: " + balance);
    }

    public int getBalance() { return balance; }

    // B. STATIC vs INSTANCE LOCKING NUANCE
    /* * Instance synchronized: Lock is on 'this'.
     * Static synchronized: Lock is on 'BankAccount.class'.
     * IMPORTANT: They are DIFFERENT locks. A thread holding the instance lock
     * does NOT block a thread needing the static lock.
     */
    public synchronized void instanceMethod() {
        System.out.println("Instance Lock Held. Running...");
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

  public static synchronized void staticMethod() { 
        System.out.println("Static Lock Held. Running...");
        try { Thread.sleep(2000); } catch (Exception e) {}
    }
}

// =========================================================================
// INTER-THREAD COMMUNICATION (wait/notify) 
// =========================================================================

/* * LOST NOTIFICATION PROBLEM:
 * If notify() is called BEFORE wait(), the signal is lost and the thread waits forever.
 * SOLUTION: Always check condition in a loop (while loop), not if statement.
 */

class Chat {
    boolean flag = false;

    public synchronized void Question(String msg) {
        // WHILE LOOP prevents Spurious Wakeups and Lost Notifications
        while (flag) { 
            try { wait(); } catch (InterruptedException e) {} 
        }
        System.out.println("Question: " + msg);
        flag = true;
        notify(); 
    }

    public synchronized void Answer(String msg) {
        while (!flag) {
            try { wait(); } catch (InterruptedException e) {}
        }
        System.out.println("Answer: " + msg);
        flag = false;
        notify();
    }
}

/*
===============================================================================
CORE CONCEPTS & DEEP DIVE
===============================================================================

1. INTERRUPT MECHANISM (The Polite Stop)
   - interrupt(): Sets a flag. Does NOT kill the thread.
   - If thread is Sleeping/Waiting: Throws InterruptedException, clears flag.
   - If thread is Running: Sets boolean flag. User must check isInterrupted().
   - Why? Safe termination. Stopping a thread forcefully (stop()) is deprecated because it leaves objects in inconsistent states. 

2. DEADLOCK CONDITIONS (The Coffman Conditions)
   Deadlock only happens if ALL 4 are true:
   1. Mutual Exclusion: Resources cannot be shared.
   2. Hold and Wait: Thread holds resource, waits for another.
   3. No Preemption: Resources cannot be forcibly taken.
   4. Circular Wait: T1 waits for T2, T2 waits for T1.
   * Solution: Break ANY one condition (e.g., impose lock ordering).

3. LIVELOCK vs DEADLOCK
   - Deadlock: Threads are BLOCKED forever (Waiting).
   - Livelock: Threads are RUNNING but making no progress (e.g., two people 
     moving left-right in a hallway trying to pass each other).

4. VOLATILE KEYWORD (Visibility Guarantee)
   - Problem: Threads copy variables from Main Memory (RAM) to CPU Cache 
     for speed. If T1 changes value in Cache, T2 might not see it in RAM.
   - Solution: 'volatile' forces variable reads/writes directly to Main Memory.
   - Usage: Flags (boolean running = true).

5. [cite_start]THREAD vs STACK MEMORY [cite: 282-286]
   - Heap: Shared. Objects live here. Race conditions happen here.
   - Stack: Private. Local variables live here. Thread-safe by default.

6. SLEEP() vs WAIT() (Elite Comparison)
   | Feature      | sleep()                       | wait()                        |
   |--------------|-------------------------------|-------------------------------|
   | Origin       | Thread class                  | Object class                  |
   | Lock Status  | KEEPS the lock                | RELEASES the lock |
   | Wake up      | Automatic (Timer)             | Needs notify()                |
   | Usage        | Time delay                    | Synchronization               |

7. SPURIOUS WAKEUPS
   - Threads can wake from wait() without notify().
   - This is why we ALWAYS use 'while(condition)' instead of 'if(condition)'.
*/