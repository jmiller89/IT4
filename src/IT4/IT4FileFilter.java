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
public class IT4FileFilter extends javax.swing.filechooser.FileFilter
{

    @Override
    public boolean accept(File f) 
    {
        return f.getName().toLowerCase().endsWith(".it4");
    }

    @Override
    public String getDescription()
    {
        return "IT4 Level Data [.it4]";
    }

}
