package bda.decissionTree;

import java.util.Arrays;

public class Classifier {

	public Classifier(String[] attributes) {
		this.attributes = attributes;
	}

	String[] attributes;

	/**
	 * @tree TreeBranch that the data input will be evaluated against
	 * @input The unclassified data input
	 * @return Computed classification of the data input
	 * 
	 *         Classify the supplied DataInput by recursively traversing the
	 *         provided TreeBranch
	 */

	public String Classify(TreeBranch tree, DataInput input) {

		String classification = null;

		if (tree.Classification != null) {
			return tree.Classification;
		}

		int attribute = Arrays.asList(attributes).indexOf(
				tree.BestAttribute.trim());

		// Loop through all the tree branches to see if the input attribute
		// matches
		// the value of the branch
		if (input.Attributes[attribute] <= tree.AttributeValue) {
			classification = Classify(tree.leftNode, input);
		} else {
			classification = Classify(tree.rightNode, input);
		}

		return classification;
	}
}
