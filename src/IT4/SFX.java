/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

import java.io.IOException;
import java.net.URL;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.sound.midi.*;

/**
 *
 * @author Jim
 */
public class SFX
{
    public static int GUNSHOT = 0;
    public static int SILENCED_GUNSHOT = 1;
    public static int HIT = 2;
    public static int EXPLOSION = 3;

    private static final String pr = "Audio/bjorn__lynne-_";
    private static final String[] sounds = {"Audio/gunshot.mid", "Audio/silencedGunshot.mid", "Audio/hit.mid", "Audio/explosion.mid"};
    //private static final String[] songs = {"Audio/Puzzle.mid", "Audio/Adventure.mid", "Audio/Ninja.mid", "Audio/AMonotonicDay.mid", "Audio/Conversions.mid",
    //"Audio/Persian_Stories.mid", "Audio/Slave.mid", "Audio/Revenge.mid", "Audio/Action.mid", "Audio/Religions.mid"};
    private static final String[] songs = {pr + "lets_go.mid", pr + "trailblazer.mid", pr + "in_the_cave.mid", pr + "incoming_signals.mid",
    pr + "proud_warriors.mid", pr + "retro_electro.mid", pr + "rock_force.mid", pr + "squadron_standby.mid", pr + "streetlight_fences.mid",
    pr + "the_chaos_warrior.mid", pr + "the_enchanted_orchard.mid", pr + "the_great_river_race.mid", pr + "the_heroes_return.mid", pr + "the_late_one.mid",
    pr + "the_sinister_maze_.mid", pr + "zombie_chase.mid", pr + "communication.mid"};

    private static Sequencer sfxPlayer = null;
    private static Sequencer songPlayer = null;

    private static boolean initialized = false;
    public static boolean musicOn = true;

    private static Sequence[] sfx = null;
    private static Sequence[] music = null;

    private static int lastIndex = 0;
    public static boolean alertMode = false;
    public static boolean alertDefault = false;

    public static void init()
    {
        if (!initialized)
        {
            sfx = new Sequence[sounds.length];
            //music = new Sequence[songs.length];
            music = new Sequence[3];

            for(int i = 0; i < sounds.length; i++)
            {
                URL url = SFX.class.getClassLoader().getResource(sounds[i]);
                
                try
                {
                    sfx[i] = MidiSystem.getSequence(url);
                }
                catch (InvalidMidiDataException ex)
                {
                    //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch(Exception e)
                {
                    
                }
            }

            try
            {
                URL url = SFX.class.getClassLoader().getResource(songs[songs.length - 2]);
                music[1] = MidiSystem.getSequence(url);
                url = SFX.class.getClassLoader().getResource(songs[songs.length - 1]);
                music[2] = MidiSystem.getSequence(url);
            }
            catch (InvalidMidiDataException ex)
            {
                //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IOException ex)
            {
                //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
            }

            /*
            for(int i = 0; i < songs.length; i++)
            {
                URL url = SFX.class.getClassLoader().getResource(songs[i]);

                try
                {
                    //System.out.println(url);
                    music[i] = MidiSystem.getSequence(url);
                }
                catch (InvalidMidiDataException ex)
                {
                    //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
             *
             */

            try
            {
                sfxPlayer = MidiSystem.getSequencer();
                sfxPlayer.open();

                songPlayer = MidiSystem.getSequencer();
                songPlayer.open();

            }
            catch (MidiUnavailableException ex)
            {
                //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            initialized = true;
        }
    }

    public static int getNumSongs()
    {
        return songs.length;
    }

    public static void playSound(int soundIndex)
    {
        if (!musicOn)
        {
            
            try
            {
                sfxPlayer.setTickPosition(0);
                sfxPlayer.setSequence(sfx[soundIndex]);
                sfxPlayer.setLoopCount(0);
                sfxPlayer.start();

            }
            catch(Exception e)
            {
                System.err.println("SFX encountered an error when attempting to play the sound " + sounds[soundIndex]);
            }
        }
    }

    public static void playMusic(int index)
    {
        alertMode = false;
        
        try
        {
            Sequence seq = null;

            if (index == songs.length - 2)
            {
                alertDefault = true;
            }
            else
            {
                alertDefault = false;
            }

            if (index > songs.length - 2)
            {
                index = (index % (songs.length - 2));
            }          
            else if (index == -2)
            {
                index = songs.length - 1;
                seq = music[2];
                //boss = true;
            }
            else if (index == -3)
            {
                index = songs.length - 2;
                alertMode = true;
                seq = music[1];
            }
            else if (index < -3)
            {
                index *= -1;
                index = (index % (songs.length - 3));
            }

            if (lastIndex == index)
            {
                seq = music[0];
            }

            if (index != songs.length - 2)
            {
                lastIndex = index;
            }

            if (seq == null)
            {
                try
                {
                    URL url = SFX.class.getClassLoader().getResource(songs[index]);
                    //System.out.println(url);
                    seq = MidiSystem.getSequence(url);
                    music[0] = seq;
                }
                catch (InvalidMidiDataException ex)
                {
                    //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    //Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            songPlayer.setSequence(seq);
            songPlayer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            if (musicOn)
            {
                songPlayer.start();
            }
        }
        catch(Exception e)
        {
            System.err.println("SFX encountered an error when attempting to play the sound " + songs[index]);
        }
    }

    /*
    public static void playLastSong()
    {
        if (alertMode)
        {
            try
            {
                songPlayer.stop();

                songPlayer.setSequence(music[lastIndex]);
                songPlayer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                alertMode = false;
                
                if (musicOn)
                {
                    songPlayer.start();
                }
            }
            catch (Exception e)
            {
                System.err.println("SFX encountered an error when attempting to play the sound " + songs[lastIndex]);
            }
        }
    }
     * 
     */

    public static void stopMusic()
    {
        songPlayer.stop();
    }

    public static void resetLastIndex()
    {
        lastIndex = -1;
    }

    public static void toggleMusic()
    {
        if (musicOn)
        {
            musicOn = false;
        }
        else
        {
            musicOn = true;
        }
    }
    
}
