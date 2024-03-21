package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import model.AuthData;

public class PreLoginUI {
    private final ServerFacade facade;
    private final BufferedReader reader;

    public PreLoginUI(ServerFacade facade) {
        this.facade = facade;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() throws IOException {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        while (true) {
            String input = reader.readLine().trim();
            if ("help".equalsIgnoreCase(input)) {
                displayHelp();
            } else if (input.startsWith("register ")) {
                register(input);
            } else if (input.startsWith("login ")) {
                login(input);
            } else if ("quit".equalsIgnoreCase(input)) {
                quit();
                break; // Exit the loop to terminate the program
            } else {
                System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
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

    private void login(String input) throws IOException {
        String[] parts = input.split(" ");
        if (parts.length != 3) {
            System.out.println("Invalid login command. Correct format: login <USERNAME> <PASSWORD>");
            return;
        }
        String username = parts[1];
        String password = parts[2];

        try {
            AuthData authData = facade.login(username, password);
            System.out.println("Login successful.");
            // Transition to PostLogin UI here
        } catch (IOException | InterruptedException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void register(String input) throws IOException {
        String[] parts = input.split(" ");
        if (parts.length != 4) {
            System.out.println("Invalid register command. Correct format: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }
        String username = parts[1];
        String password = parts[2];
        String email = parts[3];

        try {
            AuthData authData = facade.register(username, password, email);
            System.out.println("Registration successful.");
            // Transition to PostLogin UI here
        } catch (IOException | InterruptedException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void quit() {
        System.out.println("Quitting application...");
    }
}