package ui;

import model.AuthData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PreLoginUI {
    private final ServerFacade facade;
    private final BufferedReader reader;

    public PreLoginUI(ServerFacade facade) {
        this.facade = facade;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public AuthData run() throws IOException {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        while (true) {
            String input = reader.readLine().trim();
            if ("help".equalsIgnoreCase(input)) {
                displayHelp();
            } else if (input.startsWith("register ")) {
                AuthData authData = register(input);
                if (authData != null) {
                    System.out.println("Registration successful.");
                    return authData; // Return AuthData upon successful registration
                }
            } else if (input.startsWith("login ")) {
                AuthData authData = login(input);
                if (authData != null) {
                    System.out.println("Login successful.");
                    return authData; // Return AuthData upon successful login
                }
            } else if ("quit".equalsIgnoreCase(input)) {
                quit();
                break; // Exit the loop to terminate the program
            } else {
                System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
        return null; // Return null if the loop exits without logging in or registering
    }

    private void displayHelp() {
        // ANSI escape code for blue text
        final String ANSI_BLUE = "\033[34m";
        // ANSI escape code for white text. Note: The default text color varies by terminal, so "\033[0m" resets to default rather than explicitly setting white.
        final String ANSI_WHITE = "\033[37m";
        // ANSI reset code to reset text color back to the terminal's default
        final String ANSI_RESET = "\033[0m";

        System.out.println(ANSI_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" + ANSI_WHITE + " - to create an account" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "login <USERNAME> <PASSWORD>" + ANSI_WHITE + " - to play chess" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "quit" + ANSI_WHITE + " - stop playing chess" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "help" + ANSI_WHITE + " - with possible commands" + ANSI_RESET);
    }

    private AuthData login(String input) throws IOException {
        String[] parts = input.split(" ");
        if (parts.length != 3) {
            System.out.println("Invalid login command. Correct format: login <USERNAME> <PASSWORD>");
            return null;
        }
        String username = parts[1];
        String password = parts[2];

        try {
            AuthData authData = facade.login(username, password);
            System.out.println("Login successful.");
            return authData;
        } catch (IOException | InterruptedException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    private AuthData register(String input) throws IOException {
        String[] parts = input.split(" ");
        if (parts.length != 4) {
            System.out.println("Invalid register command. Correct format: register <USERNAME> <PASSWORD> <EMAIL>");
            return null;
        }
        String username = parts[1];
        String password = parts[2];
        String email = parts[3];

        try {
            AuthData authData = facade.register(username, password, email);
            System.out.println("Registration successful.");
            return authData;
        } catch (IOException | InterruptedException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
        return null;
    }

    private void quit() {
        System.out.println("Quitting application...");
    }
}