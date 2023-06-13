package tkachgeek.config.yaml.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseConnection {
  String url = "jdbc:mysql://localhost:3306/db_name?useSSL=false";
  String user = "root";
  String password = "root";
  
  public DatabaseConnection() {
  }
  
  public String getUrl() {
    return url;
  }
  
  public String getUser() {
    return user;
  }
  
  public String getPassword() {
    return password;
  }
  
  public Optional<Connection> getConnection() {
    try {
      return Optional.ofNullable(DriverManager.getConnection(url, user, password));
    } catch (SQLException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }
}
