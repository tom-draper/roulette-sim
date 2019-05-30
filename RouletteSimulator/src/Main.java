import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    boolean exit = false;
    boolean lose;

    NumberProperties numberProp = new NumberProperties();
    NumberGenerator numberGen = new NumberGenerator();
    Display display = new Display();
    InputAnalyser analyser = new InputAnalyser();
    ResultProcessor resultProcessor = new ResultProcessor();
    Strategy strategy = new Strategy();

    Scanner scanner = new Scanner(System.in);

    display.enterBank();
    Session session = new Session(scanner.nextDouble());

    display.possibleChips();
    display.commandFormat();

    do {
      scanner = new Scanner(System.in);
      String command = scanner.nextLine();

      double initialChip = analyser.getChip(command);
      double chip = initialChip;
      String placement = analyser.getPlacement(command);
      int spins = analyser.getFlagValue(command, 's');
      int simulations = analyser.getFlagValue(command, 'l');
      char strat = analyser.getStrategy(command, spins);

      if (spins == -1) {
        spins = 1;
      }
      if (simulations == -1) {
        simulations = 1;
      } else {
        session.resetSim();
      }

      display.displayStrategy(strat);

      /* Double strategy */
      strategy.setDoubleStratMax(analyser.getFlagValue(command, 'd'));
      display.newLine();

        /* Exit program */
        switch(command) {
          case "exit": //Exit program
            exit = true;
            System.out.println("Exiting");
            break;
          case "statistics":
            session.displayGeneralStatistics();
          case "reset": //Reset statistics
            session.resetStatistics();
            break;
          case "help": //Help menu
            display.help();
            break;
          case "flags": //Available command flags
            display.flags();
            break;
          case "frequency": //Frequency statistics
            break;
          default:
            display.spinInfo(chip, placement, spins);

            if (chip != -1 && !placement.equals("-1")) { //Valid spin
              int odds = analyser.getWinOdds(placement);
              int realSpins = spins;
              display.displayOdds(odds);

              /* Simulations */
              for (int sim = 0; sim < simulations; sim++) {
                /* Spins */
                for (int roll = 0; roll < spins; roll++) {
                  /* Stop spinning if out of money */
                  if (!session.subBank(chip)) {
                    realSpins = roll; //Record spins taken
                    break;
                  }

                  int number = numberGen.generateNumber(); //Generate number

                  /* Display number, black/red, odd/even */
                  display.displayNumber(numberProp, chip, number, roll);

                  /* Check for win or loss */
                  if (resultProcessor.processResult(chip, placement, odds, number, roll, session, numberProp)) {
                    display.displayWin(chip, odds);
                    lose = false;
                  } else {
                    display.displayLoss(chip);
                    lose = true;
                  }

                  /* Strategies */
                  if (strat == 'd') {
                    chip = strategy.doubleEachTime(chip, initialChip, lose);
                  }

                  display.displayBank(session);
                  display.displaySpinDivider();
                }
                session.displayStatistics(realSpins, sim, odds);

                /* Update simulation data */
                if (simulations > 1) {
                  session.handleSimulations(sim, simulations, realSpins);
                  session.resetStatistics();
                  chip = initialChip;
                }
              }

            } else {
              System.out.println("Command invalid.");
            }
        }
    } while (!exit);
  }
}
