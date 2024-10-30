package com.pedrodavi.cardizpsj;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;

import static com.pedrodavi.cardizpsj.PDFGen.generatePDF;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Path currentDirectoryPath = FileSystems.getDefault().getPath("");
        String currentDirectoryName = currentDirectoryPath.toAbsolutePath().toString();
        String dirOutput = currentDirectoryName.concat("\\output\\");
        String dirOutputPdfs = makeDir(dirOutput + "\\" + String.valueOf(LocalDate.now().getYear()));
        dirOutput = dirOutput.concat(dirOutputPdfs + "\\");

        String databaseURL = "jdbc:ucanaccess://dizimistas.accdb";

//        JOptionPane meuJOPane = new JOptionPane("Teste");//instanciando o JOptionPane
//        final JDialog dialog = meuJOPane.createDialog(null, "test");//aqui uso um JDialog para manipular
//        //meu JOptionPane
//        dialog.setModal(true);
//        //Usando o javax.swing.Timer para poder gerar um evento em um tempo determinado
//        //Veja o construtor da classe Timer para mais explicações
//        Timer timer = new Timer(2 * 1000, new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                dialog.dispose();  //o evento(no caso fechar o meu JDialog)
//            }
//        });
//        timer.start();
//        dialog.setVisible(true);
//        timer.stop();

        int qtdTotal = 0;

        JFrame f = new JFrame("Geração de Carnês");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLocationRelativeTo(null);

        try (Connection connection = DriverManager.getConnection(databaseURL)) {

            String sql = "SELECT * FROM Dizimistas";
            String sqlCount = "SELECT Count(*) AS TotalDizimistas FROM Dizimistas";

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            ResultSet result2 = statement.executeQuery(sqlCount);

            while(result2.next()) {
                qtdTotal = result2.getInt("TotalDizimistas");
            }

            Container content = f.getContentPane();
            JProgressBar progressBar = new JProgressBar(1, qtdTotal);
            JLabel label = new JLabel();

            int qtdParc = 1;

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

                label.setText(nameFile);
                label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                progressBar.setValue(qtdParc);
                progressBar.setStringPainted(true);
                Border border = BorderFactory.createTitledBorder("Gerando " + qtdTotal + " carnês. Aguarde\n\n");
                progressBar.setBorder(border);
                content.add(progressBar, BorderLayout.NORTH);
                content.add(label,BorderLayout.CENTER);
                f.setSize(400, 100);
                f.setVisible(true);

                qtdParc++;

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        f.dispose();

        JFrame finished = new JFrame("Geração de Carnês");
        Container contentF = finished.getContentPane();
        finished.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        finished.setLocationRelativeTo(null);
        JLabel labelF = new JLabel();
        labelF.setText("Carnês em PDFS gerados com sucesso!");
        labelF.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        labelF.setHorizontalAlignment(SwingConstants.CENTER);
        contentF.add(labelF,BorderLayout.CENTER);
        finished.setSize(400, 100);
        finished.setVisible(true);
        Thread.sleep(3500);
        finished.dispose();

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