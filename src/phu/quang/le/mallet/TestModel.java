package phu.quang.le.mallet;

import java.io.IOException;
import java.net.URISyntaxException;

import phu.quang.le.Utility.TopicModelUtility;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class TestModel {
	public static void main (String[] args) throws IOException, URISyntaxException,
			ClassNotFoundException {
		String text = "Fascinating Early Posts From Tech Founders Who Changed The World Larry Page asked a Java question when developing Google. Just like us.";
		System.out.println (text);
		InstanceList testing = new InstanceList (TopicModelUtility.createPipes ());
		testing.addThruPipe (new Instance (text, null, "test instance", null));
		ParallelTopicModel model = TopicModelUtility.getModel ();
		TopicInferencer inferencer = model.getInferencer ();
		//
		double[] testProbabilities = inferencer.getSampledDistribution (
				testing.get (0), 10, 1, 5);
		for (int i = 0 ; i < model.getNumTopics () ; i++) {
			if (testProbabilities[i] >= 0.1) {
				System.out.println (i + " " + testProbabilities[i]);
			}
		}
	}
}
