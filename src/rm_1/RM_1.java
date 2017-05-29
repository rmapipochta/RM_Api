/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rm_1;


import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import rm_1.redmineapi.RedmineManager;
import rm_1.redmineapi.RedmineManagerFactory;
import rm_1.redmineapi.bean.Issue;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import org.apache.http.HttpResponse;
import rm_1.redmineapi.RedmineException;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import rm_1.redmineapi.Include;
import rm_1.redmineapi.Params;
import rm_1.redmineapi.bean.IssueStatus;
import rm_1.redmineapi.bean.Journal;
import rm_1.redmineapi.bean.Project;
import rm_1.redmineapi.bean.TimeEntry;
import rm_1.redmineapi.bean.Tracker;

/**
 *
 * @author post
 */

public class RM_1 extends Application {
    private final Alert alert = new Alert(AlertType.INFORMATION);
    private static final Logger LOG = Logger.getLogger(RM_1.class.getName());
    private Handler handler;      
    private static final int ID_STATUS_IN_WORK=3;
    private static final int ID_STATUS_NEW=2;
    private static final int ID_STATUS_FINISHED=4;
    private static final int ID_STATUS_DEFER=30;
    private static final int ID_STATUS_VALUATION_SPENT_TIME=29;
    private static final int ID_ACTIVITY_DEVELOPMENT=11;
    private static final int ID_ACTIVITY_ENGINEERING=10;
    private static final String ICON_IMAGE_LOC =
            "http://icons.iconarchive.com/icons/scafer31000/bubble-circle-3/16/GameCenter-icon.png";
    private static final String PROGRAM_NAME= "QuestBook";
    private long timeStartInWork=0;//time when issue was started
    private int selectedIssue=0;//id of selected issue
    private String markedIssue="";//issue which marked to change color in table pf selected Issue
    private int issueIdSpentHours=0;//finished issue which need to enter spent hours
    private int idCurrentUser=0;//id of user who connected
    private String idProject ="";
    private String idSubproject ="";
    private Stage stage;//main stage of application
    private String urlIssue="";//url of selected issue
    // a timer allowing the tray icon to provide a periodic notification event.
    private Timer notificationTimer;// = new Timer();
    private Timer workTimer = new Timer();
    private Issue issueByIdWithDescription;//issue which selected to view description and comments
    private String uri="";//url of host
    private String apiAccessKey;//uses's key to connect to api
    private List<Issue> issues=null;//list of user's issues                 
    private List<Issue> issuesAll=null;//list of all user's issues                 
    private RedmineManager mgr;
    private final List<ObservableList<RowIssue>> rowIssueList=new ArrayList();
    private final TableView<RowIssue> table = new TableView<>();//table with issues
    private final TableColumn<RowIssue, String> idCol = new TableColumn("#");//row with id of issue
    private final TableColumn<RowIssue, String> statusCol = new TableColumn("Статус");//row with status
    private final TableColumn<RowIssue, String> priorityCol = new TableColumn("Приоритет");//row with priority
    private final TableColumn<RowIssue, String> themeCol = new TableColumn("");//row with theme
    private final TableColumn<RowIssue, String> spentHoursCol = new TableColumn("");//row with author
    private final TableColumn<RowIssue, String> priorityIdCol = new TableColumn("Приоритет номер");//row with priority id. need to sort
    private final ObservableList<RowIssue> rowList = FXCollections.observableArrayList(); 
    private final Stage description = new Stage();//stage to view description and comments
    private final VBox descriptionVbox = new VBox(20);
    private final Label descriptionText=new Label();
    private final Label descriptionComments=new Label();
    private final TextArea tfNewComment=new TextArea();
    private final Button addComment = new Button();
    private final ScrollPane commentsScroll = new ScrollPane();
    private final ScrollPane descriptionScroll = new ScrollPane();
    private final Stage dialog = new Stage();//stage to connect window
    private final VBox dialogVbox = new VBox(20);
    private final TextField tfHost=new TextField();
    private final TextField tfKey=new TextField();
    private final Label lbHost=new Label("Хост");
    private final Label lbKey=new Label("Ключ");
    private final Button connectButton = new Button();
    private final Stage trackers = new Stage();//stage to view trackers and statuses
    private final List<CheckBox> checkboxTrackers = new ArrayList();
    private final List<CheckBox> checkboxStatuses = new ArrayList();
    private List<IssueStatus> statusesList = null;
    private List<Tracker> trackersList = null;
    private final Button selectTrackersButton = new Button();
    private StackPane trackersStackPane;   
    private final Stage projectStage = new Stage();//stage to select project
    private final TextField tfSubproject = new TextField();
    private final ToggleGroup projectsToggleGroup = new ToggleGroup();
    private List<Project> projectsList = null;
    private final Button selectProjectButton = new Button();
    private final ScrollPane projectsScroll = new ScrollPane();
    private final VBox projectsVbox = new VBox(5);
    private final Label lbSubproject=new Label();
    private final VBox projectsToggleGroupVbox = new VBox(5);
    private Scene projectsScene;            
    private final Stage spentHours = new Stage();//stage to add spent hours
    private final VBox spentHoursVbox = new VBox(20);
    private final Label lbSpentHours=new Label();
    private final TextField tfSpentHours=new TextField();
    private final Button addSpentHours= new Button();
    private ConnectClass cc;  
    private final Image urlImage = new Image("images/url.png",30,30,false,false);
    private final Button urlButton = new Button("",new ImageView(urlImage));
    private final Image startImage = new Image("images/start.png",30,30,false,false);
    private final Button startButton = new Button("",new ImageView(startImage));
    private final Image finishImage = new Image("images/fin.png",30,30,false,false);
    private final Button finishButton = new Button("",new ImageView(finishImage));
    private final Image settingsImage = new Image("images/settings.png",30,30,false,false);
    private final Button settingsButton = new Button("",new ImageView(settingsImage));
    private final Image deferImage = new Image("images/time2.png",30,30,false,false);
    private final Button deferButton = new Button("",new ImageView(deferImage));
    private final Image trackerImage = new Image("images/info.png",30,30,false,false);
    private final Button trackerButton = new Button("",new ImageView(trackerImage));
    private final Image createImage = new Image("images/create.png",30,30,false,false);
    private final Button createButton = new Button("",new ImageView(createImage));
    private final Image projectsImage = new Image("images/project.png",30,30,false,false);
    private final Button projectsButton = new Button("",new ImageView(projectsImage));   
    private final Image pauseImage = new Image("images/pause.png",30,30,false,false);
    private final Label lbTime=new Label(""); 
    private final AnchorPane rootAnchorPane = new AnchorPane();
    private final TabPane tabs = new TabPane();
    private final Button addTabButton = new Button("+");
    private double sceneHeight=0,sceneWidth=0, sceneX=0, sceneY=0;
    private final Map<Integer,String> spentHoursMap = new HashMap<>();
    private String trackerIdString="";
    private String statusIdString="";
    private List<String> statusIdStringList=new ArrayList();
    private List<String> trackerIdStringList=new ArrayList();
    private List<String> nameTabsStringList=new ArrayList();
    private URL imageLoc;
    private java.awt.Image image;
    private java.awt.TrayIcon trayIcon;
      
      private void connectFunc() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException{
      if((!tfHost.getText().trim().isEmpty())&&(!tfKey.getText().trim().isEmpty()))
                    {
                        //allow to use this certificate
                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        trustStore.load(null, null);
                        
                        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                        
                        HttpParams params = new BasicHttpParams();
                        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                        
                        SchemeRegistry registry = new SchemeRegistry();
                        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                        registry.register(new Scheme("https", sf, 443));
                        
                        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
                        
                        HttpClient client = new DefaultHttpClient(ccm, params);
                        if((selectedIssue!=0)&&(timeStartInWork!=0)){
                            saveSpentHours();
                            
                            workTimer.cancel();
                            //clear counter
                                lbTime.setText("");
                            timeStartInWork=0;
                        }
                        uri = tfHost.getText();
                        apiAccessKey = tfKey.getText();
                        urlIssue=uri;
                     
                        cc=new ConnectClass(uri,apiAccessKey);
                        //save connection info to file
                    try {
                        FileOutputStream fos = new FileOutputStream("conn.out");
                            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                                oos.writeObject(cc);
                                oos.flush();
                            }
                    } catch (FileNotFoundException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
                        try{
                            //create connection
                        mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey,client);
                        idCurrentUser = mgr.getUserManager().getCurrentUser().getId();
                        setTableIssues();
                    
                     Params params2 = new Params()
                            .add("set_filter", "1")
                            .add("assigned_to_id", String.valueOf(idCurrentUser));//mgr.getUserManager().getCurrentUser().getId()));

                    try {
                        issuesAll = mgr.getIssueManager().getIssues(params2).getResults();
            
                    } catch (RedmineException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }

                  disableButtons(false);
                        }
                        catch(RedmineException ex){
                            //if connection failed disable buttons
                            disableButtons(true);
                        }
                        
                }
          
                    dialog.hide();                
      }
    private void addExtraSpentHours(String name){
       if((issueIdSpentHours!=0)&&(!name.trim().isEmpty())&&Float.parseFloat(name.replaceAll(",",".").replaceAll(":","."))>0)
       {
           TimeEntry TE=new TimeEntry();
           TE.setHours(Float.parseFloat(name.replaceAll(",",".").replaceAll(":",".")));
           TE.setIssueId(issueIdSpentHours);
           TE.setActivityId(ID_ACTIVITY_ENGINEERING); 
           try {
                mgr.getTimeEntryManager().createTimeEntry(TE);
               } catch (RedmineException ex) {
                   alert.setTitle("Внимание");
                   alert.setHeaderText(null);
                   alert.setContentText("Трудозатраты не удалось сохранить!");
                   alert.showAndWait();
                   LOG.addHandler(handler);
               LOG.log(Level.SEVERE, null, ex);
               }
           issueIdSpentHours=0;
       }
    }
    private void saveSpentHours(){
      //if issue selected and in work now
        if(selectedIssue!=0&&timeStartInWork!=0)
                   {
                       //get time
                        long allTime=System.currentTimeMillis()-timeStartInWork;
                        //if spent more than one minute 
                               if(TimeUnit.MILLISECONDS.toMinutes(allTime)>=1)
                               {
                           long hours=TimeUnit.MILLISECONDS.toHours(allTime);
                           long minutes=TimeUnit.MILLISECONDS.toMinutes(allTime)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(allTime));

                               int min=(int)Math.ceil((float)minutes/60*100);
                               String s=String.valueOf(hours)+"."+((min<10)?"0"+String.valueOf(min):String.valueOf(min));
                               Float f=Float.parseFloat(s);
                          //create timentry for selected issue and set spent time
                       TimeEntry TE=new TimeEntry();
                       TE.setHours(f);
                       TE.setIssueId(selectedIssue);
                       TE.setActivityId(ID_ACTIVITY_DEVELOPMENT);
                            try {
                                mgr.getTimeEntryManager().createTimeEntry(TE);
                                String spentHoursStringBuf=mgr.getIssueManager().getIssueById(selectedIssue).getSpentHours().toString();
                                spentHoursMap.replace(selectedIssue,spentHoursStringBuf);
                                for (int i = 0; i < table.getItems().size(); i++) {
                                   if (Integer.valueOf(table.getItems().get(i).getIdCol()) == selectedIssue) {
                                    //   rowList.get(i).setSpentHoursCol(spentHoursStringBuf);
                                       rowIssueList.get(tabs.getSelectionModel().getSelectedIndex()).get(i).setSpentHoursCol(spentHoursStringBuf);

                                       priorityIdCol.setSortType(TableColumn.SortType.DESCENDING);
                                     //  table.setItems(rowList);
                                       table.setItems(rowIssueList.get(tabs.getSelectionModel().getSelectedIndex()));
                                       table.getSortOrder().add(priorityIdCol);
                                       break;
                                   }
                               }
                           } catch (RedmineException ex) {
                               alert.setTitle("Внимание");
                               alert.setHeaderText(null);
                               alert.setContentText("Трудозатраты не удалось сохранить!");
                               alert.showAndWait();
                               LOG.addHandler(handler);
                                LOG.log(Level.SEVERE, null, ex);
                            }
                               }
                   }
    }
    //add aplication to tray
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            imageLoc = new URL(
                    ICON_IMAGE_LOC
            );
            image = ImageIO.read(imageLoc);
            trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));
           
                MouseListener ml;
                ml=new MouseListener() {
    
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
        
                    if ((e.getButton() == 1) && (e.getClickCount() == 1)) {
        
                        Platform.runLater(() -> {
                            showStage();
                        });
                 }
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                 }
            }; 
                trayIcon.addMouseListener(ml);
            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("Открыть");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);
     
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Выход");
            exitItem.addActionListener(event -> {
         
                SizesPositionClass spc=new SizesPositionClass(stage.getX(),stage.getY(),stage.getScene().getHeight(),stage.getScene().getWidth());
                        //save sizes and position info to file
                    try {
                        FileOutputStream fos = new FileOutputStream("size.out");
                            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                                oos.writeObject(spc);
                                oos.flush();
                            }
                    } catch (FileNotFoundException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    writeToTrackersOut();
                saveSpentHours();
                workTimer.cancel();
                pauseTimer();
                //notificationTimer.cancel();
                Platform.exit();
                System.exit(0);
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // create a timer which periodically displays a notification message.
            resumeTimer();
           
            // add the application tray icon to the system tray.
            tray.add(trayIcon);
            
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
        }
    }
      private String getNumEnding(int iNumber)
    {
        String sEnding;
        int i;
        iNumber = iNumber % 100;
        if (iNumber>=11 && iNumber<=19) {
            sEnding="новых задач";
        }
        else {
            i = iNumber % 10;
            switch (i)
            {
                case (1): sEnding = "новая задача"; break;
                case (2):
                case (3):
                case (4): sEnding = "новые задачи"; break;
                default: sEnding = "новых задач";
            }
        }
        return sEnding;
    }
     private void showStage() {
            if (stage != null) {
                stage.show();
                stage.toFront();
                if(sceneHeight!=0&&sceneWidth!=0){
                stage.setHeight(sceneHeight);
                stage.setWidth(sceneWidth);
                }
            }
        }
    private void setHeightOfRowsLinux()
    {
        spentHoursCol.setCellFactory(column -> {
            return new TableCell<RowIssue, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(empty ? "" : getItem());
                    setGraphic(null);

                    TableRow<RowIssue> currentRow = getTableRow();

                    if (!isEmpty()) {
                         int n=table.getColumns().get(3).getCellObservableValue(currentRow.getIndex()).getValue().toString().split("\n").length;
                    currentRow.setMinHeight(n*19);

                    }
                }
            };
        });
    }
    //change color oin cell with selected item
    private void updateTableIssues()
    {
        idCol.setCellFactory(column -> {
            return new TableCell<RowIssue, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(empty ? "" : getItem());
                    setGraphic(null);

                    TableRow<RowIssue> currentRow = getTableRow();

                    if (!isEmpty()) {

                        if(item.equals(markedIssue)&&timeStartInWork!=0)
                            currentRow.setStyle("-fx-background-color:lightgreen");
                        else
                        {
                            currentRow.setStyle("-fx-background-color:white");
                            currentRow.setStyle("-fx-color:white");
                        }

                    }
                }
            };
        });
    }
    //get items from api and set their in the table
    private void setTableIssues()
    {
        String bufStatusIdString;
        String bufTrackerIdString;
        int selectedRowIndex; 

        if(tabs.getSelectionModel().getSelectedItem()!=null)
        {
            bufStatusIdString=statusIdStringList.get(tabs.getSelectionModel().getSelectedIndex());
            bufTrackerIdString=trackerIdStringList.get(tabs.getSelectionModel().getSelectedIndex());
            selectedRowIndex = table.getSelectionModel().getSelectedIndex();
        }
        else
        {
            bufStatusIdString=statusIdStringList.get(0);
            bufTrackerIdString=trackerIdStringList.get(0);   
            selectedRowIndex=-1;
        }
        ObservableList<RowIssue> bufRowList = FXCollections.observableArrayList();

        if(!bufStatusIdString.isEmpty()&&!bufTrackerIdString.isEmpty()){
        priorityIdCol.setSortType(TableColumn.SortType.DESCENDING);

             Params params2 = new Params()
                                .add("set_filter", "1")
                                .add("assigned_to_id", String.valueOf(idCurrentUser))
                                .add("tracker_id", bufTrackerIdString)
                                .add("status_id", bufStatusIdString);

            try {

                issues = mgr.getIssueManager().getIssues(params2).getResults();
            } catch (RedmineException ex) {
                LOG.addHandler(handler);
                LOG.log(Level.SEVERE, null, ex);
            }

                                String bufTheme;
                                for (Issue issue : issues) {
                                    if(issue.getAssigneeId()!=null) {
                                        if (issue.getAssigneeId()==idCurrentUser) {
                                             bufTheme=issue.getSubject();
                                             for(int j=30;j<bufTheme.length();j+=30)
                                             {
                                                 for(int jj=j;jj>j-30;jj--)
                                                     if(bufTheme.charAt(jj)==' ')
                                                     {
                                                         bufTheme=bufTheme.substring(0, jj) + "\n" + bufTheme.substring(jj+1, bufTheme.length());
                                                         j=jj+1;
                                                         break;
                                                     }
                                             }
                                   
                                            try {
                                                String spentHoursStringBuf=spentHoursMap.get(issue.getId());
                                                if(spentHoursStringBuf==null)
                                                {

                                                        if(issue.getStatusId()!=ID_STATUS_NEW)
                                                        {
                                                            Float bufSpentTimeIfEnable=mgr.getIssueManager().getIssueById(issue.getId()).getSpentHours();
                                                            spentHoursStringBuf=(bufSpentTimeIfEnable!=null)?bufSpentTimeIfEnable.toString():"НД";
                                                        }
                                                        else
                                                        {
                                                            spentHoursStringBuf="Новая";
                                                        }
                                                    spentHoursMap.put(issue.getId(), spentHoursStringBuf);
                                                }
                                                bufRowList.add(new RowIssue(issue.getId().toString(),issue.getStatusName(),issue.getPriorityText(),bufTheme,spentHoursStringBuf,issue.getPriorityId().toString()));
                                            } catch (RedmineException ex) {
                                                LOG.addHandler(handler);
                                                LOG.log(Level.SEVERE, null, ex);
                                            }

                                        }
                                    }
                                }
        }
        else
        {
             bufRowList.add(new RowIssue("","","","","",""));            
        }
        int tabIndex = tabs.getSelectionModel().getSelectedIndex();
        if(rowIssueList.size()>0)
            rowIssueList.set(tabIndex, bufRowList);
        else
            rowIssueList.add(bufRowList);
        table.setItems(bufRowList);
        table.getSortOrder().add(priorityIdCol);
        if(selectedRowIndex!=-1)
            table.getSelectionModel().select(selectedRowIndex);           
    }
    private void changeIssueStatusFromNew(int idOfUpdatedIssue, int newIdStatus, boolean flag)
    {
        try {
            Issue updatedIssue = mgr.getIssueManager().getIssueById(idOfUpdatedIssue);
            if(updatedIssue.getStatusId()==ID_STATUS_NEW)
            {
            updatedIssue.setStatusId(newIdStatus);
                           
            mgr.getIssueManager().update(updatedIssue);
            String spentHoursStringBuf=updatedIssue.getSpentHours().toString();
            spentHoursMap.replace(idOfUpdatedIssue, spentHoursStringBuf);

            for (int i = 0; i < table.getItems().size(); i++) {
                if (Integer.valueOf(table.getItems().get(i).getIdCol()) == idOfUpdatedIssue) {
                
                rowIssueList.get(tabs.getSelectionModel().getSelectedIndex()).get(i).setSpentHoursCol(spentHoursStringBuf);
                priorityIdCol.setSortType(TableColumn.SortType.DESCENDING);
                table.setItems(rowIssueList.get(tabs.getSelectionModel().getSelectedIndex()));
                table.getSortOrder().add(priorityIdCol);
                break;
                }
            }
            }
            else if((flag)&&(updatedIssue.getStatusId()!=newIdStatus))
            {
                updatedIssue.setStatusId(newIdStatus);
                mgr.getIssueManager().update(updatedIssue);                        
            }
        } catch (RedmineException ex) {
            LOG.addHandler(handler);
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    private void pauseTimer()
    {
        notificationTimer.cancel();
    }
    private void resumeTimer()
    {
        notificationTimer = new Timer();
        notificationTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                if((idCurrentUser!=0)&&(issues!=null))
                                {
                                Params params2;
                            
                                    params2 = new Params()
                                            .add("set_filter", "1")
                                            .add("assigned_to_id", String.valueOf(idCurrentUser));
                                
                                List<Issue> issues2=null;
                                try {
                                    issues2 = mgr.getIssueManager().getIssues(params2).getResults();
                                 
                                } catch (RedmineException ex) {
                                    LOG.addHandler(handler);
                                    LOG.log(Level.SEVERE, null, ex);
                                }
                             
                                if(issues2!=null)
                                {
                              //compare received issues with initial issues
                                if(!issuesAll.toString().equals(issues2.toString()))
                                {
                                 issues2.removeAll(issuesAll);
                                 setTableIssues();
                             issuesAll.addAll(issues2);
                            if(selectedIssue!=0&&timeStartInWork!=0)
                                updateTableIssues();
                            if(issues2.size()>0)
                            {
                                String messageShow="У вас "+String.valueOf(issues2.size())+" "+getNumEnding(issues2.size());
                                javax.swing.SwingUtilities.invokeLater(() ->
                                    trayIcon.displayMessage(
                                        "Внимание",
                                        messageShow, 
                                        java.awt.TrayIcon.MessageType.INFO
                                    )
                                );
                            }
                                }
                                }
                        
                                }
                        });  
                        }
                    },
                    20_000,
                    30_000
            );

    }
    private void disableButtons(boolean dis)
    {
        startButton.setDisable(dis);
        finishButton.setDisable(dis);
        urlButton.setDisable(dis);
        deferButton.setDisable(dis);
        settingsButton.setDisable(dis);
        createButton.setDisable(dis);
        trackerButton.setDisable(dis);
        if(dis)
        {
            pauseTimer();
        }
        else
        {
            resumeTimer();
        }
    }
    private void updateAllSpentHours()
    {
    disableButtons(true);
    spentHoursMap.clear();
    try {
        Params params2 = new Params()
            .add("set_filter", "1")
            .add("assigned_to_id", String.valueOf(idCurrentUser));//mgr.getUserManager().getCurrentUser().getId()));

    issuesAll = mgr.getIssueManager().getIssues(params2).getResults();
    String spentHoursStringBuf;
    for(Issue issue : issuesAll)
    {
        if(issue.getStatusId()!=ID_STATUS_NEW)
        {
            Float bufSpentTimeIfEnable=mgr.getIssueManager().getIssueById(issue.getId()).getSpentHours();
            spentHoursStringBuf=(bufSpentTimeIfEnable!=null)?bufSpentTimeIfEnable.toString():"НД";
        }
        else
        {
            spentHoursStringBuf="Новая";
        }
        spentHoursMap.put(issue.getId(),spentHoursStringBuf);
    }
    } catch (RedmineException ex) {
        LOG.addHandler(handler);
        LOG.log(Level.SEVERE, null, ex);
    }
    setTableIssues();
    updateTableIssues();
    disableButtons(false);
}
    private void showProjectStage()
    {
            if(mgr!=null)
            {
                projectsVbox.getChildren().clear();
                projectsToggleGroupVbox.getChildren().clear();
                try {
                    
                    projectsList=mgr.getProjectManager().getProjects();
                    projectsList.stream().map((project) -> {
                        RadioButton rb = new RadioButton(project.getName());
                        rb.setId(String.valueOf(project.getId()));
                        return rb;
                    }).map((rb) -> {
                        rb.setToggleGroup(projectsToggleGroup);
                        return rb;
                    }).map((rb) -> {
                        if(rb.getId().equals(idProject))
                            rb.setSelected(true);
                        return rb;
                    }).forEachOrdered((rb) -> {
                        projectsToggleGroupVbox.getChildren().add(rb);
                    });
                    tfSubproject.setMaxWidth(140);
                    tfSubproject.setText(idSubproject);
                    
                    lbSubproject.setText("Родительская задача");
                    selectProjectButton.setText("Применить");
                    projectsScroll.setContent(projectsToggleGroupVbox);
                    projectsVbox.getChildren().addAll(projectsScroll);
                    projectsVbox.getChildren().add(lbSubproject);
                    projectsVbox.getChildren().add(tfSubproject);
                    projectsVbox.getChildren().add(selectProjectButton);
                    
                    VBox.setMargin(projectsScroll, new Insets(5.0,5.0,0.0,5.0));
                    VBox.setMargin(lbSubproject, new Insets(5.0,5.0,15.0,5.0));
                    VBox.setMargin(tfSubproject, new Insets(-40.0,5.0,15.0,140.0));
                    VBox.setMargin(selectProjectButton, new Insets(-45.0,5.0,15.0,300.0));
                    
                    tfSubproject.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                        if (!newValue.matches("\\d*")) {
                            tfSubproject.setText(newValue.replaceAll("[^\\d]", ""));
                        }
                    });
                    selectProjectButton.setOnAction((final ActionEvent e) -> {
                        idSubproject=tfSubproject.getText();
                        if(projectsToggleGroup.getSelectedToggle()!=null)
                        {
                            RadioButton chk = (RadioButton)projectsToggleGroup.getSelectedToggle().getToggleGroup().getSelectedToggle(); // Cast object to radio button
                            idProject=chk.getId();
                        }
                        projectStage.hide();
                        writeToTrackersOut();                      
                    });
                    if(projectsScene==null)
                        projectsScene = new Scene(projectsVbox, 400, 400);
                    if(projectStage.getScene()==null)
                        projectStage.setScene(projectsScene);
                    projectStage.setTitle("Проект, в который будут добавляться задачи");
                    projectStage.show();
                } catch (RedmineException ex) {
                    LOG.addHandler(handler);
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            
    }
private void writeToTrackersOut()
{
    //save trackers and statuses info to file
    TrackersStatusesClass tsc=new TrackersStatusesClass(trackerIdStringList,statusIdStringList,nameTabsStringList, idProject, idSubproject);         
                    try {
                        FileOutputStream fos = new FileOutputStream("trackers.out");
                            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                                oos.writeObject(tsc);
                                oos.flush();
                            }
                    } catch (FileNotFoundException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }        
}

@Override
    public void start(Stage primaryStage) throws FileNotFoundException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException, RedmineException {
       handler = new FileHandler("logger.log", 1000000, 7);
        handler.setFormatter(new SimpleFormatter());
        
//get OS
        boolean windowsSystem=((System.getProperty("os.name").contains("Windows")));
        if(windowsSystem){
            String line;
            String pidInfo ="";

            Process p =Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
  
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                while ((line = input.readLine()) != null) {

                    pidInfo+=line;
                } 
            }

        Pattern pattern = Pattern.compile("RM_1_QuestBook.exe");
        Matcher m = pattern.matcher(pidInfo);
        int counter = 0;
            while(m.find()) {
                counter++;
            }

            if(counter>1)
            {
                alert.setTitle("Внимание");
                alert.setHeaderText(null);
                alert.setContentText("Программа уже запущена!");
                alert.showAndWait();    
                System.exit(0);
            }
        }
        else
        {
            //for linux
            JustOneLock u = new JustOneLock();

           try {
               if (u.isAppActive()) {
         
                   alert.setTitle("Внимание");
                    alert.setHeaderText(null);
                    alert.setContentText("Программа уже запущена!");
                    alert.showAndWait();    
                   System.exit(0);
               }
           } catch (Exception ex) {
               LOG.addHandler(handler);
               LOG.log(Level.SEVERE, null, ex);
           }
        }
    
        stage=primaryStage;
        description.setTitle(PROGRAM_NAME);
        dialog.setTitle(PROGRAM_NAME);
        trackers.setTitle(PROGRAM_NAME);
        spentHours.setTitle(PROGRAM_NAME);
        settingsButton.setTooltip(new Tooltip("Установить соединение"));
        urlButton.setTooltip(new Tooltip("Открыть задачу в браузере"));
        startButton.setTooltip(new Tooltip("Запустить задачу"));
        deferButton.setTooltip(new Tooltip("Отложить задачу"));
        finishButton.setTooltip(new Tooltip("Завершить задачу"));
        trackerButton.setTooltip(new Tooltip("Выбор отображаемых трекеров и статусов"));
        createButton.setTooltip(new Tooltip("Создать задачу"));
        projectsButton.setTooltip(new Tooltip("Выбрать проект, в который\n будут создаваться задачи"));

        if(!windowsSystem)table.setStyle("-fx-font-size:12px;");
                    description.initModality(Modality.APPLICATION_MODAL);
                    description.initOwner(primaryStage);
                    //set sizes and margin depending on OS
                    if(windowsSystem)
                    {
                        descriptionScroll.setMinHeight(140);
                        descriptionScroll.setMaxHeight(500);
                        commentsScroll.setMinHeight(100);
                        commentsScroll.setMaxHeight(500);
                        addComment.setMaxWidth(35);
                        themeCol.setMinWidth(210);
                        themeCol.setMaxWidth(210);
                        table.setMaxWidth(285);
                        StackPane.setMargin(table, new Insets(40.0,5.0,5.0,5.0));
                        StackPane.setMargin(urlButton, new Insets(50.0,10.0,5.0,295.0));
                        StackPane.setMargin(startButton, new Insets(90.0,10.0,5.0,295.0));
                        StackPane.setMargin(finishButton, new Insets(130.0,10.0,5.0,295.0));
                        StackPane.setMargin(deferButton, new Insets(170.0,10.0,5.0,295.0));
                        StackPane.setMargin(trackerButton, new Insets(210.0,10.0,5.0,295.0));
                        StackPane.setMargin(createButton, new Insets(250.0,10.0,5.0,295.0));
                        StackPane.setMargin(projectsButton, new Insets(290.0,10.0,5.0,295.0));
                        StackPane.setMargin(settingsButton, new Insets(10.0,5.0,5.0,295.0));
                        StackPane.setMargin(lbTime, new Insets(10.0,10.0,10.0,10.0));
                        lbHost.setMaxWidth(35);
                        lbKey.setMaxWidth(35);
                        tfHost.setMaxWidth(200);
                        tfKey.setMaxWidth(200);
                        lbHost.setMaxHeight(40);
                        lbKey.setMaxHeight(40);
                        tfHost.setMaxHeight(40);
                        tfKey.setMaxHeight(40);
                    }
                    else
                    {
                        descriptionScroll.setMinHeight(140);
                        descriptionScroll.setMaxHeight(500);
                        commentsScroll.setMinHeight(100);
                        commentsScroll.setMaxHeight(500);                
                        addComment.setMaxWidth(40);
                        themeCol.setMinWidth(250);
                        themeCol.setMaxWidth(250);
                        table.setMaxWidth(325);
                        StackPane.setMargin(table, new Insets(40.0,5.0,5.0,5.0));
                        StackPane.setMargin(urlButton, new Insets(50.0,10.0,5.0,335.0));
                        StackPane.setMargin(startButton, new Insets(90.0,10.0,5.0,335.0));
                        StackPane.setMargin(finishButton, new Insets(130.0,10.0,5.0,335.0));
                        StackPane.setMargin(deferButton, new Insets(170.0,10.0,5.0,335.0));
                        StackPane.setMargin(trackerButton, new Insets(210.0,10.0,5.0,335.0));
                        StackPane.setMargin(createButton, new Insets(250.0,10.0,5.0,335.0));
                        StackPane.setMargin(projectsButton, new Insets(290.0,10.0,5.0,335.0));
                        StackPane.setMargin(settingsButton, new Insets(10.0,5.0,5.0,335.0));
                        StackPane.setMargin(lbTime, new Insets(10.0,10.0,10.0,10.0));
                        lbHost.setMaxWidth(35);
                        lbKey.setMaxWidth(40);
                        tfHost.setMaxWidth(200);
                        tfKey.setMaxWidth(200);
                        lbHost.setMaxHeight(40);
                        lbKey.setMaxHeight(40);
                        tfHost.setMaxHeight(40);
                        tfKey.setMaxHeight(40);
                    }
                    
                    VBox.setMargin(descriptionScroll, new Insets(1.0,5.0,1.0,5.0));
                    VBox.setMargin(commentsScroll, new Insets(-17.0,5.0,0.0,5.0));
                    VBox.setMargin(tfNewComment, new Insets(-15.0,50.0,15.0,5.0));
               
                    if(windowsSystem)
                        VBox.setMargin(addComment, new Insets(-66.0,5.0,15.0,5.0));
                    else
                        VBox.setMargin(addComment, new Insets(-66.0,5.0,15.0,5.0));
               
                    addComment.setText("ОК");
                    commentsScroll.setContent(descriptionComments);
                    descriptionScroll.setContent(descriptionText);
                    descriptionVbox.getChildren().add(descriptionScroll);
                    descriptionVbox.getChildren().add(commentsScroll);
                    descriptionVbox.getChildren().add(tfNewComment);
                    descriptionVbox.getChildren().add(addComment);
                    descriptionVbox.setAlignment(Pos.BOTTOM_RIGHT);
                    addSpentHours.setText("ОК");
                    tfSpentHours.setMaxWidth(240);
                   
                    spentHoursVbox.getChildren().add(lbSpentHours);
                    spentHoursVbox.getChildren().add(tfSpentHours);
                    spentHoursVbox.getChildren().add(addSpentHours);
                    VBox.setMargin(lbSpentHours, new Insets(5.0,5.0,5.0,5.0));
                    VBox.setMargin(tfSpentHours, new Insets(-5.0,5.0,15.0,5.0));
                    VBox.setMargin(addSpentHours, new Insets(-60.0,5.0,15.0,260.0));
                    
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(primaryStage);
                    connectButton.setText("Соединиться");
                    selectTrackersButton.setText("Принять");
                    VBox.setMargin(lbHost, new Insets(30.0,5.0,5.0,25.0));
                    VBox.setMargin(lbKey, new Insets(20.0,5.0,5.0,25.0));
                    VBox.setMargin(tfHost, new Insets(-107.0,5.0,5.0,65.0));
                    VBox.setMargin(tfKey, new Insets(12.0,5.0,5.0,65.0));
                    if(windowsSystem)
                        VBox.setMargin(connectButton, new Insets(5.0,5.0,5.0,110.0));
                    else
                        VBox.setMargin(connectButton, new Insets(5.0,5.0,5.0,100.0));
                    dialogVbox.getChildren().add(lbHost);
                    dialogVbox.getChildren().add(lbKey);
                    dialogVbox.getChildren().add(tfHost);
                    dialogVbox.getChildren().add(tfKey);
                    dialogVbox.getChildren().add(connectButton);
                    Scene dialogScene = new Scene(dialogVbox, 300, 190);
                    dialog.setScene(dialogScene);
                    Scene descriptionScene;
                    if(windowsSystem)
                        descriptionScene = new Scene(descriptionVbox, 310, 300);
                    else
                        descriptionScene = new Scene(descriptionVbox, 380, 300);
                    description.setScene(descriptionScene);
                    Scene spentHoursScene = new Scene(spentHoursVbox, 310, 80);
                    spentHours.setScene(spentHoursScene);
                    
        //hide unnecessary columns
        spentHoursCol.setResizable(false);
        spentHoursCol.setMaxWidth(50);
        spentHoursCol.setMinWidth(50);
        spentHoursCol.setStyle("-fx-alignment: CENTER");
        statusCol.setVisible(false);
        idCol.setMaxWidth(0.0);
        priorityCol.setVisible(false);
        priorityIdCol.setVisible(false);    
        idCol.setResizable(false);
        themeCol.setSortable(false);
        spentHoursCol.setSortable(false);
        idCol.setSortable(false);
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("idCol"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statusCol"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priorityCol"));
        themeCol.setCellValueFactory(new PropertyValueFactory<>("themeCol"));
        spentHoursCol.setCellValueFactory(new PropertyValueFactory<>("spentHoursCol"));
        priorityIdCol.setCellValueFactory(new PropertyValueFactory<>("priorityIdCol"));
    
        rowList.add(new RowIssue("","","","","",""));
        table.setItems(rowList);
        table.getColumns().addAll(idCol, statusCol, priorityCol, themeCol, spentHoursCol, priorityIdCol);
        themeCol.setResizable(false);
        Label themeColHeaderLabel = new Label("                      Мои задачи                  ");
        themeColHeaderLabel.setOnMouseClicked((MouseEvent mouseEvent) -> {
                                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                    if (mouseEvent.getClickCount() == 2) {
                                            Params params2;
                                        try {
                                            params2 = new Params()
                                                    .add("set_filter", "1")
                                                    .add("assigned_to_id", String.valueOf(idCurrentUser));//mgr.getUserManager().getCurrentUser().getId()));
                                            issuesAll = mgr.getIssueManager().getIssues(params2).getResults();
                                        } catch (RedmineException ex) {
                                            LOG.addHandler(handler);
                                            LOG.log(Level.SEVERE, null, ex);
                                        }

                                        
                                        setTableIssues();
                                        updateTableIssues();
                                    }
                                }
        });
        themeCol.setGraphic(themeColHeaderLabel);

        Label spentHoursColHeaderLabel = new Label("Время");
        spentHoursColHeaderLabel.setOnMouseClicked((MouseEvent mouseEvent) -> {
                                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                    if (mouseEvent.getClickCount() == 2) {                            
                                        updateAllSpentHours();
                                    }
                                }
        });
        spentHoursCol.setGraphic(spentHoursColHeaderLabel);
        
        //make main StackPain
        StackPane root = new StackPane();
        root.getChildren().add(lbTime);
        root.getChildren().add(table);
        root.getChildren().add(urlButton);
        root.getChildren().add(startButton);
        root.getChildren().add(finishButton);
        root.getChildren().add(deferButton);
        root.getChildren().add(trackerButton);
        root.getChildren().add(createButton);
        root.getChildren().add(projectsButton);
        root.getChildren().add(settingsButton);
        
        StackPane.setAlignment(table, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(lbTime, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(urlButton, Pos.TOP_LEFT);
        StackPane.setAlignment(startButton, Pos.TOP_LEFT);
        StackPane.setAlignment(finishButton, Pos.TOP_LEFT);
        StackPane.setAlignment(deferButton, Pos.TOP_LEFT);
        StackPane.setAlignment(trackerButton, Pos.TOP_LEFT);
        StackPane.setAlignment(createButton, Pos.TOP_LEFT);
        StackPane.setAlignment(projectsButton, Pos.TOP_LEFT);
        StackPane.setAlignment(settingsButton, Pos.TOP_LEFT);
        
         Tab tabMain = new Tab();
        tabMain.setText("Главная");
        tabMain.closableProperty().set(false);
        tabMain.setStyle("-fx-padding: 3 10 3 35;");
        tabs.getTabs().add(tabMain);
        
        //try to open file with information about trackers and statuses we need
                    try {
                        FileInputStream fis = new FileInputStream("trackers.out");
                        ObjectInputStream oin = new ObjectInputStream(fis);
                        TrackersStatusesClass tsc = (TrackersStatusesClass) oin.readObject();
                        trackerIdStringList=tsc.getTrackers();
                        statusIdStringList=tsc.getStatuses();
                        nameTabsStringList=tsc.getNames();
                        idProject=tsc.getProject();
                        idSubproject=tsc.getSubproject();
                        trackerIdString=trackerIdStringList.get(0);
                        statusIdString=statusIdStringList.get(0);
                        ObservableList<RowIssue> rowListnew = FXCollections.observableArrayList();
                        rowListnew.add(new RowIssue("","","","","",""));
                        rowIssueList.add(rowListnew);
                        for(int i=1;i<trackerIdStringList.size();i++)
                        {
                            final Tab tab = new Tab();
                            tab.setOnClosed((Event t) -> {
                                rowIssueList.remove(tabs.getSelectionModel().getSelectedIndex()+1);
                                statusIdStringList.remove(tabs.getSelectionModel().getSelectedIndex()+1);
                                trackerIdStringList.remove(tabs.getSelectionModel().getSelectedIndex()+1);
                                nameTabsStringList.remove(tabs.getSelectionModel().getSelectedIndex()+1);
                                writeToTrackersOut();
                            });
                            Label tabALabel = new Label(nameTabsStringList.get(i));
                            tab.setGraphic(tabALabel);
                            tabALabel.setOnMouseClicked((MouseEvent mouseEvent) -> {
                                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                    if (mouseEvent.getClickCount() == 2) {
                                        TextInputDialog tiDialog = new TextInputDialog("Вкладка");
                                        tiDialog.setTitle("Изменение названия вкладки");
                                        tiDialog.setHeaderText("Заполните поле ниже");
                                        tiDialog.setContentText("Введите название владки:");
                                        
                                        Optional<String> result = tiDialog.showAndWait();
                                        result.ifPresent(name -> {
                                            tabALabel.setText(name);
                                            nameTabsStringList.set(tabs.getSelectionModel().getSelectedIndex(), name);
                                            writeToTrackersOut();
                                        });
                                    }
                                }
                            });
                            rowIssueList.add(rowListnew);
                            tabs.getTabs().add(tab);
                        }
                        } catch (FileNotFoundException | ClassNotFoundException ex) { 
                            trackerIdString="1|2|3|4|14";
                            statusIdString="1|2|3|6|8|9|21|25|27|29";
                            idProject="";
                            idSubproject="";
                        
                            trackerIdStringList.add(trackerIdString);
                            statusIdStringList.add(statusIdString);
                            nameTabsStringList.add("Главная");
                            LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    //try to open file with information about connection
                    try {
                        FileInputStream fis = new FileInputStream("conn.out");
                        ObjectInputStream oin = new ObjectInputStream(fis);
                        cc = (ConnectClass) oin.readObject();
                        tfHost.setText(cc.getHost());
                        tfKey.setText(cc.getKey());
                        connectFunc();
                        setHeightOfRowsLinux();
                        } catch (FileNotFoundException | ClassNotFoundException ex) { 
                            LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
                     
                       //connect by enter press in connection window
                    dialogScene.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            try {
                                //if some issue now in work, save time which spent on this issue 
                                saveSpentHours();
                                //stop timer
                                workTimer.cancel();
                                //clear counter
                                lbTime.setText("");
                                //connect to api
                                connectFunc();
                            } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException | UnrecoverableKeyException ex) {
                                LOG.addHandler(handler);
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                    });

    themeCol.setCellFactory((TableColumn<RowIssue, String> col) -> {
        final TableCell<RowIssue, String> cell = new TableCell<>();
        cell.textProperty().bind(cell.itemProperty()); // in general might need to subclass TableCell and override updateItem(...) here
        cell.setOnMouseClicked((MouseEvent event) -> {//set action for click on table (column - themeCol)
            if (event.getButton() == MouseButton.SECONDARY) {//set action for right button click
                //select Issue for Subsequent work
                int indexRow = table.getSelectionModel().getSelectedIndex();
                if(selectedIssue==0||timeStartInWork==0)
                {
                    selectedIssue=Integer.parseInt(table.getColumns().get(0).getCellObservableValue(indexRow).getValue().toString());
                    markedIssue=table.getColumns().get(0).getCellObservableValue(indexRow).getValue().toString();
                }
                //open window with description and comments of issue
                int issueId=Integer.parseInt(table.getColumns().get(0).getCellObservableValue(indexRow).getValue().toString());
                try {
                    issueByIdWithDescription = mgr.getIssueManager().getIssueById(issueId, Include.journals);
                    String BufText="Описание:"+issueByIdWithDescription.getDescription();
                    String BufComment="Комментарии:\n";
                    StringBuilder msg = new StringBuilder();
                    String buf;
                    try{
                        Collection<Journal> collectionJournals=issueByIdWithDescription.getJournals();
                        List<Journal> listJournals = new ArrayList(collectionJournals);
                        Comparator<Journal> comparatorJournals = 
                            (Journal left, Journal right) -> left.getCreatedOn().compareTo(right.getCreatedOn());
                        Collections.sort(listJournals, comparatorJournals);
                        
                        int countjrnl=1;
                        for (Journal journal : listJournals) {
                            try{
                            if((journal.getNotes()!=null)&&(!journal.getNotes().trim().isEmpty()))
                            {
                                buf=journal.getNotes().trim().replaceAll("&ldquo;","\"").replaceAll("&rdquo;","\"").replaceAll("&nbsp;"," ").replaceAll("&quot;"," ").replaceAll("<p>","\n").replaceAll("<div>","\n").replaceAll("<br>","\n").replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
                                BufComment+=countjrnl+") "+((buf.charAt(0)=='\n')?buf.substring(1, buf.length()):buf.substring(0, buf.length()))+"\n";
                                countjrnl++;
                            }
                            }catch(Exception ex){  
                        LOG.addHandler(handler);        
                        LOG.log(Level.SEVERE, null, ex);
                    }
                        }
                    }catch(SecurityException ex){
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    //delete html markup
                    BufText=BufText.replaceAll("&ldquo;","\"").replaceAll("&rdquo;","\"").replaceAll("&nbsp;"," ").replaceAll("&quot;"," ").replaceAll("<p>","\n").replaceAll("<div>","\n").replaceAll("<br>","\n").replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
                    //split the string
                    boolean fl=true;
                    for(int j=45;j<BufText.length();j+=45)
                    {
                        for(int jj=j;jj>j-44;jj--)
                            if(BufText.charAt(jj)=='\n')
                            {
                                j=jj+1;
                                fl=false;
                                break;
                            }
                        if(fl)
                            for(int jj=j;jj>j-45;jj--)
                                if(BufText.charAt(jj)==' ')
                                {
                                    BufText=BufText.substring(0, jj) + "\n" + BufText.substring(jj+1, BufText.length());
                                    j=jj+1;
                                    break;
                                }
                        fl=true;
                    }
                    for(int j=45;j<BufComment.length();j+=45)
                    {
                        for(int jj=j;jj>j-44;jj--)
                            if(BufComment.charAt(jj)=='\n')
                            {
                                j=jj+1;
                                fl=false;
                                break;
                            }
                        if(fl)
                            for(int jj=j;jj>j-45;jj--)
                                if(BufComment.charAt(jj)==' ')
                                {
                                    BufComment=BufComment.substring(0, jj) + "\n" + BufComment.substring(jj+1, BufComment.length());
                                    j=jj+1;
                                    break;
                                }
                        fl=true;
                    }
                    //set text in textfields
                    msg.append(BufText);
                    descriptionText.setText(msg.toString());
                    msg = new StringBuilder();
                    msg.append(BufComment);
                    descriptionComments.setText(msg.toString());
                    tfNewComment.setText("");
                    urlIssue=uri+"/issues/"+table.getColumns().get(0).getCellObservableValue(indexRow).getValue().toString();
                } catch (RedmineException ex) {
                    LOG.addHandler(handler);
                    LOG.log(Level.SEVERE, null, ex);
                }
                //show window
                description.show();
                
            }
            else{//set action for left button click
                try{
           
                     int indexRow;
                    if(table.getSelectionModel().getSelectedItem()!=null){
                //select Issue for Subsequent work
                indexRow = table.getSelectionModel().getSelectedIndex();
                }else{
                        indexRow=cell.getIndex();
                
                        table.requestFocus();
                        table.getSelectionModel().select(indexRow);
                        table.getFocusModel().focus(indexRow);
                    }
                if(selectedIssue==0||timeStartInWork==0)
                {
                    selectedIssue=Integer.parseInt(table.getColumns().get(0).getCellObservableValue(indexRow).getValue().toString());
                    markedIssue=table.getColumns().get(0).getCellObservableValue(indexRow).getValue().toString();
                }
                urlIssue=uri+"/issues/"+table.getColumns().get(0).getCellObservableValue(indexRow).getValue().toString();
                //if control button was pressed open url of issue
                if(event.isControlDown())
                {
                    getHostServices().showDocument(urlIssue);
                }
                else if(event.isAltDown())
                {
                    TextInputDialog tiDialog = new TextInputDialog("00:01");
                    tiDialog.setTitle(PROGRAM_NAME);
                    tiDialog.setHeaderText(null);
                    tiDialog.setContentText("Введите трудозатраты:");
                                     
                    Optional<String> result = tiDialog.showAndWait();
                    result.ifPresent(name -> {
                          if(!name.trim().isEmpty())
                          {
                              int idOfIssueWhereChangeSpentHours=Integer.valueOf(table.getSelectionModel().getSelectedItem().getIdCol());
                                TimeEntry TE=new TimeEntry();
                                TE.setHours(Float.parseFloat(name.replaceAll(",",".").replaceAll(":",".")));
                                TE.setIssueId(idOfIssueWhereChangeSpentHours);
                                TE.setActivityId(ID_ACTIVITY_ENGINEERING); 
                                try {
                                     mgr.getTimeEntryManager().createTimeEntry(TE);
                                     changeIssueStatusFromNew(idOfIssueWhereChangeSpentHours,ID_STATUS_VALUATION_SPENT_TIME, false);
                     
                                     String spentHoursStringBuf=mgr.getIssueManager().getIssueById(idOfIssueWhereChangeSpentHours).getSpentHours().toString();
                                    spentHoursMap.replace(idOfIssueWhereChangeSpentHours,spentHoursStringBuf);
                                    setTableIssues();
                                    updateTableIssues();
                                    } catch (RedmineException ex) {
                                        alert.setTitle("Внимание");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Трудозатраты не удалось сохранить!");
                                        alert.showAndWait();
                                        LOG.addHandler(handler);
                                        LOG.log(Level.SEVERE, null, ex);
                                    }
                          }
                    });
                }
                
               }catch(NumberFormatException ex){
                   LOG.addHandler(handler);
                   LOG.log(Level.SEVERE, null, ex);
               }
            }
        });
        return cell ;
        });
        //add action for button OK in window with description of issue
    addComment.setOnAction((final ActionEvent e) -> {
        //if comment isset in textfield set note to issue
        if(!tfNewComment.getText().trim().isEmpty())
        {
            try {
                 issueByIdWithDescription.setNotes(tfNewComment.getText().trim().replaceAll("\n","<br>"));
                 mgr.getIssueManager().update(issueByIdWithDescription);
                 if(issueByIdWithDescription.getStatusId()==ID_STATUS_NEW)
                 {
                     changeIssueStatusFromNew(issueByIdWithDescription.getId(),ID_STATUS_VALUATION_SPENT_TIME, false);
                     setTableIssues();
                     updateTableIssues();
                 }
            } catch (RedmineException ex) {
                LOG.addHandler(handler);
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        tfNewComment.setText("");
        description.hide();
        });
    //open issue's url by button press
          urlButton.setOnAction((final ActionEvent e) -> {
              //if isset host url
              if(!uri.trim().isEmpty())
              {
                  getHostServices().showDocument(urlIssue);
              }
        });
          //show window with connection info by press settingsButton
          settingsButton.setOnAction((final ActionEvent e) -> {
              dialog.show();
        });
          //add action of finishButton press
           finishButton.setOnAction((final ActionEvent e) -> {
               //disable buttons at the time of work
            
               disableButtons(true);
               //if connection established and issue selected
               if((mgr!=null)&&(table.getSelectionModel().getSelectedItem()!=null))
               {
                   int idFinishedIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
                   //if issue is in work now
                   if((selectedIssue!=0)&&(timeStartInWork!=0)&&(selectedIssue==idFinishedIssue)){
                       //save time which spent on this issue
                       saveSpentHours();
                       //stop timer
                       workTimer.cancel();                    
                       //clear counter
                       lbTime.setText("");                       
                       timeStartInWork=0;
                       Issue updateIssue;
                       //set issue status finished
                       try {
                           updateIssue = mgr.getIssueManager().getIssueById(selectedIssue);
                           updateIssue.setStatusId(ID_STATUS_FINISHED);
                           
                           mgr.getIssueManager().update(updateIssue);
                           spentHoursMap.remove(selectedIssue);
                       } catch (RedmineException ex) {
                           LOG.addHandler(handler);
                           LOG.log(Level.SEVERE, null, ex);
                       }
                       issueIdSpentHours=selectedIssue;
                       TextInputDialog tiDialog = new TextInputDialog("00:01");
                        tiDialog.setTitle(PROGRAM_NAME);
                        tiDialog.setHeaderText(null);
                        tiDialog.setContentText("Хотите ввести дополнительные трудозатраты?");

                        Optional<String> result = tiDialog.showAndWait();
                        result.ifPresent(name -> {
                              if(!name.trim().isEmpty())
                              {
                                 addExtraSpentHours(name); 
                              }
                        });
       
                       startButton.setGraphic(new ImageView(startImage));
                       startButton.setTooltip(new Tooltip("Запустить задачу"));
                       workTimer = new Timer();
                       
                       markedIssue="";
                       selectedIssue=0;
                       //make all cells of table without background
                       updateTableIssues();
                       //get updated issues from api and insert into table
                       setTableIssues();
                       lbTime.setText("");
                   }
                   else if((selectedIssue!=0)&&(timeStartInWork!=0)&&(selectedIssue!=idFinishedIssue)){
                       Issue updateIssue;
                       //set issue status finished
                       try {
                           updateIssue = mgr.getIssueManager().getIssueById(idFinishedIssue);
                           updateIssue.setStatusId(ID_STATUS_FINISHED);
                           
                           mgr.getIssueManager().update(updateIssue);
                           spentHoursMap.remove(idFinishedIssue);
                       } catch (RedmineException ex) {
                           LOG.addHandler(handler);
                           LOG.log(Level.SEVERE, null, ex);
                       }
                       issueIdSpentHours=idFinishedIssue;
                       TextInputDialog tiDialog = new TextInputDialog("00:01");
                        tiDialog.setTitle(PROGRAM_NAME);
                        tiDialog.setHeaderText(null);
                        tiDialog.setContentText("Введите трудозатраты:");

                        Optional<String> result = tiDialog.showAndWait();
                        result.ifPresent(name -> {
                              if(!name.trim().isEmpty())
                              {
                                 addExtraSpentHours(name); 
                              }
                        });
                       setTableIssues();
                       updateTableIssues();
                   }
                   //if issue not in work but also selected
                   else if((selectedIssue!=0)&&(timeStartInWork==0))
                   {
                       try {
                           //change status to finished
                           Issue updateIssue=mgr.getIssueManager().getIssueById(selectedIssue);
                           updateIssue.setStatusId(ID_STATUS_FINISHED);
                           
                           mgr.getIssueManager().update(updateIssue);
                           spentHoursMap.remove(selectedIssue);
                       } catch (RedmineException ex) {
                           LOG.addHandler(handler);
                           LOG.log(Level.SEVERE, null, ex);
                       }
                       issueIdSpentHours=selectedIssue;
                       TextInputDialog tiDialog = new TextInputDialog("00:01");
                        tiDialog.setTitle(PROGRAM_NAME);
                        tiDialog.setHeaderText(null);
                        tiDialog.setContentText("Введите трудозатраты:");

                        Optional<String> result = tiDialog.showAndWait();
                        result.ifPresent(name -> {
                              if(!name.trim().isEmpty())
                              {
                                 addExtraSpentHours(name); 
                              }
                        });
                       selectedIssue=0;
                       markedIssue="";
                       //get updated issues from api and insert into table
                       setTableIssues();
                   }  
                   try {
               Params params2 = new Params()
                            .add("set_filter", "1")
                            .add("assigned_to_id", String.valueOf(idCurrentUser));//mgr.getUserManager().getCurrentUser().getId()));
              
                        issuesAll = mgr.getIssueManager().getIssues(params2).getResults();
                    } catch (RedmineException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }    
               }
               lbTime.setText("");
               //enable buttons      
               disableButtons(false);
        });
           //add action to connectButton on window dialog
          connectButton.setOnAction((final ActionEvent e) -> {
              try {
                  //if some issue now in work, save time which spent on this issue 
                  saveSpentHours();
                  //stop timer
                  workTimer.cancel();
                  //clear counter
                  lbTime.setText("");
                  //connect to api
                  connectFunc();
              } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException | UnrecoverableKeyException ex) {
                  LOG.addHandler(handler);
                  LOG.log(Level.SEVERE, null, ex);
              }
        });
          //add action to button which starts issues
          startButton.setOnAction((final ActionEvent e) -> {
              //disable buttons
   
             disableButtons(true);
              //if connection established and issue selected
              if ((mgr!=null)&&(selectedIssue!=0)) {
                  if ((selectedIssue!=0)&&(timeStartInWork==0)) {
                      //notice time
                      timeStartInWork= System.currentTimeMillis();
                      
                      try{
                      //save value of selectd issue so that change it color in the table
                      if(table.getSelectionModel().getSelectedItem()!=null){
                        markedIssue=table.getSelectionModel().getSelectedItem().getIdCol();
                      }
                      else
                      {
                          markedIssue=String.valueOf(selectedIssue);                          
                      }
                      changeIssueStatusFromNew(selectedIssue, ID_STATUS_IN_WORK,true);
                      setTableIssues();      
                      updateTableIssues();
                      //start timer
                      } catch(Exception ex){}
                      try{
                      workTimer.schedule(new TimerTask() {
                          @Override
                          public void run() {
                              Platform.runLater(() -> {
                                  //get time
                                  long millis=System.currentTimeMillis()-timeStartInWork;
                                  long hours=TimeUnit.MILLISECONDS.toHours(millis);
                                  long minutes=TimeUnit.MILLISECONDS.toMinutes(millis)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
                                  long seconds=TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
                                  
                                  String s = hours+":"+((minutes<10)?"0"+String.valueOf(minutes):minutes)+":"+((seconds<10)?"0"+String.valueOf(seconds):seconds);
                                  //set spent time value in counter
                                  if(s.length()<=8)
                                      lbTime.setText(s);
                                  else
                                      lbTime.setText("");
                              });
                          }
                      }, 0_000, 1_000);
                      } catch(Exception ex){}
                      //cahnge image view of button to pause image
                      Button button = (Button) e.getSource();
                      button.setGraphic(new ImageView(pauseImage));
                      button.setTooltip(new Tooltip("Остановить таймер"));
                  } else { //if issue already started in this program
                    
                      //stop timer
                      workTimer.cancel();
                      //clear counter
                      lbTime.setText("");
                      //save time spent on this issue
                      saveSpentHours();
                      timeStartInWork=0;
                      //cahnge image view to start image
                      Button button = (Button) e.getSource();
                      button.setGraphic(new ImageView(startImage));
                      button.setTooltip(new Tooltip("Запусить задачу"));
                      //update timer
                      workTimer = new Timer();
                      lbTime.setText("");
                      markedIssue="";
                      //make all backgrounds of cells to none
                      updateTableIssues();
                      //select issue which selected now
                      try{
                          if(table.getSelectionModel().getSelectedItem()!=null)
                          selectedIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
                      }catch(NumberFormatException ex){
                          selectedIssue=0;
                      }
                  }
                  lbTime.setText("");
              }
              //enable buttons   
             disableButtons(false);
        });
        deferButton.setOnAction((final ActionEvent e) -> {
   
        disableButtons(true);
            try{
                if(table.getSelectionModel().getSelectedItem()!=null){
                    int idDeferIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
                    changeIssueStatusFromNew(idDeferIssue,ID_STATUS_DEFER,true);
                    setTableIssues();
                    if(!markedIssue.trim().isEmpty()&&timeStartInWork!=0)
                    updateTableIssues();   
                }
            }catch(NumberFormatException ex){
                LOG.addHandler(handler);
                LOG.log(Level.SEVERE, null, ex);
            }
     
         disableButtons(false);
        });
        trackerButton.setOnAction((final ActionEvent e) -> {
            if(mgr!=null)
            {
                trackersStackPane = new StackPane();
                try {
                    checkboxStatuses.clear();
                    checkboxTrackers.clear();
                    statusesList = mgr.getIssueManager().getStatuses();
                    trackersList = mgr.getIssueManager().getTrackers();
                    int marginSpace=20;
                    int marginCounter=1;
                    trackersStackPane.getChildren().add(selectTrackersButton);
                    StackPane.setAlignment(selectTrackersButton, Pos.BOTTOM_LEFT);
                    StackPane.setMargin(selectTrackersButton, new Insets(5.0,5.0,5.0,175.0)); 
                    String bufStatusIdString=statusIdStringList.get(tabs.getSelectionModel().getSelectedIndex());//statusIdString
                    String bufTrackerIdString=trackerIdStringList.get(tabs.getSelectionModel().getSelectedIndex());
        
                    for (IssueStatus issueStatus : statusesList) {
                        checkboxStatuses.add(new CheckBox(issueStatus.getName()));
                 
                        trackersStackPane.getChildren().add(checkboxStatuses.get(checkboxStatuses.size()-1));
                 
                     StackPane.setAlignment(checkboxStatuses.get(checkboxStatuses.size()-1), Pos.TOP_LEFT);
                     StackPane.setMargin(checkboxStatuses.get(checkboxStatuses.size()-1), new Insets(marginSpace*marginCounter,5.0,5.0,5.0)); 
                     if((bufStatusIdString.contains("|"+String.valueOf(issueStatus.getId())+"|"))
                         ||(bufStatusIdString.equals(String.valueOf(issueStatus.getId())))
                         ||(bufStatusIdString.indexOf(String.valueOf(issueStatus.getId())+"|")==0)
                         ||((bufStatusIdString.indexOf("|"+String.valueOf(issueStatus.getId()))==bufStatusIdString.length()-String.valueOf(issueStatus.getId()).length()-1)&&(bufStatusIdString.contains("|"+String.valueOf(issueStatus.getId())))))
                     {
                         checkboxStatuses.get(checkboxStatuses.size()-1).setSelected(true);                
                     }
                    else
                        checkboxStatuses.get(checkboxStatuses.size()-1).setSelected(false);
                    marginCounter++;
                    checkboxStatuses.get(checkboxStatuses.size()-1).setId(String.valueOf(issueStatus.getId()));
                    }
                    marginCounter=1;
                  
                    for (Tracker issueTracker : trackersList) {    
                        checkboxTrackers.add(new CheckBox(issueTracker.getName()));
                
                        trackersStackPane.getChildren().add(checkboxTrackers.get(checkboxTrackers.size()-1));
                        StackPane.setAlignment(checkboxTrackers.get(checkboxTrackers.size()-1), Pos.TOP_LEFT);
                        StackPane.setMargin(checkboxTrackers.get(checkboxTrackers.size()-1), new Insets(marginSpace*marginCounter,5.0,5.0,200.0)); 
                        if((bufTrackerIdString.contains("|"+String.valueOf(issueTracker.getId())+"|"))
                         ||(bufTrackerIdString.equals(String.valueOf(issueTracker.getId())))
                         ||(bufTrackerIdString.indexOf(String.valueOf(issueTracker.getId())+"|")==0)
                         ||((bufTrackerIdString.indexOf("|"+String.valueOf(issueTracker.getId()))==bufTrackerIdString.length()-String.valueOf(issueTracker.getId()).length()-1)&&(bufTrackerIdString.contains("|"+String.valueOf(issueTracker.getId())))))
                     checkboxTrackers.get(checkboxTrackers.size()-1).setSelected(true);
                        else
                     checkboxTrackers.get(checkboxTrackers.size()-1).setSelected(false);
                    
                        marginCounter++;
                        checkboxTrackers.get(checkboxTrackers.size()-1).setId(String.valueOf(issueTracker.getId()));              
                    }            
                } catch (RedmineException ex) {
                    LOG.addHandler(handler);
                    LOG.log(Level.SEVERE, null, ex);
                }
                Scene trackersScene = new Scene(trackersStackPane, 400, 600);
                trackers.setScene(trackersScene);                    
                trackers.show();
            }
        });
        selectTrackersButton.setOnAction((final ActionEvent e) -> {
            String bufTrackerIdString="";
            String bufStatusIdString="";
            
            for (int i=0;i<checkboxTrackers.size();i++) {    
                if(checkboxTrackers.get(i).selectedProperty().getValue())
                    {
                
                        bufTrackerIdString+="|"+checkboxTrackers.get(i).getId();
                    }
                }
                if(bufTrackerIdString.length()>0)
                    bufTrackerIdString=bufTrackerIdString.substring(1,bufTrackerIdString.length());
            
            for (int i=0;i<checkboxStatuses.size();i++) {    
                if(checkboxStatuses.get(i).selectedProperty().getValue())
                    {
                 
                        bufStatusIdString+="|"+checkboxStatuses.get(i).getId();
                    }
                }
                if(bufStatusIdString.length()>0)
                    bufStatusIdString=bufStatusIdString.substring(1,bufStatusIdString.length());
           
                trackerIdStringList.set(tabs.getSelectionModel().getSelectedIndex(), bufTrackerIdString);
                statusIdStringList.set(tabs.getSelectionModel().getSelectedIndex(), bufStatusIdString);
                //nameTabsStringList.set(tabs.getSelectionModel().getSelectedIndex(), tabs.getSelectionModel().getSelectedItem().getText());
                writeToTrackersOut();
                
                    checkboxTrackers.clear();
                    checkboxStatuses.clear();
            setTableIssues();
            if(!markedIssue.trim().isEmpty()&&timeStartInWork!=0)
                updateTableIssues();
            trackers.hide();
        });
        
        createButton.setOnAction((final ActionEvent e) -> {
            if(mgr!=null&&cc!=null){
             if(idProject.trim().isEmpty())
             {
                showProjectStage(); 
             }
             else
             {
            TextInputDialog tiDialog = new TextInputDialog("Задача");
            tiDialog.setTitle("Новая задача");
            tiDialog.setHeaderText(null);
            tiDialog.setContentText("Введите название задачи:");
            Optional<String> result = tiDialog.showAndWait();
            result.ifPresent(name -> {
                if(!name.trim().isEmpty())
                {
                 disableButtons(true);
                 URIBuilder builder;
                try {
                    builder = new URIBuilder(cc.getHost()+"/issues.json");
                    builder.addParameter("key", cc.getKey());

                    URI uriNew = builder.build();
            
                    HttpPost request = new HttpPost(uriNew);
                    
//allow to use this certificate
                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        trustStore.load(null, null);
                        
                        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                        
                        HttpParams params = new BasicHttpParams();
                        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                        
                        SchemeRegistry registry = new SchemeRegistry();
                        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                        registry.register(new Scheme("https", sf, 443));
                        
                        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
                        
                        HttpClient client = new DefaultHttpClient(ccm, params);

                        StringEntity paramsEntity =new StringEntity("{\"issue\": {\"project_id\": "+idProject+",\"subject\": \""+name+"\", \"assigned_to_id\": "+String.valueOf(idCurrentUser)+", "+((!idSubproject.trim().isEmpty())?"\"parent_issue_id\":"+idSubproject:"")+"}}","UTF-8");
                     
                        request.addHeader("Content-type", "application/json; charset=utf-8");
                        request.setEntity(paramsEntity);
             
                        HttpResponse response = client.execute(request);
                        alert.setTitle("Внимание");
                        alert.setHeaderText(null);
                        if(response.getStatusLine().getStatusCode()!=201)
                        {
                            alert.setContentText("Не удалось создать задачу!");
                            alert.showAndWait();
                        }
                        else
                        {
                            alert.setContentText("Задача была успешно создана!");
                            alert.showAndWait();
                            setTableIssues();
                                if(!markedIssue.trim().isEmpty()&&timeStartInWork!=0)
                                    updateTableIssues();
                                Params params2 = new Params()
                                    .add("set_filter", "1")
                                    .add("assigned_to_id", String.valueOf(idCurrentUser));//mgr.getUserManager().getCurrentUser().getId()));

                            try {
                                issuesAll = mgr.getIssueManager().getIssues(params2).getResults();

                            } catch (RedmineException ex) {
                                LOG.addHandler(handler);
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                        
                } catch (URISyntaxException | IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException | UnrecoverableKeyException ex) {
                    LOG.addHandler(handler);
                    LOG.log(Level.SEVERE, null, ex);
                } 
                disableButtons(false);
                }
            });
            }
        }
        });
        projectsButton.setOnAction((final ActionEvent e) -> {
          showProjectStage();  
        });
        Platform.setImplicitExit(false);
        //add application to tray
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
        showStage();
 
        Scene scene;
        
         try {
                        FileInputStream fis = new FileInputStream("size.out");
                        ObjectInputStream oin = new ObjectInputStream(fis);
                        SizesPositionClass spc = (SizesPositionClass) oin.readObject();
                        sceneWidth=spc.getWidth();
                        sceneHeight=spc.getHeight();
                        sceneX=spc.getX();
                        sceneY=spc.getY();
                        } catch (FileNotFoundException | ClassNotFoundException ex) {    
                            if(windowsSystem)
                                sceneWidth= 345;
                            else
                                sceneWidth= 395;
                            sceneHeight=400;
                            sceneX=0;
                            sceneY=0;
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
            
        AnchorPane.setTopAnchor(tabs, 5.0);
        AnchorPane.setLeftAnchor(tabs, 5.0);
        AnchorPane.setRightAnchor(tabs, 5.0);
        AnchorPane.setBottomAnchor(tabs, 5.0);
        AnchorPane.setTopAnchor(addTabButton, 10.0);
        AnchorPane.setLeftAnchor(addTabButton, 10.0);
     
        rootAnchorPane.getChildren().addAll(tabs, addTabButton);
        addTabButton.setOnAction((final ActionEvent e) -> {
                final Tab tab = new Tab();
        
                ObservableList<RowIssue> rowListnew = FXCollections.observableArrayList();
                rowListnew.add(new RowIssue("","","","","",""));
                rowIssueList.add(rowListnew);
                statusIdStringList.add("");
                trackerIdStringList.add("");
                nameTabsStringList.add("Вкладка "+(tabs.getTabs().size()+1));
                tab.setOnClosed((Event t) -> {
                    rowIssueList.remove(tabs.getSelectionModel().getSelectedIndex()+1);
                    statusIdStringList.remove(tabs.getSelectionModel().getSelectedIndex()+1);
                    trackerIdStringList.remove(tabs.getSelectionModel().getSelectedIndex()+1);
                    nameTabsStringList.remove(tabs.getSelectionModel().getSelectedIndex()+1);
                    writeToTrackersOut();
                });
                Label tabALabel = new Label("Вкладка " + (tabs.getTabs().size() + 1));
                tab.setGraphic(tabALabel);
          
                tabALabel.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (mouseEvent.getClickCount() == 2) {
                            TextInputDialog tiDialog = new TextInputDialog("Вкладка");
                            tiDialog.setTitle("Изменение названия вкладки");
                            tiDialog.setHeaderText("Заполните поле ниже");
                            tiDialog.setContentText("Введите название владки:");
                                        
                            Optional<String> result = tiDialog.showAndWait();
                            result.ifPresent(name -> {
                                if(!name.trim().isEmpty())
                                {
                                    tabALabel.setText(name);
                                    nameTabsStringList.set(tabs.getSelectionModel().getSelectedIndex(), name);
                                    writeToTrackersOut();
                                }
                            });                                     
                        }
                    }
                });
                tabs.getTabs().add(tab);
                tabs.getSelectionModel().select(tab);
      
            });

        tabs.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue, Number newValue) -> {
            setTableIssues();
            if((oldValue!=newValue)&&(timeStartInWork==0))
            {
                selectedIssue=0;
                table.getSelectionModel().select(null);
            }
            
            updateTableIssues();
           
        }); 
        rootAnchorPane.setMaxWidth(table.getMaxWidth()+10);
        rootAnchorPane.setMaxHeight(50);
        StackPane.setMargin(rootAnchorPane, new Insets(0.0,0.0,0.0,0.0)); 
        StackPane.setAlignment(rootAnchorPane, Pos.TOP_LEFT);     

        root.getChildren().add(rootAnchorPane);
        scene= new Scene(root, sceneWidth, sceneHeight);

        scene.setFill(Color.TRANSPARENT);
        stage.setX(sceneX);
        stage.setY(sceneY);
        stage.setTitle(PROGRAM_NAME);
        stage.setScene(scene);
        stage.setOnCloseRequest((WindowEvent we) -> {
       
            sceneWidth=stage.getWidth();
            sceneHeight=stage.getHeight();
      
        });
        stage.focusedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if(t)
            {
                sceneWidth=stage.getWidth();
                sceneHeight=stage.getHeight();
             
            }
        });

      
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
