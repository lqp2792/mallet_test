package phu.quang.le.mallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.grmm.test.TestPottsFactor;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

public class TopicModelCustom {

	public static void main (String[] args) throws Exception {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe> ();
		pipeList.add (new CharSequenceLowercase ());
		pipeList.add (new CharSequence2TokenSequence (Pattern
				.compile ("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add (new TokenSequenceRemoveStopwords (new File ("stoplist/en.txt"),
				"UTF-8", false, false, false));
		pipeList.add (new TokenSequence2FeatureSequence ());
		InstanceList instances = new InstanceList (new SerialPipes (pipeList));
		Reader fileReader = new InputStreamReader (new FileInputStream (
				new File (args[0])), "UTF-8");
		instances.addThruPipe (new CsvIterator (fileReader, Pattern
				.compile ("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1));
		// data, label, name, fields
		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		// Note that the first parameter is passed as the sum over topics, while
		// the second is the parameter for a single dimension of the Dirichlet
		// prior.
		int numTopics = 500;
		ParallelTopicModel model = new ParallelTopicModel (numTopics, 5, 0.01);
		model.addInstances (instances);
		// Use two parallel samplers, which each look at one half the corpus and
		// combine
		// statistics after every iteration.
		model.setNumThreads (2);
		// Run the model for 50 iterations and stop (this is for testing only,
		// for real applications, use 1000 to 2000 iterations)
		model.setNumIterations (1000);
		model.estimate ();
		System.out.println ("==========================================");
		ObjectOutputStream output = new ObjectOutputStream (new FileOutputStream (
				"TopicModel.lda"));
		output.writeObject (model);
		output.close ();
		// Show the words and topics in the first instance
		// The data alphabet maps word IDs to strings
//		Alphabet dataAlphabet = instances.getDataAlphabet ();
		Alphabet dataAlphabet = model.getAlphabet ();
		System.out.println (dataAlphabet.size ());
		FeatureSequence tokens = (FeatureSequence) model.getData ().get (0).instance
				.getData ();
		LabelSequence topics = model.getData ().get (0).topicSequence;
		Formatter out = new Formatter (new StringBuilder (), Locale.US);
		for (int position = 0; position < tokens.getLength (); position++) {
			out.format ("%s-%d ",
					dataAlphabet.lookupObject (tokens.getIndexAtPosition (position)),
					topics.getIndexAtPosition (position));
		}
		System.out.println (out);
		System.out.println ("===========================================");
		// Estimate the topic distribution of the first instance,
		// given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities (0);
		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords ();
		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get (topic).iterator ();
			out = new Formatter (new StringBuilder (), Locale.US);
			out.format ("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			while (iterator.hasNext () && rank < 5) {
				IDSorter idCountPair = iterator.next ();
				out.format ("%s (%.0f) ",
						dataAlphabet.lookupObject (idCountPair.getID ()),
						idCountPair.getWeight ());
				rank++;
			}
			System.out.println (out);
		}
		// Create a new instance with high probability of topic 0
		String topicZeroText = "BBC - Earth - How long will life survive on planet Earth?  Life on Earth will surely be wiped out eventually. But how long does it have, and what will it take to sterilise the entire planet? BBC Earth Extinction story STORY The Big Questions";
		Iterator<IDSorter> iterator = topicSortedWords.get (0).iterator ();
		// int rank = 0;
		// while (iterator.hasNext () && rank < 5) {
		// IDSorter idCountPair = iterator.next ();
		// topicZeroText.append (dataAlphabet.lookupObject (idCountPair.getID
		// ()) + " ");
		// rank++;
		// }
		// Create a new instance named "test instance" with empty target and
		// source fields.
		InstanceList testing = new InstanceList (instances.getPipe ());
		System.out.println (topicZeroText);
		testing.addThruPipe (new Instance (topicZeroText, null, "test instance", null));
		/* System.out.println (testing.getAlphabet ().toString ()); */
		TopicInferencer inferencer = model.getInferencer ();
		output = new ObjectOutputStream (new FileOutputStream (
				"TopicInferencer.lda"));
		output.writeObject (inferencer);
		output.close ();
		System.out.println (testing.get (0).getData ().toString ());
		double[] testProbabilities = inferencer.getSampledDistribution (testing.get (0),
				10, 1, 5);
		for (int i = 0; i < 500; i++) {
			
			if (testProbabilities[i] >= 0.1) {
				System.out.println (i + " " + testProbabilities[i]);
			}
		}
		/*double[] testProbabilities = inferencer.getSampledDistribution (testing.get (0),
				100, 10, 50);
		for (int i = 0; i < 500; i++) {
			if (testProbabilities[i] >= 0.1) {
				Iterator<IDSorter> iterator1 = topicSortedWords.get (i).iterator ();
				System.out.println (i + " " + testProbabilities[i]);
				int rank1 = 0;
				while (iterator1.hasNext () && rank1 < 5) {
					IDSorter idCountPair1 = iterator.next ();
					System.out.print (dataAlphabet.lookupObject (idCountPair1.getID ())
							+ " ");
					rank1++;
				}
				System.out.println ();
			}
		}*/
		inferencer.writeInferredDistributions (testing, new File ("abc.txt"), 10, 1, 5,
				0.1, 5);
	}
}