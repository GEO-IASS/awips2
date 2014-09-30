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
package com.raytheon.uf.viz.monitor.snow.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.raytheon.uf.common.monitor.config.FSSObsMonitorConfigurationManager;
import com.raytheon.uf.common.monitor.config.FSSObsMonitorConfigurationManager.MonName;
import com.raytheon.uf.common.monitor.data.CommonConfig;
import com.raytheon.uf.common.monitor.data.CommonConfig.AppName;
import com.raytheon.uf.common.monitor.data.ObConst.DataUsageKey;
import com.raytheon.uf.viz.monitor.snow.SnowMonitor;
import com.raytheon.uf.viz.monitor.snow.threshold.SnowThresholdMgr;
import com.raytheon.uf.viz.monitor.ui.dialogs.MonitoringAreaConfigDlg;

/**
 * SNOW Monitor area configuration dialog.
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Jan  5, 2010            mpduff      Initial creation
 * Nov 27, 2012 1351       skorolev    Changes for non-blocking dialog.
 * Jan 29, 2014 2757       skorolev    Changed OK button handler.
 * Apr 23, 2014 3054       skorolev    Fixed issue with removing a new station from list.
 * Apr 28, 2014 3086       skorolev    Updated snowConfigManager.
 * Sep 15, 2014 2757       skorolev    Removed extra dialog.
 * 
 * </pre>
 * 
 * @author mpduff
 * @version 1.0
 */

public class SnowMonitoringAreaConfigDlg extends MonitoringAreaConfigDlg {

    /** Configuration manager for SNOW monitor. */
    private FSSObsMonitorConfigurationManager snowConfigMgr;

    /**
     * Constructor
     * 
     * @param parent
     * @param title
     */
    public SnowMonitoringAreaConfigDlg(Shell parent, String title) {
        super(parent, title, AppName.SNOW);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.raytheon.uf.viz.monitor.ui.dialogs.MonitoringAreaConfigDlg#
     * handleOkBtnSelection()
     */
    @Override
    protected void handleOkBtnSelection() {
        snowConfigMgr = getInstance();
        // Check for changes in the data\
        if (dataIsChanged()) {
            int choice = showMessage(shell, SWT.OK | SWT.CANCEL,
                    "SNOW Monitor Confirm Changes",
                    "Want to update the SNOW setup files?");
            if (choice == SWT.OK) {
                // Save the config xml file
                getValues();
                resetStatus();
                snowConfigMgr.saveConfigXml();
                /**
                 * DR#11279: re-initialize threshold manager and the monitor
                 * using new monitor area configuration
                 */
                SnowThresholdMgr.reInitialize();
                SnowMonitor.reInitialize();

                if ((!snowConfigMgr.getAddedZones().isEmpty())
                        || (!snowConfigMgr.getAddedStations().isEmpty())) {
                    if (editDialog() == SWT.YES) {
                        SnowMonDispThreshDlg snowMonitorDlg = new SnowMonDispThreshDlg(
                                shell, CommonConfig.AppName.SNOW,
                                DataUsageKey.MONITOR);
                        snowMonitorDlg.open();
                    }
                    snowConfigMgr.getAddedZones().clear();
                    snowConfigMgr.getAddedStations().clear();
                }
            }
            snowConfigMgr = null;
        } 
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.raytheon.uf.viz.monitor.ui.dialogs.MonitoringAreaConfigDlg#getInstance
     * ()
     */
    @Override
    protected FSSObsMonitorConfigurationManager getInstance() {
        if (configMgr == null) {
            configMgr = new FSSObsMonitorConfigurationManager(currentSite,
                    MonName.snow.name());
        }
        return (FSSObsMonitorConfigurationManager) configMgr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.raytheon.uf.viz.monitor.ui.dialogs.MonitoringAreaConfigDlg#disposed()
     */
    @Override
    protected void disposed() {
        configMgr = null;
    }
}
