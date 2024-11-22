package dev3.projet.oxono_g63888.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

    class GameTest {
        private Game game;

        @BeforeEach
        void setUp() {
            game = new Game();
        }

        @Test
        void testInitializeGame_TwoPlayers() {
            game.initializeGame(6, 2);

            assertNotNull(game.getBoard());
            assertEquals(6, game.getBoard().getSize());
            assertNotNull(game.getCurrentPlayer());
            assertFalse(game.isCurrentPlayerAI());
            assertEquals(GameState.AI_TURN, game.getGameState());
        }

        @Test
        void testInitializeGame_ThreePlayers() {
            game.initializeGame(6, 4);

            assertNotNull(game.getBoard());
            assertTrue(game.isCurrentPlayerAI());
        }

        @Test
        void testCanUndo_BeforeAnyMove() {
            game.initializeGame(6, 2);
            assertFalse(game.canUndo());
        }

        @Test
        void testCanRedo_BeforeAnyMove() {
            game.initializeGame(8, 2);
            assertFalse(game.canRedo());
        }

        @Test
        void testGetRemainingPawns() {
            game.initializeGame(8, 2);
            int[] remainingPawns = game.getRemainingPawns();

            assertEquals(4, remainingPawns.length);
            for (int pawns : remainingPawns) {
                assertEquals(8, pawns);
            }
        }


        @Test
        void testGetMovesPossiblesForTotem() {
            game.initializeGame(8, 2);
            Position totemPos = new Position(0, 0); // Assuming this is the initial totem position

            List<Position> possibleMoves = game.getMovesPossilesForTotem(totemPos);
            assertNotNull(possibleMoves);
            assertFalse(possibleMoves.isEmpty());
        }

        @Test
        void testPositionsInsert() {
            game.initializeGame(8, 2);
            Position totemPos = new Position(0, 0); // Assuming initial totem position

            List<Position> insertPositions = game.positionsInsert(totemPos);
            assertNotNull(insertPositions);
            assertFalse(insertPositions.isEmpty());
        }

        @Test
        void testSurrender() {
            game.initializeGame(8, 2);

            game.surrender();
            assertEquals(GameState.SURRENDER, game.getGameState());
        }

        @Test
        void testIsDraw() {
            game.initializeGame(8, 2);
            assertFalse(game.isDraw());
        }

        @Test
        void testCheckWinCondition_NotWin() {
            game.initializeGame(8, 2);
            // Simulate a scenario where win condition is not met
            assertFalse(game.checkWinCondition());
        }
    }