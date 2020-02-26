package ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import ecommerce.dto.CartDto;

@Stateless
public class CartDao {

	@Resource(lookup = "java:/jdbc/ecommerce")
	private DataSource ds;

	public List<CartDto> selectAll() {

		List<CartDto> cartReg = new ArrayList<>();

		try {

			Connection conn = ds.getConnection();
			String sql = "select  * from cart";
			PreparedStatement select_stmt = conn.prepareStatement(sql);
			ResultSet result = select_stmt.executeQuery();

			while (result.next()) {
				int cartID = result.getInt(1);
				int userID = result.getInt(2);
				int productID = result.getInt(3);

				cartReg.add(new CartDto(cartID, userID, productID));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cartReg;

	}

	public List<CartDto> selectById(String cond) {

		List<CartDto> cartReg = new ArrayList<>();

		try {

			Connection conn = ds.getConnection();
			String sql = "select  * from cart where cartID = ?";
			PreparedStatement select_stmt = conn.prepareStatement(sql);
			select_stmt.setString(1, cond);
			

			ResultSet result = select_stmt.executeQuery(sql);

			while (result.next()) {
				int cartID = result.getInt(1);
				int userID = result.getInt(2);
				int productID = result.getInt(3);

				cartReg.add(new CartDto(cartID, userID, productID));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cartReg;

	}

	public void insert(CartDto cdto) throws Exception {

		try {

			Connection conn = ds.getConnection();
			String sql = " insert into cart (userID, productID)" + " values (?, ?)";

			PreparedStatement insert_statement = conn.prepareStatement(sql);
			insert_statement.setInt(1, cdto.getUserID());
			insert_statement.setInt(2, cdto.getProductID());

			int rows_affected = insert_statement.executeUpdate();

			if (rows_affected == 0) {
				throw new Exception("Inserimento non valido");
			}
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(String cond) {

		try {

			Connection conn = ds.getConnection();
			String sql = "delete from cart where cartID = ?";

			PreparedStatement delete_stmt = conn.prepareStatement(sql);
			delete_stmt.setString(1, cond);
			delete_stmt.execute();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}