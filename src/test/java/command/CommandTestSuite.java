package command;

import calculator.commands.CodingHabitsCommand;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        CalculatorCommandFactoryTest.class,
        SimilarCodeCommandTest.class,
        CodingHabitsCommandTest.class,
})
public class CommandTestSuite {

}
