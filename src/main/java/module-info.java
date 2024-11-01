module com.example.HTMLEditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.apache.pdfbox;


    opens com.example.HTMLEditor to javafx.fxml;
    exports com.example.HTMLEditor;
}