public class Card implements Comparable<Card>{
    int rank, suit;
    Card (int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }
    @Override
    public int compareTo(Card other) {
        if (rank == other.rank) {
            return Integer.compare(suit, other.suit);
        }
        return Integer.compare(rank, other.rank);
    }
    @Override
    public String toString() {
        char[] suits = {'\u2660','\u2663','\u2764','\u2666'}; // {spade, club, heart, diamond}
        char[] special_ranks = {'J','Q','K','A'};

        String res = "";

        // add rank
        if (rank <= 10) {
            res += rank;
        } else {
            res += special_ranks[rank-11];
        }

        // add suit
        res += suits[suit];

        return res;
    }
}
