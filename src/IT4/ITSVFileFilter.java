/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

import java.io.File;

/**
 *
 * @author Jim
 */
public class ITSVFileFilter extends javax.swing.filechooser.FileFilter
{
    @Override
    public boolean accept(File f)
    {
        return f.getName().toLowerCase().endsWith(".it4save");
    }

    @Override
    public String getDescription()
    {
        return "The Endling's Artifice Save Game Data [.it4save]";
    }
}
