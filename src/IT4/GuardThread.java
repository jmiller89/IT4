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
    private static float GUARD_RUNSPEED = 0.95f;
    private static float GUARD_WALKSPEED = 0.5f;

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
        
        //Main Loop starts here
        while (game.running)
        {
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
                    if (game.getNPCs().get(i).initialized == false)
                    {
                        guardInit(game.getNPCs().get(i));
                    }

                    if ((!game.paused) && (game.isPlayerAlive()))
                    {
                        guardAI(game.getNPCs().get(i));
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

                if (!game.isPlayerSpotted())
                {
                    for(int i = 0; i < game.getCameras().size(); i++)
                    {
                        if (!game.paused)
                        {
                            game.getCameras().get(i).move();

                            int x = game.getCameras().get(i).getX() + game.getCameras().get(i).xOffset;
                            int y = game.getCameras().get(i).getY() + game.getCameras().get(i).yOffset;

                            if (game.isPlayerVisible(x+20, y+20, game.getCameras().get(i).getDirection(), 280))
                            {
                                if ((game.getPlayerStance() == Stance.PRONE) && ((game.isPlayerInWater() || (game.isPlayerInTallGrass()))))
                                {
                                    //Player is hidden
                                }
                                else
                                {
                                    game.setPlayerSpotted();
                                }
                                
                            }
                        }
                    }
                }
                else
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

    //            for(int i = 0; i < game.getCameras().size(); i++)
    //            {
    //                if (game.getCameras().get(i).remove == true)
    //                {
    //                    game.getCameras().remove(i);
    //                }
    //            }
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
                    Thread.sleep(game.SLEEPTIME);
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
            friend.movementDelta = GUARD_RUNSPEED;
        }
        else
        {
            //speedAdjuster = 20;
            friend.movementDelta = GUARD_WALKSPEED;
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

    private void playerAI(Player player)
    {
        if (player != null)
        {
            if (player.getStance() == Stance.PRONE)
            {
                player.changeStance(game.forceprone);
            }

            if ((player.getX() == player.wayX) && (player.getY() == player.wayY))
            {
                reachedWaypoint(player);
            }
            
            //Move the player
            if ((!player.guardStopped) && (player.timeToWait == 0))
            {
                if (player.getDirection() == Direction.UP)
                {
                    game.moveUp(GUARD_WALKSPEED);
                }
                else if (player.getDirection() == Direction.DOWN)
                {
                    game.moveDown(GUARD_WALKSPEED);
                }
                else if (player.getDirection() == Direction.LEFT)
                {
                    game.moveLeft(GUARD_WALKSPEED);
                }
                else if (player.getDirection() == Direction.RIGHT)
                {
                    game.moveRight(GUARD_WALKSPEED);
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
        
        if ((game.isPlayerSpotted() == true) && (guard.getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
        {
            //speedAdjuster = 12;
            guard.movementDelta = GUARD_RUNSPEED;
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
            guard.movementDelta = GUARD_WALKSPEED;
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

    private void bossAI(Boss boss)
    {
        if (searchForPlayer(boss) == true)
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
        if (!boss.guardStopped)
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
        
        Path path = game.getPlayerPath();
        if ((path != null) && (player != null))
        {
            path.resetPath();
            player.wayX = path.getNextWaypoint().getXPos() * 40;
            player.wayY = path.getNextWaypoint().getYPos() * 40;

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
    }

    private void guardInit(NPC guard)
    {
        //System.out.println("Spawning NPC");

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

        game.displayDialog(boss.getPreDialog(), "Boss fight");
        
    }

    private void resetGuardAnimation(NPC guard)
    {
        //Don't leave a guard frozen in motion when asleep or stopped!
        
        if (guard.getDirection() == Direction.UP)
        {
            if (guard.getType() == GuardType.LIGHT)
            {
                guard.setID(19);
            }
            else if (guard.getType() == GuardType.MEDIUM)
            {
                guard.setID(27);
            }
            else if (guard.getType() == GuardType.HEAVY)
            {
                guard.setID(35);
            }
            else if (guard.getType() == GuardType.SCIENTIST1)
            {
                guard.setID(162);
            }
            else if (guard.getType() == GuardType.SCIENTIST2)
            {
                guard.setID(170);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY)
            {
                guard.setID(261);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
            {
                guard.setID(269);
            }
            else if (guard.getType() == GuardType.WORM)
            {
                guard.setID(277);
            }
            else if (guard.getType() == GuardType.LARVA)
            {
                guard.setID(285);
            }
            else if (guard.getType() == GuardType.ALIEN)
            {
                guard.setID(242);
            }
            else if (guard.getType() == GuardType.WOMAN1)
            {
                guard.setID(310);
            }
            else if (guard.getType() == GuardType.WOMAN2)
            {
                guard.setID(318);
            }
            else if (guard.getType() == GuardType.WOMAN3)
            {
                guard.setID(326);
            }
            else if (guard.getType() == GuardType.ISLAND_GUY)
            {
                guard.setID(342);
            }
            else if (guard.getType() == GuardType.CHIEF)
            {
                guard.setID(334);
            }
            else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
            {
                guard.setID(350);
            }
        }
        else if (guard.getDirection() == Direction.DOWN)
        {
            if (guard.getType() == GuardType.LIGHT)
            {
                guard.setID(20);
            }
            else if (guard.getType() == GuardType.MEDIUM)
            {
                guard.setID(28);
            }
            else if (guard.getType() == GuardType.HEAVY)
            {
                guard.setID(36);
            }
            else if (guard.getType() == GuardType.SCIENTIST1)
            {
                guard.setID(163);
            }
            else if (guard.getType() == GuardType.SCIENTIST2)
            {
                guard.setID(171);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY)
            {
                guard.setID(262);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
            {
                guard.setID(270);
            }
            else if (guard.getType() == GuardType.WORM)
            {
                guard.setID(278);
            }
            else if (guard.getType() == GuardType.LARVA)
            {
                guard.setID(286);
            }
            else if (guard.getType() == GuardType.ALIEN)
            {
                guard.setID(243);
            }
            else if (guard.getType() == GuardType.WOMAN1)
            {
                guard.setID(311);
            }
            else if (guard.getType() == GuardType.WOMAN2)
            {
                guard.setID(319);
            }
            else if (guard.getType() == GuardType.WOMAN3)
            {
                guard.setID(327);
            }
            else if (guard.getType() == GuardType.ISLAND_GUY)
            {
                guard.setID(343);
            }
            else if (guard.getType() == GuardType.CHIEF)
            {
                guard.setID(335);
            }
            else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
            {
                guard.setID(351);
            }
        }
        else if (guard.getDirection() == Direction.LEFT)
        {
            if (guard.getType() == GuardType.LIGHT)
            {
                guard.setID(21);
            }
            else if (guard.getType() == GuardType.MEDIUM)
            {
                guard.setID(29);
            }
            else if (guard.getType() == GuardType.HEAVY)
            {
                guard.setID(37);
            }
            else if (guard.getType() == GuardType.SCIENTIST1)
            {
                guard.setID(164);
            }
            else if (guard.getType() == GuardType.SCIENTIST2)
            {
                guard.setID(172);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY)
            {
                guard.setID(263);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
            {
                guard.setID(271);
            }
            else if (guard.getType() == GuardType.WORM)
            {
                guard.setID(279);
            }
            else if (guard.getType() == GuardType.LARVA)
            {
                guard.setID(287);
            }
            else if (guard.getType() == GuardType.ALIEN)
            {
                guard.setID(244);
            }
            else if (guard.getType() == GuardType.WOMAN1)
            {
                guard.setID(312);
            }
            else if (guard.getType() == GuardType.WOMAN2)
            {
                guard.setID(320);
            }
            else if (guard.getType() == GuardType.WOMAN3)
            {
                guard.setID(328);
            }
            else if (guard.getType() == GuardType.ISLAND_GUY)
            {
                guard.setID(344);
            }
            else if (guard.getType() == GuardType.CHIEF)
            {
                guard.setID(336);
            }
            else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
            {
                guard.setID(352);
            }
        }
        else if (guard.getDirection() == Direction.RIGHT)
        {
            if (guard.getType() == GuardType.LIGHT)
            {
                guard.setID(22);
            }
            else if (guard.getType() == GuardType.MEDIUM)
            {
                guard.setID(30);
            }
            else if (guard.getType() == GuardType.HEAVY)
            {
                guard.setID(38);
            }
            else if (guard.getType() == GuardType.SCIENTIST1)
            {
                guard.setID(165);
            }
            else if (guard.getType() == GuardType.SCIENTIST2)
            {
                guard.setID(173);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY)
            {
                guard.setID(264);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
            {
                guard.setID(272);
            }
            else if (guard.getType() == GuardType.WORM)
            {
                guard.setID(280);
            }
            else if (guard.getType() == GuardType.LARVA)
            {
                guard.setID(288);
            }
            else if (guard.getType() == GuardType.ALIEN)
            {
                guard.setID(245);
            }
            else if (guard.getType() == GuardType.WOMAN1)
            {
                guard.setID(313);
            }
            else if (guard.getType() == GuardType.WOMAN2)
            {
                guard.setID(321);
            }
            else if (guard.getType() == GuardType.WOMAN3)
            {
                guard.setID(329);
            }
            else if (guard.getType() == GuardType.ISLAND_GUY)
            {
                guard.setID(345);
            }
            else if (guard.getType() == GuardType.CHIEF)
            {
                guard.setID(337);
            }
            else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
            {
                guard.setID(353);
            }
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
            
            if (guard.getType() == GuardType.LIGHT)
            {
                guard.setID(19);
            }
            else if (guard.getType() == GuardType.MEDIUM)
            {
                guard.setID(27);
            }
            else if (guard.getType() == GuardType.HEAVY)
            {
                guard.setID(35);
            }
            else if (guard.getType() == GuardType.SCIENTIST1)
            {
                guard.setID(162);
            }
            else if (guard.getType() == GuardType.SCIENTIST2)
            {
                guard.setID(170);
            }
            else if (guard.getType() == GuardType.BOSS1)
            {
                guard.setID(119);
            }
            else if (guard.getType() == GuardType.BOSS2)
            {
                guard.setID(127);
            }
            else if (guard.getType() == GuardType.BOSS3)
            {
                guard.setID(135);
            }
            else if (guard.getType() == GuardType.BOSS4)
            {
                guard.setID(226);
            }
            else if (guard.getType() == GuardType.BOSS5)
            {
                guard.setID(234);
            }
            else if (guard.getType() == GuardType.BOSS6)
            {
                guard.setID(242);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY)
            {
                guard.setID(261);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
            {
                guard.setID(269);
            }
            else if (guard.getType() == GuardType.WORM)
            {
                guard.setID(277);
            }
            else if (guard.getType() == GuardType.LARVA)
            {
                guard.setID(285);
            }
            else if (guard.getType() == GuardType.ALIEN)
            {
                guard.setID(242);
            }
            else if (guard.getType() == GuardType.WOMAN1)
            {
                guard.setID(310);
            }
            else if (guard.getType() == GuardType.WOMAN2)
            {
                guard.setID(318);
            }
            else if (guard.getType() == GuardType.WOMAN3)
            {
                guard.setID(326);
            }
            else if (guard.getType() == GuardType.ISLAND_GUY)
            {
                guard.setID(342);
            }
            else if (guard.getType() == GuardType.CHIEF)
            {
                guard.setID(334);
            }
            else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
            {
                guard.setID(350);
            }

        }

        if (guard.animationIterations <= 0)
        {
            if (guard.getID() == 19)
            {
                guard.setID(23);
            }
            else if (guard.getID() == 27)
            {
                guard.setID(31);
            }
            else if (guard.getID() == 35)
            {
                guard.setID(39);
            }
            else if (guard.getID() == 162)
            {
                guard.setID(166);
            }
            else if (guard.getID() == 170)
            {
                guard.setID(174);
            }
            else if (guard.getID() == 119)
            {
                guard.setID(123);
            }
            else if (guard.getID() == 127)
            {
                guard.setID(131);
            }
            else if (guard.getID() == 135)
            {
                guard.setID(139);
            }
            else if (guard.getID() == 226)
            {
                guard.setID(230);
            }
            else if (guard.getID() == 234)
            {
                guard.setID(238);
            }
            else if (guard.getID() == 242)
            {
                guard.setID(246);
            }
            else if (guard.getID() == 261)
            {
                guard.setID(265);
            }
            else if (guard.getID() == 269)
            {
                guard.setID(273);
            }
            else if (guard.getID() == 277)
            {
                guard.setID(281);
            }
            else if (guard.getID() == 285)
            {
                guard.setID(289);
            }
            else if (guard.getID() == 310)
            {
                guard.setID(314);
            }
            else if (guard.getID() == 318)
            {
                guard.setID(322);
            }
            else if (guard.getID() == 326)
            {
                guard.setID(330);
            }
            else if (guard.getID() == 342)
            {
                guard.setID(346);
            }
            else if (guard.getID() == 334)
            {
                guard.setID(338);
            }
            else if (guard.getID() == 350)
            {
                guard.setID(354);
            }
            else
            {
                if (guard.getType() == GuardType.LIGHT)
                {
                    guard.setID(19);
                }
                else if (guard.getType() == GuardType.MEDIUM)
                {
                    guard.setID(27);
                }
                else if (guard.getType() == GuardType.HEAVY)
                {
                    guard.setID(35);
                }
                else if (guard.getType() == GuardType.SCIENTIST1)
                {
                    guard.setID(162);
                }
                else if (guard.getType() == GuardType.SCIENTIST2)
                {
                    guard.setID(170);
                }
                else if (guard.getType() == GuardType.BOSS1)
                {
                    guard.setID(119);
                }
                else if (guard.getType() == GuardType.BOSS2)
                {
                    guard.setID(127);
                }
                else if (guard.getType() == GuardType.BOSS3)
                {
                    guard.setID(135);
                }
                else if (guard.getType() == GuardType.BOSS4)
                {
                    guard.setID(226);
                }
                else if (guard.getType() == GuardType.BOSS5)
                {
                    guard.setID(234);
                }
                else if (guard.getType() == GuardType.BOSS6)
                {
                    guard.setID(242);
                }
                else if (guard.getType() == GuardType.FEMALE_ALLY)
                {
                    guard.setID(261);
                }
                else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
                {
                    guard.setID(269);
                }
                else if (guard.getType() == GuardType.WORM)
                {
                    guard.setID(277);
                }
                else if (guard.getType() == GuardType.LARVA)
                {
                    guard.setID(285);
                }
                else if (guard.getType() == GuardType.ALIEN)
                {
                    guard.setID(242);
                }
                else if (guard.getType() == GuardType.WOMAN1)
                {
                    guard.setID(310);
                }
                else if (guard.getType() == GuardType.WOMAN2)
                {
                    guard.setID(318);
                }
                else if (guard.getType() == GuardType.WOMAN3)
                {
                    guard.setID(326);
                }
                else if (guard.getType() == GuardType.ISLAND_GUY)
                {
                    guard.setID(342);
                }
                else if (guard.getType() == GuardType.CHIEF)
                {
                    guard.setID(334);
                }
                else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
                {
                    guard.setID(350);
                }
            }

            guard.animationIterations = MAX_ANIMATION_ITERS;
        }
        else
        {
            guard.animationIterations--;
        }

        //TODO: Support floats here
        if (game.canAdvance(guard, guard.getDirection(), 1))
        {
            //guard.setY(guard.getY() - 1);
            guard.move(0, -1 * guard.movementDelta);
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

            if (guard.getType() == GuardType.LIGHT)
            {
                guard.setID(20);
            }
            else if (guard.getType() == GuardType.MEDIUM)
            {
                guard.setID(28);
            }
            else if (guard.getType() == GuardType.HEAVY)
            {
                guard.setID(36);
            }
            else if (guard.getType() == GuardType.SCIENTIST1)
            {
                guard.setID(163);
            }
            else if (guard.getType() == GuardType.SCIENTIST2)
            {
                guard.setID(171);
            }
            else if (guard.getType() == GuardType.BOSS1)
            {
                guard.setID(120);
            }
            else if (guard.getType() == GuardType.BOSS2)
            {
                guard.setID(128);
            }
            else if (guard.getType() == GuardType.BOSS3)
            {
                guard.setID(136);
            }
            else if (guard.getType() == GuardType.BOSS4)
            {
                guard.setID(227);
            }
            else if (guard.getType() == GuardType.BOSS5)
            {
                guard.setID(235);
            }
            else if (guard.getType() == GuardType.BOSS6)
            {
                guard.setID(243);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY)
            {
                guard.setID(262);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
            {
                guard.setID(270);
            }
            else if (guard.getType() == GuardType.WORM)
            {
                guard.setID(278);
            }
            else if (guard.getType() == GuardType.LARVA)
            {
                guard.setID(286);
            }
            else if (guard.getType() == GuardType.ALIEN)
            {
                guard.setID(243);
            }
            else if (guard.getType() == GuardType.WOMAN1)
            {
                guard.setID(311);
            }
            else if (guard.getType() == GuardType.WOMAN2)
            {
                guard.setID(319);
            }
            else if (guard.getType() == GuardType.WOMAN3)
            {
                guard.setID(327);
            }
            else if (guard.getType() == GuardType.ISLAND_GUY)
            {
                guard.setID(343);
            }
            else if (guard.getType() == GuardType.CHIEF)
            {
                guard.setID(335);
            }
            else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
            {
                guard.setID(351);
            }

        }

        if (guard.animationIterations <= 0)
        {
            if (guard.getID() == 20)
            {
                guard.setID(24);
            }
            else if (guard.getID() == 28)
            {
                guard.setID(32);
            }
            else if (guard.getID() == 36)
            {
                guard.setID(40);
            }
            else if (guard.getID() == 163)
            {
                guard.setID(167);
            }
            else if (guard.getID() == 171)
            {
                guard.setID(175);
            }
            else if (guard.getID() == 120)
            {
                guard.setID(124);
            }
            else if (guard.getID() == 128)
            {
                guard.setID(132);
            }
            else if (guard.getID() == 136)
            {
                guard.setID(140);
            }
            else if (guard.getID() == 227)
            {
                guard.setID(231);
            }
            else if (guard.getID() == 235)
            {
                guard.setID(239);
            }
            else if (guard.getID() == 243)
            {
                guard.setID(247);
            }
            else if (guard.getID() == 262)
            {
                guard.setID(266);
            }
            else if (guard.getID() == 270)
            {
                guard.setID(274);
            }
            else if (guard.getID() == 278)
            {
                guard.setID(282);
            }
            else if (guard.getID() == 286)
            {
                guard.setID(290);
            }
            else if (guard.getID() == 311)
            {
                guard.setID(315);
            }
            else if (guard.getID() == 319)
            {
                guard.setID(323);
            }
            else if (guard.getID() == 327)
            {
                guard.setID(331);
            }
            else if (guard.getID() == 343)
            {
                guard.setID(347);
            }
            else if (guard.getID() == 335)
            {
                guard.setID(339);
            }
            else if (guard.getID() == 351)
            {
                guard.setID(355);
            }
            else
            {
                if (guard.getType() == GuardType.LIGHT)
                {
                    guard.setID(20);
                }
                else if (guard.getType() == GuardType.MEDIUM)
                {
                    guard.setID(28);
                }
                else if (guard.getType() == GuardType.HEAVY)
                {
                    guard.setID(36);
                }
                else if (guard.getType() == GuardType.SCIENTIST1)
                {
                    guard.setID(163);
                }
                else if (guard.getType() == GuardType.SCIENTIST2)
                {
                    guard.setID(171);
                }
                else if (guard.getType() == GuardType.BOSS1)
                {
                    guard.setID(120);
                }
                else if (guard.getType() == GuardType.BOSS2)
                {
                    guard.setID(128);
                }
                else if (guard.getType() == GuardType.BOSS3)
                {
                    guard.setID(136);
                }
                else if (guard.getType() == GuardType.BOSS4)
                {
                    guard.setID(227);
                }
                else if (guard.getType() == GuardType.BOSS5)
                {
                    guard.setID(235);
                }
                else if (guard.getType() == GuardType.BOSS6)
                {
                    guard.setID(243);
                }
                else if (guard.getType() == GuardType.FEMALE_ALLY)
                {
                    guard.setID(262);
                }
                else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
                {
                    guard.setID(270);
                }
                else if (guard.getType() == GuardType.WORM)
                {
                    guard.setID(278);
                }
                else if (guard.getType() == GuardType.LARVA)
                {
                    guard.setID(286);
                }
                else if (guard.getType() == GuardType.ALIEN)
                {
                    guard.setID(243);
                }
                else if (guard.getType() == GuardType.WOMAN1)
                {
                    guard.setID(311);
                }
                else if (guard.getType() == GuardType.WOMAN2)
                {
                    guard.setID(319);
                }
                else if (guard.getType() == GuardType.WOMAN3)
                {
                    guard.setID(327);
                }
                else if (guard.getType() == GuardType.ISLAND_GUY)
                {
                    guard.setID(343);
                }
                else if (guard.getType() == GuardType.CHIEF)
                {
                    guard.setID(335);
                }
                else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
                {
                    guard.setID(351);
                }
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
            guard.move(0, 1 * guard.movementDelta);
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

            if (guard.getType() == GuardType.LIGHT)
            {
                guard.setID(21);
            }
            else if (guard.getType() == GuardType.MEDIUM)
            {
                guard.setID(29);
            }
            else if (guard.getType() == GuardType.HEAVY)
            {
                guard.setID(37);
            }
            else if (guard.getType() == GuardType.SCIENTIST1)
            {
                guard.setID(164);
            }
            else if (guard.getType() == GuardType.SCIENTIST2)
            {
                guard.setID(172);
            }
            else if (guard.getType() == GuardType.BOSS1)
            {
                guard.setID(121);
            }
            else if (guard.getType() == GuardType.BOSS2)
            {
                guard.setID(129);
            }
            else if (guard.getType() == GuardType.BOSS3)
            {
                guard.setID(137);
            }
            else if (guard.getType() == GuardType.BOSS4)
            {
                guard.setID(228);
            }
            else if (guard.getType() == GuardType.BOSS5)
            {
                guard.setID(236);
            }
            else if (guard.getType() == GuardType.BOSS6)
            {
                guard.setID(244);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY)
            {
                guard.setID(263);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
            {
                guard.setID(271);
            }
            else if (guard.getType() == GuardType.WORM)
            {
                guard.setID(279);
            }
            else if (guard.getType() == GuardType.LARVA)
            {
                guard.setID(287);
            }
            else if (guard.getType() == GuardType.ALIEN)
            {
                guard.setID(244);
            }
            else if (guard.getType() == GuardType.WOMAN1)
            {
                guard.setID(312);
            }
            else if (guard.getType() == GuardType.WOMAN2)
            {
                guard.setID(320);
            }
            else if (guard.getType() == GuardType.WOMAN3)
            {
                guard.setID(328);
            }
            else if (guard.getType() == GuardType.ISLAND_GUY)
            {
                guard.setID(344);
            }
            else if (guard.getType() == GuardType.CHIEF)
            {
                guard.setID(336);
            }
            else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
            {
                guard.setID(352);
            }

        }

        if (guard.animationIterations <= 0)
        {
            if (guard.getID() == 21)
            {
                guard.setID(25);
            }
            else if (guard.getID() == 29)
            {
                guard.setID(33);
            }
            else if (guard.getID() == 37)
            {
                guard.setID(41);
            }
            else if (guard.getID() == 164)
            {
                guard.setID(168);
            }
            else if (guard.getID() == 172)
            {
                guard.setID(176);
            }
            else if (guard.getID() == 121)
            {
                guard.setID(125);
            }
            else if (guard.getID() == 129)
            {
                guard.setID(133);
            }
            else if (guard.getID() == 137)
            {
                guard.setID(141);
            }
            else if (guard.getID() == 228)
            {
                guard.setID(232);
            }
            else if (guard.getID() == 236)
            {
                guard.setID(240);
            }
            else if (guard.getID() == 244)
            {
                guard.setID(248);
            }
            else if (guard.getID() == 263)
            {
                guard.setID(267);
            }
            else if (guard.getID() == 271)
            {
                guard.setID(275);
            }
            else if (guard.getID() == 279)
            {
                guard.setID(283);
            }
            else if (guard.getID() == 287)
            {
                guard.setID(291);
            }
            else if (guard.getID() == 312)
            {
                guard.setID(316);
            }
            else if (guard.getID() == 320)
            {
                guard.setID(324);
            }
            else if (guard.getID() == 328)
            {
                guard.setID(332);
            }
            else if (guard.getID() == 344)
            {
                guard.setID(348);
            }
            else if (guard.getID() == 336)
            {
                guard.setID(340);
            }
            else if (guard.getID() == 352)
            {
                guard.setID(356);
            }
            else
            {
                if (guard.getType() == GuardType.LIGHT)
                {
                    guard.setID(21);
                }
                else if (guard.getType() == GuardType.MEDIUM)
                {
                    guard.setID(29);
                }
                else if (guard.getType() == GuardType.HEAVY)
                {
                    guard.setID(37);
                }
                else if (guard.getType() == GuardType.SCIENTIST1)
                {
                    guard.setID(164);
                }
                else if (guard.getType() == GuardType.SCIENTIST2)
                {
                    guard.setID(172);
                }
                else if (guard.getType() == GuardType.BOSS1)
                {
                    guard.setID(121);
                }
                else if (guard.getType() == GuardType.BOSS2)
                {
                    guard.setID(129);
                }
                else if (guard.getType() == GuardType.BOSS3)
                {
                    guard.setID(137);
                }
                else if (guard.getType() == GuardType.BOSS4)
                {
                    guard.setID(228);
                }
                else if (guard.getType() == GuardType.BOSS5)
                {
                    guard.setID(236);
                }
                else if (guard.getType() == GuardType.BOSS6)
                {
                    guard.setID(244);
                }
                else if (guard.getType() == GuardType.FEMALE_ALLY)
                {
                    guard.setID(263);
                }
                else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
                {
                    guard.setID(271);
                }
                else if (guard.getType() == GuardType.WORM)
                {
                    guard.setID(279);
                }
                else if (guard.getType() == GuardType.LARVA)
                {
                    guard.setID(287);
                }
                else if (guard.getType() == GuardType.ALIEN)
                {
                    guard.setID(244);
                }
                else if (guard.getType() == GuardType.WOMAN1)
                {
                    guard.setID(312);
                }
                else if (guard.getType() == GuardType.WOMAN2)
                {
                    guard.setID(320);
                }
                else if (guard.getType() == GuardType.WOMAN3)
                {
                    guard.setID(328);
                }
                else if (guard.getType() == GuardType.ISLAND_GUY)
                {
                    guard.setID(344);
                }
                else if (guard.getType() == GuardType.CHIEF)
                {
                    guard.setID(336);
                }
                else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
                {
                    guard.setID(352);
                }
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
            guard.move(-1 * guard.movementDelta, 0);
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

            if (guard.getType() == GuardType.LIGHT)
            {
                guard.setID(22);
            }
            else if (guard.getType() == GuardType.MEDIUM)
            {
                guard.setID(30);
            }
            else if (guard.getType() == GuardType.HEAVY)
            {
                guard.setID(38);
            }
            else if (guard.getType() == GuardType.SCIENTIST1)
            {
                guard.setID(165);
            }
            else if (guard.getType() == GuardType.SCIENTIST2)
            {
                guard.setID(173);
            }
            else if (guard.getType() == GuardType.BOSS1)
            {
                guard.setID(122);
            }
            else if (guard.getType() == GuardType.BOSS2)
            {
                guard.setID(130);
            }
            else if (guard.getType() == GuardType.BOSS3)
            {
                guard.setID(138);
            }
            else if (guard.getType() == GuardType.BOSS4)
            {
                guard.setID(229);
            }
            else if (guard.getType() == GuardType.BOSS5)
            {
                guard.setID(237);
            }
            else if (guard.getType() == GuardType.BOSS6)
            {
                guard.setID(245);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY)
            {
                guard.setID(264);
            }
            else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
            {
                guard.setID(272);
            }
            else if (guard.getType() == GuardType.WORM)
            {
                guard.setID(280);
            }
            else if (guard.getType() == GuardType.LARVA)
            {
                guard.setID(288);
            }
            else if (guard.getType() == GuardType.ALIEN)
            {
                guard.setID(245);
            }
            else if (guard.getType() == GuardType.WOMAN1)
            {
                guard.setID(313);
            }
            else if (guard.getType() == GuardType.WOMAN2)
            {
                guard.setID(321);
            }
            else if (guard.getType() == GuardType.WOMAN3)
            {
                guard.setID(329);
            }
            else if (guard.getType() == GuardType.ISLAND_GUY)
            {
                guard.setID(345);
            }
            else if (guard.getType() == GuardType.CHIEF)
            {
                guard.setID(337);
            }
            else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
            {
                guard.setID(353);
            }

        }

        if (guard.animationIterations <= 0)
        {
            if (guard.getID() == 22)
            {
                guard.setID(26);
            }
            else if (guard.getID() == 30)
            {
                guard.setID(34);
            }
            else if (guard.getID() == 38)
            {
                guard.setID(42);
            }
            else if (guard.getID() == 165)
            {
                guard.setID(169);
            }
            else if (guard.getID() == 173)
            {
                guard.setID(177);
            }
            else if (guard.getID() == 122)
            {
                guard.setID(126);
            }
            else if (guard.getID() == 130)
            {
                guard.setID(134);
            }
            else if (guard.getID() == 138)
            {
                guard.setID(142);
            }
            else if (guard.getID() == 229)
            {
                guard.setID(233);
            }
            else if (guard.getID() == 237)
            {
                guard.setID(241);
            }
            else if (guard.getID() == 245)
            {
                guard.setID(249);
            }
            else if (guard.getID() == 264)
            {
                guard.setID(268);
            }
            else if (guard.getID() == 272)
            {
                guard.setID(276);
            }
            else if (guard.getID() == 280)
            {
                guard.setID(284);
            }
            else if (guard.getID() == 288)
            {
                guard.setID(292);
            }
            else if (guard.getID() == 313)
            {
                guard.setID(317);
            }
            else if (guard.getID() == 321)
            {
                guard.setID(325);
            }
            else if (guard.getID() == 329)
            {
                guard.setID(333);
            }
            else if (guard.getID() == 345)
            {
                guard.setID(349);
            }
            else if (guard.getID() == 337)
            {
                guard.setID(341);
            }
            else if (guard.getID() == 353)
            {
                guard.setID(357);
            }
            else
            {
                if (guard.getType() == GuardType.LIGHT)
                {
                    guard.setID(22);
                }
                else if (guard.getType() == GuardType.MEDIUM)
                {
                    guard.setID(30);
                }
                else if (guard.getType() == GuardType.HEAVY)
                {
                    guard.setID(38);
                }
                else if (guard.getType() == GuardType.SCIENTIST1)
                {
                    guard.setID(165);
                }
                else if (guard.getType() == GuardType.SCIENTIST2)
                {
                    guard.setID(173);
                }
                else if (guard.getType() == GuardType.BOSS1)
                {
                    guard.setID(122);
                }
                else if (guard.getType() == GuardType.BOSS2)
                {
                    guard.setID(130);
                }
                else if (guard.getType() == GuardType.BOSS3)
                {
                    guard.setID(138);
                }
                else if (guard.getType() == GuardType.BOSS4)
                {
                    guard.setID(229);
                }
                else if (guard.getType() == GuardType.BOSS5)
                {
                    guard.setID(237);
                }
                else if (guard.getType() == GuardType.BOSS6)
                {
                    guard.setID(245);
                }
                else if (guard.getType() == GuardType.FEMALE_ALLY)
                {
                    guard.setID(264);
                }
                else if (guard.getType() == GuardType.FEMALE_ALLY_PRISONER)
                {
                    guard.setID(272);
                }
                else if (guard.getType() == GuardType.WORM)
                {
                    guard.setID(280);
                }
                else if (guard.getType() == GuardType.LARVA)
                {
                    guard.setID(288);
                }
                else if (guard.getType() == GuardType.ALIEN)
                {
                    guard.setID(245);
                }
                else if (guard.getType() == GuardType.WOMAN1)
                {
                    guard.setID(313);
                }
                else if (guard.getType() == GuardType.WOMAN2)
                {
                    guard.setID(321);
                }
                else if (guard.getType() == GuardType.WOMAN3)
                {
                    guard.setID(329);
                }
                else if (guard.getType() == GuardType.ISLAND_GUY)
                {
                    guard.setID(345);
                }
                else if (guard.getType() == GuardType.CHIEF)
                {
                    guard.setID(337);
                }
                else if ((guard.getType() == GuardType.SPECIAL) || (guard.getType() == GuardType.SPECIAL_ENEMY))
                {
                    guard.setID(353);
                }
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
            guard.move(1 * guard.movementDelta, 0);
        }

        game.checkWaterAndGrass(guard);
        game.checkDoors(guard, 1);
        guard.checkDoorStatus();
    }

    private boolean canShootPlayer(NPC guard)
    {
        return !((guard.isFriendly()) ||
                (guard.getType() == GuardType.WORM) || (guard.getType() == GuardType.LARVA));
    }

    /*
    private boolean isFriend(NPC guard)
    {
        return ((guard.getType() == GuardType.FEMALE_ALLY) || (guard.getType() == GuardType.FEMALE_ALLY_PRISONER));
    }
     *
     */

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

    /*
    private void generateAdjacencies(NPC guard)
    {
        for(int j = 0; j < guard.obstacleMatrix.length; j++)
        {
            for(int i = 0; i < guard.obstacleMatrix[0].length; i++)
            {
                Edge left = null;
                Edge right = null;
                Edge up = null;
                Edge down = null;

                ArrayList<Edge> edges = new ArrayList<Edge>(4);

                if (i-1 > -1)
                {
                    if (guard.obstacleMatrix[j][i-1] == 1)
                    {
                        left = new Edge(guard.vertices[j][i-1], guard.obstacleMatrix[j][i-1]);
                        edges.add(left);
                    }
                }

                if (i+1 < guard.obstacleMatrix[0].length)
                {
                    if (guard.obstacleMatrix[j][i+1] == 1)
                    {
                        right = new Edge(guard.vertices[j][i+1], guard.obstacleMatrix[j][i+1]);
                        edges.add(right);
                    }
                }

                if (j-1 > -1)
                {
                    if (guard.obstacleMatrix[j-1][i] == 1)
                    {
                        up = new Edge(guard.vertices[j-1][i], guard.obstacleMatrix[j-1][i]);
                        edges.add(up);
                    }
                }

                if (j+1 < guard.obstacleMatrix.length)
                {
                    if (guard.obstacleMatrix[j+1][i] == 1)
                    {
                        down = new Edge(guard.vertices[j+1][i], guard.obstacleMatrix[j+1][i]);
                        edges.add(down);
                    }
                }

                Edge[] adjs = new Edge[edges.size()];

                for(int z = 0; z < edges.size(); z++)
                {
                    adjs[z] = edges.get(z);
                }

                guard.vertices[j][i].adjacencies = adjs;
            }
        }
    }
     */

//    private void computePaths(Vertex source)
//    {
//        source.minDistance = 0;
//        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
//        vertexQueue.add(source);
//
//        while(!vertexQueue.isEmpty())
//        {
//            Vertex u = vertexQueue.poll();
//
//            for(Edge e : u.adjacencies)
//            {
//                Vertex v = e.target;
//                int weight = e.weight;
//                int distanceThroughU = u.minDistance + weight;
//                if (distanceThroughU < v.minDistance)
//                {
//                    vertexQueue.remove(v);
//                    v.minDistance = distanceThroughU;
//                    v.previous = u;
//                    vertexQueue.add(v);
//                }
//            }
//        }
//    }

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
