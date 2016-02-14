/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GFX;

import java.util.Random;

/**
 *
 * @author Jim
 */
public class ParticleSystem
{
    public Particle[] particles;
    public short id = 376;//375 is snow
    private Random rng;
    private byte mode = 0;

    private static final float baseX = -0.5f;
    private static final float baseY = 0.5f;
    private static final float baseYRain = 1.0f;
    private static final float snowFactor = 1.0f;
    private static final float rainFactor = 3.0f;

    public ParticleSystem(int numParticles)
    {
        rng = new Random();

        particles = new Particle[numParticles];

        for(int i = 0; i < particles.length; i++)
        {
            particles[i] = new Particle(600);
        }
    }

    private void reset()
    {
        for(int i = 0; i < particles.length; i++)
        {
            particles[i].active = false;
            particles[i].initialized = false;
        }
    }

    private void respawnSnow(Particle p)
    {
        float zX = rng.nextFloat();
        float zY = rng.nextFloat();

        p.dx = baseX + zX;
        p.dy = (baseY + zY) * snowFactor;

        p.x = (float)(rng.nextInt(820))-20;
        p.y = -20.0f;

        if (!p.initialized)
        {
            p.y = (float)(rng.nextInt((int)p.maxY+20))-20;
            p.initialized = true;
        }

        p.active = true;
    }

    private void respawnRain(Particle p)
    {
        float zY = rng.nextFloat();

        p.dx = 0;
        p.dy = (baseYRain + zY) * rainFactor;

        p.x = (float)(rng.nextInt(820))-20;
        p.y = -20.0f;

        if (!p.initialized)
        {
            p.y = (float)(rng.nextInt((int)p.maxY+20))-20;
            p.initialized = true;
        }

        p.active = true;
    }

    public void snow()
    {
        if (mode != 0)
        {
            reset();
            mode = 0;
        }
        
        for(int i = 0; i < particles.length; i++)
        {
            if (particles[i].active)
            {
                particles[i].move();
            }
            else
            {
                respawnSnow(particles[i]);
            }
        }
    }

    public void rain()
    {
        if (mode != 1)
        {
            reset();
            mode = 1;
        }

        for(int i = 0; i < particles.length; i++)
        {
            if (particles[i].active)
            {
                particles[i].move();
            }
            else
            {
                respawnRain(particles[i]);
            }
        }
    }
}
