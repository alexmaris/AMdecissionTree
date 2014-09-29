package bda.decissionTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class DataLoader {
	
	public String[] attributes;
	public int attributeCount;
	public HashMap<Integer, DataInput> dataSet;
	public ArrayList<Integer> attributeSet;
	
	public DataLoader(){
		dataSet = new HashMap<Integer, DataInput>();
		attributeSet = new ArrayList<Integer>();
	}
	
	
	public int loadData(String fileName) {

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			// The first row should contain the attribute names, the last one
			// being the class, there should be at least 2 (1 attribute 1 class)
			StringTokenizer tokenizer = new StringTokenizer(br.readLine(), ",");

			int attribCount = tokenizer.countTokens();
			int attributeCount = attribCount - 1;
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
	
	public void loadIrisDataSet(String fileName){
		DecisionTreeID3 dt = new DecisionTreeID3();
		int ret = this.loadData(fileName);

		// TODO: this is already in the data file...read it from there
		attributeSet = new ArrayList<Integer>();
		attributes = new String[] { "sepal-length", "sepal-width", "petal-length", "petal-width" };
		
		for (int i = 0; i < attributes.length; i++) {
			attributeSet.add(i);
		}
		
		if (ret == 0) {
			System.out.println("Finished loading data file with " + dt.dataSet.size() + " records.");
		}
	}

}
