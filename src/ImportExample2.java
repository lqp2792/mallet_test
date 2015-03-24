import java.util.ArrayList;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.StringList2FeatureSequence;
import cc.mallet.types.Instance;

public class ImportExample2 {

	protected int a = 10;
	static Pipe pipe = null;

	public static void main (String args[]) {
		ArrayList<String> ob1 = new ArrayList<String> ();
		ob1.add ("on the plains of africa the lions roar");
		ob1.add ("in swahili ngoma means to dance");
		Instance ob2 = new Instance (ob1, null, "array-1", null);
		StringList2FeatureSequence ob3 = new StringList2FeatureSequence ();
		Instance i = ob3.pipe (ob2);
		PrintInputAndTarget piat = new PrintInputAndTarget ();
		Instance i5 = piat.pipe (i);
	}
}