package bda.decissionTree;

public class TreePruner {

	/**
	 * 
	 * @param k is the number of classes 
	 * @param N is the is the number of instances in the data set
	 * @param n out of N examples in data set belong to the class
	 * @return expected error
	 */
	public double expectedErrorEstimate(int k, int N,  int n) {
		double error = (N - n + k - 1) / (N + k);

		return error;
	}
	
	
	public TreeBranch ReducedErrorPruner(TreeBranch tree, DataInput[] data){
		
		for(TreeBranch branch: tree.Children){
			// Calculate how pruning the branch improves error accuracy on the test data
			//double treeError = 
			
			// If pruning one of the branches increases the accuracy the prune the node
			// that increases the accuracy the most
			
			
		}
		
		return null;
	}
	
}
