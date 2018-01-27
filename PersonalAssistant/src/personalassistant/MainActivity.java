/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalassistant;

import com.teknikindustries.yahooweather.WeatherDisplay;
import com.teknikindustries.yahooweather.WeatherDoc;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Shreyas
 */
public class MainActivity extends javax.swing.JFrame {

    
    int xMouse,yMouse;
    String defaultPlaceHolder = "< Enter something >";
    public static final String pythonFile="test.py";
    public static final String chatBotPy="brain.py";
    public static final String getReplyPy="getReply.py";
    
    public static final int defaultMode=0;
    public static final int emailMode=1;
    public static final int weatherMode=2;
    
    int currMode=defaultMode;
    
    public static final String fileDir = "C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\src\\ChatBot";
    public static final String path = "C:\\Python27";
    
    public static final String weatherAPIKey = "V4cASGCClX7p8Xy51P5olxn3WnaAGfwC";//"UlLAKVh111vc2WanhX1IbRNKOzjB49uK";
    public static final String cityCodeAPI = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=";//UlLAKVh111vc2WanhX1IbRNKOzjB49uK&q=";
    public static final String weatherAPI = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/";
    public static final String weatherIconURL = "https://developer.accuweather.com/sites/default/files/";
    
    
    public static final String wikipediaURL = "https://en.wikipedia.org/w/api.php?action=opensearch&search=";//+"narendra+modi"+"&format=json";
    
    
    public static String jsonOutput = "{\"Headline\":{\"EffectiveDate\":\"2018-01-26T19:00:00+05:30\",\"EffectiveEpochDate\":1516973400,\"Severity\":7,\"Text\":\"Cool Friday night\",\"Category\":\"cold\",\"EndDate\":\"2018-01-27T07:00:00+05:30\",\"EndEpochDate\":1517016600,\"MobileLink\":\"http://m.accuweather.com/en/in/dehradun/191339/extended-weather-forecast/191339?lang=en-us\",\"Link\":\"http://www.accuweather.com/en/in/dehradun/191339/daily-weather-forecast/191339?lang=en-us\"},\"DailyForecasts\":[{\"Date\":\"2018-01-26T07:00:00+05:30\",\"EpochDate\":1516930200,\"Temperature\":{\"Minimum\":{\"Value\":40.0,\"Unit\":\"F\",\"UnitType\":18},\"Maximum\":{\"Value\":65.0,\"Unit\":\"F\",\"UnitType\":18}},\"Day\":{\"Icon\":3,\"IconPhrase\":\"Partly sunny\"},\"Night\":{\"Icon\":37,\"IconPhrase\":\"Hazy moonlight\"},\"Sources\":[\"AccuWeather\"],\"MobileLink\":\"http://m.accuweather.com/en/in/dehradun/191339/daily-weather-forecast/191339?day=1&lang=en-us\",\"Link\":\"http://www.accuweather.com/en/in/dehradun/191339/daily-weather-forecast/191339?day=1&lang=en-us\"}]}";
    
    public static final int blankPanelIndex = 0;
    public static final int weatherPanelIndex = 1;
    public static final int wikiPanelIndex = 2;
    public static final int emailPanelIndex = 3;
    public static final int reminderPanelIndex = 4;
    public static final int numberOfPanels = 5;
    
    JPanel panels[];
    
    ProcessBuilder pb;
    Process pro;
    BufferedReader br;
    
    
    boolean addRecipients=false;
    
    
    /**
     * Creates new form MainActivity
     */
    public MainActivity() {
        initComponents();
        initPython();
        weatherPanel.setBackground( new Color(150, 150, 150, 100) );
        emailPanel.setBackground( new Color(150, 150, 150, 100) );
        reminderPanel.setBackground( new Color(150, 150, 150, 100) );
        txtWikiOutput.setBackground( new Color(150, 150, 150, 255) );
        
        panels=new JPanel[numberOfPanels];
        panels[blankPanelIndex] = blankPanel;
        panels[weatherPanelIndex] = weatherPanel;
        panels[wikiPanelIndex] = wikiPanel;
        panels[emailPanelIndex] = emailPanel;
        panels[reminderPanelIndex] = reminderPanel;
        setParentPanelTo(blankPanelIndex);
        
        getTime();
    }

    public void getTime() {
        Date d = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MMM-yyyy");
        String date = s.format(d);
        s = new SimpleDateFormat("hh:mm:ss a");
        String time = s.format(d);
        System.out.println(date+" "+time);
    }
    
    public void getWeatherAPI(String location) {
        String url = cityCodeAPI+weatherAPIKey+"&q="+location;
        System.out.println(url);
        String cityCode=getCityCode(url);
        url = weatherAPI+cityCode+"?apikey="+weatherAPIKey;
        System.out.println(url);
        
        getWeatherData(url);
        setParentPanelTo(weatherPanelIndex);
        
    }
    
    public void getWikiAPI(String search) {
        search=search.replaceAll(" ","+");
        String url = wikipediaURL+search+"&format=json";
        System.out.println(url);
        String info = loadWikiData(url);
        setParentPanelTo(wikiPanelIndex);
        txtWikiOutput.setText(info);
    }
    
    public String loadWikiData(String url) {
        try {
            getResponseString(url);
            String resp = getResponseString(url);
            JSONParser parser = new JSONParser();
            String info = (String)(((JSONArray)(((JSONArray)parser.parse(resp)).get(2))).get(0));
            return info;
        }
        catch (Exception e) {
            System.out.println("Error loading ");
            e.printStackTrace();
            return null;
        }
    }
    
    public void setParentPanelTo(int panelIndex) {
        parentPanel.removeAll();
        parentPanel.add(panels[panelIndex]);
        parentPanel.repaint();
        parentPanel.revalidate();
    }
    
    private String getResponseString(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
            in.close();
            String resp=response.toString();
            System.out.println(resp);
            return resp;
        }
        catch (Exception e) {
            System.out.println("Error loading ");
            e.printStackTrace();
            return null;
        }
    }
    
    public String getCityCode(String url) {
        try {
            String resp = getResponseString(url);
            JSONParser parser = new JSONParser();
            JSONObject myResponse = (JSONObject)((JSONArray)parser.parse(resp)).get(0);
            return (String)myResponse.get("Key");
        }
        catch (Exception e) {
            System.out.println("Error loading ");
            e.printStackTrace();
            return null;
        }
    }
    
    public void getWeatherData(String url) {
        try {
            String resp = getResponseString(url); // jsonOutput
            System.out.println(resp);
            JSONParser parser = new JSONParser();
            String data = (String)((JSONObject)(((JSONObject)parser.parse(resp)).get("Headline"))).get("Text");
            showResponse(data);
            
            
            JSONObject dailyForecasts = ((JSONObject)(((JSONArray)(((JSONObject)parser.parse(resp)).get("DailyForecasts"))).get(0)));
            double maxTemp = (double)((JSONObject)((JSONObject)dailyForecasts.get("Temperature")).get("Maximum")).get("Value");
            double minTemp = (double)((JSONObject)((JSONObject)dailyForecasts.get("Temperature")).get("Minimum")).get("Value");
            
            String day = (String)((JSONObject)dailyForecasts.get("Day")).get("IconPhrase");
            String night = (String)((JSONObject)dailyForecasts.get("Night")).get("IconPhrase");
            
            long ic = (long)((JSONObject)dailyForecasts.get("Day")).get("Icon");
            String iconDay = "";
            String iconNight = "";
            if (ic<10)
                iconDay = "0"+ic;
            else
                iconDay = ic+"";
            ic = (long)((JSONObject)dailyForecasts.get("Night")).get("Icon");
            if (ic<10)
                iconNight = "0"+ic;
            else
                iconNight = ic+"";
            
            setWeatherIcon(dayIcon,iconDay);
            setWeatherIcon(nightIcon,iconNight);
            
            maxTemp = 5.0/9*(maxTemp-32);
            minTemp = 5.0/9*(minTemp-32);
            
            txtWeatherInfo.setText(data);
            txtMaxTemp.setText(String.format("%.1f",maxTemp)+" °C");
            txtMinTemp.setText(String.format("%.1f",minTemp)+" °C");
            txtDayPhrase.setText(day);
            txtNightPhrase.setText(night);
            
            System.out.println(data);
            System.out.println(maxTemp+" "+minTemp);
            System.out.println(day+"\n"+night);
            System.out.println(iconDay+"\n"+iconNight);
        }
        catch (Exception e) {
            System.out.println("Error loading ");
            e.printStackTrace();
            //return null;
        }
    }
    
    public void setWeatherIcon(JLabel label,String index) {
        try {
            BufferedImage brImg = ImageIO.read(new URL(weatherIconURL+index+"-s.png"));
            int w=150,h=90;
            BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(brImg, 0, 0, w, h, null);
            g2.dispose();
            label.setIcon(new ImageIcon(resizedImg)); 
        }
        catch (Exception e) {
            
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parentPanel = new javax.swing.JPanel();
        blankPanel = new javax.swing.JPanel();
        weatherPanel = new javax.swing.JPanel();
        dayIcon = new javax.swing.JLabel();
        nightIcon = new javax.swing.JLabel();
        txtWeatherInfo = new javax.swing.JLabel();
        txtMinTemp = new javax.swing.JLabel();
        txtMaxTemp = new javax.swing.JLabel();
        txtDayPhrase = new javax.swing.JLabel();
        txtNightPhrase = new javax.swing.JLabel();
        wikiPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtWikiOutput = new javax.swing.JTextArea();
        emailPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtEmailTo = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtEmailText = new javax.swing.JTextArea();
        btnSendEmail = new javax.swing.JButton();
        btnEmailClose = new javax.swing.JLabel();
        reminderPanel = new javax.swing.JPanel();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        datePicker = new com.toedter.calendar.JDateChooser();
        btnRemSet = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnRemClose = new javax.swing.JLabel();
        hourPicker = new javax.swing.JSpinner();
        minPicker = new javax.swing.JSpinner();
        txtRemMsg = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txtOutput = new javax.swing.JTextField();
        txtInput = new javax.swing.JTextField();
        btnClose = new javax.swing.JLabel();
        titleBar = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        parentPanel.setOpaque(false);
        parentPanel.setLayout(new java.awt.CardLayout());

        blankPanel.setOpaque(false);

        javax.swing.GroupLayout blankPanelLayout = new javax.swing.GroupLayout(blankPanel);
        blankPanel.setLayout(blankPanelLayout);
        blankPanelLayout.setHorizontalGroup(
            blankPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 730, Short.MAX_VALUE)
        );
        blankPanelLayout.setVerticalGroup(
            blankPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );

        parentPanel.add(blankPanel, "card3");

        weatherPanel.setBackground(new java.awt.Color(51, 51, 51));

        dayIcon.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        dayIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mic-icon.png"))); // NOI18N

        nightIcon.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        nightIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mic-icon.png"))); // NOI18N

        txtWeatherInfo.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
        txtWeatherInfo.setForeground(new java.awt.Color(255, 255, 255));
        txtWeatherInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtWeatherInfo.setText("Clear Sky");

        txtMinTemp.setFont(new java.awt.Font("Agency FB", 0, 70)); // NOI18N
        txtMinTemp.setForeground(new java.awt.Color(255, 255, 255));
        txtMinTemp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtMinTemp.setText("10 °C");

        txtMaxTemp.setFont(new java.awt.Font("Agency FB", 0, 70)); // NOI18N
        txtMaxTemp.setForeground(new java.awt.Color(255, 255, 255));
        txtMaxTemp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtMaxTemp.setText("25 °C");

        txtDayPhrase.setFont(new java.awt.Font("Tahoma", 0, 25)); // NOI18N
        txtDayPhrase.setForeground(new java.awt.Color(255, 255, 255));
        txtDayPhrase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDayPhrase.setText("Sunny day");

        txtNightPhrase.setFont(new java.awt.Font("Tahoma", 0, 25)); // NOI18N
        txtNightPhrase.setForeground(new java.awt.Color(255, 255, 255));
        txtNightPhrase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNightPhrase.setText("Cool night");

        javax.swing.GroupLayout weatherPanelLayout = new javax.swing.GroupLayout(weatherPanel);
        weatherPanel.setLayout(weatherPanelLayout);
        weatherPanelLayout.setHorizontalGroup(
            weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weatherPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dayIcon)
                    .addComponent(nightIcon))
                .addGap(75, 75, 75)
                .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtWeatherInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(weatherPanelLayout.createSequentialGroup()
                        .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtDayPhrase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtMaxTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMinTemp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNightPhrase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(610, 610, 610))
        );
        weatherPanelLayout.setVerticalGroup(
            weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weatherPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, weatherPanelLayout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDayPhrase)
                            .addComponent(txtNightPhrase))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addComponent(txtWeatherInfo)
                        .addGap(31, 31, 31))
                    .addGroup(weatherPanelLayout.createSequentialGroup()
                        .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(weatherPanelLayout.createSequentialGroup()
                                .addComponent(dayIcon)
                                .addGap(102, 102, 102)
                                .addComponent(nightIcon))
                            .addGroup(weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtMaxTemp)
                                .addComponent(txtMinTemp)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        parentPanel.add(weatherPanel, "card2");

        wikiPanel.setOpaque(false);

        jScrollPane1.setOpaque(false);

        txtWikiOutput.setColumns(20);
        txtWikiOutput.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        txtWikiOutput.setForeground(new java.awt.Color(51, 51, 255));
        txtWikiOutput.setLineWrap(true);
        txtWikiOutput.setRows(5);
        txtWikiOutput.setOpaque(false);
        jScrollPane1.setViewportView(txtWikiOutput);

        javax.swing.GroupLayout wikiPanelLayout = new javax.swing.GroupLayout(wikiPanel);
        wikiPanel.setLayout(wikiPanelLayout);
        wikiPanelLayout.setHorizontalGroup(
            wikiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wikiPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
                .addContainerGap())
        );
        wikiPanelLayout.setVerticalGroup(
            wikiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wikiPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addContainerGap())
        );

        parentPanel.add(wikiPanel, "card4");

        emailPanel.setBackground(new java.awt.Color(51, 51, 51));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/gmail icon.png"))); // NOI18N

        txtEmailTo.setBackground(new java.awt.Color(230, 230, 230));
        txtEmailTo.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        txtEmailText.setBackground(new java.awt.Color(230, 230, 230));
        txtEmailText.setColumns(20);
        txtEmailText.setFont(new java.awt.Font("Monospaced", 0, 20)); // NOI18N
        txtEmailText.setRows(5);
        jScrollPane2.setViewportView(txtEmailText);

        btnSendEmail.setText("Send");
        btnSendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendEmailActionPerformed(evt);
            }
        });

        btnEmailClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        btnEmailClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEmailCloseMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout emailPanelLayout = new javax.swing.GroupLayout(emailPanel);
        emailPanel.setLayout(emailPanelLayout);
        emailPanelLayout.setHorizontalGroup(
            emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(emailPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtEmailTo, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSendEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEmailClose)
                        .addGap(13, 13, 13)))
                .addContainerGap())
        );
        emailPanelLayout.setVerticalGroup(
            emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emailPanelLayout.createSequentialGroup()
                .addGroup(emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(emailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtEmailTo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSendEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEmailClose))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addContainerGap())
        );

        parentPanel.add(emailPanel, "card5");

        reminderPanel.setBackground(new java.awt.Color(51, 51, 51));

        datePicker.setDateFormatString("d MMM , yyyy");
        datePicker.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        btnRemSet.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnRemSet.setText("Set");
        btnRemSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemSetActionPerformed(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/reminder.png"))); // NOI18N

        btnRemClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        btnRemClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRemCloseMouseClicked(evt);
            }
        });

        hourPicker.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        minPicker.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        txtRemMsg.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        txtRemMsg.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRemMsg.setText("Reminder");

        javax.swing.GroupLayout reminderPanelLayout = new javax.swing.GroupLayout(reminderPanel);
        reminderPanel.setLayout(reminderPanelLayout);
        reminderPanelLayout.setHorizontalGroup(
            reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reminderPanelLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(reminderPanelLayout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(btnRemSet, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, reminderPanelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(reminderPanelLayout.createSequentialGroup()
                                .addComponent(hourPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(minPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(datePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtRemMsg))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reminderPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRemClose)
                .addContainerGap())
        );
        reminderPanelLayout.setVerticalGroup(
            reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reminderPanelLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reminderPanelLayout.createSequentialGroup()
                        .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(reminderPanelLayout.createSequentialGroup()
                        .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(reminderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hourPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(minPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(txtRemMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(btnRemSet, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reminderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRemClose)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(27, 27, 27))
        );

        parentPanel.add(reminderPanel, "card6");

        getContentPane().add(parentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 730, 310));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mic-icon.png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 540, -1, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mic-icon.png"))); // NOI18N
        jButton1.setOpaque(false);
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 50));

        txtOutput.setEditable(false);
        txtOutput.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        txtOutput.setForeground(new java.awt.Color(255, 255, 255));
        txtOutput.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtOutput.setText("< Thinking >");
        txtOutput.setCaretColor(new java.awt.Color(255, 255, 255));
        txtOutput.setOpaque(false);
        txtOutput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOutputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtOutputFocusLost(evt);
            }
        });
        txtOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtOutputKeyTyped(evt);
            }
        });
        getContentPane().add(txtOutput, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, 640, 40));

        txtInput.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        txtInput.setForeground(new java.awt.Color(255, 255, 255));
        txtInput.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInput.setText("< Enter something >");
        txtInput.setCaretColor(new java.awt.Color(255, 255, 255));
        txtInput.setOpaque(false);
        txtInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtInputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtInputFocusLost(evt);
            }
        });
        txtInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtInputKeyTyped(evt);
            }
        });
        getContentPane().add(txtInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 540, 540, 40));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close-icon.png"))); // NOI18N
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCloseMouseClicked(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, -1, -1));

        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                titleBarMouseDragged(evt);
            }
        });
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                titleBarMousePressed(evt);
            }
        });
        getContentPane().add(titleBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 600));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/liveWallpaper.gif"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        setSize(new java.awt.Dimension(800, 600));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void titleBarMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titleBarMouseDragged
        this.setLocation(evt.getXOnScreen()-xMouse,evt.getYOnScreen()-yMouse);
    }//GEN-LAST:event_titleBarMouseDragged

    private void titleBarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titleBarMousePressed
        xMouse=evt.getX();
        yMouse=evt.getY();
    }//GEN-LAST:event_titleBarMousePressed

    private void btnCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCloseMouseClicked
        closeApp();
    }//GEN-LAST:event_btnCloseMouseClicked

    private void txtInputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtInputFocusGained
        String txt=txtInput.getText();
        if (txt.equals(defaultPlaceHolder)) {
            txtInput.setText("");
        }
    }//GEN-LAST:event_txtInputFocusGained

    private void txtInputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtInputFocusLost
        String txt=txtInput.getText();
        if (txt.equals("")) {
            txtInput.setText(defaultPlaceHolder);
        }
    }//GEN-LAST:event_txtInputFocusLost

    private void txtInputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInputKeyTyped
        if (evt.getKeyChar()=='\n') {
            String txt=txtInput.getText();
            txtInput.setText("");
            executeCommand(txt);
            //exec(txt);
            //execURL(txt);
            //searchQuery();
            //getWeather();
        }
    }//GEN-LAST:event_txtInputKeyTyped

    private void txtOutputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOutputFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputFocusGained

    private void txtOutputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOutputFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputFocusLost

    private void txtOutputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputKeyTyped

    private void btnSendEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendEmailActionPerformed
        String sendTo = txtEmailTo.getText();
        if (sendTo.equals("")) {
            addRecipients=true;
            showResponse("Add Recipients");
        }
        else {
            String to[]=sendTo.split(" ");
            if (Email.sendMail(txtEmailText.getText(),to)) {
                showResponse("Your email has been sent");
                currMode=defaultMode;
                txtEmailText.setText("");
                setParentPanelTo(blankPanelIndex);
            }
            else {
                showResponse("Sorry email was not sent");
            }
        }
    }//GEN-LAST:event_btnSendEmailActionPerformed

    private void btnEmailCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEmailCloseMouseClicked
        // TODO add your handling code here:
        currMode=defaultMode;
        txtEmailText.setText("");
        txtEmailTo.setText("");
        setParentPanelTo(blankPanelIndex);
    }//GEN-LAST:event_btnEmailCloseMouseClicked

    private void btnRemCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRemCloseMouseClicked
        setParentPanelTo(blankPanelIndex);
    }//GEN-LAST:event_btnRemCloseMouseClicked

    private void btnRemSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemSetActionPerformed
        Date d = datePicker.getDate();
        SimpleDateFormat s = new SimpleDateFormat("dd-MMM-yyyy");
        String date = s.format(d);
        System.out.println(date);
        
        String hrs = hourPicker.getValue().toString();
        String min = minPicker.getValue().toString();
        
        System.out.println(hrs+" "+min);
        
        if (hrs.length()<2)
            hrs="0"+hrs;
        hrs=hrs+":";
        if (min.length()<2)
            min="0"+min;
        
        String targetTime = hrs+min;
        
        s = new SimpleDateFormat("hh:mm:ss");
        String time = s.format(d);
        time=time.substring(0,time.lastIndexOf(":"));
        
        System.out.println(targetTime);
        System.out.println(time);
        
        
        Popup window = new Popup();
        window.targetDate=date;
        window.targetTime=targetTime;
        window.message=txtRemMsg.getText();
        window.startTimer();
    }//GEN-LAST:event_btnRemSetActionPerformed

    private void closeApp() {
        runPython("shutdown");
        System.exit(0);
    }
    
    private void executeCommand(String command) {
        System.out.println("=>"+command);
        //String tmpCom=command;
        
        
        if (currMode==defaultMode) {
            command=command.toLowerCase();
            if (command.contains("open")) {
                try {
                    String app = command.substring(command.indexOf("open")+5);
                    ProcessBuilder pb;
                    pb = new ProcessBuilder (app);
                    Process p = pb.start();
                }
                catch (Exception e) {
                    System.out.println("err");
                }
            }
            else if (command.contains("email")) {
                currMode=emailMode;
                setParentPanelTo(emailPanelIndex);
            }
            else if (command.contains("weather")) {
                //currMode=weatherMode;
                getWeatherAPI("allahabad");
            }
            else if (command.contains("search")) {
                if (command.contains("search for")) {
                    command=command.substring(command.indexOf("search for")+"search for".length());
                }
                else {
                    command=command.substring(command.indexOf("search")+"search".length());
                }
                execURL(command);
            }
            else if(command.contains("reminder")||command.contains("alarm")) {
                showResponse("I will remind you of that");
                setParentPanelTo(reminderPanelIndex);
                
            }
            else {
                runPython(command);
                //getWikiAPI(command);
            }
        }
        else if (currMode==emailMode) {
            // send an email
            if (command.equals("cancel email")) {
                currMode=defaultMode;
                txtEmailText.setText("");
                txtEmailTo.setText("");
                setParentPanelTo(blankPanelIndex);
            }
            else if (addRecipients) {
                txtEmailTo.setText(txtEmailTo.getText()+command+" ");
            }
            else {
                txtEmailText.append(command+"\n");
            }
            
        }
        
    }
    
    public void showResponse(String data) {
        if (data.equals("")) {
            return;
        }
        txtOutput.setText(data);
    }
    
    public void initPython() {
        try {
            pb = new ProcessBuilder ("cmd","/C","python "+fileDir+"\\"+chatBotPy);
            pb.directory(new File(path));
            pro = pb.start();
            br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            
            Thread output = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String out="";
                        while ((out=br.readLine())!=null) {
                            System.out.println(chatBotPy+" : "+out);
                        }
                        pro.waitFor();
                        int x = pro.exitValue();
                        if (x == 0) {
                            System.out.println(chatBotPy+" : done successful");
                        }
                        else {
                            System.out.println(chatBotPy+" : done with error");
                            
                            BufferedReader r = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
                            String errMsg;
                            while ((errMsg = r.readLine()) != null)
                            {
                                String msg=errMsg + System.getProperty("line.separator");
                                System.out.println(msg);
                                System.out.println("Compiler : "+out);
                                //textDebug.append(errMsg+"\n");
                            }
                            //long t2=System.currentTimeMillis();
                            //textDebug.append("Compilation Time : "+((t2-t1)/1000.0)+" sec\n");
                            
                            
                        }
                    }
                    catch (Exception e) {
                        
                    }
                    
                }
            });
            output.start();
        }
        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public void runPython(String msg) {
        long t1=System.currentTimeMillis();
        try {
            String inp=msg+"\n";
            byte buffer[] = inp.getBytes();
            OutputStream os =(pro.getOutputStream());
            os.write(buffer,0,buffer.length);
            os.flush();
            
            Thread output = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ProcessBuilder pblocal;
                        pblocal = new ProcessBuilder ("cmd","/C","python "+fileDir+"\\"+getReplyPy);
                        pblocal.directory(new File(path));
                        Process plocal = pblocal.start();
                        BufferedReader brlocal = new BufferedReader(new InputStreamReader(plocal.getInputStream()));
                        String out="";
                        while ((out=brlocal.readLine())!=null) {
                            System.out.println(getReplyPy+" "+out);
                            showResponse(out);
                        }
                        plocal.waitFor();
                    }
                    catch (Exception e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                    
                }
            });
            output.start();
        }
        catch (Exception e) {
            System.out.println("Error in running");
        }
    }
    
    
    
//    
//    public void compilePython(String msg) {
//        long t1=System.currentTimeMillis();
//        String file = pythonFile;
//        try {
//            ProcessBuilder pb;
//            pb = new ProcessBuilder ("cmd","/C","python "+fileDir+"\\"+file);
//            pb.directory(new File(path));
//            System.out.println("started");
//            Process p = pb.start();
//            
//            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String inp=msg+"\n";
//            byte buffer[] = inp.getBytes();
//            OutputStream os =(p.getOutputStream());
//            os.write(buffer,0,buffer.length);
//            os.flush();
//            
//            String out="";
//            System.out.println(br.readLine());
//            System.out.println("ended");
//            while ((out=br.readLine())!=null) {
//                System.out.println(out);
//            }
//            long t2=System.currentTimeMillis();
//            System.out.println("Compilation Time : "+((t2-t1)/1000.0)+" sec\n");
//            
//            p.waitFor();
//            int x = p.exitValue();
//            if (x == 0) {
//                ;//System.out.println("done successful");
//            }
//            else
//            {
//                ;//System.out.println("done with error");
//            }
//        }
//        catch (Exception e) {
//            System.out.println("Error in running");
//        }
//    }
//    
//    public void runPython() {
//        long t1=System.currentTimeMillis();
//        String file = pythonFile;
//        String fileDir = "C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\src\\ChatBot";
//        String path = "C:\\Python27";
//        try {
//            ProcessBuilder pb;
//            pb = new ProcessBuilder ("cmd","/C","python "+fileDir+"\\"+file);
//            pb.directory(new File(path));
//            Process p = pb.start();
//            p.waitFor();
//            int x = p.exitValue();
//            if (x == 0) {
//                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                String out="";
//                while ((out=br.readLine())!=null) {
//                    System.out.println(out);
//                }
//                long t2=System.currentTimeMillis();
//                System.out.println("Compilation Time : "+((t2-t1)/1000.0)+" sec\n");
//            }
//            else
//            {
//                BufferedReader r = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                String out;
//                while ((out = r.readLine()) != null)
//                {
//                    String msg=out + System.getProperty("line.separator");
//                    System.out.println(msg);
//                    System.out.println("Compiler : "+out);
//                    System.out.println(out+"\n");
//                }
//                long t2=System.currentTimeMillis();
//                System.out.println("Compilation Time : "+((t2-t1)/1000.0)+" sec\n");
//            }
//        }
//        catch (Exception e) {
//            System.out.println("Error ");
//        }
//    }
//    
//    public void getWeather() {
//        // find woeid from this link
//        // http://woeid.rosselliot.co.nz/lookup/city
//        
//        WeatherDoc doc = new WeatherDoc("1580913","c");
//        WeatherDisplay disp = new WeatherDisplay();
//        //disp=doc.
//        System.out.println(disp.getTemperature());
//    }
//    
//    public void searchQuery() {
//        try {
//            String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
//            String search = "stackoverflow";
//            String charset = "UTF-8";
//
//            URL url = new URL(google + URLEncoder.encode(search, charset));
//            Reader reader = new InputStreamReader(url.openStream(), charset);
//            System.out.println(reader);
//            BufferedReader br = new BufferedReader(reader);
//            System.out.println(br);
//            String data=br.readLine();
//            System.out.println(data);
////            while ((data=br.readLine())!=null) {
////                System.out.println(data);
////            }
//            //GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
//
//            // Show title and URL of 1st result.
//            //System.out.println(results.getResponseData().getResults().get(0).getTitle());
//            //System.out.println(results.getResponseData().getResults().get(0).getUrl());
//        }
//        catch (Exception e) {
//            System.out.println("error");
//            System.out.println(e);
//            e.printStackTrace();
//        }
//    }
    
    public void exec(String command) {
        Runtime runtime = Runtime.getRuntime(); 
        try {
            runtime.exec(command);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void execURL(String command) {
        command=command.replaceAll(" ","+");
        String query="www.google.com/search?q=";
        query=query+command;
        Runtime runtime = Runtime.getRuntime(); 
        String[] s = new String[] {"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", query};
        try {
            runtime.exec(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainActivity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainActivity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainActivity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainActivity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainActivity().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel blankPanel;
    private javax.swing.JLabel btnClose;
    private javax.swing.JLabel btnEmailClose;
    private javax.swing.JLabel btnRemClose;
    private javax.swing.JButton btnRemSet;
    private javax.swing.JButton btnSendEmail;
    private com.toedter.calendar.JDateChooser datePicker;
    private javax.swing.JLabel dayIcon;
    private javax.swing.JPanel emailPanel;
    private javax.swing.JSpinner hourPicker;
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner minPicker;
    private javax.swing.JLabel nightIcon;
    private javax.swing.JPanel parentPanel;
    private javax.swing.JPanel reminderPanel;
    private javax.swing.JLabel titleBar;
    private javax.swing.JLabel txtDayPhrase;
    private javax.swing.JTextArea txtEmailText;
    private javax.swing.JTextField txtEmailTo;
    private javax.swing.JTextField txtInput;
    private javax.swing.JLabel txtMaxTemp;
    private javax.swing.JLabel txtMinTemp;
    private javax.swing.JLabel txtNightPhrase;
    private javax.swing.JTextField txtOutput;
    private javax.swing.JTextField txtRemMsg;
    private javax.swing.JLabel txtWeatherInfo;
    private javax.swing.JTextArea txtWikiOutput;
    private javax.swing.JPanel weatherPanel;
    private javax.swing.JPanel wikiPanel;
    // End of variables declaration//GEN-END:variables
}

class GoogleResults {

    private ResponseData responseData;
    public ResponseData getResponseData() { return responseData; }
    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
    public String toString() { return "ResponseData[" + responseData + "]"; }

    static class ResponseData {
        private List<Result> results;
        public List<Result> getResults() { return results; }
        public void setResults(List<Result> results) { this.results = results; }
        public String toString() { return "Results[" + results + "]"; }
    }

    static class Result {
        private String url;
        private String title;
        public String getUrl() { return url; }
        public String getTitle() { return title; }
        public void setUrl(String url) { this.url = url; }
        public void setTitle(String title) { this.title = title; }
        public String toString() { return "Result[url:" + url +",title:" + title + "]"; }
    }

}