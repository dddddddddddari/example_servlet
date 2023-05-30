package com.example_servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String url = "jdbc:h2:file:/Users/user/IdeaProjects/db/Files";
    private static final String user = "root";
    private static final String password = "root";

    private static Connection con;
    private static Statement stmt;
    private static Statement stm;
    private static ResultSet rs;
    private static PreparedStatement deleteTableStm;
    private static PreparedStatement statement;

    public static void main(String[] args) {

    }
    public static void countWords(String path) throws IOException, SQLException {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            System.err.println("Нет такой директории" + path);
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                countWords(file.getPath());
            } else if (file.getName().endsWith(".txt")) {
                Map<String, Integer> wordCount = new HashMap<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split("\\s+");
                        for (String word : words) {
                            if (!word.isEmpty()) {
                                int count = wordCount.getOrDefault(word.toLowerCase(), 0) + 1;
                                wordCount.put(word.toLowerCase(), count);
                            }
                        }
                    }
                }
                for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                    PreparedStatement stm = con.prepareStatement("insert into FILES values (?,?,?)");
                    stm.setString(1, file.getPath());
                    stm.setString(2, entry.getKey());
                    stm.setInt(3, entry.getValue());
                    stm.executeUpdate();
                }
            }
        }
    }

    public static String GetDictionary(String keyword) throws SQLException {
        String result = null;
        InitDataBase();
        statement = con.prepareStatement("Select file_location, number_of_words, word From files Where word = ? Order by number_of_words");
        statement.setString(1, keyword);

        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            result = rs.getObject(1).toString() + " " + rs.getObject(2).toString() + " " + rs.getObject(3).toString();
            //result.add(rs.getObject(2).toString());
            //result.add(rs.getObject(3).toString());
        }
        return result;
    }

    public static void InitDataBase()
    {
        try{
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            stm = con.prepareStatement("insert into FILES values (?,?,?)");
            countWords("C://Users//User//IdeaProjects");

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //try { stmt.close();} catch (SQLException se) {}
            //try { rs.close();} catch (SQLException se) {}
        }
    }
}