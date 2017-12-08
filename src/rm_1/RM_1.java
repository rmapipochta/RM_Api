
package rm_1;


import java.awt.AWTException;
import javafx.scene.text.Font;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
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
import rm_1.redmineapi.CustomFieldManager;
import rm_1.redmineapi.Include;
import rm_1.redmineapi.Params;
import rm_1.redmineapi.bean.CustomFieldDefinition;
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
    private CustomizeClass custClass;
    private int ID_STATUS_IN_WORK=0;//3;
    private int ID_STATUS_NEW=0;//2;
    private int ID_STATUS_FINISHED=0;//4;
    private int ID_STATUS_DEFER=0;//30;
    private int ID_STATUS_VALUATION_SPENT_TIME=0;//29;
    private int ID_ACTIVITY_DEVELOPMENT=0;//11;
    private int ID_ACTIVITY_ENGINEERING=0;//10;
    private static final String ICON_IMAGE_LOC =
            "http://icons.iconarchive.com/icons/scafer31000/bubble-circle-3/16/GameCenter-icon.png";
    private static final String PROGRAM_NAME= "QuestBook v3.1.7";
    private long timeStartInWork=0;//time when issue was started
    private int selectedIssue=0;//id of selected issue
    private String markedIssue="";//issue which marked to change color in table pf selected Issue
    private int issueIdSpentHours=0;//finished issue which need to enter spent hours
    private int idCurrentUser=0;//id of user who connected
    private String idProject ="";
    private String idSubproject ="";
    private Stage stage;//main stage of application
    private String urlIssue="";//url of selected issue
    private String dirHome="";//home folder user
    // a timer allowing the tray icon to provide a periodic notification event.
    private Timer notificationTimer;
    private Timer workTimer = new Timer();
//    private Timer onlineTimer = new Timer(); //sending http request that user online
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
    private final TableColumn<RowIssue, String> author = new TableColumn("Автор задачи");//row with priority id. need to sort
    private final ObservableList<RowIssue> rowList = FXCollections.observableArrayList(); 
    private final Stage descriptionStage = new Stage();//stage to view description and comments
    private final VBox descriptionVbox = new VBox(20);
    private final TextArea descriptionText=new TextArea();
    private final TextArea descriptionComments=new TextArea();
    private final TextArea tfNewComment=new TextArea();
    private final Button addComment = new Button();
    private final ScrollPane commentsScroll = new ScrollPane();
    private final ScrollPane descriptionScroll = new ScrollPane();
    private final Stage deferStage = new Stage();//stage to defer issues
    private final VBox deferVbox = new VBox(20);
    private final Pane deferPane = new Pane();
    
    private final Label lbDeferTime=new Label("Время");
    private final Label lbDeferDate=new Label("Дата");
    private final Label lbDeferTo=new Label("До");
    private final TextField tfDeferTime=new TextField();
    private final DatePicker deferDatePicker = new DatePicker();
    private final Button deferTimeButton = new Button();
    private final Button deferDayButton = new Button();
    private final Button deferHourButton = new Button();
    private final Button deferAfterButton = new Button();
    private final ProgressBar progressBarDefer = new ProgressBar();
    
    private final Stage dialogStage = new Stage();//stage to connect window
    private final VBox dialogVbox = new VBox(20);
    private final TextField tfHost=new TextField("https://redmine.post.msdnr.ru");
    private final TextField tfHostDefer=new TextField("http://rmdash.post.msdnr.ru");
    private final TextField tfPriorityDefer=new TextField("3");
    private final TextField tfKey=new TextField();
    private final TextField tfSIW=new TextField("3");
    private final TextField tfSN=new TextField("2");
    private final TextField tfSF=new TextField("4");
    private final TextField tfSD=new TextField("30");
    private final TextField tfSVSH=new TextField("29");
    private final TextField tfAD=new TextField("11");
    private final TextField tfAE=new TextField("10");
    private final Label lbHost=new Label("Хост");
    private final Label lbHostDefer=new Label("Хост отложенных");
    private final Label lbPriorityDefer=new Label("Ид приоритета для отложенных");
    private final Label lbKey=new Label("Ключ");
    private final Label lbStatusesText=new Label("Идентификаторы статусов задач");
    private final Label lbSIW=new Label("В работе");
    private final Label lbSN=new Label("Новая");
    private final Label lbSF=new Label("Решена");
    private final Label lbSD=new Label("Отложена");
    private final Label lbSVSH=new Label("Оценка трудозатрат");
    private final Label lbActivityText=new Label("Идентификаторы типов деятельности");
    private final Label lbAD=new Label("Разработка");
    private final Label lbAE=new Label("Проектирование");
    private final Button connectButton = new Button();
    private final Stage trackersStage = new Stage();//stage to view trackers and statuses
    private final List<CheckBox> checkboxTrackers = new ArrayList();
    private final List<CheckBox> checkboxStatuses = new ArrayList();
    private List<IssueStatus> statusesList = null;
    private List<Tracker> trackersList = null;
    private final Button selectTrackersButton = new Button();
    private StackPane trackersStackPane;   
    private StackPane browserStackPane;   
    private final Stage browserStage = new Stage();//stage to select browser
    private final CheckBox defaultBrowserCheckBox = new CheckBox("По умолчанию");
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
    private final Stage spentHoursStage = new Stage();//stage to add spent hours
    private final VBox spentHoursVbox = new VBox(20);
    private final Label lbSpentHours=new Label();
    private final TextField tfSpentHours=new TextField();
    private final Button addSpentHours= new Button();
    private ConnectClass cc;  
    private ConnectDeferClass connDefer;  
    private final Image urlImage = new Image("images/url.png",30,30,false,false);
    private final Button urlButton = new Button("",new ImageView(urlImage));
    private final Image startImage = new Image("images/start.png",30,30,false,false);
    private final Image startImage24 = new Image("images/start.png",24,24,false,false);
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
    private final Image browserImage = new Image("images/browser.png",30,30,false,false);
    private final Button browserButton = new Button("",new ImageView(browserImage));   
    private final Image stopImage = new Image("images/stop.png",30,30,false,false);
    private final Image stopImage24 = new Image("images/stop.png",24,24,false,false);
    private final Button stopButton = new Button("",new ImageView(stopImage));   
    private final Label lbTime=new Label(""); 
    private final AnchorPane rootAnchorPane = new AnchorPane();
    private final TabPane tabs = new TabPane();
    private final Button addTabButton = new Button("+");
    private final Button selectBrowserFileButton = new Button("Выбрать");
    private final ToggleGroup selectBrowserToggleGroup = new ToggleGroup();
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
    private final ButtonType okButtonType = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
    private TextInputDialog tiDialog = new TextInputDialog();
    private String selectedBrowser = "";
    private boolean defaultBrowser = true;
    private boolean useDeferHost = false;
    private String userName="";
    
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
                        FileOutputStream fos = new FileOutputStream(dirHome+"conn.out");
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
                        userName=mgr.getUserManager().getCurrentUser().getFullName();
                         try {
                            userName=URLEncoder.encode(userName, "UTF-8");
                            } catch (UnsupportedEncodingException ex) {
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);
                        }
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
          
                    dialogStage.hide();                
      }
    private void addExtraSpentHours(String name){
       if((issueIdSpentHours!=0)&&(!name.trim().isEmpty())&&Float.parseFloat(name.replaceAll(",",".").replaceAll(":","."))>0)
       {
           TimeEntry TE=new TimeEntry();          
           TE.setHours(Float.parseFloat(convertMinutesToSpentHours(name)));
           TE.setIssueId(issueIdSpentHours);
           TE.setActivityId(ID_ACTIVITY_ENGINEERING); 
           try {
                mgr.getTimeEntryManager().createTimeEntry(TE);
               } catch (RedmineException ex) {
                   alert.setTitle("Внимание");
                   alert.setHeaderText(null);
                   alert.setContentText("Трудозатраты не удалось сохранить! Задача: " +issueIdSpentHours+" Трудозатраты: "+TE.getHours());
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
                      //change image in tray
                      BufferedImage trayImage = SwingFXUtils.fromFXImage(stopImage24,null);             
                      trayIcon.setImage(trayImage);
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
                                String spentHoursStringBuf=convertSpentHours(mgr.getIssueManager().getIssueById(selectedIssue).getSpentHours().toString());
                                spentHoursMap.replace(selectedIssue,spentHoursStringBuf);
                                for (int i = 0; i < table.getItems().size(); i++) {
                                   if (Integer.valueOf(table.getItems().get(i).getIdCol()) == selectedIssue) {                                    
                                       rowIssueList.get(tabs.getSelectionModel().getSelectedIndex()).get(i).setSpentHoursCol(spentHoursStringBuf);

                                       priorityIdCol.setSortType(TableColumn.SortType.DESCENDING);                                   
                                       table.setItems(rowIssueList.get(tabs.getSelectionModel().getSelectedIndex()));
                                       table.getSortOrder().add(priorityIdCol);
                                       break;
                                   }
                               }
                           } catch (RedmineException ex) {
                               alert.setTitle("Внимание");
                               alert.setHeaderText(null);
                               alert.setContentText("Трудозатраты не удалось сохранить! Задача: " +selectedIssue+" Трудозатраты: "+TE.getHours());
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
            BufferedImage trayImage = SwingFXUtils.fromFXImage(stopImage24,null);
            trayIcon = new java.awt.TrayIcon(trayImage);

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
                        FileOutputStream fos = new FileOutputStream(dirHome+"size.out");
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
                    if(selectedIssue!=0&&timeStartInWork!=0&& useDeferHost)
                       sendAction(String.valueOf(selectedIssue),"Пауза");
                saveSpentHours();
                workTimer.cancel();
                pauseTimer();
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
            
        } catch (Exception ex) {
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
   private void multilineRowTheme()
    {        
        themeCol.setCellFactory((TableColumn<RowIssue, String> col) -> {
        TableCell<RowIssue, String> cell = new TableCell<RowIssue, String>(){
            Text text;
        @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        text = new Text(getItem());
                        text.setWrappingWidth(themeCol.getWidth());                        
                        this.setWrapText(true);   
                        setGraphic(text);
                    }
                }
        };
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
                    String BufText="Описание:"+((issueByIdWithDescription.getDescription()!=null)?issueByIdWithDescription.getDescription():"");
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
                  
                    //set text in textfields
                    msg.append(BufText);
                    
                    descriptionText.setText(msg.toString());
                    
                    
                    descriptionText.setWrapText(true);
                    descriptionComments.setWrapText(true);
//                    descriptionText.setTextAlignment(TextAlignment.JUSTIFY);
//                   descriptionComments.setTextAlignment(TextAlignment.JUSTIFY);
                    descriptionText.setEditable(false);
                    descriptionComments.setEditable(false);
                    descriptionComments.setBorder(Border.EMPTY);
                    descriptionComments.setBackground(Background.EMPTY);
                    descriptionText.setBorder(Border.EMPTY);
                    descriptionText.setBackground(Background.EMPTY);
                    
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
                descriptionStage.show();
                descriptionText.setMaxWidth(descriptionStage.getWidth()-30);
                descriptionComments.setMaxWidth(descriptionStage.getWidth()-30);
                descriptionStage.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {
                  descriptionText.setMaxWidth(descriptionStage.getWidth()-30);
                  descriptionComments.setMaxWidth(descriptionStage.getWidth()-30);
                });
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
                if(event.isControlDown()||event.getClickCount() == 2)
                {
                    if((defaultBrowser)||(selectedBrowser.trim().isEmpty()))
                        getHostServices().showDocument(urlIssue);
                    else
                         try {
                             Runtime.getRuntime().exec(selectedBrowser+" "+urlIssue);                       
                         } catch (IOException ex) {
                             Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
                         }
                }
                else if(event.isShiftDown())
                {
                    tiDialog.getEditor().setText("00:01");
                    tiDialog.getEditor().requestFocus();
                    tiDialog.setTitle(PROGRAM_NAME);
                    tiDialog.setHeaderText(null);
                    tiDialog.setContentText("Введите трудозатраты:");
                                     
                    Optional<String> result = tiDialog.showAndWait();
                    result.ifPresent(name -> {
                          if(!name.trim().isEmpty())
                          {
                              int idOfIssueWhereChangeSpentHours=Integer.valueOf(table.getSelectionModel().getSelectedItem().getIdCol());
                                TimeEntry TE=new TimeEntry();                                
                                TE.setHours(Float.parseFloat(convertMinutesToSpentHours(name)));
                                TE.setIssueId(idOfIssueWhereChangeSpentHours);
                                TE.setActivityId(ID_ACTIVITY_ENGINEERING); 
                                try {
                                     mgr.getTimeEntryManager().createTimeEntry(TE);
                                     changeIssueStatusFromNew(idOfIssueWhereChangeSpentHours,ID_STATUS_VALUATION_SPENT_TIME, false);
                     
                                     String spentHoursStringBuf=mgr.getIssueManager().getIssueById(idOfIssueWhereChangeSpentHours).getSpentHours().toString();
                                    spentHoursMap.replace(idOfIssueWhereChangeSpentHours,convertSpentHours(spentHoursStringBuf));
                                    setTableIssues();
                                    updateTableIssues();
                                    } catch (RedmineException ex) {
                                        alert.setTitle("Внимание");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Трудозатраты не удалось сохранить! Задача: "+idOfIssueWhereChangeSpentHours+" Трудозатраты:"+TE.getHours());
                                        alert.showAndWait();
                                        LOG.addHandler(handler);
                                        LOG.log(Level.SEVERE, null, ex);
                                    }
                          }
                    });
                    tiDialog = new TextInputDialog();
                }
                
               }catch(NumberFormatException ex){
                   LOG.addHandler(handler);
                   LOG.log(Level.SEVERE, null, ex);
               }
            }
        });
        return cell ;
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
                        currentRow.setTooltip(new Tooltip(currentRow.getItem().getAuthor()));
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
                                            Text text = new Text(issue.getSubject());                                          
                                            text.setFont(new Font(12));
                                            text.setWrappingWidth(100.0);
                                            
                                            text.setTextAlignment(TextAlignment.JUSTIFY);
                            
                                         bufTheme=issue.getSubject();
                                         
                                   
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
                                                        spentHoursStringBuf=convertSpentHours(spentHoursStringBuf);
                                                    spentHoursMap.put(issue.getId(), spentHoursStringBuf);
                                                }
                                                bufRowList.add(new RowIssue(issue.getId().toString(),issue.getStatusName(),issue.getPriorityText(),bufTheme,spentHoursStringBuf,issue.getPriorityId().toString(),issue.getAuthorName()));
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
             bufRowList.add(new RowIssue("","","","","","",""));            
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
        issues=null;
        
    }
    private void changeIssueStatusFromNew(int idOfUpdatedIssue, int newIdStatus, boolean flag)
    {
        try {
            Issue updatedIssue = mgr.getIssueManager().getIssueById(idOfUpdatedIssue);
            if(updatedIssue.getStatusId()==ID_STATUS_NEW)
            {
            updatedIssue.setStatusId(newIdStatus);
                           
            mgr.getIssueManager().update(updatedIssue);
            String spentHoursStringBuf=convertSpentHours(updatedIssue.getSpentHours().toString());
            
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
                                if((idCurrentUser!=0)&&(issuesAll!=null))
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
                    60_000,
                    60_000
            );

    }
    private void disableButtons(boolean dis)
    {
        try{
//            if(dis)
//             stage.getScene().setCursor(Cursor.WAIT);
//            else
             stage.getScene().setCursor(Cursor.DEFAULT);
        }
        catch(Exception ex){          
        }
        startButton.setDisable(dis);
        finishButton.setDisable(dis);
        urlButton.setDisable(dis);
        deferButton.setDisable(dis);
      //  settingsButton.setDisable(dis);
        createButton.setDisable(dis);
        stopButton.setDisable(dis);
        trackerButton.setDisable(dis);
        browserButton.setDisable(dis);
        projectsButton.setDisable(dis);
        if(dis)
        {
            pauseTimer();
        }
        else
        {
            resumeTimer();
        }
    }
    private boolean setConfigParams()
    {
        try{
                    ID_STATUS_IN_WORK=Integer.valueOf(tfSIW.getText());
                  }
                  catch(NumberFormatException ex){
                      ID_STATUS_IN_WORK=0;
                  }
                  try{
                  ID_STATUS_NEW=Integer.valueOf(tfSN.getText());
                  }
                  catch(NumberFormatException ex){
                      ID_STATUS_NEW=0;
                  }
                  try{
                  ID_STATUS_FINISHED=Integer.valueOf(tfSF.getText());
                  }
                  catch(NumberFormatException ex){
                      ID_STATUS_FINISHED=0;
                  }
                  try{
                  ID_STATUS_DEFER=Integer.valueOf(tfSD.getText());
                  }
                  catch(NumberFormatException ex){
                      ID_STATUS_DEFER=0;
                  }
                  try{
                  ID_STATUS_VALUATION_SPENT_TIME=Integer.valueOf(tfSVSH.getText());
                  }
                  catch(NumberFormatException ex){
                      ID_STATUS_VALUATION_SPENT_TIME=0;
                  }
                  try{
                  ID_ACTIVITY_DEVELOPMENT=Integer.valueOf(tfAD.getText());
                  }
                  catch(NumberFormatException ex){
                      ID_ACTIVITY_DEVELOPMENT=0;
                  }
                  try{
                      ID_ACTIVITY_ENGINEERING=Integer.valueOf(tfAE.getText());
                  }
                  catch(NumberFormatException ex){
                      ID_ACTIVITY_ENGINEERING=0;
                  }
                  custClass = new CustomizeClass(ID_STATUS_IN_WORK,ID_STATUS_NEW,
                    ID_STATUS_FINISHED,ID_STATUS_DEFER,ID_STATUS_VALUATION_SPENT_TIME,
                    ID_ACTIVITY_DEVELOPMENT, ID_ACTIVITY_ENGINEERING);
                  try { 
                      if(custClass!=null && custClass.isOk())
                      {
                        FileOutputStream fos = new FileOutputStream(dirHome+"config.out");
                            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                                oos.writeObject(custClass);
                                oos.flush();
                                return true;
                            }
                     catch (FileNotFoundException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
                  }
                  }catch (IOException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
        return false;
    }
    private boolean getConfigParams() throws IOException
    {
        try {
                        FileInputStream fis = new FileInputStream(dirHome+"config.out");
                        ObjectInputStream oin = new ObjectInputStream(fis);
                        custClass = (CustomizeClass) oin.readObject(); 
                        if(custClass!=null && custClass.isOk())
                        {
                            ID_STATUS_IN_WORK=custClass.getID_STATUS_IN_WORK();
                            ID_STATUS_NEW=custClass.getID_STATUS_NEW();
                            ID_STATUS_FINISHED=custClass.getID_STATUS_FINISHED();
                            ID_STATUS_DEFER=custClass.getID_STATUS_DEFER();
                            ID_STATUS_VALUATION_SPENT_TIME=custClass.getID_STATUS_VALUATION_SPENT_TIME();
                            ID_ACTIVITY_DEVELOPMENT=custClass.getID_ACTIVITY_DEVELOPMENT();
                            ID_ACTIVITY_ENGINEERING=custClass.getID_ACTIVITY_ENGINEERING();
                            return true;
                        }
                        
                        } catch (FileNotFoundException | ClassNotFoundException ex) { 
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);         
                            return false;
                           
                    }
        return false;
    }
    
    private boolean setDeferParams()
    {
            String hostDefer;
            int idPriorityDefer;
                try{
                     hostDefer=tfHostDefer.getText();
                  }
                  catch(NumberFormatException ex){
                      hostDefer="";
                  }
                  try{
                  idPriorityDefer=Integer.valueOf(tfPriorityDefer.getText());
                  }
                  catch(NumberFormatException ex){
                      idPriorityDefer=-1;
                  }
                  if(hostDefer.equals("") || idPriorityDefer==-1)
                    connDefer = null;
                  else
                      connDefer = new ConnectDeferClass(hostDefer,idPriorityDefer);
                  try { 
                      if(connDefer!=null)
                      {
                        FileOutputStream fos = new FileOutputStream(dirHome+"connDefer.out");
                            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                                oos.writeObject(connDefer);
                                oos.flush();
                                return true;
                            }
                     catch (FileNotFoundException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
                  }
                  }catch (IOException ex) {
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                    }
        return false;
    }
    private boolean getDeferParams() throws IOException
    {
        try {
                        FileInputStream fis = new FileInputStream(dirHome+"connDefer.out");
                        ObjectInputStream oin = new ObjectInputStream(fis);
                        connDefer = (ConnectDeferClass) oin.readObject(); 
                        if(connDefer!=null)
                        {
                           
                            return true;
                        }
                        
                        } catch (FileNotFoundException | ClassNotFoundException ex) { 
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);
                            return false;
                    }
        return false;
    }
    
    private void updateAllSpentHours()
    {
    disableButtons(true);
    spentHoursMap.clear();
    try {
        Params params2 = new Params()
            .add("set_filter", "1")
            .add("assigned_to_id", String.valueOf(idCurrentUser));

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
        spentHoursMap.put(issue.getId(),convertSpentHours(spentHoursStringBuf));
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
                    lbSubproject.setStyle("-fx-font-size:12px;");
                    lbSubproject.setText("Родительская задача");
                    selectProjectButton.setText("Применить");
                    projectsScroll.setContent(projectsToggleGroupVbox);
                    projectsVbox.getChildren().addAll(projectsScroll);
                    projectsVbox.getChildren().add(lbSubproject);
                    projectsVbox.getChildren().add(tfSubproject);
                    projectsVbox.getChildren().add(selectProjectButton);
                 
                    VBox.setMargin(projectsScroll, new Insets(5.0,5.0,0.0,5.0));
                    VBox.setMargin(lbSubproject, new Insets(5.0,5.0,15.0,5.0));
                    VBox.setMargin(tfSubproject, new Insets(-40.0,5.0,15.0,145.0));
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
                    projectsList.clear();
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
                        FileOutputStream fos = new FileOutputStream(dirHome+"trackers.out");
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
private String convertSpentHours(String s)
{
    if(s.equals("Новая"))return s;
    String min;
    try{
        min = s.substring(s.indexOf(".")+1);
        if(min.length()!=0){
        float minFloat=Float.parseFloat(s);
        minFloat-=Math.floor(minFloat);        
        int minInt = (int)Math.round(minFloat*60);
        if(minInt>=60)
        {
            s=String.valueOf(Float.parseFloat(s)+1);
            s=s.substring(0, s.indexOf("."))+":00";
        }
        else
        {
            s=s.substring(0, s.indexOf("."))+":"+((minInt<10)?"0":"")+String.valueOf(minInt);
        }
        
        }
        else
            s=s.substring(0, s.indexOf("."))+":00";
        
    }catch(NumberFormatException ex){
     LOG.addHandler(handler);
     LOG.log(Level.SEVERE, null, ex);
    }

    return s;
}
private String convertMinutesToSpentHours(String s)
{
    if(!s.trim().isEmpty()&&Float.parseFloat(s.replaceAll(",",".").replaceAll(":","."))>=0)
        try{          
            s = s.replaceAll(",",".").replaceAll(":",".");
            if(s.contains(".")&&s.indexOf(".")!=s.length()-1)
            {
                Integer min = Integer.valueOf(s.substring(s.indexOf(".")+1));
                if(s.indexOf(".")==s.length()-2)
                {
                    min*=10;
                }
                min = (int)Math.ceil((float)min/60*100);
                if(min>99)
                {
                    s=String.valueOf(Float.parseFloat(s)+1);
                    min=0;
                }
                s=s.substring(0, s.indexOf(".")+1)+((min<10)?"0":"")+String.valueOf(min);
            }
        }catch(NumberFormatException ex){
         LOG.addHandler(handler);
         LOG.log(Level.SEVERE, null, ex);
        }

    return s;
}
private static String readFile(String path, Charset encoding) 
  throws IOException 
{
  byte[] encoded = Files.readAllBytes(Paths.get(path));
  return new String(encoded, encoding);
}

    private void setDeferTime(String dateReturn){
         progressBarDefer.setVisible(true);
         try{
                if(table.getSelectionModel().getSelectedItem()!=null){
                    int idDeferIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
                   
                              
                    String url = connDefer.getHostDefer()+"/create?host="+cc.getHost()+"&api_key="+cc.getKey()+"&id_issue="+idDeferIssue+"&date_return="+dateReturn+"&status_before="+mgr.getIssueManager().getIssueById(idDeferIssue).getStatusId()+"&status_defer="+custClass.getID_STATUS_DEFER();
                                       
                    changeIssueStatusFromNew(idDeferIssue,ID_STATUS_DEFER,true);                  
                    setTableIssues();
                    if(!markedIssue.trim().isEmpty()&&timeStartInWork!=0)
                    updateTableIssues(); 
                    multilineRowTheme();              
                    URL obj;
                    if(useDeferHost)
                    sendAction(String.valueOf(idDeferIssue),"Отложил");
                    try {
                        obj = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                        connection.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                         }
                        in.close();
                        System.out.println(response.toString());
                        if(!response.toString().equals("true")){
                            alert.setTitle("Внимание");
                            alert.setHeaderText(null);
                            alert.setContentText("Возникли проблемы. Автоматический возврат произведен не будет.");
                            alert.showAndWait();
                        
                        }
                        
                        } catch (MalformedURLException ex) {
                             Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
                             alert.setTitle("Внимание");
                            alert.setHeaderText(null);
                            alert.setContentText("Неправильно задано имя хоста для отложенных задач. Автоматический возврат произведен не будет!");
                            alert.showAndWait();
                              LOG.addHandler(handler);
                              LOG.log(Level.SEVERE, null, ex);
                         } catch (ProtocolException ex) {
                            Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
                              LOG.addHandler(handler);
                              LOG.log(Level.SEVERE, null, ex);
                         } catch (IOException ex) {
                               alert.setTitle("Внимание");
                               alert.setHeaderText(null);
                            alert.setContentText("Неправильно задано имя хоста для отложенных задач. Автоматический возврат произведен не будет!");
                            alert.showAndWait();
                             Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
                               LOG.addHandler(handler);
                              LOG.log(Level.SEVERE, null, ex);
                        }
                       
                }
            }catch(NumberFormatException ex){
                LOG.addHandler(handler);
                LOG.log(Level.SEVERE, null, ex);
            } catch (RedmineException ex) {  
                Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
            }
            progressBarDefer.setVisible(false);
            disableButtons(false);
    }
    //send to server dashboard info about action user
    private void sendAction(String issue, String action){
       
                   
                    int idActionIssue =Integer.parseInt(issue);                  
                   try {
                            issue=mgr.getIssueManager().getIssueById(idActionIssue).getSubject();
                            issue=URLEncoder.encode(issue, "UTF-8");
                            action=URLEncoder.encode(action, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);
                    } catch (RedmineException ex) {
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);
                    }
                   
                    long unixTime = System.currentTimeMillis() / 1000L;
                 //   System.out.println(String.valueOf(idCurrentUser));
                    String url = connDefer.getHostDefer()+"/actions/create?user="+userName+"&action="+action+"&issue="+issue+"&time="+String.valueOf(unixTime)+"&id_issue="+idActionIssue+"&id_user="+idCurrentUser+"&id_parent="+((!idSubproject.trim().isEmpty())?idSubproject:"")+"&id_project="+idProject;
                                                         
                    URL obj;
                    try {
                        obj = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                        connection.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                         }
                        in.close();
                        System.out.println(response.toString());
                        if(!response.toString().equals("true")){
//                            alert.setTitle("Внимание");
//                            alert.setHeaderText(null);
//                            alert.setContentText("Возникли проблемы.");
//                            alert.showAndWait();
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, "statistic not send");
                        
                        }
                        
                        } catch (MalformedURLException ex) {
                             Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
//                             alert.setTitle("Внимание");
//                            alert.setHeaderText(null);
//                            alert.setContentText("Статистика не отправлена");
//                            alert.showAndWait();
                              LOG.addHandler(handler);
                              LOG.log(Level.SEVERE, null, ex);
                         } catch (ProtocolException ex) {
                            Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
                              LOG.addHandler(handler);
                              LOG.log(Level.SEVERE, null, ex);
                         } catch (IOException ex) {
//                               alert.setTitle("Внимание");
//                               alert.setHeaderText(null);
//                            alert.setContentText("Статистика не отправлена");
//                            alert.showAndWait();
                             Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
                               LOG.addHandler(handler);
                              LOG.log(Level.SEVERE, null, ex);
                        }
                       
             
    }
//    //send to server dashboard info about status online
//    private void sendOnline(){
//      
//                    String url = connDefer.getHostDefer()+"/actions/online?id_user="+idCurrentUser;                                                      
//                    URL obj;
//                    try {
//                        obj = new URL(url);
//                        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
//                        connection.setRequestMethod("GET");
//                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                        String inputLine;
//                        StringBuffer response = new StringBuffer();
//                        while ((inputLine = in.readLine()) != null) {
//                            response.append(inputLine);
//                         }
//                        in.close();                     
//                        if(!response.toString().equals("true")){//                      
//                            LOG.addHandler(handler);
//                            LOG.log(Level.SEVERE, null, "status not send");                        
//                        }
//                        
//                        } catch (MalformedURLException ex) {
//                             Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
//                              LOG.addHandler(handler);
//                              LOG.log(Level.SEVERE, null, ex);
//                         } catch (ProtocolException ex) {
//                            Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
//                              LOG.addHandler(handler);
//                              LOG.log(Level.SEVERE, null, ex);
//                         } catch (IOException ex) {
//                             Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
//                               LOG.addHandler(handler);
//                              LOG.log(Level.SEVERE, null, ex);
//                        }
//                       
//             
//    }
@Override
    public void start(Stage primaryStage) throws FileNotFoundException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException, RedmineException {
     
        tiDialog.getDialogPane().getButtonTypes().setAll(okButtonType, cancelButtonType);
         
        System.out.println(System.getProperty("user.home"));
        dirHome = System.getProperty("user.home")+"/.questbook";
        final File dir1 = new File(dirHome);
          if(!dir1.exists()) {
            if(dir1.mkdir()) {
                System.out.println("Каталог " + dir1.getAbsolutePath()
                        + " успешно создан.");
                dirHome +="/";
            } else {
                System.out.println("Каталог " + dir1.getAbsolutePath()
                        + " создать не удалось.");
                dirHome ="";
            }
        } else {
            System.out.println("Каталог " + dir1.getAbsolutePath()
                        + " уже существует.");
            dirHome +="/";
        }
        
        
        handler = new FileHandler(dirHome+"logger.log", 1000000, 7);
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
        descriptionStage.setTitle(PROGRAM_NAME);
        deferStage.setTitle("Отложить задачу");
        dialogStage.setTitle(PROGRAM_NAME);
        trackersStage.setTitle(PROGRAM_NAME);
        spentHoursStage.setTitle(PROGRAM_NAME);
        settingsButton.setTooltip(new Tooltip("Установить соединение"));
        urlButton.setTooltip(new Tooltip("Открыть задачу в браузере"));
        startButton.setTooltip(new Tooltip("Запустить задачу"));
        deferButton.setTooltip(new Tooltip("Отложить задачу"));
        finishButton.setTooltip(new Tooltip("Завершить задачу"));
        trackerButton.setTooltip(new Tooltip("Выбор отображаемых трекеров и статусов"));
        browserButton.setTooltip(new Tooltip("Выбор браузера для просмотра задач"));
        createButton.setTooltip(new Tooltip("Создать задачу"));
        stopButton.setTooltip(new Tooltip("Экстренная остановка"));
        projectsButton.setTooltip(new Tooltip("Выбрать проект, в который\n будут создаваться задачи"));

        if(!windowsSystem)table.setStyle("-fx-font-size:12px;");
                    descriptionStage.initModality(Modality.APPLICATION_MODAL);
                    descriptionStage.initOwner(primaryStage);
                    //set sizes and margin depending on OS
                    if(windowsSystem)
                    {
                        descriptionScroll.setMinHeight(140);
                        descriptionScroll.setMaxHeight(500);
                        commentsScroll.setMinHeight(100);
                        commentsScroll.setMaxHeight(500);
                        addComment.setMaxWidth(35);
                        themeCol.setMinWidth(210);              
                        table.setMaxWidth(285);
                        StackPane.setMargin(table, new Insets(40.0,5.0,5.0,5.0));
                        StackPane.setMargin(lbTime, new Insets(10.0,10.0,10.0,10.0));              
                       
                        lbKey.setMaxWidth(35);
                
                    }
                    else
                    {
                        descriptionScroll.setMinHeight(140);
                        descriptionScroll.setMaxHeight(500);
                        commentsScroll.setMinHeight(100);
                        commentsScroll.setMaxHeight(500);                
                        addComment.setMaxWidth(40);
                        themeCol.setMinWidth(250);             
                        table.setMaxWidth(325);
                        StackPane.setMargin(table, new Insets(40.0,5.0,5.0,5.0));
                        StackPane.setMargin(lbTime, new Insets(10.0,10.0,10.0,10.0));                        
            
                        lbKey.setMaxWidth(40);
               
                    }
                        lbHost.setMaxWidth(35);
                      //  lbHostDefer.setMaxWidth(35);
                        lbKey.setMaxWidth(40);
                        tfHost.setMaxWidth(200);
                        tfKey.setMaxWidth(200);
                        lbHost.setMaxHeight(40);
                      //  lbHostDefer.setMaxHeight(40);
                        lbKey.setMaxHeight(40);
                        tfHost.setMaxHeight(40);
                       // tfHostDefer.setMaxHeight(40);
                        tfKey.setMaxHeight(40);
                        tfSIW.setMaxWidth(80);
                        tfSN.setMaxWidth(80);
                        tfSD.setMaxWidth(80);
                        tfSF.setMaxWidth(80);
                        tfSVSH.setMaxWidth(80);
                        tfAD.setMaxWidth(80);
                        tfAE.setMaxWidth(80);
                    StackPane.setMargin(startButton, new Insets(30.0,5.0,5.0,20.0));
                    StackPane.setMargin(urlButton, new Insets(70.0,5.0,5.0,20.0));                        
                    StackPane.setMargin(finishButton, new Insets(110.0,5.0,5.0,20.0));
                    StackPane.setMargin(deferButton, new Insets(150.0,5.0,5.0,20.0));
                    StackPane.setMargin(createButton, new Insets(190.0,5.0,5.0,20.0));
                    StackPane.setMargin(stopButton, new Insets(230.0,5.0,5.0,20.0));
                    StackPane.setMargin(browserButton, new Insets(5.0,5.0,125.0,20.0));                        
                    StackPane.setMargin(trackerButton, new Insets(5.0,5.0,85.0,20.0));                        
                    StackPane.setMargin(projectsButton, new Insets(5.0,5.0,45.0,20.0));
                    StackPane.setMargin(settingsButton, new Insets(5.0,5.0,5.0,20.0));
                        
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
                   
                   
          
                    //components on defer pane
               
                    deferAfterButton.setText("На потом");
                    deferAfterButton.setLayoutX(20.0);
                    deferAfterButton.setLayoutY(20.0);
                   
                    
                    deferDayButton.setText("До завтра");
                    deferDayButton.setLayoutX(120.0);
                    deferDayButton.setLayoutY(20.0);
                    
                    deferHourButton.setText("На час");
                    deferHourButton.setLayoutX(230.0);
                    deferHourButton.setLayoutY(20.0);
                    
                    lbDeferTo.setLayoutX(20.0);
                    lbDeferTo.setLayoutY(80.0); 
                    
                    lbDeferTime.setLayoutX(210.0);
                    lbDeferTime.setLayoutY(50.0);
                    
                    lbDeferDate.setLayoutX(40.0);
                    lbDeferDate.setLayoutY(50.0);
                    
                    tfDeferTime.setLayoutX(200.0);
                    tfDeferTime.setLayoutY(80.0);
                    tfDeferTime.setMaxWidth(100);                   
                   
                    deferDatePicker.setLayoutX(40.0);
                    deferDatePicker.setLayoutY(80.0);
                    deferDatePicker.setMaxWidth(130);
                    
                    deferTimeButton.setText("ОК");
                    deferTimeButton.setLayoutX(300.0);
                    deferTimeButton.setLayoutY(80.0);
                    
                    progressBarDefer.setLayoutX(120.0);
                    progressBarDefer.setLayoutY(110.0);
                    progressBarDefer.setVisible(false);
                          
                    deferPane.getChildren().add(lbDeferTime);
                    deferPane.getChildren().add(lbDeferTo);
                    deferPane.getChildren().add(lbDeferDate);
                   
                    deferPane.getChildren().add(tfDeferTime);                              
                    
                    deferPane.getChildren().add(deferDayButton);
                    deferPane.getChildren().add(deferHourButton);
                    deferPane.getChildren().add(deferAfterButton);
                    deferPane.getChildren().add(deferTimeButton);
                    deferPane.getChildren().add(progressBarDefer);
                    deferPane.getChildren().add(deferDatePicker);
                            
                    addSpentHours.setText("ОК");
                    tfSpentHours.setMaxWidth(240);
                   
                    spentHoursVbox.getChildren().add(lbSpentHours);
                    spentHoursVbox.getChildren().add(tfSpentHours);
                    spentHoursVbox.getChildren().add(addSpentHours);
                    VBox.setMargin(lbSpentHours, new Insets(5.0,5.0,5.0,5.0));
                    VBox.setMargin(tfSpentHours, new Insets(-5.0,5.0,15.0,5.0));
                    VBox.setMargin(addSpentHours, new Insets(-60.0,5.0,15.0,260.0));
                    
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    dialogStage.initOwner(primaryStage);
                    connectButton.setText("Соединиться");
                    selectTrackersButton.setText("Принять");
                    VBox.setMargin(lbHost, new Insets(30.0,5.0,5.0,25.0));
                    VBox.setMargin(lbHostDefer, new Insets(-15.0,5.0,0.0,25.0));
                    VBox.setMargin(lbPriorityDefer, new Insets(-15.0,5.0,0.0,25.0));
                    VBox.setMargin(lbKey, new Insets(8.0,5.0,5.0,25.0));
                    VBox.setMargin(tfHost, new Insets(-95.0,5.0,5.0,65.0));
                    VBox.setMargin(tfHostDefer, new Insets(-20.0,30.0,5.0,25.0));
                    VBox.setMargin(tfPriorityDefer, new Insets(-20.0,30.0,5.0,25.0));
                    VBox.setMargin(tfKey, new Insets(0.0,5.0,5.0,65.0));
                    VBox.setMargin(lbStatusesText, new Insets(-10.0,5.0,5.0,65.0));
                    VBox.setMargin(lbSIW, new Insets(-20.0,5.0,5.0,25.0));
                    VBox.setMargin(lbSN, new Insets(-20.0,5.0,5.0,25.0));
                    VBox.setMargin(lbSF, new Insets(-20.0,5.0,5.0,25.0));
                    VBox.setMargin(lbSD, new Insets(-20.0,5.0,5.0,25.0));
                    VBox.setMargin(lbSVSH, new Insets(-20.0,5.0,5.0,25.0));
                    if(windowsSystem)
                        VBox.setMargin(lbActivityText, new Insets(-10.0,5.0,5.0,65.0));
                    else
                        VBox.setMargin(lbActivityText, new Insets(-10.0,5.0,5.0,35.0));
                    VBox.setMargin(lbAD, new Insets(-20.0,5.0,5.0,25.0));
                    VBox.setMargin(lbAE, new Insets(-20.0,5.0,5.0,25.0));
                    VBox.setMargin(tfSIW, new Insets(-45.0,5.0,5.0,150.0));
                    VBox.setMargin(tfSN, new Insets(-45.0,5.0,5.0,150.0));
                    VBox.setMargin(tfSF, new Insets(-45.0,5.0,5.0,150.0));
                    VBox.setMargin(tfSD, new Insets(-45.0,5.0,5.0,150.0));
                    VBox.setMargin(tfSVSH, new Insets(-45.0,5.0,5.0,150.0));
                    VBox.setMargin(tfAD, new Insets(-45.0,5.0,5.0,150.0));
                    VBox.setMargin(tfAE, new Insets(-45.0,5.0,5.0,150.0));
                    if(windowsSystem)
                        VBox.setMargin(connectButton, new Insets(-10.0,5.0,5.0,110.0));
                    else
                        VBox.setMargin(connectButton, new Insets(-10.0,5.0,5.0,100.0));
                    dialogVbox.getChildren().add(lbHost);
                    dialogVbox.getChildren().add(lbKey);
                    dialogVbox.getChildren().add(tfHost);
                    dialogVbox.getChildren().add(tfKey);
                    dialogVbox.getChildren().add(lbHostDefer);
                    dialogVbox.getChildren().add(tfHostDefer);
                    dialogVbox.getChildren().add(lbPriorityDefer);
                    dialogVbox.getChildren().add(tfPriorityDefer);
                    dialogVbox.getChildren().add(lbStatusesText);
                    dialogVbox.getChildren().add(lbSIW);
                    dialogVbox.getChildren().add(tfSIW);
                    dialogVbox.getChildren().add(lbSN);
                    dialogVbox.getChildren().add(tfSN);
                    dialogVbox.getChildren().add(lbSF);
                    dialogVbox.getChildren().add(tfSF);
                    dialogVbox.getChildren().add(lbSD);
                    dialogVbox.getChildren().add(tfSD);
                    dialogVbox.getChildren().add(lbSVSH);
                    dialogVbox.getChildren().add(tfSVSH);
                    dialogVbox.getChildren().add(lbActivityText);
                    dialogVbox.getChildren().add(lbAD);
                    dialogVbox.getChildren().add(tfAD);
                    dialogVbox.getChildren().add(lbAE);
                    dialogVbox.getChildren().add(tfAE);
                    dialogVbox.getChildren().add(connectButton);
                    Scene dialogScene;
                    if(windowsSystem)
                        dialogScene = new Scene(dialogVbox, 300, 500);
                    else
                        dialogScene = new Scene(dialogVbox, 320, 500);
                    dialogStage.setScene(dialogScene);
                    Scene descriptionScene;
                    if(windowsSystem)
                        descriptionScene = new Scene(descriptionVbox, 310, 300);
                    else
                        descriptionScene = new Scene(descriptionVbox, 380, 300);
                    descriptionStage.setScene(descriptionScene);
                     Scene deferScene;                
                     deferScene = new Scene(deferPane, 380, 150);
                     deferStage.setScene(deferScene);
                    Scene spentHoursScene = new Scene(spentHoursVbox, 310, 80);
                    spentHoursStage.setScene(spentHoursScene);
                    
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
        statusCol.setResizable(false);
        statusCol.setMaxWidth(0.0);
        priorityCol.setResizable(false);
        priorityCol.setMaxWidth(0.0);
        themeCol.setSortable(false);
        spentHoursCol.setSortable(false);
        idCol.setSortable(false);
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("idCol"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statusCol"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priorityCol"));
        themeCol.setCellValueFactory(new PropertyValueFactory<>("themeCol"));
        spentHoursCol.setCellValueFactory(new PropertyValueFactory<>("spentHoursCol"));
        priorityIdCol.setCellValueFactory(new PropertyValueFactory<>("priorityIdCol"));
    
        rowList.add(new RowIssue("","","","","","",""));
        table.setItems(rowList);
        table.getColumns().addAll(idCol, statusCol, priorityCol, themeCol, spentHoursCol, priorityIdCol);
        themeCol.setResizable(true);
        Label themeColHeaderLabel = new Label("                   Мои задачи               ");
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
        root.getChildren().add(browserButton);
        root.getChildren().add(createButton);
        root.getChildren().add(stopButton);
        root.getChildren().add(projectsButton);
        root.getChildren().add(settingsButton);
        
        StackPane.setAlignment(table, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(lbTime, Pos.TOP_RIGHT);
        StackPane.setAlignment(urlButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(startButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(finishButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(deferButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(trackerButton, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(browserButton, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(createButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(stopButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(projectsButton, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(settingsButton, Pos.BOTTOM_RIGHT);
        
        Tab tabMain = new Tab();
        tabMain.setText("Главная");
        tabMain.closableProperty().set(false);
        tabMain.setStyle("-fx-padding: 3 10 3 35;");
        tabs.getTabs().add(tabMain);
        
        //try to open file with information about trackers and statuses we need
                    try {
                        FileInputStream fis = new FileInputStream(dirHome+"trackers.out");
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
                        rowListnew.add(new RowIssue("","","","","","",""));
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
                                        tiDialog.getEditor().setText("Вкладка");
                                        tiDialog.getEditor().requestFocus();
                                        tiDialog.setTitle("Изменение названия вкладки");
                                        tiDialog.setHeaderText("Заполните поле ниже");
                                        tiDialog.setContentText("Введите название владки:");
                                        
                                        Optional<String> result = tiDialog.showAndWait();
                                        result.ifPresent(name -> {
                                            tabALabel.setText(name);
                                            nameTabsStringList.set(tabs.getSelectionModel().getSelectedIndex(), name);
                                            writeToTrackersOut();
                                        });
                                        tiDialog = new TextInputDialog();
                                    }
                                }
                            });
                            rowIssueList.add(rowListnew);
                            tabs.getTabs().add(tab);
                        }
                        } catch (FileNotFoundException | ClassNotFoundException ex) { 
                            trackerIdString="1|2|3|4|14|20";
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
                        FileInputStream fis = new FileInputStream(dirHome+"conn.out");
                        ObjectInputStream oin = new ObjectInputStream(fis);
                        cc = (ConnectClass) oin.readObject();
                        tfHost.setText(cc.getHost());
                        tfKey.setText(cc.getKey());
                        if(getConfigParams())
                            connectFunc();
                        tfSIW.setText(String.valueOf(ID_STATUS_IN_WORK));
                        tfSN.setText(String.valueOf(ID_STATUS_NEW));
                        tfSF.setText(String.valueOf(ID_STATUS_FINISHED));
                        tfSD.setText(String.valueOf(ID_STATUS_DEFER));
                        tfSVSH.setText(String.valueOf(ID_STATUS_VALUATION_SPENT_TIME));
                        tfAD.setText(String.valueOf(ID_ACTIVITY_DEVELOPMENT));
                        tfAE.setText(String.valueOf(ID_ACTIVITY_ENGINEERING));
                        if(getDeferParams()){
                            useDeferHost=true;
                            tfHostDefer.setText(connDefer.getHostDefer());
                            tfPriorityDefer.setText(String.valueOf(connDefer.getPriorityDefer()));
                        }
                        
                        setHeightOfRowsLinux();
                        
                        } catch (FileNotFoundException | ClassNotFoundException ex) { 
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);
                    }
                        try{
                            selectedBrowser=readFile("browser.out", StandardCharsets.UTF_8).trim();
                            defaultBrowser = selectedBrowser.isEmpty();
                        }
                        catch(IOException ex){
                            selectedBrowser="";
                            defaultBrowser = true;
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
                                if(setConfigParams())
                                //connect to api
                                {
                                    connectFunc();
                                    multilineRowTheme();
                                }
                                if(setDeferParams()){     
                                    useDeferHost=true;
                                }else  useDeferHost=false;
                            } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException | UnrecoverableKeyException ex) {
                                LOG.addHandler(handler);
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
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
        descriptionStage.hide();
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
              dialogStage.show();
        });
          
          finishButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);                     
               }
          });
          //add action of finishButton press
           finishButton.setOnAction((final ActionEvent e) -> {
               //disable buttons at the time of work
            
               disableButtons(true);
                
               BufferedImage trayImage = SwingFXUtils.fromFXImage(stopImage24,null);             
               trayIcon.setImage(trayImage);
                
               //if connection established and issue selected
               if((mgr!=null)&&(table.getSelectionModel().getSelectedItem()!=null))
               {
                   int idFinishedIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
                   if(useDeferHost)
                   sendAction(String.valueOf(idFinishedIssue),"Завершил");
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
                       tiDialog.getEditor().setText("00:01");
                       tiDialog.getEditor().requestFocus();
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
                        tiDialog = new TextInputDialog();
       
                       startButton.setGraphic(new ImageView(startImage));
                       startButton.setTooltip(new Tooltip("Запустить задачу"));
                       workTimer = new Timer();
                       
                       markedIssue="";
                       selectedIssue=0;
                       //get updated issues from api and insert into table
                       setTableIssues();
                       //make all cells of table without background
                       updateTableIssues();
                       multilineRowTheme();
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
                       tiDialog.getEditor().setText("00:01");
                       tiDialog.getEditor().requestFocus();
                        tiDialog.setTitle("Внести дополнительные трудозатраты");
                        tiDialog.setHeaderText(null);
                        tiDialog.setContentText("Введите трудозатраты:");

                        Optional<String> result = tiDialog.showAndWait();
                        result.ifPresent(name -> {
                              if(!name.trim().isEmpty())
                              {
                                 addExtraSpentHours(name); 
                              }
                        });
                        tiDialog = new TextInputDialog();
                       setTableIssues();
                       updateTableIssues();
                       multilineRowTheme();
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
                       tiDialog.getEditor().setText("00:01");
                       tiDialog.getEditor().requestFocus();
                        tiDialog.setTitle("Внести дополнительные трудозатраты");
                        tiDialog.setHeaderText(null);
                        tiDialog.setContentText("Введите трудозатраты:");

                        Optional<String> result = tiDialog.showAndWait();
                        result.ifPresent(name -> {
                              if(!name.trim().isEmpty())
                              {
                                 addExtraSpentHours(name); 
                              }
                        });
                        tiDialog = new TextInputDialog();
                       selectedIssue=0;
                       markedIssue="";
                       //get updated issues from api and insert into table
                       setTableIssues();
                       multilineRowTheme();
                   }  
                   try {
               Params params2 = new Params()
                            .add("set_filter", "1")
                            .add("assigned_to_id", String.valueOf(idCurrentUser));
              
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
                  
                   startButton.setGraphic(new ImageView(startImage));
                   startButton.setTooltip(new Tooltip("Запусить задачу"));
                   
                   if(selectedIssue!=0&&timeStartInWork!=0&& useDeferHost)
                       sendAction(String.valueOf(selectedIssue),"Пауза");
                  saveSpentHours();
                  //stop timer
                  workTimer.cancel();
                  //clear counter
                  lbTime.setText("");
                  
                  //update id of statuses and activities
                  if(setConfigParams())
                  
                  //connect to api
                  {
                      connectFunc();
                      multilineRowTheme();
                  }
                   if(setDeferParams()){     
                        useDeferHost=true;
                   }else  useDeferHost=false;
                 
              } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException | UnrecoverableKeyException ex) {
                  LOG.addHandler(handler);
                  LOG.log(Level.SEVERE, null, ex);
              }
        });
          
          
          
    class onStartButton extends Thread
    {   
	@Override
	public void run()
	{
//            try {
//                        System.out.println("Now");
//                        if(mgr!=null){
//                            CustomFieldManager customFieldManager =mgr.getCustomFieldManager();
//                            List<CustomFieldDefinition> customFieldDefinitions = customFieldManager.getCustomFieldDefinitions();
//
//                            for (CustomFieldDefinition customFieldDefinition : customFieldDefinitions) {                        
//                             System.out.println( customFieldDefinition.getName());
//                            }
//                        }
//                     } catch (RedmineException ex) {
//                         Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
//                     }
              try{
                   changeIssueStatusFromNew(selectedIssue, ID_STATUS_IN_WORK,true);                                      
                      //send to rmdashboard action start
                      if(useDeferHost)
                      sendAction(String.valueOf(selectedIssue),"Запустил");
              } catch(Exception ex){}
            Platform.runLater(new Runnable() {
            @Override
            public void run() {
           
                     timeStartInWork= System.currentTimeMillis();
                        //change image in tray
                      BufferedImage trayImage = SwingFXUtils.fromFXImage(startImage24,null);             
                      trayIcon.setImage(trayImage);
                      try{
                      //save value of selectd issue so that change it color in the table
                      if(table.getSelectionModel().getSelectedItem()!=null){
                        markedIssue=table.getSelectionModel().getSelectedItem().getIdCol();
                      }
                      else
                      {
                          markedIssue=String.valueOf(selectedIssue);                          
                      }
                     
                      setTableIssues();      
                      updateTableIssues();
                      //start timer
                      } catch(Exception ex){}
                         
            } 
            });                
        }
    }
          startButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);                     
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
                      workTimer = new Timer();
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
                      } catch(Exception ex){
                        LOG.addHandler(handler);
                        LOG.log(Level.SEVERE, null, ex);
                      }
                       startButton.setGraphic(new ImageView(pauseImage));
                      startButton.setTooltip(new Tooltip("Остановить таймер")); 
                       onStartButton sb = new onStartButton();	//Создание потока    
                       sb.start(); 
                  } else { //if issue already started in this program
               
                      //stop timer
                      workTimer.cancel();
                      
                      //lbTime.setText("");
                      //save time spent on this issue
                      saveSpentHours();
                      timeStartInWork=0;
                      //cahnge image view to start image
                      Button button = (Button) e.getSource();
                      button.setGraphic(new ImageView(startImage));
                      button.setTooltip(new Tooltip("Запусить задачу"));
                      if(useDeferHost)
                       sendAction(String.valueOf(markedIssue),"Пауза");

                      //update timer
                      workTimer = new Timer();
                      //clear counter
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
    
        deferButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);                     
               }
          });
        deferButton.setOnAction((final ActionEvent e) -> {  
                 
    
            
     // deferTimeFrame.setVisible(true);
        disableButtons(true);
        if(useDeferHost){
        if((mgr!=null)&&(table.getSelectionModel().getSelectedItem()!=null)){
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");//dd/MM/yyyy
            Date now = new Date();
            String strDate = sdfDate.format(now);                   
            deferDatePicker.setValue(LocalDate.now());
          
             sdfDate.applyPattern("HH:mm");  
             Calendar c = Calendar.getInstance(); 
             c.setTime(now);           
             c.add(Calendar.HOUR_OF_DAY, 1);                      
             now = c.getTime();        
             strDate = sdfDate.format(now);                   
            tfDeferTime.setText(strDate);
            
            
            deferStage.show();
            int idDeferIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
        }
        }else{
            try{
                if(table.getSelectionModel().getSelectedItem()!=null){
                    int idDeferIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
                    changeIssueStatusFromNew(idDeferIssue,ID_STATUS_DEFER,true);
                    setTableIssues();
                    if(!markedIssue.trim().isEmpty()&&timeStartInWork!=0)
                    updateTableIssues(); 
                     multilineRowTheme();
                }
            }catch(NumberFormatException ex){
                LOG.addHandler(handler);
                LOG.log(Level.SEVERE, null, ex);
            }   
        }
         disableButtons(false);
        });
      
     
        deferTimeButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);   
                     deferStage.getScene().setCursor(Cursor.WAIT);  
               }
          });
        deferTimeButton.setOnAction((final ActionEvent e) -> {
           
            String dateDT=deferDatePicker.getValue().toString();
            
            String timeDT=tfDeferTime.getText();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateReturn=dateDT+" "+timeDT;
            try {
                Date dt = formatter.parse(dateReturn);
                Calendar c = Calendar.getInstance(); 
                c.setTime(dt);
                c.add(Calendar.HOUR_OF_DAY, 3);
                dt = c.getTime();        
                long unixTime = (long) dt.getTime()/1000;
                System.out.println(unixTime );
                deferStage.hide();
               
                int idDeferIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
                Issue updatedIssue = mgr.getIssueManager().getIssueById(idDeferIssue);               
                updatedIssue.setPriorityId(connDefer.getPriorityDefer());
              // mgr.getIssueManager().getIssuePriorities().get(0);
                mgr.getIssueManager().update(updatedIssue);
                setDeferTime(Long.toString(unixTime));       
               
         
            } catch (ParseException ex) {
                Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RedmineException ex) {
                Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
          
          
        
        });
         deferDayButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);    
                     deferStage.getScene().setCursor(Cursor.WAIT);  
               }
          });
         deferDayButton.setOnAction((final ActionEvent e) -> {
         
            
            Date dt = new Date();
            Calendar c = Calendar.getInstance(); 
            c.setTime(dt); 
            c.add(Calendar.DATE, 1);
            c.set(Calendar.HOUR_OF_DAY, 9);
            c.set(Calendar.MINUTE, 0);
           
            dt = c.getTime();        
            long unixTime = (long) dt.getTime()/1000;
            System.out.println(unixTime );
            deferStage.hide();
            setDeferTime(Long.toString(unixTime));          

        });
         deferHourButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);    
                     deferStage.getScene().setCursor(Cursor.WAIT);  
               }
          });
         deferHourButton.setOnAction((final ActionEvent e) -> {
   
            Date dt = new Date();
            Calendar c = Calendar.getInstance(); 
            c.setTime(dt); 
            c.add(Calendar.HOUR_OF_DAY, 4);
            dt = c.getTime();        
            long unixTime = (long) dt.getTime()/1000;
            System.out.println(unixTime );
            setDeferTime(Long.toString(unixTime));
            deferStage.hide();          
        
        });
          deferAfterButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);                     
                     deferStage.getScene().setCursor(Cursor.WAIT);                     
               }
          });
         deferAfterButton.setOnAction((final ActionEvent e) -> {
   
            Date dt = new Date();
            Calendar c = Calendar.getInstance(); 
            c.setTime(dt); 
            c.add(Calendar.MONTH, 1);
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 9);
            c.set(Calendar.MINUTE, 0);
            dt = c.getTime();        
            long unixTime = (long) dt.getTime()/1000;
            System.out.println(unixTime );
            setDeferTime(Long.toString(unixTime));
            deferStage.hide();            
        
        });
        
       deferStage.setOnHidden(event -> {
           stage.getScene().setCursor(Cursor.DEFAULT);
           deferStage.getScene().setCursor(Cursor.DEFAULT);
       });
        
        browserButton.setOnAction((final ActionEvent e) -> {
            browserStackPane = new StackPane();
            if(defaultBrowser||selectedBrowser.isEmpty())defaultBrowserCheckBox.setSelected(true);
            else defaultBrowserCheckBox.setSelected(false);
            browserStackPane.getChildren().add(defaultBrowserCheckBox);
            defaultBrowserCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) {
                    defaultBrowser = defaultBrowserCheckBox.isSelected();
                    selectBrowserFileButton.setDisable(defaultBrowserCheckBox.isSelected());                    

                    for(int i=0;i<selectBrowserToggleGroup.getToggles().size();i++)
                    {
                        RadioButton rb = (RadioButton)selectBrowserToggleGroup.getToggles().get(i);
                        rb.setDisable(defaultBrowser);
                    }
                    if(defaultBrowser)
                    {
                        selectedBrowser="";
                        try(FileWriter writer = new FileWriter("browser.out", false))
                        {         
                            writer.write("");
                            writer.flush();
                        }
                        catch(IOException ex){
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
         Scene browserScene;
         StackPane.setAlignment(defaultBrowserCheckBox, Pos.TOP_LEFT);
          StackPane.setMargin(defaultBrowserCheckBox, new Insets(15.0,5.0,5.0,5.0)); 
            if(windowsSystem)
            {
                selectBrowserFileButton.setDisable(defaultBrowser);
                browserStackPane.getChildren().add(selectBrowserFileButton);
                
                StackPane.setAlignment(selectBrowserFileButton, Pos.TOP_RIGHT);
                StackPane.setMargin(selectBrowserFileButton, new Insets(10.0,5.0,5.0,5.0)); 
               
                    
                selectBrowserFileButton.setOnAction((final ActionEvent ev) -> {
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("EXE files (*.exe)", "*.exe");
                    fileChooser.getExtensionFilters().add(extFilter);
                    // Show open file dialog
                    File file = fileChooser.showOpenDialog(browserStage);
                    if (file != null) {
                        selectedBrowser = file.getPath();
                        browserStage.hide();
                        try(FileWriter writer = new FileWriter("browser.out", false))
                        {
                           //write browser in file                            
                            writer.write(selectedBrowser);
                            writer.flush();
                        }
                        catch(IOException ex){
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);
                        }
                        //System.out.println(selectedBrowser);
                    }
                    
                });
                browserScene = new Scene(browserStackPane, 200, 50);
            }
            else
            {
                RadioButton rb = new RadioButton("Mozilla Firefox");
                rb.setId("firefox");
                if(rb.getId().equals(selectedBrowser))
                    rb.setSelected(true);                
                rb.setToggleGroup(selectBrowserToggleGroup);
                browserStackPane.getChildren().add(rb);
                StackPane.setAlignment(rb, Pos.TOP_LEFT);
                StackPane.setMargin(rb, new Insets(70.0,5.0,5.0,5.0)); 
                rb = new RadioButton("Chromium");
                rb.setId("chromium-browser");
                if(rb.getId().equals(selectedBrowser))
                    rb.setSelected(true);
                rb.setToggleGroup(selectBrowserToggleGroup);
                browserStackPane.getChildren().add(rb);
                StackPane.setAlignment(rb, Pos.TOP_LEFT);
                StackPane.setMargin(rb, new Insets(120.0,5.0,5.0,5.0)); 
                for(int i=0;i<selectBrowserToggleGroup.getToggles().size();i++)
                    {
                        rb = (RadioButton)selectBrowserToggleGroup.getToggles().get(i);
                        rb.setDisable(defaultBrowser);
                    }
                selectBrowserToggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) -> {
                    if(selectBrowserToggleGroup.getSelectedToggle()!=null)
                        {
                            RadioButton chk = (RadioButton)selectBrowserToggleGroup.getSelectedToggle(); // Cast object to radio button
                            selectedBrowser=chk.getId();                            
                        }
                     try(FileWriter writer = new FileWriter("browser.out", false))
                        {
                           //write browser in file                            
                            writer.write(selectedBrowser);
                            writer.flush();
                        }
                        catch(IOException ex){
                            LOG.addHandler(handler);
                            LOG.log(Level.SEVERE, null, ex);
                        }
                });
            
                browserScene = new Scene(browserStackPane, 200, 200);
            }
            browserStage.setScene(browserScene);                    
            browserStage.show();
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
                statusesList.clear();
                trackersList.clear();
                    
                Scene trackersScene = new Scene(trackersStackPane, 400, 600);
                trackersStage.setScene(trackersScene);                    
                trackersStage.show();
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
                writeToTrackersOut();
                
                    checkboxTrackers.clear();
                    checkboxStatuses.clear();
            setTableIssues();
            if(!markedIssue.trim().isEmpty()&&timeStartInWork!=0)
                updateTableIssues();
            trackersStage.hide();
        });
        createButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);                     
               }
          });
        createButton.setOnAction((final ActionEvent e) -> {
            if(mgr!=null&&cc!=null){
             if(idProject.trim().isEmpty())
             {
                showProjectStage(); 
             }
             else
             {
            tiDialog.getEditor().setText("Задача");
            tiDialog.getEditor().requestFocus();
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
                    
                     name= name.replaceAll("\"","\\\\\"");
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
//                        \"custom_fields\":[{ \"value\":[5,4,3], \"id\":168}  ] , 
                        StringEntity paramsEntity =new StringEntity("{\"issue\": {    \"project_id\": "+idProject+",\"subject\": \""+name+"\", \"assigned_to_id\": "+String.valueOf(idCurrentUser)+((!idSubproject.trim().isEmpty())?", \"parent_issue_id\":"+idSubproject:"")+"}}","UTF-8");                    
                        request.addHeader("Content-type", "application/json; charset=utf-8");
                        request.setEntity(paramsEntity);
             
                        HttpResponse response = client.execute(request);
                        
                        if(response.getStatusLine().getStatusCode()!=201)
                        {
                            alert.setTitle("Внимание");
                            alert.setHeaderText(null);
                            alert.setContentText("Не удалось создать задачу!");
                            alert.showAndWait();
                        }
                        else
                        {
                           
                            setTableIssues();
                                if(!markedIssue.trim().isEmpty()&&timeStartInWork!=0)
                                    updateTableIssues();
                                Params params2 = new Params()
                                    .add("set_filter", "1")
                                    .add("assigned_to_id", String.valueOf(idCurrentUser));

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
            tiDialog = new TextInputDialog();
            }
        }
        });
         stopButton.setOnMousePressed((final MouseEvent e) -> {
               if (e.getButton().equals(MouseButton.PRIMARY)) {  
                     stage.getScene().setCursor(Cursor.WAIT);                     
               }
          });
        stopButton.setOnAction((final ActionEvent e) -> {
            disableButtons(true);
            if ((mgr!=null)&&(selectedIssue!=0)&&(timeStartInWork!=0)) {
                 
                tiDialog.getEditor().setText("00:01");
                tiDialog.getEditor().requestFocus();
                long millis=System.currentTimeMillis()-timeStartInWork;
                long hours=TimeUnit.MILLISECONDS.toHours(millis);
                long minutes=TimeUnit.MILLISECONDS.toMinutes(millis)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
                long seconds=TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
                                
                String s = hours+":"+((minutes<10)?"0"+String.valueOf(minutes):minutes)+":"+((seconds<10)?"0"+String.valueOf(seconds):seconds);
        
                if(s.length()<=8)
                    tiDialog.setTitle("Таймер был запущен в течении "+s);
                else
                    tiDialog.setTitle("Таймер был запущен и не остановлен");

                tiDialog.setHeaderText(null);               
                
                try {
                    tiDialog.setContentText("Введите действительные трудозатраты по задаче: \n"+mgr.getIssueManager().getIssueById(selectedIssue).getSubject());
                } catch (RedmineException ex) {
                    Logger.getLogger(RM_1.class.getName()).log(Level.SEVERE, null, ex);
                }
                tiDialog.setWidth(600);
                Optional<String> result = tiDialog.showAndWait();
                result.ifPresent(name -> {
                    if(!name.trim().isEmpty())
                    {
                        if(useDeferHost)
                        sendAction(String.valueOf(markedIssue),"Пауза");
                        //change image in tray
                        BufferedImage trayImage = SwingFXUtils.fromFXImage(stopImage24,null);             
                        trayIcon.setImage(trayImage);
                        //останавливаем задачу и записываем трудозатраты введенные пользователем
                       //stop timer
                      workTimer.cancel();
                      timeStartInWork=0;
                      markedIssue="";
                      
                      float timeEntryBuf=Float.parseFloat(convertMinutesToSpentHours(name));
                      if(timeEntryBuf>0){
                      TimeEntry TE=new TimeEntry(); 
                      TE.setHours(timeEntryBuf);
                      TE.setIssueId(selectedIssue);
                      TE.setActivityId(ID_ACTIVITY_ENGINEERING); 
                                try {
                                     mgr.getTimeEntryManager().createTimeEntry(TE);
                                    
                                     String spentHoursStringBuf=mgr.getIssueManager().getIssueById(selectedIssue).getSpentHours().toString();
                                    spentHoursMap.replace(selectedIssue,convertSpentHours(spentHoursStringBuf));
                                    setTableIssues();
                                  
                                    
                                    } catch (RedmineException ex) {
                                        alert.setTitle("Внимание");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Трудозатраты не удалось сохранить! Задача: " +selectedIssue+" Трудозатраты: "+TE.getHours());
                                        alert.showAndWait();
                                        LOG.addHandler(handler);
                                        LOG.log(Level.SEVERE, null, ex);
                                    }
                      }
                      updateTableIssues();
                        //update timer
                      workTimer = new Timer();
                      //clear counter
                      lbTime.setText("");
                      startButton.setGraphic(new ImageView(startImage));
                      startButton.setTooltip(new Tooltip("Запустить задачу"));
                       
                    
                      //select issue which selected now
                      try{
                          if(table.getSelectionModel().getSelectedItem()!=null)
                          selectedIssue=Integer.parseInt(table.getSelectionModel().getSelectedItem().getIdCol());
                      }catch(NumberFormatException ex){
                          selectedIssue=0;
                      }
                    }
                });
                tiDialog = new TextInputDialog();
            }
            disableButtons(false);
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
                        FileInputStream fis = new FileInputStream(dirHome+"size.out");
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
                rowListnew.add(new RowIssue("","","","","","",""));
                rowIssueList.add(rowListnew);
                statusIdStringList.add("");
                trackerIdStringList.add("");
                nameTabsStringList.add("Вкладка "+(tabs.getTabs().size()+1));
                writeToTrackersOut();
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
                            tiDialog.getEditor().setText("Вкладка");
                            tiDialog.getEditor().requestFocus();
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
                            tiDialog = new TextInputDialog();
                        }
                    }
                });
                tabs.getTabs().add(tab);
                tabs.getSelectionModel().select(tab);
      
            });
        //timer for sending status online
//        try{
//              onlineTimer = new Timer();
//              onlineTimer.schedule(new TimerTask() {
//                          @Override
//                          public void run() {
//                              sendOnline();
//                                            System.out.println("Онлайн!");
//                          }
//                      }, 5_000, 900_000);
//                      } catch(Exception ex){
//                        LOG.addHandler(handler);
//                        LOG.log(Level.SEVERE, null, ex);
//         }
    //поток для обработки перехода на новую вкладку
    class tabClick extends Thread
    {   
        Number oldValue;
        Number newValue;
        public tabClick( Number oldValue, Number newValue){
            this.newValue=newValue;
            this.oldValue=oldValue;
        }
	@Override
	public void run()
	{
            Platform.runLater(new Runnable() {
            @Override
            public void run() {
        // Update UI here.
          setTableIssues();
            if((oldValue!=newValue)&&(timeStartInWork==0))
            {
                selectedIssue=0;
                table.getSelectionModel().select(null);
            }
            
            updateTableIssues();
            stage.getScene().setCursor(Cursor.DEFAULT);
             }
            });
          
	}
    }
        tabs.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue, Number newValue) -> {         
	     stage.getScene().setCursor(Cursor.WAIT);	
             tabClick te = new tabClick(oldValue,newValue);	//Создание потока
             te.start();         
        }); 
        rootAnchorPane.setMaxWidth(table.getMaxWidth()+10);
        rootAnchorPane.setMaxHeight(50);
        StackPane.setMargin(rootAnchorPane, new Insets(0.0,0.0,0.0,0.0)); 
        StackPane.setAlignment(rootAnchorPane, Pos.TOP_LEFT);     

        root.getChildren().add(rootAnchorPane);
        scene= new Scene(root, sceneWidth, sceneHeight);
        scene.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {            
          double newWidth = newSceneWidth.intValue()-60-(!windowsSystem?10:0);
          table.setMaxWidth(newWidth);         
          table.setMinWidth(newWidth);         
          themeCol.setMaxWidth(newWidth-75);
          themeCol.setMinWidth(newWidth-75);
          tabs.setMaxWidth(newWidth);
          tabs.setMinWidth(newWidth);
          
          multilineRowTheme();
        });
                    
        scene.setFill(Color.TRANSPARENT);
        stage.setX(sceneX);
        stage.setY(sceneY);
        stage.setTitle(PROGRAM_NAME);
        stage.setScene(scene);
        double newWidth = sceneWidth-60-(!windowsSystem?10:0);
        table.setMaxWidth(newWidth);         
        table.setMinWidth(newWidth);         
        themeCol.setMaxWidth(newWidth-75);
        themeCol.setMinWidth(newWidth-75);
        tabs.setMaxWidth(newWidth);
        tabs.setMinWidth(newWidth);
        multilineRowTheme();
        
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
