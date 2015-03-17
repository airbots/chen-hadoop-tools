 import org.apache.log4j.Logger;

 /**
04  * User: Diegoyun
05  * Date: 2006-3-4
06  * Time: 23:21:17
07  */
 public class Log4jTest {
     public static void main(String[] args) {
         String path="/opt/hadoop/conf/log4j.properties";
         org.apache.log4j.PropertyConfigurator.configure(path);
         Logger logger = Logger.getLogger(Log4jTest.class);
         logger.debug("a test.");
     }
 }  
