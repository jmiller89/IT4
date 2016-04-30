/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Jim
 */
public class MainMenuPanel extends JPanel
{
    private Image image;
    private MainMenuFrame mmf;
    private JButton newGameBtn;
    private JButton loadMapBtn;
    private JButton loadLvSetBtn;
    private JButton aboutBtn;
    private JButton controlsBtn;
    private JButton mazeBtn;
    private JButton tutorialBtn;
    private JButton loadGameBtn;
    private JCheckBox enableSound;
    private JCheckBox enableFullscreen;
    private static final int INTRO = 13;
    //private final Color purple = new Color(153, 43, 204);
    private final Color darkRed = new Color(112, 8, 0);
    private final Color argentineBlue = new Color(117, 170, 220);
    private static MainMenuPanel mmp = null;

    //Maintain a pointer to the game object
    public static Game game = null;

    public MainMenuPanel(MainMenuFrame mf)
    {   
        //Delete old game object
        if (game != null)
        {
            game = null;
            System.gc();
        }

        GameFileManager.playTime = 0;
        MainMenuPanel.mmp = this;

        mmf = mf;

        this.setSize(800, 600);
        this.setVisible(true);

        this.setLayout(null);

        BufferedImage source = null;

        try
        {
            URL url = this.getClass().getClassLoader().getResource("Sprites/IT4Title.gif");

            source = ImageIO.read(url);

        }
        catch (IOException e)
        {
            System.out.println("Intruder's Thunder: IO Exception");
        }

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        image = gc.createCompatibleImage(source.getWidth(),source.getHeight(),Transparency.BITMASK);

        image.getGraphics().drawImage(source,0,0,null);

        newGameBtn = new JButton("New Game");
        newGameBtn.setBounds(50, 180, 150, 45);
        newGameBtn.setBackground(argentineBlue);
        newGameBtn.setForeground(darkRed);
        newGameBtn.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        newGameBtn.repaint();

        loadGameBtn = new JButton("Load Game");
        loadGameBtn.setBounds(50, 255, 150, 45);
        loadGameBtn.setBackground(argentineBlue);
        loadGameBtn.setForeground(darkRed);
        loadGameBtn.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        loadGameBtn.repaint();

        loadMapBtn = new JButton("Open Level");
        loadMapBtn.setBounds(50, 485, 150, 45);
        loadMapBtn.setBackground(argentineBlue);
        loadMapBtn.setForeground(darkRed);
        loadMapBtn.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        loadMapBtn.repaint();

        loadLvSetBtn = new JButton("Open Levelset");
        loadLvSetBtn.setBounds(50, 410, 150, 45);
        loadLvSetBtn.setBackground(argentineBlue);
        loadLvSetBtn.setForeground(darkRed);
        loadLvSetBtn.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        loadLvSetBtn.repaint();

        tutorialBtn = new JButton("Mission Select");
        tutorialBtn.setBounds(585, 180, 150, 45);
        tutorialBtn.setBackground(argentineBlue);
        tutorialBtn.setForeground(darkRed);
        tutorialBtn.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        tutorialBtn.repaint();

        mazeBtn = new JButton("Instant Action");
        mazeBtn.setBounds(585, 255, 150, 45);
        mazeBtn.setBackground(argentineBlue);
        mazeBtn.setForeground(darkRed);
        mazeBtn.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        mazeBtn.repaint();

        aboutBtn = new JButton("About");
        aboutBtn.setBounds(585, 485, 150, 45);
        aboutBtn.setBackground(argentineBlue);
        aboutBtn.setForeground(darkRed);
        aboutBtn.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        aboutBtn.repaint();

        controlsBtn = new JButton("Controls");
        controlsBtn.setBounds(585, 410, 150, 45);
        controlsBtn.setBackground(argentineBlue);
        controlsBtn.setForeground(darkRed);
        controlsBtn.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        controlsBtn.repaint();

        JLabel opts = new JLabel("Options");
        opts.setBounds(350, 440, 200, 40);
        opts.setForeground(darkRed);

        enableSound = new JCheckBox("Enable Soundtrack");
        enableSound.setSelected(SFX.musicOn);
        enableSound.setBounds(310, 500, 200, 40);
        enableSound.setBackground(argentineBlue);
        enableSound.setForeground(darkRed);
        enableSound.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        enableSound.repaint();

        enableFullscreen = new JCheckBox("Fullscreen Mode");
        enableFullscreen.setSelected(Game.FULLSCREEN);
        enableFullscreen.setBounds(310, 470, 200, 40);
        enableFullscreen.setBackground(argentineBlue);
        enableFullscreen.setForeground(darkRed);
        enableFullscreen.setBorder(new MatteBorder(5, 5, 5, 5, Color.darkGray));
        enableFullscreen.repaint();

        enableFullscreen.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                if (Game.FULLSCREEN)
                {
                    Game.FULLSCREEN = false;
                }
                else
                {
                    Game.FULLSCREEN = true;
                }
            }


        });

        enableSound.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                SFX.toggleMusic();
                if (SFX.musicOn)
                {
                    SFX.playMusic(INTRO);
                }
                else
                {
                    SFX.stopMusic();
                }
            }

        });

        newGameBtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {                
                newGame("LevelData/IT4Content.it4ls", true, true);
            }

        });

        tutorialBtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {                
                String[] LevelSelection = {"IT3 Tutorial - Southwestern USA", "Flashback - Siberia"};
                String selection = (String)JOptionPane.showInputDialog(MainMenuPanel.mmp, "Select the mission you wish to play", "The Endling's Artifice", JOptionPane.QUESTION_MESSAGE, null, LevelSelection, LevelSelection[0]);
                System.out.println(selection);
                if (selection != null)
                {
                    if (selection.startsWith("Flashback"))
                    {
                        newGame("LevelData/it4lv1.it4", false, true);
                    }
                    else if (selection.contains("Tutorial"))
                    {
                        newGame("LevelData/it4tutorial.it4", false, true);
                    }
                }
            }

        });

        loadGameBtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                loadGame();
            }

        });

        loadMapBtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("Load a level");
                loadLevel();
            }

        });

        loadLvSetBtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("Load a levelset");
                loadLevelSet();
            }

        });

        aboutBtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                showInfo();
            }

        });

        controlsBtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                showControls();
            }

        });

        mazeBtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                playMazeMode();
            }

        });

        
        this.add(newGameBtn);
        this.add(loadGameBtn);
        this.add(loadMapBtn);
        this.add(loadLvSetBtn);
        this.add(tutorialBtn);
        this.add(mazeBtn);
        this.add(aboutBtn);
        this.add(controlsBtn);
        this.add(enableSound);
        this.add(enableFullscreen);
        this.add(opts);

        this.setBackground(Color.BLACK);

        SFX.init();
        SFX.stopMusic();
        SFX.resetLastIndex();
        SFX.playMusic(INTRO);
    }

    private void loadGame()
    {        
        JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "saves"));
        fc.setFileFilter(new ITSVFileFilter());
        int retval = fc.showDialog(this, "Load Game");
        String fp = null;

        if (retval == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();

            if (file.getName().toLowerCase().endsWith(".it4save"))
            {
                fp = file.getPath();
                loadSavedGame(fp);
            }
        }
        
    }

    private void loadSavedGame(String fp)
    {
        boolean load = true;
        if (fp == null)
        {
            load = false;
        }
        else
        {
            System.out.println(fp);
        }

        String levelpath = "LevelData/IT4Content.it4ls";
        boolean lvset = true;
        boolean internal = true;

        GameFileManager.filepath = fp;
        GameFileManager.load();

        levelpath = GameFileManager.levelPath;
        lvset = GameFileManager.isLevelset;
        internal = GameFileManager.internal;

        SFX.stopMusic();

        game = new Game(levelpath, lvset, internal, false, load, mmf);
        mmf.dispose();
    }

    private void playMazeMode()
    {
        SFX.stopMusic();
        game = new Game("", false, true, true, false, mmf);
        mmf.dispose();
    }

    private void newGame(String filepath, boolean lvset, boolean internal)
    {        
        String playername = JOptionPane.showInputDialog(this, "Please enter your name", "New Game", JOptionPane.OK_OPTION);

        if (playername == null)
        {
            playername = "Unknown_Soldier";
        }

        if (playername.length() == 0)
        {
            playername = "Unknown_Soldier";
        }

        playername = playername.replace(' ', '_');
        playername = playername.replace('\n', '_');
        playername += ".it4save";
        GameFileManager.filepath = System.getProperty("user.dir") + System.getProperty("file.separator") + "saves" + System.getProperty("file.separator") + playername;
        System.out.println(GameFileManager.filepath);

        SFX.stopMusic();

        game = new Game(filepath, lvset, internal, false, false, mmf);
        mmf.dispose();
    }

    private void showInfo()
    {
        JOptionPane.showMessageDialog(this, "The Endling's Artifice\nSoftware version 4.6.42 Alpha\nProgrammed By: jmiller89 (C) 2011-2016\n"
                + "Soundtrack By: Bjorn Lynne\n\n"
                + "This program is free software: you can redistribute it and/or modify\n"
                + "it under the terms of the GNU General Public License as published by\n"
                + "the Free Software Foundation, either version 3 of the License, or\n"
                + "(at your option) any later version.\n\n"
                + "This program is distributed in the hope that it will be useful,\n"
                + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                + "GNU General Public License for more details.\n\n"
                + "You should have received a copy of the GNU General Public License\n"
                + "along with this program.  If not, see <http://www.gnu.org/licenses/>.\n"
                + "\n*Although this program was not designed to harm anybody or anybody's computer,"
                + "\nthe author is not liable for any damages this may cause you or your computer.\n"
                + "\n*The story and characters portrayed in this game are completely fictional.", "About The Endling's Artifice", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showControls()
    {
        JOptionPane.showMessageDialog(this, "W                      Move Up\nS                       Move Down\nA                       Move Right\nD                       Move Left"
                + "\n1                       Select Primary Weapon"
                + "\n2                       Select Secondary Weapon"
                + "\n3                       Select Explosives"
                + "\n4                       Select Item"
                + "\nE                       Use Medkit"
                + "\nF                       Perform Action/Confirm Weapon Pickup"
                + "\nQ                      Show Objectives"
                + "\nCtrl                   Melee\nSpace              Attack, Fast-Forward/Exit Dialog\nShift                 Go Prone\nEnter                Pause/Unpause game"
                + "\nEsc                  Return to Main Menu"
                + "\nF5                    Save Game"
                , "Controls", JOptionPane.PLAIN_MESSAGE);
    }

    private void loadLevel()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new IT4FileFilter());
        int retval = fc.showDialog(this, "Open Level");

        if (retval == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();

            if ((file.getName().toLowerCase().endsWith(".it3")) || (file.getName().toLowerCase().endsWith(".it4")))
            {
                System.out.println(file.getName());
                System.out.println(file.getPath());
                newGame(file.getPath(), false, false);
            }
            
        }
    }

    private void loadLevelSet()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ILSFileFilter());
        int retval = fc.showDialog(this, "Open Levelset");

        if (retval == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();

            if ((file.getName().toLowerCase().endsWith(".it3ls")) || (file.getName().toLowerCase().endsWith(".it4ls")))
            {
                System.out.println(file.getName());
                System.out.println(file.getPath());
                newGame(file.getPath(), true, false);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(image, 0, 0, this);
    }

}
