/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
//import java.util.Arrays;

public class Dialog
{
    public ArrayList<DialogSection> dialog;
    public String title = "";
    //private int currentText = 0;
    //public int currentPage = 0;
    public static final short maxLineLength = 56;
    public static final short maxLines_top = 35;
    public static final short maxLines_bottom = 10;
    private boolean active = true;
    private boolean substance = false;
    public boolean confirmation = false;
    public boolean choice = false;
    public ITEvent event = null;
    private Item item = null;
    public String choiceString = "";
    public boolean onTop = false;
    public static Pattern speakerPattern = Pattern.compile("\\[(.*)\\]");

    public Dialog(boolean showOnTop)
    {
        onTop = showOnTop;
        //dialog = new ArrayList<ArrayList<String>>();
        //dialog.add(new ArrayList<String>());
        dialog = new ArrayList<DialogSection>();
        dialog.add(new DialogSection(onTop));
    }

    public Dialog(Item it, ITEvent ev)
    {
        //dialog = new ArrayList<ArrayList<String>>();
        //dialog.add(new ArrayList<String>());
        dialog = new ArrayList<DialogSection>();
        dialog.add(new DialogSection(onTop));

        confirmation = true;
        item = it;
        event = ev;
        onTop = true;

        String text = "Found " + it.weaponName + "\n\n";
        text += it.description;

        String[] contents = text.split("\n");
        this.addStrings(contents);

        /*
        for(int i = 0; i < contents.length; i++)
        {
            add(contents[i]);
        }
        */

        if (confirmation)
        {
            choiceString = "Confirm [F] | Cancel [Space]";
        }
    }

    public Dialog(Dialog d, String t, boolean showOnTop)
    {        
        title = t;
        //dialog = new ArrayList<ArrayList<String>>();
        dialog = new ArrayList<DialogSection>();
        onTop = showOnTop;

        for(int j = 0; j < d.dialog.size(); j++)
        {
            //dialog.add(new ArrayList<String>());
            dialog.add(new DialogSection(onTop));
            for(int i = 0; i < d.dialog.get(j).size(); i++)
            {
                dialog.get(j).speaker = d.dialog.get(j).speaker;
                dialog.get(j).add(new String(d.dialog.get(j).get(i)));
            }
        }

        if (d.isValid())
        {
            substance = d.substance;
        }
        else
        {
            active = false;
        }
    }

    /*
    public boolean hasNext()
    {
        return (currentText < dialog.size());
    }

    public String getNext()
    {
        return dialog.get(currentText++);
    }
    */
    
    public final void add(String s)
    {
        short maxLines = maxLines_bottom;
        if (onTop)
        {
            maxLines = maxLines_top;
        }
        if (s.length() > 0)
        {
            substance = true;
        }        

        if (s.length() > maxLineLength)
        {
            String[] strs = new String[(s.length() / maxLineLength) + 1];
            int start = 0;
            int end = maxLineLength;
            for(int i = 0; i < strs.length; i++)
            {
                strs[i] = s.substring(start, end);

                if (end != s.length())
                {
                    strs[i] += "-";
                }

                start += maxLineLength;
                end += maxLineLength;

                if (end >= s.length())
                {
                    end = s.length();
                }                
            }

            for(int i = 0; i < strs.length; i++)
            {
                if (dialog.get(dialog.size() - 1).size() >= maxLines)
                {
                    //dialog.add(new ArrayList<String>());
                    dialog.add(new DialogSection(onTop));
                }

                dialog.get(dialog.size() - 1).add(strs[i]);
            }

            strs = null;       
        }
        else
        {
            if (dialog.get(dialog.size() - 1).size() >= maxLines)
            {
                //dialog.add(new ArrayList<String>());
                dialog.add(new DialogSection(onTop));
            }
            
            dialog.get(dialog.size() - 1).add(s);
        }
    }

    public void fastForward()
    {
        //currentText = dialog.size();
        if (choice)
        {
            if (event != null)
            {
                event.function(item);
            }
        }
        active = false;        
    }

    public boolean isValid()
    {
        //return (dialog.size() > 0);
        return active & substance;
    }

    public void makeDialog(ArrayList<String> prepStrs)
    {
        short maxLines = maxLines_bottom;
        if (onTop)
        {
            maxLines = maxLines_top;
        }

        DialogSection ds = new DialogSection(onTop);
        //ds.speaker = "Dialog";
        this.dialog.clear();
        for(int i = 0; i < prepStrs.size(); i++)
        {
            Matcher m = speakerPattern.matcher(prepStrs.get(i));
            if (m.matches())
            {
                String speaker = m.group(1);

                if (speaker.equalsIgnoreCase("Player"))
                {
                    speaker = Game.playerName;
                }

                if (ds.speaker.equals(speaker) == false)
                {
                    if (ds.size() > 0)
                    {
                        //Split if necessary
                        if (ds.size() > maxLines)
                        {
                            ArrayList<DialogSection> subsections = ds.split(maxLines);
                            for(int j = 0; j < subsections.size(); j++)
                            {
                                this.dialog.add(subsections.get(j));
                            }
                        }
                        else
                        {
                            ds.finishSection(maxLines);
                            this.dialog.add(ds.copy());
                        }
                        
                        ds.clear();
                    }
                }

                ds.speaker = speaker;
            }
            else
            {
                //Add to existing dialogSection
                if (prepStrs.get(i).length() > 0)
                {
                    substance = true;
                }
                ds.add(prepStrs.get(i));
            }
            
        }

        if (ds.size() > 0)
        {
            ds.finishSection(maxLines);
            this.dialog.add(ds.copy());
        }

        //System.err.println("Dialogs: " + dialog.size() + " prepStrs len=" + prepStrs.size());
        //for(int i = 0; i < dialog.size(); i++)
        //{
        //    System.err.println(dialog.get(i).toString());
        //}
    }

    public void addStrings(ArrayList<String> strs)
    {
        ArrayList<String> prepStrs = new ArrayList<String>();

        for(int i = 0; i < strs.size(); i++)
        {
            String[] linesplit = strs.get(i).split(" ");
            int start = 0;

            while (start <= linesplit.length - 1)
            {
                int totalChars = 0;
                int j;
                for(j = start; j < linesplit.length; j++)
                {
                    int charcount = linesplit[j].length() + 1;

                    if (totalChars + charcount > maxLineLength)
                    {
                        break;
                    }
                    else
                    {
                        totalChars += charcount;
                    }
                }

                if (j == 0)
                {
                    prepStrs.add(strs.get(i));
                    break;
                }

                String prepStr = "";
                for(int k = start; k < j; k++)
                {
                    prepStr += linesplit[k];
                    if (k != (j - 1))
                    {
                        prepStr += " ";
                    }
                }

                prepStrs.add(prepStr);

                start = j;
            }
        }

        this.makeDialog(prepStrs);

        /*
        for(int i = 0; i < prepStrs.size(); i++)
        {
            this.add(prepStrs.get(i));
        }
         */
    }

    public void addStrings(String[] strs)
    {
        ArrayList<String> prepStrs = new ArrayList<String>();

        for(int i = 0; i < strs.length; i++)
        {
            String[] linesplit = strs[i].split(" ");
            int start = 0;

            while (start <= linesplit.length - 1)
            {
                int totalChars = 0;
                int j;
                for(j = start; j < linesplit.length; j++)
                {
                    int charcount = linesplit[j].length() + 1;

                    if (totalChars + charcount > maxLineLength)
                    {
                        break;
                    }
                    else
                    {
                        totalChars += charcount;
                    }
                }

                if (j == 0)
                {
                    prepStrs.add(strs[i]);
                    break;
                }

                String prepStr = "";
                for(int k = start; k < j; k++)
                {
                    prepStr += linesplit[k];
                    if (k != (j - 1))
                    {
                        prepStr += " ";
                    }
                }

                prepStrs.add(prepStr);

                start = j;
            }
        }

        for(int i = 0; i < prepStrs.size(); i++)
        {
            this.add(prepStrs.get(i));
        }
    }

    @Override
    public String toString()
    {
        String dlg = "";

        for(int i = 0; i < dialog.size(); i++)
        {
            for(int j = 0; j < dialog.get(i).size(); j++)
            {
                dlg += dialog.get(i).get(j) + "\n";
            }
        }

        return dlg;
    }    
}
