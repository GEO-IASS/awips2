package com.raytheon.uf.viz.collaboration.ui.session;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.common.eventbus.Subscribe;
import com.raytheon.uf.common.status.IUFStatusHandler;
import com.raytheon.uf.common.status.UFStatus;
import com.raytheon.uf.common.status.UFStatus.Priority;
import com.raytheon.uf.viz.collaboration.comm.identity.CollaborationException;
import com.raytheon.uf.viz.collaboration.comm.identity.IMessage;
import com.raytheon.uf.viz.collaboration.comm.identity.IPresence;
import com.raytheon.uf.viz.collaboration.comm.identity.IPresence.Mode;
import com.raytheon.uf.viz.collaboration.comm.identity.IPresence.Type;
import com.raytheon.uf.viz.collaboration.comm.identity.IVenueSession;
import com.raytheon.uf.viz.collaboration.comm.identity.event.IVenueParticipantEvent;
import com.raytheon.uf.viz.collaboration.comm.identity.event.ParticipantEventType;
import com.raytheon.uf.viz.collaboration.comm.identity.info.IVenueInfo;
import com.raytheon.uf.viz.collaboration.comm.identity.user.IVenueParticipant;
import com.raytheon.uf.viz.collaboration.comm.identity.user.ParticipantRole;
import com.raytheon.uf.viz.collaboration.data.CollaborationDataManager;
import com.raytheon.uf.viz.collaboration.data.CollaborationKeywords;
import com.raytheon.uf.viz.collaboration.data.CollaborationUser;
import com.raytheon.uf.viz.core.VizApp;

/**
 * TODO Add Description
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * 
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Mar 1, 2012            rferrel     Initial creation
 * 
 * </pre>
 * 
 * @author rferrel
 * @version 1.0
 */
public class SessionView extends AbstractSessionView {
    private static final transient IUFStatusHandler statusHandler = UFStatus
            .getHandler(SessionView.class);

    private static final String SESSION_IMAGE_NAME = "chats.gif";

    public static final String ID = "com.raytheon.uf.viz.collaboration.SessionView";

    private TableViewer usersTable;

    protected String sessionId;

    private Image downArrow;

    private Image rightArrow;

    private Image highlightedRightArrow;

    private Image highlightedDownArrow;

    protected Action chatAction;

    public SessionView() {
        super();
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        createActions();
        createContextMenu();
    }

    @Override
    protected void populateSashForm(SashForm sashForm) {
        createArrows();
        createUsersComp(sashForm);
        super.populateSashForm(sashForm);
        sashForm.setWeights(new int[] { 1, 20, 5 });
    }

    protected void createActions() {
        chatAction = new Action("Chat") {
            @Override
            public void run() {
                try {
                    CollaborationDataManager dataManager = CollaborationDataManager
                            .getInstance();
                    CollaborationUser user = (CollaborationUser) ((IStructuredSelection) usersTable
                            .getSelection()).getFirstElement();
                    String session = dataManager.createCollaborationSession(
                            user.getId(), "Chatting...");
                    PlatformUI
                            .getWorkbench()
                            .getActiveWorkbenchWindow()
                            .getActivePage()
                            .showView(CollaborationSessionView.ID, session,
                                    IWorkbenchPage.VIEW_ACTIVATE);
                } catch (PartInitException e) {
                    statusHandler.handle(Priority.PROBLEM,
                            "Unable to open chat", e);
                }
            }
        };
    }

    /**
     * 
     */
    private void createContextMenu() {
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse
             * .jface.action.IMenuManager)
             */
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        Menu menu = menuManager.createContextMenu(usersTable.getControl());
        usersTable.getControl().setMenu(menu);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActivePart().getSite()
                .registerContextMenu(menuManager, usersTable);
        usersTable.getTable().setMenu(menu);
    }

    protected void fillContextMenu(IMenuManager manager) {
        IStructuredSelection selection = (IStructuredSelection) usersTable
                .getSelection();
        // do something here!
        Object ob = selection.getFirstElement();
        System.out.println(ob.toString());
        // super.fillContextMenu(manager);
        // manager.add(chatAction);
        // manager.add(new Separator());
    }

    @Subscribe
    public void handleMessage(IMessage message) {
        final IMessage msg = message;
        VizApp.runAsync(new Runnable() {

            @Override
            public void run() {
                addMessage(msg);
            }
        });
    }

    /**
     * Ties the view to a session.
     * 
     * @param sessionId
     */
    @Override
    protected void createListeners() {
        super.createListeners();
        IVenueSession session = CollaborationDataManager.getInstance()
                .getSession(sessionId);
        if (session != null) {
            session.registerEventHandler(this);
        }
    }

    protected void createUsersComp(final Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        comp.setLayout(layout);
        comp.setLayoutData(data);

        final CLabel participantsLabel = new CLabel(comp, SWT.NONE);
        layout = new GridLayout(1, false);
        data = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
        participantsLabel.setLayout(layout);
        participantsLabel.setLayoutData(data);
        participantsLabel.setText("Participants");
        participantsLabel.setImage(rightArrow);
        participantsLabel.setToolTipText("Select to show participants...");

        final Composite usersComp = new Composite(comp, SWT.NONE);
        layout = new GridLayout(1, false);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        usersComp.setVisible(false);
        layout.marginWidth = 0;
        usersComp.setLayout(layout);
        usersComp.setLayoutData(data);

        participantsLabel.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                if (usersComp.getVisible()) {
                    participantsLabel.setImage(highlightedDownArrow);
                } else {
                    participantsLabel.setImage(highlightedRightArrow);
                }
            }

            @Override
            public void mouseExit(MouseEvent e) {
                if (usersComp.getVisible()) {
                    participantsLabel.setImage(downArrow);
                } else {
                    participantsLabel.setImage(rightArrow);
                }
            }
        });
        participantsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                GridData data = ((GridData) usersComp.getLayoutData());
                data.exclude = !data.exclude;
                usersComp.setVisible(!data.exclude);

                usersComp.layout();
                int[] weights = ((SashForm) parent).getWeights();
                if (!usersComp.getVisible()) {
                    int val = weights[0] + weights[1] + weights[2];
                    val = (int) Math.ceil(((double) val / 26.0));
                    weights[1] = weights[0] + weights[1] - 1;
                    weights[0] = val;
                    participantsLabel.setImage(rightArrow);
                    participantsLabel
                            .setToolTipText("Select to show participants...");
                } else {
                    // fix this to make up for possible negative values TODO XXX
                    int val = usersComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y
                            + participantsLabel.getBounds().height;
                    double percentage = ((double) val)
                            / (double) parent.getSize().y;
                    // not greater than 50% of view when popping out
                    if (percentage > 0.5) {
                        percentage = 0.5;
                    }
                    int weight = weights[0] + weights[1] + weights[2];
                    double tmp = weight * percentage;
                    weights[1] = (int) (weights[1] - tmp);
                    weights[0] = (int) (weights[0] + tmp);
                    participantsLabel.setImage(downArrow);
                    participantsLabel
                            .setToolTipText("Select to hide participants...");
                }
                ((SashForm) parent).setWeights(weights);
                parent.layout();
            }
        });

        usersTable = new TableViewer(usersComp, SWT.BORDER | SWT.SINGLE
                | SWT.V_SCROLL | SWT.H_SCROLL);
        layout = new GridLayout(1, false);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        usersTable.getTable().setLayout(layout);
        usersTable.getTable().setLayoutData(data);

        ParticipantsContentProvider contentProvider = new ParticipantsContentProvider();
        ParticipantsLabelProvider labelProvider = new ParticipantsLabelProvider();
        labelProvider.setSessionId(sessionId);
        usersTable.setContentProvider(contentProvider);

        usersTable.setLabelProvider(labelProvider);
        usersTable.setSorter(new ViewerSorter() {
            public int compare(Viewer viewer, Object e1, Object e2) {
                CollaborationUser c1 = (CollaborationUser) e1;
                CollaborationUser c2 = (CollaborationUser) e1;
                return c1.compareTo(c2);
            }
        });

        usersTable.getTable().addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseHover(MouseEvent e) {
                TableItem item = usersTable.getTable().getItem(
                        new Point(e.x, e.y));
                if (item != null) {
                    CollaborationUser user = (CollaborationUser) item.getData();
                    StringBuilder builder = new StringBuilder();
                    builder.append("mode: ").append(user.getMode())
                            .append("\n");
                    builder.append("type: ").append(user.getType())
                            .append("\n");
                    builder.append("-- Roles --");
                    for (ParticipantRole type : user.getRoles()) {
                        // TODO fake XXX take this out
                        // if (type == ParticipantRole.UNKNOWN) {
                        // continue;
                        // }
                        builder.append("\n" + type.toString());
                    }
                    usersTable.getTable().setToolTipText(builder.toString());
                } else {
                    usersTable.getTable().setToolTipText("");
                }
            }
        });

        IVenueSession session = CollaborationDataManager.getInstance()
                .getSession(sessionId);
        List<CollaborationUser> users = new ArrayList<CollaborationUser>();
        if (session != null) {
            for (IVenueParticipant participant : session.getVenue()
                    .getParticipants()) {

                CollaborationUser user = new CollaborationUser(
                        participant.getFQName());
                user.setMode(Mode.AVAILABLE);
                user.setType(Type.AVAILABLE);

                // ParticipantRole[] roles = user.getRoles(sessionId);
                // for (ParticipantRole role : roles) {
                // user.addRole(role);
                // }
                user.addRole(ParticipantRole.DATA_PROVIDER);
                user.addRole(ParticipantRole.SESSION_LEADER);
                user.setText(participant.getFQName());
                // user.setMode(mode);
                // user.setType(Type.AVAILABLE);
                users.add(user);
            }
        } else {
            participantsLabel.setEnabled(false);
            participantsLabel.setForeground(Display.getCurrent()
                    .getSystemColor(SWT.COLOR_DARK_GRAY));
            comp.setEnabled(false);
        }
        usersTable.setInput(users);
        ((GridData) usersComp.getLayoutData()).exclude = true;
    }

    @Override
    public void dispose() {
        // dispose of the images first
        disposeArrow(highlightedDownArrow);
        disposeArrow(highlightedRightArrow);
        disposeArrow(downArrow);
        disposeArrow(rightArrow);
        super.dispose();
    }

    private void disposeArrow(Image image) {
        if (image != null && !image.isDisposed()) {
            image.dispose();
        }
    }

    // @SuppressWarnings("unchecked")
    // public void addUser(CollaborationUser user) {
    // List<CollaborationUser> list = (List<CollaborationUser>) usersTable
    // .getInput();
    // list.add(user);
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void clearUsers() {
    // List<CollaborationUser> list = (List<CollaborationUser>) usersTable
    // .getInput();
    // list.clear();
    // ;
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void removeUser(CollaborationUser user) {
    // List<CollaborationUser> list = (List<CollaborationUser>) usersTable
    // .getInput();
    // list.remove(user);
    // }

    public void addMessage(IMessage message) {
        String name = message.getFrom().getFQName();
        String user = message.getFrom().getName();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(message.getTimeStamp());
        String time = String.format("%1$tI:%1$tM:%1$tS %1$Tp", cal);
        StringBuilder sb = new StringBuilder();
        if (messagesText.getCharCount() != 0) {
            sb.append("\n");
        }
        int offset = 0;
        sb.append("(").append(time).append(") ");
        offset = sb.length();

        sb.append(user).append(": ").append(message.getBody());

        // here is the place to put the font and color changes for keywords
        // read in localization file once and then don't read in again, per
        // chat room?
        List<String> keywords = CollaborationKeywords.parseKeywords();
        List<StyleRange> ranges = new ArrayList<StyleRange>();
        if (keywords != null) {
            for (String keyword : keywords) {
                if (sb.toString().toLowerCase().contains(keyword.toLowerCase())) {
                    StyleRange keywordRange = new StyleRange(
                            messagesText.getCharCount()
                                    + sb.toString().toLowerCase()
                                            .indexOf(keyword.toLowerCase()),
                            keyword.length(), null, null, SWT.BOLD | SWT.ITALIC);
                    ranges.add(keywordRange);
                }
            }
        }

        ParticipantRole[] type = null;
        for (CollaborationUser u : (List<CollaborationUser>) usersTable
                .getInput()) {
            if (name.equals(u.getId())) {
                type = u.getRoles();
                break;
            }
        }

        Color color = SessionColorAdvisor.getColor(type, name
                .equals(CollaborationDataManager.getInstance().getLoginId()));
        StyleRange range = new StyleRange(messagesText.getCharCount() + offset,
                user.length() + 1, color, null, SWT.BOLD);
        messagesText.append(sb.toString());
        messagesText.setStyleRange(range);
        for (StyleRange newRange : ranges) {
            messagesText.setStyleRange(newRange);
        }
        messagesText.setTopIndex(messagesText.getLineCount() - 1);

        // room for other fun things here, such as sounds and such
        executeSightsSounds();
    }

    /**
     * 
     */
    private void executeSightsSounds() {
        // TODO Auto-generated method stub
        // placeholder for future things
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.raytheon.uf.viz.collaboration.ui.session.AbstractSessionView#sendMessage
     * ()
     */
    public void sendMessage() {
        String message = getComposedMessage();
        if (message.length() > 0) {
            // CollaborationDataManager.getInstance().getSession(sessionId)
            // .sendTextMessage(message);

            try {
                CollaborationDataManager.getInstance().getSession(sessionId)
                        .sendTextMessage(message);
            } catch (CollaborationException e) {
                // TODO Auto-generated catch block. Please revise as
                // appropriate.
                statusHandler.handle(Priority.PROBLEM, e.getLocalizedMessage(),
                        e);
            }
        }
    }

    public String getRoom() {
        return sessionId;
    }

    protected String getSessionImageName() {
        return SESSION_IMAGE_NAME;
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.raytheon.uf.viz.collaboration.ui.session.AbstractSessionView#partClosed
     * (org.eclipse.ui.IWorkbenchPart)
     */
    public void partClosed(IWorkbenchPart part) {
        super.partClosed(part);
        if (this == part) {
            CollaborationDataManager.getInstance().getSession(sessionId)
                    .unRegisterEventHandler(this);
            CollaborationDataManager.getInstance().closeSession(sessionId);
        }
    }

    private void createArrows() {
        int imgWidth = 11;
        int imgHeight = 11;

        rightArrow = new Image(Display.getCurrent(), imgWidth, imgHeight);
        downArrow = new Image(Display.getCurrent(), imgWidth, imgHeight);
        highlightedRightArrow = new Image(Display.getCurrent(), imgWidth,
                imgHeight);
        highlightedDownArrow = new Image(Display.getCurrent(), imgWidth,
                imgHeight);

        // the right arrow
        GC gc = new GC(rightArrow);
        drawArrowImage(gc, imgWidth, imgHeight, false, false);

        // the down arrow
        gc = new GC(downArrow);
        drawArrowImage(gc, imgWidth, imgHeight, true, false);

        // the down arrow
        gc = new GC(highlightedRightArrow);
        drawArrowImage(gc, imgWidth, imgHeight, false, true);

        // the down arrow
        gc = new GC(highlightedDownArrow);
        drawArrowImage(gc, imgWidth, imgHeight, true, true);

        gc.dispose();
    }

    private void drawArrowImage(GC gc, int imgWidth, int imgHeight,
            boolean down, boolean fill) {
        gc.setAntialias(SWT.ON);
        // "Erase" the canvas by filling it in with a rectangle.
        gc.setBackground(Display.getCurrent().getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND));
        gc.fillRectangle(0, 0, imgWidth, imgHeight);
        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        int[] polyArray = null;
        if (down) {
            polyArray = new int[] { 2, 3, 5, 6, 8, 3 };
        } else {
            polyArray = new int[] { 3, 2, 6, 5, 3, 8 };
        }
        if (fill) {
            gc.fillPolygon(polyArray);
        } else {
            gc.drawPolygon(polyArray);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.raytheon.uf.viz.collaboration.ui.session.AbstractSessionView#
     * setMessageLabel(org.eclipse.swt.widgets.Label)
     */
    @Override
    protected void setMessageLabel(Label label) {
        StringBuilder labelInfo = new StringBuilder();
        labelInfo.append("Private Chat: ");
        IVenueSession session = CollaborationDataManager.getInstance()
                .getSession(sessionId);
        if (session != null) {
            IVenueInfo info = session.getVenue().getInfo();
            labelInfo.append(info.getVenueSubject());
            label.setToolTipText(info.getVenueSubject());
        }
        label.setText(labelInfo.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.raytheon.uf.viz.collaboration.ui.session.AbstractSessionView#
     * getSessionName()
     */
    @Override
    protected String getSessionName() {
        sessionId = getViewSite().getSecondaryId();
        IVenueSession session = CollaborationDataManager.getInstance()
                .getSession(sessionId);
        if (session == null) {
            return sessionId;
        }
        return session.getVenue().getInfo().getVenueDescription();
    }

    @Subscribe
    public void participantHandler(IVenueParticipantEvent event)
            throws Exception {
        System.out.println("++ ParticipantHander type " + event.getEventType());
        final ParticipantEventType type = event.getEventType();
        final IVenueParticipant participant = event.getParticipant();
        final IPresence presence = event.getPresence();
        VizApp.runAsync(new Runnable() {

            @Override
            public void run() {
                switch (type) {
                case ARRIVED:
                    participantArrived(participant);
                    break;
                case DEPARTED:
                    participantDeparted(participant);
                    break;
                case PRESENCE_UPDATED:
                    participantPresenceUpdated(participant, presence);
                    break;
                case UPDATED:
                    System.out.println("---- handle update here: "
                            + participant.getName() + ", "
                            + participant.getFQName());
                    break;
                default:
                    System.err.println("Unknown Event type");
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void participantArrived(IVenueParticipant participant) {
        List<CollaborationUser> users = (List<CollaborationUser>) usersTable
                .getInput();
        String name = participant.getFQName();
        for (CollaborationUser user : users) {
            if (name.equals(user.getId())) {
                return;
            }
        }
        CollaborationUser user = new CollaborationUser(name);
        user.setText(name);
        users.add(user);
        usersTable.refresh();
    }

    @SuppressWarnings("unchecked")
    private void participantDeparted(IVenueParticipant participant) {
        System.out.println("++++ handle departed here: "
                + participant.getName() + ", " + participant.getFQName());
        List<CollaborationUser> users = (List<CollaborationUser>) usersTable
                .getInput();
        String name = participant.getFQName();
        for (int i = 0; i < users.size(); ++i) {
            if (name.equals(users.get(i).getId())) {
                users.remove(i);
                usersTable.refresh();
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void participantPresenceUpdated(IVenueParticipant participant,
            IPresence presence) {
        List<CollaborationUser> users = (List<CollaborationUser>) usersTable
                .getInput();
        System.out.println("++++ handle presence updated here: "
                + presence.getMode() + "/" + presence.getType() + ": "
                + participant.getName() + ", " + participant.getFQName());
        String name = participant.getFQName();
        for (CollaborationUser user : users) {
            if (name.equals(user.getId())) {
                user.setMode(presence.getMode());
                user.setType(presence.getType());
                usersTable.refresh();
                return;
            }
        }

        CollaborationUser user = new CollaborationUser(name);
        user.setMode(presence.getMode());
        user.setType(presence.getType());
        // usersTable.refresh();
    }
}
