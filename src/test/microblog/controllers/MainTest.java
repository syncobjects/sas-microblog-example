package microblog.controllers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	AuthInterceptorTest.class, 
	MainControllerTest.class,
	SignupControllerTest.class,
	PostControllerTest.class
})
public class MainTest {
}
