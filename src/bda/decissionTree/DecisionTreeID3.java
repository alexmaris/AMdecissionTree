package bda.decissionTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

public class DecisionTreeID3 {

	String[] attributes;
	int attributeCount;

	public HashMap<Integer, DataInput> dataSet;

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Please pass in the location of the data file (csv).");

			return;
		}

		DecisionTreeID3 dt = new DecisionTreeID3();
		int ret = dt.loadData(args[0]);

		// TODO: this is already in the data file...read it from there
		ArrayList<Integer> attributeSet = new ArrayList<Integer>();
		String[] attributes = new String[] { "sepal-length", "sepal-width", "petal-length", "petal-width" };
		for (int i = 0; i < attributes.length; i++) {
			attributeSet.add(i);
		}

		// TODO: this is a copy of the data in the HashMap...change that prop
		DataInput[] dataTest = dt.dataSet.values().toArray(new DataInput[dt.dataSet.size()]);

		TreeBranch branch = dt.createTree(dataTest, attributeSet, attributes, null);

		System.out.println("Decission Tree - using ID3:");
		DecisionTreeID3.printTree(branch, 1);

		if (ret == 0) {
			System.out.println("Finished loading data file with " + dt.dataSet.size() + " records.");
		}

		Classifier classifier = new Classifier(attributes);
		DataInput d = new DataInput(new double[] { 4.6, 3.4, 1.4, 0.3 }, "");
		DataInput d2 = new DataInput(new double[] { 6.3, 2.8, 5.1, 1.5 }, "");
		DataInput d3 = new DataInput(new double[] { 6.1, 3.0, 4.9, 1.8 }, "");

		System.out.println("Classifying (4.6,3.4,1.4,0.3,Iris-setosa) :" + classifier.Classify(branch, d));
		System.out
				.println("Classifying (6.3,2.8,5.1,1.5,Iris-virginica) :" + classifier.Classify(branch, d2));
		System.out
				.println("Classifying (6.1,3.0,4.9,1.8,Iris-virginica) :" + classifier.Classify(branch, d3));

		// Uncomment to run 'tests' at the bottom
		// DecissionTreeID3 dt = new DecissionTreeID3();
		// dt.testsThatBelongInTestingFramework();

		return;
	}

	/**
	 * Decision Tree using the ID3 algorithm
	 */
	public DecisionTreeID3() {
		dataSet = new HashMap<Integer, DataInput>();
	}

	/**
	 * 
	 * @param data Array of DataInput values to use during the tree/branch creation
	 * @param attributeSet List of attributes in the 'data' parameter
	 * @param attributeNames Array of attribute names represented in the attributeSet
	 * @param value Value of the attribute that this branch is created for, used when called recursively
	 * @return TreeBranch containing other branches or the classification of the parent's split in case of a leaf
	 * 
	 *         Assume that the target attribute is the DataInput.Classification
	 */
	// TODO: Combine the attributeSet and attributeNames params
	public TreeBranch createTree(DataInput[] data, ArrayList<Integer> attributeSet, String[] attributeNames,
			Double value) {

		ArrayList<String> classList = new ArrayList<String>();

		TreeBranch branch = new TreeBranch();
		branch.Examples = data;

		// Get all the classifications that belong to this dataset
		for (DataInput d : data) {
			classList.add(d.Classification);
			branch.AttributeValue = value;
		}

		// If all the classes are equal, return an empty TreeBranch
		if (Collections.frequency(classList, classList.get(0)) == classList.size()) {
			branch.Classification = classList.get(0);
			branch.AttributeValue = value;
			return branch;
		}

		// If there's no more attributes, return the majority vote
		if (data[0].Attributes.length == 0) {
			String[] vals = new String[data.length];
			for (int i = 0; i < data.length; i++) {
				vals[i] = data[i].Classification;
			}
			branch.Classification = getMajorityCount(vals);
			return branch;
		}

		// Find best feature to split data on
		int bestAttribute = findBestAttributeForSplit(data);
		if (bestAttribute >= 0)
			branch.BestAttribute = attributeNames[bestAttribute];

		attributeSet.remove(bestAttribute);

		// Get a set of values that we can populate the tree with
		HashSet<Double> uniqueValues = new HashSet<Double>();
		for (DataInput d : data) {
			if (!uniqueValues.contains(d.Attributes[bestAttribute])) {
				uniqueValues.add(d.Attributes[bestAttribute]);
			}
		}

		for (Double d : uniqueValues) {
			ArrayList<Integer> subAttributes = new ArrayList<Integer>(attributeSet);
			String[] subNames = new String[subAttributes.size()];

			for (int i = 0; i < subAttributes.size(); i++) {
				subNames[i] = attributeNames[subAttributes.get(i)];
			}

			branch.Children.add(createTree(splitDataSet(data, bestAttribute, d.doubleValue()), subAttributes,
					subNames, d));
		}

		return branch;
	}

	/**
	 * 
	 * @param data Array of DataInput elements that the split will be performed on
	 * @param attributeLocation Location of the attribute to split on
	 * @param value Value of the attribute to split on
	 * @return Array of DataInput elements containing the split
	 */
	protected DataInput[] splitDataSet(DataInput[] data, int attributeLocation, double value) {
		ArrayList<DataInput> returnDataSet = new ArrayList<DataInput>();

		for (int i = 0; i < data.length; i++) {

			// If the attribute matches the value that we want to split on
			// make a new DataInput and add it to the returnDataSet
			if (data[i].Attributes[attributeLocation] == value) {
				
				ArrayList<Double> tempAttributes = new ArrayList<Double>();
				
				for (int j = 0; j < data[i].Attributes.length; j++) {
					if(j != attributeLocation){
						tempAttributes.add(data[i].Attributes[j]);
					}
				}
				
				DataInput reducedInput = new DataInput(tempAttributes.size());
				
				for (int j = 0; j < tempAttributes.size(); j++) {
					reducedInput.Attributes[j] = tempAttributes.get(j);
				}
				
				/*reducedInput.Attributes = Arrays.copyOfRange(data[i].Attributes, attributeLocation + 1,
						data[i].Attributes.length);*/
				reducedInput.Classification = data[i].Classification;

				returnDataSet.add(reducedInput);
			}
		}

		return returnDataSet.toArray(new DataInput[0]);
	}

	/**
	 * 
	 * @param data Array of DataInput to calculate the entropy of
	 * @return Entropy calculation value
	 */
	protected double calculateEntropy(DataInput[] data) {

		int dataCount = data.length;
		if (dataCount == 0)
			return 0;

		HashMap<String, Integer> classCounts = new HashMap<String, Integer>();

		for (int i = 0; i < data.length; i++) {
			String currentClass = data[i].Classification;
			if (!classCounts.containsKey(currentClass)) {
				classCounts.put(currentClass, 0);
			}

			classCounts.put(currentClass, classCounts.get(currentClass) + 1);
		}

		double entropy = 0;

		for (String key : classCounts.keySet()) {
			double probability = (double) classCounts.get(key) / dataCount;
			entropy -= probability * Math.log(probability);
		}

		return entropy;
	}

	/**
	 * 
	 * @param data Array of DataInput used in determining which attribute to split on
	 * @return Attribute location that best splits the data
	 * 
	 *         Find the attribute that generates the greatest information gain when splitting the DataInput array
	 *         among its attributes
	 */
	protected int findBestAttributeForSplit(DataInput[] data) {
		// Set a large number for the base Entropy, for some
		// reason
		double baseEntropy = 10000000;// calculateEntropy(data);
		double bestInfoGain = 0.0;
		int bestAttribute = -1;
		int numOfAttribute = data[0].Attributes.length;

		for (int i = 0; i < numOfAttribute; i++) {
			// Get a unique list of attribute values
			HashSet<Double> uniqueValues = getUniqueValues(data, i);
			double newEntropy = 0.0;

			// Get the entropy for each data set split
			for (Double value : uniqueValues) {
				DataInput[] subsetData = splitDataSet(data, i, value);
				double prob = subsetData.length / (double) subsetData.length;

				newEntropy += prob * calculateEntropy(subsetData);
			}

			double infoGain = baseEntropy - newEntropy;
			// Select the best information gain and attribute
			if (infoGain > bestInfoGain) {
				bestInfoGain = infoGain;
				bestAttribute = i;
			}
		}

		return bestAttribute;
	}

	protected HashSet<Double> getUniqueValues(DataInput[] data, int attributeLocation) {
		HashSet<Double> uniqueVals = new HashSet<Double>();
		for (int i = 0; i < data.length; i++) {
			if (!uniqueVals.contains(data[i].Attributes[attributeLocation]))
				uniqueVals.add(data[i].Attributes[attributeLocation]);
		}

		return uniqueVals;
	}

	/**
	 * 
	 * @param values Array of string values to use for voting
	 * @return Most common String value in the values array
	 */
	protected String getMajorityCount(String[] values) {
		// TODO: This could be done better
		HashMap<String, Integer> valueCounts = new HashMap<String, Integer>();

		for (String d : values) {
			if (!valueCounts.containsKey(d)) {
				valueCounts.put(d, 0);
			}
			valueCounts.put(d, valueCounts.get(d) + 1);
		}

		String majorityVal = null;

		for (String d : valueCounts.keySet()) {
			if (majorityVal == null) {
				majorityVal = d;
			}

			if (valueCounts.get(d) > valueCounts.get(majorityVal)) {
				majorityVal = d;
			}
		}

		return majorityVal;
	}

	public int loadData(String fileName) {

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			// The first row should contain the attribute names, the last one
			// being the class, there should be at least 2 (1 attribute 1 class)
			StringTokenizer tokenizer = new StringTokenizer(br.readLine(), ",");

			int attribCount = tokenizer.countTokens();
			attributeCount = attribCount - 1;
			if (attribCount < 2) {
				System.err
						.println("Make sure the first line of the data file contain the attributes (at least 1 attribute and a class)");
				System.err.println("Example first line: attribute1, attribute2, className");
				return 1;
			}

			attributes = new String[attribCount];

			for (int i = 0; i < attribCount; i++) {
				attributes[i] = tokenizer.nextToken();
			}

			// Begin loading the remainder of the data file
			String line;
			int rowCont = 0;
			while ((line = br.readLine()) != null) {
				// Skip blank lines or ones 'commented' out
				if (!line.isEmpty() && !line.startsWith("//")) {
					tokenizer = new StringTokenizer(line, ",");

					if (tokenizer.countTokens() != attribCount)
						throw new Exception("Expected " + attribCount + " attributes but encountered "
								+ tokenizer.countTokens());

					String[] input = new String[attribCount];
					for (int i = 0; i < attribCount; i++) {
						input[i] = tokenizer.nextToken();
					}
					dataSet.put(rowCont, new DataInput(input));
					rowCont++;

				}
			}

			br.close();

			return 0;

		} catch (FileNotFoundException e) {
			System.err.println("Could not read the file specified, please check the path (path = " + fileName
					+ ")");
			return 1;
		} catch (Exception e) {
			System.err.println("Error processing the specified file.");
			System.err.println(e.getMessage());
			return 2;
		}
	}

	// TODO: Set up real unit tests
	public void testsThatBelongInTestingFramework() {
		System.out.println("----START TESTS----");

		DataInput[] testdata = { new DataInput(new double[] { 1, 1, 1 }, "yes"),
				new DataInput(new double[] { 1, 1, 1 }, "yes"),
				new DataInput(new double[] { 1, 0, 1 }, "yes"),
				new DataInput(new double[] { 0, 1, 0 }, "no"), new DataInput(new double[] { 0, 1, 0 }, "no") };

		System.out.println("getEntropy() - for testData:" + this.calculateEntropy(testdata));

		DataInput[] t = this.splitDataSet(testdata, 0, 1);
		System.out.println("splitDataSet() - expecting 3 items: " + t.length);
		System.out.println("splitDataSet() - expecting 2 attributes  for remaning data: "
				+ t[0].Attributes.length);

		HashSet<Double> td = this.getUniqueValues(testdata, 0);
		System.out.println("getUniqueValues() - expecting 2 items: " + td.size());

		System.out.println("findBestAttributeForSplit() - expecting 0 : "
				+ this.findBestAttributeForSplit(testdata));

		attributes = new String[] { "no surfacing", "flippers" };

		ArrayList<Integer> attributeSet = new ArrayList<Integer>();
		attributeSet.add(0);
		attributeSet.add(1);
		DataInput[] testdata2 = { new DataInput(new double[] { 1, 1 }, "yes"),
				new DataInput(new double[] { 1, 1 }, "yes"), new DataInput(new double[] { 1, 0 }, "no"),
				new DataInput(new double[] { 0, 1 }, "no"), new DataInput(new double[] { 0, 1 }, "no") };

		TreeBranch branch = this.createTree(testdata2, attributeSet, attributes, null);
		System.out.println("createTree() - ");
		printTree(branch, 1);

		Classifier classifier = new Classifier(attributes);
		String classification = classifier.Classify(branch, new DataInput(new double[] { 1, 0 }, ""));
		System.out.println("classifier.Classify() - expecting 'yes' : " + classification);

		System.out.println("----END TESTS----");
	}

	public static void printTree(TreeBranch branch, int level) {

		System.out.print(padLeft("{", level));
		System.out.print("Classification: " + branch.Classification + ", ");
		System.out.print("AttributeValue: " + branch.AttributeValue + ", ");
		System.out.print("BestAttribute: " + branch.BestAttribute + ", ");
		System.out.print("IsBranchLeaf: " + branch.isLeaf() + ", ");
		System.out.print("Examples.length: " + branch.Examples.length);
		System.out.print("}");

		System.out.println("");

		level = level + 15;

		for (int i = 0; i < branch.Children.size(); i++) {
			printTree(branch.Children.get(i), level);
		}

	}

	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}
}
