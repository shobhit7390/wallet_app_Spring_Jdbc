package shobhit.wallet_springJdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WalletManager {
	private Connection con;
	
	public WalletManager() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wallet_jdbc_db", "root", "Shobhit@123");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public User getUser(int userId) {
		User user=null;
		
		try {
			String query="Select * from Users where userId=?";
			PreparedStatement st=con.prepareStatement(query);
			st.setInt(1, userId);
			ResultSet rs=st.executeQuery();
			if(rs.next()) {
				user=new User(rs.getInt(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getString(5),rs.getString(6),
						rs.getString(7), rs.getString(8), rs.getDouble(9));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public User getUserBySpecifier(String q) {
		User user=null;

		try {
			String query="Select * from Users where upiId=? or accountNumber=?";
			PreparedStatement st=con.prepareStatement(query);
			st.setString(1, q);
			st.setString(2, q);
			ResultSet rs=st.executeQuery();
			if(rs.next()) {
				user=new User(rs.getInt(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getString(5),rs.getString(6),
						rs.getString(7), rs.getString(8), rs.getDouble(9));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}


	public void addUser(String userName, String userPassword, String userEmail, String userPhone, String upiId, String accountNumber, String userPin) {
		try {
			String query="insert into Users(userName, userPassword, userEmail, userPhone, upiId, accountNumber, userPin) values(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st=con.prepareStatement(query);
			st.setString(1, userName);
			st.setString(2, userPassword);
			st.setString(3, userEmail);
			st.setString(4, userPhone);
			st.setString(5, upiId);
			st.setString(6, accountNumber);
			st.setString(7, userPin);

			st.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public User authenticateUser(String userPhone, String userPassword) {
		User user=null;
		
		try {
			String query="Select * from Users where userPhone=? AND userPassword=?";
			PreparedStatement st=con.prepareStatement(query);
			st.setString(1, userPhone);
			st.setString(2, userPassword);
			ResultSet rs=st.executeQuery();
			if(rs.next()) {
				user=new User(rs.getInt(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getString(5),rs.getString(6),
						rs.getString(7), rs.getString(8), rs.getDouble(9));
				System.out.println("User is authenticated..!!");
			} else {
				System.out.println("No data found in database");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public void updateUserBalance(int userId, double newBalance) {
		try {
			String query="update Users set balance=? where userId=?";
			PreparedStatement st=con.prepareStatement(query);
			st.setDouble(1, newBalance);
			st.setInt(2, userId);
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addTransaction(Transaction transaction) {
		try {
			String query="insert into Transactions(userId, amount, transactionType) values(?, ?, ?)";
			PreparedStatement st=con.prepareStatement(query);
			st.setInt(1, transaction.getUserId());
			st.setDouble(2, transaction.getAmount());
			st.setString(3, transaction.getTransactionType());
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void modifyPin(int userId, String newPin){
        try {
			String query="update Users set userPin=? where userId=?";
            PreparedStatement st= con.prepareStatement(query);
			st.setString(1, newPin);
			st.setInt(2, userId);
			st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
	public List<Transaction> getTransactionHistory(int userId){
		List<Transaction> transactions=new ArrayList<>();
		
		try {
			String query="select * from Transactions where userId=? order by transTimestamp desc";
			PreparedStatement st=con.prepareStatement(query);
			st.setInt(1, userId);
			ResultSet rs=st.executeQuery();
			while(rs.next()) {
				Transaction transaction=new Transaction(rs.getInt(1), rs.getInt(2), rs.getDouble(3), rs.getString(4), rs.getTimestamp(5));
				transactions.add(transaction);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return transactions;
	}

}
