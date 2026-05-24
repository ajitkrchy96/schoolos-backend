import java.sql.*;
String url = "jdbc:postgresql://localhost:5432/school_os?currentSchema=schoolos";
String user = "postgres";
String pass = "postgres";
Connection conn = DriverManager.getConnection(url, user, pass);
PreparedStatement ps1 = conn.prepareStatement("SELECT lower('%' || ? || '%')");
ps1.setString(1, "test");
ResultSet rs1 = ps1.executeQuery();
if (rs1.next()) {
    System.out.println("concat lower test: " + rs1.getString(1));
}
rs1.close(); ps1.close();
PreparedStatement ps2 = conn.prepareStatement("SELECT lower(first_name) FROM student WHERE school_id = ? AND status = 'ACTIVE' AND lower(first_name) LIKE lower('%' || ? || '%') LIMIT 1");
ps2.setLong(1, 1L);
ps2.setString(2, "test");
ResultSet rs2 = ps2.executeQuery();
System.out.println("student lower query executed successfully");
while (rs2.next()) {
    System.out.println("row: " + rs2.getString(1));
}
rs2.close(); ps2.close();
conn.close();
