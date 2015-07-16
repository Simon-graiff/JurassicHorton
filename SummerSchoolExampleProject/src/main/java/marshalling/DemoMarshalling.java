package marshalling;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import model.ERPData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;


/**
 * Demo class marshalling XML to Java Object and vice versa
 * @author julian
 *
 */
public class DemoMarshalling {

	private Logger _log = LogManager.getLogger(DemoMarshalling.class); 
	
	private JAXBContext ctx; 
	
	private Marshaller _marshaller ; 
	
	private Unmarshaller _unmarshaller; 
	
	
	public DemoMarshalling() {
		try {
			ctx = JAXBContext.newInstance(ERPData.class);
			_marshaller = ctx.createMarshaller();
			_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			_unmarshaller = ctx.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		} 
	}
	
	public void run() {
		_log.debug("Creating a java object");
		//Create a java object 
		ERPData erpData = new ERPData(); 
		erpData.setCustomerNumber(4711);
		erpData.setMaterialNumber(42);
		erpData.setOrderNumber("123 ABC 456");
		erpData.setTimeStamp(new Date());
		
		
		//generate XML
		StringWriter sWriter = new StringWriter(); 
		String xmlString = null; 
		try {
			_log.debug("marshalling java object to XML");
			_marshaller.marshal(erpData, sWriter);
			sWriter.flush();
			xmlString = sWriter.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		_log.debug(xmlString);
		
		_log.debug("Creating a java object from XML string");
		
		StringReader sReader = new StringReader(xmlString); 
		try {
			ERPData erpData2 = (ERPData) _unmarshaller.unmarshal(sReader);
			_log.debug("Object created: " + erpData2.toString());
			
		
		} catch (JAXBException e) {
			e.printStackTrace();
		} 
		
		
		
		_log.debug("Marshalling object to JSON:");
		Gson gson = new Gson();
		
		String jsonString = gson.toJson(erpData);
		_log.debug(jsonString);
		
		_log.debug("Unmarshalling from JSON ...");
		
		ERPData tmpData = gson.fromJson(jsonString, ERPData.class);
		_log.debug("Unmarshalling from JSON done: " + tmpData.toString());
		
	}
}
