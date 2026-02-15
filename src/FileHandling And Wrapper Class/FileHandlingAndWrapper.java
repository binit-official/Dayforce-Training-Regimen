package basics;

import java.io.*;
import java.nio.file.*; // Modern NIO
import java.util.*;
import java.util.stream.Stream;

/*
===============================================================================
JAVA MASTER FILE: ADVANCED FILE HANDLING (I/O) & TYPE ARCHITECTURE
(Streams, Serialization, NIO, and Wrapper Internals)
===============================================================================

1. THE I/O STREAM HIERARCHY
    - Byte Streams (InputStream/OutputStream): For binary data (Images, Audio, Objects).
      -> FileInputStream, FileOutputStream, BufferedInputStream, ObjectInputStream.
    - Character Streams (Reader/Writer): For text data (Unicode support).
      -> FileReader, FileWriter, BufferedReader, PrintWriter.
    
    

2. CHECKED EXCEPTIONS IN I/O
    - IOException: The parent Checked Exception for all I/O errors.
    - FileNotFoundException: Thrown if file path is invalid (Subclass of IOException).
    - EOFException: Thrown specifically by DataInputStream when end of file is reached unexpectedly.

3. WRAPPER CLASS INTERNALS
    - Caching mechanism (Integer Cache -128 to 127).
    - Immutability guarantees.
    - Boxing/Unboxing & Deprecated Constructors.
===============================================================================
*/

// =========================================================================
// SERIALIZATION DEMO CLASS (Must implement Serializable)
// =========================================================================
class User implements Serializable {
    private static final long serialVersionUID = 1L; // Version Control
    
    String name;
    int id;
    
    // 'transient': This field will NOT be saved to the file.
    transient String password; 

    User(String name, int id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', id=" + id + ", password='" + password + "'}";
    }
}

public class FileHandlingAndWrapper {

    public static void main(String[] args) {

        // =====================================================================
        // 1) THE FILE CLASS: METADATA, PATHS & DIRECTORIES
        // =====================================================================
        System.out.println("--- File Metadata & Paths ---");

        // A. CROSS-PLATFORM PATH SEPARATOR
        /* Windows uses '\', Linux/Mac uses '/'. Hardcoding breaks portability.
         * File.separator ensures the code runs on ANY OS. */
        String path = "data" + File.separator + "master_data.txt";
        File file = new File(path);
        
        // B. ABSOLUTE vs RELATIVE PATH
        /* Relative: "data/file.txt" (Depends on where you run the project from).
         * Absolute: "C:/Users/Binit/Project/data/file.txt" (Full system path). */
        System.out.println("Relative: " + file.getPath());
        System.out.println("Absolute: " + file.getAbsolutePath());

        // C. DIRECTORY LISTING
        File currentDir = new File("."); // "." means current directory
        if (currentDir.isDirectory()) {
            System.out.println("Listing files in current dir:");
            File[] files = currentDir.listFiles(); // Returns File objects
            if (files != null) {
                for (File f : files) {
                    System.out.print(f.getName() + " | ");
                }
                System.out.println();
            }
        }
        
        try {
            // Create directories if they don't exist
            file.getParentFile().mkdirs(); 
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            }
            
            // FILE PERMISSIONS
            file.setReadable(true);
            file.setWritable(true);
            file.setExecutable(false); // Security practice
            
        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }

        // =====================================================================
        // 2) BYTE STREAMS & BUFFERING (Binary Data)
        // =====================================================================
        System.out.println("\n--- Byte Streams (Buffered) ---");
        
        /* ARCHITECTURE: 
         * FileOutputStream (Connector) -> BufferedOutputStream (Performance) -> Disk
         * Buffered streams reduce physical disk I/O by writing chunks (8KB default).
         */
        
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            String content = "Java Buffered Byte Stream Logic";
            bos.write(content.getBytes());
            bos.flush(); // Force write from buffer to disk
            
        } catch (IOException e) { e.printStackTrace(); }

        // READING (Buffered Input)
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            
            int data;
            // Reading byte-by-byte from RAM Buffer (Fast), not Disk (Slow)
            while ((data = bis.read()) != -1) {
                System.out.print((char) data);
            }
            System.out.println();
            
        } catch (IOException e) { e.printStackTrace(); }

        // =====================================================================
        // 3) CHARACTER STREAMS (FileReader / BufferedReader)
        // =====================================================================
        System.out.println("\n--- Character Streams (Text) ---");
        
        /* USAGE: Best for Text Files (Handles Unicode/Internationalization). */
        
        // Writing with PrintWriter (Convenience wrapper around Writer)
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Line 1: Character Stream");
            pw.println("Line 2: BufferedReader Demo");
        } catch (IOException e) { e.printStackTrace(); }

        // Reading with BufferedReader
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            
            String line;
            // readLine() returns null at EOF (End of File)
            while ((line = br.readLine()) != null) {
                System.out.println("Read Line: " + line);
            }
            
        } catch (IOException e) { e.printStackTrace(); }

        // =====================================================================
        // 4) DATA STREAMS (Primitives & EOFException)
        // =====================================================================
        System.out.println("\n--- Data Streams (Primitives) ---");
        
        File dataFile = new File("primitives.dat");
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(dataFile));
             DataInputStream dis = new DataInputStream(new FileInputStream(dataFile))) {
            
            dos.writeInt(100);
            dos.writeDouble(99.99);
            
            System.out.println("Int: " + dis.readInt());
            System.out.println("Double: " + dis.readDouble());
            
            // EOFException DEMO
            // If we try to read past the end of file, DataInputStream throws EOFException
            // dis.readBoolean(); // Uncomment to trigger EOFException
            
        } catch (EOFException e) {
            System.out.println("End of File Reached Unexpectedly.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // =====================================================================
        // 5) SERIALIZATION (Object Streams)
        // =====================================================================
        System.out.println("\n--- Serialization (Object Persistence) ---");
        
        File objectFile = new File("user.ser");
        User myUser = new User("Binit", 101, "SecretPass123");
        
        // A. SERIALIZATION (Writing Object)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(objectFile))) {
            oos.writeObject(myUser);
            System.out.println("Object Saved: " + myUser);
        } catch (IOException e) { e.printStackTrace(); }

        // B. DESERIALIZATION (Reading Object)
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objectFile))) {
            User loadedUser = (User) ois.readObject();
            // Note: password will be 'null' because it was transient
            System.out.println("Object Loaded: " + loadedUser);
        } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }

        // =====================================================================
        // 6) MODERN NIO (Files.lines for Large Files)
        // =====================================================================
        System.out.println("\n--- NIO (Lazy Streaming) ---");
        
        Path nioPath = Paths.get(file.toURI());
        
        /* Files.lines() returns a Stream<String>. 
         * It reads the file lazily (line by line) instead of loading 
         * the whole file into RAM (like readAllLines does).
         * Essential for processing massive logs/datasets.
         */
        try (Stream<String> stream = Files.lines(nioPath)) {
            stream.filter(s -> s.contains("Line")) // Filter logic
                  .forEach(System.out::println);   // Print logic
        } catch (IOException e) { e.printStackTrace(); }

        // =====================================================================
        // 7) WRAPPER CLASS DEEP DIVE (Internals & Deprecation)
        // =====================================================================
        System.out.println("\n--- Wrapper Class Deep Dive ---");

        // A. EQUALS vs == (The Cache Trap)
        /* Integer Cache: Java caches integers from -128 to 127. */
        Integer a = 100; 
        Integer b = 100;
        System.out.println("100 == 100? " + (a == b)); // true (From Cache)

        Integer x = 200; 
        Integer y = 200;
        System.out.println("200 == 200? " + (x == y)); // false (New Objects)
        System.out.println("200 Equals 200? " + x.equals(y)); // true (Value check)

        // B. DEPRECATED CONSTRUCTORS vs FACTORY METHOD
        /* 'new Integer(100)' is Deprecated since Java 9.
         * It forces creation of a new object (inefficient).
         * 'Integer.valueOf(100)' uses the Cache (efficient). */
        
        @SuppressWarnings("deprecation")
        Integer newObj = new Integer(100); // Bad Practice (Always new heap object)
        Integer valObj = Integer.valueOf(100); // Good Practice (Uses Cache)
        
        System.out.println("new(100) == valueOf(100)? " + (newObj == valObj)); // false

        // C. PARSEINT vs VALUEOF
        int p = Integer.parseInt("123");      // Returns primitive int
        Integer w = Integer.valueOf("123");   // Returns Integer object
        
        // D. COMPARE TO
        /* Returns 0 if equal, <0 if a<b, >0 if a>b */
        System.out.println("Compare 10 vs 20: " + Integer.compare(10, 20)); // -1
    }
}

/*
===============================================================================
ELITE CONCEPTUAL BREAKDOWN
===============================================================================

1. TRY-WITH-RESOURCES RULE
   - Only classes implementing 'java.lang.AutoCloseable' can be used in try(...).
   - Java automatically calls .close() (which calls .flush()) when the block exits.
   - Even if an exception occurs, the resource is closed safely (preventing memory leaks).

2. BUFFERED STREAMS (The Decorator Pattern)
   - FileInputStream: Knows how to read bytes from disk.
   - BufferedInputStream: Knows how to store bytes in RAM.
   - We "decorate" the File stream with the Buffered stream to add performance.
   - [Program] <-> [Buffer (RAM)] <-> [File Stream] <-> [Disk]

3. SERIALIZATION & TRANSIENT
   - Serialization is the "Save Game" feature of Java.
   - 'transient' keyword excludes fields (like passwords) from being saved.
   - 'serialVersionUID': Acts as a version checksum to prevent loading incompatible objects.



4. NIO vs IO (Lazy Loading)
   - Standard IO (BufferedReader) is blocking.
   - NIO (Files.lines) creates a Stream. It consumes memory line-by-line. 
   - If you have a 10GB file, 'Files.readAllLines()' crashes (OutOfMemory). 
     'Files.lines()' works perfectly.

5. WRAPPER ARCHITECTURE
   - Use 'valueOf()' instead of constructors to leverage the Cache.
   - Use 'equals()' for comparison, never '=='.
   - 'parseInt()' is for math (primitives), 'valueOf()' is for Collections (objects).

===============================================================================
EDGE CASES & CORNER CASES
===============================================================================

1. EOFException
   - Specifically for DataInputStream. Unlike readLine() which returns null, 
     readInt() cannot return null. So it throws EOFException when data runs out.

2. File Separators
   - Hardcoding "C:\\" fails on Linux. Always use File.separator.
   - Relative paths depend on where the JVM was launched (usually project root).

3. Directory Listing
   - listFiles() returns null if the path is not a directory or an IO error occurs.
   - Always check for null before looping to avoid NullPointerException.
*/