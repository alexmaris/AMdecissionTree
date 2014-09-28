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
	 * Classify the supplied DataInput by recursively traversing the provided
	 * TreeBranch
	 */
	
	public String Classify(TreeBranch tree, DataInput input) {

		String classification = null;

		int attribute = Arrays.asList(attributes).indexOf(tree.BestAttribute);

		for (TreeBranch branch : tree.Children) {
			if (input.Attributes[attribute] == branch.AttributeValue) {
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
