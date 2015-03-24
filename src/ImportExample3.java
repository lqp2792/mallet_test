import java.util.ArrayList;

import cc.mallet.pipe.Array2FeatureVector;
import cc.mallet.pipe.Csv2Array;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.types.InstanceList;

public class ImportExample3 {

	static Pipe pipe = null;
	static InstanceList instancelist = null;

	public Pipe buidPipe () {
		ArrayList pipeList = new ArrayList ();
		pipeList.add (new Csv2Array ());
		pipeList.add (new Target2Label ());
		pipeList.add (new Array2FeatureVector ());
		pipeList.add (new PrintInputAndTarget ());
		return (new SerialPipes (pipeList));
	}

	public static void main (String args[]) {
		String[][][] trainingdata = new String[][][] {
				{ { "1,0,1,1,1,1", "1,1,1,1,1,1", "1,1,1,1,1,1", "1,1,1,1,1,1" },
						{ "data_bmp" } },
				{ { "0,0,0,0,0,0", "0,0,0,0,0,0", "0,0,0,0,0,0" }, { "data_jpeg" } },
				{ { "1,1,1,1,1,1", "1,1,1,1,1,0", "1,1,1,0,1,0" }, { "data_gif" } } };
		ImportExample3 ob1 = new ImportExample3 ();
		pipe = ob1.buidPipe ();
		InstanceList instances = new InstanceList (pipe);
		for (int i = 0; i < 3; i++) {
			try {
				instances.addThruPipe (new ArrayIterator (trainingdata[i][0],
						trainingdata[i][1][0]));
			} catch (Exception e) {
				System.out.println (e);
			}
		}
	}
}