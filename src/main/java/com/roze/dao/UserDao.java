package com.roze.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.roze.bean.User;

public class UserDao {
private String jdbcUrl = "jdbc:mysql://localhost:3306/usermng";
private String jdbcUsername="root";
private String jdbcPassword="firoze28";
private String jdbcDriver="com.mysql.cj.jdbc.Driver";


private static final String insert_users_sql="insert into users" + " (name,email,country) "
		+ "values " + " (?,?,?);";

private static final String select_user_by_id="select id,name,email,country from users where id=?";
private static final String select_all_users="select * from users";
private static final String delete_users_sql="delete from users where id=?;";
private static final String update_users_sql="update users set name=?,email=?,country=? where id=?;";

protected Connection getConnection() {
	
	Connection connection=null;
	try {
		Class.forName(jdbcDriver);
		connection=DriverManager.getConnection(jdbcUrl,jdbcUsername,jdbcPassword);
		
	}
	catch(SQLException e){
		
		e.printStackTrace();
	}
	catch(ClassNotFoundException e) {
		e.printStackTrace();
	}
	return connection;
}

public void insertUser(User user) throws SQLException {
	System.out.println(insert_users_sql);
	try(
	Connection connection=getConnection();
	PreparedStatement pst=connection.prepareStatement(insert_users_sql)){
		pst.setString(1,user.getName());
		pst.setString(2,user.getEmail());
		pst.setString(3,user.getCountry());
		System.out.println(pst);
		pst.executeUpdate();
		
}
	catch(SQLException e) {
		printSQLException(e);
	}
}

public User selectUser(int id ) {
	User user=null;
	try(Connection connection=getConnection();
			PreparedStatement pst=connection.prepareStatement(select_user_by_id)){
		pst.setInt(1,id);
		System.out.println(pst);
		
		ResultSet rs=pst.executeQuery();
		while(rs.next()) {
			String name=rs.getString("name");
			String email=rs.getString("email");
			String country=rs.getString("country");
			user=new User(id,name, email, country);
		}
	}
	catch(SQLException e) {
		printSQLException(e);
	}
	return user;
}

public List<User> selectAllUsers(){
	
	List<User> users=new ArrayList<>();
	try(Connection connection=getConnection();
			PreparedStatement pst=connection.prepareStatement(select_all_users)){
		
		System.out.println(pst);
		
		ResultSet rs=pst.executeQuery();
		while(rs.next()) {
			int id=rs.getInt("id");
			String name=rs.getString("name");
			String email=rs.getString("email");
			String country=rs.getString("country");
			users.add(new User(id,name, email, country));
		}
	}
	catch(SQLException e) {
		printSQLException(e);
	}
	return users;
}

public boolean updateUser(User user) throws SQLException{
	boolean rowUpdated;
	try(
			Connection connection=getConnection();
			PreparedStatement pst=connection.prepareStatement(update_users_sql)){
		System.out.println("Updated users: "+pst);
				pst.setString(1,user.getName());
				pst.setString(2,user.getEmail());
				pst.setString(3,user.getCountry());
				pst.setInt(4, user.getId());
				rowUpdated=pst.executeUpdate()>0;
				
		}
	return rowUpdated;
}

public boolean deleteUser(int id) throws SQLException {
	boolean rowDeleted;
	try(
	Connection connection=getConnection();
	PreparedStatement pst=connection.prepareStatement(delete_users_sql)){
		pst.setInt(1, id);
		rowDeleted=pst.executeUpdate()>0;
	}
	return rowDeleted;
}


private void printSQLException(SQLException ex) {
	for(Throwable e : ex) {
		if( e instanceof SQLException) {
			e.printStackTrace(System.err);
			System.err.println("SQLState: "+ ((SQLException) e).getSQLState());
			System.err.println("Error Code: "+ ((SQLException) e).getErrorCode());
			System.err.println("Message: "+ e.getMessage());
			
			Throwable t=ex.getCause();
			while(t !=null) {
				System.out.println("Cause: "+t);
				t=t.getCause();
			}
		}
	}
	
}

}
