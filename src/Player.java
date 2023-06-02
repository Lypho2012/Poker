import java.util.*;
public class Player implements Comparable<Player>{
    ArrayList<Card> hand;
    int tokens;
    int bet;
    ArrayList<ArrayList<Card>> combinations;
    Card highCard;
    int valueOfHand = -1;
    Player () {
        hand = new ArrayList<>();
        tokens = 10;
        bet = 0;
    }
    Player (int tokens) {
        hand = new ArrayList<>();
        this.tokens = tokens;
        bet = 0;
    }
    void showHand() {
        for (Card card: hand) {
            System.out.print(card+", ");
        }
    }
    boolean makeMove(Move move, Move lastMove) {
        if (move.type == 0) { // check
            if (lastMove.type == 2) {
                return false;
            }
            return true;
        } else if (move.type == 1) { // call
            if (lastMove.type == 0) {
                return false;
            }
            bet += Math.min(tokens, lastMove.amount);
            return true;
        } else if (move.type == 2) { // raise
            if (tokens < move.amount) {
                System.out.println("You currently have "+tokens+" tokens, which is not enough tokens to raise by "+move.amount+".");
                return false;
            }
            bet += move.amount;
            return true;
        } else if (move.type == 3) { // fold
            bet += tokens;
            return true;
        }
        return false;
    }
    int getValueOfHand() {
        // returns number of hands that can be beaten
        combinationsOfHand();
        System.out.println(hand);
        for (ArrayList<Card> combination: this.combinations) {
            System.out.println(combination);
        }
        if (isRoyalFlush()) {
            return valueOfHand=2598956; 
        } else if (isStraightFlush()) {
            return valueOfHand=2598920;
        } else if (isFourOfAKind()) {
            return valueOfHand=2598296;
        } else if (isFullHouse()) {
            return valueOfHand=2594552;
        } else if (isRegularFlush()) {
            return valueOfHand=2589444;
        } else if (isThreeOfAKind()) {
            return valueOfHand=2534532;
        } else if (isRegularStraight()) {
            return valueOfHand=2524332;
        } else if (isTwoPair()) {
            return valueOfHand=2400780;
        } else if (isOnePair()) {
            return valueOfHand=1302540;
        } else {
            highCard = hand.get(hand.size()-1);
            return valueOfHand=0;
        }
    }

    boolean isStraightFlush() {
        for (ArrayList<Card> h: combinations) {
            int suit = h.get(0).suit;
            int start = h.get(0).rank;
            for (int i=0; i<5; i++) {
                if (h.get(i).rank != start+i || h.get(i).suit != suit) {
                    continue;
                }
            }
            highCard = h.get(h.size()-1);
            return true;
        }
        return false;
    }

    boolean isRoyalFlush() {
        for (ArrayList<Card> h: combinations) {
            int suit = h.get(0).suit;
            int start = h.get(0).rank;
            for (int i=0; i<4; i++) {
                if (h.get(i).rank != start+i || h.get(i).suit != suit) {
                    continue;
                }
            }
            if (start != 10)
                continue;
            highCard = h.get(h.size()-1);
            return true;
        }
        return false;
    }

    boolean isFourOfAKind() {
        for (ArrayList<Card> h: combinations) {
            int kind = h.get(0).rank;
            int numMatches = 0;
            for (int i=1; i<5; i++) {
                if (h.get(i).rank == kind) {
                    numMatches ++;
                }
            }
            if (numMatches == 0) {
                kind = h.get(1).rank;
                numMatches = 0;
                for (int i=0; i<5; i++) {
                    if (h.get(i).rank == kind) {
                        numMatches ++;
                    }
                }
                if (numMatches != 4)
                    continue;
                highCard = h.get(h.size()-1);
                return true;
            } else if (numMatches == 3) {
                highCard = h.get(h.size()-2);
            }
        }
        return false;
    }

    boolean isFullHouse() {
        for (ArrayList<Card> h: combinations) {
            int kind = h.get(0).rank;
            int numMatches = 0;
            for (int i=1; i<3; i++) {
                if (h.get(i).rank == kind) {
                    numMatches ++;
                }
            }
            if (numMatches == 2) {
                if (h.get(3).rank == h.get(4).rank) {
                    highCard = h.get(2);
                    return true;
                }
            } else if (numMatches == 1) {
                if (h.get(2).rank == h.get(3).rank && h.get(3).rank == h.get(4).rank) {
                    highCard = h.get(4);
                    return true;
                }
            }
        }
        return false;
    }

    boolean isRegularFlush() {
        for (ArrayList<Card> h: combinations) {
            int suit = h.get(0).suit;
            for (int i=1; i<5; i++) {
                if (h.get(i).suit != suit) {
                    continue;
                }
            }
            highCard = h.get(4);
            return true;
        }
        return false;
    }

    boolean isThreeOfAKind() {
        for (ArrayList<Card> h: combinations) {
            int kind = h.get(0).rank;
            int numMatches = 0;
            for (int i=1; i<3; i++) {
                if (h.get(i).rank == kind) {
                    numMatches ++;
                }
            }
            if (numMatches == 2) {
                highCard = h.get(2);
                return true;
            }
            if (h.get(2).rank == h.get(3).rank && h.get(3).rank == h.get(4).rank) {
                highCard = h.get(4);
                return true;
            }
        }
        return false;
    }

    boolean isRegularStraight() {
        for (ArrayList<Card> h: combinations) {
            int start = h.get(0).rank;
            for (int i=0; i<5; i++) {
                if (h.get(i).rank != start+i) {
                    continue;
                }
            }
            highCard = h.get(4);
            return true;
        }
        return false;
    }

    boolean isTwoPair() {
        for (ArrayList<Card> h: combinations) {
            if (h.get(0).rank == h.get(1).rank) {
                if (hand.get(2).rank == hand.get(3).rank) {
                    highCard = h.get(3);
                    return true;
                }
                if (hand.get(3).rank == hand.get(4).rank) {
                    highCard = h.get(4);
                    return true;
                }
            }
            if (hand.get(2).rank == hand.get(3).rank && hand.get(3).rank == hand.get(4).rank) {
                highCard = h.get(4);
                return true;
            }
        }
        return false;
    }

    boolean isOnePair() {
        for (ArrayList<Card> h: combinations) {
            for (int i=1; i<5; i++) {
                if (h.get(i).rank == h.get(i-1).rank) {
                    highCard = h.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int compareTo(Player other) {
        return Integer.compare(getValueOfHand(),other.getValueOfHand());
    }

    void combinationsOfHand() {
        Collections.sort(hand);
        combinations = new ArrayList<ArrayList<Card>>();
        if (hand.size() == 5) {
            combinations.add(hand);
        }
        else if (hand.size() == 6) {
            for (int i=0; i<hand.size(); i++) {
                ArrayList<Card> newHand = new ArrayList<Card>();
                for (int k=0; k<hand.size(); k++) {
                    if (k != i) {
                        newHand.add(hand.get(k));
                    }
                }
                combinations.add(newHand);
            }
        } else {
            for (int i=0; i<hand.size(); i++) {
                for (int j=i+1; j<hand.size(); j++) {
                    ArrayList<Card> newHand = new ArrayList<Card>();
                    for (int k=0; k<hand.size(); k++) {
                        if (k != i && k != j) {
                            newHand.add(hand.get(k));
                        }
                    }
                    combinations.add(newHand);
                }
            }
        }
    }
}
