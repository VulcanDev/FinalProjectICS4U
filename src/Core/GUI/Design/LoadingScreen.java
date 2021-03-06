package Core.GUI.Design;

import AetheriusEngine.core.gui.*;
import Core.GUI.gfxRepository;
import Core.gameSettings;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * KM
 * June 16 2017
 * Handles the design and display of the loading screen UI.
 * The XLoader class is used in conjunction to determine the behavior during loading.
 */

public class LoadingScreen extends XLabel {

    private XLoader loader;
    private JProgressBar loadingBar;

    public LoadingScreen() {
        gfxRepository.randomBackground();
        this.setBackground(gfxRepository.clrTrueBlack);
        this.setAlignments(SwingConstants.CENTER);
        this.setPreferredSize(new Dimension(gameSettings.ui.getUIScaleX(), gameSettings.ui.getUIScaleY()));
        this.setSize(this.getPreferredSize());
        this.setVisible(true);
        loadElements();
    }

    private void loadElements() { //loads the elements into the loading screen

        this.scaleImage(gfxRepository.mainBackground);

        //loads the loading icon gif that plays during the loading screen
        XLabel loadIcon = new XLabel(gfxRepository.loadingIcon);
        this.add(loadIcon);
        loadIcon.setBounds((this.getWidth() / 2) - 240, this.getHeight() - 490, 480, 320);
        loadIcon.setVisible(true);

        //loads the text box above the loading bar
        XLabel lblInformation = new XLabel("Astra Project - Work In Progress", gfxRepository.txtLargeText, gfxRepository.clrText);
        this.add(lblInformation);
        lblInformation.setBounds((this.getWidth() / 2) - 500, this.getHeight() - 175, 1000, 50);
        lblInformation.setAlignments(SwingConstants.CENTER, SwingConstants.BOTTOM);
        lblInformation.setVisible(true);

        //loading bar
        loadingBar = new JProgressBar(0, 100);
        this.add(loadingBar);
        loadingBar.setBounds(75, this.getHeight() - 120, this.getWidth() - 150, 50);
        loadingBar.setFocusable(false);
        loadingBar.setOpaque(true);
        loadingBar.setValue(0);
        loadingBar.setFont(gfxRepository.txtSubheader);
        loadingBar.setForeground(gfxRepository.clrEnable);
        loadingBar.setBackground(gfxRepository.clrDGrey);
        loadingBar.setStringPainted(true);
        loadingBar.setBorderPainted(false);
        loadingBar.setVisible(true);

        this.revalidate();
        this.repaint();

    }

    public void load(XLoader loader) {
        this.loader = loader;

        loader.addPropertyChangeListener(new PropertyChangeListener() { //adds the listener
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if ("progress".equals(e.getPropertyName())) {
                    int progress = (Integer)e.getNewValue();
                    loadingBar.setIndeterminate(false);
                    loadingBar.setValue(progress);
                }
            }
        });

        this.loader.execute();
    }


}
