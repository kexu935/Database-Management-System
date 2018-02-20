package CS657HW1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that defines table.
 */
public class Table {
	private String tblName;
	private String parentDir;
	private List<String[]> schemas;
	private File tbl;
	
	/**
	 * Class constructor.
	 * 
	 * @param tblName name of the table
	 * @param schemas array of schemas
	 * @param parentDir path of the directory
	 * @throws IOException
	 */
	public Table(String tblName, String[][] schemas, String parentDir) throws IOException {
		this.tblName = tblName;
		this.parentDir = parentDir;
		this.schemas = new ArrayList<>();
		addSchemas(schemas);
		tbl = new File(this.parentDir + "/" + this.tblName);
		tbl.createNewFile();
		write();
		System.out.println("Table " + tblName + " created.");
	}

	/**
	 * Output data to file.
	 * 
	 * @throws IOException
	 */
	private void write() throws IOException {
		FileWriter writer = new FileWriter(tbl);
		for (String[] schema : schemas) {
			for (String s : schema) {
				writer.write(s);
				writer.write("\t");
			}
		}
		writer.close();
	}

	/**
	 * Delete the table.
	 */
	public void delete() {
		tbl.delete();
		System.out.println("Table " + tblName + " deleted.");
	}

	/**
	 * Print table schemas and content.
	 */
	public void print() {
		StringBuilder sb = new StringBuilder();
		for (String[] schema : schemas) {
			sb.append(" ");
			for (String s : schema) {
				sb.append(s + " ");
			}
			sb.append("|");
		}
		sb.deleteCharAt(0);
		sb.deleteCharAt(sb.length() - 1);
		System.out.println(sb.toString());
	}

	/**
	 * Add schemas to the table.
	 * 
	 * @param schemas array of schemas to add
	 */
	public void addSchemas(String[][] schemas) {
		for (int i = 0; i < schemas.length; i++) {
			String[] schema = new String[2];
			for (int j = 0; j < schemas[0].length; j++) {
				schema[j] = schemas[i][j];
			}
			this.schemas.add(schema);
		}
	}

	/**
	 * Alter add schemas to the table. Then update the file.
	 * 
	 * @param schemas array of schemas to add
	 * @throws IOException
	 */
	public void alterAdd(String[][] schemas) throws IOException {
		addSchemas(schemas);
		write();
	}
	
}
