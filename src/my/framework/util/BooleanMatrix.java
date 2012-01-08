package my.framework.util;

public class BooleanMatrix {
	
	public static BooleanMatrix multiply(BooleanMatrix a, BooleanMatrix b) {
		int size= a.size();
		BooleanMatrix result = new BooleanMatrix(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				boolean value = false; 
				for (int k = 0; k < size; k++) {
					value |= a.get(i, k) & b.get(k, j);
				}
				result.set(i, j, value);
			}
		}
		return result;
	}
	
	private boolean[][] matrix;
	private int size;
	
	public BooleanMatrix(int size) {
		this.matrix = new boolean[size][size];
		this.size = size;
	}
	
	public boolean get(int i, int j) {
		return matrix[i][j];
	}
	
	public void set(int i, int j, boolean value) {
		matrix[i][j] = value;
	}
	
	public int size() {
		return size;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sb.append(matrix[i][j] ? "1" : "0");
				if (j < size - 1) sb.append(", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
