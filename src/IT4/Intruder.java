//This is the launch point for Intruder's Thunder 4

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class Intruder
{
    public static boolean debug = false;
    private static final String DB_FLAG = "-d";
    
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            if (args[0].startsWith(DB_FLAG))
            {
                debug = true;
                System.out.println("DEBUG MODE");
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Cleanup()));

        //Create a new Instance of the game
        MainMenuFrame mmf = new MainMenuFrame();

    }
}
