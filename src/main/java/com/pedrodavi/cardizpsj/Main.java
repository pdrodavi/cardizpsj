package com.pedrodavi.cardizpsj;

import java.io.*;
import java.sql.*;

import static com.pedrodavi.cardizpsj.PDFGen.generatePDF;
import static com.pedrodavi.cardizpsj.PDFGen.shortName;

public class Main {
    public static void main(String[] args) {

        String databaseURL = "jdbc:ucanaccess://D://restore//dizimistas.accdb";

        try (Connection connection = DriverManager.getConnection(databaseURL)) {

            String sql = "SELECT * FROM Dizimistas";

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            String cod = "";
            String nome = "";

            while (result.next()) {

                int id = result.getInt("COD");
                if (cod.isBlank()) {
                    cod = String.valueOf(id);
                }
                String fullname = result.getString("NOME");
                if (nome.isBlank()) {
                    nome = fullname;
                }

                shortName(fullname);

                File dir = new File("C:\\Users\\pedro\\IdeaProjects\\cardizpsj\\output\\");
                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    assert files != null;
                    for (File fl : files) {
                        String fileName = fl.getName();
                        if (fileName.contains(fullname)) {
                            boolean deleted = fl.delete();
//                            if (deleted) System.out.println("Arquivo excluido: " + fileName);
//                            else System.out.println("Erro ao tentar excluir o arquivo " + fileName);
                        }
                    }
                }

                byte[] bytes = generatePDF(String.valueOf(id), fullname);
                String nameFile = String.valueOf(id) + " - " + fullname + ".pdf";

                OutputStream out = new FileOutputStream("C:\\Users\\pedro\\IdeaProjects\\cardizpsj\\output\\" + nameFile);
                out.write(bytes);
                out.close();

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}