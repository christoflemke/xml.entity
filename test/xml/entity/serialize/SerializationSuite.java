package xml.entity.serialize;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
               IdentityTest.class,
               TestParser.class,
               TestSerializer.class,
               TestXsiContext.class
})
public class SerializationSuite
{

}
