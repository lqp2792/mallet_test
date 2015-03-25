package phu.quang.le.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;

public class TopicModelUtility {
	public static SerialPipes createPipes () throws IOException, URISyntaxException {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe> ();
		pipeList.add (new CharSequenceLowercase ());
		pipeList.add (new CharSequence2TokenSequence (Pattern
				.compile ("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add (new TokenSequenceRemoveStopwords (
				new File ("stoplist/en.txt"), "UTF-8", false, false, false));
		pipeList.add (new TokenSequence2FeatureSequence ());
		//
		return new SerialPipes (pipeList);
	}

	public static ParallelTopicModel createModel (InstanceList instances,
			int numTopics, int numIterations) throws IOException {
		ParallelTopicModel model = new ParallelTopicModel (numTopics,
				0.01 * numTopics, 0.01);
		model.addInstances (instances);
		model.setNumThreads (2);
		model.setNumIterations (numIterations);
		model.estimate ();
		//
		return model;
	}

	public static ParallelTopicModel getModel () throws FileNotFoundException,
			IOException, ClassNotFoundException {
		ParallelTopicModel model = null;
		ObjectInputStream input = new ObjectInputStream (new FileInputStream (
				"TopicModel.lda"));
		model = (ParallelTopicModel) input.readObject ();
		//
		return model;
	}
}
