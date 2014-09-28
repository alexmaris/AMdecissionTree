package bda.decissionTree;

import java.util.ArrayList;

public class TreeBranch {

	public ArrayList<TreeBranch> Children;
	public DataInput[] Examples; // list of data rows if this is a leaf
	
	public String Classification;
	public Integer Decission;
	public String BestAttribute;
	public Double AttributeValue;
	
	public TreeBranch Parent;
	
	public TreeBranch() {
		//Decission = -1;
		
		Children = new ArrayList<TreeBranch>();
	}
	
	public boolean isLeaf(){
		return Children.isEmpty();
	}

}
