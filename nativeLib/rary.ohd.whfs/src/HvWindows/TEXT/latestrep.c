/*
** Generated by X-Designer
*/
/*
**LIBS: -lXm -lXt -lX11
*/

#include <X11/Xatom.h>
#include <X11/Intrinsic.h>
#include <X11/Shell.h>

#include <Xm/Xm.h>
#include <Xm/DialogS.h>
#include <Xm/Form.h>
#include <Xm/Frame.h>
#include <Xm/Label.h>
#include <Xm/List.h>
#include <Xm/PushB.h>
#include <Xm/RowColumn.h>
#include <Xm/ScrollBar.h>
#include <Xm/Text.h>
#include <Xm/CascadeBG.h>
#include <Xm/LabelG.h>


Widget latestrepDS = (Widget) NULL;
Widget latestrepFO = (Widget) NULL;
Widget latest_filterOM = (Widget) NULL;
Widget latest_filterLB = (Widget) NULL;
Widget latest_filterCB = (Widget) NULL;
Widget latest_filterPDM = (Widget) NULL;
Widget filter_allPB = (Widget) NULL;
Widget filter_olderthanPB = (Widget) NULL;
Widget filter_neverPB = (Widget) NULL;
Widget latest_sortOM = (Widget) NULL;
Widget latest_sortLB = (Widget) NULL;
Widget latest_sortCB = (Widget) NULL;
Widget latest_sortPDM = (Widget) NULL;
Widget sort_locationPB = (Widget) NULL;
Widget sort_timePB = (Widget) NULL;
Widget latestrepLB = (Widget) NULL;
Widget latestlistSL = (Widget) NULL;
Widget latestlistHSB = (Widget) NULL;
Widget latestlistVSB = (Widget) NULL;
Widget latestlistLS = (Widget) NULL;
Widget latestlocFR = (Widget) NULL;
Widget latestlocFO = (Widget) NULL;
Widget latestlocSL = (Widget) NULL;
Widget latestlocHSB = (Widget) NULL;
Widget latestlocVSB = (Widget) NULL;
Widget latestlocLS = (Widget) NULL;
Widget latestlocLB1 = (Widget) NULL;
Widget latestlocLB2 = (Widget) NULL;
Widget cutimeLB = (Widget) NULL;
Widget curtimeTX = (Widget) NULL;
Widget dcpfreqLB = (Widget) NULL;
Widget dcpfreqTX = (Widget) NULL;
Widget dcprepLB = (Widget) NULL;
Widget dcprepTX = (Widget) NULL;
Widget telemfreqLB = (Widget) NULL;
Widget telemfreqTX = (Widget) NULL;
Widget telemrepLB = (Widget) NULL;
Widget latestlocLB = (Widget) NULL;
Widget latestrep_okPB = (Widget) NULL;
Widget durationTX = (Widget) NULL;
Widget hoursLB = (Widget) NULL;



void create_latestrepDS (parent)
Widget parent;
{
	Widget children[12];      /* Children to manage */
	Arg al[64];                    /* Arg List */
	register int ac = 0;           /* Arg Count */
	XtPointer tmp_value;             /* ditto */
	XmString xmstrings[16];    /* temporary storage for XmStrings */

	XtSetArg(al[ac], XmNallowShellResize, TRUE); ac++;
	XtSetArg(al[ac], XmNtitle, "Station Reporting Status"); ac++;
	XtSetArg(al[ac], XmNminWidth, 940); ac++;
	XtSetArg(al[ac], XmNminHeight, 755); ac++;
	XtSetArg(al[ac], XmNmaxWidth, 940); ac++;
	XtSetArg(al[ac], XmNmaxHeight, 755); ac++;
	latestrepDS = XmCreateDialogShell ( parent, (char *) "latestrepDS", al, ac );
	ac = 0;
	XtSetArg(al[ac], XmNautoUnmanage, FALSE); ac++;
	latestrepFO = XmCreateForm ( latestrepDS, (char *) "latestrepFO", al, ac );
	ac = 0;
#if       ((XmVERSION > 1) && (XmREVISION == 1) && (XmUPDATE_LEVEL < 20))
	xmstrings[0] = XmStringGenerate((XtPointer) "List", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL);
#else  /* ((XmVERSION > 1) && (XmREVISION == 1) && (XmUPDATE_LEVEL < 20)) */
	xmstrings[0] = XmStringCreateLtoR("List", (XmStringCharSet) XmFONTLIST_DEFAULT_TAG);
#endif /* ((XmVERSION > 1) && (XmREVISION == 1) && (XmUPDATE_LEVEL < 20)) */
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	latest_filterOM = XmCreateOptionMenu ( latestrepFO, (char *) "latest_filterOM", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	latest_filterLB = XmOptionLabelGadget ( latest_filterOM );
	latest_filterCB = XmOptionButtonGadget ( latest_filterOM );
	latest_filterPDM = XmCreatePulldownMenu ( latest_filterOM, (char *) "latest_filterPDM", al, ac );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "All Locations With Latest Data", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	filter_allPB = XmCreatePushButton ( latest_filterPDM, (char *) "filter_allPB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Only Locations With Latest Data Older Than", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	filter_olderthanPB = XmCreatePushButton ( latest_filterPDM, (char *) "filter_olderthanPB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Locations Without Any Latest Data", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	filter_neverPB = XmCreatePushButton ( latest_filterPDM, (char *) "filter_neverPB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
#if       ((XmVERSION > 1) && (XmREVISION == 1) && (XmUPDATE_LEVEL < 20))
	xmstrings[0] = XmStringGenerate((XtPointer) "Sort", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL);
#else  /* ((XmVERSION > 1) && (XmREVISION == 1) && (XmUPDATE_LEVEL < 20)) */
	xmstrings[0] = XmStringCreateLtoR("Sort", (XmStringCharSet) XmFONTLIST_DEFAULT_TAG);
#endif /* ((XmVERSION > 1) && (XmREVISION == 1) && (XmUPDATE_LEVEL < 20)) */
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	latest_sortOM = XmCreateOptionMenu ( latestrepFO, (char *) "latest_sortOM", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	latest_sortLB = XmOptionLabelGadget ( latest_sortOM );
	latest_sortCB = XmOptionButtonGadget ( latest_sortOM );
	latest_sortPDM = XmCreatePulldownMenu ( latest_sortOM, (char *) "latest_sortPDM", al, ac );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "By Location", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	sort_locationPB = XmCreatePushButton ( latest_sortPDM, (char *) "sort_locationPB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "By Time", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	sort_timePB = XmCreatePushButton ( latest_sortPDM, (char *) "sort_timePB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Location     Name                        Observation TimeZ                                 Latest Data Posted TimeZ", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_BEGINNING); ac++;
	latestrepLB = XmCreateLabel ( latestrepFO, (char *) "latestrepLB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	XtSetArg(al[ac], XmNlistSizePolicy, XmRESIZE_IF_POSSIBLE); ac++;
	latestlistLS = XmCreateScrolledList ( latestrepFO, (char *) "latestlistLS", al, ac );
	ac = 0;
	latestlistSL = XtParent ( latestlistLS );

	XtSetArg(al[ac], XmNhorizontalScrollBar, &latestlistHSB); ac++;
	XtSetArg(al[ac], XmNverticalScrollBar, &latestlistVSB); ac++;
	XtGetValues(latestlistSL, al, ac );
	ac = 0;
	latestlocFR = XmCreateFrame ( latestrepFO, (char *) "latestlocFR", al, ac );
	latestlocFO = XmCreateForm ( latestlocFR, (char *) "latestlocFO", al, ac );
	XtSetArg(al[ac], XmNlistSizePolicy, XmRESIZE_IF_POSSIBLE); ac++;
	latestlocLS = XmCreateScrolledList ( latestlocFO, (char *) "latestlocLS", al, ac );
	ac = 0;
	latestlocSL = XtParent ( latestlocLS );

	XtSetArg(al[ac], XmNhorizontalScrollBar, &latestlocHSB); ac++;
	XtSetArg(al[ac], XmNverticalScrollBar, &latestlocVSB); ac++;
	XtGetValues(latestlocSL, al, ac );
	ac = 0;
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Location      Elm    Dur Src Ex    ObsTimeZ                     Value     SQ  QC  RV      Id             TimeZ                       TimeZ", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_BEGINNING); ac++;
	latestlocLB1 = XmCreateLabel ( latestlocFO, (char *) "latestlocLB1", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "              Phys       Typ                                                                             Product                     Posting", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_BEGINNING); ac++;
	latestlocLB2 = XmCreateLabel ( latestlocFO, (char *) "latestlocLB2", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Current TimeZ:", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_BEGINNING); ac++;
	cutimeLB = XmCreateLabel ( latestlocFO, (char *) "cutimeLB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	XtSetArg(al[ac], XmNeditable, FALSE); ac++;
	curtimeTX = XmCreateText ( latestlocFO, (char *) "curtimeTX", al, ac );
	ac = 0;
	xmstrings[0] = XmStringGenerate ( (XtPointer) "DCP Reports\nEvery:", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_END); ac++;
	dcpfreqLB = XmCreateLabel ( latestlocFO, (char *) "dcpfreqLB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	XtSetArg(al[ac], XmNmaxLength, 5); ac++;
	XtSetArg(al[ac], XmNeditable, FALSE); ac++;
	dcpfreqTX = XmCreateText ( latestlocFO, (char *) "dcpfreqTX", al, ac );
	ac = 0;
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Minutes\nStarting:", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_END); ac++;
	dcprepLB = XmCreateLabel ( latestlocFO, (char *) "dcprepLB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	XtSetArg(al[ac], XmNmaxLength, 8); ac++;
	XtSetArg(al[ac], XmNeditable, FALSE); ac++;
	dcprepTX = XmCreateText ( latestlocFO, (char *) "dcprepTX", al, ac );
	ac = 0;
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Telemetry\nReports Every:", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_END); ac++;
	telemfreqLB = XmCreateLabel ( latestlocFO, (char *) "telemfreqLB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	XtSetArg(al[ac], XmNmaxLength, 5); ac++;
	XtSetArg(al[ac], XmNeditable, FALSE); ac++;
	telemfreqTX = XmCreateText ( latestlocFO, (char *) "telemfreqTX", al, ac );
	ac = 0;
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Minutes", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_BEGINNING); ac++;
	telemrepLB = XmCreateLabel ( latestlocFO, (char *) "telemrepLB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	XtSetArg(al[ac], XmNframeChildType, XmFRAME_TITLE_CHILD); ac++;
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Latest Data for Selected Location", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	latestlocLB = XmCreateLabel ( latestlocFR, (char *) "latestlocLB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Close", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	latestrep_okPB = XmCreatePushButton ( latestrepFO, (char *) "latestrep_okPB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );
	XtSetArg(al[ac], XmNmaxLength, 4); ac++;
	durationTX = XmCreateText ( latestrepFO, (char *) "durationTX", al, ac );
	ac = 0;
	xmstrings[0] = XmStringGenerate ( (XtPointer) "Hours Ago", XmFONTLIST_DEFAULT_TAG, XmCHARSET_TEXT, NULL );
	XtSetArg(al[ac], XmNlabelString, xmstrings[0]); ac++;
	XtSetArg(al[ac], XmNalignment, XmALIGNMENT_BEGINNING); ac++;
	hoursLB = XmCreateLabel ( latestrepFO, (char *) "hoursLB", al, ac );
	ac = 0;
	XmStringFree ( xmstrings [ 0 ] );


	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 5); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -40); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 10); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -465); ac++;
	XtSetValues ( latest_filterOM, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 5); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -40); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 730); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -990); ac++;
	XtSetValues ( latest_sortOM, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 45); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -80); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 5); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -985); ac++;
	XtSetValues ( latestrepLB, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 80); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -395); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 5); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -935); ac++;
	XtSetValues ( latestlistSL, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 400); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -710); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 0); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -935); ac++;
	XtSetValues ( latestlocFR, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 715); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -750); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 435); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -535); ac++;
	XtSetValues ( latestrep_okPB, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 5); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -40); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 475); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -545); ac++;
	XtSetValues ( durationTX, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 5); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -40); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 550); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -640); ac++;
	XtSetValues ( hoursLB, al, ac );
	ac = 0;
	if ((children[ac] = filter_allPB) != (Widget) 0) { ac++; }
	if ((children[ac] = filter_olderthanPB) != (Widget) 0) { ac++; }
	if ((children[ac] = filter_neverPB) != (Widget) 0) { ac++; }
	if (ac > 0) { XtManageChildren(children, ac); }
	ac = 0;
	XtSetArg(al[ac], XmNsubMenuId, latest_filterPDM); ac++;
	XtSetValues(latest_filterCB, al, ac );
	ac = 0;
	if ((children[ac] = sort_locationPB) != (Widget) 0) { ac++; }
	if ((children[ac] = sort_timePB) != (Widget) 0) { ac++; }
	if (ac > 0) { XtManageChildren(children, ac); }
	ac = 0;
	XtSetArg(al[ac], XmNsubMenuId, latest_sortPDM); ac++;
	XtSetValues(latest_sortCB, al, ac );
	ac = 0;
	if (latestlistLS != (Widget) 0) { XtManageChild(latestlistLS); }

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 50); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -220); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 5); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -925); ac++;
	XtSetValues ( latestlocSL, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 25); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -50); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 5); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -925); ac++;
	XtSetValues ( latestlocLB1, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 0); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -25); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 5); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -925); ac++;
	XtSetValues ( latestlocLB2, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 225); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -250); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 60); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -200); ac++;
	XtSetValues ( cutimeLB, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 250); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -285); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 10); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -240); ac++;
	XtSetValues ( curtimeTX, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 240); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -280); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 555); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -660); ac++;
	XtSetValues ( dcpfreqLB, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 245); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -280); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 665); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -735); ac++;
	XtSetValues ( dcpfreqTX, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 240); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -280); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 740); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -810); ac++;
	XtSetValues ( dcprepLB, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 245); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -280); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 815); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -925); ac++;
	XtSetValues ( dcprepTX, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 240); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -280); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 265); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -390); ac++;
	XtSetValues ( telemfreqLB, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 245); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -280); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 395); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -470); ac++;
	XtSetValues ( telemfreqTX, al, ac );
	ac = 0;

	XtSetArg(al[ac], XmNtopAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNtopOffset, 240); ac++;
	XtSetArg(al[ac], XmNbottomAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNbottomOffset, -280); ac++;
	XtSetArg(al[ac], XmNleftAttachment, XmATTACH_FORM); ac++;
	XtSetArg(al[ac], XmNleftOffset, 475); ac++;
	XtSetArg(al[ac], XmNrightAttachment, XmATTACH_OPPOSITE_FORM); ac++;
	XtSetArg(al[ac], XmNrightOffset, -545); ac++;
	XtSetValues ( telemrepLB, al, ac );
	ac = 0;
	if (latestlocLS != (Widget) 0) { XtManageChild(latestlocLS); }
	if ((children[ac] = latestlocLB1) != (Widget) 0) { ac++; }
	if ((children[ac] = latestlocLB2) != (Widget) 0) { ac++; }
	if ((children[ac] = cutimeLB) != (Widget) 0) { ac++; }
	if ((children[ac] = curtimeTX) != (Widget) 0) { ac++; }
	if ((children[ac] = dcpfreqLB) != (Widget) 0) { ac++; }
	if ((children[ac] = dcpfreqTX) != (Widget) 0) { ac++; }
	if ((children[ac] = dcprepLB) != (Widget) 0) { ac++; }
	if ((children[ac] = dcprepTX) != (Widget) 0) { ac++; }
	if ((children[ac] = telemfreqLB) != (Widget) 0) { ac++; }
	if ((children[ac] = telemfreqTX) != (Widget) 0) { ac++; }
	if ((children[ac] = telemrepLB) != (Widget) 0) { ac++; }
	if (ac > 0) { XtManageChildren(children, ac); }
	ac = 0;
	if ((children[ac] = latestlocFO) != (Widget) 0) { ac++; }
	if ((children[ac] = latestlocLB) != (Widget) 0) { ac++; }
	if (ac > 0) { XtManageChildren(children, ac); }
	ac = 0;
	if ((children[ac] = latest_filterOM) != (Widget) 0) { ac++; }
	if ((children[ac] = latest_sortOM) != (Widget) 0) { ac++; }
	if ((children[ac] = latestrepLB) != (Widget) 0) { ac++; }
	if ((children[ac] = latestlocFR) != (Widget) 0) { ac++; }
	if ((children[ac] = latestrep_okPB) != (Widget) 0) { ac++; }
	if ((children[ac] = durationTX) != (Widget) 0) { ac++; }
	if ((children[ac] = hoursLB) != (Widget) 0) { ac++; }
	if (ac > 0) { XtManageChildren(children, ac); }
	ac = 0;
}

