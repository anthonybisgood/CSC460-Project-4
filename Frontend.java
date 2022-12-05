/**
 * export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${PWD}
 */

import java.util.Scanner;
import java.sql.*; // For access to the SQL interaction methods
import java.util.ArrayList;
import java.util.HashMap;
// TODO: Choose 3rd benefit
// TODO: Choose 3rd Category for Query 4
// TODO: Create Query 5 "You are required to implement the four provided queries as well as at least one query of your own design."
// TODO: do user record changing

public class Frontend {
    public static void main(String[] args) {
        // TODO: WHEN RAN IN LECTURA UNCOMMENT COMMENTED CODE BELOW AND DELETE LINE 26
        final String oracleURL = // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
        String username = null;
        String password = null;

        if (args.length == 2) { // get username/password from cmd line args
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage: java JDBC <username> <password>\n"
                    + " where <username> is your Oracle DBMS"
                    + " username,\n and <password> is your Oracle"
                    + " password (not your system password).\n");
            System.exit(-1);
        }
        Connection dbConn = getDbconn(oracleURL, username, password);
        // Connection dbConn = null;
        try {
            takeInput(dbConn);
            dbConn.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /**
     * Creates a Db Connection with the oracleURL and the users username and
     * password, returns the connection
     * used to make queries. WRITTEN BY Proffessor Mccann, taken from JDBC.java
     * 
     * @param oracleURL "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle"
     * @param username  the users oracle username
     * @param password  the users oracle password
     * @return returns a db connection used to make queries
     */
    private static Connection getDbconn(String oracleURL, String username, String password) {
        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);
        }
        // make and return a database connection to the user's
        // Oracle database
        Connection dbconn = null;
        try {
            dbconn = DriverManager.getConnection(oracleURL, username, password);
            dbconn.setAutoCommit(false);
            return dbconn;
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
        return null;
    }

    /**
     * Acts as user interface, where user inputs commands for quaries they want
     * 
     * @param dbConn the Connection to the database and how we send quaries
     * @throws SQLException
     */
    private static void takeInput(Connection dbConn) throws SQLException {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Type your query: '1', '2', '3', '4', '5', or 'exit' to exit the program.");
            System.out.println("Type 'help' to view queries.");
            System.out.println("Type 'rc' to edit records.");
            System.out.print(">: ");
            String value = input.nextLine().trim().toLowerCase();
            if (value.equals("exit")) {
                System.exit(0);
            } else if (value.equals("help")) {
                helpScreen();
            } else if (value.equals("1")) {
                query1Handler(dbConn);
            } else if (value.equals("2")) {
                query2Handler(input, dbConn);
            } else if (value.equals("3")) {
                query3Handler(input, dbConn);
            } else if (value.equals("4")) {
                query4Handler(dbConn);
            } else if (value.equals("5")) {
                query5Handler(input, dbConn);
            } else if (value.equals("rc")) {
                editDatabase(input, dbConn);
            } else {
                System.out.println("Please input a valid query number or type 'help' for help");
            }
        }
    }

    private static void query1Handler(Connection dbConn) {  
        String query = "SELECT DISTINCT first_name, last_name " +
        "FROM Passenger INNER JOIN Passenger_trip ON Passenger.passenger_id = Passenger_trip.passenger_id " + 
        "WHERE 4 IN (SELECT COUNT(DISTINCT airline_id) FROM Flight WHERE Flight.flight_id = Passenger_trip.flight_id)";
        executeQuery(query, dbConn, 1);
    }

    private static void query2Handler(Scanner input, Connection dbConn) {
        String date = validateDate(input, "march");
        if (date == null) {
            return;
        }
        // TODO: adjust flight month/day fields based on final table
        String query = "select Passenger_ID, Num_Bags "+ 
        "from Passenger_Trip join Flight on (flight_ID) "+
        "where extract(DAY from Flight.Boarding_time) = ? and extract(MONTH from Flight.Boarding_time) = 3";
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        string_parameters.put(1, date);
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, 2);
    }

    private static void query3Handler(Scanner input, Connection dbConn) {
         String query = "SELECT DISTINCT flight_id boarding_gate name boarding_time departing_time duration origin destination " +
        "FROM Flight WHERE EXTRACT(MONTH FROM Flight.boarding_time) = 'June' AND " +
        "EXTRACT(DAY FROM Flight.boarding_time) = ?";
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        String month = validateDate(input, "june");
        string_parameters.put(1, month);
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, 3);
    }

    private static void query4Handler(Connection dbConn) {

    }

    private static void query5Handler(Scanner input, Connection dbConn) {

    }

    /**
     * Method for sending the user to either change the airline side of passenger side of the Database
     * @param input
     * @param dbConn
     */
    private static void editDatabase(Scanner input, Connection dbConn) {
        String ans = "";
        while (true) {
            System.out.println("Type 1 to edit passenger info, 2 for flight info or 'exit' to quit");
            ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            if (ans.equals("1") || ans.equals("2")) {
                break;
            } else {
                System.out.println("Please enter a valid query.");
            }
        }
        if (ans.equals("1")) {
            editPassengerInfo(input, dbConn);
        } else if (ans.equals("2")) {
            editFlightInfo(input, dbConn);
        }
    }

    /**
     * Method for taking user input on if to add, update, or delete passenger info from the database.
     * @param input
     * @param dbConn
     */
    private static void editPassengerInfo(Scanner input, Connection dbConn) {
        String ans = "";
        while (true) {
            System.out.println("Type '1' to add passenger, '2' to update passenger info, or '3' to delete a passenger");
            ans = input.nextLine().trim();
            if (ans.equals("exit")) {
                return;
            }
            if (ans.equals("1") || ans.equals("2") || ans.equals("3")) {
                break;
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
        if (ans.equals("1")) {
            addPassenger(input, dbConn);
        } else if (ans.equals("2")) {
            updatePassengerInfo(input, dbConn);
        } else if (ans.equals("3")) {
            deletePassengerInfo(input, dbConn);
        }
    }

    /**
     * takes user input about a new passenger and adds it to the database.
     * @param input
     * @param dbConn
     */
    private static void addPassenger(Scanner input, Connection dbConn) {
        String[] fields = { "email", "phone_number", "address", "first_name", "last_name" };
        String res;
        try {
            PreparedStatement sql = dbConn.prepareStatement(
                    "insert into passenger (email, phone_number, address, first_name, last_name) values (?,?,?,?,?)");
            
            for (int i = 0; i < fields.length; i++) {
                System.out.printf("Input a new %s\n", fields[i]);
                res = input.nextLine().trim();
                sql.setString(i + 1, res);
            }
            sql.execute();
            dbConn.commit();
        } catch (SQLException e) {
            System.out.println("Error inserting passenger info into database");
            e.printStackTrace();
        }
    }

    /**
     * Method for taking in user input and allowing them to update fields in the passenger side of the DB
     * including fields from the passenger Table, Passenger benefits, number of bags, the flights they take, 
     * and the food/drink that they have
     * @param input
     * @param dbConn
     */
    private static void updatePassengerInfo(Scanner input, Connection dbConn) {
        int toChange = -1;
        String query = "";
        int id = -1;
        while (true) {
            System.out.println("What is the passenger id of the passenger you want to update?");
            System.out.print(">: ");
            String strId = input.nextLine().trim();
            try {
                id = Integer.parseInt(strId);
            } catch (NumberFormatException e) {
                System.out.println("Input a valid integer.");
                continue;
            }
            if (!validateId(id, "passenger", dbConn, "passenger_id")) {
                System.out.println("id is not in table, choose another");
            } else {
                break;
            }
        }
        // TODO: 9/10
        while (true) {
            System.out.println("What field would you like to update?");
            System.out.println("    Type '1' to change Email Address");
            System.out.println("    Type '2' to change Phone Number");
            System.out.println("    Type '3' to change Address");
            System.out.println("    Type '4' to change First Name");
            System.out.println("    Type '5' to change Last Name");
            System.out.println("    Type '6' to add Passenger benefit");
            System.out.println("    Type '7' to remove Passenger benefit");
            System.out.println("    Type '8' to change number of bags");
            System.out.println("    Type '9' to change passenger flights");
            System.out.println("    Type '10' to add Food/drink");
            System.out.print(">: ");
            String ans = input.nextLine().trim();
            // validate user input is an integer and within bounds
            try {
                toChange = Integer.parseInt(ans);
                if (toChange > 0 && toChange < 11) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an Integer");
            }
            System.out.println("Please enter a valid integer (1-10)");
        }
        String[] fields = {"email", "phone_number", "address", "first_name", "last_name"};
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        if (toChange >=1 && toChange <=5) {
            System.out.println("Please enter the updated value.");
            String newVal = input.nextLine().trim();
            System.out.print(">: ");
            query = String.format("UPDATE passenger SET %s = ? where passenger_id = ?", fields[toChange-1]);
            string_parameters.put(1, newVal);
            int_parameters.put(2, id);
            executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
        } else if (toChange == 6 || toChange == 7) {
            Boolean remove = (toChange == 7);
            changeBenefits(input, remove, id, dbConn);
        } else if (toChange == 8) {
            changeNumBags(input, id, dbConn);
        } else if (toChange == 9) {
            changePassengerFlights(input, id, dbConn);
        }
    }

    // make sure there arent any conflicting flights
    private static void changePassengerFlights(Scanner input, int id, Connection dbConn) {

    }

    /**
     * This method allows the user to change the number of bags a passenger has on a flight. Takes into 
     * account if the flight is real, if the passenger is on that flight, and if the passenger is 
     * a student(allowed 1 more bag). Changes num_bags field in passenger_trip.
     * 
     * @param input
     * @param id Passenger id
     * @param dbConn
     */
    private static void changeNumBags(Scanner input, int id, Connection dbConn) {
        int flight_ID = -1;
        while (true) {
            System.out.println("Enter a flight ID number");
            System.out.print(">: ");
            try {
                String in = input.nextLine().trim();
                if (in.equals("exit")) {
                    return;
                }
                flight_ID = Integer.parseInt(in);
                // check if the flight is real
                if (validateId(flight_ID, "Passenger_trip", dbConn, "flight_id")) {
                    // check if passenger is on that flight
                    if (validateId(flight_ID, "Passenger_trip", dbConn, "passenger_id = " + id + " and flight_id")) {
                        break;
                    } else {
                        System.out.println("Passenger is not on that Flight.");
                    }
                } else {
                    System.out.println("Flight ID not in DB records.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input an integer");
            }
        }
        int num_bags = 0;
        // checks to see if the passenger is a student
        boolean isStudent = validateId(id, "passenger_benefit", dbConn, "benefit_id = 2 and passenger_id");
        while (true) {
            System.out.println("How many bags? (Max 2 unless student).");
            System.out.print(">: ");
            try{
                String in = input.nextLine().trim();
                if (in.equals("exit")) {
                    return;
                }
                num_bags = Integer.parseInt(in);
                if (num_bags == 3 && isStudent) {
                    break;
                } else if (num_bags < 0 || num_bags > 2) {
                    System.out.println("Max number of bags is 2 (excluding students), cannot have negative bags");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Input an integer");
            }
        }
        String query = "UPDATE passenger_trip SET num_bags = ? where flight_id = ? and passenger_id = ?";
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, num_bags);
        int_parameters.put(2, flight_ID);
        int_parameters.put(3, id);
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
    }

    /**
     * Uses user input to add/remove benefits from Passenger_benefits based on the passenger_id
     * and benefit_id. Sanity checks benefit_id to make sure it is in bounds. 
     * @param input Scanner for input
     * @param remove A boolean where true if the user is removing a benefit from the passenger and
     * false if the user is adding a benefit to the passenger.
     * @param id passenger ID
     * @param dbConn 
     */
    private static void changeBenefits(Scanner input, Boolean remove, int id, Connection dbConn) {
        int benefit_id = -1;
        String query = "";
        while (true) {
            System.out.println("What is the benefit id that you would like to add/remove?");
            System.out.print(">: ");
            try {
                benefit_id = Integer.parseInt(input.nextLine().trim());
                if (benefit_id < 1 || benefit_id > 3) {
                    System.out.println("Please enter a valid Integer (1-3)");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an Integer");
            }
        }
        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();
        HashMap<Integer, Integer> int_parameters = new HashMap<Integer, Integer>();
        int_parameters.put(1, id);
        int_parameters.put(2, benefit_id);
        if (remove) {
            query = String.format("delete from passenger_benefit where passenger_id = ? and benefit_id = ?");
        } else {
            query = String.format("insert into passenger_benefit (passenger_id, benefit_id) values (?, ?)");
        }
        executeProtectedQuery(query, dbConn, string_parameters, int_parameters, -1);
    } 

    /**
     * Method for validating if an ID is in a table, also can be used for checking the join 
     * of 2 conditions (if sneaky enough, check changeNumBags()). Executes a query and 
     * checks if the query resulted in a Result set that is empty.
     * @param id Any Id that you want to check
     * @param table the table which you want to check
     * @param dbConn Connection to the DB
     * @param field The field attr that the ID is based on (if id is a flight_id (Ex. 31), field is 'flight_id')
     * @return Returns a boolean if the id is in the table or not.
     */
    private static boolean validateId(Integer id, String table, Connection dbConn, String field) {
        String query = String.format("select * from %s where %s = ?", table, field);
        try {
            PreparedStatement statement = dbConn.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet answer = statement.executeQuery();
            while (answer.next()) {
                ResultSetMetaData answermetadata = answer.getMetaData();
                if (answermetadata.getColumnCount() > 0) {
                    statement.close();
                    return true;
                } else{
                    statement.close();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    /**
     * Executes the query with a prepared statement to avoid sql injection entirely
     * 
     * @param query The query to be executed
     * @param dbConn The connection to the database
     * @param string_parameters A hashmap of the string parameters to be inserted into the query
     * @param int_parameters A hashmap of the int parameters to be inserted into the query
     * @param queryNumber The number of the query
     */
    private static void executeProtectedQuery(String query, Connection dbConn, HashMap<Integer, String> string_parameters, HashMap<Integer, Integer> int_parameters, int queryNumber) {
        try {
            PreparedStatement statement = dbConn.prepareStatement(query);
            for (Integer key : string_parameters.keySet()) {
                statement.setString(key, string_parameters.get(key));
            }
            for (Integer key : int_parameters.keySet()) {
                statement.setInt(key, int_parameters.get(key));
            }
            if (queryNumber < 0) {
                statement.executeUpdate();
                statement.close();
                dbConn.commit();
                return;
            }
            ResultSet answer = statement.executeQuery();
            if (answer != null) {
                while (answer.next()) {
                    ResultSetMetaData answermetadata = answer.getMetaData();
                    for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                        System.out.print(answermetadata.getColumnName(i) + "\t");
                    }
                    for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                        System.out.print(answer.getString(i) + "\t");
                    }
                }
            }
            statement.close();
            dbConn.commit();
        } catch(SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /**
     * Allows the user to delete a passenger by their passenger_id number, deletes all instances of that
     * passenger across all tables
     * @param input
     * @param dbConn
     */
    private static void deletePassengerInfo(Scanner input, Connection dbConn) {
        int idNum = -1;
        while (true) {
            System.out.println("Enter the passenger_id that you would like to delete");
            String id = input.nextLine().trim();
            try {
                idNum = Integer.parseInt(id);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
        String[] tables = {"passenger", "passenger_trip", "passenger_benefit", "passenger_history"};
        for (int i = 0; i < tables.length; i++) {
            String query = String.format("delete from %s where passenger_id = %d", tables[i], idNum);
            executeQuery(query, dbConn, -1);
        }
    }

    /**
     * 
     * @param input
     * @param dbConn
     */
    private static void editFlightInfo(Scanner input, Connection dbConn) {

    }

    /**
     * Executes quaries that are passed in by paramenters through the dbConnection.
     * (Influenced by Proffessor McCanns JDBC.java)
     * 
     * @param query    The query string we wish to execute
     * @param dbconn   the Connection to the database and how we send quaries
     * @param queryNum The number query that we wish to execute (1-3)
     */
    private static void executeQuery(String query, Connection dbConn, int queryNumber) {
        Statement stmt = null;
        ResultSet answer = null;
        try {
            stmt = dbConn.createStatement();
            answer = stmt.executeQuery(query);
            if (answer != null) {
                // Get the data about the query result to learn
                // the attribute names and use them as column headers
                ResultSetMetaData answermetadata = answer.getMetaData();
                for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
                    System.out.print(answermetadata.getColumnName(i) + "\t");
                }
                if (queryNumber == 2) {
                    while (answer.next()) {
                        System.out.println(answer.getString("Passenger_ID") + "\t"
                        + answer.getInt("num_bags"));
                    }
                }
                else {

                }
                // Use next() to advance cursor through the result
                // tuples and print their attribute values
                // TODO: parse answers here
            }
            dbConn.commit();
            System.out.println();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /**
     * Takes the scanner input and the months of EITHER JUNE OR MARCH ONLY and asks
     * the user
     * for a date in each respective month until a valid date is inputed.
     * 
     * @param input the Scanner object used to read user input
     * @param month the months of either march or june to validate
     * @return Returns a string of the valid user date in the respective month, or
     *         null if user exits
     */
    private static String validateDate(Scanner input, String month) {
        int days = 31;
        month = month.toLowerCase().trim();
        if (month.equals("june")) {
            days = 30;
        }
        String date = "";
        while (true) {
            System.out.printf("Enter a date from %s that you would like to query (1-%d).\n", month, days);
            System.out.print(">: ");
            date = input.nextLine().trim();
            // go back to main loop if user types exit
            if (date.equals("exit")) {
                return null;
            }
            try {
                int dateNum = Integer.parseInt(date);
                // if the user enters a valid number break the while loop, else keep going
                if (dateNum > 0) {
                    if (month.equals("june") && dateNum < 31) {
                        break;
                    }
                    if (month.equals("march") && dateNum < 32) {
                        break;
                    }
                }
                // if user does not enter a number
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number between 1 to 31 (inclusive)");
            }
        }
        return date;
    }

    /**
     * Provides the user with a help screen displaying the contents of each query
     */
    private static void helpScreen() {
        String query2 = "For any airline entered by the user, print the list of passengers, with the number of checked-in bags,\n"
                +
                "         sorted in ascending order by the number of checked-in bags and displays the output grouped by flights\n"
                +
                "         for a date in March that you choose.\n";
        String query3 = "Displays the schedule for flights on a date in June that you choose.\n" +
                "         The schedule contains the flight number, gate number, name of the airline of that flight,\n" +
                "         boarding time, departing time, duration of flight, and the origin and destination fo the flight.\n"
                +
                "         Sorted in ascending order of boarding time.\n";
        System.out.println();
        System.out.println("HELP SCREEN\n");
        System.out.println(
                "QUERY 1: Display the list of distinct passenger names, who took flights from all four airlines in the year 2021.\n");
        System.out.printf("QUERY 2: %s\n", query2);
        System.out.printf("QUERY 3: %s\n", query3);
        // TODO: Chose the 3rd Category of our choice and airline to query
        System.out.println("QUERY 4: Displays the a list of Students, Frequent Flyers, and ____ for ____ who: ");
        System.out.println("         1.) Traveled only once in the month of March.");
        System.out.println("         2.) Traveled with exactly one checked in bag anytime in the months of June aand July.");
        System.out.println("         3.) Ordered snacks/beverages on at least one flight.\n");
    }
}