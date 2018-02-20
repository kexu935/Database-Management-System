# Database-Management-System
* A database management system with similar but simpler interface than Sqlite3
* Code in Java

## compile and run in Linux shell:
* copy all java file into one folder, create 'sourecefiles' containing the names of all java files in each line 
```
1)	$ mkdir out
2)	$ javac -d ./out @sourcefiles
```
* take a look at the folder's name inside out, e.g CS657HW1
```
3)	$ java -cp ./out CS657HW1.DatabaseSystem
```
* or if you have testing file, like PA_test.sql
```
	$ java -cp ./out CS657HW1.DatabaseSystem PA1_test.sql
```
