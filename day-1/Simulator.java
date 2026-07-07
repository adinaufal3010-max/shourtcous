import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulator {
    private static final int MAX_PITY = 10;
    private static final int MAX_GUARANTEE = 120;

    private int totalPulls;
    private int pity;
    private int guarantee;
    private int legendaryCount;
    private int featuredCount;
    private final Random random = new Random();

    public PullResult pull() {
        boolean featuredGuaranteed = guarantee + 1 >= MAX_GUARANTEE;
        boolean pityGuaranteed = pity + 1 >= MAX_PITY;

        PullResult result = new PullResult(totalPulls + 1, "Rare", false, false);

        if (featuredGuaranteed) {
            result = new PullResult(totalPulls + 1, "Featured", false, true);
        } else if (pityGuaranteed) {
            result = new PullResult(totalPulls + 1, "Legendary", true, false);
        } else {
            int roll = random.nextInt(100) + 1;

            if (roll <= 70) {
                result = new PullResult(totalPulls + 1, "Rare", false, false);
            } else if (roll <= 95) {
                result = new PullResult(totalPulls + 1, "Epic", false, false);
            } else {
                result = new PullResult(totalPulls + 1, "Legendary", false, false);
            }
        }

        applyResult(result);
        return result;
    }

    public List<PullResult> pullMany(int amount) {
        List<PullResult> results = new ArrayList<>();

        for (int index = 0; index < amount; index++) {
            results.add(pull());
        }

        return results;
    }

    public void printStats() {
        System.out.println();
        System.out.println("Total pulls: " + totalPulls);
        System.out.println("Pity: " + pity + " / " + MAX_PITY);
        System.out.println("Featured guarantee: " + guarantee + " / " + MAX_GUARANTEE);
        System.out.println("Legendary count: " + legendaryCount);
        System.out.println("Featured count: " + featuredCount);
    }

    private void applyResult(PullResult result) {
        totalPulls++;

        boolean isLegendary = result.rarity().equals("Legendary");
        boolean isFeatured = result.rarity().equals("Featured");

        pity = isLegendary || isFeatured ? 0 : pity + 1;
        guarantee = isFeatured ? 0 : guarantee + 1;
        legendaryCount += isLegendary ? 1 : 0;
        featuredCount += isFeatured ? 1 : 0;
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        List<PullResult> results = simulator.pullMany(10);

        for (PullResult result : results) {
            System.out.print("#" + result.number() + " " + result.rarity());

            if (result.pityTriggered()) {
                System.out.print(" (pity)");
            }

            if (result.featuredTriggered()) {
                System.out.print(" (120 guarantee)");
            }

            System.out.println();
        }

        simulator.printStats();
    }

    public record PullResult(
        int number,
        String rarity,
        boolean pityTriggered,
        boolean featuredTriggered
    ) {
    }
}
