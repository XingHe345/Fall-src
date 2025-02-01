package dev.Fall.utils.client;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
public class logger {
    public static void info(String message) {
        System.out.println("[*] " + message);
    }

    public static void warn(String message) {
        System.out.println("[~] " + message);
    }

    public static void success(String message) {
        System.out.println("[+] " + message);
    }

    public static void error(String message) {
        System.out.println("[!] " + message);
    }
}
