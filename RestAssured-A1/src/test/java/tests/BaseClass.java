package tests;

import java.util.Properties;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import Utils.ConfigRead;
import io.restassured.RestAssured;

public class BaseClass {

	String configFilename;
	Properties configProperties;
    String currentWorkingDirectory;
    
	@BeforeSuite
	public void preSetup() throws Exception{
		
		currentWorkingDirectory =System.getProperty("user.dir");
		configFilename =currentWorkingDirectory + "/src/test/resources/config/config.properties";
		configProperties=ConfigRead.readConfigProperties(configFilename);
	}

	@BeforeClass
	public void setUp() {
		
		RestAssured.baseURI=configProperties.getProperty("baseUrl");
		RestAssured.port=Integer.parseInt(configProperties.getProperty("portNumber"));
		
		
	}
	@AfterClass
	public void cleanUp() {
		RestAssured.reset();
	}
}
