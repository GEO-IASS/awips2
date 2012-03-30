/**
 * This software was developed and / or modified by Raytheon Company,
 * pursuant to Contract DG133W-05-CQ-1067 with the US Government.
 * 
 * U.S. EXPORT CONTROLLED TECHNICAL DATA
 * This software product contains export-restricted data whose
 * export/transfer/disclosure is restricted by U.S. law. Dissemination
 * to non-U.S. persons whether in the United States or abroad requires
 * an export license or other authorization.
 * 
 * Contractor Name:        Raytheon Company
 * Contractor Address:     6825 Pine Street, Suite 340
 *                         Mail Stop B8
 *                         Omaha, NE 68106
 *                         402.291.0100
 * 
 * See the AWIPS II Master Rights File ("Master Rights File.pdf") for
 * further licensing information.
 **/
package com.raytheon.viz.core.gl.images;

import org.eclipse.swt.graphics.RGB;

import com.raytheon.uf.viz.core.drawables.ext.ISingleColorImageExtension.ISingleColorImage;
import com.raytheon.viz.core.gl.internal.ext.GLSingleColorImageExtension;

/**
 * GL Image object that all non-zero values should be mapped to a single color
 * value
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * 
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Dec 15, 2011            mschenke     Initial creation
 * 
 * </pre>
 * 
 * @author mschenke
 * @version 1.0
 */

public class GLSingleColorImage extends GLDelegateImage<AbstractGLImage>
        implements ISingleColorImage {

    private RGB color;

    public GLSingleColorImage(AbstractGLImage image, RGB color) {
        super(image, GLSingleColorImageExtension.class);
        this.color = color;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.raytheon.uf.viz.core.drawables.ext.ISingleColorImageExtension
     * .ISingleColorImage#setColor(org.eclipse.swt.graphics.RGB)
     */
    @Override
    public void setColor(RGB color) {
        this.color = color;
    }

    public RGB getColor() {
        return color;
    }

}