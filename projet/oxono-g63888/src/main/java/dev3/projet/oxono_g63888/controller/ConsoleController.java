package dev3.projet.oxono_g63888.controller;

import dev3.projet.oxono_g63888.model.Game;
import dev3.projet.oxono_g63888.model.GameState;
import dev3.projet.oxono_g63888.model.Observer.Observer;
import dev3.projet.oxono_g63888.model.Observer.OxonoEvent;
import dev3.projet.oxono_g63888.model.OxonoException;
import dev3.projet.oxono_g63888.view.ConsoleView;

import java.util.regex.Matcher;

public class ConsoleController implements Observer {
    private final Game game;
    private final ConsoleView view;
    private int aiChoice;
    private int boardSize;

    public ConsoleController(Game game, ConsoleView view) {
        this.game = game;
        this.view = view;
        game.registerObserver(this);
    }

    public void startGame() {
        view.displayMenu();
        boardSize = view.getBoardSize();
        aiChoice = view.getAIChoice();

        processGameCommands();
    }

    private void processGameCommands() {
        while (true) {
            String command = view.getCommandInput();
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

    private void executeCommand(InputRegexPattern parser, String command) throws OxonoException {
        Matcher matcher = parser.getMatcher(command);
        if (!matcher.matches()) return;

        switch (parser) {
            case START -> startNewGame();
            case RESTART -> restartGame();
            case UNDO -> performUndo();
            case REDO -> performRedo();
            case SURRENDER -> handleSurrender();
            case HELP -> view.displayHelp();
            case QUIT -> handleQuit();
            case AI_MOVE -> processAIMove();
            case TOTEM_MOVE, PAWN_MOVE -> processPlayerMove(parser, matcher);
        }
    }

    private void startNewGame() throws OxonoException {
        game.initializeGame(boardSize, aiChoice);
        playGameLoop();
    }

    private void restartGame() throws OxonoException {
        if (game.getGameState() == GameState.STARTED) {
            game.initializeGame(boardSize, aiChoice);
            view.showRestartMessage();
            playGameLoop();
        } else {
            throw new OxonoException("La partie n'a pas encore commencé.");
        }
    }

    private void performUndo() throws OxonoException {
        validateGameStarted();
        game.undo();
        continueGameAfterUndoRedo();
    }

    private void performRedo() throws OxonoException {
        validateGameStarted();
        game.redo();
        continueGameAfterUndoRedo();
    }

    private void continueGameAfterUndoRedo() {
        if (game.isCurrentPlayerAI()) {
            playGameLoop();
        } else {
            playPlayerTurn();
        }
    }

    private void handleSurrender() throws OxonoException {
        validateGameStarted();
        view.showSurrenderMessage();
        game.surrender();
        System.exit(0);
    }

    private void handleQuit() {
        view.showQuitMessage();
        System.exit(0);
    }

    private void validateGameStarted() throws OxonoException {
        if (game.getGameState() == null || game.getGameState() == GameState.GAME_OVER) {
            throw new OxonoException("La partie n'a pas encore commencé.");
        }
    }

    private void processAIMove() throws OxonoException {
        if (game.getGameState() == GameState.AI_TURN) {
            game.playAITurn();
        }
    }

    private void processPlayerMove(InputRegexPattern moveType, Matcher matcher) throws OxonoException {
        if (game.getGameState() != GameState.AI_TURN) {
            if (moveType == InputRegexPattern.TOTEM_MOVE && game.getGameState() == GameState.WAITING_FOR_TOTEM) {
                processTotemMove(matcher);
            } else if (moveType == InputRegexPattern.PAWN_MOVE && game.getGameState() == GameState.WAITING_FOR_PAWN) {
                processPawnMove(matcher);
            }
        }
    }

    private void processTotemMove(Matcher matcher) throws OxonoException {
        String totemType = matcher.group(1);
        int row = Integer.parseInt(matcher.group(2));
        int col = Integer.parseInt(matcher.group(3));
        game.processTotemInput(String.format("%s %d %d", totemType, row, col));
    }

    private void processPawnMove(Matcher matcher) throws OxonoException {
        String pawnType = matcher.group(1);
        int row = Integer.parseInt(matcher.group(2));
        int col = Integer.parseInt(matcher.group(3));
        game.processPawnInput(String.format("%s %d %d", pawnType, row, col));
    }

    private void playGameLoop() {
        do{

            if (game.isCurrentPlayerAI()) {
                playAITurn();
            } else {
                playPlayerTurn();
            }

            if (!game.checkWinCondition() && !game.isDraw()) {
                switchToNextPlayer();
            }
        } while (!game.isDraw() && !game.checkWinCondition());

        handleGameEnd();
    }

    private void playAITurn() {
        game.setGameState(GameState.AI_TURN);
        System.out.println("C'est au tour de l'IA " + game.getCurrentPlayer().getColor() +
                ". Appuyez sur Entrée pour que l'IA joue.");

        String input = view.getNextLine();
        processAITurnInput(input);
    }

    private void processAITurnInput(String input) {
        try {
            InputRegexPattern command = InputRegexPattern.findCommand(input);
            if (command == InputRegexPattern.AI_MOVE) {
                game.playAITurn();
            } else if (command != null) {
                executeCommand(command, input);
            } else {
                view.showErrorMessage("Commande invalide. Appuyez simplement sur Entrée pour faire jouer l'IA.");
            }
        } catch (OxonoException e) {
            view.showErrorMessage(e.getMessage());
        }
    }

    private void playPlayerTurn() {
        if (game.isUndoRedoInProgress()) {
            return;
        }
        if (game.getGameState() != GameState.GAME_OVER){

        if (game.getGameState() == GameState.WAITING_FOR_TOTEM){
            processPlayerInput("totem");
        }
        if (game.getGameState() == GameState.WAITING_FOR_PAWN) {
            processPlayerInput("pion");
        }

        }

    }

    private void processPlayerInput(String pieceType) {
        boolean validMove = false;
        InputRegexPattern expectedPattern = pieceType.equals("totem") ?
                InputRegexPattern.TOTEM_MOVE : InputRegexPattern.PAWN_MOVE;

        while (!validMove && game.getGameState() != GameState.GAME_OVER) {
            try {
                String prompt = pieceType.equals("totem") ?
                        "Entrez la position de votre totem (ex : X 2 3)" :
                        "Entrez la position de votre pion (ex : RX 2 3)";

                String input = view.getPlayerInput(game.getCurrentPlayer(), prompt);
                validMove = handlePlayerInput(input, expectedPattern);
            } catch (OxonoException e) {
                view.showErrorMessage("Erreur lors de l'entrée du " + pieceType + ": " + e.getMessage());
            }
        }
    }

    private boolean handlePlayerInput(String input, InputRegexPattern expectedPattern) throws OxonoException {
        InputRegexPattern command = InputRegexPattern.findCommand(input);

        if (command == null) {
            throw new OxonoException("Entrée invalide. Veuillez respecter le format demandé.");
        }

        if (command == expectedPattern) {
            executeCommand(command, input);
            return true;
        } else {
            executeCommand(command, input);
            return false;
        }
    }

    private void switchToNextPlayer() {
        game.switchPlayer();
    }

    private void handleGameEnd() {
        view.displayBoard(game.getBoard());
        if (game.checkWinCondition()) {
            view.showWinMessage(game.getCurrentPlayer());
        } else {
            view.showDrawMessage();
        }
    }
    @Override
    public void update(Game game, OxonoEvent event) {
        switch (event.getEvent()) {
                case GAME_START:
                    view.displayMenu();
                    break;
                case MOVE_TOTEM:
                    System.out.println("Move totem");
                    break;
                case PLACE_PAWN:

                    break;
                case WIN:
                    view.showWinMessage(game.getCurrentPlayer());
                    break;
                case DRAW:
                    view.showDrawMessage();
                    break;
                case UNDO:
                    view.showUndoMessage();
                    break;
                case REDO:
                    view.showRedoMessage();
                    break;
            }
        view.displayBoard(game.getBoard());
        view.displayRack(game.getRemainingPawns());
    }

}