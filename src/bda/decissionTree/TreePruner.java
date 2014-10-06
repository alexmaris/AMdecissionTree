package bda.decissionTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TreePruner {

	private static final int RIGHT = 0;
	private static final int LEFT = 1;

	/**
	 * 
	 * @param k
	 *            is the number of classes
	 * @param N
	 *            is the is the number of instances in the data set
	 * @param n
	 *            out of N examples in data set belong to the class
	 * @return expected error
	 */
	public double expectedErrorEstimate(int k, int N, int n) {
		double error = (N - n + k - 1) / (N + k);

		return error;
	}

	public boolean ReducedErrorPruner(TreeBranch tree) {
		boolean ret = false;
		// Make sure we weren't passed an empty tree
		if (tree == null) {
			return ret;
		}

		// Prune the tree if it should be pruned
		if (shouldBePruned(tree)) {
			int branchToPrune = voteOnPrune(tree);

			// Set the classification of this leaf and remove its
			// branches/attribute
			// information
			if (branchToPrune == RIGHT) {
				tree.Classification = tree.rightNode.Classification;
			} else if (branchToPrune == LEFT) {
				tree.Classification = tree.leftNode.Classification;
			}

			tree.rightNode = null;
			tree.leftNode = null;
			tree.BestAttribute = null;

			return true;

		} else {

			// Prune the right side
			ret = ReducedErrorPruner(tree.rightNode);

			// Prune the left side
			ret = ret || ReducedErrorPruner(tree.leftNode);
		}

		return ret;

	}

	/**
	 * @param branch
	 *            TreeBranch that the check should be performed against
	 * @return True = yes prune, False = don't prune
	 * 
	 *         Check to see if the branch has should be pruned by looking at
	 *         it's children's branches
	 */
	protected boolean shouldBePruned(TreeBranch branch) {
		// Make sure we weren't passed a leaf
		if (branch != null) {
			if (branch.rightNode != null && branch.leftNode != null)
				// Check the right branch
				if (branch.rightNode.Classification != null) {
					// Check the left branch
					if (branch.leftNode.Classification != null) {
						return true;
					}
				}
		}
		return false;
	}

	/**
	 * Given a tree, identify which side (LEFT = 1, RIGHT = 0) to prune
	 */
	protected int voteOnPrune(TreeBranch branch) {
		if (branch.leftNode.Examples.length >= branch.rightNode.Examples.length) {
			return LEFT;
		} else {
			return RIGHT;
		}
	}

	/**
	 * Perform 10-fold cross validation on the input data
	 * 
	 * @param data
	 */
	public void Ten_x_CrossValidation(DataInput[] data, String[] attributeNames) {
		// Split data into 10 parts
		int k = 10;
		ArrayList<DataInput[]> dataSplits = (ArrayList<DataInput[]>) splitDataSet(
				Arrays.asList(data), k);

		HashMap<TreeBranch, DataInput[]> trees = new HashMap<TreeBranch, DataInput[]>();

		for (int i = 0; i < dataSplits.size(); i++) {

			// Gather the data for the training set and classification tests
			ArrayList<DataInput> tempData = new ArrayList<DataInput>();

			for (int j = 0; j < dataSplits.size(); j++) {
				if (j != i) {
					tempData.addAll(Arrays.asList(dataSplits.get(j)));
				}
			}

			DataInput[] trainigData = tempData.toArray(new DataInput[0]);
			tempData.clear();

			// Perform the training and pruning
			DecisionTreeID3 dt = new DecisionTreeID3();
			TreeBranch tree = dt.createTree(trainigData, attributeNames, null);

			// Prune while there are still branches that can be trimmed
			boolean prune = this.ReducedErrorPruner(tree);
			while (prune) {
				prune = this.ReducedErrorPruner(tree);
			}

			// Classify the data
			Classifier cls = new Classifier(attributeNames);
			for (DataInput d : dataSplits.get(i)) {
				d.GuessedClassification = cls.Classify(tree, d);
			}

			// Store the tree, and the data that it was validated against
			trees.put(tree, dataSplits.get(i));
		}

		voteAndPrintBestTree(trees);
	}

	/**
	 * Find the tree with the highest accuracy and print its confusion matrix
	 * 
	 * @param trees
	 *            Collection of Trees and Classified data
	 */
	protected void voteAndPrintBestTree(HashMap<TreeBranch, DataInput[]> trees) {
		TreeBranch bestTree = null;
		DataInput[] classifiedData = null;
		double accuracy = 0;
		double avgAccuracy = 0;
		int itr = 0, chosen = 0;

		for (TreeBranch tree : trees.keySet()) {
			double temp = calculateAccuracy(trees.get(tree));
			if (accuracy < temp) {
				accuracy = temp;
				bestTree = tree;
				classifiedData = trees.get(tree);
				chosen = itr;
			}
			avgAccuracy += temp;
			itr++;
		}

		//DecisionTreeID3.printTree(bestTree, 1);

		String[] uniqueClassifications = getUniqueClassifications(classifiedData);
		printConfusionMatrix(classifiedData, uniqueClassifications);
		System.out.format("\nAverage accuracy: %s ", (avgAccuracy/trees.size()));
		System.out.format("\nMost accurate tree was produced in itteration # %s ", chosen);
	}

	/**
	 * Split a List of DataInput types into even chunks of the specified size
	 * 
	 * @param data
	 *            List of DataInputs
	 * @param k
	 *            split size
	 * @return List of DataInput arrays of size k
	 */
	protected List<DataInput[]> splitDataSet(List<DataInput> data, int k) {
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

	/**
	 * Calculate the accuracy of the DataInput array (inputs should be run
	 * through a classifier first) as 1 - (Incorrect Classifications - Total
	 * Classifications)
	 * 
	 * @param dataSet
	 *            Array of DataInput[]
	 * @return Classification accuracy
	 */
	protected double calculateAccuracy(DataInput[] dataSet) {
		int totalCount = 0, incorrectlyClassified = 0;

		// Loop through all the possible classifications and
		// identify if the classification was correct or incorrect
		for (DataInput d : dataSet) {
			if (!d.Classification.trim().equals(d.GuessedClassification.trim())) {
				incorrectlyClassified++;
			}
			totalCount++;
		}

		return (1 - ((double) incorrectlyClassified / totalCount));
	}

	/**
	 * Print a confusion matrix
	 * 
	 * @param dataSet
	 *            Array of DataInput[] that were run through a classifier
	 * @param classificationNames
	 *            All possible (unique) data classes
	 */
	protected void printConfusionMatrix(DataInput[] dataSet,
			String[] classificationNames) {
		System.out.format("\n%55s\n", "Predicted Class");
		System.out.format("%33s | %15s | %15s | \n", classificationNames);
		printLineBreak(70);

		for (String classificationName : classificationNames) {
			// Create a matrix that will hold all of the
			HashMap<String, Integer> matrix = new HashMap<String, Integer>();
			for (String a : classificationNames) {
				matrix.put(a, 0);
			}

			for (DataInput d : dataSet) {
				if (d.Classification.trim().equals(classificationName.trim())) {
					matrix.put(d.GuessedClassification,
							matrix.get(d.GuessedClassification) + 1);
				}
			}

			System.out.format("%15s |", classificationName);
			for (String s : matrix.keySet()) {
				System.out.format(" %15s |", matrix.get(s));
			}
			System.out.print("\n");
		}
		printLineBreak(70);
		System.out.print("\n");
	}

	/**
	 * Get the unique classifications available in this dataset
	 * 
	 * @param dataSet
	 *            Array of DataInput values
	 * @return Array of unique classification
	 */
	protected String[] getUniqueClassifications(DataInput[] dataSet) {
		HashSet<String> uniqueValues = new HashSet<String>();

		for (DataInput d : dataSet) {
			uniqueValues.add(d.Classification);
		}

		return uniqueValues.toArray(new String[0]);
	}

	protected void printLineBreak(int charCount) {
		System.out.println(new String(new char[charCount]).replace("\0", "-"));
	}
}
