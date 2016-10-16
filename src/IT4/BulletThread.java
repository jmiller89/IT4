//This thread controls the movement of all Bullets

package IT4;

/**
 *
 * @author Jim (Admin)
 */

import java.util.ArrayList;

public class BulletThread implements Runnable
{
    private ArrayList<Bullet> bullets;
    private Game game;
    private int deathEffectFramesRemaining = 225;

    public BulletThread(ArrayList<Bullet> b, Game g)
    {
        bullets = b;
        game = g;
    }

    public void run()
    {
        long lasttime = System.nanoTime();
        float desiredDelta = (float)Game.SLEEPTIME;

        while (game.running)
        {
            long systime = System.nanoTime();
            float timedelta = ((systime - lasttime) / 1000000.0f);
            timedelta = Math.min(timedelta, desiredDelta);
            float moveDelta = timedelta / desiredDelta;
            //System.err.println(delta);
            lasttime = systime;

            if (game.deathEffect)
            {
                if (deathEffectFramesRemaining > 0)
                {
                    deathEffectFramesRemaining--;
                }
                else
                {
                    game.deathEffect = false;
                    deathEffectFramesRemaining = 225;
                    game.respawn();
                }
            }

            try
            {
                if (!game.paused)
                {
                    for (int i = 0; i < bullets.size(); i++)
                    {
                        if (bullets.get(i) != null)
                        {
                            bullets.get(i).move(moveDelta);

                            if (detectCollision(i) == true)
                            {
                                bullets.get(i).setStatus(false);
                            }
                        }
                    }

                    for (int i = 0; i < game.getExplosions().size(); i++)
                    {
                        game.getExplosions().get(i).scatter(moveDelta);
                    }
                }

                for (int i = 0; i < bullets.size(); i++)
                {
                    if (bullets.get(i) != null)
                    {
                        if (bullets.get(i).getStatus() == false)
                        {
                            if (!game.drawingBullets)
                            {
                                //Explode grenade
                                if (bullets.get(i).explosive)
                                {
                                    game.explosion(bullets.get(i).getX(), bullets.get(i).getY(), !(bullets.get(i).isPlayerBullet()), bullets.get(i).rank);
                                }

                                bullets.remove(i);
                            }
                        }
                    }
                }

                for (int i = 0; i < game.getExplosions().size(); i++)
                {
                    if (!game.getExplosions().get(i).status)
                    {
                        game.getExplosions().remove(i);
                    }
                }

                try
                {
                    Thread.sleep(Game.SLEEPTIME);
                }
                catch(InterruptedException e)
                {
                    System.out.println("Interrupted Exception!");
                }
            }
            catch(Exception e)
            {

            }

        }

        bullets.clear();
    }
    
    private boolean detectCollision(int index)
    {
        boolean collided = false;

        //Check for Wall Collision
        if (game.canBulletAdvance(bullets.get(index)))
        {
            collided = false;
        }
        else
        {
            collided = true;
        }

        //Check for NPC Collision
        if (bullets.get(index).isPlayerBullet() == true)
        {
            if (collided == false)
            {
                if (game.bulletCollisionNPC(bullets.get(index)) == true)
                {
                    collided = true;
                }
                else if (game.bulletCollisionCamera(bullets.get(index)) == true)
                {
                    collided = true;
                }
                else
                {
                    collided = false;
                }
            }
        }

        //Check for Player Collision
        if (bullets.get(index).isPlayerBullet() == false)
        {
            if (collided == false)
            {
                if (game.bulletCollisionPlayer(bullets.get(index)) == true)
                {
                    collided = true;
                    //game.checkPlayerStatus();
                }
                else
                {
                    collided = false;
                }
            }
        }

        if (bullets.get(index).getStatus() == false)
        {
            collided = false;
        }

        return collided;
    }
}
