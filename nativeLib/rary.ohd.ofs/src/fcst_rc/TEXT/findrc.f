C MODULE FINDRC
C-----------------------------------------------------------------------
C
C  ROUTINE TO FIND A RATING CURVE.
C
      SUBROUTINE FINDRC (FRCID,IRCREC,IRCSW)
C
C  SUBROUTINE FINDRC CHECKS FOR A RATING CURVE AND RETURNS THE RECORD
C  IF FOUND OR THE NEXT WRITE LOCATION IF NOT FOUND.
C
C  ARGUMENT LIST:
C      FRCID  - 8-CHARACTER RATING CURVE IDENTIFIER
C      IRCREC - RECORD WHERE RATING CURVE IS LOCATED IF FOUND
C               OR NEXT WRITE POSITION IF CURVE IS NOT FOUND
C      IRCSW  -  0 = CURVE NOT FOUND
C                1 = CURVE IS FOUND
C               -1 = RATING CURVE NOT FOUND BUT NO ROOM FOR ANOTHER
C                    TO BE ADDED
C
C  SUBROUTINE ORIGINALLY WRITTEN BY - JOE OSTROWSKI - HRL - 8/1980
C
      CHARACTER*8 FRCID,FRCIDZ,OLDOPN
C
      DIMENSION IRCZ(3,1),BUF(3),IBUF(3)
      EQUIVALENCE (RCZZ(1,1),IRCZ(1,1)),(IBUF(1),BUF(1))
C
      INCLUDE 'common/ionum'
      INCLUDE 'common/fdbug'
      INCLUDE 'common/errdat'
      INCLUDE 'common/frcptr'
      INCLUDE 'common/fcunit'
C
C    ================================= RCS keyword statements ==========
      CHARACTER*68     RCSKW1,RCSKW2
      DATA             RCSKW1,RCSKW2 /                                 '
     .$Source: /fs/hseb/ob72/rfc/ofs/src/fcst_rc/RCS/findrc.f,v $
     . $',                                                             '
     .$Id: findrc.f,v 1.4 2001/06/13 10:57:55 mgm Exp $
     . $' /
C    ===================================================================
C
C
      IOPNUM=-1
      CALL FSTWHR ('FINDRC  ',IOPNUM,OLDOPN,IOLDOP)
C
      IF (ITRACE.GE.2) WRITE (IODBUG,*) 'ENTER FINDRC'
C
      IBUG=IFBUG('RTCV')
C
      IRCSW=0
      IFNOBS=0
C
      IF (IBUG.GE.1) WRITE (IODBUG,*) 'FRCID=',FRCID
C
      IF (IBUG.GE.1) WRITE (IODBUG,*) 'NRC=',NRC,' MRCF=',MRCF
C
C  CHECK IF ANY RATING CURVES DEFINED
      IF (NRC.EQ.0) GO TO 30
C
      MAXRC=NRC
C
C  CHECK IF MAXIMUM NUMBER OF RATING CURVES THAT CAN BE HELD IN COMMON
C  FRCPTR EXCEEDED
      IF (MAXRC.GT.MRC) MAXRC=MRC
C
C  SEARCH COMMON FRCPTR FOR THE RATING CURVE ID
      DO 10 I=1,MAXRC
         CALL UMEMOV (RCZZ(1,I),FRCIDZ,2)
         IF (FRCID.EQ.FRCIDZ) THEN
            IRCREC=IRCZ(3,I)
            IRCSW=1
            IF (IBUG.GE.1) WRITE (IODBUG,*) 'IRCREC=',IRCREC
            GO TO 50
            ENDIF
         IF (IFNOBS.EQ.0) THEN
C        SAVE FIRST FOUND OBSOLETE RC LOCATION
            IF (FRCIDZ.EQ.'OBSOLETE') THEN
               IFNOBS=1
               LOCOBS=IRCZ(3,I)
               IF (IBUG.GT.0)WRITE (IODBUG,*) 'LOCOBS=',LOCOBS
               ENDIF
             ENDIF
10       CONTINUE
C
C  DEFINED RATING CURVES NOT FOUND IN COMMON BLOCK - CHECK IF MORE
C  CURVES ARE TO BE CHECKED ON THE POINTER FILE
      IF (MAXRC.EQ.NRC) GO TO 30
C
C  START WITH RECORD MRC+2 (2 THRU MRC+1 ARE IN COMMON FRCPTR)
C  AND GO THROUGH NRC+1 (THERE ARE NRC+1 RECORDS ON THE POINTER FILE -
C  THE FIRST RECORD IS CONTROL INFORMATION)
      INR=MRC+2
      NRCLST=NRC+1
      DO 20 I=INR,NRCLST
         CALL UREADT (KFRCPT,I,BUF,ISTAT)
         CALL UMEMOV (BUF,FRCIDZ,2)
         IF (FRCID.EQ.FRCIDZ) THEN
            IRCREC=IBUF(3)
            IRCSW=1
            IF (IBUG.GE.1) WRITE (IODBUG,*) 'IRCREC=',IRCREC
            GO TO 50
            ENDIF
         IF (IFNOBS.EQ.0) THEN
C        SAVE FIRST FOUND OBSOLETE RC LOCATION
            IF (FRCIDZ.EQ.'OBSOLETE') THEN
               IFNOBS=1
               LOCOBS=IBUF(3)
               IF (IBUG.GT.0) WRITE (IODBUG,*) 'LOCOBS=',LOCOBS
               ENDIF
            ENDIF
20       CONTINUE
C
C  RATING CURVE NOT FOUND - SET NEXT AVAILABLE RECORD
30    IRCREC=NRC+1
C
C  IF ADDING ANOTHER CURVE TO THE RATING CURVE FILE EXCEEDS THE MAXIMUM
C  ALLOWED, SET SWITCH TO -1.
      IF (IRCREC.GT.MRCF) GO TO 40
         IRCSW=0
         GO TO 50
C
C  NOT ENOUGH ROOM IN FILE
40    IRCSW=-1
      IRCREC=-1
      GO TO 50
C
50    IF (ITRACE.GE.2) WRITE (IODBUG,*) 'EXIT FINDRC'
C
      CALL FSTWHR (OLDOPN,IOLDOP,OLDOPN,IOLDOP)
C
      RETURN
C
      END
