package bda.decissionTree;

public class DataInput {

	/**
	 * Default constructor
	 */
	public DataInput() {

	}

	/**
	 * 
	 * @param attributecount Count of expected attributes
	 */
	public DataInput(int attributecount) {
		Attributes = new double[attributecount];
	}

	/**
	 * 
	 * @param attributes Array of attribute values
	 * @param classification Classification value
	 */
	public DataInput(double[] attributes, String classification) {
		this.Attributes = attributes;
		this.Classification = classification;
	}

	/**
	 * @param fileLine
	 *            Comma separated values of the DataInput's attributes including
	 *            classification
	 * 
	 */
	public DataInput(String[] fileLine) {
		Attributes = new double[fileLine.length - 1];
		for (int i = 0; i < fileLine.length - 1; i++) {
			Attributes[i] = Double.parseDouble(fileLine[i]);
		}
		this.Classification = fileLine[fileLine.length - 1];
	}

	public double[] Attributes;
	public String Classification;

}
