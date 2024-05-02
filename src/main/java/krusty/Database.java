package krusty;

import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {

	private Connection connection;
	/**
	 * Modify it to fit your environment and then use this string when connecting to
	 * your database!
	 */
	private static final String jdbcString = "jdbc:mysql://localhost/krusty?serverTimezone=Europe/Stockholm";

	// For use with MySQL or PostgreSQL
	private static final String jdbcUsername = "root";
	private static final String jdbcPassword = "root";

	public void connect() {
		try {
			connection = DriverManager.getConnection(jdbcString, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getCustomers(Request req, Response res) {
		String sqlQuery = "SELECT name, address FROM customers";
		this.connect();
		try (PreparedStatement statement = connection.prepareStatement(sqlQuery);
				ResultSet resultSet = statement.executeQuery()) {
			return Jsonizer.toJson(resultSet, "customers");
		} catch (SQLException e) {
			System.err.println("Error getting customers: " + e.getMessage());
			return "{\"error\":\"Unable to retrieve customers\"}";
		}
	}

	public String getRawMaterials(Request req, Response res) {
		String sqlQuery = "SELECT name, amount, unit FROM `raw-materials`";
		this.connect();
		try (PreparedStatement statement = connection.prepareStatement(sqlQuery);
				ResultSet resultSet = statement.executeQuery()) {
			return Jsonizer.toJson(resultSet, "raw-materials");

		} catch (SQLException e) {
			System.err.println("Error getting raw-materials: " + e.getMessage());
			return "{\"error\":\"Unable to retrieve raw-materials\"}";
		}
	}

	public String getCookies(Request req, Response res) {
		String sqlQuery = "SELECT name FROM cookies";
		this.connect();
		try (PreparedStatement statement = connection.prepareStatement(sqlQuery);
				ResultSet resultSet = statement.executeQuery()) {
			return Jsonizer.toJson(resultSet, "cookies");
		} catch (SQLException e) {
			System.err.println("Error getting cookies: " + e.getMessage());
			return "{\"error\":\"Unable to retrieve cookies\"}";
		}
	}

	public String getRecipes(Request req, Response res) {
		String sqlQuery = "SELECT cookie, `raw-materials`, amount, unit FROM recipes";
		this.connect();
		try (PreparedStatement statement = connection.prepareStatement(sqlQuery);
				ResultSet resultSet = statement.executeQuery()) {
			return Jsonizer.toJson(resultSet, "recipes");
		} catch (SQLException e) {
			System.err.println("Error getting recipe: " + e.getMessage());
			return "{\"error\":\"Unable to retrieve recipe\"}";
		}
	}

	public String getPallets(Request req, Response res) {

		
		StringBuilder sqlQuery = new StringBuilder(
				"SELECT id, cookies AS cookie, production_date, customer_id AS customer, blocked FROM pallets WHERE 1=1");
		ArrayList<String> parameters = new ArrayList<>();
		if (req.queryParams("from") != null) {
			sqlQuery.append(" AND production_date >= ?");
			parameters.add(req.queryParams("from"));
		}
		if (req.queryParams("to") != null) {
			sqlQuery.append(" AND production_date <= ?");
			parameters.add(req.queryParams("to"));
		}
		if (req.queryParams("cookie") != null) {
			sqlQuery.append(" AND cookies = ?");
			parameters.add(req.queryParams("cookie"));
		}
		if (req.queryParams("blocked") != null) {
			sqlQuery.append(" AND blocked = ?");
			parameters.add(req.queryParams("blocked"));
		}
		this.connect();

		try (PreparedStatement statement = this.connection.prepareStatement(sqlQuery.toString())) {
			for (int i = 0; i < parameters.size(); i++) {
				statement.setString(i + 1, parameters.get(i));
			}
			ResultSet resultSet = statement.executeQuery();
			return Jsonizer.toJson(resultSet, "pallets");
		} catch (SQLException e) {
			System.err.println("Error getting pallet: " + e.getMessage());
			return "{\"error\":\"Unable to retrieve pallet\"}";
		}

	}

	public String reset(Request req, Response res) {
		this.connect();
		// Start transaction
		try {
			connection.setAutoCommit(false);

			// Delete data from tables
			connection.prepareStatement("DELETE FROM `pallets`").executeUpdate();
			connection.prepareStatement("DELETE FROM `cookies`").executeUpdate();
			connection.prepareStatement("DELETE FROM `customers`").executeUpdate();
			connection.prepareStatement("DELETE FROM `raw-materials`").executeUpdate();
			connection.prepareStatement("DELETE FROM `recipes`").executeUpdate();

			// Reset auto increment if necessary
			connection.prepareStatement("ALTER TABLE `customers` AUTO_INCREMENT = 1").executeUpdate();
			connection.prepareStatement("ALTER TABLE `raw-materials` AUTO_INCREMENT = 1").executeUpdate();
			connection.prepareStatement("ALTER TABLE `cookies` AUTO_INCREMENT = 1").executeUpdate();
			connection.prepareStatement("ALTER TABLE `recipes` AUTO_INCREMENT = 1").executeUpdate();
			connection.prepareStatement("ALTER TABLE `pallets` AUTO_INCREMENT = 1").executeUpdate();

			// Reinsert initial data (example for customers)
			connection.prepareStatement(
					"INSERT INTO customers (name, address) VALUES " +
							"('Bjudkakor AB', 'Ystad'), " +
							"('Finkakor AB', 'Helsingborg'), " +
							"('Gästkakor AB', 'Hässleholm'), " +
							"('Kaffebröd AB', 'Landskrona'), " +
							"('Kalaskakor AB', 'Trelleborg'), " +
							"('Partykakor AB', 'Kristianstad'), " +
							"('Skånekakor AB', 'Perstorp'), " +
							"('Småbröd AB', 'Malmö')")
					.executeUpdate();

			connection.prepareStatement(
					"INSERT INTO cookies (name) VALUES " +
							"('Almond delight'), " +
							"('Amneris'), " +
							"('Berliner'), " +
							"('Nut cookie'), " +
							"('Nut ring'), " +
							"('Tango')")
					.executeUpdate();

			connection.prepareStatement(
					"INSERT INTO `raw-materials` (name, amount, unit) VALUES " +
							"('Bread crumbs', 500000, 'g'), " +
							"('Butter', 500000, 'g'), " +
							"('Chocolate', 500000, 'g'), " +
							"('Chopped almonds', 500000, 'g'), " +
							"('Cinnamon', 500000, 'g'), " +
							"('Egg whites', 500000, 'ml'), " +
							"('Eggs', 500000, 'g'), " +
							"('Fine-ground nuts', 500000, 'g'), " +
							"('Flour', 500000, 'g'), " +
							"('Ground, roasted nuts', 500000, 'g'), " +
							"('Icing sugar', 500000, 'g'), " +
							"('Marzipan', 500000, 'g'), " +
							"('Potato starch', 500000, 'g'), " +
							"('Roasted, chopped nuts', 500000, 'g'), " +
							"('Sodium bicarbonate', 500000, 'g'), " +
							"('Sugar', 500000, 'g'), " +
							"('Vanilla sugar', 500000, 'g'), " +
							"('Vanilla', 500000, 'g'), " +
							"('Wheat flour', 500000, 'g')")
					.executeUpdate();

			connection.prepareStatement(
					"INSERT INTO recipes (cookieName, raw_materialName, amount) VALUES " +
							"('Almond delight', 'Butter', 400), " +
							"('Almond delight', 'Chopped almonds', 279), " +
							"('Almond delight', 'Cinnamon', 10), " +
							"('Almond delight', 'Flour', 400), " +
							"('Almond delight', 'Sugar', 270), " +
							"('Amneris','Butter', 250), " +
							"('Amneris','Eggs', 250), " +
							"('Amneris','Marzipan', 750), " +
							"('Amneris','Potato starch', 25), " +
							"('Amneris','Wheat flour', 25), " +
							"('Berliner', 'Butter', 250), " +
							"('Berliner', 'Chocolate', 50), " +
							"('Berliner', 'Eggs', 50), " +
							"('Berliner', 'Flour', 350), " +
							"('Berliner', 'Icing sugar', 100), " +
							"('Berliner', 'Vanilla sugar', 5), " +
							"('Nut cookie', 'Bread crumbs', 125), " +
							"('Nut cookie', 'Chocolate', 50), " +
							"('Nut cookie', 'Egg whites', 350), " +
							"('Nut cookie', 'Fine-ground nuts', 750), " +
							"('Nut cookie', 'Ground, roasted nuts', 625), " +
							"('Nut cookie', 'Sugar', 375), " +
							"('Nut ring', 'Butter', 450), " +
							"('Nut ring', 'Flour', 450), " +
							"('Nut ring', 'Icing sugar', 190), " +
							"('Nut ring', 'Roasted, chopped nuts', 225), " +
							"('Tango', 'Butter', 200), " +
							"('Tango', 'Flour', 300), " +
							"('Tango', 'Sodium bicarbonate', 4), " +
							"('Tango', 'Sugar', 250), " +
							"('Tango', 'Vanilla', 2)")
					.executeUpdate();

			connection.commit();
			return "{\"Status\": \"Database succesfully reset.\"}";
		} catch (SQLException e) {
			System.err.println("Error resetting database: " + e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException ex) {
				System.err.println("Transaction rollback failed: " + ex.getMessage());
			}
			return "{\"error\":\"Unable to reset database\"}";
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Error setting auto-commit: " + e.getMessage());
			}
		}
	}

	public String createPallet(Request req, Response res) {
		String cookie = req.queryParams("cookie");
		if (cookie == null) {
			return "{\"error\": \"Cookies parameter missing\"}";
		}

		this.connect();
		try {
			String insertSql = "INSERT INTO pallets (cookies, production_date, blocked) VALUES (?, NOW(), 'no')";
			try (PreparedStatement statement = connection.prepareStatement(insertSql,
					Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, cookie);

				int affectedRows = statement.executeUpdate();

				if (affectedRows == 0) {
					throw new SQLException("Creating pallet failed, no rows affected.");
				}

				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						long id = generatedKeys.getLong(1);
						updateIngredients(cookie);
						return "{\"status\": \"ok\", \"id\": " + id + "}";

					} else {
						throw new SQLException("Creating pallet failed, no ID obtained.");
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error creating pallet: " + e.getMessage());
			return "{\"error\":\"Unable to create pallet\"}";
		}
	}

	private void updateIngredients(String cookie) {
		HashMap<String, Integer> Ingredients = new HashMap<>();

		try {
			connection.setAutoCommit(false);

			String sql = "SELECT `raw-materials`.name, `raw-materials`.amount as rmamount, recipes.amount\n" + "FROM `raw-materials`\n"
					+ "INNER JOIN recipes on recipes.raw_materialName = `raw-materials`.name\n"
					+ "INNER JOIN cookies on recipes.cookieName = cookies.name\n"
					+ "WHERE cookies.name = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, cookie);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String ingredient = rs.getString("name");
				int recipeAmount = rs.getInt("recipes.amount");
				int rawAmount = rs.getInt("rmamount");

				int newAmount = rawAmount - (recipeAmount * 54);
				Ingredients.put(ingredient, newAmount); // Put in new total amount
			}

			for (Map.Entry<String, Integer> entry : Ingredients.entrySet()) {
				sql = "UPDATE `raw-materials` SET `raw-materials`.amount = ? WHERE name = ?";
				ps = connection.prepareStatement(sql);
				ps.setInt(1, entry.getValue());
				ps.setString(2, entry.getKey());
				ps.executeUpdate();
				ps.close();
			}
			connection.commit();
		} catch (SQLException e) {
			System.err.println("Error updating ingredients: " + e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException ex) {
				System.err.println("Transaction rollback failed: " + ex.getMessage());
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Error setting auto-commit: " + e.getMessage());
			}
		}
	}

}
