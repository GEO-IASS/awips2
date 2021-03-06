C MODULE QPRTTS
C-----------------------------------------------------------------------
C
C  ROUTINE QPRTTS PRINTS THE TIME SERIES DATA.
C
C  ORIGINALLY CODED BY DEBBIE VAN DEMARK, HSD
C
C------------------------------------------------------------------
C
C  INPUT ARGUMENTS:
C     LUTMP - LOGICAL UNIT NUMBER OF TEMPORARY FILE TO BE READ
C     TSDAT - ARRAY TO STORE TIME SERIES DATA
C
C-------------------------------------------------------------------
C
      SUBROUTINE QPRTTS (LUTMP,TSDAT)
C
      CHARACTER*1 CARCTL
      CHARACTER*4 STATE,DTYPE,UNITOT,STRING
      CHARACTER*8 OLDOPN,STAID
      CHARACTER*8 OLDID/' '/
      CHARACTER*20 DESC
      CHARACTER*132 LINE,STRNG,FORMT
      DIMENSION TSDAT(1)
C
      INCLUDE 'common/ionum'
      INCLUDE 'common/fctim2'
      INCLUDE 'common/fctime'
      INCLUDE 'common/pudbug'
      INCLUDE 'common/pptime'
      INCLUDE 'common/qprint'
      INCLUDE 'common/where'
C
C    ================================= RCS keyword statements ==========
      CHARACTER*68     RCSKW1,RCSKW2
      DATA             RCSKW1,RCSKW2 /                                 '
     .$Source: /fs/hseb/ob72/rfc/ofs/src/fcst_rrs/RCS/qprtts.f,v $
     . $',                                                             '
     .$Id: qprtts.f,v 1.7 2002/02/11 20:37:51 dws Exp $
     . $' /
C    ===================================================================
C
C
      IOPNUM=-3
      CALL FSTWHR ('QPRTTS  ',IOPNUM,OLDOPN,IOLDOP)
C
      IF (IPTRCE.GT.1) WRITE (IOPDBG,*) 'ENTER QPRTTS'
C
      IBUG=IPBUG('QPTS')
C
C  PRINT HEADER
      STRNG='RRS TIME SERIES DISPLAY'
      CARCTL='1'
      CALL QFORMT (STRNG,CARCTL,FORMT)
      WRITE (IPR,FORMT)
      N=IDARUN
      IF (IPLSTD.NE.0) N=LDACPD-1
      CALL MDYH1 (N,IHRRUN,INMO,INDA,INYR,INHR,NOUTZ,NOUTDS,TZC)
      CALL MDYH1 (LDARUN,LHRRUN,LAMO,LADA,LAYR,LAHR,NOUTZ,NOUTDS,TZC)
      CALL MDYH1 (LDACPD,LHRCPD,LOMO,LODA,LOYR,LOHR,NOUTZ,NOUTDS,TZC)
      WRITE (IPR,110) INMO,INDA,INYR,INHR,TZC,
     $   LAMO,LADA,LAYR,LAHR,TZC,
     $   LOMO,LODA,LOYR,LOHR,TZC
      WRITE (IPR,111)
      WRITE (IPR,112)
C
C  READ TEMPORARY FILE TO GET THE FOLLOWING VARIABLES:
C    STAID - STATION IDENTIFIER
C     DESC - STATION DESCRIPTION
C    STSTE - STATE IDENTIFIER
C    DTYPE - DATA TYPE
C   UNITOT - OUTPUT DATA UNTS
C      IDT - TIME INTERVAL
C   NUMVAL - NUMBER OF DATA VALUES IN TSDAT
C     NUMR - NUMBER OF REGULAR DATA VALUES FOR THE PERIOD
C            (MAY EXCEED NUMVAL IF TRAILING -999 CUT OFF)
C    CONVT - METRIC CONVERSION FACTOR
C     NDEC - NUMBER OF DECIMAL PLACES DESIRED
C   LENGTH - FIELD WIDTH FOR ONE DATA VALUE
C    TSDAT - TIME SERIES DATA
10    READ (LUTMP,END=90) STAID,DESC,STATE,DTYPE,UNITOT,IDT,NUMVAL,
     $   NUMR,CONVT,NDEC,LENGTH,(TSDAT(I),I=1,NUMVAL)
C
C  SET ISEG IN WHERE COMMON BLOCK
      CALL UMEMOV (STAID,ISEG,2)
C
C  CHECK IF NEW STATION
      IF (OLDID.NE.STAID) WRITE (IPR,*)
C
C  CHECK IF NEED TO CONVERT DATA
      IF (METRIC.EQ.0.OR.CONVT.EQ.1.0) THEN
         ELSE
            DO 20 I=1,NUMVAL
               IF (IFMSNG(TSDAT(I)).EQ.0) TSDAT(I)=TSDAT(I)/CONVT
20             CONTINUE
         ENDIF
C
C  SET NUMBER OF VALUES PER DAY
      NVDA=24/IDT
C
C  SET NUMBER SPACES USED FOR EACH DAY (WITH 2 SPACES BETWEEN VALUES)
      NXDA=NVDA*(LENGTH+2)
C
C  SET NUMBER OF DAYS PER LINE (CONSIDERING 82 COLUMNS LEFT)
      NDLINE=82/NXDA
C
C  SET NUMBER OF VALUES PER LINE
      NVLINE=NDLINE*NVDA
C
C  CHECK IF MORE THAN ONE LINE REQUIRED FOR A DAY
      IF (NDLINE.NE.0) GO TO 50
C
C  SET NVLINE TO USE ALL SPACES IN A LINE THEN REDUCE IT SO THAT
C  NVDA IS AN EVEN MULTIPLE OF NVLINE
      NVLINE=82/(LENGTH+2)
      NN=NVLINE
      DO 30 N=1,NN
         IF (MOD(NVDA,NVLINE).EQ.0) GO TO 40
         NVLINE=NVLINE-1
30       CONTINUE
40    NXDA=NVLINE*(LENGTH+2)
C
C  SET NUMBER OF LINES NEEDED
50    NLINE=NUMVAL/NVLINE
      IF (MOD(NUMVAL,NVLINE).NE.0) NLINE=NLINE+1
C
C  SET NUMBER OF SPACES TO SKIP BEFORE SEPARATE OBSERVED AND
C  FUTURE DATA
      NXOF=MOD(NUMR,NVLINE)
      IF (NXOF.EQ.0) NXOF=NVLINE
      NXOF=NXOF*(LENGTH+2)+1
C
      NXDA=NXDA-1
C
      IF (IBUG.GE.1) WRITE (IOPDBG,*)
     $   ' STAID=',STAID,
     $   ' DESC=',DESC,
     $   ' STATE=',STATE,
     $   ' DTYPE=',DTYPE,
     $   ' UNITOT=',UNITOT,
     $   ' IDT=',IDT,
     $   ' NUMVAL=',NUMVAL,
     $   ' NUMR=',NUMR,
     $   ' CONVT=',CONVT,
     $   ' NDEC=',NDEC,
     $   ' LENGTH=',LENGTH,
     $   ' NVDA=',NVDA,
     $   ' NXDA=',NXDA,
     $   ' NDLINE=',NDLINE,
     $   ' NVLINE=',NVLINE,
     $   ' NLINE=',NLINE,
     $   ' (TSDAT(I),I=1,NUMVAL)=',(TSDAT(I),I=1,NUMVAL),
     $   ' '
C
C  WRITE STATION INFORMATION
      LINE=' '
      IPOS=1
      NCHAR=LEN(DESC)
      LINE(IPOS:IPOS+NCHAR-1)=DESC
      IPOS=IPOS+NCHAR+2
      NCHAR=LEN(STAID)
      LINE(IPOS:IPOS+NCHAR-1)=STAID
      IPOS=IPOS+NCHAR+2
      NCHAR=LEN(DTYPE)
      LINE(IPOS:IPOS+NCHAR-1)=DTYPE
      IPOS=IPOS+NCHAR+2
      NCHAR=LEN(UNITOT)
      LINE(IPOS:IPOS+NCHAR-1)=UNITOT
      IPOS=IPOS+NCHAR+2
      MCHAR=2
      CALL UINTCH (IDT,MCHAR,STRING,NCHAR,IERR)
      IF (STRING(1:1).EQ.' ') STRING(1:1)='0'
      LINE(IPOS:IPOS+MCHAR-1)=STRING
C
C  SET POINTER TO FIRST VALUE IN TSDAT FOR THIS LINE
      INTS=1
C
C  PRINT EACH LINE
      ILINE=49
      DO 80 N=1,NLINE
C     SET POINTER TO LAST VALUE IN TSDAT FOR THIS LINE
         LATS=INTS+NVLINE-1
         IF (LATS.GT.NUMVAL) THEN
C        END OF DATA - RESET LATS AND SLASH FORMAT
            LATS=NUMVAL
            NDLINE=(LATS-INTS+1)/NVDA
            ENDIF
C      CONVERT DATA FROM REAL TO CHARACTER
         IPOS=ILINE
         NCHAR=LENGTH
         DO 60 I=INTS,LATS
            CALL URELCH (TSDAT(I),NCHAR,LINE(IPOS:IPOS),NDEC,NFILL,
     *         IERR)
            IF (IERR.NE.0) THEN
               WRITE (IPR,120) 'URELCH','TSDAT',I,TSDAT(I)
               CALL WARN
               CALL UREPET ('?',LINE(IPOS:IPOS),NCHAR)
               ENDIF
            IPOS=IPOS+NCHAR+2
60          CONTINUE
C     PRINT SLASHES ONLY FOR MULTIPLE DAYS ON A LINE OR WHEN
C     A DAY THAT ENDS ON THIS LINE
         IF (NDLINE.NE.0.OR.MOD(LATS,NVDA).EQ.0) THEN
            NTIME=NDLINE
            IF (NTIME.EQ.0) NTIME=1
            IPOS=ILINE-1
            DO 70 I=1,NTIME
               IPOS=IPOS+NXDA
               LINE(IPOS:IPOS)='/'
               IPOS=IPOS+1
70             CONTINUE
            ENDIF
C     CHECK IF NEED TO PRINT CHARACTER TO SEPARATE FUTURE DATA
         IF (NUMR.GE.INTS.AND.NUMR.LE.LATS) THEN
            IPOS=ILINE+NXOF-2
            LINE(IPOS:IPOS)='>'
            ENDIF
         WRITE (IPR,'(A,A)') ' ',LINE(1:LENSTR(LINE))
         LINE=' '
         INTS=INTS+NVLINE
80       CONTINUE
C
      OLDID=STAID
      GO TO 10
C
90    CALL FSTWHR (OLDOPN,IOLDOP,OLDOPN,IOLDOP)
      IF (IBUG.GT.0) WRITE (IOPDBG,*) 'EXIT QPRTTS'
C
      RETURN
C
110   FORMAT (
     $   '0',20X,'PERIOD: ',
     $         I2.2,'/',I2.2,'/',I4,'-',I2.2,A4,' THRU ',
     $         I2.2,'/',I2.2,'/',I4,'-',I2.2,A4,2X,
     $      'LAST OBSERVED DATA: ',
     $         I2.2,'/',I2.2,'/',I4,'-',I2.2,A4)
111   FORMAT (
     $   '0',20X,'NOTES : FIRST VALUE IS AT THE INITIAL TIME PLUS ONE ',
     $      'TIME STEP.' /
     $   ' ',20X,8X,'MISSING DATA=-999.  TRAILING DAYS OR TIME SERIES ',
     $      'WITH ALL MISSING NOT DISPLAYED.' /
     $   ' ',20X,8X,'DT=TIME INTERVAL.',3X,
     $      '/ SEPARATES DAYS.',3X,
     $      '> SEPARATES OBSERVED AND FUTURE.')
112   FORMAT (
     $   '0','DESCRIPTION',11X,'ID',8X,'TYPE',2X,
     $       'UNIT',2X,'DT',2X,'TIME SERIES VALUES' /
     $   ' ',20('-'),2X,8('-'),2X,4('-'),2X,
     $        4('-'),2X,2('-'),2X,80('-'))
120   FORMAT ('0**WARNING** ERROR ENCOUNTERED IN ROUTINE ',A,' ',
     $   'CONVERTING ',A,' ARRAY DATA VALUE NUMBER ',I4,'. ',
     $   'VALUE IS ',G10.4,'.')
C
      END
