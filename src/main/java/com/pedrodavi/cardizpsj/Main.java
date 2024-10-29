package com.pedrodavi.cardizpsj;

import javax.swing.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;

import static com.pedrodavi.cardizpsj.PDFGen.generatePDF;

public class Main {
    public static void main(String[] args) {

        Path currentDirectoryPath = FileSystems.getDefault().getPath("");
        String currentDirectoryName = currentDirectoryPath.toAbsolutePath().toString();
        String dirOutput = currentDirectoryName.concat("\\output\\");
        String dirOutputPdfs = makeDir(dirOutput + "\\" + String.valueOf(LocalDate.now().getYear()));
        dirOutput = dirOutput.concat(dirOutputPdfs + "\\");

        String databaseURL = "jdbc:ucanaccess://dizimistas.accdb";

        try (Connection connection = DriverManager.getConnection(databaseURL)) {

            String sql = "SELECT * FROM Dizimistas";

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            while (result.next()) {

                int id = result.getInt("COD");
                String fullname = result.getString("NOME");

                File dir = new File(dirOutput);
                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    assert files != null;
                    for (File fl : files) {
                        String fileName = fl.getName();
                        if (fileName.contains(fullname)) {
                            boolean deleted = fl.delete();
                            if (!deleted) JOptionPane.showMessageDialog(null, "Erro ao tentar excluir o arquivo " + fileName);
                        }
                    }
                }

                byte[] bytes = generatePDF(String.valueOf(id), fullname);
                String nameFile = String.valueOf(id) + " - " + fullname + " - " + String.valueOf(LocalDate.now().getYear()) + ".pdf";

                OutputStream out = new FileOutputStream(dirOutput + nameFile);
                out.write(bytes);
                out.close();

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JOptionPane.showMessageDialog(null, "PDFS Gerados com sucesso");

        try {
            Process exec = Runtime.getRuntime().exec("explorer.exe " + dirOutput);
            if (!exec.isAlive()) exec.destroy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String makeDir(String secondArg) {
        File theDir = new File(secondArg);
        if (!theDir.exists()) {
            boolean result = false;
            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                return theDir.getName();
            }
        }
        return theDir.getName();
    }
}