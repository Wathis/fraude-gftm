package command;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        CalculatorCommandFactoryTest.class,
        SimilarCodeCommandTest.class,
        OrdonateWordCommandTest2.class
})
public class CommandTestSuite {

}
