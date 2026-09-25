package sample;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;

public class Snovio extends AnchorPane implements Initializable {

    private Main application;
    String countOfEmailsForSelectedDomains = "";
    ObservableList<String> emailAddresses = FXCollections.observableArrayList();

    @FXML
    org.controlsfx.control.CheckListView listView;

    @FXML
    ComboBox cboDomain;

    @FXML
    CheckBox cboxEmailCount;

    @FXML
    CheckBox cboxEmailDomain;

    @FXML
    CheckBox cboxEmailUserInfo;

    public void setApp(Main application) {
        this.application = application;
    }

    public void prepare(ObservableList selectedDomains) {
        listView.setItems(selectedDomains);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void GoBack(ActionEvent event) {
        application.gotoMain();
    }

    @FXML
    private void CheckAll(ActionEvent event) {
        System.out.println("Check All Function");
        listView.getCheckModel().checkAll();
    }

    @FXML
    private void UncheckAll(ActionEvent event) {
        System.out.println("Uncheck All Function");
        listView.getCheckModel().clearChecks();
    }

    @FXML
    private void GetCountOfEmailsForSelectedDomains() {
        listView.getCheckModel().getCheckedItems().forEach((item) -> {
            String selectedDomain = item.toString();
            Snovio_API snovio = new Snovio_API();
            String count = snovio.getEmailCount(selectedDomain);
            System.out.println(count);
            countOfEmailsForSelectedDomains += "" + count + "\n";
        });
    }

    @FXML
    private void GetEmailAddresses() {
        listView.getCheckModel().getCheckedItems().forEach((item) -> {
            String selectedDomain = item.toString();
            Snovio_API snovio = new Snovio_API();
            String domainSrc = snovio.getDomainSearch(selectedDomain);
            System.out.println(domainSrc);
            if (domainSrc != null && domainSrc.length() > 0) {
                String domainSrcArr[] = domainSrc.split("\n");
                for (String domainStr : domainSrcArr) {
                    if (domainStr != null && domainStr.length() > 0) {
                        emailAddresses.add(domainStr.replaceAll("null", ""));
                    }
                }
            }
        });
    }

    @FXML
    private void GetUserInfoFromEmailAddress(String selectedDomain) {
        //
        //listView.getCheckModel().getCheckedItems().forEach((item) -> {
        //String domain = item.toString();
        String email = "";
        Snovio_API snovio = new Snovio_API();
        snovio.getProfileWithEmail(email);
        //});
    }

    @FXML
    private void GetSnovioData(ActionEvent event) {
        countOfEmailsForSelectedDomains = "";
        emailAddresses = FXCollections.observableArrayList();
        if (cboxEmailCount.isSelected()) {
            GetCountOfEmailsForSelectedDomains();
        }
        if (cboxEmailDomain.isSelected()) {
            GetEmailAddresses();
        }
    }

    @FXML
    private void ReturnHome(ActionEvent event) {
        application.gotoMain();
    }

    @FXML
    private void HitSnovioAPI(ActionEvent event) {
        GetSnovioData(event);
        application.gotoSnovioDisplay(countOfEmailsForSelectedDomains, emailAddresses);
    }
}
