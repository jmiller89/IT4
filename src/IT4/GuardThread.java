//This is a thread for an NPC

package IT4;

/**
 *
 * @author Jim (Admin)
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class GuardThread implements Runnable
{
    private Game game;
    private static byte FAST_SHOT_INTERVAL = 40;
    private static byte GUN_CAM_SHOT_INTERVAL = 20;
    private static byte MAX_ANIMATION_ITERS = 16; //was 8
    //private int speedAdjuster = 20;
    private int spawnTimer = 0;
    private int lastRoomIndex = 0;
    private Vertex[][] vertices;
    private int plx;
    private int ply;
    private PriorityQueue<Vertex> open = new PriorityQueue<Vertex>();
    private ArrayList<Vertex> closed = new ArrayList<Vertex>();
    private ArrayList<Vertex> path = new ArrayList<Vertex>();
    private boolean lastPlayerOnPath = false;
    //private static float GUARD_RUNSPEED = 0.95f;
    //private static float GUARD_WALKSPEED = 0.5f;

    private float moveDelta = 1.0f;

    public GuardThread(Game gm)
    {
        game = gm;
        plx = game.getPlayerTileX();
        ply = game.getPlayerTileY();
        vertices = null;
    }

    public void run()
    {
        //System.out.println("Guard Thread is running");
        vertices = game.getVertices();
        
        //Initialize guards
        for(int i = 0; i < game.getNPCs().size(); i++)
        {
            this.guardInit(game.getNPCs().get(i));
        }

        long lasttime = System.nanoTime();
        float desiredDelta = (float)Game.SLEEPTIME;

        //Main Loop starts here
        while (game.running)
        {
            long systime = System.nanoTime();
            float timedelta = ((systime - lasttime) / 1000000.0f);
            timedelta = Math.min(timedelta, desiredDelta);
            moveDelta = timedelta / desiredDelta;
            //System.err.println(delta);
            lasttime = systime;

            try
            {
                //Reset spawn timer on room change
                vertices = game.getVertices();
                
                if (lastRoomIndex != game.getRoomIndex())
                {
                    spawnTimer = 0;
                    lastRoomIndex = game.getRoomIndex();

                    lastPlayerOnPath = false;
                }
                

                for(int i = 0; i < game.getNPCs().size(); i++)
                {
                    NPC npc = game.getNPCs().get(i);
                    if (npc.initialized == false)
                    {
                        guardInit(npc);
                    }

                    if ((!game.paused) && (game.isPlayerAlive()))
                    {
                        if (npc.markedForDeath)
                        {
                            npc.markedForDeathIters--;

                            if (npc.markedForDeathIters <= 0)
                            {
                                npc.setHealth(0);
                            }
                        }

                        guardAI(npc);
                    }
                    else
                    {
                        //Reset npc ids if in dialog
                        if (game.inDialog)
                        {
                            resetGuardAnimation(npc);
                        }
                    }
                }
                
                if (game.playerOnPath)
                {
                    if (!lastPlayerOnPath)
                    {
                        playerInit(game.getPlayer());
                    }

                    if ((!game.paused) && (game.isPlayerAlive()))
                    {
                        playerAI(game.getPlayer());
                    }
                    else
                    {
                        //Reset player id if in dialog
                        if (game.inDialog)
                        {
                            game.resetPlayerAnimation();
                        }
                    }
                }                                
                
                lastPlayerOnPath = game.playerOnPath;

                //Add boss handling here
                if (!game.paused)
                {
                    if (game.fightBoss())
                    {
                        if (game.getBoss() != null)
                        {
                            if (!game.getBoss().initialized)
                            {
                                bossInit(game.getBoss());
                            }

                            if (game.isPlayerAlive())
                            {
                                bossAI(game.getBoss());
                            }
                        }
                    }
                }

                for(int i = 0; i < game.getCameras().size(); i++)
                {
                    SecurityCamera sc = game.getCameras().get(i);
                    
                    if (!game.paused)
                    {
                        if (sc.spottedPlayerReaction > 0)
                        {
                            sc.spottedPlayerReaction--;
                        }

                        int x = sc.getX() + sc.xOffset;
                        int y = sc.getY() + sc.yOffset;
                        boolean stopCamera = false;

                        if (game.isPlayerVisible(x+20, y+20, sc.getDirection(), 280))
                        {
                            if ((game.getPlayerStance() == Stance.PRONE) && ((game.isPlayerInWater() || (game.isPlayerInTallGrass()))))
                            {
                                //Player is hidden
                            }
                            else
                            {
                                stopCamera = true;
                                if (sc.hasGun())
                                {
                                    sc.spottedPlayerReaction = 15;
                                    if (sc.currentShotInterval <= 0)
                                    {
                                        Bullet b = sc.attack((short)181, game.getPlayerX(), game.getPlayerY(), 0, 1);

                                        if (b != null)
                                        {
                                            game.NPCAttack(b);
                                        }
                                        
                                        sc.currentShotInterval = GUN_CAM_SHOT_INTERVAL;
                                    }
                                    else
                                    {
                                        sc.currentShotInterval--;
                                    }
                                }
                                else
                                {
                                    if (!game.isPlayerSpotted())
                                    {
                                        sc.spottedPlayerReaction = 60;
                                        game.setPlayerSpotted();
                                    }
                                }
                            }

                        }

                        if (!stopCamera)
                        {
                            if (!game.isPlayerSpotted())
                            {
                                game.getCameras().get(i).move(moveDelta);
                            }
                            else
                            {
                                if (game.getCameras().get(i).hasGun())
                                {
                                    game.getCameras().get(i).move(moveDelta);
                                }
                            }
                        }
                    }
                }
                
                if (game.isPlayerSpotted())
                {
                    if (!game.paused)
                    {
                        
                        if (spawnTimer <= 0)
                        {

                            for(int i = 0; i < game.getSpawns().size(); i++)
                            {   
                                NPC spawned = game.getSpawns().get(i).spawnNPC();
                                
                                if (spawned != null)
                                {                                    
                                    game.getNPCs().add(spawned);
                                }
                            }

                            spawnTimer = game.SPAWN_TIME;
                        }
                        else
                        {
                            spawnTimer--;
                        }
                    }
                }

                //Clean up
                for(int i = 0; i < game.getNPCs().size(); i++)
                {
                    if (game.getNPCs().get(i).getStatus() == NPCStatus.DEAD)
                    {
                        if (game.getNPCs().get(i).getCurrentHealth() <= 0)
                        {
                            game.enemyKilled();
                        }

                        game.getNPCs().get(i).closeDoor();
                        game.getNPCs().remove(i);
                        
                    }
                }

                if (game.fightBoss())
                {
                    if (game.getBoss().getStatus() == NPCStatus.DEAD)
                    {
                        game.bossDefeated();
                        game.enemyKilled();
                    }
                }

                for(int i = 0; i < game.getCameras().size(); i++)
                {
                    if (game.getCameras().get(i).alive == false)
                    {
                        SecurityCamera deadCam = game.getCameras().remove(i);
                        game.explosion(deadCam.getX(), deadCam.getY(), false, 1);
                    }
                }
    //
    //            for(int i = 0; i < game.getSpawns().size(); i++)
    //            {
    //                if (game.getSpawns().get(i).remove == true)
    //                {
    //                    game.getSpawns().remove(i);
    //                }
    //            }

                //try
                //{
                    Thread.sleep(Game.SLEEPTIME);
                //}
                //catch(InterruptedException e)
                //{
                //    System.out.println("Interrupted Exception!");
                //}

            }
            catch(Exception e)
            {
                System.out.println("GuardThread Exception");
            }
        }

    }

    private void friendAI(NPC friend)
    {
        if (friend.getStatus() == NPCStatus.TRANQUILIZED_SLEEP)
        {
            friend.setStatus(NPCStatus.PATROL);
        }

        if (game.isPlayerSpotted() == true)
        {
            //speedAdjuster = 10;
            friend.movementDelta = friend.NPC_RUNSPEED;
        }
        else
        {
            //speedAdjuster = 20;
            friend.movementDelta = friend.NPC_WALKSPEED;
        }

        if (friend.firstAI)
        {
            int ptx = game.getPlayerTileX();
            int pty = game.getPlayerTileY();

            byte[][] om = game.getObstacleMatrix();
            //int deltax = ptx - game.lastPlayerTileX;
            //int deltay = pty - game.lastPlayerTileY;

            //System.out.println(deltax + ", " + deltay);

            Direction d = game.getPlayerDirection();
            boolean placed = false;

            try
            {
                if (om != null)
                {

                    if (d == Direction.UP)
                    {
                        if (pty + 1 < om.length)
                        {
                            if (om[pty+1][ptx] == 1)
                            {
                                synchronized (game.followerLock)
                                {
                                    game.lastPlayerTileX = game.getPlayerTileX();
                                    game.lastPlayerTileY = game.getPlayerTileY() + 1;
                                }
                                plx = game.lastPlayerTileX;
                                ply = game.lastPlayerTileY;
                                placed = true;
                            }
                        }
                    }
                    else if (d == Direction.DOWN)
                    {
                        if (pty - 1 >= 0)
                        {
                            if (om[pty-1][ptx] == 1)
                            {
                                synchronized (game.followerLock)
                                {
                                    game.lastPlayerTileX = game.getPlayerTileX();
                                    game.lastPlayerTileY = game.getPlayerTileY() - 1;
                                }
                                plx = game.lastPlayerTileX;
                                ply = game.lastPlayerTileY;
                                placed = true;
                            }
                        }
                    }
                    else if (d == Direction.LEFT)
                    {
                        if (ptx - 1 >= 0)
                        {
                            if (om[pty][ptx-1] == 1)
                            {
                                synchronized (game.followerLock)
                                {
                                    game.lastPlayerTileX = game.getPlayerTileX() - 1;
                                    game.lastPlayerTileY = game.getPlayerTileY();
                                }
                                plx = game.lastPlayerTileX;
                                ply = game.lastPlayerTileY;
                                placed = true;
                            }
                        }
                    }
                    else if (d == Direction.RIGHT)
                    {
                        if (ptx + 1 < om[0].length)
                        {
                            if (om[pty][ptx+1] == 1)
                            {
                                synchronized (game.followerLock)
                                {
                                    game.lastPlayerTileX = game.getPlayerTileX() + 1;
                                    game.lastPlayerTileY = game.getPlayerTileY();
                                }
                                plx = game.lastPlayerTileX;
                                ply = game.lastPlayerTileY;
                                placed = true;
                            }
                        }
                    }
                }
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                //Context switch, oh well. It won't break the program.
                System.out.println("Context switch in the middle of check");
            }

            if (!placed)
            {
                synchronized (game.followerLock)
                {
                    game.lastPlayerTileX = game.getPlayerTileX();
                    game.lastPlayerTileY = game.getPlayerTileY();
                }

                plx = game.lastPlayerTileX;
                ply = game.lastPlayerTileY;
            }
            
            friend.firstAI = false;
        }

        if (((plx != game.getPlayerTileX()) || (ply != game.getPlayerTileY())) && (friend.guardStopped))
        {
            plx = game.lastPlayerTileX;
            ply = game.lastPlayerTileY;
            
            friend.setPath(getPathTo(friend.getX(), friend.getY(), plx * 40, ply * 40));
            friend.wayX = friend.getPath().getNextWaypoint().getXPos() * 40;
            friend.wayY = friend.getPath().getNextWaypoint().getYPos() * 40;

            //System.out.println("GP SIZE " + friend.getPath().getAllWaypoints().size());

            if (friend.isPathAttainable())
            {
                //System.out.println("PATH ATTAINABLE");
                friend.setX(friend.getPath().getStartingWaypoint().getXPos() * 40);
                friend.setY(friend.getPath().getStartingWaypoint().getYPos() * 40);
                friend.guardStopped = false;
            }
            else
            {
                System.out.println("***Trapped");
                friend.trapped = true;
                friend.guardStopped = true;
            }

            friend.changeDirection(friend.getPath().getStartingWaypoint().getDirection());
            resetGuardAnimation(friend);

            plx = game.getPlayerTileX();
            ply = game.getPlayerTileY();
        }
        else
        {
            if (friend.getPath().getNextWaypointIndex() == 0)
            {
                friend.guardStopped = true;
            }

            if (friend.guardStopped)
            {
                int dx = game.getPlayerTileX() - friend.getTileX();
                int dy = game.getPlayerTileY() - friend.getTileY();

                double distance = Math.sqrt((double)((dx * dx) + (dy * dy)));
                if (distance > 1.0)
                {
                    //System.out.println(distance);
                    plx = game.getPlayerTileX();
                    ply = game.getPlayerTileY();
                }
            }
            
        }
    }

    private void correctCharacterPositioning(ITCharacter c)
    {
        Direction dir = c.getDirection();

        if (dir == Direction.UP)
        {
            if ((c.getY() < c.wayY) && (c.lastY > c.wayY))
            {
                //System.err.println("Correcting ITCharacter position");
                c.setY(c.wayY);
            }
        }
        else if (dir == Direction.DOWN)
        {
            if ((c.getY() > c.wayY) && (c.lastY < c.wayY))
            {
                //System.err.println("Correcting ITCharacter position");
                c.setY(c.wayY);
            }
        }
        else if (dir == Direction.LEFT)
        {
            if ((c.getX() < c.wayX) && (c.lastX > c.wayX))
            {
                //System.err.println("Correcting ITCharacter position");
                c.setX(c.wayX);
            }
        }
        else if (dir == Direction.RIGHT)
        {
            if ((c.getX() > c.wayX) && (c.lastX < c.wayX))
            {
                //System.err.println("Correcting ITCharacter position");
                c.setX(c.wayX);
            }
        }
    }

    private void playerAI(Player player)
    {
        if (player != null)
        {
            if (player.getStance() == Stance.PRONE)
            {
                player.changeStance(game.forceprone);
            }

            correctCharacterPositioning(player);

            if ((player.getX() == player.wayX) && (player.getY() == player.wayY))
            {
                reachedWaypoint(player);
            }
            
            //Move the player
            if ((!player.guardStopped) && (player.timeToWait == 0))
            {
                if (player.getDirection() == Direction.UP)
                {
                    game.moveUp(player.NPC_WALKSPEED, moveDelta);
                }
                else if (player.getDirection() == Direction.DOWN)
                {
                    game.moveDown(player.NPC_WALKSPEED, moveDelta);
                }
                else if (player.getDirection() == Direction.LEFT)
                {
                    game.moveLeft(player.NPC_WALKSPEED, moveDelta);
                }
                else if (player.getDirection() == Direction.RIGHT)
                {
                    game.moveRight(player.NPC_WALKSPEED, moveDelta);
                }
            }

            if (player.timeToWait > 0)
            {
                player.timeToWait--;
            }
        }
    }

    private void guardAI(NPC guard)
    {
        //Don't have guards walking into walls
        if ((game.canAdvance(guard, guard.getDirection(), 1) == false) && (guard.trapped == false))
        {
            guard.setPath(getPathTo(guard.getX(), guard.getY(), game.getPlayerX(), game.getPlayerY()));

            guard.wayX = guard.getPath().getNextWaypoint().getXPos() * 40;
            guard.wayY = guard.getPath().getNextWaypoint().getYPos() * 40;

            if (guard.isPathAttainable())
            {
                //System.out.println("PATH ATTAINABLE");
                guard.setX(guard.getPath().getStartingWaypoint().getXPos() * 40);
                guard.setY(guard.getPath().getStartingWaypoint().getYPos() * 40);
            }
            else
            {
                System.out.println("***Trapped");
                guard.trapped = true;
                guard.guardStopped = true;
            }

            guard.changeDirection(guard.getPath().getStartingWaypoint().getDirection());
            resetGuardAnimation(guard);
        }

        //Added v2.07
        if ((guard.isFriendly()) && (guard.following))
        {
            friendAI(guard);
        }
        else if (!guard.isFriendly())
        {
            correctCharacterPositioning(guard);
        }
        
        if ((game.isPlayerSpotted() == true) && (guard.getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
        {
            //speedAdjuster = 12;
            guard.movementDelta = guard.NPC_RUNSPEED;
            if (guard.getStatus() != NPCStatus.SUSPICIOUS)
            {
                guard.setStatus(NPCStatus.ALERT);
            }
            guard.timeToWait = 0;

            if (!guard.isFriendly())
            {
                if (!guard.pathFound)
                {
                    guard.pathFound = true;

                    guard.setPath(getPathTo(guard.getX(), guard.getY(), game.getPlayerX(), game.getPlayerY()));

                    guard.wayX = guard.getPath().getNextWaypoint().getXPos() * 40;
                    guard.wayY = guard.getPath().getNextWaypoint().getYPos() * 40;

                    if (guard.getX() > guard.getPath().getStartingWaypoint().getXPos() * 40)
                    {
                        guard.changeDirection(Direction.LEFT);
                        guard.correcting = true;
                    }
                    if (guard.getX() < guard.getPath().getStartingWaypoint().getXPos() * 40)
                    {
                        guard.changeDirection(Direction.RIGHT);
                        guard.correcting = true;
                    }
                    if (guard.getY() > guard.getPath().getStartingWaypoint().getYPos() * 40)
                    {
                        guard.changeDirection(Direction.UP);
                        guard.correcting = true;
                    }
                    if (guard.getY() < guard.getPath().getStartingWaypoint().getYPos() * 40)
                    {
                        guard.changeDirection(Direction.DOWN);
                        guard.correcting = true;
                    }
                    if ((guard.getX() == guard.getPath().getStartingWaypoint().getXPos() * 40) && (guard.getY() == guard.getPath().getStartingWaypoint().getYPos() * 40))
                    {
                        guard.changeDirection(guard.getPath().getStartingWaypoint().getDirection());
                    }

                    //guard.changeDirection(guard.getPath().getStartingWaypoint().getDirection());

                    resetGuardAnimation(guard);                    
                }
            }
        }
        else
        {
            //speedAdjuster = 20;
            guard.movementDelta = guard.NPC_WALKSPEED;
        }
        
        if ((guard.isFriendly()) && (guard.following))
        {
            guard.movementDelta = guard.NPC_RUNSPEED;
        }

        if ((guard.getStatus() == NPCStatus.SLEEP) || (guard.getStatus() == NPCStatus.TRANQUILIZED_SLEEP))
        {
            guard.guardStopped = true;

            //Guards can only be tranquilized for 30 seconds.
            if (guard.getStatus() == NPCStatus.TRANQUILIZED_SLEEP)
            {
                long currentTime = System.currentTimeMillis();
                if (currentTime > guard.tranqedDate + guard.tranqTimeMillis)
                {
                    guard.setStatus(NPCStatus.SUSPICIOUS);
                    guard.guardStopped = false;
                }
            }
        }

        if ((guard.getStatus() != NPCStatus.SLEEP) && (guard.getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
        {
            if (!guard.isFriendly()) //Allies will not look for or attack player
            {
                if (!guard.isRecentlyWounded)
                {
                    if (searchForPlayer(guard) == true)
                    {
                        if (game.isPlayerSpotted() == false)
                        {
                            guard.spottedPlayerReaction = 60;
                        }
                        
                        guard.setStatus(NPCStatus.ALERT);

                        game.setPlayerSpotted();

                        if (guard.currentShotInterval == 0)
                        {
                            if (!game.NPCMelee(guard))
                            {
                               attackPlayer(guard);
                            }
                            
                            guard.currentShotInterval = FAST_SHOT_INTERVAL;
                        }

                        int dx = Math.abs(game.getPlayerTileX() - guard.getPath().getEndingWaypoint().getXPos());
                        int dy = Math.abs(game.getPlayerTileY() - guard.getPath().getEndingWaypoint().getYPos());
                        if ((dx + dy) > 3)
                        {
                            guard.getPath().adjust();
                        }
                    }
                }
            }
        }
        else if (guard.getStatus() == NPCStatus.SLEEP) //Sleeping guards can be woken up
        {
            if ((((game.getPlayerX() + 19) / 40) == (guard.getX() + 19) / 40) && ((game.getPlayerY() + 19) / 40 == (guard.getY() + 19) / 40))
            {
                guard.setStatus(NPCStatus.ALERT);
                game.setPlayerSpotted();

                if (guard.currentShotInterval == 0)
                {
                    if (!game.NPCMelee(guard))
                    {
                       attackPlayer(guard);
                    }
                    
                    guard.currentShotInterval = FAST_SHOT_INTERVAL;
                }
            }
        }

        if ((guard.getX() == guard.wayX) && (guard.getY() == guard.wayY))
        {
            reachedWaypoint(guard);
        }

        if (guard.correcting)
        {
            if ((guard.getX() == guard.getPath().getStartingWaypoint().getXPos() * 40) && (guard.getY() == guard.getPath().getStartingWaypoint().getYPos() * 40))
            {
                guard.correcting = false;
                guard.changeDirection(guard.getPath().getStartingWaypoint().getDirection());
                resetGuardAnimation(guard);
            }
        }

        //Move the guard
        if ((!guard.guardStopped) && (guard.timeToWait == 0) && (guard.getStatus() != NPCStatus.SUSPICIOUS))
        {
            if (guard.getDirection() == Direction.UP)
            {
                moveUp(guard);
            }
            else if (guard.getDirection() == Direction.DOWN)
            {
                moveDown(guard);
            }
            else if (guard.getDirection() == Direction.LEFT)
            {
                moveLeft(guard);
            }
            else if (guard.getDirection() == Direction.RIGHT)
            {
                moveRight(guard);
            }
        }
        else
        {
            if (!guard.isFriendly())
            {
                if (game.isPlayerSpotted() && (guard.getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
                {
                    if (!guard.trapped)
                    {
                        guard.setPath(getPathTo(guard.getX(), guard.getY(), game.getPlayerX(), game.getPlayerY()));

                        if (guard.isPathAttainable())
                        {
                            //System.out.println("PATH ATTAINABLE");
                            guard.setX(guard.getPath().getStartingWaypoint().getXPos() * 40);
                            guard.setY(guard.getPath().getStartingWaypoint().getYPos() * 40);
                        }
                        else
                        {
                            System.out.println("***Trapped");
                            guard.trapped = true;
                            guard.guardStopped = true;
                        }

                        guard.wayX = guard.getPath().getNextWaypoint().getXPos() * 40;
                        guard.wayY = guard.getPath().getNextWaypoint().getYPos() * 40;

                        //guard.setX(guard.getPath().getStartingWaypoint().getXPos() * 40);
                        //guard.setY(guard.getPath().getStartingWaypoint().getYPos() * 40);

                        guard.changeDirection(guard.getPath().getStartingWaypoint().getDirection());

                        if (guard.isPathAttainable())
                        {
                            guard.guardStopped = false;
                        }

                    }
                }

            }
        }

        if (guard.timeToWait > 0)
        {
            guard.timeToWait--;
        }

        if (guard.spottedPlayerReaction > 0)
        {
            guard.spottedPlayerReaction--;
        }

        if (guard.getStatus() == NPCStatus.SUSPICIOUS)
        {            
            guard.timeToWaitSuspect--;
            if (guard.timeToWaitSuspect == 0)
            {
                guard.timeToWaitSuspect = 150;
                guard.changeDirection(guard.getPath().getCurrentWaypoint().getDirection());
                guard.setStatus(NPCStatus.PATROL);                
            }
            resetGuardAnimation(guard);
        }        

        if (guard.isRecentlyWounded == true)
        {
            guard.timeToWaitBloodSpatter--;
        }

        if (guard.timeToWaitBloodSpatter <= 0)
        {
            guard.isRecentlyWounded = false;
            guard.timeToWaitBloodSpatter = 15;
        }

        if (guard.getStatus() == NPCStatus.ALERT)
        {
            if (guard.currentShotInterval > 0)
            {
                guard.currentShotInterval--;
            }
        }

        if (guard.getCurrentHealth() <= 0)
        {
            guard.setStatus(NPCStatus.DEAD);
        }
    }

    private void specialAttack(NPC guard)
    {
        int x = (guard.getTileX() * 40);
        int y = (guard.getTileY() * 40);
        Bullet b1 = new Bullet(x, y, -0.25f, 0, (short)181, 0, false, false, 500, 1, 0);
        Bullet b2 = new Bullet(x, y, 0.25f, 0, (short)181, 0, false, false, 500, 1, 0);
        Bullet b3 = new Bullet(x, y, 0, -0.25f, (short)181, 0, false, false, 500, 1, 0);
        Bullet b4 = new Bullet(x, y, 0, 0.25f, (short)181, 0, false, false, 500, 1, 0);

        Bullet b5 = new Bullet(x, y, -0.25f, -0.25f, (short)181, 0, false, false, 500, 1, 0);
        Bullet b6 = new Bullet(x, y, 0.25f, 0.25f, (short)181, 0, false, false, 500, 1, 0);
        Bullet b7 = new Bullet(x, y, 0.25f, -0.25f, (short)181, 0, false, false, 500, 1, 0);
        Bullet b8 = new Bullet(x, y, -0.25f, 0.25f, (short)181, 0, false, false, 500, 1, 0);

        b1.explosive = true;
        b2.explosive = true;
        b3.explosive = true;
        b4.explosive = true;
        b5.explosive = true;
        b6.explosive = true;
        b7.explosive = true;
        b8.explosive = true;

        game.NPCAttack(b1);
        game.NPCAttack(b2);
        game.NPCAttack(b3);
        game.NPCAttack(b4);
        
        game.NPCAttack(b5);
        game.NPCAttack(b6);
        game.NPCAttack(b7);
        game.NPCAttack(b8);

        guard.specialAttack = false;
    }

    private void bossAI(Boss boss)
    {
        if ((searchForPlayer(boss) == true) && (boss.toStun == false) && (boss.timeToWait == 0))
        {
            if (boss.currentShotInterval == 0)
            {
                if (!game.NPCMelee(boss))
                {
                   attackPlayer(boss);
                }
                
                boss.currentShotInterval = boss.shotInterval;
            }

            int dx = Math.abs(game.getPlayerTileX() - boss.getPath().getEndingWaypoint().getXPos());
            int dy = Math.abs(game.getPlayerTileY() - boss.getPath().getEndingWaypoint().getYPos());
            if ((dx + dy) > 3)
            {
                boss.getPath().adjust();
            }
        }

        if (boss.specialAttack)
        {
            specialAttack(boss);
        }

        //Don't have the boss walk into walls
        if (game.canAdvance(boss, boss.getDirection(), 1) == false)
        {
            boss.setPath(getPathTo(boss.getX(), boss.getY(), game.getPlayerX(), game.getPlayerY()));

            boss.wayX = boss.getPath().getNextWaypoint().getXPos() * 40;
            boss.wayY = boss.getPath().getNextWaypoint().getYPos() * 40;

            boss.setX(boss.getPath().getStartingWaypoint().getXPos() * 40);
            boss.setY(boss.getPath().getStartingWaypoint().getYPos() * 40);
            boss.changeDirection(boss.getPath().getStartingWaypoint().getDirection());
        }

        //Move the boss
        if ((!boss.guardStopped) && (boss.timeToWait == 0))
        {
            if (boss.getDirection() == Direction.UP)
            {
                moveUp(boss);
            }
            else if (boss.getDirection() == Direction.DOWN)
            {
                moveDown(boss);
            }
            else if (boss.getDirection() == Direction.LEFT)
            {
                moveLeft(boss);
            }
            else if (boss.getDirection() == Direction.RIGHT)
            {
                moveRight(boss);
            }

            correctCharacterPositioning(boss);

            if ((boss.getX() == boss.wayX) && (boss.getY() == boss.wayY))
            {
                reachedBossWaypoint(boss);
            }
        }
        else
        {
            if (game.isPlayerAlive())
            {
                boss.setPath(getPathTo(boss.getX(), boss.getY(), game.getPlayerX(), game.getPlayerY()));

                boss.wayX = boss.getPath().getNextWaypoint().getXPos() * 40;
                boss.wayY = boss.getPath().getNextWaypoint().getYPos() * 40;

                //boss.setX(boss.getPath().getStartingWaypoint().getXPos() * 40);
                //boss.setY(boss.getPath().getStartingWaypoint().getYPos() * 40);

                boss.changeDirection(boss.getPath().getStartingWaypoint().getDirection());


                boss.guardStopped = false;
            }
        }

        if (boss.isRecentlyWounded == true)
        {
            boss.timeToWaitBloodSpatter--;
        }

        if (boss.timeToWaitBloodSpatter <= 0)
        {
            boss.isRecentlyWounded = false;
            boss.timeToWaitBloodSpatter = 15;
        }

        if (boss.timeToWait > 0)
        {
            boss.timeToWait--;
        }

        if (boss.currentShotInterval > 0)
        {
            boss.currentShotInterval--;
        }

        if (boss.getCurrentHealth() <= 0)
        {
            boss.setStatus(NPCStatus.DEAD);
        }
    }

    public void playerInit(Player player)
    {                
        //speedAdjuster = 20;
        //System.err.println("playerInit called");
        Path path = game.getPlayerPath();
        if ((path != null) && (player != null))
        {
            path.resetPath();
            player.wayX = path.getNextWaypoint().getXPos() * 40;
            player.wayY = path.getNextWaypoint().getYPos() * 40;

            int startx, starty;
            startx = path.getStartingWaypoint().getXPos() * 40;
            starty = path.getStartingWaypoint().getYPos() * 40;

            if ((startx >= 0) && (starty >= 0))
            {
                player.setX(path.getStartingWaypoint().getXPos() * 40);
                player.setY(path.getStartingWaypoint().getYPos() * 40);
                player.changeDirection(path.getStartingWaypoint().getDirection());

                if (player.getDirection() == Direction.DOWN)
                {
                    player.setID(player.getID() + 1);
                }
                if (player.getDirection() == Direction.LEFT)
                {
                    player.setID(player.getID() + 2);
                }
                if (player.getDirection() == Direction.RIGHT)
                {
                    player.setID(player.getID() + 3);
                }
            }
            else
            {
                path.reachedWaypoint();
                player.wayX = path.getNextWaypoint().getXPos() * 40;
                player.wayY = path.getNextWaypoint().getYPos() * 40;
            }
        }
    }

    private void guardInit(NPC guard)
    {
        //System.out.println("Spawning NPC");

        guard.getPath().refresh();

        if (guard.getPath().getStartingWaypoint().getBehavior() == WaypointBehavior.EXIT)
        {
            Waypoint dst = guard.getPath().getEndingWaypoint().copy();
            Waypoint src = guard.getPath().getStartingWaypoint().copy();
            Path p = getPathTo(src.getXPos() * 40, src.getYPos() * 40, dst.getXPos() * 40, dst.getYPos() * 40);
            p.getEndingWaypoint().setWaypointBehavior(dst.getBehavior());
            guard.setPath(p);
            guard.NPC_WALKSPEED = guard.NPC_RUNSPEED;

            //System.err.println(guard.getPath().toString());
        }

        guard.wayX = guard.getPath().getNextWaypoint().getXPos() * 40;
        guard.wayY = guard.getPath().getNextWaypoint().getYPos() * 40;
        //guard.obstacleMatrix = game.getObstacleMatrix();
        
        guard.setX(guard.getPath().getStartingWaypoint().getXPos() * 40);
        guard.setY(guard.getPath().getStartingWaypoint().getYPos() * 40);
        guard.changeDirection(guard.getPath().getStartingWaypoint().getDirection());

        if (guard.getDirection() == Direction.DOWN)
        {
            guard.setID(guard.getID() + 1);
        }
        if (guard.getDirection() == Direction.LEFT)
        {
            guard.setID(guard.getID() + 2);
        }
        if (guard.getDirection() == Direction.RIGHT)
        {
            guard.setID(guard.getID() + 3);
        }


        guard.initialized = true;

        if ((guard.isFriendly()) && (guard.following))
        {
            initFriend(guard);
            //guard.setX(game.getPlayerTileX() * 40);
            //guard.setY(game.getPlayerTileY() * 40);
            //guard.changeDirection(game.getPlayerDirection());
            //guard.wayX = game.getPlayerTileX() * 40;
            //guard.wayY = game.getPlayerTileY() * 40;
        }
    }

    private boolean placeFriendUp(NPC guard, int x, int y, byte[][] om)
    {
        boolean placed = false;
        if (y - 1 >= 0)
        {
            if (om[y-1][x] == 1)
            {
                guard.setX(x * 40);
                guard.setY((y-1) * 40);
                guard.changeDirection(Direction.DOWN);
                guard.wayX = x * 40;
                guard.wayY = (y-1) * 40;
                placed = true;
            }
        }
        return placed;
    }

    private boolean placeFriendDown(NPC guard, int x, int y, byte[][] om)
    {
        boolean placed = false;
        if (y + 1 < om.length)
        {
            if (om[y+1][x] == 1)
            {
                guard.setX(x * 40);
                guard.setY((y+1) * 40);
                guard.changeDirection(Direction.UP);
                guard.wayX = x * 40;
                guard.wayY = (y+1) * 40;
                placed = true;
            }
        }
        return placed;
    }

    private boolean placeFriendLeft(NPC guard, int x, int y, byte[][] om)
    {
        boolean placed = false;
        if (x - 1 >= 0)
        {
            if (om[y][x-1] == 1)
            {
                guard.setX((x-1) * 40);
                guard.setY(y * 40);
                guard.changeDirection(Direction.RIGHT);
                guard.wayX = (x-1) * 40;
                guard.wayY = y * 40;
                placed = true;
            }
        }
        return placed;
    }

    private boolean placeFriendRight(NPC guard, int x, int y, byte[][] om)
    {
        boolean placed = false;
        if (x + 1 < om[0].length)
        {
            if (om[y][x+1] == 1)
            {
                guard.setX((x+1) * 40);
                guard.setY(y * 40);
                guard.changeDirection(Direction.LEFT);
                guard.wayX = (x+1) * 40;
                guard.wayY = y * 40;
                placed = true;
            }
        }
        return placed;
    }

    private void initFriend(NPC guard)
    {
        byte[][] om = game.getObstacleMatrix();
        int x = game.getPlayerTileX();
        int y = game.getPlayerTileY();
        Direction d = game.getPlayerDirection();
        
        if (om != null)
        {
            if (((x >= 0) && (x < om[0].length)) && ((y >= 0) && (y < om.length)))
            {
                boolean placed = false;

                if (d == Direction.UP)
                {
                    placed = placeFriendDown(guard, x, y, om);

                    if (!placed)
                    {
                        placed = placeFriendLeft(guard, x, y, om);
                    }
                    if (!placed)
                    {
                        placed = placeFriendRight(guard, x, y, om);
                    }
                    if (!placed)
                    {
                        placed = placeFriendUp(guard, x, y, om);
                    }
                }
                else if (d == Direction.DOWN)
                {
                    placed = placeFriendUp(guard, x, y, om);

                    if (!placed)
                    {
                        placed = placeFriendLeft(guard, x, y, om);
                    }
                    if (!placed)
                    {
                        placed = placeFriendRight(guard, x, y, om);
                    }
                    if (!placed)
                    {
                        placed = placeFriendDown(guard, x, y, om);
                    }
                }
                else if (d == Direction.LEFT)
                {
                    placed = placeFriendRight(guard, x, y, om);

                    if (!placed)
                    {
                        placed = placeFriendUp(guard, x, y, om);
                    }
                    if (!placed)
                    {
                        placed = placeFriendDown(guard, x, y, om);
                    }
                    if (!placed)
                    {
                        placed = placeFriendLeft(guard, x, y, om);
                    }
                }
                else if (d == Direction.RIGHT)
                {
                    placed = placeFriendLeft(guard, x, y, om);

                    if (!placed)
                    {
                        placed = placeFriendUp(guard, x, y, om);
                    }
                    if (!placed)
                    {
                        placed = placeFriendDown(guard, x, y, om);
                    }
                    if (!placed)
                    {
                        placed = placeFriendRight(guard, x, y, om);
                    }
                }

                if (!placed)
                {
                    guard.setX(x * 40);
                    guard.setY(y * 40);
                    guard.changeDirection(game.getPlayerDirection());
                    guard.wayX = x * 40;
                    guard.wayY = y * 40;
                }
            }
        }
        else
        {
            guard.setX(x * 40);
            guard.setY(y * 40);
            guard.changeDirection(game.getPlayerDirection());
            guard.wayX = x * 40;
            guard.wayY = y * 40;
        }
    }

    private void bossInit(Boss boss)
    {
        System.out.println("Spawning boss");

        //boss.obstacleMatrix = game.getObstacleMatrix();

        boss.setPath(getPathTo(boss.getX(), boss.getY(), game.getPlayerX(), game.getPlayerY()));

        boss.wayX = boss.getPath().getNextWaypoint().getXPos() * 40;
        boss.wayY = boss.getPath().getNextWaypoint().getYPos() * 40;

        boss.setX(boss.getPath().getStartingWaypoint().getXPos() * 40);
        boss.setY(boss.getPath().getStartingWaypoint().getYPos() * 40);
        boss.changeDirection(boss.getPath().getStartingWaypoint().getDirection());

        //speedAdjuster = boss.speed;
        if (boss.speed < 1)
        {
            boss.speed = 1;
        }
        boss.movementDelta = 10.0f / (float)boss.speed;
        boss.initialized = true;

        game.displayDialog(boss.getPreDialog(), "Boss fight", false);
        
    }

    private void resetGuardAnimation(NPC guard)
    {
        //Don't leave a guard frozen in motion when asleep or stopped!
        if (guard.getDirection() == Direction.UP)
        {
            guard.setID(guard.initialID);
        }
        else if (guard.getDirection() == Direction.DOWN)
        {
            guard.setID(guard.initialID + 1);
        }
        else if (guard.getDirection() == Direction.LEFT)
        {
            guard.setID(guard.initialID + 2);
        }
        else if (guard.getDirection() == Direction.RIGHT)
        {
            guard.setID(guard.initialID + 3);
        }
    }

    private void reachedWaypoint(Player player)
    {
        if (!player.guardStopped)
        {
            Path path = game.getPlayerPath();
            if (path != null)
            {
                if (path.getNextWaypoint() == path.getEndingWaypoint())
                {
                    game.playerOnPath = false;
                    path.resetPath();
                    game.resetPlayerAnimation();
                }

                if (game.playerOnPath)
                {
                    if (path.getNextWaypoint().getBehavior() == WaypointBehavior.WAIT_AND_CONTINUE)
                    {
                        player.timeToWait = 100;
                    }
                    if (path.getNextWaypoint().getBehavior() == WaypointBehavior.LONG_WAIT_AND_CONTINUE)
                    {
                        player.timeToWait = 200;
                    }
                    else if (path.getNextWaypoint().getBehavior() == WaypointBehavior.STOP)
                    {
                        player.guardStopped = true;
                    }

                    player.changeDirection(path.getNextWaypoint().getDirection());
                    path.reachedWaypoint();
                    player.wayX = path.getNextWaypoint().getXPos() * 40;
                    player.wayY = path.getNextWaypoint().getYPos() * 40;
                    game.resetPlayerAnimation();
                }
            }
            else
            {
                game.playerOnPath = false;
                game.resetPlayerAnimation();
            }
        }
    }

    private void reachedWaypoint(NPC guard)
    {
        if (!guard.guardStopped)
        {
            //System.out.println("Reached Waypoint! Sprite ID: " + guard.getID());

            if (game.isPlayerSpotted() == false)
            {
                if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.WAIT_AND_CONTINUE)
                {
                    guard.timeToWait = 100;
                }
                if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.LONG_WAIT_AND_CONTINUE)
                {
                    guard.timeToWait = 200;
                }
                else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.SLEEP)
                {
                    guard.setStatus(NPCStatus.SLEEP);
                }
                else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.STOP)
                {
                    guard.guardStopped = true;
                }
                else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.WARP)
                {
                    guard.guardStopped = true;
                    guard.setHealth(0);
                }
                else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.DIE)
                {
                    guard.guardStopped = true;
                    guard.isRecentlyWounded = true;
                    guard.markedForDeath = true;
                    guard.setHealth(1);
                    SFX.playSound(SFX.SHOTGUN_GUNSHOT);
                }
                else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.EXPLODE)
                {
                    guard.guardStopped = true;
                    guard.isRecentlyWounded = true;
                    game.explosion(guard.getX(), guard.getY(), false, 1);
                    guard.setHealth(0);
                }

                guard.changeDirection(guard.getPath().getNextWaypoint().getDirection());
                guard.getPath().reachedWaypoint();
                guard.wayX = guard.getPath().getNextWaypoint().getXPos() * 40;
                guard.wayY = guard.getPath().getNextWaypoint().getYPos() * 40;
            }
            else
            {
                if (!guard.isFriendly())
                {
                    if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.STOP)
                    {
                        guard.setPath(getPathTo(guard.getX(), guard.getY(), game.getPlayerX(), game.getPlayerY()));

                        guard.wayX = guard.getPath().getNextWaypoint().getXPos() * 40;
                        guard.wayY = guard.getPath().getNextWaypoint().getYPos() * 40;

                        if (guard.isPathAttainable())
                        {
                            guard.setX(guard.getPath().getStartingWaypoint().getXPos() * 40);
                            guard.setY(guard.getPath().getStartingWaypoint().getYPos() * 40);
                        }

                        guard.changeDirection(guard.getPath().getStartingWaypoint().getDirection());
                    }
                    else
                    {
                        guard.changeDirection(guard.getPath().getNextWaypoint().getDirection());
                        guard.getPath().reachedWaypoint();
                        guard.wayX = guard.getPath().getNextWaypoint().getXPos() * 40;
                        guard.wayY = guard.getPath().getNextWaypoint().getYPos() * 40;
                    }
                }
                else //Still do normal AI for friendly follower
                {
                    if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.WAIT_AND_CONTINUE)
                    {
                        guard.timeToWait = 100;
                    }
                    if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.LONG_WAIT_AND_CONTINUE)
                    {
                        guard.timeToWait = 200;
                    }
                    else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.SLEEP)
                    {
                        guard.setStatus(NPCStatus.SLEEP);
                    }
                    else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.STOP)
                    {
                        guard.guardStopped = true;
                    }
                    else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.STOP)
                    {
                        guard.guardStopped = true;
                    }
                    else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.WARP)
                    {
                        guard.guardStopped = true;
                        guard.setHealth(0);
                    }
                    else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.DIE)
                    {
                        guard.guardStopped = true;
                        guard.isRecentlyWounded = true;
                        guard.setHealth(1);
                        guard.markedForDeath = true;
                        SFX.playSound(SFX.SHOTGUN_GUNSHOT);
                    }
                    else if (guard.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.EXPLODE)
                    {
                        guard.guardStopped = true;
                        guard.isRecentlyWounded = true;
                        game.explosion(guard.getX(), guard.getY(), false, 1);
                        guard.setHealth(0);
                    }

                    guard.changeDirection(guard.getPath().getNextWaypoint().getDirection());
                    guard.getPath().reachedWaypoint();
                    guard.wayX = guard.getPath().getNextWaypoint().getXPos() * 40;
                    guard.wayY = guard.getPath().getNextWaypoint().getYPos() * 40;
                }
            }            

            resetGuardAnimation(guard);

        }
    }

    private void reachedBossWaypoint(Boss boss)
    {
        if (boss.toStun)
        {
            boss.timeToWait = 100;
            boss.toStun = false;
        }
        if (boss.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.WAIT_AND_CONTINUE)
        {
            boss.timeToWait = 70;
        }
        if (boss.getPath().getNextWaypoint().getBehavior() == WaypointBehavior.STOP)
        {
            boss.guardStopped = true;
        }

        boss.changeDirection(boss.getPath().getNextWaypoint().getDirection());
        boss.getPath().reachedWaypoint();
        boss.wayX = boss.getPath().getNextWaypoint().getXPos() * 40;
        boss.wayY = boss.getPath().getNextWaypoint().getYPos() * 40;

        //System.out.println("Reached Waypoint! Sprite ID: " + boss.getID());
    }

    private void moveUp(NPC guard)
    {
        if (guard.getDirection() != Direction.UP)
        {
            guard.changeDirection(Direction.UP);

            guard.setID(guard.initialID);
        }

        if (guard.animationIterations <= 0)
        {
            short lastid = guard.getID();
            guard.setID(guard.initialID + 4);

            if (lastid != guard.initialID)
            {
                guard.setID(guard.initialID);
            }

            guard.animationIterations = MAX_ANIMATION_ITERS;
        }
        else
        {
            guard.animationIterations--;
        }

        if (game.canAdvance(guard, guard.getDirection(), 1))
        {
            //guard.setY(guard.getY() - 1);
            guard.move(0, -1 * guard.movementDelta, moveDelta);
        }

        game.checkWaterAndGrass(guard);
        game.checkDoors(guard, 1);
        guard.checkDoorStatus();
        
    }

    private void moveDown(NPC guard)
    {
        if (guard.getDirection() != Direction.DOWN)
        {
            guard.changeDirection(Direction.DOWN);

            guard.setID(guard.initialID + 1);
        }

        if (guard.animationIterations <= 0)
        {
            short lastid = guard.getID();
            guard.setID(guard.initialID + 5);

            if (lastid != guard.initialID + 1)
            {
                guard.setID(guard.initialID + 1);
            }

            guard.animationIterations = MAX_ANIMATION_ITERS;
        }
        else
        {
            guard.animationIterations--;
        }

        if (game.canAdvance(guard, guard.getDirection(), 1))
        {
            //guard.setY(guard.getY() + 1);
            guard.move(0, 1 * guard.movementDelta, moveDelta);
        }

        game.checkWaterAndGrass(guard);
        game.checkDoors(guard, 1);
        guard.checkDoorStatus();
    }

    private void moveLeft(NPC guard)
    {
        if (guard.getDirection() != Direction.LEFT)
        {
            guard.changeDirection(Direction.LEFT);

            guard.setID(guard.initialID + 2);
        }

        if (guard.animationIterations <= 0)
        {
            short lastid = guard.getID();
            guard.setID(guard.initialID + 6);

            if (lastid != guard.initialID + 2)
            {
                guard.setID(guard.initialID + 2);
            }

            guard.animationIterations = MAX_ANIMATION_ITERS;
        }
        else
        {
            guard.animationIterations--;
        }

        if (game.canAdvance(guard, guard.getDirection(), 1))
        {
            //guard.setX(guard.getX() - 1);
            guard.move(-1 * guard.movementDelta, 0, moveDelta);
        }

        game.checkWaterAndGrass(guard);
        game.checkDoors(guard, 1);
        guard.checkDoorStatus();
    }

    private void moveRight(NPC guard)
    {
        if (guard.getDirection() != Direction.RIGHT)
        {
            guard.changeDirection(Direction.RIGHT);

            guard.setID(guard.initialID + 3);
        }

        if (guard.animationIterations <= 0)
        {
            short lastid = guard.getID();
            guard.setID(guard.initialID + 7);

            if (lastid != guard.initialID + 3)
            {
                guard.setID(guard.initialID + 3);
            }

            guard.animationIterations = MAX_ANIMATION_ITERS;
        }
        else
        {
            guard.animationIterations--;
        }

        if (game.canAdvance(guard, guard.getDirection(), 1))
        {
            //guard.setX(guard.getX() + 1);
            guard.move(1 * guard.movementDelta, 0, moveDelta);
        }

        game.checkWaterAndGrass(guard);
        game.checkDoors(guard, 1);
        guard.checkDoorStatus();
    }

    private boolean canShootPlayer(NPC guard)
    {
        return !((guard.isFriendly()) ||
                (guard.getType() == GuardType.MUTANT1) || (guard.getType() == GuardType.MUTANT2));
    }

    //This is where the guard will check for the player
    //Coming in to his field of vision
    private boolean searchForPlayer(NPC guard)
    {
        boolean foundPlayer = false;

        foundPlayer = game.isPlayerVisible(guard.getX() + 20, guard.getY() + 20, guard.getDirection(), guard.viewDistance);

        //Guards cannot see a player crawling through tall grass or under water
        if (foundPlayer == true)
        {
            if ((game.getPlayerStance() == Stance.PRONE) && (game.isPlayerInTallGrass() == true))
            {
                foundPlayer = false;
            }

            if ((game.getPlayerStance() == Stance.PRONE) && (game.isPlayerInWater() == true))
            {
                foundPlayer = false;
            }
        }

        //Guards cannot be walked on anymore
        if ((((game.getPlayerX() + 19) / 40) == (guard.getX() + 19) / 40) && ((game.getPlayerY() + 19) / 40 == (guard.getY() + 19) / 40))
        {
            foundPlayer = true;
        }

        return foundPlayer;
    }

    private void attackPlayer(NPC guard)
    {
        //System.out.println("Guard attacks!");
        if (canShootPlayer(guard))
        {
            Bullet b = guard.attack((short)181, game.getPlayerX(), game.getPlayerY(), 0, 1);

            if (guard.getCurrentHealth() > 0)
            {
                game.NPCAttack(b);
            }
        }
        else
        {
           game.NPCMelee(guard);
        }
    }

    private Path computePathsAstar(Vertex source, Vertex destination)
    {
        Path p = new Path();
        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

        open.clear();
        closed.clear();
        //LinkedList<Vertex> open = new LinkedList<Vertex>();
        //ArrayList<Vertex> open = new ArrayList<Vertex>(); //game.getObstacleMatrix().length * game.getObstacleMatrix()[0].length
        
        open.add(source);
        source.minDistance = 0.0f;

        Vertex v = null;

        //Loop
        while ((!open.isEmpty()) && (!destination.closed))
        {
            //Collections.sort(open);

            v = open.poll();
            //v = open.removeFirst();
            
            v.closed = true;
            closed.add(v);

            //System.out.println("Searching " + v.toString());

            for(Edge e : v.adjacencies)
            {
                Vertex v2 = e.target;

                if (!v2.closed)
                {
                    if (!open.contains(v2))
                    {
                        v2.previous = v;

                        int weight = e.weight;
                        float distanceThroughU = v.minDistance + weight + calculateHeuristic(v2, destination, source);

                        v2.minDistance = distanceThroughU;

                        open.add(v2);
                    }
                    else
                    {
                        if ((e.weight + v.minDistance) < v2.minDistance)
                        {
                            open.remove(v2);
                            v2.previous = v;

                            int weight = e.weight;
                            float distanceThroughU = v.minDistance + weight + calculateHeuristic(v2, destination, source);

                            v2.minDistance = distanceThroughU;
                            open.add(v2);
                        }
                    }
                }
            }
        }

        getShortestPathTo(destination);

        for(int i = 0; i < path.size(); i++)
        {
            //Add waypoints to p here
            if (i+1 >= path.size())
            {
                if (i-1 >= 0)
                {
                    waypoints.add(new Waypoint(path.get(i).x, path.get(i).y, waypoints.get(i-1).getDirection(), WaypointBehavior.STOP));
                }
                else
                {
                    waypoints.add(new Waypoint(path.get(i).x, path.get(i).y, Direction.DOWN, WaypointBehavior.STOP));
                }
            }
            else
            {
                //Only 1 of these conditions will be true
                if (path.get(i+1).x > path.get(i).x)
                {
                    //Right
                    waypoints.add(new Waypoint(path.get(i).x, path.get(i).y, Direction.RIGHT, WaypointBehavior.CONTINUE));
                }
                else if (path.get(i+1).x < path.get(i).x)
                {
                    //Left
                    waypoints.add(new Waypoint(path.get(i).x, path.get(i).y, Direction.LEFT, WaypointBehavior.CONTINUE));
                }
                else if (path.get(i+1).y > path.get(i).y)
                {
                    //Down
                    waypoints.add(new Waypoint(path.get(i).x, path.get(i).y, Direction.DOWN, WaypointBehavior.CONTINUE));
                }
                else if (path.get(i+1).y < path.get(i).y)
                {
                    //Up
                    waypoints.add(new Waypoint(path.get(i).x, path.get(i).y, Direction.UP, WaypointBehavior.CONTINUE));
                }
            }
        }

        p.addWaypoints(waypoints);

        //System.out.println("closed size:" + closed.size());
        //System.out.println("open size:" + open.size());

        //Reset the vertices
        for(int i = 0; i < closed.size(); i++)
        {
            closed.get(i).minDistance = Float.MAX_VALUE;
            closed.get(i).closed = false;
            closed.get(i).previous = null;            
        }
        while(!open.isEmpty())
        {
            Vertex x = open.poll();
            x.minDistance = Float.MAX_VALUE;
            x.previous = null;
        }

        return p;

    }

    private float calculateHeuristic(Vertex src, Vertex dest, Vertex start)
    {
        float distance = 0.0f;

        int xpart = Math.abs(dest.x - src.x);
        
        int ypart = Math.abs(dest.y - src.y);

        distance = xpart + ypart;

        int dx1 = src.x - dest.x;
        int dy1 = src.y - dest.y;
        int dx2 = start.x - dest.x;
        int dy2 = start.y - dest.y;

        float cross = Math.abs((dx1 * dy2) - (dx2 * dy1));
        distance += (cross * 0.001);

        return distance;
    }

    private void getShortestPathTo(Vertex target)
    {
        path.clear();

        for(Vertex vertex = target; vertex != null; vertex = vertex.previous)
        {
            path.add(vertex);
        }

        Collections.reverse(path);

        //System.out.println(path.toString());

        //return path;
    }

    private Path getPath(int srcX, int srcY, int destX, int destY)
    {   
        Path p = computePathsAstar(vertices[srcY][srcX], vertices[destY][destX]);
        return p;
        //ArrayList<Vertex> path = this.getShortestPathTo(guard.vertices[destY][destX]);
        //System.out.println("Path: " + path);
    }

    private Path getPathTo(int srcX, int srcY, int destX, int destY)
    {
        return getPath(((srcX+19)/40), ((srcY+19)/40), ((destX+19)/40), ((destY+19)/40));
    }
}
