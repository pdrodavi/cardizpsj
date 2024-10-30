package com.pedrodavi.cardizpsj;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;

public class PDFGen {

    public static byte[] generatePDF(String cod, String nome) {

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            PdfWriter writer = PdfWriter.getInstance(document, baos);

            document.setPageSize(PageSize.A4);
            document.setMargins(0, 0, 10, 10);
            document.open();

//            Rectangle rect = new Rectangle(10, 10, document.getPageSize().getWidth() - 10, document.getPageSize().getHeight() - 10);
            Rectangle rect = new Rectangle(0, 10, 0, document.getPageSize().getHeight() - 10);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(1);
            writer.setBoxSize("rectangle", rect);
            document.add(rect);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100); // Define a largura da tabela para 100% do documento

            int month = 1;

            for (int i = month; i <= 4; i++) {

                PdfPCell lineTop = new PdfPCell(new Phrase(""));
                lineTop.setHorizontalAlignment(Element.ALIGN_CENTER);
                lineTop.setBorder(Rectangle.TOP);
                table.addCell(lineTop);
                table.addCell(lineTop);
                table.addCell(lineTop);

                PdfPCell lineRight = new PdfPCell(new Phrase(""));
                lineRight.setHorizontalAlignment(Element.ALIGN_CENTER);
                lineRight.setBorder(Rectangle.RIGHT);
                table.addCell(lineRight);
                table.addCell(lineRight);
                table.addCell(lineRight);

                PdfPCell nomeParoquiaTitle = new PdfPCell(new Paragraph("Paróquia São José\nPaulista-PB", boldCustom(11)));
                nomeParoquiaTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
                nomeParoquiaTitle.setBorder(Rectangle.RIGHT);
                nomeParoquiaTitle.setPaddingTop(15);
                table.addCell(nomeParoquiaTitle);
                table.addCell(nomeParoquiaTitle);
                table.addCell(nomeParoquiaTitle);

                table.addCell(getPdfPCell(cod, month));
                month++;
                table.addCell(getPdfPCell(cod, month));
                month++;
                table.addCell(getPdfPCell(cod, month));
                month++;

                PdfPCell nomeDizimista = new PdfPCell(new Phrase(shortName(nome.toUpperCase()), boldCustom(9)));
                nomeDizimista.setHorizontalAlignment(Element.ALIGN_LEFT);
                nomeDizimista.setBorder(Rectangle.RIGHT);
                nomeDizimista.setPaddingLeft(25);
                nomeDizimista.setPaddingTop(5);
                table.addCell(nomeDizimista);
                table.addCell(nomeDizimista);
                table.addCell(nomeDizimista);

                PdfPCell cellData = new PdfPCell(new Phrase("Data: ______/______/_______\n\nValor: ____________________\n\nAss.: ____________________", normalCustom(11)));
                cellData.setBorder(Rectangle.RIGHT);
                cellData.setPaddingTop(20);
                cellData.setPaddingLeft(25);
                cellData.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cellData);
                table.addCell(cellData);
                table.addCell(cellData);

                PdfPCell cellFooter = new PdfPCell(new Phrase("Dizimista consciente\nexpressa sua confiança em DEUS", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)));
                cellFooter.setBorder(Rectangle.RIGHT);
                cellFooter.setPaddingTop(10);
                cellFooter.setPaddingLeft(15);
                cellFooter.setPaddingBottom(20);
                cellFooter.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellFooter);
                table.addCell(cellFooter);
                table.addCell(cellFooter);

            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }

    private static PdfPCell getPdfPCell(String cod, int month) {
        String newMonth = String.valueOf(month);
        if (newMonth.length() == 1) newMonth = "0" + newMonth;
        PdfPCell codRef = new PdfPCell(new Phrase(cod +"                                 " + newMonth + " / " + String.valueOf(LocalDate.now().getYear()), boldCustomRed(11)));
                        codRef.setPaddingTop(10);
                        codRef.setPaddingLeft(25);
                codRef.setHorizontalAlignment(Element.ALIGN_LEFT);
                codRef.setBorder(Rectangle.RIGHT);
        return codRef;
    }

    private static Font boldCustom(int size) {
        return new Font(Font.FontFamily.HELVETICA, size, Font.BOLD);
    }

    private static Font boldCustomRed(int size) {
        return new Font(Font.FontFamily.HELVETICA, size, Font.BOLD, BaseColor.RED);
    }

    private static Font normalCustom(int size) {
        return new Font(Font.FontFamily.HELVETICA, size);
    }

    public static String shortName(String name) {

        String nomeFormatado = "";

        if (name.length() > 27) {

            List<String> preposicoes = new ArrayList<>();
            preposicoes.add("DE");
            preposicoes.add("DA");
            preposicoes.add("DAS");
            preposicoes.add("DOS");
            String[] nomes = name.toUpperCase().split(" ");

            if (nomes.length < 2) {
                return name;
            }

            if (nomes.length == 4) {

                boolean isPreposicao = isPreposicaoSubName(nomes, preposicoes);

                if (isPreposicao) nomeFormatado = tratSubNameComPrepo(nomes, preposicoes);
                else nomeFormatado = nomes[0]
                        + " " + nomes[1] + " "
                        + Arrays.stream(nomes)
                        .limit(nomes.length - 1)
                        .skip(2)
                        .filter(nome -> !preposicoes.contains(nome))
                        .map(nome -> nome.substring(0, 1) + ". ")
                        .collect(Collectors.joining())
                        + nomes[nomes.length - 1];

            } else {

                nomeFormatado = nomes[0]
                        + " " + nomes[1] + " "
                        + Arrays.stream(nomes)
                        .limit(nomes.length - 1)
                        .skip(2)
                        .filter(nome -> !preposicoes.contains(nome))
                        .map(PDFGen::verifyLenghtName)
                        .collect(Collectors.joining())
                        + nomes[nomes.length - 1];
            }

            return nomeFormatado;
        }

        return name;
    }

    private static boolean isPreposicaoSubName(String[] nomes, List<String> preposicoes) {

        String parte = nomes[nomes.length - 2].toUpperCase();
        boolean isPreposicao = false;

        for (String prep : preposicoes) {
            if (parte.equals(prep)) {
                isPreposicao = true;
                break;
            }
        }

        return isPreposicao;
    }

    private static String tratSubNameComPrepo(String[] nomes, List<String> preposicoes) {
        return nomes[0]
                + " "
                + Arrays.stream(nomes)
                .limit(nomes.length - 1)
                .skip(1)
                .filter(nome -> !preposicoes.contains(nome))
                .map(nome -> nome.substring(0, 1) + ". ")
                .collect(Collectors.joining())
                + nomes[nomes.length - 1];
    }

    private static String verifyLenghtName(String name) {
        if (name.length() <= 4) return name + " ";
        return name.substring(0, 1) + ". ";
    }

}
