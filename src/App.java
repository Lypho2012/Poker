import java.io.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // print instructions for poker
        System.out.println("This follows the Texas Hold'em rules. The highest suit is diamonds, followed by hearts, clubs, and spades.");

        // checking for number of players
        System.out.println("How many humans are playing? ");
        int numPlayers = Integer.parseInt(in.readLine());
        System.out.println("Are you playing with the bot? (y/n) ");
        boolean hasBot = in.readLine().charAt(0) == 'y';
        
        // start game
        System.out.println("Are you ready to start? (y/n) ");
        if (in.readLine().charAt(0) == 'y') {
            System.out.println();
            play(in, numPlayers, hasBot);
        }
    }
    static void play(BufferedReader in, int numPlayers, boolean hasBot) throws Exception {
        // initialize default state of poker table
        Player[] players = new Player[numPlayers];
        boolean[] inGame = new boolean[numPlayers];
        int playersInGame = numPlayers;
        for (int i=0; i<numPlayers; i++) {
            players[i] = new Player();
        }
        Player computer = new Player();
        Player burned = new Player();
        Player table = new Player();
        ArrayList<Card> deck = new ArrayList<Card>();
        for (int rank=2; rank<=13; rank++) {
            for (int suit=0; suit<=3; suit++) {
                deck.add(new Card(rank,suit));
            }
        }

        // deal cards to all players
        for (int i=0; i<2; i++) {
            for (Player player: players) {
                dealCard(player,deck);
            }
            if (hasBot) 
                dealCard(computer,deck);
        }

        // play the round
        Move lastMove = new Move(-1);
        for (int round=0; round<4; round++) {
            // burn a card
            dealCard(burned,deck);

            // reveal community cards
            if (round == 1) {
                for (int i=0; i<3; i++) {
                    dealCard(table,deck);
                    for (Player p: players) {
                        dealCard(p,deck.get(deck.size()-1));
                    }
                    if (hasBot) {
                        dealCard(computer,deck.get(deck.size()-1));
                    }
                }
                System.out.println("Revealing flop cards: ");
            } else if (round == 2) {
                dealCard(table,deck);
                for (Player p: players) {
                    dealCard(p,deck.get(deck.size()-1));
                }
                if (hasBot) {
                    dealCard(computer,deck.get(deck.size()-1));
                }
                System.out.println("Revealing turn card: ");
            } else if (round == 3) {
                System.out.println("Revealing river card: ");
            }
            if (round > 0)
                table.showHand();

            // reset valid moves for this round
            String[] moves = {"check", "call", "raise", "fold"};
            boolean[] isValid = {true, true, true, true};
            if (lastMove.type == 2) {
                isValid[0] = false;
            }
            while (lastMove.amount != 0) {
                // humans take turn
                for (int i=0; i<numPlayers; i++) {
                    if (!inGame[i]) {
                        continue;
                    }
                    System.out.println("It is player "+i+"'s turn.");
                    System.out.println("Here is player "+i+"'s hand including the community cards: ");
                    Player player = players[i];
                    player.showHand();
                    takeTurn(table, player, isValid, moves, in, lastMove);
                    if (lastMove.type == 3) {
                        inGame[i] = false;
                        playersInGame --;
                    }
                    // add empty spaces
                    for (int j=0; j<20; j++) {
                        System.out.println();
                    }
                }

                if (playersInGame == 0) {
                    break;
                }
                // TODO: computer takes turn
                double probability = computer.getValueOfHand()*1.0/2598960;
                // if the probability of winning is high, raise/call
                if (probability > 0.6) {
                    computer.makeMove(lastMove, new Move(1));
                }
                // if the probability of winning is average, check/call
                // if the probability of winning is low, fold
                else {
                    computer.makeMove(lastMove, new Move(3));
                }
            }
        }
        if (playersInGame == 0) {
            System.out.println("The bot wins!");
        } else {
            Player topPlayer = null;
            int playerNumber = 0;
            for (int i=0; i<numPlayers; i++) {
                if (inGame[playerNumber]) {
                    if (topPlayer == null) {
                        topPlayer = players[playerNumber];
                        playerNumber = i;
                    } else if (topPlayer.valueOfHand < players[playerNumber].valueOfHand) {
                        topPlayer = players[playerNumber];
                        playerNumber = i;
                    } else if (topPlayer.valueOfHand == players[playerNumber].valueOfHand && topPlayer.highCard.compareTo(players[playerNumber].highCard) < 0) {
                        topPlayer = players[playerNumber];
                        playerNumber = i;
                    }
                }
            }
            if (topPlayer.valueOfHand > computer.valueOfHand) {
                System.out.println("Player "+playerNumber+" wins!");
            } else if (topPlayer.valueOfHand == computer.valueOfHand && topPlayer.highCard.compareTo(computer.highCard) < 0) {
                System.out.println("Player "+playerNumber+" wins!");
            } else {
                System.out.println("The bot wins!");
            }
        }
    }
    static void shuffle(ArrayList<Card> deck) {
        Collections.shuffle(deck);
    }
    static void dealCard(Player p, ArrayList<Card> deck) {
        p.hand.add(deck.remove(deck.size()-1));
    }
    static void dealCard(Player p, Card c) {
        p.hand.add(c);
    }
    static void takeTurn(Player table, Player player, boolean[] isValid, String[] moves, BufferedReader in, Move lastMove) throws IOException {
        System.out.println("You have "+player.tokens+" tokens.");
        Move player_move = new Move(-1);
        boolean isValidMove = false;
        while (!isValidMove) {
            String question = "Do you want to ";
            for (int i=0; i<4; i++) {
                if (isValid[i]) {
                    System.out.print(moves[i]+", ");
                }
            }
            question = question.substring(0,question.length()-2)+"?\n";
            System.out.print(question);
            player_move.type = Integer.parseInt(in.readLine());

            if (player_move.type == 2) {
                System.out.println("How much do you want to raise by? ");
                player_move.amount = Integer.parseInt(in.readLine());
            }

            isValidMove = player.makeMove(player_move, lastMove);
        }
    }
}
