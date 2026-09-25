/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import org.controlsfx.control.CheckListView;
import org.xml.sax.SAXException;

public class KeywordGrabber extends AnchorPane implements Initializable {

    private static final String DEFAULT_LANG = "en";
    private boolean bProgress = false;
    private KWTool kwTool;
    private Main application;
    boolean checkedItem = false;

    @FXML
    TextField txtKeywordSource;
    @FXML
    Button btnProcess;
    @FXML
    Label lblStatus;
    @FXML
    CheckListView checkListView;

    public void setApp(Main application) {
        this.application = application;
    }

    public void main(String args[]) {
    }

    @FXML
    private void GetResults(ActionEvent e) {
        kwTool = new KWTool();
        String _inheritanceKws = "";
        final ObservableList<String> strings = FXCollections.observableArrayList();
        if (btnProcess.getText().equals("Get Keywords")) {
            String _keyword = txtKeywordSource.getText();
            if (_keyword != null && !_keyword.equals("")) {
                String[] tmpSplit = _keyword.split("\n");
                if (tmpSplit.length > 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Single Keyword");
                    alert.setHeaderText("Information");
                    alert.setContentText("Please input single keyword.");
                    alert.show();
                    return;
                } else {
                    boolean err1 = false;
                    bProgress = true;
                    btnProcess.setText("Stop Keyword");
                    checkListView.getItems().clear();

                    ArrayList<String> lstKwLevel1 = null;
                    try {
                        lstKwLevel1 = kwTool.fetchKeywords(_keyword, DEFAULT_LANG);
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(KeywordGrabber.class.getName()).log(Level.SEVERE, null, ex);
                        err1 = true;
                    } catch (SAXException ex) {
                        Logger.getLogger(KeywordGrabber.class.getName()).log(Level.SEVERE, null, ex);
                        err1 = true;
                    } catch (IOException ex) {
                        Logger.getLogger(KeywordGrabber.class.getName()).log(Level.SEVERE, null, ex);
                        err1 = true;
                    }

                    TextField txtPref = new TextField();

                    if (lstKwLevel1 != null && !err1) {
                        if (lstKwLevel1.size() > 0) {
                            String mKwLvl1 = "";
                            for (String lvl1 : lstKwLevel1) {
                                mKwLvl1 += lvl1.trim() + "\n";
                            }
                            _inheritanceKws = mKwLvl1;

                            //lblStatus.setText("Fetch first level keyword..");
                            ArrayList<String> lstKw = kwTool.getListKeyword(txtPref.getText(), _inheritanceKws); //. note-hirin
                            if (lstKw.size() > 0) {
                                String mKw = "";
                                for (String szKw : lstKw) {
                                    mKw += szKw.trim() + "\n";
                                }
                                _inheritanceKws = mKw;
                            }

                            for (int i = 0; i < lstKw.size(); i++) {
                                String _curKw = lstKw.get(i);
                                ArrayList<String> lstKwResult = new ArrayList<>();
                                try {
                                    lstKwResult = kwTool.fetchKeywords(_curKw, DEFAULT_LANG);
                                } catch (ParserConfigurationException ex) {
                                    Logger.getLogger(KeywordGrabber.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (SAXException ex) {
                                    Logger.getLogger(KeywordGrabber.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(KeywordGrabber.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                //Dedupe the list
                                List<String> newList = lstKwResult.stream()
                                        .distinct()
                                        .collect(Collectors.toList());

                                for (String _kwRes : newList) {
                                    strings.add(_kwRes);
                                }
                                checkListView.setItems(strings);
                            }
                        } else {
                        }
                    } else {
                    }
                }
            } else {
            }
            bProgress = false;
            btnProcess.setText("Get Keywords");
        } else {
            bProgress = false;
            btnProcess.setText("Get Keywords");
        }
        RemoveDuplicate();
    }

    
    private void RemoveDuplicate() {
        System.out.println("Remove Duplicate");
        if (checkListView.getItems().size() > 0) {
            try {
                List<String> searchLines = new ArrayList<>();

                checkListView.getItems().forEach((item) -> {
                    searchLines.add("" + item);

                });
                Set<String> searchLinesDupRem = new LinkedHashSet<String>(searchLines);
                ObservableList names = FXCollections.observableArrayList();
                for (String searchLine : searchLinesDupRem) {
                    if (searchLine != null && searchLine.length() > 0) {
                        names.add(searchLine);
                    }
                }
                checkListView.setItems(names);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("There is no record to remove duplocate!");
        }
    }
    
    @FXML
    private void GoHome(ActionEvent e) {
        if (checkListView.getCheckModel().getCheckedIndices().size() == 1) {
            for (Object szKeyword : checkListView.getCheckModel().getCheckedItems()) {
                System.out.println(szKeyword.toString());
                application.gotoMainParam(szKeyword.toString());
            }
        } else {
            //here we would inform user only can select one
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Multi-Select Not Allowed.");
            alert.setHeaderText("Warning");
            alert.setContentText("Please select only one keyword");
            alert.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void initialize() {
    }
}
