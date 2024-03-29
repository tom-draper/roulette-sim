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

    display.enterBank(); //User prompt
    Scanner scanner = new Scanner(System.in);
    Session session = new Session(scanner.nextDouble()); //Get bank value

    /* User command help */
    display.possibleChips();
    display.commandFormat();

    do {
      scanner = new Scanner(System.in);
      String command = scanner.nextLine(); //Get command

      double initialChip = analyser.getChip(command);
      double chip = initialChip;
      String placement = analyser.getPlacement(command);
      int spins = analyser.getFlagValue(command, 's');
      int simulations = analyser.getFlagValue(command, 'l');
      char strat = analyser.getStrategy(command, spins);

      /* If input spins or simulations set to -1 (missing), default to 1 */
      if (spins == -1) {
        spins = 1;
      }
      if (simulations == -1) {
        simulations = 1;
        session.setSimulating(false);
      } else {
        session.resetSim();
        session.setSimulating(true);
      }

      display.displayStrategy(strat);

      /* Double strategy set up*/
      if (strat == 'd') {
        /* Search for option max value */
        strategy.setDoubleStratMax(analyser.getFlagValue(command, 'd'));
      }
      display.newLine();

        /* Exit program */
        switch(command) {
          case "exit": //Exit program
            exit = true;
            display.exiting();
            break;
          case "statistics":
            display.displayGeneralStatistics(session);
          case "reset": //Reset statistics
            session.resetStatistics(display);
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

            if (chip != -1 && !placement.equals("-1")) { //Valid spin
              int realSpins = spins;
              int odds = analyser.getWinOdds(placement);

              display.spinInfo(chip, placement, spins, odds);


              /* Simulations */
              for (int sim = 0; sim < simulations; sim++) {
                /* Spins */
                for (int roll = 0; roll < spins; roll++) {
                  /* Stop spinning if out of money */
                  if (!session.subBank(chip)) {
                    realSpins = roll; //Record spins taken
                    break;
                  }

                  int number = numberGen.generateEuropeanNumber(); //Generate number

                  if (spins > 1) {
                    display.displayRoll(roll);
                  }

                  /* Display number, black/red, odd/even */
                  display.displayNumber(numberProp, number);

                  display.displayChip(chip);

                  /* Check for win or loss */
                  if (resultProcessor.processResult(chip, placement, odds, number, roll, session, numberProp, analyser)) {
                    display.displayWin(chip, odds);
                    lose = false;
                  } else {
                    display.displayLoss(chip);
                    lose = true;
                  }

                  /* Strategies */
                  if (strat == 'd') { //Double strategy
                    chip = strategy.doubleEachTime(chip, initialChip, lose);
                  }

                  display.displayBank(session);
                  display.displaySmallDivider();
                }
                display.displayStatistics(realSpins, sim, odds, session);


                /* Update simulation data */
                if (simulations > 1) {
                  session.handleSimulations(sim, simulations, realSpins);
                  session.resetStatistics(display);
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
