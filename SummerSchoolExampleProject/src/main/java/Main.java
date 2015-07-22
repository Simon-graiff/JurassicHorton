import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import esper.EsperTest;
import activemq.QueueConnection;
import stateless4j.TestMachine;
import ui.Server;


/**
 * Just comment out the code pieces you want.
 * @author julian
 *
 */
public class Main {

	Logger _log = 	LogManager.getLogger(Main.class);
		
	public static void main(String[] args) {
		Server.getInstance().start();
		System.out.println("Server is running");
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec("CMD /C start executeSim.bat");
			System.out.println("Waiting for Simulation to be started for 1 second");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//JAXB Marshalling / Unmarshalling 
//		DemoMarshalling demo = new DemoMarshalling(); 
//		demo.run();
		
		//Message Queue Connection using JMS 
		QueueConnection q = new QueueConnection();
		
		// State Machine 
//		TestMachine t = new TestMachine(); 
//		t.run(); 
	
		//Esper test 
//		EsperTest esperTest = new EsperTest(); 
//		esperTest.run();
	}
}
