package com.chesapeaketechnology.networkviz;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

// My addons
import java.io.File;
import gov.nasa.worldwindx.examples.GeoJSONLoader;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.Timer;
import javax.swing.JLabel;
import java.awt.event.ActionListener;

//   ./gradlew run

/**
 * Creates and manages visuals within a WorldWind based application. Default controls for toggling the visibility of each
 * {@link Layer} and skeleton methods are included to help get started.
 * <p>
 * Additional documentation can be found at WorldWind's open source github - https://nasaworldwind.github.io/WorldWindJava/.
 */
public class NetworkVisualizer extends ApplicationTemplate
{
    private AppFrame appFrame;
    private int queryInterval;
    private JLabel queryIntervalLabel;
    private String url;
    private Timer timer;
    private RenderableLayer fileGeoJSONLayer;
    private RenderableLayer liveGeoJSONLayer;
    private JFileChooser filePanel;
    private JPanel intervalPanel;
    private JTextField inputField;
    private JComboBox<String> unitComboBox;
    private GeoJSONLoader loader;

    /**
     * Configures and opens the WorldWind application that can be used to view GeoJSON data. After this method completes,
     * the {@link #appFrame} will be initialized.
     */
    public void launchApplication()
    {
        queryInterval = 300; // 5 Minute default value for live stream query
        appFrame = start("World Wind JSON Network Viewer", AppFrame.class);
        loader = new GeoJSONLoader();
        configurePanels();
        
        // Add query timer label to the status bar
        queryIntervalLabel = new JLabel("Query Interval: " + queryInterval + " seconds");
        appFrame.getStatusBar().add(queryIntervalLabel);
    }

    /**
     * Adds panels that will be used to get user input
     */
    private void configurePanels()
    {
        // Reconfigure the size of the World Window to take up the space typically used by the layer panel
        Dimension dimension = new Dimension(1400, 800);
        appFrame.setPreferredSize(dimension);
        appFrame.pack();
        WWUtil.alignComponent(null, appFrame, AVKey.CENTER);
        addMenusToFrame();

        // Configure JFileChooser to choose json files
        filePanel = new JFileChooser();
        filePanel.addChoosableFileFilter(new FileNameExtensionFilter("JSON File", "json", "json"));
        File projectRootDirectory = new File(System.getProperty("user.dir")); 
        filePanel.setCurrentDirectory(projectRootDirectory); // Set the initial directory to the project root
        filePanel.setFileSelectionMode(JFileChooser.FILES_ONLY); // Can only select files
        filePanel.setMultiSelectionEnabled(false); // Can't select multiple files
        filePanel.setDialogTitle("Select a JSON file"); // Clearer dialog title so user can know what to pick
        
        // Configure query panel to choose query interval
        intervalPanel = new JPanel();
        inputField = new JTextField(10);
        unitComboBox = new JComboBox<>(new String[]{"seconds", "minutes"});
        intervalPanel.add(inputField);
        intervalPanel.add(unitComboBox);
    }

    /**
     * Adds menu options that visualize GeoJSON data to the WorldWind application's menubar.
     */
    private void addMenusToFrame()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openFileMenuItem = makeOpenFileMenu();
        JMenuItem openLinkMenuItem = makeOpenLinkMenu();
        JMenuItem openQueryMenuItem = makeQueryMenu();

        appFrame.setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        fileMenu.add(openFileMenuItem);
        fileMenu.add(openLinkMenuItem);
        fileMenu.add(openQueryMenuItem);
    }

    /**
     * Creates a menu option to allow users to view GeoJSON data on the WorldWind canvas.
     *
     * @return A menu option that can be added to an application's menu bar.
     */
    private JMenuItem makeOpenFileMenu() {
        JMenuItem openFileMenuItem = new JMenuItem(new AbstractAction("Open File...") {
            public void actionPerformed(ActionEvent actionEvent) {
                int status; 
                File jsonFile;
                while(true)
                {
                    status = filePanel.showOpenDialog(appFrame);

                    // If file chooser successfully gets a file
                    if (status == JFileChooser.APPROVE_OPTION) {
                        
                        jsonFile = filePanel.getSelectedFile(); // Get selected file
                    
                        // Check if the selected file has a ".json" extension
                        if (jsonFile.getName().endsWith(".json")) {
                            parseAddJson(jsonFile);
                            break;
                        } 
                        else 
                        {
                            // Show a warning if the selected file is not a JSON file
                            JOptionPane.showMessageDialog(appFrame,
                                    "The selected file is not a JSON file.",
                                    "Invalid File", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else // User cancelled, exit loop
                        break;
                }
            }
        });
        return openFileMenuItem;
    }

    

    /**
     * Creates a menu option to allow users to enter a link to a live GeoJSON feed
     *
     * @return A menu option that can be added to an application's menu bar.
     */
    private JMenuItem makeOpenLinkMenu()
    {
        JMenuItem openLinkMenuItem = new JMenuItem(new AbstractAction("Open Link...")
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                String userInput = null;

                // Repeatedly prompt user until they exit or give a proper response
                while(true) 
                {
                    userInput = JOptionPane.showInputDialog(null, "Enter live JSON Feed URL:");
                    
                    // User canceled the input dialog
                    if (userInput == null)
                        // Exit the loop and do nothing further
                        return;
                    // User entered nothing, use default url
                    else if (userInput.trim().isEmpty()) {
                        url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson"; // Default URL
                        parseAddJson(url);
                        break;
                    }
                    // Valid JSON feed URL provided
                    else if (userInput.endsWith(".json")) 
                    {
                        url = userInput; // Update URL to user's input
                        parseAddJson(url);
                        break;
                    }
                    else 
                    {
                        // Inform the user of the invalid URL
                        JOptionPane.showMessageDialog(null,
                                "Invalid JSON Feed URL. The URL must end with '.json'.",
                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        return openLinkMenuItem;
    }

    /**
     * Creates a menu option to allow users to enter a query timer value
     *
     * @return A menu option that can be added to an application's menu bar.
     */
    private JMenuItem makeQueryMenu() {
        JMenuItem openQueryMenu = new JMenuItem(new AbstractAction("Adjust Query Timer...") {
            public void actionPerformed(ActionEvent actionEvent) {
                while(true)
                {
                    // Show the input dialog and capture user input
                    int result = JOptionPane.showConfirmDialog(null, intervalPanel, "Enter Query Time Interval",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    // If the user clicked the OK button
                    if (result == JOptionPane.OK_OPTION) {
                        String userInput = inputField.getText().trim(); // Get the user input value
                        String selectedUnit = (String) unitComboBox.getSelectedItem(); // Get the selected time unit from the combo box
                        
                        // Validate and process the user input
                        if (!userInput.isEmpty()) {
                            int timeValue = Integer.parseInt(userInput); // Parse user (string) input into an integer

                            // Determine the time interval in seconds based on the selected time unit
                            if (selectedUnit.equals("minutes"))
                                queryInterval = timeValue * 60;
                            else
                                queryInterval = timeValue; // Additional variable for clarity

                            // Validate if the input is within the valid range (1 second to 5 minutes)
                            if (queryInterval >= 1 && queryInterval <= 300) 
                            {
                                startTimer(); // Start the timer
                                break; // Exit the loop once valid input is provided
                            } 
                            else 
                                JOptionPane.showMessageDialog(null, 
                                "Time value must be between 1 second and 5 minutes.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                            JOptionPane.showMessageDialog(null, 
                            "Input field cannot be empty. Please enter a valid time value.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                        break; // Exit loop if user cancels dialogue
                }
            }
        });
        return openQueryMenu;
    }

    /*
     * Starts the Java Swing timer or stops/starts time again if timer already exists
     */
    private void startTimer() 
    {
        // Stop the existing timer if one is already running
        if (timer != null)
            timer.stop(); 

        // Create new timer, refresh layer with updated link, and redraw
        timer = new Timer(queryInterval * 1000, new ActionListener() {
            // Defines what the timer does everytime it goes off
            @Override
            public void actionPerformed(ActionEvent ev) {
                parseAddJson(url); // Parse live JSON again
            }
        });
        timer.start(); // Start the new timer with the updated interval
        queryIntervalLabel.setText("Query Interval: " + queryInterval + " seconds"); // Update query interval label
    }

    /**
     * Overloaded helper Function to parse JSON from file, add geometry to new layer, and refresh globe
     */ 
    private void parseAddJson(File jsonFile)
    {
        // Remove existing fileGeoJSONLayer if it exists, if not ignore
        if (fileGeoJSONLayer != null)
            appFrame.getWwd().getModel().getLayers().remove(fileGeoJSONLayer);

        // Add layer to add onto WorldWindow
        fileGeoJSONLayer = new RenderableLayer();
        fileGeoJSONLayer.setName("GeoJSON File Layer");
        
        try {
            // Try to parse JSON and add geometry to the layer
            loader.addSourceGeometryToLayer(jsonFile, fileGeoJSONLayer);
            appFrame.getWwd().getModel().getLayers().add(fileGeoJSONLayer);
            appFrame.getWwd().redraw();
        } 
        catch (Exception e) 
        {
            // Handle exceptions related to JSON parsing or layer update
            JOptionPane.showMessageDialog(appFrame,
                    "Error processing the JSON file: " + e.getMessage(),
                    "Processing Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Overloaded helper Function to parse JSON from a live link, add geometry to new layer, and refresh globe
     */ 
    private void parseAddJson(String url)
    {
        // Remove existing liveGeoJSONLayer if it exists
        if (liveGeoJSONLayer != null)
            appFrame.getWwd().getModel().getLayers().remove(liveGeoJSONLayer);
        
        // Add layer to add onto WorldWindow
        liveGeoJSONLayer = new RenderableLayer();
        liveGeoJSONLayer.setName("Live GeoJSON Layer");

        try
        {
            // Try to parse live JSON and add geometry to the layer
            loader.addSourceGeometryToLayer(url, liveGeoJSONLayer);
            appFrame.getWwd().getModel().getLayers().add(liveGeoJSONLayer); // Add layer to the WorldWindow
            appFrame.getWwd().redraw(); // Redraw to see changes
            startTimer(); // Start query refresh timer with current interval
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,
                    "Error loading GeoJSON data: " + e.getMessage(),
                     "Data Loading Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args)
    {
        NetworkVisualizer networkVisualizer = new NetworkVisualizer();
        networkVisualizer.launchApplication();
    }
}
