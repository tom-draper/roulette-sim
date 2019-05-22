import java.util.Random;

public class NumberGenerator {

  private Random random = new Random();

  /**
   * Generate random number on the roulette board.
   * @return - randomly selected number.
   */
  public int generateNumber() {
    int randomNumber = Math.abs(random.nextInt()) % 37;

    System.out.println(randomNumber);
    
    return randomNumber;
  }

  /**
   * Generate a random colour (red or black).
   * @return - randomly selected colour.
   */
  public int generateColour() {
    int randomNumber = Math.abs(random.nextInt()) % 2;

    System.out.println(randomNumber);

    return randomNumber;
  }
}
