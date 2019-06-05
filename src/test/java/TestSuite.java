import command.CommandTestSuite;
import filter.FilterTestSuite;
import io.IOTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import parser.ParserTestSuite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        FilterTestSuite.class,
        CommandTestSuite.class,
        ParserTestSuite.class,
        IOTestSuite.class,
})
public class TestSuite {

}
