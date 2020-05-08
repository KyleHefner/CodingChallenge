//Written by Kyle Hefner

package proj.CodingChallengeDemo;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import org.sqlite.SQLiteDataSource;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;




//Program that takes as input from the command line 1 csv file. The csv file must have a header of 10 fields.
//The program processes each entry in the csv file. If the entry has 10 fields, then that entry is inserted into a 
// .db file. If the entry doesn't have 10 fields, then it is written into a bad csv file. The number of successful records,
// bad records, and total records are then written to a log file. Maven dependencies for sqlite and OpenCsv have been 
//added to the pom.xml file
public class App 
{
	
	
	public Connection connect(String fileName){
		
		SQLiteDataSource ds = new SQLiteDataSource();
		String newFileName = fileName.replaceAll(".csv", "");
		ds.setUrl("jdbc:sqlite:" + newFileName + ".db");
		Connection c = null;
		try{
			c = ds.getConnection();
		} catch (SQLException e){
			System.out.println("Could not connect to db " + e.getMessage());
			System.exit(0);
		}
		return c;
		
	}
	
	
	//create a table in the new .db file and also add column names from the first line of the .csv file
	public void createTable(Connection conn, String[] columnNames ){
		
		Statement stmt = null;
		String sql1="";
		String sql2="";
		try{
			sql1 = "DROP TABLE IF EXISTS employees";
			sql2 = "CREATE TABLE employees ("
						 + columnNames[0] + " TEXT NOT NULL,"
						 + columnNames[1] + " TEXT NOT NULL,"
						 + columnNames[2] + " TEXT PRIMARY KEY NOT NULL,"
						 + columnNames[3] + " TEXT NOT NULL,"
						 + columnNames[4] + " TEXT NOT NULL,"
						 + columnNames[5] + " TEXT NOT NULL,"
						 + columnNames[6] + " TEXT NOT NULL,"
						 + columnNames[7] + " TEXT NOT NULL,"
						 + columnNames[8] + " TEXT NOT NULL,"
						 + columnNames[9] + " TEXT NOT NULL)";
			stmt = conn.createStatement();
			stmt.executeUpdate(sql1);
			stmt.executeUpdate(sql2);
			stmt.close();
		}  catch(SQLTimeoutException e){
			System.out.println("database connection timed out");
			System.exit(0);
		} catch(SQLException e ){
			System.out.println("could not execute the query: " + sql2);
			System.exit(0);
		}
	}
	
	//inserts good records into the db
	public void insertToTable(Connection conn, String[] columnNames, String[] entry ){
		Statement stmt = null;
		String query = "";
		try{
			query = "INSERT INTO employees ( " +
			columnNames[0] + ", " + columnNames[1] + ", " + columnNames[2] + ", " + 
			columnNames[3] + ", " + columnNames[4] + ", " + columnNames[5] + ", " + 
			columnNames[6] + ", " + columnNames[7] + ", " + columnNames[8] + ", " +
			columnNames[9] + ") "+ "VALUES ( " + 
			'"' + entry[0] + '"' + ", " + '"' + entry[1] + '"' + ", " + '"' + entry[2] + '"' + ", " + 
			'"' + entry[3] + '"' + ", " + '"' + entry[4] + '"' + ", " + '"' + entry[5] + '"' + ", " + 
			'"' + entry[6] + '"' + ", " + '"' + entry[7] + '"' + ", " + '"' + entry[8] + '"' + ", " +
			'"' + entry[9] + '"' + ")";
			
			//System.out.println(query);
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		}  catch(SQLTimeoutException e){
			System.out.println("database connection timed out");
			System.exit(0);
		} catch(SQLException e ){
			System.out.println("could not execute the query: " + query);
			System.exit(0);
		}
				
	}
	
	//will open the input csv file, read through it line by line, add matching records to the .db file, 
	//add non-matching records to bad.csv, and write statistics to a log file
	public void readInput(Connection conn, String fileName){
		int recordsReceived=0,recordsSuccessful=0,recordsFailed=0;
		String newFileName = fileName.replaceAll(".csv", "");
		
		try{
			
			//Reading the input csv file
			 Reader reader = Files.newBufferedReader(Paths.get("src/main/java/proj/CodingChallengeDemo/input.csv"));
			 CSVReader csvReader = new CSVReader(reader);
			
			//writing to the output csv file
			 String outputFileName = newFileName + "-bad.csv";
			 CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName));
			 
			 //writing to log file
			 File logFile = new File(newFileName + ".log");
			 logFile.delete();
			 logFile.createNewFile();
			 FileWriter logWriter = new FileWriter(logFile);
			 
			 String[] columnNames = csvReader.readNext(); //column headers
			 int columnCount = columnNames.length; //number of column headers 
			 createTable(conn, columnNames); //create table with column headers
			 
			 String[] record;
			 while((record = csvReader.readNext()) != null){
				 
				//want to use a List<String> rather than a String[] here
				 List<String> newRecord = new ArrayList<String>();
				 Collections.addAll(newRecord, record);
				 
				//remove spaces from newRecord so that its size will reflect if its a good record or a bad one
				 newRecord.removeAll(Collections.singleton(""));
				 
				 if(newRecord.size() != columnCount){
					 //bad record
					 recordsFailed++;
					 csvWriter.writeNext(record);//write record into bad csv file
				 }
				 else{
					 //good record
					 recordsSuccessful++;
					 insertToTable(conn, columnNames, record); //insert good record into db
				 }
				 recordsReceived++;

			 }
			 //close csv files and write to log file
			 csvReader.close();
			 csvWriter.close();
			 logWriter.write("# of records received: " + recordsReceived + '\n');
			 logWriter.write("# of records successful: " + recordsSuccessful + '\n');
			 logWriter.write("# of records failed: " + recordsFailed + '\n');
			 logWriter.close();
			 System.out.println("Done.");
		}
		catch(FileNotFoundException e){
			System.out.println("File not found");
			e.printStackTrace();
			System.exit(0);
		}
		catch(IOException e){
			e.printStackTrace();
			System.exit(0);
		} 
		catch(SecurityException e){
			System.out.println("You don't have permission to delete or modify the file");
			e.printStackTrace();
			System.exit(0);
		}
		catch(InvalidPathException e){
			System.out.println("Path string could not be converted to a Path");
			e.printStackTrace();
			System.exit(0);
		}
		

	}
	
    public static void main( String[] args )
    {
    	try{
			String file = "src/main/java/proj/CodingChallengeDemo/" + args[0];
			App application = new App();
			
			Connection conn = application.connect(file);
			application.readInput(conn,file);
    	}
    	catch(ArrayIndexOutOfBoundsException e){
    		System.out.println("ERROR: You must supply 1 command line argument in the form of a .csv file ");
    		e.printStackTrace();
    		System.exit(0);
    	}
    }
}
