package bda.decissionTree;

import java.util.ArrayList;
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
	 *         Classify the supplied DataInput by recursively traversing the provided TreeBranch
	 */

	public String Classify(TreeBranch tree, DataInput input) {

		String classification = null;

		int attribute = Arrays.asList(attributes).indexOf(tree.BestAttribute.trim());

		// Loop through all the tree branches to see if the input attribute matches
		// the value of the branch
		for (TreeBranch branch : tree.Children) {
			if (input.Attributes[attribute] == branch.AttributeValue) {
				// If we found a leaf then this is our classification, otherwise follow the
				// branch further
				if (branch.isLeaf()) {
					return branch.Classification;
				} else {
					classification = Classify(branch, input);
				}
			}
		}

		return classification;
	}
}
