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

    public void startGame() {
        view.displayMenu();
        boardSize = view.getBoardSize();
        aiChoice = view.getAIChoice();
        processGameCommands();
    }

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

    private void executeCommand(InputRegexPattern parser, String command) throws OxonoException {
        Matcher matcher = parser.getMatcher(command);
        if (!matcher.matches()) return;

        switch (parser) {
            case START -> game.initializeGame(boardSize, aiChoice);
            case RESTART -> restartGame();
            case UNDO -> game.undo();
            case REDO -> game.redo();
            case SURRENDER -> game.surrender();
            case HELP -> view.displayHelp();
            case QUIT -> handleQuit();
            case AI_MOVE -> game.playAITurn();
            case TOTEM_MOVE, PAWN_MOVE -> processPlayerMove(parser, matcher);
        }

        if (game.getGameState() != GameState.GAME_OVER) {
            playNextTurn();
        }
    }

    private void restartGame() throws OxonoException {
        if (game.getGameState() != GameState.STARTED) {
            game.initializeGame(boardSize, aiChoice);
            view.showRestartMessage();
        } else {
            throw new OxonoException("La partie n'a pas encore commencé.");
        }
    }

    private void handleQuit() {
        view.showQuitMessage();
        System.exit(0);
    }

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

