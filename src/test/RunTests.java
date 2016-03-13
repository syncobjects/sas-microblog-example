import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	microblog.services.MainTest.class,
	microblog.controllers.MainTest.class
})
public class RunTests {
}
