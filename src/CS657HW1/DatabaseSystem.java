/**
 * Build up a database system to implement MySQL like commands
 * 
 * @author Ke Xu
 * @version 1.0
 * @date 2/14/2018
 */

package CS657HW1;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This is the class that defines database system. Main part is the hashmap
 * that indicates the database managed in the system.
 */
public class DatabaseSystem {
	private Map<String, Database> dbs;
	private static final String homeDir = System.getProperty("user.dir");
	private String curDbName;

	/**
	 * Class constructor.
	 */
	public DatabaseSystem() {
		dbs = new HashMap<>();
	}

	/**
	 * Main function. Initialize an instance and handle command from 
	 * standard input or file.
	 * 
	 * @param args name of input file
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DatabaseSystem cur = new DatabaseSystem();
		if (args.length > 0) {
			cur.readFile(args[0]);
		} else {
			cur.readKeyboard();
		}
	}

	/**
	 * Function to read standard input. Parse the command afterwards.
	 * 
	 * @throws IOException
	 */
	public void readKeyboard() throws IOException {
		Scanner sc = new Scanner(System.in);
		while(sc.hasNext()) {
			String line = sc.nextLine();
			if(line.equals(".EXIT")) {
				System.out.println("All done.");
				break;
			}
			if (!line.isEmpty()) {
				parseCommand(line.substring(0, line.length() - 1).split(" "));
			}
		}
		sc.close();
	}

	/**
	 * Function to read commands from file. Parse the command afterwards.
	 * 
	 * @param fileName	the name of file containing commands
	 * @throws IOException
	 */
	public void readFile(String fileName) throws IOException {
		File inFile = new File(fileName);
		Scanner sc = new Scanner(inFile);
		while (sc.hasNextLine())
		{
			String line = sc.nextLine();
			if(line.equals(".EXIT")) {
				System.out.println("All done.");
				break;
			}
			if (!line.isEmpty() && !line.startsWith("--")) {
				parseCommand(line.substring(0, line.length() - 1).split(" "));
			}
		}
		sc.close();
	}

	/**
	 * Command Parser. Understand the command and determine the following steps
	 * 
	 * @param cd the string of words in command
	 * @throws IOException
	 */
	public void parseCommand(String[] cd) throws IOException {
		// if the first word is CREATE
		if (cd[0].equals("CREATE")) {

			// if the second word is DATABASE
			if (cd[1].equals("DATABASE")) {
				String dbName = cd[2].toLowerCase();
				if (dbs.containsKey(dbName)) {
					System.out.println("!Failed to create database " + dbName + " because it already exists.");
				} else {
					dbs.put(dbName, new Database(dbName, homeDir));
				}

				// if the second word is TABLE
			} else if (cd[1].equals("TABLE")) {
				if (curDbName != null) {
					String tbName = cd[2].toLowerCase();
					Database curDb = dbs.get(curDbName);
					curDb.createTable(tbName, getSchemas(cd, 3));
				} else {
					System.out.println("!Select the database to use first.");
				}
			}

			// if the first word is DROP
		} else if (cd[0].equals("DROP")) {

			// if the second word is DATABASE
			if (cd[1].equals("DATABASE")) {
				String dbName = cd[2].toLowerCase();
				if (dbs.containsKey(dbName)) {
					dbs.get(dbName).deleteDatabase();
					dbs.remove(dbName);
				} else {
					System.out.println("!Failed to delete " + dbName + " because it does not exist.");
				}

				// if the second word is TABLE
			} else if (cd[1].equals("TABLE")) {
				if (curDbName != null) {
					String tbName = cd[2].toLowerCase();
					Database curDb = dbs.get(curDbName);
					curDb.dropTable(tbName);
				} else {
					System.out.println("!Select the database to use first.");
				}
			}

			// if the first word is SELECT
		} else if (cd[0].equals("SELECT")) {

			// if the second word is *
			if (cd[1].equals("*")) {
				if (curDbName != null) {
					String tbName = cd[3].toLowerCase();
					Database curDb = dbs.get(curDbName);
					curDb.selectAll(tbName);
				} else {
					System.out.println("!Select the database to use first.");
				}
			}

			// if the first word is USE
		} else if (cd[0].equals("USE")) {
			curDbName = cd[1].toLowerCase(); 
			if (!dbs.containsKey(curDbName)) {
				System.out.println("!Failed to use database " + curDbName + " because it does not exist.");
				curDbName = null;
			} else {
				System.out.println("Using database " + curDbName + ".");
			}

			// if the first word is ALTER
		} else if (cd[0].equals("ALTER")) {

			// if one following word is ADD
			if (cd[3].equals("ADD")) {
				if (curDbName != null) {
					String tbName = cd[2].toLowerCase();
					Database curDb = dbs.get(curDbName);
					curDb.alterAdd(tbName, getSchemas(cd, 4));
				} else {
					System.out.println("!Select the database to use first.");
				}
			}

			// otherwise
		} else {
			System.out.println("Unidentified command!");
		}
	}

	/**
	 * Parse the command which containing schemas. Get rid of parenthesis and comma. 
	 * 
	 * @param cd the string of words in command
	 * @param offset the start position of schemas in the command
	 * @return
	 */
	private String[][] getSchemas(String[] cd, int offset) {
		String[][] res = new String[(cd.length - offset) / 2][2];
		for (int i = 0; i < res.length; i++) {
			for (int j = 0; j < res[0].length; j++) {
				String cur = cd[offset + i * 2 + j];

				// delele '('
				if (cur.startsWith("(")) {
					res[i][j] = cur.substring(1, cur.length());
					// delete ',' and ')'
				} else if (cur.endsWith(",") || cur.endsWith(")")) {
					res[i][j] = cur.substring(0, cur.length() - 1);
				} else {
					res[i][j] = cur;
				}
			}
		}
		return res;
	}
}