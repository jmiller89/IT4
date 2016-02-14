//This is a container class for all LevelMaps

package IT4;

/**
 *
 * @author Jim (Admin)
 */

import java.util.ArrayList;

public class Level
{
    private LevelMap[] levelMaps;
    private int levelID;
    
    private int startX = 1;
    private int startY = 1;

    private ArrayList<ArrayList<Objective>> objectives;
    private ArrayList<Objective> allObjectives;

    private ArrayList<ArrayList<Objective>> objectiveCopies;
    private ArrayList<Objective> allObjectiveCopies;

    public boolean stripItems = false;

    private Game game;

    public ArrayList<Dialog> dialogStore;

    public Level(int size, int id, Game gm)
    {
        levelMaps = new LevelMap[size];
        levelID = id;

        //For the levelmap
        objectives = new ArrayList<ArrayList<Objective>>();
        objectiveCopies = new ArrayList<ArrayList<Objective>>();
        //For the player to view
        allObjectives = new ArrayList<Objective>();
        allObjectiveCopies = new ArrayList<Objective>();
        game = gm;

        dialogStore = new ArrayList<Dialog>();
    }

    public void initializeObjectives()
    {
        for(int i = 0; i < levelMaps.length; i++)
        {
            objectives.add(new ArrayList<Objective>());

            if (levelMaps[i].isBossFight())
            {
                allObjectives.add(new Objective((short)0, -1, -1, i, "Kill " + levelMaps[i].getBoss().name));
            }
        }

        int escapeIndex = -1;
        
        //Add the objective to its appropriate sector
        for(int i = 0; i < allObjectives.size(); i++)
        {
            if (allObjectives.get(i).name.toLowerCase().startsWith("escape"))
            {
                escapeIndex = i;
            }

            Objective obj = allObjectives.get(i);

            Objective copy = new Objective(obj);

            objectives.get(obj.mapIndex).add(copy);
        }

        //Make sure objectives named "escape" appear last
        if (escapeIndex >= 0)
        {
            Objective ob = allObjectives.remove(escapeIndex);
            allObjectives.add(ob);
        }

        this.saveObjectives();
    }

    public void addObjective(Objective o)
    {
        allObjectives.add(new Objective(o));
    }

    public ArrayList<Objective> getObjectives(int index)
    {
        if (objectives.size() > index)
        {
            return objectives.get(index);
        }
        else
        {
            return null;
        }
    }

    public ArrayList<Objective> getAllObjectives()
    {
        return allObjectives;
    }

    public ArrayList<Objective> getAllObjectiveCopies()
    {
        return allObjectiveCopies;
    }

    public void removeObjective(Objective obj, int index)
    {
        objectives.get(index).remove(obj);
        allObjectives.remove(obj);
    }

    private void saveObjectives()
    {
        this.objectiveCopies.clear();
        this.allObjectiveCopies.clear();

        for(int i = 0; i < levelMaps.length; i++)
        {
            objectiveCopies.add(new ArrayList<Objective>());
        }

        for(int i = 0; i < allObjectives.size(); i++)
        {
            Objective copy = new Objective(allObjectives.get(i));
            allObjectiveCopies.add(copy);

            Objective copy2 = new Objective(copy);

            objectiveCopies.get(copy2.mapIndex).add(copy2);

        }

    }

    private void respawnObjectives()
    {
        this.objectives.clear();
        this.allObjectives.clear();

        for(int i = 0; i < levelMaps.length; i++)
        {
            objectives.add(new ArrayList<Objective>());
        }

        for(int i = 0; i < allObjectiveCopies.size(); i++)
        {
            Objective copy = new Objective(allObjectiveCopies.get(i));
            allObjectives.add(copy);

            Objective copy2 = new Objective(copy);

            objectives.get(copy2.mapIndex).add(copy2);

        }
    }

    public void setStartX(int x)
    {
        startX = x;
    }

    public void setStartY(int y)
    {
        startY = y;
    }

    public int getStartX()
    {
        return startX;
    }

    public int getStartY()
    {
        return startY;
    }

    public int getID()
    {
        return levelID;
    }

    public void setID(int id)
    {
        if (id >= 0)
        {
            levelID = id;
        }
    }

    public void setLevelMap(LevelMap l, int loc)
    {
        levelMaps[loc] = l;

        if (l.getDialog() != null)
        {
            dialogStore.add(l.getDialog());
        }

        System.out.println("Lv map set");
    }

    public LevelMap getLevelMap(int loc)
    {
        return levelMaps[loc];
    }

    public int getNumLevels()
    {
        return levelMaps.length;
    }

    private void saveItems()
    {
        for(int i = 0; i < levelMaps.length; i++)
        {
            levelMaps[i].saveItems();
        }
    }

    private void respawnItems()
    {
        for(int i = 0; i < levelMaps.length; i++)
        {
            levelMaps[i].respawnItems();
        }
    }

    private void respawnBosses()
    {
        for(int i = 0; i < levelMaps.length; i++)
        {
            levelMaps[i].respawnBoss();
        }
    }

    public void saveState()
    {
        saveItems();
        saveObjectives();
    }

    public void respawnState()
    {
        respawnItems();
        respawnObjectives();
        respawnBosses();
    }

    private void validateObjectives()
    {
        allObjectives.clear();
        for(int i = 0; i < allObjectiveCopies.size(); i++)
        {
            if (GameFileManager.objectives.contains(allObjectiveCopies.get(i)))
            {
                allObjectives.add(this.allObjectiveCopies.get(i));
            }
        }

        objectives.clear();
        initializeObjectives();
    }

    public void load()
    {
        this.validateObjectives();
        
        //GameFileManager.bosses, GameFileManager.dialogsCompleted, GameFileManager.items, GameFileManager.objectives
        for(int i = 0; i < levelMaps.length; i++)
        {
            if (levelMaps[i].getBoss() != null)
            {
                if (GameFileManager.bosses[i] == 0)
                {
                    this.removeObjective(new Objective((short)0, -1, -1, i, "Kill " + levelMaps[i].getBoss().name), i);
                    levelMaps[i].noBoss();
                }
            }

            levelMaps[i].initializeItems(GameFileManager.items.get(i));
        }

        for(int i = 0; i < GameFileManager.dialogsCompleted.size(); i++)
        {            
            levelMaps[GameFileManager.dialogsCompleted.get(i)].getDialog().fastForward();     
        }
    }
}
