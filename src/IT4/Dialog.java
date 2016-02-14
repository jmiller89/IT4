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

public class Dialog
{
    public ArrayList<ArrayList<String>> dialog;
    public String title = "";
    //private int currentText = 0;
    //public int currentPage = 0;
    private static final short maxLineLength = 68;
    private static final short maxLines = 35;
    private boolean active = true;
    private boolean substance = false;
    public boolean confirmation = false;
    public boolean choice = false;
    public ITEvent event = null;
    private Item item = null;
    public String choiceString = "";

    public Dialog()
    {
        dialog = new ArrayList<ArrayList<String>>();
        dialog.add(new ArrayList<String>());        
    }

    public Dialog(Item it, ITEvent ev)
    {
        dialog = new ArrayList<ArrayList<String>>();
        dialog.add(new ArrayList<String>());
        confirmation = true;
        item = it;
        event = ev;

        String text = "Found " + it.toString(null) + "\n\n";
        text += it.description;

        String[] contents = text.split("\n");
        for(int i = 0; i < contents.length; i++)
        {
            add(contents[i]);
        }

        if (confirmation)
        {
            choiceString = "Confirm [F] | Cancel [Space]";
        }
    }

    public Dialog(Dialog d, String t)
    {        
        title = t;
        dialog = new ArrayList<ArrayList<String>>();

        for(int j = 0; j < d.dialog.size(); j++)
        {
            dialog.add(new ArrayList<String>());
            for(int i = 0; i < d.dialog.get(j).size(); i++)
            {
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
                //dialog.add(strs[i]);
                if (dialog.get(dialog.size() - 1).size() >= maxLines)
                {
                    dialog.add(new ArrayList<String>());
                }

                dialog.get(dialog.size() - 1).add(strs[i]);
            }

            strs = null;       
        }
        else
        {
            if (dialog.get(dialog.size() - 1).size() >= maxLines)
            {
                dialog.add(new ArrayList<String>());
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
