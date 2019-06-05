package filter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        KeywordFilterTest.class,
        ModelSuppressionFilterTest.class,
        ImportationSuppressionFilterTest.class
})
public class FilterTestSuite {



}
