package g63888.ascii.controller;

import g63888.ascii.model.AsciiPaint;
import g63888.ascii.view.*;
import java.util.Scanner;

public class AsciiController {

    private final AsciiPaint paint;
    private final View view;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Constructor for AsciiController.
     * Initializes the controller with an AsciiPaint object and sets up the view.
     *
     * @param paint the AsciiPaint object to control and interact with
     */
    public AsciiController(AsciiPaint paint) {
        this.paint = paint;
        this.view = new View(paint);
    }

    /**
     * Starts the main loop for the ASCII paint application.
     * Continuously prompts the user for commands until the user enters "Quit".
     */
    public void start() {
        String command;
        do {
            view.displayMenu();
            System.out.print("Enter command: ");
            command = scanner.nextLine().trim();
            if (!command.equalsIgnoreCase("Quit")) {
                parseCommand(command);
            }
        } while (!command.equalsIgnoreCase("Quit"));
    }

    /**
     * Parses the user's command and performs the corresponding action.
     * Commands include adding shapes, moving shapes, changing colors, and more.
     *
     * @param command the user input command
     */
    private void parseCommand(String command) {
        try {
            if (command.startsWith("add circle")) {
                paint.addCircle(command);
            } else if (command.startsWith("add rectangle")) {
                paint.addRectangle(command);
            } else if (command.startsWith("add square")) {
                paint.addSquare(command);
            } else if (command.equals("show")) {
                view.showShapes();
            } else if (command.equals("list")) {
                view.listShapes();
            } else if (command.startsWith("move")) {
                paint.moveShape(command);
            } else if (command.startsWith("color")) {
                paint.setColor(command);
            } else if (command.startsWith("delete")) {
                paint.removeShape(command);
            } else {
                View.display("Unknown command");
            }
        } catch (NumberFormatException e) {
            View.display("Invalid number format: " + e.getMessage());
        } catch (Exception e) {
            View.display("An error occurred: " + e.getMessage());
        }
    }
}
