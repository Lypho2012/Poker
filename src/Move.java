public class Move {
    int type;
    int amount;
    Move (int type) {
        this.type = type;
        this.amount = 0;
    }
    Move (int type, int amount) {
        this.type = type;
        this.amount = amount;
    }
}
