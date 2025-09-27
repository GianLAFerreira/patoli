package padroes.projeto.patoli.model;

import java.util.Random;

public class CoinDice {
    private final Random random = new Random();
    private int lastRoll = 0;

    public int roll() {
        int paintedUp = 0;
        for (int i = 0; i < 5; i++) {
            boolean painted = random.nextBoolean(); // true ~ lado pintado
            if (painted) paintedUp++;
        }
        lastRoll = paintedUp; // 0..5
        return lastRoll;
    }

    public int getLastRoll() {
        return lastRoll;
    }
}
