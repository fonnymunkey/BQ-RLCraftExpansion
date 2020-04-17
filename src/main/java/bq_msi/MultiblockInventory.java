package bq_msi;

public class MultiblockInventory {
	private String[][][] fileArray;
	private int[] keyCoords;
	
	public MultiblockInventory(String[][][] fileArray, int[] keyCoords)
	{
		this.fileArray = fileArray;
		this.keyCoords = keyCoords;
	}
	public String[][][] getFileArray() {return fileArray;}
	public int[] getKeyCoords() {return keyCoords;}
}
