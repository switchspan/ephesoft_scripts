import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Date;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

// RabbitMQ libraries - the actual jar file needs to be placed in C:\Ephesoft\Application\WEB-INF\lib
import com.rabbitmq.client.*;
// Google gson library
import com.google.gson.Gson;

import com.ephesoft.dcma.script.IJDomScript;


public class ScriptExport implements IJDomScript {

	private static final String BATCH_LOCAL_PATH = "BatchLocalPath";
	private static final String BATCH_INSTANCE_ID = "BatchInstanceIdentifier";
	private static final String EXT_BATCH_XML_FILE = "_batch.xml";
	private static String ZIP_FILE_EXT = ".zip";
	private static String AMQP_EXCHANGE = "TestExchange";
	private static String AMQP_QUEUE = "TestQueue";
	private static String AMQP_ROUTING_KEY = "#";
	private static String AMQP_USERNAME = "guest";
	private static String AMQP_PASSWORD = "guest";
	private static String AMQP_HOST = "RabbitMqServer.NetworkName.local";
	private static String AMQP_VIRTUAL_HOST = "/";
	private static int AMQP_TCP_PORT = 5672;
	
	
	public Object execute(Document document, String methodName, String docIdentifier) {
		Exception exception = null;
		try {
			System.out.println("*************  Inside ExportScript scripts.");

			System.out.println("*************  Start execution of the ExportScript scripts.");

			if (null == document) {
				System.out.println("Input document is null.");
				return null;
			}
			
			System.out.println("*************  We have a valid document.");
			
			// Publish the AMQP message to the RabbitMQ server
			publishAmqpMessage(document);
			
			System.out.println("*************  End execution of the ScriptExport scripts.");
		} catch (Exception e) {
			System.out.println("*************  Error occurred in scripts." + e.getMessage());
			exception = e;
		}
		return null;
	}

	/**
	 * The <code>publishAmqpMessage</code> method will publish a message to a configured RabbitMQ/AMQP server.
	 * 
	 * @param document {@link Document}.
	 */
	private void publishAmqpMessage(Document document) {
		Exception exception = null;
		try {
			String batchInstanceID = null;
			List<?> batchInstanceIDList = document.getRootElement().getChildren(BATCH_INSTANCE_ID);
			if (null != batchInstanceIDList) {
				batchInstanceID = ((Element) batchInstanceIDList.get(0)).getText();
			}

			if (null == batchInstanceID) {
				System.err.println("Unable to find the batch instance ID in batch xml file.");
				return;
			}
			
			// Create a message to send via AMQP
			Map<String, String> messageMap = new HashMap<String, String>();
			Date dateNow = new Date();
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			messageMap.put("DocumentIdentifier", batchInstanceID);
			messageMap.put("DateReceived", df.format(dateNow));
			Gson gson = new Gson();
			String message = gson.toJson(messageMap);
			
			System.out.println(batchInstanceID);
			System.out.println("Message to send: ");
			System.out.println(message);
			
			byte[] messageBodyBytes = message.getBytes();
			
			// We have a document, so open a connection to the RabbitMQ server
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(AMQP_USERNAME);
			factory.setPassword(AMQP_PASSWORD);
			factory.setVirtualHost(AMQP_VIRTUAL_HOST);
			factory.setHost(AMQP_HOST);
			factory.setPort(AMQP_TCP_PORT);
			
			Connection conn = factory.newConnection();
			// Open a channel
			Channel channel = conn.createChannel();
			
			System.out.println("Channel open, sending message...");
			
			// Create a correclation id for the message
			String corrId = java.util.UUID.randomUUID().toString();

			// Send a message with the builder class
			channel.basicPublish(AMQP_EXCHANGE, AMQP_ROUTING_KEY,
								new AMQP.BasicProperties.Builder()
								.correlationId(corrId)
								.type(AMQP_QUEUE)
								.contentType("application/json")
								.deliveryMode(2)
								.priority(0)
								.build(),
								messageBodyBytes);
			
			// Close the connection
			channel.close();
			conn.close();

			System.out.println("Message sent.");
			
		} catch (Exception e) {
			System.out.println("*************  Error occurred in sending AMQP Message." + e.getMessage());
			exception = e;
		}
	}
	
	
	/**
	 * The <code>writeToXML</code> method will write the state document to the XML file.
	 * 
	 * @param document {@link Document}.
	 */
	private void writeToXML(Document document) {
		String batchLocalPath = null;
		List<?> batchLocalPathList = document.getRootElement().getChildren(BATCH_LOCAL_PATH);
		if (null != batchLocalPathList) {
			batchLocalPath = ((Element) batchLocalPathList.get(0)).getText();
		}

		if (null == batchLocalPath) {
			System.err.println("Unable to find the local folder path in batch xml file.");
			return;
		}

		String batchInstanceID = null;
		List<?> batchInstanceIDList = document.getRootElement().getChildren(BATCH_INSTANCE_ID);
		if (null != batchInstanceIDList) {
			batchInstanceID = ((Element) batchInstanceIDList.get(0)).getText();

		}

		if (null == batchInstanceID) {
			System.err.println("Unable to find the batch instance ID in batch xml file.");
			return;
		}

		String batchXMLPath = batchLocalPath.trim() + File.separator + batchInstanceID + File.separator + batchInstanceID
				+ EXT_BATCH_XML_FILE;

		String batchXMLZipPath = batchXMLPath + ZIP_FILE_EXT;

		System.out.println("batchXMLZipPath************" + batchXMLZipPath);

		OutputStream outputStream = null;
		File zipFile = new File(batchXMLZipPath);
		FileWriter writer = null;
		XMLOutputter out = new XMLOutputter();
		try {
			if (zipFile.exists()) {
				System.out.println("Found the batch xml zip file.");
				outputStream = getOutputStreamFromZip(batchXMLPath, batchInstanceID + EXT_BATCH_XML_FILE);
				out.output(document, outputStream);
			} else {
				writer = new java.io.FileWriter(batchXMLPath);
				out.output(document, writer);
				writer.flush();
				writer.close();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static OutputStream getOutputStreamFromZip(final String zipName, final String fileName) throws FileNotFoundException,
			IOException {
		ZipOutputStream stream = null;
		stream = new ZipOutputStream(new FileOutputStream(new File(zipName + ZIP_FILE_EXT)));
		ZipEntry zipEntry = new ZipEntry(fileName);
		stream.putNextEntry(zipEntry);
		return stream;
	}
}
