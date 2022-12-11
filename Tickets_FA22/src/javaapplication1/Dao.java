package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE aabuh_tickets(ticketID INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticketDESC VARCHAR(200), startDATE DATE, endDATE DATE)";
		final String createUsersTable = "CREATE TABLE aabuh_users(ticketID INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into aabuh_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDESC) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into aabuh_tickets" + "(ticket_issuer, ticketDESC) values(" + " '"
					+ ticketName + "','" + ticketDESC + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM aabuh_tickets");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	public void ticketStatus(String ticketID, String ticketDESC)
	{
		try
		{
			Statement state = getConnection().createStatement();
			//Selects to edit description from input ID
			ResultSet results = state.executeQuery("SELECT ticketDESC FROM aabuh_tickets WHERE ticketID = " + ticketID);
			getConnection().close();
			
			String result = null;
			while(results.next())
			{
				result = results.getString("ticketDESC");
			}
			
			//looks where to update ticket description from the input ID
			PreparedStatement prepared = getConnection().prepareStatement("UPDATE aabuh_tickets SET ticketDESC = ? WHERE ticketID = ?");
			String desc = ticketDESC;
			
			prepared.setString(1, desc);
			prepared.setString(2, ticketID);
			prepared.executeUpdate();
			prepared.close();
		}
		catch (SQLException e4)
		{
			e4.printStackTrace();
		}
	}
	
	// continue coding for updateRecords implementation
	public void updateRecords(String ticketID, String ticketDESC) {		
		try
		{
			Statement state = getConnection().createStatement();
			
			//looks for what record to update based on input ID
			ResultSet results = state.executeQuery("SELECT ticketDESC FROM aabuh_tickets WHERE ticketID = " + ticketID);
			getConnection().close();
			
			String result = null;
			while(results.next())
			{
				result = results.getString("ticketDESC");
			}
			
			//updates based on input description and ID
			PreparedStatement prepared = getConnection().prepareStatement("UPDATE aabuh_tickets SET ticketDESC = ? WHERE ticketID = ?");
			
			String desc = ticketDESC;
			
			prepared.setString(1, desc);
			prepared.setString(2, ticketID);
			prepared.executeUpdate();
			prepared.close();
		}
		catch (SQLException e2)
		{
			e2.printStackTrace();
		}
	}

	// continue coding for deleteRecords implementation
	public int deleteRecords(int ticketID) {
		try
		{
			Statement state = getConnection().createStatement();
			//Deletes ticket based on input ID
			String delete = ("DELETE FROM aabuh_tickets WHERE ticketID = " + ticketID);
			state.executeUpdate(delete);
		}
		catch(SQLException e3)
		{
			e3.printStackTrace();
		}
		
		return ticketID;
	}
}
