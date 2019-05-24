package command;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        CalculatorCommandFactoryTest.class,
        SimilarWordOrderedCommandTest.class
})
public class CommandTestSuite {

}
