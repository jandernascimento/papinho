package virtualtree;

import bootstrap.Bootstrap;

/**
 * Concrete visitor for the creation of the topics
 */
public class ChannelCreatorVisitor implements VirtualNodeVisitor {
	public void visit(VirtualNode node) {
		String topicName = node.getId() + "Topic";
		try{
			Bootstrap.adminServer.createTopic(topicName);
		} catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}
