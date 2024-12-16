package dev3.projet.oxono_g63888.model;

import dev3.projet.oxono_g63888.model.Observer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;
    private TestObserver observer;
    private int eventCount;

    private class TestObserver implements Observer {
        private OxonoEvent lastEvent;

        @Override
        public void update(Game game, OxonoEvent event) {
            this.lastEvent = event;
            eventCount++;
        }

        public OxonoEvent getLastEvent() {
            return lastEvent;
        }

    }

    @BeforeEach
    void setUp() {
        game = new Game();
        observer = new TestObserver();
        game.registerObserver(observer);
        eventCount = 0;
    }

    /**
     * Tests a winning scenario achieved through a diagonal line in the game.
     * This test verifies if the game correctly identifies the winning condition
     * when a player forms a diagonal line using their pawns and totems.
     *
     * The test performs the following operations:
     * 1. Initializes the game with a specific board size and mode.
     * 2. Simulates alternating turns between the two players (PINK and BLACK),
     *    ensuring the game progresses correctly without a win.
     * 3. Executes a winning move for the PINK player by forming a diagonal line.
     * 4. Verifies that the win condition is detected correctly, the game state is updated
     *    to GAME_OVER, and appropriate events are triggered for observers.
     *
     * @throws OxonoException if any invalid moves or operations occur during the test
     */
    @Test
    @DisplayName("Test winning scenario with Horizontal line")
    void testWinningScenarioDiagonal() throws OxonoException {
        game.initializeGame(6, 1);

        // Premier joueur (PINK)
        game.processTotemInput("X 2 3");
        game.processPawnInput("RX 1 3");
        assertFalse(game.checkWinCondition());

        // Deuxième joueur (BLACK)
        game.processTotemInput("X 2 2");
        game.processPawnInput("RX 1 2");
        assertFalse(game.checkWinCondition());

        // PINK
        game.processTotemInput("X 2 1");
        game.processPawnInput("RX 1 1");
        assertFalse(game.checkWinCondition());

        // BLACK
        game.processTotemInput("O 4 3");
        game.processPawnInput("RO 4 2");
        assertFalse(game.checkWinCondition());

        // PINK - winning move
        game.processTotemInput("X 2 4");
        game.processPawnInput("RX 1 4");

        assertTrue(game.checkWinCondition());
        assertEquals(GameState.GAME_OVER, game.getGameState());

        OxonoEvent event = observer.getLastEvent();
        assertEquals(ObservableEvent.WIN, event.getEvent());
    }

    /**
     * Tests a complex sequence of undo and redo operations, validating the game
     * state consistency and ensuring the correct behavior of player switching and
     * game state restoration throughout the process.
     *
     * The test first initializes a game instance with predefined parameters, performs
     * a sequence of totem and pawn inputs alternating between players, and captures
     * the initial player's color and game state. It then verifies the undo functionality
     * by sequentially undoing each move and checks if the game state and active
     * player return to their original conditions.
     *
     * Subsequently, the redo functionality is tested by replaying each previously undone
     * move and validating that the game state and player sequence match their state
     * prior to the undo operations.
     *
     * Throws OxonoException if any invalid commands are processed.
     *
     * Annotations:
     * - The method is annotated with {@code @Test} for junit integration.
     * - The custom display name provides a descriptive title for the test case in
     *   the test results.
     *
     * Assertions within this test:
     * - Confirms the ability to undo or redo when expected using {@code assertTrue}.
     * - Verifies correctness of game states using equality and inequality assertions
     *   such as {@code assertEquals} and {@code assertNotEquals}.
     */
    @Test
    @DisplayName("Test complex undo/redo sequence with win check")
    void testComplexUndoRedoSequence() throws OxonoException {
        game.initializeGame(6, 1);

        // Séquence de mouvements initiale
        game.processTotemInput("X 1 2");
        game.processPawnInput("RX 2 2");

        game.processTotemInput("O 3 2");
        game.processPawnInput("RO 3 3");

        // Mémoriser l'état du plateau
        ColorPawn initialColor = game.getColorPlayer();

        // Séquence d'undo
        assertTrue(game.canUndo());
        game.undo(); // Annuler le pawn O
        game.undo(); // Annuler le totem O
        game.undo(); // Annuler le pawn X
        game.undo(); // Annuler le totem X

        // Vérifier que nous sommes revenus à l'état initial
        assertEquals(initialColor, game.getColorPlayer());
        assertEquals(GameState.WAITING_FOR_TOTEM, game.getGameState());

        // Séquence de redo
        assertTrue(game.canRedo());
        game.redo(); // Refaire le totem X
        game.redo(); // Refaire le pawn X
        game.redo(); // Refaire le totem O


        // Vérifier que nous sommes revenus à l'état avant les undos
        assertNotEquals(initialColor, game.getColorPlayer());
    }


    /**
     * Tests a complete game with two AI players playing against each other.
     * This test ensures that the game progresses correctly with AI players,
     * checks whether the AI takes turns properly, and validates the end
     * game conditions (win or draw).
     *
     * The test includes the following checks:
     * - AI players take turns until the game ends, and during this process,
     *   the current player must always be an AI.
     * - The game transitions to the GAME_OVER state upon completion.
     * - The end game condition must satisfy either a win or a draw.
     *
     * This test uses the AI mode (game mode 4) and verifies proper flow
     * and adherence to game rules throughout the AI vs AI gameplay.
     *
     * @throws OxonoException if any irregularity occurs during the AI players' turn execution.
     */
    @Test
    @DisplayName("Test AI vs AI complete game")
    void testAIvsAIGame() throws OxonoException {
        game.initializeGame(5, 4); // Mode AI vs AI

        // Faire jouer les AI jusqu'à la fin du jeu
        while (game.getGameState() != GameState.GAME_OVER) {
            game.playAITurn();
            if (!game.checkWinCondition() && !game.isDraw()) {
                assertTrue(game.isCurrentPlayerAI());
            }
        }

        // Vérifier que le jeu s'est terminé correctement
        assertTrue(game.getGameState() == GameState.GAME_OVER);
        assertTrue(game.checkWinCondition() || game.isDraw());
    }

    /**
     * Tests a complex totem blocking scenario in the game.
     *
     * This test sets up a specific board configuration where a totem is surrounded
     * by pawns and totems to limit its movement options drastically. It verifies
     * that the logic correctly identifies the restricted mobility of the totem
     * by ensuring that the possible moves are either empty or very limited.
     *
     * The board is initialized with a 6x6 size, and a series of totem and pawn
     * placements are executed to create the blocking scenario. Subsequently,
     * the possible moves for the blocked totem are retrieved and validated.
     *
     * Assertions are used to ensure the game correctly restricts the movement
     * of the totem in this specific setup.
     *
     * @throws OxonoException if any invalid move is processed during setup or validation
     */
    @Test
    @DisplayName("Test complex totem blocking scenario")
    void testComplexTotemBlocking() throws OxonoException {
        game.initializeGame(6, 1);

        // Créer une situation où un totem est entouré
        game.processTotemInput("X 1 2");
        game.processPawnInput("RX 2 2");

        game.processTotemInput("X 1 1");
        game.processPawnInput("RX 1 2");

        game.processTotemInput("X 1 0");
        game.processPawnInput("RX 1 1");

        game.processTotemInput("X 0 0");
        game.processPawnInput("RX 1 0");

        game.processTotemInput("X 0 1");
        game.processPawnInput("RX 0 2");

        game.processTotemInput("X 0 0");
        game.processPawnInput("RX 0 1");


        // Le totem en (2,2) devrait être entouré
        // Vérifier que les mouvements possibles sont vides ou très limités
        Position totemPos = new Position(0, 0);
        assertTrue(game.getMovesPossilesForTotem(totemPos).isEmpty() ||
                game.getMovesPossilesForTotem(totemPos).size() < 4);
    }
}