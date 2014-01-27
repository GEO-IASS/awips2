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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Locpdc generated by hbm2java
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
@Entity
@Table(name = "locpdc")
@com.raytheon.uf.common.serialization.annotations.DynamicSerialize
public class Locpdc extends com.raytheon.uf.common.dataplugin.persist.PersistableDataObject implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private LocpdcId id;

    public Locpdc() {
    }

    public Locpdc(LocpdcId id) {
        this.id = id;
    }

    @EmbeddedId
    @AttributeOverrides( {
            @AttributeOverride(name = "lid", column = @Column(name = "lid", length = 8)),
            @AttributeOverride(name = "name", column = @Column(name = "name", length = 50)),
            @AttributeOverride(name = "lat", column = @Column(name = "lat", precision = 17, scale = 17)),
            @AttributeOverride(name = "lon", column = @Column(name = "lon", precision = 17, scale = 17)),
            @AttributeOverride(name = "hsa", column = @Column(name = "hsa", length = 3)),
            @AttributeOverride(name = "post", column = @Column(name = "post")),
            @AttributeOverride(name = "elev", column = @Column(name = "elev", precision = 17, scale = 17)),
            @AttributeOverride(name = "primaryPe", column = @Column(name = "primary_pe", length = 2)),
            @AttributeOverride(name = "fs", column = @Column(name = "fs", precision = 17, scale = 17)),
            @AttributeOverride(name = "fq", column = @Column(name = "fq", precision = 17, scale = 17)),
            @AttributeOverride(name = "dispClass", column = @Column(name = "disp_class", length = 10)),
            @AttributeOverride(name = "isDcp", column = @Column(name = "is_dcp", length = 1)),
            @AttributeOverride(name = "isObserver", column = @Column(name = "is_observer", length = 1)),
            @AttributeOverride(name = "telemType", column = @Column(name = "telem_type", length = 10)) })
    public LocpdcId getId() {
        return this.id;
    }

    public void setId(LocpdcId id) {
        this.id = id;
    }

}
