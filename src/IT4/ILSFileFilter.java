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
public class ILSFileFilter extends javax.swing.filechooser.FileFilter
{

    @Override
    public boolean accept(File f)
    {
        return f.getName().toLowerCase().endsWith(".it4ls");
    }

    @Override
    public String getDescription()
    {
        return "The Endling's Artifice Levelset [.it4ls]";
    }

}
