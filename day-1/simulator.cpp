#include <iostream>
#include <random>
#include <string>
#include <vector>

constexpr int MaxPity = 10;
constexpr int MaxGuarantee = 120;

struct PullResult {
  int number;
  std::string rarity;
  bool pityTriggered;
  bool featuredTriggered;
};

class PityPullSimulator {
 public:
  PityPullSimulator() : rng(std::random_device{}()), roll(1, 100) {}

  PullResult pull() {
    const bool featuredGuaranteed = guarantee + 1 >= MaxGuarantee;
    const bool pityGuaranteed = pity + 1 >= MaxPity;

    PullResult result{
      totalPulls + 1,
      "Rare",
      false,
      false
    };

    if (featuredGuaranteed) {
      result.rarity = "Featured";
      result.featuredTriggered = true;
    } else if (pityGuaranteed) {
      result.rarity = "Legendary";
      result.pityTriggered = true;
    } else {
      const int value = roll(rng);
      if (value <= 70) {
        result.rarity = "Rare";
      } else if (value <= 95) {
        result.rarity = "Epic";
      } else {
        result.rarity = "Legendary";
      }
    }

    applyResult(result);
    return result;
  }

  std::vector<PullResult> pullMany(int amount) {
    std::vector<PullResult> results;
    results.reserve(amount);

    for (int index = 0; index < amount; index += 1) {
      results.push_back(pull());
    }

    return results;
  }

  void printStats() const {
    std::cout << "\nTotal pulls: " << totalPulls
              << "\nPity: " << pity << " / " << MaxPity
              << "\nFeatured guarantee: " << guarantee << " / " << MaxGuarantee
              << "\nLegendary count: " << legendaryCount
              << "\nFeatured count: " << featuredCount << "\n";
  }

 private:
  int totalPulls = 0;
  int pity = 0;
  int guarantee = 0;
  int legendaryCount = 0;
  int featuredCount = 0;
  std::mt19937 rng;
  std::uniform_int_distribution<int> roll;

  void applyResult(const PullResult& result) {
    totalPulls += 1;

    const bool isLegendary = result.rarity == "Legendary";
    const bool isFeatured = result.rarity == "Featured";

    pity = isLegendary || isFeatured ? 0 : pity + 1;
    guarantee = isFeatured ? 0 : guarantee + 1;
    legendaryCount += isLegendary ? 1 : 0;
    featuredCount += isFeatured ? 1 : 0;
  }
};

int main() {
  PityPullSimulator simulator;
  const auto results = simulator.pullMany(10);

  for (const PullResult& result : results) {
    std::cout << "#" << result.number << " " << result.rarity;

    if (result.pityTriggered) {
      std::cout << " (pity)";
    }

    if (result.featuredTriggered) {
      std::cout << " (120 guarantee)";
    }

    std::cout << "\n";
  }

  simulator.printStats();
  return 0;
}
