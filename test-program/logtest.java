import org.apache.log4j.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
public static class logtest
{
//	static Logger logger = Logger.getLogger(logtest.class.getName());
	public static void main(String[] args) 
	{
		String path="/opt/hadoop/conf/log4j.properties";
		PropertyConfigurator.configure(path);
		Logger logger = Logger.getLogger(logtest.class);
		logger.info("Entering application.");
		logger.info("Exiting application");
	}
}
