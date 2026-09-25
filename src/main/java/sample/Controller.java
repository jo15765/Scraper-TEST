package sample;

import java.awt.*;
import java.io.File;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;

import javax.swing.plaf.PanelUI;

public class Controller extends AnchorPane implements Initializable {

    Scene sceneSettings;
    public Stage primaryStage;
    private static Pattern patternDomainName;
    private Matcher matcher;
    private static final String DOMAIN__NAME__PATTERN = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";

    static {
        patternDomainName = Pattern.compile(DOMAIN__NAME__PATTERN);
    }
    private Task<Void> task;

    @FXML
    TextField txtSearchTerm;
    @FXML
    TextArea txtDomains;
    @FXML
    org.controlsfx.control.CheckListView listView;
    @FXML
    CheckBox cboxGoogle;
    @FXML
    CheckBox cboxYahoo;
    @FXML
    CheckBox cboxAOL;
    @FXML
    CheckBox cboxAsk;

    private Main application;

    public void setApp(Main application) {
        this.application = application;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void prepare(String keyword) {
        txtSearchTerm.setText(keyword);
        //adding the combo boxes and setting them to checked
        cboxYahoo.setSelected(true);
        cboxGoogle.setSelected(true);
        cboxAOL.setSelected(true);
        cboxAsk.setSelected(true);
        ObservableList<String> items = FXCollections.observableArrayList();
        listView.setItems(items);
        listView.setPrefWidth(350);
        listView.setPrefHeight(370);
    }

    public static void writeFile(List<String> dataLines, String fileName) throws IOException {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .forEach(pw::println);
        }
    }

    @FXML
    private void GoBack(ActionEvent event) {
        application.gotoKeywordGrabber();
    }
    @FXML
    private void RemoveDuplicate(ActionEvent event) {
        System.out.println("Remove Duplicate");
        if (listView.getItems().size() > 0) {
            try {
                List<String> searchLines = new ArrayList<>();

                listView.getItems().forEach((item) -> {
                    System.out.println("Stuff with " + item);
                    searchLines.add("" + item);

                });
                Set<String> searchLinesDupRem = new LinkedHashSet<String>(searchLines);
                ObservableList names = FXCollections.observableArrayList();
                for (String searchLine : searchLinesDupRem) {
                    if (searchLine != null && searchLine.length() > 0) {
                        System.out.println(searchLine);
                        names.add(searchLine);
                    }
                }
                listView.setItems(names);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("There is no record to remove duplocate!");
        }
    }

    @FXML
    private void SaveCSV(ActionEvent event) {
        System.out.println("Save CSV FILE");
        if (listView.getCheckModel().getCheckedItems().size() > 0) {
            try {
                List<String> searchLines = new ArrayList<>();

                listView.getCheckModel().getCheckedItems().forEach((item) -> {
                    System.out.println("Stuff with " + item);
                    searchLines.add("" + item);

                });
                String fileName = txtSearchTerm.getText();
                if (fileName == null || fileName.length() <= 0) {
                    fileName = "output";
                }
                writeFile(searchLines, System.getProperty("user.home") + "/Desktop/" + fileName + ".csv");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("There is no record to save!");
        }
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
    private void QuerySearchEngines(ActionEvent event) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                String searchingText = txtSearchTerm.getText();
                int pages = 1;
                txtDomains.clear();
                searchingText = searchingText.replace(' ', '+');
                Set<String> result = getDataFromGoogle(searchingText, pages);

                if (result != null) {
                    if (txtDomains != null) {
                        txtDomains.setText("\n=======End Processing=======\n");
                        txtDomains.setText(txtDomains.getText() + "Domains Found (" + (result.size() - 1) + ")");
                    }
                    result.removeAll(Collections.singleton(null));
                    result.removeAll(Collections.singleton(""));
                } else {
                    txtDomains.setText(txtDomains.getText() + "Domains NOT FOUND!");
                }

                //clearing the values already in the text area
//                txtDomains.clear();
                ObservableList<String> items = FXCollections.observableArrayList();
                Platform.runLater(() -> {
                    result.stream().forEach(res -> items.add(res));
                    listView.setItems(items);
                });

                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        th.setDaemon(true);
        th.start();
    }

    @FXML
    private void LoadNewScene(ActionEvent event) throws Exception {
        application.gotoSnovio(listView.getCheckModel().getCheckedItems());
    }

    public String getDomainName(String url) {
        String domainName = "";
        matcher = patternDomainName.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        return domainName;
    }

    private Set<String> getDataFromGoogle(String query, int pages) {
        String request = "";
        Set<String> result = new HashSet<String>();
        Platform.runLater(() -> {
            txtDomains.setText("\n=======Processing=======\n");
            txtDomains.setText(txtDomains.getText() + "* Sending request...\n");
        });
        for (int j = 0; j <= 3; j++) {
            String provider = "";
            String key = "";
            String providerUrl = "";
            String number = "";

            if (j == 0) {
                if (cboxGoogle.isSelected()) {
                    provider = "Google";
                    providerUrl = "https://www.google.com/search?";
                    key = "q=";
                    number = "&start=";
                }
            } else if (j == 1) {

                if (cboxYahoo.isSelected()) {
                    provider = "Yahoo";
                    providerUrl = "https://search.yahoo.com/search?";
                    key = "p=";
                    number = "&b=";
                }
            } else if (j == 2) {
                if (cboxAOL.isSelected()) {
                    provider = "AOL";
                    providerUrl = "https://search.aol.com/aol/search?";
                    key = "q=";
                    number = "&b=";
                }
            } else if (j == 3) {
                if (cboxAsk.isSelected()) {
                    provider = "ASK";
                    providerUrl = "https://www.ask.com/web?";
                    key = "q=";
                    number = "&page=";
                }
            }
            /////////////////////////////////////////////////////////
            for (int i = 0; i < pages; i++) {

                if (j == 3) {
                    request = providerUrl + key + query + number + (i + 1);
                } else {
                    request = providerUrl + key + query + number + i * 10;
                }
                try {
                    if (provider.length() > 0) {
                        System.out.println(provider + " : Fetching Results from page Number " + (i + 1));
                        Thread.sleep(4000);
                        Connection.Response res = Jsoup.connect(request)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                                .timeout(5000)
                                .execute();

                        Document doc = res.parse();

                        if (j == -1) {
                            Elements links = doc.select("h2>a");
                            for (Element link : links) {
                                String temp = link.attr("abs:href");
                                if (!temp.contains("angieslist") && !temp.contains("groupon") && !temp.contains("yelp") && !temp.contains("google") && !temp.contains("yellowpages")) {
                                    result.add(getDomainName(temp));
                                }
                            }
                        } else {
                            Elements links = doc.select("a[href]");
                            for (Element link : links) {
                                String temp = link.attr("href");
                                temp = URLDecoder.decode(temp, StandardCharsets.UTF_8.name());
                                temp = temp.replaceAll(".+RU=", "");
                                if (!temp.contains("angieslist") && !temp.contains("groupon") && !temp.contains("bing") && !temp.contains("google") && !temp.contains("yahoo") && !temp.contains("aol")) {
                                    result.add(getDomainName(temp));
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    //System.out.println("Error getting urls");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

}
