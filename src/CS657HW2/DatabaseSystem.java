/**
 * Build up a database system to implement MySQL like commands
 * 
 * @author Ke Xu
 * @version 2.0
 * @date 3/25/2018
 */

package CS657HW2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
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
		String line = "";
		while(sc.hasNext()) {
			line += sc.nextLine();
			if(line.toLowerCase().equals(".exit")) {
				System.out.println("All done.");
				break;
			}
			if (!line.isEmpty() && !line.startsWith("--") && line.endsWith(";")) {
				parseCommand(line.substring(0, line.length() - 1).split("\\s+"));
			}
			if (line.startsWith("--") || line.endsWith(";")) {
				line = "";
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
		String line = "";
		while (sc.hasNextLine())
		{
			line += sc.nextLine();
			if(line.toLowerCase().equals(".exit")) {
				System.out.println("All done.");
				break;
			}
			if (!line.isEmpty() && !line.startsWith("--") && line.endsWith(";")) {
				parseCommand(line.substring(0, line.length() - 1).split("\\s+"));
			}
			if (line.startsWith("--") || line.endsWith(";")) {
				line = "";
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
				String dbName = cd[2];
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
		} else if (cd[0].equals("SELECT") || cd[0].equals("select")) {

			// if the second word is *
			if (cd[1].equals("*")) {
				if (curDbName != null) {
					String tbName = cd[3].toLowerCase();
					Database curDb = dbs.get(curDbName);
					curDb.selectAll(tbName);
				} else {
					System.out.println("!Select the database to use first.");
				}
				// if the following are schemas
			} else {
				if (curDbName != null) {
					int i = 0;
					while (i < cd.length && !cd[i].equals("from")) {
						i++;
					}
					String tbName = cd[i + 1].toLowerCase();
					Database curDb = dbs.get(curDbName);
					curDb.select(tbName, cd);
				} else {
					System.out.println("!Select the database to use first.");
				} 
			}

			// if the first word is USE
		} else if (cd[0].equals("USE")) {
			curDbName = cd[1]; 
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
			// if the first word is insert
		} else if (cd[0].equals("insert")) {
			if (curDbName != null) {
				String tbName = cd[2].toLowerCase();
				Database curDb = dbs.get(curDbName);
				curDb.insert(tbName, getTuples(cd));
			} else {
				System.out.println("!Select the database to use first.");
			}
			// if the first word is update
		} else if (cd[0].equals("update")) {
			if (curDbName != null) {
				String tbName = cd[1].toLowerCase();
				Database curDb = dbs.get(curDbName);
				curDb.update(tbName, cd);
			} else {
				System.out.println("!Select the database to use first.");
			}
			// if the first word is delete
		} else if (cd[0].equals("delete")) {
			if (curDbName != null) {
				String tbName = cd[2].toLowerCase();
				Database curDb = dbs.get(curDbName);
				curDb.delete(tbName, cd);
			} else {
				System.out.println("!Select the database to use first.");
			}
			// otherwise
		} else {
			System.out.println("Unidentified command!");
		}
	}

	/**
	 * Parse insert command to get the tuples.
	 * 
	 * @param cd the string of words in command
	 * @return array of tuples
	 */
	private String[] getTuples(String[] cd) {
		Deque<String> tuples = new ArrayDeque<>();
		int i = cd.length -1;
		while (i >= 0) {
			String cur = cd[i];
			if (cur.endsWith(")")) {
				tuples.offerFirst(cur.substring(0, cur.length() - 1));
			} else if (cur.endsWith(",")) {
				cur = cur.substring(0, cur.length() -1);
				if (cur.contains("(")) {
					int offset = cur.indexOf("(");
					cur = cur.substring(offset + 1, cur.length());
					tuples.offerFirst(cur);
					break;
				} else {
					tuples.offerFirst(cur);
				}
			}
			i--;
		}
		String[] res = new String[tuples.size()];
		for (int j = 0; j < res.length; j++) {
			res[j] = tuples.pollFirst();
		}
		return res;
	}

	/**
	 * Parse the command which containing schemas. Get rid of parenthesis and comma. 
	 * 
	 * @param cd the string of words in command
	 * @param offset the start position of schemas in the command
	 * @return 2-D array of schemas
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