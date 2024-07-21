package shobhit.wallet_springJdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class WalletManager {
	private static JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	private Connection con;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate=new JdbcTemplate(dataSource);
		System.out.println("Setter called");
	}

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
			return jdbcTemplate.queryForObject(query, new UserMapper(), userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public User getUserBySpecifier(String q) {
		User user=null;

		try {
			String query="Select * from Users where upiId=? or accountNumber=?";
			return jdbcTemplate.queryForObject(query, new UserMapper(), q, q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}


	public void addUser(String userName, String userPassword, String userEmail, String userPhone, String upiId, String accountNumber, String userPin) {
		try {
			String query="insert into Users(userName, userPassword, userEmail, userPhone, upiId, accountNumber, userPin) values(?, ?, ?, ?, ?, ?, ?)";
			jdbcTemplate.update(query, userName, userPassword, userEmail, userPhone, upiId, accountNumber, userPin);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public User authenticateUser(String userPhone, String userPassword) {
		User user=null;
		
		try {
			String query="Select * from Users where userPhone=? AND userPassword=?";
			user = jdbcTemplate.queryForObject(query, new UserMapper(), userPhone, userPassword);
			System.out.println("User Authenticated");
		} catch (Exception e) {
			System.out.println("No Valid User Found with this Id..!");
		}
		return user;
	}
	
	public void updateUserBalance(int userId, double newBalance) {
		try {
			String query="update Users set balance=? where userId=?";
			jdbcTemplate.update(query, newBalance, userId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addTransaction(Transaction transaction) {
		try {
			String query="insert into Transactions(userId, amount, transactionType) values(?, ?, ?)";
			jdbcTemplate.update(query, transaction.getUserId(), transaction.getAmount(), transaction.getTransactionType());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void modifyPin(int userId, String newPin){
        try {
			String query="update Users set userPin=? where userId=?";
			jdbcTemplate.update(query, newPin, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
	public List<Transaction> getTransactionHistory(int userId){
		List<Transaction> transactions=new ArrayList<>();
		
		try {
			String query="select * from Transactions where userId=? order by transTimestamp desc";
			return jdbcTemplate.query(query, new TransactionMapper(), userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transactions;
	}

}

class UserMapper implements RowMapper<User> {
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new User(rs.getInt(1), rs.getString(2), rs.getString(3),
				rs.getString(4), rs.getString(5),rs.getString(6),
				rs.getString(7), rs.getString(8), rs.getDouble(9));
	}
}

class TransactionMapper implements RowMapper<Transaction> {
	@Override
	public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Transaction(rs.getInt(1), rs.getInt(2), rs.getDouble(3), rs.getString(4), rs.getTimestamp(5));
	}
}
