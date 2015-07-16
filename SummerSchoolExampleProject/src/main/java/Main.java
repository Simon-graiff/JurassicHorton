import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import marshalling.DemoMarshalling;
import model.ERPData;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.impl.DefaultCamelContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import esper.EsperTest;
import activemq.QueueConnection;
import activemq.QueueConnectionUsingCamel;
import stateless4j.TestMachine;


/**
 * Just comment out the code pieces you want.
 * @author julian
 *
 */
public class Main {

	Logger _log = 	LogManager.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		try {
			Process p =  Runtime.getRuntime().exec("cmd /c executeSim.bat", null, new File("C:\\Users\\Christian\\Documents\\GitHub\\JurassicHorton"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//JAXB Marshalling / Unmarshalling 
		DemoMarshalling demo = new DemoMarshalling(); 
		demo.run();
		
		//Message Queue Connection using JMS 
		QueueConnection q = new QueueConnection();
		
		//Message Queue Connection using JMS via Apache Camel 
		QueueConnectionUsingCamel qc = new QueueConnectionUsingCamel(); 
		qc.run();
		
		// State Machine 
		TestMachine t = new TestMachine(); 
		t.run(); 
	
		//Esper test 
		EsperTest esperTest = new EsperTest(); 
		esperTest.run();
	}
}
