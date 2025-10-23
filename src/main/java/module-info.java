module com.example.realipcapmodel {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.realipcapmodel to javafx.fxml;
    exports com.example.realipcapmodel;
}