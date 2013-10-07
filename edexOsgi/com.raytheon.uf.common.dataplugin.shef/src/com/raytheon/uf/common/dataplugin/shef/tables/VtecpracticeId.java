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
package com.raytheon.uf.common.dataplugin.shef.tables;
// default package
// Generated Oct 17, 2008 2:22:17 PM by Hibernate Tools 3.2.2.GA

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * VtecpracticeId generated by hbm2java
 * 
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Oct 17, 2008                        Initial generation by hbm2java
 * Aug 19, 2011      10672     jkorman Move refactor to new project
 * Oct 07, 2013       2361     njensen Removed XML annotations
 * 
 * </pre>
 * 
 * @author jkorman
 * @version 1.1
 */
@Embeddable
@com.raytheon.uf.common.serialization.annotations.DynamicSerialize
public class VtecpracticeId extends com.raytheon.uf.common.dataplugin.persist.PersistableDataObject implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private String geoid;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private String productId;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private Date producttime;

    public VtecpracticeId() {
    }

    public VtecpracticeId(String geoid, String productId, Date producttime) {
        this.geoid = geoid;
        this.productId = productId;
        this.producttime = producttime;
    }

    @Column(name = "geoid", nullable = false, length = 24)
    public String getGeoid() {
        return this.geoid;
    }

    public void setGeoid(String geoid) {
        this.geoid = geoid;
    }

    @Column(name = "product_id", nullable = false, length = 10)
    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Column(name = "producttime", nullable = false, length = 29)
    public Date getProducttime() {
        return this.producttime;
    }

    public void setProducttime(Date producttime) {
        this.producttime = producttime;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof VtecpracticeId))
            return false;
        VtecpracticeId castOther = (VtecpracticeId) other;

        return ((this.getGeoid() == castOther.getGeoid()) || (this.getGeoid() != null
                && castOther.getGeoid() != null && this.getGeoid().equals(
                castOther.getGeoid())))
                && ((this.getProductId() == castOther.getProductId()) || (this
                        .getProductId() != null
                        && castOther.getProductId() != null && this
                        .getProductId().equals(castOther.getProductId())))
                && ((this.getProducttime() == castOther.getProducttime()) || (this
                        .getProducttime() != null
                        && castOther.getProducttime() != null && this
                        .getProducttime().equals(castOther.getProducttime())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result
                + (getGeoid() == null ? 0 : this.getGeoid().hashCode());
        result = 37 * result
                + (getProductId() == null ? 0 : this.getProductId().hashCode());
        result = 37
                * result
                + (getProducttime() == null ? 0 : this.getProducttime()
                        .hashCode());
        return result;
    }

}
