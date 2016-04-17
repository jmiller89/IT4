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

public class DialogSection
{
    public ArrayList<String> text = new ArrayList<String>();
    public String speaker = "Dialog";

    public DialogSection copy()
    {
        DialogSection d = new DialogSection();

        d.speaker = this.speaker;

        for(int i = 0; i < text.size(); i++)
        {
            d.text.add(this.text.get(i));
        }

        return d;
    }

    public void clear()
    {
        this.text.clear();
        this.speaker = "Dialog";
    }

    public void add(String s)
    {
        text.add(s);
    }

    public void finishSection(int maxLines)
    {
        int delta = maxLines - text.size();
        if (delta > 0)
        {
            for(int i = 0; i < delta; i++)
            {
                text.add("");
            }
        }
    }

    public int size()
    {
        return text.size();
    }

    public String get(int i)
    {
        if ((i >= 0) && (i < text.size()))
        {
            return text.get(i);
        }
        return null;
    }

    public ArrayList<DialogSection> split(int maxLines)
    {
        ArrayList<DialogSection> subsections = new ArrayList<DialogSection>((this.size() / maxLines) + 1);

        DialogSection d = new DialogSection();
        d.speaker = this.speaker;
        int j = 0;
        for(int i = 0; i < this.text.size(); i++)
        {
            if (j >= maxLines)
            {
                subsections.add(d.copy());
                d.clear();
                d.speaker = this.speaker;
                j = 0;
            }
            
            d.add(text.get(i));
            j++;
        }

        if (d.size() > 0)
        {
            if (d.size() == 1)
            {
                if (d.get(0).length() > 0)
                {
                    d.finishSection(maxLines);
                    subsections.add(d.copy());
                }
            }
            else
            {
                d.finishSection(maxLines);
                subsections.add(d.copy());
            }
        }

        return subsections;
    }

    @Override
    public String toString()
    {
        String output = "[" + speaker + "]\n";
        for(int i = 0; i < text.size(); i++)
        {
            output += text.get(i) + "\n";
        }
        return output;
    }
}
