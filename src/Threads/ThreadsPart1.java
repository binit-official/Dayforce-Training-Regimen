package basics;

/*
===============================================================================
THREAD MASTER FILE - PART 1: FUNDAMENTALS & ARCHITECTURE
(Multitasking, JVM Internals, Lifecycle, and Creation Strategies)
===============================================================================

1. [cite_start]WHAT IS MULTITASKING? [cite: 1-2]
    - Multitasking is the process of executing multiple tasks concurrently.
    - It allows efficient CPU utilization.
    - Achieved via:
      A) Multiprocessing
      B) Multithreading

2. [cite_start]MULTIPROCESSING vs MULTITHREADING [cite: 6-8, 15-18, 28]

   A) Multiprocessing (System Level)
      - Executing multiple processes simultaneously.
      - Each process has its own memory address space.
      - Heavyweight context switching.
      - Example: Typing in Word while listening to Spotify.

   B) Multithreading (Application Level)
      - Executing multiple threads simultaneously within a single process.
      - Threads share the same memory area.
      - Lightweight context switching.
      - Example: Inside Word -> Typing (Thread 1), Spell Check (Thread 2), Auto Save (Thread 3).

===============================================================================
3. [cite_start]PROCESS vs THREAD (The Core Difference) [cite: 28]
===============================================================================
   
   PROCESS:
   - Independent program in execution.
   - Has its own separate memory space.
   - Heavyweight (Expensive creation/destruction).
   - Inter-process communication is costly (needs sockets/pipes).
   - Context switching is slow.

   THREAD:
   - A sub-unit of a process (Lightweight subprocess).
   - Shares memory (Heap) with other threads in the same process.
   - Lightweight (Cheap creation).
   - Inter-thread communication is fast (shared memory).
   - Context switching is fast.

===============================================================================
*/

public class ThreadsPart1 {

    public static void main(String[] args) {
        
        // =====================================================================
// 4) JVM INTERNAL ARCHITECTURE (Startup Flow) [cite: 57-84]
        // =====================================================================
        /*
         * 1. Initialization: JVM requests memory from OS. (If fails -> Error).
         * 2. Thread Setup:
         * - Creates 'system' ThreadGroup -> adds 'Finalizer' thread.
         * - Creates 'main' ThreadGroup -> adds 'main' thread.
         * 3. Execution:
         * - Main thread verifies Bytecode format (Bytecode Verifier).
         * - Checks for 'public static void main(String[] args)'.
         * - Invokes main().
         * 4. Termination: Main thread is destroyed when main() finishes.
         */

        System.out.println("Current Thread: " + Thread.currentThread().getName()); 

        // =====================================================================
        // 5) THREAD SCHEDULER (The Manager)
        // =====================================================================
        /*
         * - Part of the JVM.
         * - Decides which 'RUNNABLE' thread gets the CPU.
         * - Uses Thread Priority (1-10) as a "hint" (not a guarantee).
         * - Behavior is OS-dependent (Preemptive vs Time Slicing).
         */

        // =====================================================================
        // 6) CREATING THREADS & THE "start() vs run()" TRAP
        // =====================================================================
        
        MyThread t1 = new MyThread();
        
        // WRONG WAY: Calling run() directly
        t1.run(); 
        /* Result: Executes in the 'main' thread stack. No new thread is created.
         * It acts like a normal method call.
         */

        // CORRECT WAY: Calling start()
        t1.start();
        /* Result:
         * 1. JVM creates a new call stack.
         * 2. Registers thread with Scheduler.
         * 3. Calls run() internally.
         * 4. State moves from NEW -> RUNNABLE.
         */

        // EDGE CASE: RESTARTING A THREAD
        // t1.start(); // EXCEPTION: java.lang.IllegalThreadStateException
        /* A thread can only be started ONCE. It creates a lifecycle that ends 
         * when run() finishes. You cannot restart a dead thread. */

        // =====================================================================
  // 7) THREAD CONSTANTS & PRIORITY [cite: 49-50, 116-119]
        // =====================================================================
        // Range: 1 (Min) to 10 (Max). Default is 5 (Norm).
        System.out.println("Min: " + Thread.MIN_PRIORITY);
        System.out.println("Norm: " + Thread.NORM_PRIORITY);
        System.out.println("Max: " + Thread.MAX_PRIORITY);
        
        t1.setPriority(8); 

        // =====================================================================
    // 8) DAEMON THREADS (Service Providers) 
        // =====================================================================
        /*
         * User Thread: High priority. JVM waits for them to finish (e.g., main).
         * Daemon Thread: Low priority background service (e.g., Garbage Collector).
         * * RULE: JVM does NOT wait for Daemon threads. If all User threads finish,
         * the JVM terminates, killing all Daemon threads instantly.
         */
        Thread daemon = new Thread(new DaemonWorker());
        daemon.setDaemon(true); // Must call BEFORE start() 
        daemon.start();
        System.out.println("Is Daemon? " + daemon.isDaemon()); 
    }
}

// =========================================================================
// THREAD CREATION STRATEGIES (Comparison) [cite: 85-93]
// =========================================================================

/* STRATEGY 1: EXTENDING THREAD CLASS
 * - Limitation: Cannot extend any other class (Java is single inheritance).
 * - Coupling: Tightly coupled (Task and Runner are same).
 */
class MyThread extends Thread { // [cite: 91]
    @Override
    public void run() { // [cite: 93]
        System.out.println("Thread running: " + getName());
    }
}

/* STRATEGY 2: IMPLEMENTING RUNNABLE INTERFACE (Preferred)
 * - Benefit: Can extend another class.
 * - Design: Separates 'Task' (Runnable) from 'Runner' (Thread object).
 * - Flexibility: Suitable for Thread Pools.
 */
class DaemonWorker implements Runnable { // [cite: 92]
    @Override
    public void run() {
        System.out.println("Daemon service running...");
    }
}

/*
===============================================================================
[cite_start]THREAD LIFECYCLE & STATES (The State Machine) [cite: 55-56, 144-169]
===============================================================================



1. NEW:
   - Thread object created (new Thread()). [cite_start]Not yet started. [cite: 145]

2. RUNNABLE (Ready-to-Run):
   - After start() is called.
   - Thread is registered with Scheduler.
   - [cite_start]It might be running OR waiting for CPU time. [cite: 146]

3. BLOCKED:
   - [cite_start]Waiting for a monitor lock (synchronized block) to be released by another thread. [cite: 161]

4. WAITING:
   - Waiting indefinitely for another thread's signal.
   - [cite_start]Triggers: object.wait(), thread.join(). [cite: 156]
   - Exit: notify(), notifyAll().

5. TIMED_WAITING:
   - Waiting for a fixed time.
   - [cite_start]Triggers: Thread.sleep(ms), object.wait(ms), thread.join(ms). [cite: 155]
   - Exit: Time expires.

6. TERMINATED (Dead):
   - run() method completes execution.
   - Or unhandled exception kills the thread.
   - [cite_start]Once dead, cannot be restarted. [cite: 168-169]

===============================================================================
[cite_start]THREAD API REFERENCE (Key Methods) [cite: 48, 54]
===============================================================================

- currentThread(): Reference to running thread.
- getId(): Unique ID.
- getName() / setName(): Identity management.
- isAlive(): True if started and not dead.
- join(): Current thread pauses until joined thread dies.
- yield(): Hint to scheduler to pause current thread and allow others.
- sleep(long ms): Pauses execution (does not release locks).
*/