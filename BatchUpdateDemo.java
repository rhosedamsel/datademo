import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BatchUpdateDemo extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    private Connection connection;

    public BatchUpdateDemo() {
        setTitle("Batch Update Demo");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JButton btnWithoutBatch = new JButton("Without Batch");
        JButton btnWithBatch = new JButton("With Batch");
        panel.add(btnWithoutBatch);
        panel.add(btnWithBatch);
        add(panel);

        btnWithoutBatch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertRecordsWithoutBatch();
            }
        });

        btnWithBatch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertRecordsWithBatch();
            }
        });
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertRecordsWithoutBatch() {
        connectToDatabase();
        long startTime = System.currentTimeMillis();
        try {
            Statement statement = connection.createStatement();
            for (int i = 0; i < 1000; i++) {
                double num1 = Math.random();
                double num2 = Math.random();
                double num3 = Math.random();
                String sql = String.format("INSERT INTO Temp VALUES (%f, %f, %f)", num1, num2, num3);
                statement.executeUpdate(sql);
            }
            long endTime = System.currentTimeMillis();
            JOptionPane.showMessageDialog(this, "Records inserted without batch in " + (endTime - startTime) + " milliseconds.");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertRecordsWithBatch() {
        connectToDatabase();
        long startTime = System.currentTimeMillis();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Temp VALUES (?, ?, ?)");
            for (int i = 0; i < 1000; i++) {
                double num1 = Math.random();
                double num2 = Math.random();
                double num3 = Math.random();
                preparedStatement.setDouble(1, num1);
                preparedStatement.setDouble(2, num2);
                preparedStatement.setDouble(3, num3);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            long endTime = System.currentTimeMillis();
            JOptionPane.showMessageDialog(this, "Records inserted with batch in " + (endTime - startTime) + " milliseconds.");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BatchUpdateDemo().setVisible(true);
            }
        });
    }
}
