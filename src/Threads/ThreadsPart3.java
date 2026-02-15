//package basics;

/*
===============================================================================
THREAD MASTER FILE - PART 3: ADVANCED LIFECYCLE & ARCHITECTURE
(Deadlocks, Memory Model, Concurrency Theory, and Safety Mechanisms)
===============================================================================

1. THREAD JOINING (The "Wait for me" Protocol)
    - Purpose: Pauses the CURRENT thread until the TARGET thread dies.
    - Application: Main thread waiting for workers to finish before aggregating results.
    - Overloads:
      A) join(): Waits forever.
      B) join(long millis): Waits for X milliseconds, then proceeds.

2. DAEMON THREADS (The Invisible Helpers)
    - User Thread: Foreground. JVM keeps running as long as one User Thread is alive.
    - Daemon Thread: Background. JVM kills all Daemons instantly when the last User Thread dies.
    - Rule: setDaemon(true) MUST be called BEFORE start().

3. CONCURRENCY ARCHITECTURE & SCHEDULING
    - Java Memory Model (JMM): Defines how threads interact through memory.
    - Happens-Before: The guarantee that a write in Thread A is visible to Thread B.
    - Livelock: Threads aren't blocked, but are too busy responding to each other to work.
    
    [ADDED] CONCURRENCY vs PARALLELISM:
    - Concurrency: Multiple tasks making progress in overlapping time periods. 
      (One CPU handling multiple threads via context switching).
    - Parallelism: Multiple tasks executing literally at the same instant. 
      (Requires Multi-Core CPU).
    
    [ADDED] SCHEDULING MODELS:
    - Preemptive: Higher priority thread interrupts (preempts) a running lower priority thread.
    - Time Slicing: Each thread gets a fixed time slice (quantum) in a round-robin manner.
    - Note: Java Thread Scheduler relies on the underlying OS implementation.

4. DEADLOCK (The "Deadly Embrace")
    - Occurs when two threads hold locks the other needs.
===============================================================================
*/

public class ThreadsPart3 {

    public static void main(String[] args) throws InterruptedException {

        // =====================================================================
        // 1) THREAD JOIN() & TIMEOUTS
        // =====================================================================
        System.out.println("--- Join Demo ---");
        
        Thread heavyWorker = new Thread(() -> {
            try {
                System.out.println("Worker: Processing (Takes 5 sec)...");
                Thread.sleep(5000); 
                System.out.println("Worker: Done.");
            } catch (InterruptedException e) {
                System.out.println("Worker interrupted.");
            }
        });

        heavyWorker.start();

        /* [ADDED] JOIN() INTERNAL BEHAVIOR (Deep Dive):
         * - join() is synchronized! It internally calls wait() on the thread object.
         * - The calling thread (Main) goes into WAITING state.
         * - When the target thread (heavyWorker) terminates, the JVM internally 
         * calls notifyAll() on the thread object to wake up waiting threads.
         */
        
        // heavyWorker.join(); // Standard Join (Main waits forever)

        // Join with Timeout (Main waits only 2 sec)
        heavyWorker.join(2000); 

        if (heavyWorker.isAlive()) {
            System.out.println("Main: Worker is too slow. I'm moving on.");
        } else {
            System.out.println("Main: Worker finished in time.");
        }

        // =====================================================================
        // 2) DAEMON THREAD BEHAVIOR (JVM Shutdown)
        // =====================================================================
        System.out.println("\n--- Daemon Demo ---");
        
        Thread autoSaver = new Thread(() -> {
            while (true) {
                System.out.println("[Daemon] Auto-saving...");
                try { Thread.sleep(500); } catch (Exception e) {}
            }
        });

        // EDGE CASE: Trying to setDaemon after start()
        // autoSaver.start();
        // autoSaver.setDaemon(true); // THROWS IllegalThreadStateException

        autoSaver.setDaemon(true); // Correct placement
        autoSaver.start();

        Thread.sleep(1500); // Let daemon run 3 times
        System.out.println("Main: Main thread ending. JVM will kill Daemon immediately.");
        // JVM exits here. "Auto-saving..." will stop instantly.

        // =====================================================================
        // 3) DEADLOCK SIMULATION (AND PREVENTION)
        // =====================================================================
        System.out.println("\n--- Deadlock Demo ---");
        
        DeadlockScenario deadlock = new DeadlockScenario();
        
        // Thread 1: Tries to lock String -> Integer
        Thread t1 = new Thread(deadlock::methodA, "Thread-A");
        
        // Thread 2: Tries to lock Integer -> String (Circular Dependency)
        Thread t2 = new Thread(deadlock::methodB, "Thread-B");

        // Uncomment to witness deadlock (Program hangs forever)
        // t1.start();
        // t2.start();
    }
}

// =========================================================================
// DEADLOCK ARCHITECTURE & RESOLUTION
// =========================================================================

class DeadlockScenario {
    // Resources to lock
    final Object resource1 = "StringLock";
    final Object resource2 = "IntegerLock";

    /* * DEADLOCK TRIGGER:
     * T1 holds resource1, waits for resource2.
     * T2 holds resource2, waits for resource1.
     * Result: Infinite Block.
     */
    void methodA() {
        synchronized (resource1) {
            System.out.println("T1: Locked Resource 1");
            try { Thread.sleep(100); } catch (Exception e) {} // Simulating work

            System.out.println("T1: Waiting for Resource 2...");
            synchronized (resource2) {
                System.out.println("T1: Locked Resource 2");
            }
        }
    }

    void methodB() {
        synchronized (resource2) {
            System.out.println("T2: Locked Resource 2");
            try { Thread.sleep(100); } catch (Exception e) {}

            System.out.println("T2: Waiting for Resource 1...");
            synchronized (resource1) {
                System.out.println("T2: Locked Resource 1");
            }
        }
    }
    
    /* * HOW TO FIX DEADLOCK?
     * 1. Lock Ordering: Both threads must acquire locks in the SAME order.
     * (e.g., Both must lock resource1 FIRST, then resource2).
     * 2. TryLock: Use ReentrantLock.tryLock() with a timeout. If lock isn't 
     * acquired, back off and try again later.
     */
}

// =========================================================================
// JAVA MEMORY MODEL (JMM) - VISIBILITY & VOLATILE
// =========================================================================

class VolatileDemo {
    /* * PROBLEM: Without 'volatile', Reader might cache 'flag' in CPU cache.
     * * SOLUTION: 'volatile' forces reads/writes directly to Main Memory.
     */
    private volatile boolean flag = false;

    public void writer() {
        flag = true; // "Happens-Before" any subsequent read of flag
        System.out.println("Flag updated to true");
    }

    public void reader() {
        while (!flag) { /* Spin wait */ }
        System.out.println("Reader detected flag change!");
    }
}

/*
===============================================================================
DEEP DIVE: INTERNAL CONCEPTS & DEFINITIONS
===============================================================================

1. HAPPENS-BEFORE RELATIONSHIP (Visibility Guarantee)
   
   The JVM guarantees that action A is visible to action B if:
   - A is in the same thread before B.
   - A is a monitor release (unlock) and B is a monitor acquire (lock) on same object.
   - A is writing to a 'volatile' variable and B is reading it.
   - A is Thread.start() and B is the first instruction in run().
   - A is the last instruction in run() and B is the return from Thread.join().

2. LIVELOCK vs DEADLOCK vs STARVATION
   - Deadlock: "I am blocked waiting for you." (State: BLOCKED)
   - Livelock: "I am stepping aside for you, oh you are stepping aside for me?" 
     (State: RUNNABLE, but doing nothing useful. Infinite loop of politeness).
   - Starvation: "I am ready to work, but the Scheduler never picks me because 
     Priority 10 threads are hogging the CPU."

3. CONTEXT SWITCHING (The Hidden Cost)
   - Saving the state (registers, stack pointer) of the current thread.
   - Loading the state of the next thread.
   - Cost: CPU cycles are wasted on administration instead of execution.

4. [ADDED] THREAD SAFETY vs SYNCHRONIZATION
   - Synchronization: A TOOL (Mechanism). It is the act of controlling access 
     to shared resources (e.g., using 'synchronized' keyword, Locks).
   - Thread Safety: A PROPERTY (Result). Code is "thread-safe" if it functions 
     correctly during simultaneous execution by multiple threads.
   * You use Synchronization to achieve Thread Safety.

5. [ADDED] WHY RUNNABLE IS PREFERRED OVER EXTENDING THREAD
   A. Single Inheritance: Java allows extending only one class. If you extend 
      Thread, you cannot extend anything else. Runnable saves that slot.
   B. Separation of Concerns: Runnable represents the "Task" (logic), while 
      Thread represents the "Runner" (execution). Decoupling is good design.
   C. Thread Pools: Executors (ThreadPools) accept Runnables, not Thread objects.
      This allows recycling threads for different tasks.

6. [ADDED] NOTIFY() vs NOTIFYALL() REAL RISK
   - notify(): Wakes ONE arbitrary thread. 
   - Risk: If multiple threads are waiting for *different* conditions on the 
     same lock, notify() might wake the wrong thread. That thread checks its 
     condition, finds it false, and goes back to sleep. The correct thread 
     remains sleeping. Signal is lost -> Deadlock.
   - notifyAll(): Wakes EVERYONE. The correct thread proceeds, others sleep. 
     Safer but slightly more overhead.

===============================================================================
EDGE CASES & INTERVIEW TRAPS
===============================================================================

1. Can you start a thread twice?
   - No. It throws 'IllegalThreadStateException'. Once a thread is TERMINATED, 
     it cannot be restarted. You must create a new Thread object.

2. Does sleep() release the lock?
   - NO. sleep() holds the lock while pausing.
   - wait() releases the lock while pausing.

3. Why is Thread.stop() deprecated?
   - It kills the thread instantly, releasing all locks. If the thread was 
     modifying a critical object (e.g., transferring money), the object is left 
     in a corrupt, half-written state.

4. Orphaned Threads?
   - If Main finishes, do child threads die?
   - User Threads: NO. They keep running. The JVM stays alive.
   - Daemon Threads: YES. They die immediately.

===============================================================================
END OF THREAD MASTER FILE
===============================================================================
*/