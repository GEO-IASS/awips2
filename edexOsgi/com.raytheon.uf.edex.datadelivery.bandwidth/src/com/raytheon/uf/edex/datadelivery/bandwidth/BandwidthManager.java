package com.raytheon.uf.edex.datadelivery.bandwidth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.raytheon.edex.util.Util;
import com.raytheon.uf.common.auth.exception.AuthorizationException;
import com.raytheon.uf.common.auth.user.IUser;
import com.raytheon.uf.common.datadelivery.bandwidth.IBandwidthRequest;
import com.raytheon.uf.common.datadelivery.bandwidth.IBandwidthRequest.RequestType;
import com.raytheon.uf.common.datadelivery.bandwidth.IProposeScheduleResponse;
import com.raytheon.uf.common.datadelivery.bandwidth.ProposeScheduleResponse;
import com.raytheon.uf.common.datadelivery.bandwidth.data.BandwidthGraphData;
import com.raytheon.uf.common.datadelivery.registry.AdhocSubscription;
import com.raytheon.uf.common.datadelivery.registry.DataType;
import com.raytheon.uf.common.datadelivery.registry.Network;
import com.raytheon.uf.common.datadelivery.registry.PointTime;
import com.raytheon.uf.common.datadelivery.registry.SiteSubscription;
import com.raytheon.uf.common.datadelivery.registry.Subscription;
import com.raytheon.uf.common.datadelivery.registry.Time;
import com.raytheon.uf.common.datadelivery.registry.handlers.DataDeliveryHandlers;
import com.raytheon.uf.common.registry.handler.RegistryHandlerException;
import com.raytheon.uf.common.serialization.SerializationException;
import com.raytheon.uf.common.status.IPerformanceStatusHandler;
import com.raytheon.uf.common.status.IUFStatusHandler;
import com.raytheon.uf.common.status.PerformanceStatus;
import com.raytheon.uf.common.status.UFStatus;
import com.raytheon.uf.common.status.UFStatus.Priority;
import com.raytheon.uf.common.time.util.IPerformanceTimer;
import com.raytheon.uf.common.time.util.ITimer;
import com.raytheon.uf.common.time.util.TimeUtil;
import com.raytheon.uf.common.util.CollectionUtil;
import com.raytheon.uf.common.util.LogUtil;
import com.raytheon.uf.common.util.algorithm.AlgorithmUtil;
import com.raytheon.uf.common.util.algorithm.AlgorithmUtil.IBinarySearchResponse;
import com.raytheon.uf.edex.auth.req.AbstractPrivilegedRequestHandler;
import com.raytheon.uf.edex.auth.resp.AuthorizationResponse;
import com.raytheon.uf.edex.core.EDEXUtil;
import com.raytheon.uf.edex.datadelivery.bandwidth.dao.BandwidthAllocation;
import com.raytheon.uf.edex.datadelivery.bandwidth.dao.BandwidthBucket;
import com.raytheon.uf.edex.datadelivery.bandwidth.dao.BandwidthDataSetUpdate;
import com.raytheon.uf.edex.datadelivery.bandwidth.dao.BandwidthSubscription;
import com.raytheon.uf.edex.datadelivery.bandwidth.dao.IBandwidthDao;
import com.raytheon.uf.edex.datadelivery.bandwidth.dao.IBandwidthDbInit;
import com.raytheon.uf.edex.datadelivery.bandwidth.dao.SubscriptionRetrieval;
import com.raytheon.uf.edex.datadelivery.bandwidth.dao.SubscriptionRetrievalAttributes;
import com.raytheon.uf.edex.datadelivery.bandwidth.interfaces.BandwidthInitializer;
import com.raytheon.uf.edex.datadelivery.bandwidth.interfaces.ISubscriptionAggregator;
import com.raytheon.uf.edex.datadelivery.bandwidth.processing.BandwidthSubscriptionContainer;
import com.raytheon.uf.edex.datadelivery.bandwidth.processing.SimpleSubscriptionAggregator;
import com.raytheon.uf.edex.datadelivery.bandwidth.retrieval.BandwidthMap;
import com.raytheon.uf.edex.datadelivery.bandwidth.retrieval.BandwidthRoute;
import com.raytheon.uf.edex.datadelivery.bandwidth.retrieval.RetrievalManager;
import com.raytheon.uf.edex.datadelivery.bandwidth.retrieval.RetrievalPlan;
import com.raytheon.uf.edex.datadelivery.bandwidth.retrieval.RetrievalStatus;
import com.raytheon.uf.edex.datadelivery.bandwidth.util.BandwidthDaoUtil;
import com.raytheon.uf.edex.datadelivery.bandwidth.util.BandwidthUtil;
import com.raytheon.uf.edex.registry.ebxml.exception.EbxmlRegistryException;

/**
 * Abstract {@link IBandwidthManager} implementation which provides core
 * functionality. Intentionally package-private to hide implementation details.
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Feb 07, 2012            dhladky      Initial creation
 * Aug 11, 2012 726        jspinks      Modifications for implementing bandwidth management.
 * Oct 10, 2012 0726       djohnson     Changes to support new subscription/adhoc retrievals, use enabled flag,
 *                                      add support for non-cyclic subscriptions.
 * Oct 23, 2012 1286       djohnson     Add more service requests.
 * Nov 20, 2012 1286       djohnson     Add proposing scheduling subscriptions, change some logging to debug.
 * Dec 06, 2012 1397       djohnson     Add ability to get bandwidth graph data.
 * Dec 11, 2012 1403       djohnson     Adhoc subscriptions no longer go to the registry.
 * Dec 12, 2012 1286       djohnson     Remove shutdown hook and finalize().
 * Jan 25, 2013 1528       djohnson     Compare priorities as primitive ints.
 * Jan 28, 2013 1530       djohnson     Unschedule all allocations for a subscription that does not fully schedule.
 * Jan 30, 2013 1501       djohnson     Fix broken calculations for determining required latency.
 * Feb 05, 2013 1580       mpduff       EventBus refactor.
 * Feb 14, 2013 1595       djohnson     Check with BandwidthUtil whether or not to reschedule subscriptions on update.
 * Feb 14, 2013 1596       djohnson     Do not reschedule allocations when a subscription is removed.
 * Feb 20, 2013 1543       djohnson     Add try/catch blocks during the shutdown process.
 * Feb 27, 2013 1644       djohnson     Force sub-classes to provide an implementation for how to schedule SBN routed subscriptions.
 * Mar 11, 2013 1645       djohnson     Watch configuration file for changes.
 * Mar 28, 2013 1841       djohnson     Subscription is now UserSubscription.
 * May 02, 2013 1910       djohnson     Shutdown proposed bandwidth managers in a finally.
 * May 20, 2013 1650       djohnson     Add in capability to find required dataset size.
 * Jun 03, 2013 2038       djohnson     Add base functionality to handle point data type subscriptions.
 * Jun 13, 2013 2095       djohnson     Improve bandwidth manager speed, and add performance logging.
 * Jun 18, 2013 2120       dhladky      Add times to pointtime array
 * Jun 20, 2013 1802       djohnson     Check several times for the metadata for now.
 * Jun 24, 2013 2106       djohnson     Access BandwidthBucket contents through RetrievalPlan.
 * Jul 10, 2013 2106       djohnson     Move EDEX instance specific code into its own class.
 * Jul 11, 2013 2106       djohnson     Propose changing available bandwidth returns subscription names.
 * Jul 18, 2013 1653       mpduff       Added case GET_SUBSCRIPTION_STATUS.
 * </pre>
 * 
 * @author dhladky
 * @version 1.0
 */
public abstract class BandwidthManager extends
        AbstractPrivilegedRequestHandler<IBandwidthRequest> implements
        IBandwidthManager {

    protected static final IUFStatusHandler statusHandler = UFStatus
            .getHandler(BandwidthManager.class);

    // Requires package access so it can be accessed from the maintenance task
    final IBandwidthDao bandwidthDao;

    private ISubscriptionAggregator aggregator;

    private BandwidthInitializer initializer;

    protected final BandwidthDaoUtil bandwidthDaoUtil;

    private final IBandwidthDbInit dbInit;

    // Instance variable and not static, because there are multiple child
    // implementation classes which should each have a unique prefix
    private final IPerformanceStatusHandler performanceHandler = PerformanceStatus
            .getHandler(this.getClass().getSimpleName());

    @VisibleForTesting
    final RetrievalManager retrievalManager;

    public BandwidthManager(IBandwidthDbInit dbInit,
            IBandwidthDao bandwidthDao, RetrievalManager retrievalManager,
            BandwidthDaoUtil bandwidthDaoUtil) {
        this.dbInit = dbInit;
        this.bandwidthDao = bandwidthDao;
        this.retrievalManager = retrievalManager;
        this.bandwidthDaoUtil = bandwidthDaoUtil;
    }

    private List<BandwidthAllocation> schedule(Subscription subscription,
            SortedSet<Integer> cycles) {
        SortedSet<Calendar> retrievalTimes = bandwidthDaoUtil
                .getRetrievalTimes(subscription, cycles);

        return scheduleSubscriptionForRetrievalTimes(subscription,
                retrievalTimes);
    }

    /**
     * Schedules a subscription that specifies a retrieval interval, rather than
     * cycle times.
     * 
     * @param subscription
     *            the subscription
     * @param retrievalInterval
     *            the retrieval interval
     * @return the list of unscheduled subscriptions
     */
    private List<BandwidthAllocation> schedule(Subscription subscription,
            int retrievalInterval) {
        SortedSet<Calendar> retrievalTimes = bandwidthDaoUtil
                .getRetrievalTimes(subscription, retrievalInterval);

        return scheduleSubscriptionForRetrievalTimes(subscription,
                retrievalTimes);
    }

    /**
     * Schedule the given subscription for the specified retrieval times.
     * 
     * @param subscription
     *            the subscription
     * @param retrievalTimes
     *            the retrieval times
     * @return the unscheduled subscriptions
     */
    private List<BandwidthAllocation> scheduleSubscriptionForRetrievalTimes(
            Subscription subscription, SortedSet<Calendar> retrievalTimes) {
        IPerformanceTimer timer = TimeUtil.getPerformanceTimer();
        timer.start();

        if (retrievalTimes.isEmpty()) {
            return Collections.emptyList();
        }

        List<BandwidthAllocation> unscheduled = Lists.newArrayList();

        final int numberOfRetrievalTimes = retrievalTimes.size();
        List<BandwidthSubscription> newSubscriptions = Lists
                .newArrayListWithCapacity(numberOfRetrievalTimes);

        for (Calendar retrievalTime : retrievalTimes) {
            statusHandler.info("schedule() - Scheduling subscription ["
                    + subscription.getName()
                    + String.format(
                            "] baseReferenceTime [%1$tY%1$tm%1$td%1$tH%1$tM",
                            retrievalTime) + "]");

            // Add the current subscription to the ones BandwidthManager already
            // knows about.
            newSubscriptions.add(BandwidthUtil
                    .getSubscriptionDaoForSubscription(subscription,
                            retrievalTime));
        }
        timer.lap("createBandwidthSubscriptions");

        bandwidthDao.storeBandwidthSubscriptions(newSubscriptions);
        timer.lap("storeBandwidthSubscriptions");

        unscheduled.addAll(aggregate(new BandwidthSubscriptionContainer(
                subscription, newSubscriptions)));
        timer.lap("aggregate");

        timer.stop();
        timer.logLaps("scheduleSubscriptionForRetrievalTimes() subscription ["
                + subscription.getName() + "] retrievalTimes ["
                + retrievalTimes.size() + "]", performanceHandler);

        return unscheduled;
    }

    protected List<BandwidthAllocation> schedule(Subscription subscription,
            BandwidthSubscription dao) {
        Calendar retrievalTime = dao.getBaseReferenceTime();

        // Retrieve all the current subscriptions by provider, dataset name and
        // base time.
        List<BandwidthSubscription> subscriptions = bandwidthDao
                .getBandwidthSubscriptions(dao.getProvider(),
                        dao.getDataSetName(), retrievalTime);

        statusHandler.info("schedule() - Scheduling subscription ["
                + dao.getName()
                + String.format(
                        "] baseReferenceTime [%1$tY%1$tm%1$td%1$tH%1$tM",
                        retrievalTime));

        return aggregate(new BandwidthSubscriptionContainer(subscription,
                subscriptions));
    }

    /**
     * Aggregate subscriptions for a given base time and dataset.
     * 
     * @param bandwidthSubscriptions
     *            A List of SubscriptionDaos that have the same base time and
     *            dataset.
     */
    private List<BandwidthAllocation> aggregate(
            BandwidthSubscriptionContainer bandwidthSubscriptions) {
        IPerformanceTimer timer = TimeUtil.getPerformanceTimer();
        timer.start();

        List<SubscriptionRetrieval> retrievals = getAggregator().aggregate(
                bandwidthSubscriptions);
        timer.lap("aggregator");

        // Create a separate list of BandwidthReservations to schedule
        // as the aggregation process may return all subsumed
        // SubscriptionRetrievals
        // for the specified Subscription.
        List<SubscriptionRetrieval> reservations = new ArrayList<SubscriptionRetrieval>();

        for (SubscriptionRetrieval retrieval : retrievals) {

            // New RetrievalRequests will be marked as "PROCESSING"
            // we need to make new BandwidthReservations for these
            // SubscriptionRetrievals.

            // TODO: How to process "rescheduled" RetrievalRequests
            // in the case where subscription aggregation has determined
            // that an existing subscription has now be subsumed or
            // altered to accommodate a new super set of subscriptions...
            //
            if ((retrieval.getStatus().equals(RetrievalStatus.RESCHEDULE) || retrieval
                    .getStatus().equals(RetrievalStatus.PROCESSING))
                    && !retrieval.isSubsumed()) {

                BandwidthSubscription bandwidthSubscription = retrieval
                        .getBandwidthSubscription();
                Calendar retrievalTime = bandwidthSubscription
                        .getBaseReferenceTime();
                Calendar startTime = BandwidthUtil.copy(retrievalTime);

                int delayMinutes = retrieval.getDataSetAvailablityDelay();
                int maxLatency = retrieval.getSubscriptionLatency();

                if (statusHandler.isPriorityEnabled(Priority.DEBUG)) {
                    statusHandler.debug("Adding availability minutes of ["
                            + delayMinutes
                            + "] to retrieval start time of "
                            + String.format("[%1$tY%1$tm%1$td%1$tH%1$tM]",
                                    retrievalTime));
                }

                startTime.add(Calendar.MINUTE, delayMinutes);
                retrieval.setStartTime(startTime);

                Calendar endTime = TimeUtil.newCalendar();
                endTime.setTimeInMillis(startTime.getTimeInMillis());

                if (statusHandler.isPriorityEnabled(Priority.DEBUG)) {
                    statusHandler.debug("Adding latency minutes of ["
                            + maxLatency
                            + "] to start time of "
                            + String.format("[%1$tY%1$tm%1$td%1$tH%1$tM]",
                                    startTime));
                }

                endTime.add(Calendar.MINUTE, maxLatency);
                retrieval.setEndTime(endTime);

                // Add SubscriptionRetrieval to the list to schedule..
                reservations.add(retrieval);
            }
        }
        timer.lap("creating retrievals");

        for (SubscriptionRetrieval retrieval : reservations) {
            BandwidthSubscription bandwidthSubscription = retrieval
                    .getBandwidthSubscription();
            if (bandwidthSubscription.isCheckForDataSetUpdate()) {
                // Check to see if the data subscribed to is available..
                // if so, mark the status of the BandwidthReservation as
                // READY.
                List<BandwidthDataSetUpdate> z = bandwidthDao
                        .getBandwidthDataSetUpdate(
                                bandwidthSubscription.getProvider(),
                                bandwidthSubscription.getDataSetName(),
                                bandwidthSubscription.getBaseReferenceTime());
                if (!z.isEmpty()) {
                    retrieval.setStatus(RetrievalStatus.READY);
                }
            }
        }
        timer.lap("checking if ready");

        bandwidthDao.store(reservations);
        timer.lap("storing retrievals");

        List<SubscriptionRetrievalAttributes> attributes = Lists
                .newArrayListWithCapacity(reservations.size());
        for (SubscriptionRetrieval retrieval : reservations) {
            final SubscriptionRetrievalAttributes attribute = new SubscriptionRetrievalAttributes();
            try {
                attribute.setSubscription(bandwidthSubscriptions.subscription);
            } catch (SerializationException e) {
                throw new IllegalStateException(
                        "Unable to serialize the subscription, these retrievals will not be processed!");
            }
            attribute.setSubscriptionRetrieval(retrieval);
            attributes.add(attribute);
        }

        bandwidthDao.storeSubscriptionRetrievalAttributes(attributes);
        timer.lap("storing retrieval attributes");

        List<BandwidthAllocation> unscheduled = (reservations.isEmpty()) ? Collections
                .<BandwidthAllocation> emptyList() : retrievalManager
                .schedule(reservations);
        timer.lap("scheduling retrievals");

        timer.stop();
        final int numberOfBandwidthSubscriptions = bandwidthSubscriptions.newSubscriptions
                .size();
        timer.logLaps("aggregate() bandwidthSubscriptions ["
                + numberOfBandwidthSubscriptions + "]", performanceHandler);

        return unscheduled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BandwidthAllocation> schedule(Subscription subscription) {
        // TODO: In 13.6.1 pull out all of the subscription stuff into a
        // separate plugin, BandwidthManager should not work with Subscription
        // objects directly, it should have extension plugins that can allocate
        // bandwidth in their own types (e.g. registry syncing should be able to
        // sync into the bandwidth management infrastructure if required)
        List<BandwidthAllocation> unscheduled;

        final DataType dataSetType = subscription.getDataSetType();
        switch (dataSetType) {
        case GRID:
            unscheduled = handleGridded(subscription);
            break;
        case POINT:
            unscheduled = handlePoint(subscription);
            break;
        default:
            throw new IllegalArgumentException(
                    "The BandwidthManager doesn't know how to treat subscriptions with data type ["
                            + dataSetType + "]!");
        }

        unscheduleSubscriptionsForAllocations(unscheduled);

        return unscheduled;
    }

    /**
     * Unschedules all subscriptions the allocations are associated to.
     * 
     * @param unscheduled
     *            the unscheduled allocations
     */
    protected abstract void unscheduleSubscriptionsForAllocations(
            List<BandwidthAllocation> unscheduled);

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public List<BandwidthAllocation> schedule(AdhocSubscription subscription) {

        List<BandwidthSubscription> subscriptions = new ArrayList<BandwidthSubscription>();
        Calendar now = BandwidthUtil.now();
        // Store the AdhocSubscription with a base time of now..
        subscriptions.add(bandwidthDao.newBandwidthSubscription(subscription,
                now));

        // Check start time in Time, if it is blank, we need to add the most
        // recent MetaData for the DataSet subscribed to.
        final Time subTime = subscription.getTime();
        if (subTime.getStart() == null) {
            subscription = bandwidthDaoUtil.setAdhocMostRecentUrlAndTime(
                    subscription, true);
        }

        // Use SimpleSubscriptionAggregator (i.e. no aggregation) to generate a
        // SubscriptionRetrieval for this AdhocSubscription
        SimpleSubscriptionAggregator a = new SimpleSubscriptionAggregator(
                bandwidthDao);
        List<BandwidthAllocation> reservations = new ArrayList<BandwidthAllocation>();
        List<SubscriptionRetrieval> retrievals = a
                .aggregate(new BandwidthSubscriptionContainer(subscription,
                        subscriptions));

        for (SubscriptionRetrieval retrieval : retrievals) {
            retrieval.setStartTime(now);
            Calendar endTime = BandwidthUtil.copy(now);
            endTime.add(Calendar.MINUTE, retrieval.getSubscriptionLatency());
            retrieval.setEndTime(endTime);
            // Store the SubscriptionRetrieval - retrievalManager expects
            // the BandwidthAllocations to already be stored.
            bandwidthDao.store(retrieval);
            reservations.add(retrieval);
        }

        List<BandwidthAllocation> unscheduled = retrievalManager
                .schedule(reservations);

        // Now iterate the SubscriptionRetrievals and for the
        // Allocations that were scheduled, set the status to READY
        // and notify the retrievalManager.
        for (SubscriptionRetrieval retrieval : retrievals) {

            if (retrieval.getStatus().equals(RetrievalStatus.SCHEDULED)) {
                SubscriptionRetrievalAttributes attributes = new SubscriptionRetrievalAttributes();
                attributes.setSubscriptionRetrieval(retrieval);

                try {
                    attributes.setSubscription(subscription);

                    bandwidthDao.store(attributes);
                } catch (SerializationException e) {
                    statusHandler.error(
                            "Trapped Exception trying to schedule AdhocSubscription["
                                    + subscription.getName() + "]", e);
                    return Collections.emptyList();
                }

                retrieval.setStatus(RetrievalStatus.READY);
                bandwidthDaoUtil.update(retrieval);
            }
        }

        retrievalManager.wakeAgents();

        return unscheduled;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public List<BandwidthAllocation> subscriptionUpdated(
            Subscription subscription) {
        // Since AdhocSubscription extends Subscription it is not possible to
        // separate the processing of those Objects in EventBus. So, handle the
        // case where the updated subscription is actually an AdhocSubscription
        if (subscription instanceof AdhocSubscription) {
            return adhocSubscription((AdhocSubscription) subscription);
        }
        // Dealing with a 'normal' subscription
        else {
            // First see if BandwidthManager has seen the subscription before.
            List<BandwidthSubscription> bandwidthSubscriptions = bandwidthDao
                    .getBandwidthSubscription(subscription);

            // If BandwidthManager does not know about the subscription, and
            // it's active, attempt to add it..
            if (bandwidthSubscriptions.isEmpty() && subscription.isActive()
                    && !subscription.isUnscheduled()) {
                return schedule(subscription);
            } else if (!subscription.isActive() || subscription.isUnscheduled()) {
                // See if the subscription was inactivated or unscheduled..
                // Need to remove BandwidthReservations for this
                // subscription.
                return remove(bandwidthSubscriptions);
            } else {
                // Normal update, unschedule old allocations and create new ones
                List<BandwidthAllocation> unscheduled = remove(bandwidthSubscriptions);
                unscheduled.addAll(schedule(subscription));
                return unscheduled;
            }
        }
    }

    /**
     * Handle scheduling point data type subscriptions.
     * 
     * @param subscription
     *            the subscription
     * @return the list of unscheduled subscriptions
     */
    private List<BandwidthAllocation> handlePoint(Subscription subscription) {
        return schedule(subscription,
                ((PointTime) subscription.getTime()).getInterval());
    }

    /**
     * Handle scheduling grid data type subscriptions.
     * 
     * @param subscription
     *            the subscription
     * @return the list of unscheduled subscriptions
     */
    private List<BandwidthAllocation> handleGridded(Subscription subscription) {
        final List<Integer> cycles = subscription.getTime().getCycleTimes();
        final boolean subscribedToCycles = !CollectionUtil
                .isNullOrEmpty(cycles);
        final boolean useMostRecentDataSetUpdate = !subscribedToCycles;

        // The subscription has cycles, so we can allocate bandwidth at
        // expected times
        List<BandwidthAllocation> unscheduled = Collections.emptyList();
        if (subscribedToCycles) {
            unscheduled = schedule(subscription, Sets.newTreeSet(cycles));
        }

        // Create an adhoc subscription based on the new subscription,
        // and set it to retrieve the most recent cycle (or most recent
        // url if a daily product)
        if (subscription instanceof SiteSubscription) {
            AdhocSubscription adhoc = new AdhocSubscription(
                    (SiteSubscription) subscription);
            adhoc = bandwidthDaoUtil.setAdhocMostRecentUrlAndTime(adhoc,
                    useMostRecentDataSetUpdate);

            if (adhoc == null) {
                statusHandler
                        .info(String
                                .format("There wasn't applicable most recent dataset metadata to use for new subscription [%s].  "
                                        + "No adhoc requested.",
                                        subscription.getName()));
            } else {
                unscheduled = schedule(adhoc);
            }
        } else {
            statusHandler
                    .warn("Unable to create adhoc queries for shared subscriptions at this point.  This functionality should be added in the future...");
        }
        return unscheduled;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public List<BandwidthAllocation> adhocSubscription(AdhocSubscription adhoc) {
        statusHandler.info("Scheduling adhoc subscription [" + adhoc.getName()
                + "]");
        return schedule(adhoc);
    }

    /**
     * Remove BandwidthSubscription's (and dependent Objects) from any
     * RetrievalPlans they are in and adjust the RetrievalPlans accordingly.
     * 
     * @param bandwidthSubscriptions
     *            The subscriptionDao's to remove.
     * @return
     */
    protected List<BandwidthAllocation> remove(
            List<BandwidthSubscription> bandwidthSubscriptions) {

        List<BandwidthAllocation> unscheduled = new ArrayList<BandwidthAllocation>();

        for (BandwidthSubscription bandwidthSubscription : bandwidthSubscriptions) {
            bandwidthDaoUtil.remove(bandwidthSubscription);
        }

        return unscheduled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAggregator(ISubscriptionAggregator aggregator) {
        this.aggregator = aggregator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISubscriptionAggregator getAggregator() {
        return aggregator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object handleRequest(IBandwidthRequest request) throws Exception {

        ITimer timer = TimeUtil.getTimer();
        timer.start();

        Object response = null;

        final Network requestNetwork = request.getNetwork();
        final int bandwidth = request.getBandwidth();

        final List<Subscription> subscriptions = request.getSubscriptions();
        final RequestType requestType = request.getRequestType();
        switch (requestType) {
        case GET_ESTIMATED_COMPLETION:
            Subscription adhocAsSub = null;
            if (subscriptions.size() != 1
                    || (!((adhocAsSub = subscriptions.get(0)) instanceof AdhocSubscription))) {
                throw new IllegalArgumentException(
                        "Must supply one, and only one, adhoc subscription to get the estimated completion time.");
            }
            response = getEstimatedCompletionTime((AdhocSubscription) adhocAsSub);
            break;
        case REINITIALIZE:
            response = startNewBandwidthManager();
            break;
        case RETRIEVAL_PLAN:
            response = showRetrievalPlan(requestNetwork);
            break;
        case PROPOSE_SCHEDULE_SUBSCRIPTION:
            // SBN subscriptions must go through the NCF
            if (!subscriptions.isEmpty()
                    && Network.SBN.equals(subscriptions.get(0).getRoute())) {
                final IProposeScheduleResponse proposeResponse = proposeScheduleSbnSubscription(subscriptions);
                response = proposeResponse;
            } else {
                // OPSNET subscriptions
                response = proposeScheduleSubscriptions(subscriptions);
            }
            break;
        case SCHEDULE_SUBSCRIPTION:
            // SBN subscriptions must go through the NCF
            if (!subscriptions.isEmpty()
                    && Network.SBN.equals(subscriptions.get(0).getRoute())) {
                response = scheduleSbnSubscriptions(subscriptions);
            } else {
                // OPSNET subscriptions
                response = scheduleSubscriptions(subscriptions);
            }
            break;
        case GET_BANDWIDTH:
            RetrievalPlan b = retrievalManager.getPlan(requestNetwork);
            if (b != null) {
                response = b.getDefaultBandwidth();
            } else {
                response = null;
            }
            break;
        case PROPOSE_SET_BANDWIDTH:
            Set<String> unscheduledSubscriptions = proposeSetBandwidth(
                    requestNetwork, bandwidth);
            response = unscheduledSubscriptions;
            if (unscheduledSubscriptions.isEmpty()) {
                statusHandler
                        .info("No subscriptions will be unscheduled by changing the bandwidth for network ["
                                + requestNetwork
                                + "] to ["
                                + bandwidth
                                + "].  Applying...");
                // This is a safe operation as all subscriptions will remain
                // scheduled, just apply
                setBandwidth(requestNetwork, bandwidth);
            }

            break;
        case FORCE_SET_BANDWIDTH:
            boolean setBandwidth = setBandwidth(requestNetwork, bandwidth);
            response = setBandwidth;
            break;
        case SHOW_ALLOCATION:
            break;

        case SHOW_BUCKET:

            long bucketId = request.getId();
            RetrievalPlan plan = retrievalManager.getPlan(requestNetwork);
            BandwidthBucket bucket = plan.getBucket(bucketId);
            response = plan.showBucket(bucket);
            break;

        case SHOW_DEFERRED:
            StringBuilder sb = new StringBuilder();
            List<BandwidthAllocation> z = bandwidthDao.getDeferred(
                    requestNetwork, request.getBegin());
            for (BandwidthAllocation allocation : z) {
                sb.append(allocation).append("\n");
            }
            response = sb.toString();
            break;
        case GET_BANDWIDTH_GRAPH_DATA:
            response = getBandwidthGraphData();
            break;
        case GET_SUBSCRIPTION_STATUS:
            Subscription sub = null;
            if (subscriptions.size() != 1
                    || (!((sub = subscriptions.get(0)) instanceof Subscription))) {
                throw new IllegalArgumentException(
                        "Must supply one, and only one, subscription to get the status summary.");
            }
            response = bandwidthDao.getSubscriptionStatusSummary(sub);
            break;
        default:
            throw new IllegalArgumentException(
                    "Dont know how to handle request type [" + requestType
                            + "]");
        }

        // Clean up any in-memory bandwidth configuration files
        InMemoryBandwidthContextFactory.deleteInMemoryBandwidthConfigFile();

        timer.stop();

        statusHandler.info("Processed request of type [" + requestType
                + "] in [" + timer.getElapsedTime() + "] ms");

        return response;
    }

    /**
     * Schedule the SBN subscriptions.
     * 
     * @param subscriptions
     *            the subscriptions
     * @return the set of subscription names unscheduled as a result of
     *         scheduling the subscriptions
     * @throws SerializationException
     */
    protected abstract Set<String> scheduleSbnSubscriptions(
            List<Subscription> subscriptions) throws SerializationException;

    /**
     * Proposes scheduling a list of subscriptions.
     * 
     * @param subscriptions
     *            the subscriptions
     * @return the response
     * @throws SerializationException
     */
    protected ProposeScheduleResponse proposeScheduleSubscriptions(
            List<Subscription> subscriptions) throws SerializationException {
        final ProposeScheduleResponse proposeResponse = proposeSchedule(subscriptions);
        Set<String> subscriptionsUnscheduled = proposeResponse
                .getUnscheduledSubscriptions();
        if (subscriptionsUnscheduled.isEmpty()) {
            statusHandler
                    .info("No subscriptions will be unscheduled by scheduling subscriptions "
                            + subscriptions + ".  Applying...");
            // This is a safe operation as all subscriptions will remain
            // scheduled, just apply
            scheduleSubscriptions(subscriptions);
        } else if (subscriptions.size() == 1) {
            final Subscription subscription = subscriptions.get(0);
            int requiredLatency = determineRequiredLatency(subscription);
            proposeResponse.setRequiredLatency(requiredLatency);
            long requiredDataSetSize = determineRequiredDataSetSize(subscription);
            proposeResponse.setRequiredDataSetSize(requiredDataSetSize);
        }
        return proposeResponse;
    }

    /**
     * Propose scheduling SBN routed subscriptions. Sub-classes must implement
     * the specific functionality.
     * 
     * @param subscriptions
     *            the subscriptions targeted at the SBN
     * @return the response
     * @throws Exception
     *             on error
     */
    protected abstract IProposeScheduleResponse proposeScheduleSbnSubscription(
            List<Subscription> subscriptions) throws Exception;

    /**
     * Retrieve the bandwidth graph data.
     * 
     * @return the graph data
     */
    private BandwidthGraphData getBandwidthGraphData() {
        return new BandwidthGraphDataAdapter(retrievalManager).get();
    }

    /**
     * Get the estimated completion time for an adhoc subscription.
     * 
     * @param subscription
     *            the subscription
     * @return the estimated completion time
     */
    private Date getEstimatedCompletionTime(AdhocSubscription subscription) {
        final List<BandwidthSubscription> bandwidthSubscriptions = bandwidthDao
                .getBandwidthSubscriptionByRegistryId(subscription.getId());

        if (bandwidthSubscriptions.isEmpty()) {
            statusHandler
                    .warn("Unable to find subscriptionDaos for subscription ["
                            + subscription + "].  Returning current time.");
            return new Date();
        } else if (bandwidthSubscriptions.size() > 1) {
            statusHandler
                    .warn("Found more than one subscriptionDaos for subscription ["
                            + subscription
                            + "].  Ignoring list and using the first item.");
        }

        BandwidthSubscription bandwidthSubscription = bandwidthSubscriptions
                .get(0);
        long id = bandwidthSubscription.getId();

        final List<BandwidthAllocation> bandwidthAllocations = bandwidthDao
                .getBandwidthAllocations(id);

        if (bandwidthAllocations.isEmpty()) {
            statusHandler
                    .warn("Unable to find bandwidthAllocations for subscription ["
                            + subscription + "].  Returning current time.");
            return new Date();
        }

        Calendar latest = null;
        for (BandwidthAllocation allocation : bandwidthAllocations) {
            final Calendar endTime = allocation.getEndTime();
            if (latest == null || endTime.after(latest)) {
                latest = endTime;
            }
        }

        return latest.getTime();
    }

    /**
     * Schedule the list of subscriptions.
     * 
     * @param subscriptions
     *            the subscriptions
     * @return the set of subscription names unscheduled
     * @throws SerializationException
     */
    protected Set<String> scheduleSubscriptions(List<Subscription> subscriptions)
            throws SerializationException {
        Set<String> unscheduledSubscriptions = new TreeSet<String>();

        Set<BandwidthAllocation> unscheduledAllocations = new HashSet<BandwidthAllocation>();

        for (Subscription subscription : subscriptions) {
            unscheduledAllocations.addAll(subscriptionUpdated(subscription));
        }

        for (BandwidthAllocation allocation : unscheduledAllocations) {
            if (allocation instanceof SubscriptionRetrieval) {
                SubscriptionRetrieval retrieval = (SubscriptionRetrieval) allocation;
                unscheduledSubscriptions.add(retrieval
                        .getBandwidthSubscription().getName());
            }
        }

        return unscheduledSubscriptions;
    }

    /**
     * Sets the bandwidth for a network to the specified value.
     * 
     * @param requestNetwork
     *            the network
     * @param bandwidth
     *            the bandwidth
     * @return true on success, false otherwise
     * @throws SerializationException
     *             on error serializing
     */
    private boolean setBandwidth(Network requestNetwork, int bandwidth)
            throws SerializationException {
        RetrievalPlan c = retrievalManager.getPlan(requestNetwork);
        if (c != null) {
            c.setDefaultBandwidth(bandwidth);

            return startNewBandwidthManager();
        }
        return false;
    }

    /**
     * Propose scheduling the subscriptions.
     * 
     * @param subscriptions
     *            the subscriptions
     * @return the response
     * @throws SerializationException
     */
    private ProposeScheduleResponse proposeSchedule(
            List<Subscription> subscriptions) throws SerializationException {
        BandwidthMap copyOfCurrentMap = BandwidthMap
                .load(EdexBandwidthContextFactory.getBandwidthMapConfig());

        Set<String> unscheduled = Collections.emptySet();
        BandwidthManager proposedBwManager = null;
        try {
            proposedBwManager = startProposedBandwidthManager(copyOfCurrentMap);

            IBandwidthRequest request = new IBandwidthRequest();
            request.setRequestType(RequestType.SCHEDULE_SUBSCRIPTION);
            request.setSubscriptions(subscriptions);

            unscheduled = proposedBwManager
                    .scheduleSubscriptions(subscriptions);
        } finally {
            nullSafeShutdown(proposedBwManager);
        }

        final ProposeScheduleResponse proposeScheduleResponse = new ProposeScheduleResponse();
        proposeScheduleResponse.setUnscheduledSubscriptions(unscheduled);

        return proposeScheduleResponse;
    }

    /**
     * Shutdown if not null.
     * 
     * @param bandwidthManager
     *            the bandwidth manager
     */
    private void nullSafeShutdown(BandwidthManager bandwidthManager) {
        if (bandwidthManager != null) {
            bandwidthManager.shutdown();
        }
    }

    /**
     * Propose changing a route's bandwidth to the specified amount.
     * 
     * @return the subscriptions that would be unscheduled after setting the
     *         bandwidth
     * 
     * @throws SerializationException
     */
    private Set<String> proposeSetBandwidth(Network requestNetwork,
            int bandwidth) throws SerializationException {
        BandwidthMap copyOfCurrentMap = BandwidthMap
                .load(EdexBandwidthContextFactory.getBandwidthMapConfig());
        BandwidthRoute route = copyOfCurrentMap.getRoute(requestNetwork);
        route.setDefaultBandwidth(bandwidth);

        Set<String> subscriptions = new HashSet<String>();
        BandwidthManager proposedBwManager = null;
        try {
            proposedBwManager = startProposedBandwidthManager(copyOfCurrentMap);

            if (statusHandler.isPriorityEnabled(Priority.DEBUG)) {
                statusHandler.debug("Current retrieval plan:" + Util.EOL
                        + showRetrievalPlan(requestNetwork) + Util.EOL
                        + "Proposed retrieval plan:" + Util.EOL
                        + proposedBwManager.showRetrievalPlan(requestNetwork));
            }

            List<BandwidthAllocation> unscheduledAllocations = proposedBwManager.bandwidthDao
                    .getBandwidthAllocationsInState(RetrievalStatus.UNSCHEDULED);

            if (!unscheduledAllocations.isEmpty()
                    && statusHandler.isPriorityEnabled(Priority.DEBUG)) {
                LogUtil.logIterable(
                        statusHandler,
                        Priority.DEBUG,
                        "The following unscheduled allocations would occur with the proposed bandwidth:",
                        unscheduledAllocations);
            }

            for (BandwidthAllocation allocation : unscheduledAllocations) {
                if (allocation instanceof SubscriptionRetrieval) {
                    subscriptions.add(((SubscriptionRetrieval) allocation)
                            .getBandwidthSubscription().getName());
                }
            }

            if (!subscriptions.isEmpty()
                    && statusHandler.isPriorityEnabled(Priority.INFO)) {
                LogUtil.logIterable(
                        statusHandler,
                        Priority.INFO,
                        "The following subscriptions would not be scheduled with the proposed bandwidth:",
                        subscriptions);
            }
        } finally {
            nullSafeShutdown(proposedBwManager);
        }

        return subscriptions;
    }

    /**
     * Starts the proposed bandwidth manager and returns the reference to it.
     * 
     * @param bandwidthMap
     * 
     * @return the proposed bandwidth manager
     * @throws SerializationException
     */
    @VisibleForTesting
    BandwidthManager startProposedBandwidthManager(BandwidthMap bandwidthMap) {

        InMemoryBandwidthContextFactory
                .setInMemoryBandwidthConfigFile(bandwidthMap);

        return startBandwidthManager(
                InMemoryBandwidthManager.IN_MEMORY_BANDWIDTH_MANAGER_FILES,
                true, "memory");
    }

    /**
     * Starts the new bandwidth manager.
     * 
     * @return true if the new bandwidth manager was started
     */
    private boolean startNewBandwidthManager() {
        BandwidthManager bandwidthManager = startBandwidthManager(
                getSpringFilesForNewInstance(), false, "EDEX");

        final boolean successfullyStarted = bandwidthManager != null;
        if (successfullyStarted) {
            this.shutdown();
        } else {
            statusHandler
                    .error("The new BandwidthManager reference was null, please check the log for errors.");
        }
        return successfullyStarted;
    }

    /**
     * Starts a {@link BandwidthManager} and returns a reference to it.
     * 
     * @param springFiles
     *            the spring files to use
     * @param type
     * @return the reference to the bandwidth manager
     */
    private BandwidthManager startBandwidthManager(final String[] springFiles,
            boolean close, String type) {
        ITimer timer = TimeUtil.getTimer();
        timer.start();

        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(springFiles,
                    EDEXUtil.getSpringContext());
            final BandwidthManager bwManager = ctx.getBean("bandwidthManager",
                    BandwidthManager.class);
            try {
                bwManager.initializer.executeAfterRegistryInit();
                return bwManager;
            } catch (EbxmlRegistryException e) {
                statusHandler
                        .handle(Priority.PROBLEM,
                                "Error loading subscriptions after starting the new bandwidth manager!  Returning null reference.",
                                e);
                return null;
            }
        } finally {
            if (close) {
                Util.close(ctx);
            }

            timer.stop();
            statusHandler.info("Took [" + timer.getElapsedTime()
                    + "] ms to start a new bandwidth manager of type [" + type
                    + "]");
        }
    }

    /**
     * Return the display of the retrieval plan for the network.
     * 
     * @param network
     *            the network
     * @return the plan
     */
    private String showRetrievalPlan(Network network) {
        RetrievalPlan a = retrievalManager.getPlan(network);
        return (a != null) ? a.showPlan() : "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        initializer.init(this, dbInit, retrievalManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializer(BandwidthInitializer initializer) {
        this.initializer = initializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BandwidthInitializer getInitializer() {
        return initializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorizationResponse authorized(IUser user,
            IBandwidthRequest request) throws AuthorizationException {
        return new AuthorizationResponse(true);
    }

    /**
     * {@inheritDoc}
     */
    public List<BandwidthAllocation> copyState(BandwidthManager copyFrom) {
        IPerformanceTimer timer = TimeUtil.getPerformanceTimer();
        timer.start();
        List<BandwidthAllocation> unscheduled = Collections.emptyList();
        IBandwidthDao fromDao = copyFrom.bandwidthDao;

        final boolean proposingBandwidthChange = retrievalManager
                .isProposingBandwidthChanges(copyFrom.retrievalManager);
        if (proposingBandwidthChange) {

            retrievalManager.initRetrievalPlans();

            // Proposing bandwidth changes requires the old way of bringing up a
            // fresh bandwidth manager and trying the change from scratch
            unscheduled = Lists.newArrayList();
            Set<String> subscriptionNames = Sets.newHashSet();
            for (BandwidthSubscription subscription : fromDao
                    .getBandwidthSubscriptions()) {
                subscriptionNames.add(subscription.getName());
            }

            Set<Subscription> actualSubscriptions = Sets.newHashSet();
            for (String subName : subscriptionNames) {
                try {
                    Subscription actualSubscription = DataDeliveryHandlers
                            .getSubscriptionHandler().getByName(subName);
                    actualSubscriptions.add(actualSubscription);
                } catch (RegistryHandlerException e) {
                    statusHandler
                            .handle(Priority.PROBLEM,
                                    "Unable to lookup the subscription, results may not be accurate for modeling bandwidth changes.",
                                    e);
                }
            }

            // Now for each subscription, attempt to schedule bandwidth
            for (Subscription subscription : actualSubscriptions) {
                unscheduled.addAll(this.schedule(subscription));
            }
        } else {
            // Otherwise we can just copy the entire state of the current system
            // and attempt the proposed changes
            final List<SubscriptionRetrieval> subscriptionRetrievals = fromDao
                    .getSubscriptionRetrievals();
            List<BandwidthSubscription> bandwidthSubscriptions = Lists
                    .newArrayListWithCapacity(subscriptionRetrievals.size());
            for (SubscriptionRetrieval retrieval : subscriptionRetrievals) {
                bandwidthSubscriptions
                        .add(retrieval.getBandwidthSubscription());
            }
            bandwidthDao.storeBandwidthSubscriptions(bandwidthSubscriptions);

            bandwidthDao.store(subscriptionRetrievals);

            RetrievalManager fromRetrievalManager = copyFrom.retrievalManager;
            this.retrievalManager.copyState(fromRetrievalManager);
        }

        timer.stop();
        timer.logLaps("copyingState()", performanceHandler);

        return unscheduled;
    }

    /**
     * Performs shutdown necessary for the {@link BandwidthManager} instance.
     */
    @VisibleForTesting
    void shutdown() {
        try {
            retrievalManager.shutdown();
        } catch (Exception e) {
            statusHandler.handle(Priority.WARN,
                    "Unable to shutdown the retrievalManager.", e);
        } finally {
            shutdownInternal();
        }
    }

    /**
     * Get the Spring files used to create a new instance of this
     * {@link BandwidthManager} type.
     * 
     * @return the Spring files
     */
    protected abstract String[] getSpringFilesForNewInstance();

    /**
     * Determine the latency that would be required on the subscription for it
     * to be fully scheduled.
     * 
     * @param subscription
     *            the subscription
     * @return the required latency, in minutes
     */
    @VisibleForTesting
    int determineRequiredLatency(final Subscription subscription) {
        final int requiredLatency = determineRequiredValue(subscription,
                new FindSubscriptionRequiredLatency());

        final int bufferRoomInMinutes = retrievalManager.getPlan(
                subscription.getRoute()).getBucketMinutes();

        return requiredLatency + bufferRoomInMinutes;
    }

    /**
     * Determine the dataset size that would be required on the subscription for
     * it to be fully scheduled.
     * 
     * @param subscription
     *            the subscription
     * @return the required dataset size
     */
    private long determineRequiredDataSetSize(final Subscription subscription) {
        return determineRequiredValue(subscription,
                new FindSubscriptionRequiredDataSetSize());
    }

    /**
     * Determine a value that would be required on the subscription for it to be
     * fully scheduled.
     * 
     * @param subscription
     *            the subscription
     * @param strategy
     *            the required value strategy
     * @return the required value
     */
    private <T extends Comparable<T>> T determineRequiredValue(
            final Subscription subscription,
            final IFindSubscriptionRequiredValue<T> strategy) {
        ITimer timer = TimeUtil.getTimer();
        timer.start();

        boolean foundRequiredValue = false;
        T currentValue = strategy.getInitialValue(subscription);

        T previousValue = currentValue;
        do {
            previousValue = currentValue;
            currentValue = strategy.getNextValue(subscription, currentValue);

            Subscription clone = strategy.setValue(subscription.copy(),
                    currentValue);
            foundRequiredValue = isSchedulableWithoutConflict(clone);
        } while (!foundRequiredValue);

        SortedSet<T> possibleValues = strategy.getPossibleValues(previousValue,
                currentValue);

        IBinarySearchResponse<T> response = AlgorithmUtil.binarySearch(
                possibleValues, new Comparable<T>() {
                    @Override
                    public int compareTo(T valueToCheck) {
                        Subscription clone = strategy.setValue(
                                subscription.copy(), valueToCheck);

                        boolean valueWouldWork = isSchedulableWithoutConflict(clone);

                        // Check if one more restrictive value would not work,
                        // if so then this is the required value, otherwise keep
                        // searching
                        if (valueWouldWork) {
                            clone = strategy.setValue(
                                    subscription.copy(),
                                    strategy.getNextRestrictiveValue(valueToCheck));

                            return (isSchedulableWithoutConflict(clone)) ? 1
                                    : 0;
                        } else {
                            // Stuff would still be unscheduled
                            return -1;
                        }
                    }
                });

        final T binarySearchedValue = response.getItem();
        final String valueDescription = strategy.getValueDescription();
        if (binarySearchedValue != null) {
            currentValue = binarySearchedValue;

            if (statusHandler.isPriorityEnabled(Priority.DEBUG)) {
                statusHandler.debug(String.format("Found required "
                        + valueDescription + " of [%s] in [%s] iterations",
                        binarySearchedValue, response.getIterations()));
            }
        } else {
            statusHandler.warn(String.format("Unable to find the required "
                    + valueDescription
                    + " with a binary search, using value [%s]", currentValue));
        }

        timer.stop();

        final String logMsg = String.format("Determined required "
                + valueDescription + " of [%s] in [%s] ms.", currentValue,
                timer.getElapsedTime());

        statusHandler.info(logMsg);

        return currentValue;
    }

    /**
     * Checks whether the subscription, as defined, would be schedulable without
     * conflicting with the current bandwidth or any other subscriptions.
     * 
     * @param subscription
     *            the subscription
     * @return true if able to be cleanly scheduled, false otherwise
     */
    private boolean isSchedulableWithoutConflict(final Subscription subscription) {
        BandwidthMap copyOfCurrentMap = BandwidthMap
                .load(EdexBandwidthContextFactory.getBandwidthMapConfig());

        BandwidthManager proposedBandwidthManager = null;
        try {
            proposedBandwidthManager = startProposedBandwidthManager(copyOfCurrentMap);
            Set<String> unscheduled = proposedBandwidthManager
                    .scheduleSubscriptions(Arrays.asList(subscription));
            return unscheduled.isEmpty();
        } catch (SerializationException e) {
            statusHandler
                    .handle(Priority.PROBLEM,
                            "Serialization error while determining required latency.  Returning true in order to be fault tolerant.",
                            e);
            return true;
        } finally {
            nullSafeShutdown(proposedBandwidthManager);
        }
    }

    /**
     * Provide implementation specific shutdown.
     */
    protected abstract void shutdownInternal();
}
