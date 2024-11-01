package com.example.HTMLEditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class HelloController {

    @FXML
    private HTMLEditor htmlEditor;
    @FXML
    private WebView webView;
    @FXML
    private TextArea textArea;
    @FXML
    private Button loadButton;
    @FXML
    private Label statusLabel;

    @FXML
    public void onOpen(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Document");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
        if (file != null) {
            try {
                loadFile(file);
                statusLabel.setText("Файл загружен: " + file.getName());
            } catch (Exception e) {
                statusLabel.setText("Ошибка при загрузке файла: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadFile(File file) throws Exception {
        String filePath = file.toURI().toString();
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);

        textArea.clear();

        if ("html".equalsIgnoreCase(extension)) {
            loadHtml(file);
        } else if ("txt".equalsIgnoreCase(extension)) {
            loadText(file);
        } else if ("pdf".equalsIgnoreCase(extension)) {
            loadPdf(file);
        } else {
            statusLabel.setText("Неподдерживаемый формат файла: " + extension);
            return;
        }

        htmlEditor.setHtmlText(textArea.getText());
        webView.getEngine().loadContent(htmlEditor.getHtmlText());
    }

    private void loadHtml(File file) throws IOException {
        String content = readFile(file, StandardCharsets.UTF_8.name());
        textArea.setText(content);
    }

    private void loadText(File file) throws IOException {
        String content = readFile(file, StandardCharsets.UTF_8.name());
        textArea.setText(content);
    }

    private void loadPdf(File file) throws Exception {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        textArea.setText(text);
        document.close();
    }

    private String readFile(File file, String encoding) throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding)) {
            int data = reader.read();
            while (data != -1) {
                content.append((char) data);
                data = reader.read();
            }
        }
        return content.toString();
    }

    @FXML
    public void onChange(KeyEvent keyEvent) {
        textArea.setText(htmlEditor.getHtmlText());
        webView.getEngine().loadContent(htmlEditor.getHtmlText());
    }

    @FXML
    public void onSaveHtml(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save HTML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html"));

        File file = fileChooser.showSaveDialog(loadButton.getScene().getWindow());
        if (file != null) {
            try {
                Files.writeString(file.toPath(), htmlEditor.getHtmlText(), StandardCharsets.UTF_8);
                statusLabel.setText("HTML сохранен: " + file.getName());
            } catch (IOException e) {
                statusLabel.setText("Ошибка при сохранении файла: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}