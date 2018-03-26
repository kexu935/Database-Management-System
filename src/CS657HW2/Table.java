package CS657HW2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This is the class that defines table.
 */
public class Table {
	private String tblName;
	private String parentDir;
	private List<String[]> schemas;
	private File tbl;
	private Map<String, String[]> records;
	
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
		this.records = new TreeMap<>();
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
		writer.write("\n");
		for (String[] record : records.values()) {
			for (String s : record) {
				writer.write(s);
				writer.write("\t");
			}
			writer.write("\n");
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
	 * @throws IOException 
	 */
	public void print() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String[] schema : schemas) {
			for (String s : schema) {
				sb.append(s + " ");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("|");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\n");
		for (String[] record : records.values()) {
			for (String s : record) {
				sb.append(s);
				sb.append("|");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\n");
		}
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

	/**
	 * Insert a record to the table.
	 * 
	 * @param tuples tuples to insert
	 * @throws IOException
	 */
	public void insert(String[] tuples) throws IOException {
		String key = tuples[0];
		String[] temp = records.get(key);
		if (temp != null) {
			System.out.println("!Failed record existed");
		} else {
			records.put(key, tuples);
			System.out.println("1 new record inserted.");
		}
		write();
	}
	
	/**
	 * Update the table.
	 * 
	 * @param cd input command
	 * @throws IOException
	 */
	public void update(String[] cd) throws IOException {
		int i = findAttrIndex(cd[7]);
		int j = findAttrIndex(cd[3]);
		if (i == -1 || j == -1) {
			System.out.println("!Failed cannot find attribute");
			return;
		}
		int count = 0;
		for (Map.Entry<String, String[]> e : records.entrySet()) {
			String[] curTuple = e.getValue();
			if (!curTuple[i].equals(cd[9])) {
				continue;
			} else {
				curTuple[j] = cd[5];
				e.setValue(curTuple);
				count++;
			}
		}
		if (count == 0) {
			System.out.println("No record modified.");
		} else if (count == 1) {
			System.out.println("1 record modified.");
		} else {
			System.out.println(count + " records modified.");
		}
		write();
	}
	
	/**
	 * Delete records from the table.
	 * 
	 * @param cd input command
	 * @throws IOException
	 */
	public void delete(String[] cd) throws IOException {
		int i = findAttrIndex(cd[4]);
		if (i == -1) {
			System.out.println("!Failed cannot find attribute");
			return;
		}
		int count = 0;
		Iterator<Map.Entry<String, String[]>> it = records.entrySet().iterator();
		while (it.hasNext()) {
			String[] curTuple = it.next().getValue();
			if (cd[5].equals("=")) {
				if (!curTuple[i].equals(cd[6])) {
					continue;
				} else {
					it.remove();
					count++;
				}
			} else {
				if (check(cd[5], curTuple[i], cd[6])) {
					it.remove();
					count++;
				}
			}
		}
		if (count == 0) {
			System.out.println("No record deleted.");
		} else if (count == 1) {
			System.out.println("1 record deleted.");
		} else {
			System.out.println(count + " records deleted.");
		}
		write();
	}
	
	/**
	 * Query the table.
	 * 
	 * @param cd input command
	 * @throws IOException
	 */
	public void select(String[] cd) throws IOException {
		int index = 1;
		Set<Integer> cols = new HashSet<>();
		while (index < cd.length && !cd[index].equals("from")) {
			String attr = cd[index].endsWith(",") ? cd[index].substring(0, cd[index].length() - 1) : cd[index];
			cols.add(findAttrIndex(attr));
			index++;
		}
		while (index < cd.length && !cd[index].equals("where")) {
			index++;
		}
		int j = findAttrIndex(cd[++index]);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < schemas.size(); i++) {
			if (cols.contains(i)) {
				for (String s : schemas.get(i)) {
					sb.append(s + " ");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("|");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\n");
		for (String[] record : records.values()) {
			if (check(cd[index + 1], record[j], cd[index + 2])) {
				for (int i = 0; i < record.length; i++) {
					if (cols.contains(i)) {
						sb.append(record[i]);
						sb.append("|");
					}
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\n");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		System.out.println(sb.toString());
	}
	
	/**
	 * Get the index of the schema.
	 * 
	 * @param attr the schema
	 * @return
	 */
	public int findAttrIndex(String attr) {
		for (int i = 0; i < schemas.size(); i++) {
			if (schemas.get(i)[0].equals(attr)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Calculate the operation and check if the comparison valid.
	 * 
	 * @param op operator
	 * @param left left operand
	 * @param right right operand
	 * @return if the operation is true or not
	 */
	public boolean check(String op, String left, String right) {
		Double l = Double.parseDouble(left);
		Double r = Double.parseDouble(right);
		if (op.equals(">")) {
			return Double.compare(l, r) > 0;
		} else if (op.equals("<")) {
			return Double.compare(l, r) < 0;
		} else if (op.equals(">=")) {
			return Double.compare(l, r) >= 0;
		} else if (op.equals("<=")) {
			return Double.compare(l, r) <= 0;
		} else if (op.equals("!=")) {
			return Double.compare(l, r) != 0;
		}
		return false;
	}
}
