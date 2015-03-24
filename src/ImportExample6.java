import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PipeUtils;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.SimpleTaggerSentence2TokenSequence;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.types.InstanceList;

public class ImportExample6 {

	static Pipe pipe = null;
	static InstanceList instancelist = null;

	public static void main (String args[]) {
		String trainingdata[] = { "on the plains of africa the lions roar",
				"in swahili ngoma means to dance", "the saraha dessert saraha expanding" };
		Pipe p1 = new SimpleTaggerSentence2TokenSequence ();
		Pipe p2 = new PrintInputAndTarget ();
		pipe = PipeUtils.concatenatePipes (p1, p2);
		InstanceList instances = new InstanceList (pipe);
		try {
			instances.addThruPipe (new ArrayIterator (trainingdata));
		} catch (Exception e) {
			System.out.println (e);
		}
	}
}
