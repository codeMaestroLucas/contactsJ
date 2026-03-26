package org.example.src.utils;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;

import java.io.IOException;

public class NoSleep {

    private static Process caffeinateProcess;

    /**
     * Prevent system sleep (cross-platform).
     */
    public static void preventSleep() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            try {
                // Start caffeinate process (keeps Mac awake)
                caffeinateProcess = new ProcessBuilder("caffeinate", "-dimsu").start();
                System.out.println("macOS: caffeinate started to prevent sleep.");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (os.contains("win")) {
            try {
                // Call Windows API via JNA
                Kernel32.INSTANCE.SetThreadExecutionState(
                        WinBase.ES_CONTINUOUS |
                                WinBase.ES_SYSTEM_REQUIRED |
                                WinBase.ES_AWAYMODE_REQUIRED
                );
                System.out.println("Windows: Sleep prevention enabled.");
            } catch (UnsatisfiedLinkError e) {
                System.err.println("JNA not available. Add JNA dependency for Windows support.");
            }
        } else {
            System.out.println("Unsupported OS for sleep prevention.");
        }
    }

    /**
     * Allow system to sleep again.
     */
    public static void allowSleep() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            if (caffeinateProcess != null) {
                caffeinateProcess.destroy();
                System.out.println("macOS: caffeinate stopped. Sleep allowed.");
            }
        } else if (os.contains("win")) {
            try {
                Kernel32.INSTANCE.SetThreadExecutionState(
                        WinBase.ES_CONTINUOUS
                );
                System.out.println("Windows: Sleep allowed again.");
            } catch (UnsatisfiedLinkError e) {
                System.err.println("JNA not available. Add JNA dependency for Windows support.");
            }
        }
    }
}
