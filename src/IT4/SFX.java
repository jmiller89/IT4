/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Jim
 */
public class SFX implements Runnable
{
    public static int PISTOL_GUNSHOT = 0;
    public static int RIFLE_GUNSHOT = 1;
    public static int SILENCED_GUNSHOT = 2;
    public static int SHOTGUN_GUNSHOT = 3;
    public static int EMPTY_GUNSHOT = 4;
    public static int EXPLOSION = 5;
    public static int HIT = 6;
    public static int SIREN = 7;

    public static int ALERT_MUSIC = 8;
    public static int BOSS_MUSIC = 7;

    public static final String[] songs = {"DarkForest.ogg", "Warfare.ogg", "BrokenFragment.ogg",
    "RetroSteel.ogg", "IAmYourProduct.ogg", "AndTheSunReappeared.ogg", "FourBraveChampions.ogg",
    "BuildingUp.ogg", "TightSpot.ogg", "Cavedrips.ogg", "Mystic.ogg", "AssaultOnYosuke.ogg",
    "BreakDownInTheCold.ogg", "ConfusingJustice.ogg", "Frisson.ogg", "LaunchDetected.ogg",
    "Religions.ogg"};

    public static final String[] effects = {"pistol_gunshot.wav", "rifle_gunshot.wav", "silenced_gunshot.wav",
    "shotgun_gunshot.wav", "empty_gunshot.wav", "explosion.wav", "punch.wav", "siren.wav"};

    public static final int INTRO_SONG = 6;

    public static boolean alive = true;
    public static boolean musicOn = true;
    private static int lastIndex = 0;
    public static boolean alertMode = false;
    public static boolean alertDefault = false;

    public int songIndex = -1;
    public int sourceIndex = 0;

    public static SFX self = null;

    private ConcurrentLinkedQueue<Integer> incoming;

    private Thread sfxThread = null;

    private Audio songStream = null;
    private Audio[] effectSounds = new Audio[effects.length];

    public void run()
    {
        while (alive)
        {
            try
            {
                processRequests();
                SoundStore.get().poll(0);
                Thread.sleep(10);
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
            }
        }
        
        //this.killALData();
        AL.destroy();
    }

    public static void stop()
    {
        alive = false;
        if (self != null)
        {
            try
            {
                self.sfxThread.join(1000);
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
            }
        }
    }

    public static int getNumSongs()
    {
        return songs.length;
    }

    public static String getSongPath(int index)
    {
        String songpath = "Audio/";

        if ((index < 0) || (index >= songs.length))
        {
            return "";
        }

        songpath += songs[index];

        return songpath;
    }

    public static String getFXPath(int index)
    {
        String songpath = "Audio/";

        if ((index < 0) || (index >= effects.length))
        {
            return "";
        }

        songpath += effects[index];

        return songpath;
    }

    public static void initialize()
    {
        if (self == null)
        {
            self = new SFX();
        }
    }

    private void processRequests()
    {
        while (incoming.isEmpty() == false)
        {
            int request = incoming.remove();

            //System.err.println(request);

            if (request >= 0)
            {
                int type = request & 1;
                int index = request >> 1;

                if (type == 0)
                {
                    //Play sfx
                    if ((index >= 0) && (index < effects.length))
                    {
                        //AL10.alSourcePlay(source.get(index));
                        effectSounds[index].playAsSoundEffect(1.0f, 1.0f, false);
                    }
                }
                else
                {
                    //Play song
                    if ((index >= 0) && (index < songs.length))
                    {
                        if (songIndex != index)
                        {
                            loadSong(index);
                                //AL10.alSourcePlay(source.get(NUM_BUFFERS - 1));
                            songStream.playAsMusic(1.0f, 1.0f, true);

                            if (index == SFX.ALERT_MUSIC)
                            {
                                SFX.alertMode = true;
                            }
                            else
                            {
                                SFX.alertMode = false;
                            }
                        }
                    }
                }
            }
            else if (request == -1)
            {
                //AL10.alSourceStop(source.get(NUM_BUFFERS - 1));
                songStream.stop();
            }
            else if (request == -2)
            {
                for(int i = 0; i < effects.length; i++)
                {
                    effectSounds[i].stop();
                }
            }
        }
    }

    private void loadSong(int index)
    {
        if (index != songIndex)
        {
            if (songStream != null)
            {
                songStream.stop();
            }
            
            try
            {
                songStream = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource(getSongPath(index)));
            }
            catch (IOException ex)
            {
                Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
            }

            songIndex = index;
        }
    }

    public SFX()
    {
        try
        {
            songStream = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource(getSongPath(INTRO_SONG)));
        }
        catch (IOException ex)
        {
            Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
        }

        for(int i = 0; i < effects.length; i++)
        {
            try
            {
                effectSounds[i] = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(getFXPath(i)));
            }
            catch (IOException ex)
            {
                Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        incoming = new ConcurrentLinkedQueue<Integer>();

        sfxThread = new Thread(this);
        sfxThread.start();
    }

    public static void playMusic(int index)
    {
        if (self != null)
        {
            self._playAudio(index, 1);
        }
    }

    public static void stopMusic()
    {
        if (self != null)
        {
            self._stopAudio();
        }
    }

    public static void stopFX()
    {
        if (self != null)
        {
            self._stopFX();
        }
    }

    public static void playSound(int index)
    {
        if (self != null)
        {
            self._playAudio(index, 0);
        }
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

    private void _playAudio(int index, int type)
    {
        int request = type;
        request = request | (index << 1);
        incoming.add(request);
    }

    private void _stopAudio()
    {
        int request = -1;
        incoming.add(request);
    }

    private void _stopFX()
    {
        int request = -2;
        incoming.add(request);
    }
    
}
