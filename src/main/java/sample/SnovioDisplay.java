package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.controlsfx.control.CheckListView;

public class SnovioDisplay {

    @FXML
    CheckListView clvDomainEmails;
    @FXML
    TextArea txtAreaEmailCounts;
    private Main application;

    public void prepare(String countOfEmailsForSelectedDomains,ObservableList emailAddresses) {
        txtAreaEmailCounts.setText(countOfEmailsForSelectedDomains);
        clvDomainEmails.setItems(emailAddresses);
    }
    public void setApp(Main application) {
        this.application = application;
    }

    @FXML
    private void GoBack(ActionEvent event) {
        application.gotoSnovio();
    }

    @FXML
    private void CheckAll(ActionEvent event) {
        System.out.println("Check All Function");
        clvDomainEmails.getCheckModel().checkAll();
    }

    @FXML
    private void UncheckAll(ActionEvent event) {
        System.out.println("Uncheck All Function");
        clvDomainEmails.getCheckModel().clearChecks();
    }

    @FXML
    private void AddToSnovioList(ActionEvent event) {
        return;
    }
}
