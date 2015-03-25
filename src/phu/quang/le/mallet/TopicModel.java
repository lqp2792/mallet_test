package phu.quang.le.mallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.regex.Pattern;

import phu.quang.le.Utility.TopicModelUtility;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;

public class TopicModel {
	public static void main (String[] args) throws Exception {
		InstanceList instances = new InstanceList (TopicModelUtility.createPipes ());
		Reader fileReader = new InputStreamReader (new FileInputStream (new File (
				args[0])), "UTF-8");
		instances.addThruPipe (new CsvIterator (fileReader, Pattern
				.compile ("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1));
		ParallelTopicModel model = TopicModelUtility.createModel (instances, 2500,
				2000);
		ObjectOutputStream output = new ObjectOutputStream (new FileOutputStream (
				"TopicModel1.lda"));
		output.writeObject (model);
		output.close ();
		output = new ObjectOutputStream (new FileOutputStream ("Instances1.lda"));
		output.writeObject (instances);
		output.close ();
	}
}