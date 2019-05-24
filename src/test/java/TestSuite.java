import command.CommandTestSuite;
import filter.FilterTestSuite;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        FilterTestSuite.class,
        CommandTestSuite.class,
})
public class TestSuite {

}
