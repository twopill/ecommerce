package ecommerce.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import org.apache.commons.lang3.RandomStringUtils;
import ecommerce.dto.PasswordsDto;
import ecommerce.dto.UsersDto;
import ecommerce.exceptions.EcommerceException;
import ecommerce.mail.SendEmail;

@Stateless
public class UsersDao {

	@Resource(lookup = "java:/jdbc/ecommerce")
	private DataSource ds;

	public List<UsersDto> selectAll() throws EcommerceException {

		List<UsersDto> usersReg = new ArrayList<>();

		try {
			Connection conn = ds.getConnection();
			String sql = "select  * from users";
			PreparedStatement select_stmt = conn.prepareStatement(sql);
			ResultSet result = select_stmt.executeQuery(sql);

			while (result.next()) {
				int userID = result.getInt(1);
				String email = result.getString(2);
				String password = result.getString(3);
				String name = result.getString(4);
				String surname = result.getString(5);
				String birthdate = result.getString(6);
				String phone = result.getString(7);
				String token = result.getString(8);
				usersReg.add(new UsersDto(userID, email, password, name, surname, birthdate, phone, token));
			}
			result.close();
			select_stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
		return usersReg;
	}

	public List<UsersDto> selectByName(String n) throws EcommerceException {

		List<UsersDto> usersReg = new ArrayList<>();

		try {
			Connection conn = ds.getConnection();
			String sql = "select  * from users where name = ?";
			PreparedStatement select_stmt = conn.prepareStatement(sql);
			select_stmt.setString(1, n);
			ResultSet result = select_stmt.executeQuery();

			while (result.next()) {
				int userID = result.getInt(1);
				String email = result.getString(2);
				String password = result.getString(3);
				String name = result.getString(4);
				String surname = result.getString(5);
				String birthdate = result.getString(6);
				String phone = result.getString(7);
				String token = result.getString(8);
				usersReg.add(new UsersDto(userID, email, password, name, surname, birthdate, phone, token));
			}
			result.close();
			select_stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
		return usersReg;
	}

	public List<UsersDto> selectByID(int id) throws EcommerceException {

		List<UsersDto> usersReg = new ArrayList<>();

		try {
			Connection conn = ds.getConnection();
			String sql = "select  * from users where userID = ?";
			PreparedStatement select_stmt = conn.prepareStatement(sql);
			select_stmt.setInt(1, id);
			ResultSet result = select_stmt.executeQuery();

			while (result.next()) {
				int userID = result.getInt(1);
				String email = result.getString(2);
				String password = result.getString(3);
				String name = result.getString(4);
				String surname = result.getString(5);
				String birthdate = result.getString(6);
				String phone = result.getString(7);
				String token = result.getString(8);
				usersReg.add(new UsersDto(userID, email, password, name, surname, birthdate, phone, token));
			}
			result.close();
			select_stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
		return usersReg;
	}

	public int insert(UsersDto udto) throws EcommerceException {
		int flag = 0;
		try {
			Connection conn = ds.getConnection();
			String sql = "select md5(?) from dual";
			String pswMD5 = null;
			PreparedStatement insert_statement = conn.prepareStatement(sql);
			insert_statement.setString(1, udto.getPassword());
			ResultSet result = insert_statement.executeQuery();
			while (result.next()) {
				pswMD5 = result.getString(1);
			}
			sql = " insert into users (email, password, name,  surname, birthdate, phone, token)"
					+ " values (?, ?, ?, ?, ?, ?, ?)";

			insert_statement = conn.prepareStatement(sql);
			insert_statement.setString(1, udto.getEmail());
			insert_statement.setString(2, pswMD5);
			insert_statement.setString(3, udto.getName());
			insert_statement.setString(4, udto.getSurname());
			insert_statement.setString(5, udto.getBirthdate());
			insert_statement.setString(6, udto.getPhone());
			insert_statement.setString(7, this.genToken());
			flag = insert_statement.executeUpdate();
			result.close();
			insert_statement.close();
			conn.close();
		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
		return flag;
	}

	public int deleteUser(int userID) throws EcommerceException {
		int flag = 0;
		try {
			Connection conn = ds.getConnection();
			String sql = "delete from users where userID = ?";
			PreparedStatement delete_stmt = conn.prepareStatement(sql);
			delete_stmt.setInt(1, userID);
			flag = delete_stmt.executeUpdate();
			delete_stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
		return flag;
	}

	public int updateUserByID(UsersDto udto) throws EcommerceException {
		int flag = 0;
		try {
			Connection conn = ds.getConnection();
			PreparedStatement update_insert;
			String sql = "UPDATE users SET email = ?, name = ?, surname = ?, birthdate = ?, phone=? WHERE userID = ?";
			update_insert = conn.prepareStatement(sql);
			update_insert.setString(1, udto.getEmail());
			update_insert.setString(2, udto.getName());
			update_insert.setString(3, udto.getSurname());
			update_insert.setString(4, udto.getBirthdate());
			update_insert.setString(5, udto.getPhone());
			update_insert.setInt(6, udto.getUserID());
			flag = update_insert.executeUpdate();
			update_insert.close();
			conn.close();
		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
		return flag;
	}

	public int changeUserPassword(int userID, PasswordsDto passwords) throws EcommerceException {
		int flag = 0;
		String pswMD5 = null;
		if (!passwords.getNewP().equals(passwords.getConfP())) {
			System.out.println("Le password non coincidono");
		} else {
			try {
				Connection conn = ds.getConnection();
				String sql = "select md5(?) from dual";
				PreparedStatement insert_statement = conn.prepareStatement(sql);
				insert_statement.setString(1, passwords.getOldP());
				ResultSet result = insert_statement.executeQuery();
				while (result.next()) {
					pswMD5 = result.getString(1);
				}
				sql = "update users set password = md5(?) where userID = ? and password = ?";
				insert_statement = conn.prepareStatement(sql);
				insert_statement.setString(1, passwords.getNewP());
				insert_statement.setInt(2, userID);
				insert_statement.setString(3, pswMD5);
				flag = insert_statement.executeUpdate();
				if (flag != 0) {
					System.out.println("Password cambiata con successo");
				} else {
					System.out.println();
					throw new EcommerceException("Last password is wrong");
				}
				result.close();
				insert_statement.close();
				conn.close();

			} catch (SQLException e) {
				throw new EcommerceException(e.getMessage());
			}
		}
		return flag;
	}

	private String genToken() {
		String s =RandomStringUtils.randomAlphanumeric(45);
		System.out.println(s);
		return s;
	}

	public void checkToken(String token) throws EcommerceException {
		if (token == null) {
			throw new EcommerceException("Token is missing");
		}

		Connection conn;
		try {
			conn = ds.getConnection();
			String sql = "select * from users where token = ?";
			PreparedStatement select_stmt = conn.prepareStatement(sql);
			select_stmt.setString(1, token);
			ResultSet resultToken = select_stmt.executeQuery();
			if (resultToken.next()) {
				resultToken.close();
				select_stmt.close();
				conn.close();
			} else {
				resultToken.close();
				select_stmt.close();
				conn.close();
				throw new EcommerceException("Token incorrect");
			}
		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
	}

	public int resetPassword(UsersDto udto) throws EcommerceException {
		Connection conn;
		SendEmail se = new SendEmail();
		int flag=0;
		String sql;
		try {
			conn = ds.getConnection();
			sql = "select email from users where email = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, udto.getEmail());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				flag++;
				se.mail(rs.getString(1));
			}
			rs.close();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
		return flag;
	}
	
	public List<UsersDto> usersCSV() throws EcommerceException {

		List<UsersDto> usersReg = new ArrayList<>();

		PrintWriter pw = null;

		try {
			Connection conn = ds.getConnection();
			String sql = "select  * from users";
			PreparedStatement select_stmt = conn.prepareStatement(sql);
			ResultSet result = select_stmt.executeQuery(sql);
			
			try {
			    pw = new PrintWriter(new File("usersAnagrafica.csv"));
			} catch (FileNotFoundException e) {
				throw new EcommerceException(e.getMessage());
			}

			while (result.next()) {
				StringBuilder builder = new StringBuilder();
				String ColumnNamesList = "userID, email, password, name, surname, birthdate, phone, token";
				// No need give the headers Like: id, Name on builder.append
				builder.append(ColumnNamesList +"\n");
				builder.append(result.getInt(1) + ",");
				builder.append(result.getString(2) + ",");
				builder.append(result.getString(3) + ",");
				builder.append(result.getString(4) + ",");
				builder.append(result.getString(5) + ",");
				builder.append(result.getString(6) + ",");
				builder.append(result.getString(7) + ",");
				builder.append(result.getString(8) + ",");
				pw.write(builder.toString());
				
				
				int userID = result.getInt(1);
				String email = result.getString(2);
				String password = result.getString(3);
				String name = result.getString(4);
				String surname = result.getString(5);
				String birthdate = result.getString(6);
				String phone = result.getString(7);
				String token = result.getString(8);
				usersReg.add(new UsersDto(userID, email, password, name, surname, birthdate, phone, token));
			}
			result.close();
			select_stmt.close();
			conn.close();
			pw.close();
		} catch (SQLException e) {
			throw new EcommerceException(e.getMessage());
		}
		return usersReg;
	}

	public int login(UsersDto udto) throws EcommerceException{
		
		Connection conn;
		int flag = 0;
		try {
			conn = ds.getConnection();
			String sql = "select * from users where password = md5(?) and email = ?";
			PreparedStatement select_stmt = conn.prepareStatement(sql);
			select_stmt.setString(1, udto.getPassword());
			select_stmt.setString(2, udto.getEmail());
			ResultSet resultToken = select_stmt.executeQuery();
			if(resultToken.next()) {
				sql = "update users SET token = ? where email = ?";
				select_stmt = conn.prepareStatement(sql);
				select_stmt.setString(1, this.genToken());
				select_stmt.setString(2, udto.getEmail());
				flag = select_stmt.executeUpdate();
			}
			resultToken.close();
			select_stmt.close();
			conn.close();
		}catch (SQLException e){
			throw new EcommerceException("User non trovato");
		}
		return flag;
	}
	
}
