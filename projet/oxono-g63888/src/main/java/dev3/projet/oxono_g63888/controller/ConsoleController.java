package dev3.projet.oxono_g63888.controller;

import dev3.projet.oxono_g63888.model.*;
import dev3.projet.oxono_g63888.model.Observer.Observer;
import dev3.projet.oxono_g63888.model.Observer.OxonoEvent;
import dev3.projet.oxono_g63888.view.ConsoleView;

import java.util.regex.Matcher;

public class ConsoleController implements Observer {
    private final Game game;
    private final ConsoleView view;
    private int boardSize;
    private int aiChoice;

    public ConsoleController(Game game, ConsoleView view) {
        this.game = game;
        this.view = view;
        game.registerObserver(this);
    }

    /**
     * Initiates the game setup and starts the game loop.
     *
     * This method performs the initial setup by displaying a menu to the user,
     * prompting them to select the board size, and choosing the opponent type
     * (human or AI). Once the setup is complete, it transitions into processing
     * game commands that drive the gameplay.
     */
    public void startGame() {
        view.displayMenu();
        boardSize = view.getBoardSize();
        aiChoice = view.getAIChoice();
        processGameCommands();
    }

    /**
     * Handles the processing of user game commands during gameplay.
     * This method repeatedly waits for user input, validates it against predefined commands,
     * and executes the associated game functionality until the game reaches the GAME_OVER state.
     *
     * Game commands are read from the console via the view. The method:
     * - Parses the entered command using {@code InputRegexPattern}.
     * - Validates whether the command matches any known pattern.
     * - Executes the corresponding game operation if the command is valid.
     * - Displays an error message if the command is invalid or if an exception occurs during execution.
     *
     * The game state is continually checked, and the loop terminates when the game reaches the GAME_OVER state.
     */
    private void processGameCommands() {
        view.getCommandInput();

        while (game.getGameState() != GameState.GAME_OVER) {
            String command = view.getNextLine();
            InputRegexPattern parser = InputRegexPattern.findCommand(command);

            if (parser == null) {
                view.showErrorMessage("Commande inconnue. Veuillez réessayer.");
                continue;
            }

            try {
                executeCommand(parser, command);
            } catch (OxonoException e) {
                view.showErrorMessage(e.getMessage());
            }
        }
    }

    /**
     * Executes a command based on the specified input pattern and command string.
     * This method uses a parser to match the given command against predefined input patterns
     * and executes the corresponding action based on the matched pattern.
     *
     * @param parser the input regex pattern used to determine the type of action to execute
     * @param command the string representing the user-entered command to process
     * @throws OxonoException if an error occurs during the execution of the command,
     *         such as invalid game state or improper command format
     */
    private void executeCommand(InputRegexPattern parser, String command) throws OxonoException {
        Matcher matcher = parser.getMatcher(command);
        if (!matcher.matches()) return;

        switch (parser) {
            case START -> game.initializeGame(boardSize, aiChoice);
            case RESTART -> restartGame();
            case UNDO -> game.undo();
            case REDO -> game.redo();
            case SURRENDER -> {
                view.showSurrenderMessage();
                game.surrender();
            }
            case HELP -> view.displayHelp();
            case QUIT -> handleQuit();
            case AI_MOVE -> game.playAITurn();
            case TOTEM_MOVE, PAWN_MOVE -> processPlayerMove(parser, matcher);
        }

        if (game.getGameState() != GameState.GAME_OVER) {
            playNextTurn();
        }
    }

    /**
     * Restarts the game to its initial state if the current game status allows it.
     * If the game is not in progress (i.e., not in the STARTED state),
     * the game will be reinitialized with the configured board size and AI settings,
     * and a restart message will be displayed to the user.
     * If the game is currently in progress, an exception is thrown indicating
     * that the game cannot be restarted until it has started.
     *
     * @throws OxonoException if the game is currently in the STARTED state.
     */
    private void restartGame() throws OxonoException {
        if (game.getGameState() != GameState.STARTED) {
            game.initializeGame(boardSize, aiChoice);
            view.showRestartMessage();
        } else {
            throw new OxonoException("La partie n'a pas encore commencé.");
        }
    }

    /**
     * Handles the termination of the application.
     * This method displays a quit message to the user through the view
     * and exits the program with a status code of 0.
     * Typically called when a quit command is issued or the game ends and
     * the user chooses not to restart.
     */
    private void handleQuit() {
        view.showQuitMessage();
        System.exit(0);
    }

    /**
     * Processes the player's move based on the given input type and matcher.
     * The method validates the player's move and delegates the processing
     * to the appropriate game logic depending on the game state and move type.
     * If the move does not match the current game state, it is ignored.
     *
     * @param moveType The type of the move to be processed, as identified by the InputRegexPattern.
     *                 It specifies whether the move is a totem move or a pawn move.
     * @param matcher  A Matcher object containing the player's input grouped into identifiable patterns.
     *                 The groups are used to extract information such as the piece type, row, and column.
     * @throws OxonoException If the input is invalid or the move violates game rules.
     */
    private void processPlayerMove(InputRegexPattern moveType, Matcher matcher) throws OxonoException {
        if (game.getGameState() == GameState.AI_TURN) return;

        String pieceType = matcher.group(1);
        int row = Integer.parseInt(matcher.group(2));
        int col = Integer.parseInt(matcher.group(3));
        String input = String.format("%s %d %d", pieceType, row, col);

        if (moveType == InputRegexPattern.TOTEM_MOVE && game.getGameState() == GameState.WAITING_FOR_TOTEM) {
            game.processTotemInput(input);
        } else if (moveType == InputRegexPattern.PAWN_MOVE && game.getGameState() == GameState.WAITING_FOR_PAWN) {
            game.processPawnInput(input);
        }
    }

    /**
     * Executes the next turn of the game based on the current player's type.
     *
     * If the current player is an AI, it invokes the display of an AI-specific
     * message to prompt the user to press "Enter" for the AI to play its turn.
     *
     * If the current player is not an AI, it prompts the player to input their next
     * move. The type of prompt displayed depends on the current game state:
     * - If the game is in the "WAITING_FOR_TOTEM" state, the player is instructed
     *   to enter the position of their totem.
     * - Otherwise, the player is instructed to enter the position of their pawn.
     */
    private void playNextTurn() {
        if (game.isCurrentPlayerAI()) {
            view.showAIMessage(game.getToString());
        } else {
            String prompt = (game.getGameState() == GameState.WAITING_FOR_TOTEM) ?
                    "Entrez la position de votre totem (ex : X 2 3)" :
                    "Entrez la position de votre pion (ex : RX 2 3)";
            view.getPlayerInput(game.getToString(), prompt);
        }
    }

    /**
     * Updates the console view based on the current game state and event received.
     *
     * @param game The current instance of the game containing the game state.
     * @param event The event that has occurred, representing changes in the game state.
     */
    @Override
    public void update(Game game, OxonoEvent event) {
        view.displayBoard(game);
        view.displayRack(game.getRemainingPawns());

        switch (event.getEvent()) {
            case WIN -> handleGameOver(true);
            case DRAW -> handleGameOver(false);
            case UNDO -> view.showUndoMessage();
            case REDO -> view.showRedoMessage();
        }
    }

    /**
     * Handles the end of the game by determining the result, displaying the appropriate message,
     * and prompting the user to restart or quit the game. It triggers appropriate actions
     * based on user input and the game's state.
     *
     * @param isWin a boolean indicating whether the game was won (true) or ended in a draw (false).
     */
    private void handleGameOver(boolean isWin) {
        if (isWin) {
            view.showWinMessage(game.getToString());
        } else {
            view.showDrawMessage();
        }

        if (view.showRestart().equalsIgnoreCase("yes")) {
            try {
                restartGame();
            } catch (OxonoException e) {
                view.showErrorMessage(e.getMessage());
            }
        } else {
            handleQuit();
        }
    }
}

