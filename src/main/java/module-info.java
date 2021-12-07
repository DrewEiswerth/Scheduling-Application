module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.SchedulingApplication to javafx.fxml;
    opens org.SchedulingApplication.Utilities to javafx.fxml;
    opens org.SchedulingApplication.Model to javafx.base;
    exports org.SchedulingApplication;
    exports org.SchedulingApplication.Utilities;
    exports org.SchedulingApplication.Model;
}
