package CS657HW2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the class that defines database. Main part is the hashmap
 * that indicates the tables inside.
 */
public class Database {
	private String dbName;
	private String parentDir;
	private File db;
	private Map<String, Table> tables;

	/**
	 * Class constructor.
	 * 
	 * @param dbName name of the database
	 * @param parentDir the path of directory
	 */
	public Database(String dbName, String parentDir) {
		this.dbName = dbName;
		this.parentDir = parentDir;
		this.db = new File(parentDir + "/" + dbName);
		this.db.mkdir();
		tables = new HashMap<>();
		System.out.println("Database " + dbName + " created.");
	}
	
	/**
	 * Delete database instance. Drop all tables inside as well.
	 */
	public void deleteDatabase() {
		// delete all tables inside
		for (String s : tables.keySet()) {
			dropTable(s);
		}
		tables = null;
		
		// delete the database
		this.db.delete();
		System.out.println("Database " + this.dbName + " deleted.");
	}
	
	/**
	 * Create table using given table name and schemas.
	 * 
	 * @param tbName name of the table
	 * @param schemas array of schemas
	 * @throws IOException
	 */
	public void createTable(String tbName, String[][] schemas) throws IOException {
		Table temp = tables.get(tbName);
		if (temp == null) {
			tables.put(tbName, new Table(tbName, schemas, parentDir + "/" + dbName));
		} else {
			System.out.println("!Failed to create table " + tbName + " because it already exists.");
		}
	}

	/**
	 * Delete table using the name of table.
	 * 
	 * @param tbName name of the table
	 */
	public void dropTable(String tbName) {
		Table temp = tables.get(tbName);
		if (temp == null) {
			System.out.println("!Failed to delete " + tbName + " because it does not exist.");
		} else {
			temp.delete();
			tables.remove(tbName);
		}
	}

	/**
	 * Display the schemas and content in a table.
	 * 
	 * @param tbName name of the table
	 * @throws IOException 
	 */
	public void selectAll(String tbName) throws IOException {
		Table temp = tables.get(tbName);
		if (temp == null) {
			System.out.println("!Failed to query table " + tbName + " because it does not exist.");
		} else {
			temp.print();
		}
	}

	/**
	 * Add schemas to a table.
	 * 
	 * @param tbName name of the table
	 * @param schemas schemas to add
	 * @throws IOException
	 */
	public void alterAdd(String tbName, String[][] schemas) throws IOException {
		Table temp = tables.get(tbName);
		if (temp == null) {
			System.out.println("!Failed to alter table " + tbName + " because it does not exist.");
		} else {
			temp.alterAdd(schemas);
			System.out.println("Table " + tbName + " modified.");
		}
	}

	/**
	 * Insert a record to a table.
	 * 
	 * @param tbName name of the table
	 * @param tuples tuples to add
	 * @throws IOException
	 */
	public void insert(String tbName, String[] tuples) throws IOException {
		Table temp = tables.get(tbName);
		if (temp == null) {
			System.out.println("!Failed to insert into table " + tbName + " because it does not exist.");
		} else {
			temp.insert(tuples);
		}
	}
	
	/**
	 * Update a record of a table.
	 * 
	 * @param tbName name of the table
	 * @param cd input command
	 * @throws IOException
	 */
	public void update(String tbName, String[] cd) throws IOException {
		Table temp = tables.get(tbName);
		if (temp == null) {
			System.out.println("!Failed to update table " + tbName + " because it does not exist.");
		} else {
			temp.update(cd);
		}
	}
	
	/**
	 * Delete records from a table.
	 * 
	 * @param tbName name of the table
	 * @param cd input command
	 * @throws IOException
	 */
	public void delete(String tbName, String[] cd) throws IOException {
		Table temp = tables.get(tbName);
		if (temp == null) {
			System.out.println("!Failed to delete table " + tbName + " because it does not exist.");
		} else {
			temp.delete(cd);
		}
	}
	
	/**
	 * Query a table.
	 * 
	 * @param tbName name of the table
	 * @param cd input command
	 * @throws IOException
	 */
	public void select(String tbName, String[] cd) throws IOException {
		Table temp = tables.get(tbName);
		if (temp == null) {
			System.out.println("!Failed to query table " + tbName + " because it does not exist.");
		} else {
			temp.select(cd);
		}
	}
}
