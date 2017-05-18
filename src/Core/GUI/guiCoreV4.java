package Core.GUI;

//import all relevant stuff
import Core.SFX.audioCore;
import Core.gameLoader;
import Core.xmlLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 KM
 May 16 2017
 Round FOUR of attempting to create a GUI core to manage and display UI elements.
 Just kill me now.
 */


public class guiCoreV4 {

    private File resourcesFolder = new File(("user.dir") + "/src/Core/GUI/Resources"); //stores the resources folder

    /** Stores resource declarations **/

    private ArrayList<JPanel> pnlExpansions = new ArrayList<>();
    private ArrayList<JLabel> lblExpansions = new ArrayList<>();
    private ArrayList<JLabel> lblExpanDesc = new ArrayList<>();
    private ArrayList<JButton> btnExpanEnable = new ArrayList<>();
    private ArrayList<JLabel> lblExpanID = new ArrayList<>();
    private ArrayList<addExpAL> actionEnabler = new ArrayList<>();

    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //gets the screen size of the user
    private double screenWidth = screenSize.getWidth();
    private double screenHeight = screenSize.getHeight();
    private JFrame window;
    private JLayeredPane layers; //sorts the layers of the screen
    private newPanel screen;
    private JPanel contentController;
    private JScrollPane contentList;
    private JPanel pnlExpansionHeader;
    private JLabel lblExpHeaderText;
    private JProgressBar loadingBar;
    private int screenScaleChoice;

    private int musicVolume = 70;
    private int UIVolume = 70;

    private backgroundLoader loader;

    private audioCore mainMusic;

    /** Stores UI element design properties **/

    final String gameVersion = "PTB-A Build 63a";

    private final Color clrBlk = new Color(25, 35, 35, 255);
    private final Color clrDGrey = new Color(55, 55, 55, 255);
    private final Color clrDisableBorder = new Color(75, 5, 25, 255);
    private final Color clrDisable = new Color(135, 15, 55, 255);
    private final Color clrEnable = new Color(0, 155, 105, 255);
    private  final Color clrDark = new Color(0, 135, 110, 255);
    private final Color clrButtonBackground = new Color(0, 125, 90, 220);
    private final Color clrButtonMain = new Color(0, 145, 110, 255);
    private final Color clrBackground = new Color(0, 185, 140, 105);
    private final Color clrForeground = new Color(0, 185, 110, 155);
    private final Color clrText = new Color(255, 255, 255, 255);

    private final Font txtStandard = new Font("Comic Sans", Font.PLAIN, 15);
    private final Font txtSubtitle = new Font("Arial", Font.BOLD, 14);
    private final Font txtItalSubtitle = new Font("Arial", Font.ITALIC, 14);
    private final Font txtSubheader = new Font("Arial", Font.BOLD, 16);
    private final Font txtHeader = new Font("Arial", Font.BOLD, 25);
    private final Font txtTitle = new Font("Arial", Font.BOLD, 40);
    private final Font txtTiny = new Font("Arial", Font.PLAIN, 12);

    /** UI scaling code**/
    //handles the scaling of the UI

    private int[] currentUIScale = {0, 0}; //default screen scale

    //methods to return the UI elements
    public int getUIScaleX() { return this.currentUIScale[0]; }

    public int getUIScaleY() { return this.currentUIScale[1]; }

    //sets the window size to the chosen monitor scale
    public void rescaleScreen(int option) {

        int oldX, oldY; //stores the previous values in case something goes wrong

        oldX = this.getUIScaleX();
        oldY = this.getUIScaleY();

        switch(option) {
            //Widescreen monitors
            case 1: //4K
                this.currentUIScale[0] = 3840; //X scale
                this.currentUIScale[1] = 2160; //Y scale
                break;
            case 2: //2K
                this.currentUIScale[0] = 2560;
                this.currentUIScale[1] = 1440;
                break;
            case 3: //standard monitor
                this.currentUIScale[0] = 1920;
                this.currentUIScale[1] = 1080;
                break;
            case 4:
                this.currentUIScale[0] = 1600;
                this.currentUIScale[1] = 900;
                break;
            case 5:
                this.currentUIScale[0] = 1366;
                this.currentUIScale[1] = 768;
                break;
            case 6:
                this.currentUIScale[0] = 1280;
                this.currentUIScale[1] = 720;
                break;
            //4:3 and similar monitor scales
            case 7:
                this.currentUIScale[0] = 1600;
                this.currentUIScale[1] = 1200;
                break;
            case 8:
                this.currentUIScale[0] = 1280;
                this.currentUIScale[1] = 1024;
                break;
            case 9:
                this.currentUIScale[0] = 1024;
                this.currentUIScale[1] = 768;
                break;
            case 10:
                this.currentUIScale[0] = 800;
                this.currentUIScale[1] = 600;
                break;
            case 11: //Launcher UI scale
                this.currentUIScale[0] = 700;
                this.currentUIScale[1] = 450;
                break;
            default: //safety net
                this.currentUIScale[0] = 500;
                this.currentUIScale[1] = 500;
                break;
        }

        if (this.getUIScaleX() > screenWidth || this.getUIScaleY() > screenHeight) {
            System.out.println("Monitor is not large enough to support this resolution.");
            this.currentUIScale[0] = oldX; //resets the screen scale to the previous values
            this.currentUIScale[1] = oldY;
        } else {
            //no incompatibilities, proceed
            System.out.println("UI successfully rescaled to " + currentUIScale[0] + "x" + currentUIScale[1] + ".");
        }

    }

    /** Main Constructor **/

    //builds the core framework
    public guiCoreV4(int screenScaleOption) {

        window = new JFrame("Astra Launcher");
        screen = new newPanel();

        rescaleScreen(screenScaleOption);
        try {
            window.setIconImage(ImageIO.read(this.getClass().getResource("Resources/icon.png")));
        } catch (IOException e) {
            System.out.println("Error loading game icon: " + e);
        }
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        screen.setLayout(null); //CARDINAL SIN, BUT I DON'T REALLY CARE AT THIS POINT
        screen.setVisible(true);
        window.setContentPane(screen);
        window.setUndecorated(true);
        window.setResizable(false);
        window.pack();
        window.setBounds((int)(screenWidth / 2) - (this.getUIScaleX() / 2), (int)(screenHeight / 2) - (this.getUIScaleY() / 2), this.getUIScaleX(), this.getUIScaleY());
        screen.setBounds(0, 0, getUIScaleX(), getUIScaleY());
        window.setVisible(true);


    }

    /** Screen Builders **/

    private void buttonAudio() {
        audioCore buttonPress = new audioCore("menu_press.wav", UIVolume, 0, 1000);
        buttonPress.start();
    }

    public void loadLauncherScreen() {
        System.out.println("Loading launcher data...");
        BufferedImage launcherBG;
        JLabel imgBackground;

        //initialize the layers
        layers = new JLayeredPane();
        layers.setBounds(0, 0, getUIScaleX(), getUIScaleY());
        //add the layered window to the content pane
        window.getContentPane().add(layers);

        //attempt to load images
        try {
            launcherBG = ImageIO.read(this.getClass().getResource("Resources/launcherBG.jpg"));
            imgBackground = new JLabel(new ImageIcon(launcherBG)); //TODO: Add a way to rescale images.
            layers.add(imgBackground, new Integer(0), 0);
            imgBackground.setBounds(0, 0, getUIScaleX(), getUIScaleY());
            imgBackground.setVisible(true);
        } catch (IOException e) {
            System.out.println("Error when loading images: " + e.getMessage());
        }

        //load exit button
        JButton btnExit = new JButton();

        layers.add(btnExit, new Integer(1), 0);
        btnExit.setBounds(getUIScaleX() - 35, 5, 30, 30);
        btnExit.setBackground(clrButtonBackground);
        btnExit.setFocusPainted(false);
        btnExit.setForeground(clrText);
        //btnExit.setBorderPainted(false);
        btnExit.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED, clrForeground, clrButtonBackground), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        btnExit.setOpaque(false);
        btnExit.setFont(txtStandard);
        btnExit.setText("X");
        btnExit.addActionListener(new ActionListener() { //closes the program when clicked
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Killing program.");
                buttonAudio();
                window.dispose(); //ensure the thread dies
                System.exit(0); //close the program
            }
        });
        btnExit.setVisible(true);

        //load minimize button
        JButton btnMinimize = new JButton();

        layers.add(btnMinimize, new Integer(2), 0);
        btnMinimize.setBounds(getUIScaleX() - 70, 5, 30, 30);
        btnMinimize.setBackground(clrButtonBackground);
        btnMinimize.setFocusPainted(false);
        btnMinimize.setForeground(clrText);
        //btnMinimize.setBorderPainted(false);
        btnMinimize.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED, clrForeground, clrButtonBackground), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        btnMinimize.setOpaque(false);
        btnMinimize.setFont(txtStandard);
        btnMinimize.setText("-");
        btnMinimize.addActionListener(new ActionListener() { //closes the program when clicked
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Minimizing program.");
                window.setState(Frame.ICONIFIED);
                buttonAudio();
            }
        });
        btnMinimize.setVisible(true);

        //load settings button
        JButton btnSettings = new JButton();

        layers.add(btnSettings, new Integer(3), 0);
        btnSettings.setBounds(getUIScaleX() - 105, 5, 30, 30);
        btnSettings.setBackground(clrButtonBackground);
        btnSettings.setFocusPainted(false);
        btnSettings.setForeground(clrText);
        btnSettings.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED, clrForeground, clrButtonBackground), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        btnSettings.setOpaque(false);
        btnSettings.setFont(txtStandard);
        btnSettings.setText("*");

        btnSettings.setVisible(true);

        //load the game title
        JLabel lblTitle = new JLabel();

        layers.add(lblTitle, new Integer(3), 0);
        lblTitle.setBounds(20, 20, 300, 75);
        lblTitle.setVerticalAlignment(SwingConstants.TOP);
        lblTitle.setOpaque(false);
        lblTitle.setFocusable(false);
        lblTitle.setFont(txtTitle);
        lblTitle.setText("Astra Project");
        lblTitle.setForeground(clrText);
        lblTitle.setVisible(true);

        //load the game version
        JLabel lblVersion = new JLabel();

        layers.add(lblVersion, new Integer(4), 0);
        lblVersion.setBounds(getUIScaleX() - 205, getUIScaleY() - 40, 200, 35);
        lblVersion.setOpaque(false);
        lblVersion.setFocusable(false);
        lblVersion.setFont(txtSubtitle);
        lblVersion.setText("Version; " + gameVersion);
        lblVersion.setForeground(clrText);
        lblVersion.setHorizontalAlignment(SwingConstants.RIGHT); //sets the text to center to the right side instead of the left
        lblVersion.setVisible(true);

        //load the scroll window
        contentController = new JPanel();
        layers.add(contentController, new Integer(5), 0);
        contentList = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        layers.add(contentList, new Integer(6), 0);

        contentController.setBounds(getUIScaleX() - 305, 40, 300, 300);
        contentController.setOpaque(true);
        contentController.setLayout(null);
        contentController.setBackground(clrBackground);
        contentController.setVisible(true);

        contentList.setBounds(getUIScaleX() - (contentController.getWidth() + 5), 40, contentController.getWidth(), 300);
        contentList.setViewportView(contentController);
        contentList.setOpaque(false);
        contentList.setVisible(true);
        contentList.setBorder(null);

        //load the expansion header
        pnlExpansionHeader = new JPanel();
        pnlExpansionHeader.setLayout(null);
        pnlExpansionHeader.setBounds(5, 5, contentController.getWidth() - 25, 25);
        pnlExpansionHeader.setOpaque(true);
        pnlExpansionHeader.setBackground(clrDark);
        pnlExpansionHeader.setVisible(true);

        //load expansion header text
        lblExpHeaderText = new JLabel();
        lblExpHeaderText.setBounds(5, 5, pnlExpansionHeader.getWidth() - 10, pnlExpansionHeader.getHeight() - 10);
        lblExpHeaderText.setOpaque(false);
        lblExpHeaderText.setFont(txtSubheader);
        lblExpHeaderText.setForeground(clrText);
        lblExpHeaderText.setText("Expansion Packs");
        lblExpHeaderText.setHorizontalAlignment(SwingConstants.CENTER);
        lblExpHeaderText.setVerticalAlignment(SwingConstants.CENTER);
        lblExpHeaderText.setVisible(true);

        //load launch button
        JButton btnLaunch = new JButton();
        layers.add(btnLaunch, new Integer(7), 0);
        btnLaunch.setBounds(contentController.getX(), contentController.getY() + contentController.getWidth() + 5, contentController.getWidth(), 60);
        btnLaunch.setBackground(clrButtonMain);
        btnLaunch.setForeground(clrText);
        btnLaunch.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED, clrForeground, clrButtonBackground), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        btnLaunch.setFont(txtHeader);
        btnLaunch.setOpaque(true); //TODO: Find a way to do semi-transparent buttons
        btnLaunch.setFocusPainted(false);
        btnLaunch.setHorizontalAlignment(SwingConstants.CENTER);
        btnLaunch.setVerticalAlignment(SwingConstants.CENTER);
        btnLaunch.setText("PLAY");
        btnLaunch.addActionListener(new ActionListener() { //closes the program when clicked
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Launching game...");
                screen.removeAll();
                mainMusic.interrupt();
                buttonAudio();
                loadLoadingScreen();

            }
        });

        mainMusic = new audioCore("new_dawn.mp3", musicVolume);
        mainMusic.start();

        loadLauncherExpansions();

        //make sure the content is visible and render it
        window.revalidate();
        window.repaint();

    }

    public void loadLauncherExpansions() { //Loads the expansion content for the launcher

        String expansionID;
        int expansionEnabled;

        //empties and refactors content
        if (pnlExpansions.size() > 0) {
            contentController.removeAll();
            pnlExpansions.clear();
            lblExpansions.clear();
            lblExpanDesc.clear();
            btnExpanEnable.clear();
            lblExpanID.clear();
        }

        //add the expansion pack header to the content window
        contentController.add(pnlExpansionHeader);
        pnlExpansionHeader.add(lblExpHeaderText);

        for (int i = 0; i < xmlLoader.listOfExpansions.size(); i ++) {

            pnlExpansions.add(new JPanel());
            lblExpansions.add(new JLabel());
            lblExpanDesc.add(new JLabel());
            btnExpanEnable.add(new JButton());
            lblExpanID.add(new JLabel());

            expansionID = xmlLoader.listOfExpansions.get(i).getID();

            contentController.add(pnlExpansions.get(i)); //moves all of the content to the content controller

            pnlExpansions.get(i).setLayout(null);

            if (5 + (75 * i) + (pnlExpansionHeader.getHeight() + 5) > contentController.getHeight()) { //increases the window size if needed
                System.out.println("Resizing content window.");
                contentController.setBounds(contentController.getX(), contentController.getY(), contentController.getWidth(), contentController.getHeight() + 75);
            } else {
                contentController.setBounds(getUIScaleX() - 305, 110, 300, 300);
            }

            pnlExpansions.get(i).setBounds(5, 5 + (75 * i) + (pnlExpansionHeader.getHeight() + 5), contentController.getWidth() - 25, 70);
            pnlExpansions.get(i).setBackground(clrForeground);
            pnlExpansions.get(i).add(lblExpansions.get(i));
            pnlExpansions.get(i).add(lblExpanDesc.get(i));
            pnlExpansions.get(i).add(btnExpanEnable.get(i));
            pnlExpansions.get(i).add(lblExpanID.get(i));
            pnlExpansions.get(i).setOpaque(true);

            lblExpansions.get(i).setBounds(5, 5, 195, 35);
            lblExpansions.get(i).setForeground(clrText);
            lblExpansions.get(i).setOpaque(false);
            lblExpansions.get(i).setFont(txtSubheader);
            lblExpansions.get(i).setText(xmlLoader.listOfExpansions.get(i).getName());
            lblExpansions.get(i).setVerticalAlignment(SwingConstants.TOP);

            lblExpanDesc.get(i).setBounds(5, 40, 170, 25);
            lblExpanDesc.get(i).setOpaque(false);
            lblExpanDesc.get(i).setForeground(clrText);
            lblExpanDesc.get(i).setFont(txtItalSubtitle);
            lblExpanDesc.get(i).setText(xmlLoader.listOfExpansions.get(i).getSubtitle());

            btnExpanEnable.get(i).setBounds(contentController.getWidth() - 55, 5, 25, 25);
            btnExpanEnable.get(i).setForeground(clrText);
            btnExpanEnable.get(i).setOpaque(true);
            btnExpanEnable.get(i).setFocusable(false);
            btnExpanEnable.get(i).setFont(txtSubheader);

            actionEnabler.add(new addExpAL());

            btnExpanEnable.get(i).addActionListener(actionEnabler.get(i));
            actionEnabler.get(i).setExpID(expansionID);

            //adjust the enable/disable button based on the current status of the content
            if (xmlLoader.listOfExpansions.get(i).getEnabledStatus()) {
                //content is enabled, set the button accordingly
                System.out.println("Expansion " + xmlLoader.listOfExpansions.get(i).getID() + " is enabled.");
                btnExpanEnable.get(i).setText("-");
                btnExpanEnable.get(i).setToolTipText("Disable");
                expansionEnabled = 1;
                btnExpanEnable.get(i).setBackground(clrEnable);
                btnExpanEnable.get(i).setBorder(BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED, clrForeground, clrBackground), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
            } else {
                //content is disabled, set the button accordingly
                System.out.println("Expansion " + xmlLoader.listOfExpansions.get(i).getID() + " is disabled.");
                btnExpanEnable.get(i).setText("+");
                btnExpanEnable.get(i).setToolTipText("Enable");
                expansionEnabled = 0;
                btnExpanEnable.get(i).setBackground(clrDisable);
                btnExpanEnable.get(i).setBorder(BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED, clrDisableBorder, clrBlk), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
            }

            actionEnabler.get(i).setEnable(expansionEnabled); //update the action listener tied to the enable button with the current status of the content

            lblExpanID.get(i).setBounds(contentController.getWidth() - 85, 35, 55, 25);
            lblExpanID.get(i).setHorizontalAlignment(SwingConstants.RIGHT);
            lblExpanID.get(i).setForeground(clrText);
            lblExpanID.get(i).setFont(txtTiny);
            lblExpanID.get(i).setText(xmlLoader.listOfExpansions.get(i).getID());


            //enable everything lel
            lblExpanID.get(i).setVisible(true);
            btnExpanEnable.get(i).setVisible(true);
            lblExpanDesc.get(i).setVisible(true);
            lblExpansions.get(i).setVisible(true);
            pnlExpansions.get(i).setVisible(true);

        }

        System.out.println("Loaded expansion data into GUI.");

        window.revalidate();
        window.repaint();
    }

    //load the loading screen and game content
    public void loadLoadingScreen() {

        screenScaleChoice = 8;

        //TODO: Eventually re-sort swing objects so I don't have a bunch of a empty ones lying around. Arraylists are the best bet.

        Icon loadingIcon;
        BufferedImage backgroundImage;
        JLabel bgPanel;
        JLabel bgLoadIcon;

        mainMusic = new audioCore("imperial_fleet.mp3", musicVolume);
        mainMusic.start();

        layers.removeAll(); //clean the layer slate
        window.getContentPane().add(layers);

        rescaleScreen(screenScaleChoice);
        window.setBounds((int)(screenWidth / 2) - (this.getUIScaleX() / 2), (int)(screenHeight / 2) - (this.getUIScaleY() / 2), this.getUIScaleX(), this.getUIScaleY());
        screen.setBounds(0, 0, getUIScaleX(), getUIScaleY());
        layers.setBounds(0, 0, getUIScaleX(), getUIScaleY()); //reset the size

        try {
            backgroundImage = ImageIO.read(this.getClass().getResource("Resources/loadingBG.jpg"));
            bgPanel = new JLabel(new ImageIcon(backgroundImage));
            layers.add(bgPanel, new Integer(0), 0);
            bgPanel.setBounds(0, 0, getUIScaleX(), getUIScaleY());
            bgPanel.setOpaque(true);
            bgPanel.setFocusable(false);
            bgPanel.setVisible(true);

            loadingIcon = new ImageIcon(this.getClass().getResource("Resources/ok_hand.gif"));
            bgLoadIcon = new JLabel(loadingIcon);
            layers.add(bgLoadIcon, new Integer(1), 0);
            bgLoadIcon.setBounds((getUIScaleX() / 2) - 150, getUIScaleY() - 380, 300, 300);
            bgLoadIcon.setOpaque(false);
            bgLoadIcon.setFocusable(false);
            bgLoadIcon.setVisible(true);

            //TODO: Add a way to rescale images.

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        loadingBar = new JProgressBar(0, 100);
        layers.add(loadingBar, new Integer(2), 0);
        loadingBar.setBounds(40, getUIScaleY() - 60, getUIScaleX() - 80, 30);
        loadingBar.setFocusable(false);
        loadingBar.setOpaque(true);
        loadingBar.setValue(0);
        loadingBar.setFont(txtSubheader);
        loadingBar.setForeground(clrButtonMain);
        loadingBar.setBackground(clrDGrey);
        loadingBar.setStringPainted(true);
        loadingBar.setBorderPainted(false);
        loadingBar.setVisible(true);

        JLabel lblInformation = new JLabel();
        layers.add(lblInformation, new Integer(3), 0);
        lblInformation.setBounds((getUIScaleX() / 2) - 300, getUIScaleY() - 120, 600, 50);
        lblInformation.setOpaque(false);
        lblInformation.setFocusable(false);
        lblInformation.setFont(txtHeader);
        lblInformation.setHorizontalAlignment(SwingConstants.CENTER);
        lblInformation.setVerticalAlignment(SwingConstants.BOTTOM);
        lblInformation.setForeground(clrText);
        lblInformation.setText("Astra Project - Work In Progress");
        lblInformation.setVisible(true);

        window.revalidate();
        window.repaint();

        loadingBar.setIndeterminate(true);

        //open a new thread to load content in the background
        loader = new backgroundLoader();
        loader.addPropertyChangeListener(new propertyListener());
        loader.execute();
    }

    //opens a new thread to load content in the background without interrupting the UI
    private class backgroundLoader extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            Random random = new Random();
            int progress;

            for (int i = 1; i <= 100; i++) {
                progress = i;
                setProgress(Math.min(progress, 100));
                Thread.sleep(100 + random.nextInt(600)); //unnecessary, but will help to reduce load

                switch (i) { //using a switch so i can set individual functions to each % up to 100%
                    case 21:
                        gameLoader.loadXMLData();
                        break;
                    case 84:
                        gameLoader.cleanContent();
                        break;
                }

            }

            setProgress(100);

            return null;
        }

        protected void done() {

            loadingBar.setString("Complete");
        }
    }

    private class propertyListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent e) {
            if ("progress".equals(e.getPropertyName())) {
                int progress = (Integer)e.getNewValue();
                loadingBar.setIndeterminate(false);
                loadingBar.setValue(progress);
            }

        }
    }

    //action listened for enabling/disabling expansions
    private class addExpAL implements ActionListener {

        public addExpAL() {
        }

        int enable;
        String expID;

        @Override
        public void actionPerformed(ActionEvent e) {

            buttonAudio();

            if (enable == 1) {
                enable = 0;
                System.out.println("Disabling content for " + expID);
            } else {
                enable = 1;
                System.out.println("Enabling content for " + expID);
            }

            xmlLoader.changeExpansionInfo(expID, enable);

            System.out.println("Refreshing UI...");

            loadLauncherExpansions();

        }

        public void setEnable(int enabled) {
            this.enable = enabled;
        }

        public void setExpID(String ID) {
            this.expID = ID;
        }

    }




}