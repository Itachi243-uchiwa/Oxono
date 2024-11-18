package dev3.projet.oxono_g63888.view;

import dev3.projet.oxono_g63888.model.*;

import java.util.Scanner;

public class ConsoleView {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public String getNextLine() {
        return scanner.nextLine();
    }

    public void displayBoard(Board board) {
        for (int i = 0; i < board.getSize(); i++) {
            System.out.println("+---".repeat(board.getSize()) + "+");
            for (int j = 0; j < board.getSize(); j++) {
                Token token = board.getToken(new Position(i, j));
                if (token != null) {
                    if (board.isTotem(token)) {
                        Totem totem = (Totem) token;
                        System.out.print("| " + totem.toString() + " ");
                    } else {
                        Pawn pawn = (Pawn) token;
                        System.out.print("| " + pawn.toString() + " ");
                    }
                } else {
                    System.out.print("|   ");
                }
            }
            System.out.println("|");
        }
        System.out.println("+---".repeat(board.getSize()) + "+");

        System.out.println();
    }


    public String getPlayerInput(Player player, String prompt) {
        System.out.println(player.getColor() + ", " + prompt);
        return scanner.nextLine();
    }

        public void showErrorMessage(String message) {
            System.out.println("Erreur : " + message);
        }

        public void showWinMessage(Player player) {
            System.out.println("Le joueur " + player.getColor() + " a gagné !");
        }

        public void showDrawMessage() {
            System.out.println("Le plateau est plein, match nul !");
        }

        public void showRestartMessage() {
            System.out.println("Le jeu a été redémarré.");
        }

        public void showUndoMessage() {
            System.out.println("Dernier coup annulé.");
        }

        public void showRedoMessage() {
            System.out.println("Dernier coup refait.");
        }

        public void showQuitMessage() {
            System.out.println("Merci d'avoir joué ! À bientôt.");
        }
        public void showSurrenderMessage() {
            System.out.println("Le joueur a abandonné la partie.");
        }

        public String getCommandInput() {
            System.out.println("Entrez une commande (start, restart, quit, undo, redo) ou appuyez sur Entrée pour continuer : ");
            return scanner.nextLine().trim();
        }

        public int getAIChoice() {
            while (true) {
                System.out.println("Choisissez Jouer soit avec un Humain ou avec L'IA :");
                System.out.println("1. Humain");
                System.out.println("2. RandomAI");
                System.out.println("3. MinMaxAI");
                System.out.println("4. AI vs AI");
                try {
                    int choice = Integer.parseInt(scanner.nextLine());
                    if (choice == 1 || choice == 2 || choice == 3 || choice == 4) {
                        return choice;
                    }
                    System.out.println("Veuillez entrer 1, 2, 3 ou 4.");
                } catch (NumberFormatException e) {
                    System.out.println("Veuillez entrer un nombre valide.");
                }
            }
        }
        public int getBoardSize() {
            while (true) {
                System.out.println("Choisissez la taille du plateau (entre 6 et 12) :");
                try {
                    int size = Integer.parseInt(scanner.nextLine());
                    if (size >= 6 && size <= 12) {
                        return size;
                    }
                    System.out.println("Veuillez entrer une taille valide entre 6 et 12.");
                } catch (NumberFormatException e) {
                    System.out.println("Veuillez entrer un nombre valide.");
                }
            }
        }
    public void displayHelp() {
        System.out.println("=== OXONO - Guide de jeu ===\n");

        System.out.println("But du jeu");
        System.out.println("==========");
        System.out.println("L'objectif est d'aligner 4 pièces identiques de l'une des façons suivantes :");
        System.out.println("- Même couleur (rose ou noir)");
        System.out.println("- Même symbole (X ou O)");
        System.out.println("L'alignement doit être horizontal ou vertical.\n");

        System.out.println("Déroulement du tour");
        System.out.println("===================");
        System.out.println("1. Déplacez un totem (X ou O) :");
        System.out.println("   - Déplacement en ligne droite (horizontal ou vertical)");
        System.out.println("   - Sur des cases vides uniquement");
        System.out.println("   - Distance au choix (minimum 1 case)\n");

        System.out.println("2. Placez une pièce de votre couleur :");
        System.out.println("   - Même symbole que le totem déplacé");
        System.out.println("   - Sur une case adjacente au totem");
        System.out.println("   - Horizontalement ou verticalement\n");

        System.out.println("Règles spéciales");
        System.out.println("================");
        System.out.println("- Totem enclavé (entouré de pièces) :");
        System.out.println("  * Peut sauter par-dessus les pièces");
        System.out.println("  * Si la nouvelle position est également enclavée, placement libre sur le plateau");
        System.out.println("  * Si aucun déplacement en ligne n'est possible, placement libre sur le plateau\n");

        System.out.println("Note importante");
        System.out.println("===============");
        System.out.println("- Les totems ne comptent pas dans les alignements.");
        System.out.println("- Le joueur avec les pièces roses commence toujours.");
        System.out.println("- La partie se termine par un match nul si toutes les pièces sont placées sans alignement gagnant.\n");

        System.out.println("Commandes");
        System.out.println("=========");
        System.out.println("- Pour placer un totem, entrez la commande : X 2 3");
        System.out.println("- Pour placer un pion, entrez la commande : RX 2 3\n");

        System.out.println("=== Bonne chance et amusez-vous bien ! ===");
    }


    public void displayMenu() {
            System.out.println("====================== BIENVENUE DANS OXONO =========================");
            System.out.println("Instructions du jeu:");
            System.out.println("1. Placez vos totems (X ou O) sur le plateau en entrant la commande : X 2 3");
            System.out.println("2. Placez vos pions (RX ou RO) après le totem, avec la commande : RX 2 3");
            System.out.println("3. Commandes spéciales disponibles :");
            System.out.println("   - start : Commence une nouvelle partie");
            System.out.println("   - restart : Redémarre la partie actuelle");
            System.out.println("   - undo : Annule le dernier coup");
            System.out.println("   - redo : Répète le dernier coup annulé");
            System.out.println("   - surrender : Pour abandonner la partie");
            System.out.println("   - help : Affiche un guide de jeu");
            System.out.println("   - quit : Quitte le jeu");
            System.out.println("Bon jeu !");
            System.out.println("======================================================================");
            System.out.println();
        }

    public void displayRack(int[] pawnsRemaining) {


            System.out.println("Player Pink ======================================== Player Black :");
            System.out.println();
            System.out.println("Pawns - X: " + pawnsRemaining[0] + "                          " + "Pawns - X: " + pawnsRemaining[2]);
            System.out.println("Pawns - O: " + pawnsRemaining[1] + "                          " + "Pawns - O: " + pawnsRemaining[3]);
            System.out.println();
        }

}


