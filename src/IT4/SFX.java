/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

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

    public static final String[] songs = {"DarkForest.wav", "Warfare.wav", "BrokenFragment.wav",
    "RetroSteel.wav", "IAmYourProduct.wav", "AndTheSunReappeared.wav", "FourBraveChampions.wav",
    "BuildingUp.wav", "TightSpot.wav"};

    public static final String[] effects = {"pistol_gunshot.wav", "rifle_gunshot.wav", "silenced_gunshot.wav",
    "shotgun_gunshot.wav", "empty_gunshot.wav", "explosion.wav", "punch.wav", "siren.wav"};

    /** Maximum data buffers we will need. */
    public static final int NUM_BUFFERS = 9;

    public static final int NUM_SOURCES = NUM_BUFFERS + 1;

    public static final int INTRO_SONG = 6;

    public static boolean alive = true;
    public static boolean musicOn = true;
    private static int lastIndex = 0;
    public static boolean alertMode = false;
    public static boolean alertDefault = false;

    /** Buffers hold sound data. */
    //private IntBuffer buffer = BufferUtils.createIntBuffer(NUM_BUFFERS);
    private IntBuffer buffer = null;

    /** Sources are points emitting sound. */
    //private IntBuffer source = BufferUtils.createIntBuffer(NUM_SOURCES);
    private IntBuffer source = null;

    /** Position of the source sound. */
    private FloatBuffer sourcePos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

    /** Velocity of the source sound. */
    private FloatBuffer sourceVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

    /** Position of the listener. */
    //private FloatBuffer listenerPos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

    /** Velocity of the listener. */
    //private FloatBuffer listenerVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

    /** Orientation of the listener. (first 3 elements are "at", second 3 are "up") */
    //private FloatBuffer listenerOri = (FloatBuffer)BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f }).rewind();

    public int songIndex = -1;
    public int sourceIndex = 0;

    public static SFX self = null;

    private ConcurrentLinkedQueue<Integer> incoming;

    private Thread sfxThread = null;

    public void run()
    {
        while (alive)
        {
            try
            {
                processRequests();
                Thread.sleep(10);
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
            }
        }
        
        this.killALData();
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

    private void killALData()
    {
        // set to 0, num_sources
        int position = source.position();
        source.position(0).limit(position);
        AL10.alDeleteSources(source);
        AL10.alDeleteBuffers(buffer);
    }

    private int initAL()
    {
        try {
            AL.create();
        } catch (LWJGLException ex) {
            Logger.getLogger(SFX.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Load wav data into a buffer.
        buffer = BufferUtils.createIntBuffer(NUM_BUFFERS);
        source = BufferUtils.createIntBuffer(NUM_SOURCES);
        
        AL10.alGenBuffers(buffer);

        if(AL10.alGetError() != AL10.AL_NO_ERROR)
        return AL10.AL_FALSE;

        for(int i = 0; i < NUM_BUFFERS-1; i++)
        {
            WaveData waveFile = WaveData.create(getFXPath(i));
            AL10.alBufferData(buffer.get(i), waveFile.format, waveFile.data, waveFile.samplerate);
            waveFile.dispose();
            initSource(i, false);
            //System.err.println(i);
        }

        //WaveData waveFile = WaveData.create(getSongPath(INTRO_SONG));
        //AL10.alBufferData(buffer.get(NUM_BUFFERS - 1), waveFile.format, waveFile.data, waveFile.samplerate);
        //waveFile.dispose();
        //initSource(NUM_BUFFERS-1, true);
        //System.err.println(NUM_BUFFERS-1);

        // Do another error check and return.
        if (AL10.alGetError() == AL10.AL_NO_ERROR)
        {
            return AL10.AL_TRUE;
        }

        return AL10.AL_FALSE;
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
                        //addSource(index+1);
                        AL10.alSourcePlay(source.get(index));
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
                            AL10.alSourcePlay(source.get(NUM_BUFFERS - 1));
                        }
                        /*
                        if (songIndex != -1)
                        {
                            AL10.alSourcePlay(source.get(NUM_BUFFERS - 1));
                            songIndex = index;
                        }
                        else
                        {
                            loadSong(index);
                            AL10.alSourcePlay(source.get(NUM_BUFFERS - 1));
                        }
                         *
                         */
                    }
                }
            }
            else if (request == -1)
            {
                AL10.alSourceStop(source.get(NUM_BUFFERS - 1));
            }
            else if (request == -2)
            {
                AL10.alSourcePause(source.get(NUM_BUFFERS - 1));
                songIndex = -2;
            }
        }
    }

    private void loadSong(int index)
    {
        if (index != songIndex)
        {
            int position = source.position();
            if (songIndex == -1)
            {
                source.limit(position + 1);
            }
            else
            {
                AL10.alSourceStop(source.get(NUM_BUFFERS - 1));

                AL10.alSourcei(source.get(NUM_BUFFERS - 1), AL10.AL_BUFFER, 0);
            }

            WaveData waveFile = WaveData.create(getSongPath(index));
            AL10.alBufferData(buffer.get(NUM_BUFFERS - 1), waveFile.format, waveFile.data, waveFile.samplerate);
            waveFile.dispose();

            //System.err.println(getSongPath(index));

            AL10.alGenSources(source);

            if (AL10.alGetError() != AL10.AL_NO_ERROR)
            {
                System.out.println("Error generating audio source.");
                System.exit(-1);
            }

            AL10.alSourcei(source.get(NUM_BUFFERS-1), AL10.AL_BUFFER,   buffer.get(NUM_BUFFERS-1) );
            AL10.alSourcef(source.get(NUM_BUFFERS-1), AL10.AL_PITCH,    1.0f             );
            AL10.alSourcef(source.get(NUM_BUFFERS-1), AL10.AL_GAIN,     1.0f             );
            AL10.alSource (source.get(NUM_BUFFERS-1), AL10.AL_POSITION, sourcePos        );
            AL10.alSource (source.get(NUM_BUFFERS-1), AL10.AL_VELOCITY, sourceVel        );
            AL10.alSourcei(source.get(NUM_BUFFERS-1), AL10.AL_LOOPING,  AL10.AL_TRUE     );

            if (songIndex == -1)
            {
                source.position(position+1);
            }

            songIndex = index;
        }
    }

    public SFX()
    {
        incoming = new ConcurrentLinkedQueue<Integer>();
        //source.position();
        int retval = initAL();
        //System.err.println("everything ok? " + (retval == AL10.AL_TRUE));

        source.limit(NUM_SOURCES - 1);

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

    public static void pauseMusic()
    {
        if (self != null)
        {
            self._pauseAudio();
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

    private void _pauseAudio()
    {
        int request = -2;
        incoming.add(request);
    }

    private void initSource(int bufferIndex, boolean continuous)
    {
        int position = source.position();
        source.limit(position + 1);
        AL10.alGenSources(source);

        if (AL10.alGetError() != AL10.AL_NO_ERROR)
        {
            System.out.println("Error generating audio source.");
            System.exit(-1);
        }

        AL10.alSourcei(source.get(position), AL10.AL_BUFFER,   buffer.get(bufferIndex) );
        AL10.alSourcef(source.get(position), AL10.AL_PITCH,    1.0f             );
        AL10.alSourcef(source.get(position), AL10.AL_GAIN,     1.0f             );
        AL10.alSource (source.get(position), AL10.AL_POSITION, sourcePos        );
        AL10.alSource (source.get(position), AL10.AL_VELOCITY, sourceVel        );

        if (continuous)
        {
            AL10.alSourcei(source.get(position), AL10.AL_LOOPING,  AL10.AL_TRUE     );
        }

        // next index
        source.position(position+1);
    }


    
}
