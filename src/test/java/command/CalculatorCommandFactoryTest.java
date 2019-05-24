package command;

import calculator.CalculatorCommandFactory;
import calculator.commands.ExampleCommand;
import org.junit.Assert;
import org.junit.Test;

public class CalculatorCommandFactoryTest {

    @Test
    public void testExampleCommand() {
        CalculatorCommandFactory calculatorCommandFactory = CalculatorCommandFactory.init();
        calculatorCommandFactory.addCommand("Example Command", new ExampleCommand());
        double score = calculatorCommandFactory.executeAllCommands();
        Assert.assertEquals(score,0,0.01);
    }

}
