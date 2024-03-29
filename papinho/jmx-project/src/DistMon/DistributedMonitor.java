package DistMon;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import bootstrap.Main;

import sun.management.ManagementFactory;
import utils.CmdLineParser;
import dto.NodeInfo;
import dto.NodeInfoComposite;
import dto.NodeInfoLeaf;
import example.TextListener;

/**
 * This starts the node remotely, this class is used
 * 
 * @author jander
 * @see Main
 */
public class DistributedMonitor extends Thread implements MessageListener {

	private static final long TIME_THREAD=3000;
	
	//private String topicName = null;
	private Context jndiContext = null;
	private TopicConnectionFactory topicConnectionFactory = null;
	private TopicConnection topicConnection = null;
	private TopicSession topicSession = null;
	private Topic topic = null;
	private TopicSubscriber topicSubscriber = null;
	private TopicPublisher topicPublisher = null;
	//private TextListener topicListener = null;
	private ObjectMessage message = null;
	//private InputStreamReader inputStreamReader = null;
	//private char answer = '\0';
	private Context context = null;
	private String name;
	private String parent;
	private List<String> children;
	private NodeInfoComposite info;
	private boolean isActive = true;

	/**
	 * Contructor for the list of remote nodes
	 * 
	 * @param name
	 *            Name of the current node
	 * @param parentName
	 *            of the parent for this current node
	 * @param children
	 *            List of the children
	 */
	public DistributedMonitor(String name, String parent, List<String> children) {
		this.name = name;
		this.parent = parent;
		this.children = children;
		info = new NodeInfoComposite();
		System.out.print("Creating node " + name + " parent: " + parent
				+ " children:");
		if (children != null) {
			for (String line : children) {
				System.out.print(line + ",");
			}
		}

		initConnection(name);

		System.out.println("post init");

		if (children != null) {
			for (String childTopic : children) {
				subscribe(childTopic, this);
			}
		}

	}

	private NodeInfo getNodeInfo() {
		Random r = new Random();

		long mem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
				.getInit();

		double cpuLoad = ManagementFactory.getOperatingSystemMXBean()
				.getSystemLoadAverage();

		NodeInfoLeaf fakeinfo = new NodeInfoLeaf(this.name, mem, cpuLoad);

		return fakeinfo;
	}

	/**
	 * Sends aggregated information from the children and current node for the parent node.
	 */
	private void sendinfo() {
		if (children != null && children.size() == 0) {
			dispatchForParent(getNodeInfo());
		} else {
			info.addNodeInfo(getNodeInfo());
			dispatchForParent(info);
			info.clear();
		}

	}

	/**
	 * Starts JMS connection 
	 * @param name of the parent topic in which this children should be subscribed
	 */
	private void initConnection(String topicName) {

		Hashtable properties = new Hashtable();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.exolab.jms.jndi.InitialContextFactory");
		properties.put(Context.PROVIDER_URL, "rmi://localhost:1099/");

		try {
			context = new InitialContext(properties);
		} catch (Exception e) {
			System.out.println("Initial context error");

		}

		/*
		 * Create a JNDI API InitialContext object if none exists yet.
		 */
		try {
			jndiContext = new InitialContext();
		} catch (NamingException e) {
			System.out.println("Could not create JNDI API " + "context: "
					+ e.toString());
			e.printStackTrace();
			System.exit(1);
		}

		/*
		 * Look up connection factory and topic. If either does not exist, exit.
		 */
		try {
			topicConnectionFactory = (TopicConnectionFactory) context
					.lookup("JmsTopicConnectionFactory");

			if (topicName != null && !topicName.equals("null")) {
				topic = (Topic) context.lookup(topicName);
			}

		} catch (NamingException e) {
			System.out.println("JNDI API lookup failed: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}

	}

	private void subscribe(String topicName, MessageListener callback) {

		System.out.println(name + " is subscribing to " + topicName);

		try {
			Topic topicChildren = (Topic) context.lookup(topicName);
			topicConnection = topicConnectionFactory.createTopicConnection();
			topicSession = topicConnection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			topicSubscriber = topicSession.createSubscriber(topicChildren);
			// topicListener = new example.TextListener();
			topicSubscriber.setMessageListener(callback);
			topicConnection.start();

		} catch (JMSException e) {
			System.out.println("Exception occurred: " + e.toString());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// finally {
		// if (topicConnection != null) {
		// try {
		// topicConnection.close();
		// } catch (JMSException e) {
		// }
		// }
		// }

	}

	/**
	 * Sends the agregated node information for the parent of the current node
	 * 
	 * @param agregated
	 *            node information
	 */
	private void dispatchForParent(NodeInfo ni) {
		System.out.println(name + " memory=" + ni.getMemory());
		// If does not have a parent, do not dispatch
		if (this.parent == null || this.parent.equals("null")) {
			System.out
					.println(this.name + "-No parent, suspending dispatching");
			return;
		}

		System.out.println(this.name + " is dispatching info");

		try {
			topicConnection = topicConnectionFactory.createTopicConnection();
			topicSession = topicConnection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			topicPublisher = topicSession.createPublisher(topic);
			// message = topicSession.createTextMessage();
			message = topicSession.createObjectMessage();
			message.setObject((Serializable) ni);
			// message.setText(strmessage);
			// System.out.println("Publishing message: " +message.getText());
			topicPublisher.publish(message);
			// topicPublisher.publish(topicSession.createMessage());

		} catch (JMSException e) {
			System.out.println("Exception occurred: " + e.toString());
		} finally {
			// if (topicConnection != null) {
			// try {
			// topicConnection.close();
			// } catch (JMSException e) {
			// }
			// }
		}

	}

	/**
	 * Print the usage of this class
	 */
	private static void printUsage() {
		System.out
				.println("Usage: distmon -n Name -p ParentName -c Child1 -c Child2");
	}

	public static void main(String... args) {

		CmdLineParser parser = new CmdLineParser();

		CmdLineParser.Option nameArg = parser.addStringOption('n', "name");
		CmdLineParser.Option parentArg = parser.addStringOption('p', "parent");
		CmdLineParser.Option childrenArg = parser.addStringOption('c',
				"children");

		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}

		Vector<String> childrenList = parser.getOptionValues(childrenArg);
		String nameString = (String) parser.getOptionValue(nameArg, "null");
		String parentString = (String) parser.getOptionValue(parentArg, "null");

		DistributedMonitor monitor = new DistributedMonitor(nameString,
				parentString, childrenList.subList(0, childrenList.size()));

		System.out.println("To end program, enter Q or q, " + "then <return>");
		char answer = 's';
		monitor.start();
		InputStreamReader inputStreamReader = new InputStreamReader(System.in);
		while (!((answer == 'q') || (answer == 'Q'))) {
			try {
				answer = (char) inputStreamReader.read();
			} catch (IOException e) {
				System.out.println("I/O exception: " + e.toString());
			}
		}
		monitor.setActive(false);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Establishes a period for sending the agregated node information for the parent of the current node
	 */
	@Override
	public void run() {
		System.out.println("Starting Monitor..");
		while (isActive) {
			sendinfo();
			try {
				Thread.sleep(TIME_THREAD);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Finishing monitor...");
	}

	/**
	 * Aggregates of the informations received by child nodes; 
	 */
	@Override
	public void onMessage(Message msg) {
		// TODO Auto-generated method stub
		ObjectMessage om = (ObjectMessage) msg;
		try {
			NodeInfo childInfo = (NodeInfo) om.getObject();
			info.addNodeInfo(childInfo);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}