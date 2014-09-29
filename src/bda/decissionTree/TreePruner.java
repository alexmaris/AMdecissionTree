package bda.decissionTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TreePruner {

	public static void main(String args[]) {
		DataLoader dl = new DataLoader();
		dl.loadIrisDataSet("iris.data");

		TreePruner t = new TreePruner();
		t.Ten_x_CrossValidation(dl.dataSet.values().toArray(new DataInput[0]));

	}

	/**
	 * 
	 * @param k is the number of classes
	 * @param N is the is the number of instances in the data set
	 * @param n out of N examples in data set belong to the class
	 * @return expected error
	 */
	public double expectedErrorEstimate(int k, int N, int n) {
		double error = (N - n + k - 1) / (N + k);

		return error;
	}

	public TreeBranch ReducedErrorPruner(TreeBranch tree) {

		for (TreeBranch branch : tree.Children) {
			if (!branch.isLeaf()) {
				ReducedErrorPruner(branch);
			}
			// Calculate how pruning the branch improves error accuracy on the test data
			// double treeError =

			// If pruning one of the branches increases the accuracy the prune the node
			// that increases the accuracy the most

		}

		return null;
	}

	public void Ten_x_CrossValidation(DataInput[] data) {
		// Split data into 10 parts
		int k = 10;
		ArrayList<DataInput[]> dataSplits = splitDataSet(Arrays.asList(data), k);
		ArrayList<TreeBranch> trees = new ArrayList<TreeBranch>();

		for (int i = 0; i < dataSplits.size(); i++) {
			System.out.println("Training classifier on " + (k - 1) + " folds, testing on fold " + (i + 1));
			

		}

	}

	protected ArrayList<DataInput[]> splitDataSet(List<DataInput> data, int k) {
		ArrayList<DataInput[]> dataSplits = new ArrayList<DataInput[]>();
		Collections.shuffle(data);

		int splitSize = data.size() / k;

		for (int i = 1; i < k + 1; i++) {
			ArrayList<DataInput> split = new ArrayList<DataInput>();
			for (int j = (i - 1) * splitSize; j < i * splitSize; j++) {
				split.add(data.get(j));
			}
			dataSplits.add(split.toArray(new DataInput[0]));
		}

		return dataSplits;
	}

}
