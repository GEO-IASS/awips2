C  =====================================================================
C DESC -- THIS SUBROUTINE PLOTS THE HYDROGRAPH
C
C  =====================================================================
      SUBROUTINE PLOT55(T1,LTT1,TII,QTC,LTQTC,STC,LTSTC,FS,FFS,NGAGE,
     . STM,ST0,QT0,KU,KKP,FLDH,KTIMI,KHR,KDA,KMO,KYR,ISTRT,INOW,IGAG,
     . GZERO,KSTG,K1,K28)
C
      INCLUDE 'common/fdbug'
      INCLUDE 'common/fnopr'
      INCLUDE 'common/fpltab'
cc      COMMON/COPER/ISTRT,INOW,IEND
      COMMON/M655/KTIME,DTHYD,J1
      COMMON/M3255/IOBS,KTERM,KPL,JNK,TEH
      COMMON/METR55/METRIC
      COMMON/NPC55/NP,NPST,NPEND
      COMMON/TPL55/DTHPLT
      COMMON/IONUM/IN,IPR,IPU
C
      DIMENSION T1(*),LTT1(*),TII(*),QTC(*),LTQTC(*),FS(*),FFS(*)
      DIMENSION STC(*),LTSTC(*),SCALE (11),ORD(101),NGAGE(K1)
      DIMENSION STM(K1),ST0(K28),QT0(K28),KU(K1)
C
C    ================================= RCS keyword statements ==========
      CHARACTER*68     RCSKW1,RCSKW2
      DATA             RCSKW1,RCSKW2 /                                 '
     .$Source: /fs/hseb/ob72/rfc/ofs/src/fcst_fldwav/RCS/plot55.f,v $
     . $',                                                             '
     .$Id: plot55.f,v 1.4 2004/02/02 21:53:46 jgofus Exp $
     . $' /
C    ===================================================================
C

      CHARACTER*8 SNAME
C
C
      DATA DOT,BLANK,ASTER,PLUS/1h.,1h ,1h*,1h+/
      DATA DOLAR,DASH,COLON/1h$,1h-,1h:/
      DATA EXCLAM/1h!/
      DATA SNAME/8hPLOT55  /
C    ===================================================================
C  KKP =1 PLOT COMPUTED RESULTS ONLY
C      =0 PLOT BOTH OBSERVED DATA AND COMPUTED RESULTS
C
C
C
      CALL FPRBUG(SNAME,1,55,IBUG)
C


      IF(METRIC.EQ.0) THEN
        FT=1.
        CFS=1.
      ELSE
        FT=3.281
        CFS=35.32
      ENDIF
      KX=IGAG
      M=0
      IF(DTHPLT.GE.1.0) M=1

      IDHF=DTHYD+0.0001
cc      FLDHT=FLDH-GZERO
      FLDHT=FLDH
      STMN=STM(J1)
      IF(KU(J1).EQ.1.AND.IGAG.EQ.1) STMN=STMN-GZERO
      KTIMEE=KTIME
      IF(NOPROT.EQ.1.OR.IPLHY.NE.1) GO TO 8000

C     WRITE(6,995) KTIME,KHR,KDA,KMO,KYR,ISTRT,INOW
C 995 FORMAT(10X,'KTIME KHR KDA KMO KYR   ISTRT    INOW ='/10X,I5,4I4,
C    1 2I8)
C     WRITE(6,998) (FS(I),I=1,KTIMEE)
C 998 FORMAT(2X,'FS=',10F12.2)
C     WRITE(6,997) (FFS(I),I=1,KTIMEE)
C 997 FORMAT(1X,'FFS=',10F12.2)

      MINN=0
      T=0.
      FMAX=-20000000.
      FMIN=20000000.
      ZE=0.
      LKK=1
      IF(IOBS.LE.0) KKP=1
      IF(DTHYD.GT.0.) THEN
        LKK=DTHPLT/DTHYD+0.0001
        KME=KTIMEE*LKK
        KTME=KTIMEE
        IF(KME.GT.KTIMEE) KTME=KME
      ENDIF
      IF(KKP.NE.0) KTME=KTIMEE
      ISPLT=KTME/4
C     IF(LKK.LT.1) ISPLT=ISPLT+(DTHYD/DTHPLT-1.0)*ISPLT+0.0001
C     IF(KKP.EQ.1) ISPLT=KTME/4
      IDASH=1
      KXJ1=LCAT21(KX,J1,NGAGE)
      LSTC=LTSTC(KXJ1)-1
      LQTC=LTQTC(KXJ1)-1
      LT1=LTT1(J1)-1

C     WRITE(6,991) (STC(I+LSTC),I=1,KTIME)
C 991 FORMAT(2X,'STC=',10F12.2)
C     WRITE(6,992) (QTC(I+LQTC),I=1,KTIME)
C 992 FORMAT(1X,'QTC=',10F12.2)

      KKI=1
      KI=0
      NKI=1

      DO 40 K=1,KTIMEE
        IK=1
        IF(KKP.NE.1.AND.DTHPLT.GE.DTHYD) THEN
          IF(FS(K).GT.FMAX) FMAX=FS(K)
          IF(FS(K).LT.FMIN) FMIN=FS(K)
        ELSE
          IF(KKP.NE.1.AND.K.EQ.NKI) THEN
            KI=KI+1
            IF(KKP.NE.1) IK=IFMSNG(FFS(KI))
            NKI=KI*DTHYD/DTHPLT+1.001
          ENDIF
          IF(FS(K).GT.FMAX) FMAX=FS(K)
          IF(KKP.NE.1.AND.IK.NE.1) THEN
            IF(FFS(KI).GT.FMAX) FMAX=FFS(KI)
          ENDIF
          IF(FS(K).LT.FMIN) FMIN=FS(K)
          IF(KKP.NE.1.AND.IK.NE.1) THEN
            IF(FFS(KI).LT.FMIN) FMIN=FFS(KI)
          ENDIF
        ENDIF
   40 CONTINUE

      IF(FMIN.LE.STMN.AND.IGAG.EQ.1) MINN=1
      IF(KU(J1).EQ.1.AND.KPL.EQ.2) MINN=0
      IF(KU(J1).EQ.2.AND.KPL.NE.2) MINN=0

      IF(KKP.NE.1.AND.DTHPLT.GE.DTHYD) THEN
        DO 45 K=1,KTME
          IK=IFMSNG(FFS(K))
          IF(IK.NE.1) THEN
            IF(FFS(K).GT.FMAX) FMAX=FFS(K)
            IF(FFS(K).LT.FMIN) FMIN=FFS(K)
          ENDIF
   45   CONTINUE
      ENDIF

      RG=FMAX-FMIN
      KG=0
      IF(FMIN.GE.ZE.AND.FMAX.GE.ZE) KG=1
      IF(FMIN.LE.ZE.AND.FMAX.LE.ZE) KG=1

      I = 1
      XP = 10.0
   60 IF((RG/XP).LE.10.0.OR.I.GT.6) GOTO 70
        XP = XP*10.0
        I  = I+1
        GOTO 60
   70 CONTINUE

      MP=RG/XP+1.3
      IRG=MP*XP
      AIRG=IRG
      DPL=IRG*0.01
      IF(KG.GT.0) THEN
        IMIN=FMIN
        IF(FMIN.LT.0.) IMIN=IMIN-1
        FMIN=IMIN
      ELSE
        IMIN=FMIN-2.5
        IMAX=IRG+IMIN
        FMAX=IMAX
        KK=(AIRG-FMAX)/AIRG*100.+0.1
        KK=KK+1
        FMIN=IMIN
      ENDIF

      DO 110 I=1,11
        SCALE(I)=IMIN+(I-1)*DPL*10.
  110 CONTINUE

      IF(KPL.EQ.1.OR.KPL.EQ.3) WRITE(IPR,260)SCALE
      IF(KPL.EQ.2) WRITE(IPR,261)SCALE
      KI=0
      NKI=1
      LKI=1
      MP=1
      IF(NP.EQ.0) MP=0
C
C        DETERMINE INITIAL DAY AND HOUR
C
      DTIM=DTHPLT
      IF(KKP.NE.1.AND.DTHPLT.GE.DTHYD) DTIM=DTHYD
      LTIM=DTIM+.0001
      LDA=KDA
cc      LHR=KHR-IDHF
      LHR=KHR
      IF(LTIM.GE.KHR) THEN
        LDA=KDA-1
        LHR=LHR+24
      ENDIF
C
C        DETERMINE THE CORRECT MONTH
C
      LMO = KMO
      CALL DDGCDM(KYR,LMO,KMX)

      IT1=1
C
C          PLOT THE MINIMUM ALLOWABLE POINT
C
      IF(MINN.NE.0) THEN
        IF(KG.GT.0) LMN=(STMN-FMIN)/AIRG*100.+1.5
        IF(KG.LE.0) THEN
          IF(STMN.LT.0.) LMN=KK-STMN/FMIN*KK
          IF(STMN.GE.0.) LMN=KK+STMN/FMAX*(101-KK)
        ENDIF
        ORD(LMN)=COLON
      ENDIF

c      WRITE(6,9997) KKP,LKI,LDA,LHR,LTIM,LMO,KG,LMN
c 9997 FORMAT(2X,'BEFORE DO 40  KKP LKI LDA LHR LTIM LMO  KG LMN='/
c     *       2X,'             ',4I4,I5,3I4)

      DO 240 K=1,KTIMEE
      IF(K.GT.0) T=TII(K)
      IF(KKP.EQ.1) GO TO 112
      IF(DTHPLT.GE.DTHYD) GO TO 111
      IF(K.NE.NKI) GO TO 112
      KI=KI+1
      IF(KKP.NE.1) IK=IFMSNG(FFS(KI))
      LKI=NKI
      NKI=KI*DTHYD/DTHPLT+1.001
      GO TO 112
  111 KI=KI+1
      IF(KKP.NE.1) IK=IFMSNG(FFS(KI))
      IF(KI.GT.0) T=T1(KI+LT1)
  112 HR=T

      IF(K.NE.0.OR.KI.NE.0) THEN
        LHR=KHR-DTHYD+HR+0.0001
CC      LHR=MOD(LHR,24)
        LLHR = LHR - LHR/24 * 24
C       WRITE(6,889) LLHR,LHR,KHR,DTHYD,HR
C 889   FORMAT(5X,'====== LLHR,LHR,KHR,DTHYD,HR=',3I5,2F10.2,1X,5(1H=))
        LHR=LLHR
        IF(LHR.LT.1) LHR=LHR+24
        IF(LHR.LE.LTIM) LDA=LDA+1
      ENDIF

c      WRITE(6,888) LHR,KI,T,DTHPLT,DTHYD
c  888 FORMAT(5X,' LHR  KI         T      DTHPLT       DTHYD='/5X,
c     1 2I4,F10.2,2F10.7)

      IF(KMX.LE.0) CALL DDGCDM(KYR,LMO,KMX)

      IF(LDA.GT.KMX) THEN
        KMX=0
        LDA=1
        LMO=LMO+1
        IF(LMO.GT.12) LMO=1
      ENDIF
      MINS=ISTRT+(K-1)*DTIM+0.0001
C
C     WRITE(6,9998) KXJ1,ST0(KXJ1),QT0(KXJ1)
C9998 FORMAT(2X,'KXJ1=',I10,3X,'ST0=',F10.2,3X,'QT0=',F10.0)

      IF(K.LE.0) THEN
        ST=ST0(KXJ1)/FT
        QT=QT0(KXJ1)/CFS
      ELSE
        IF(KKP.EQ.0) THEN
          ST=(STC(K+LSTC))/FT
          QT=(QTC(K+LQTC))/CFS
        ELSE
          ST=(TERP21(T,KTIMI,IT1,IT2,TII,STC,LSTC))/FT
          QT=(TERP21(T,KTIMI,IT1,IT2,TII,QTC,LQTC))/CFS
        ENDIF
      ENDIF

cc      IF(KPL.EQ.3) ST=ST-GZERO
        IF(KSTG.EQ.1) ST=ST-GZERO
        KFLDHT = (FLDHT-SCALE(1))/(SCALE(11)-SCALE(1))*101 + 1

c      WRITE(6,996) ST,QT,K,LSTC,LQTC,KKP
c  996 FORMAT(1X,'      ST      QT    K LSTC LQTC KKP='/1X,2F8.2,3I5,I4)

      DO 120 I=2,100
      ORD(I) = BLANK
  120 CONTINUE
      DO 130 I=1,101,10
      ORD(I) = DOT
  130 CONTINUE

c      WRITE(6,9999) KKP,K,KI,KKI,LKI,LHR
c 9999 FORMAT(2X,'AT LABEL 130  KKP,K,KI,KKI,LKI,LHR=',6I10)

      IF(NP.LT.1) THEN
        IF(MINS.LT.INOW) IDASH=0
        IF(MINS.GT.INOW)KKP=1
        IF(KPL.EQ.1.AND.KFLDHT.GE.1.AND.KFLDHT.LE.101)
     .     ORD(KFLDHT)=EXCLAM
        IF(IDASH.EQ.1) THEN
          DO 135 N=1,101
          ORD(N)=DASH
  135     CONTINUE
          IDASH=2
        ENDIF
      ENDIF

      IF(KG.GT.0) THEN
        LS=(FS(K)-FMIN)/AIRG*100.+1.5
        ORD(LS)=ASTER
        IF(KKP.EQ.1) GO TO 235
        IF(IK.NE.1) THEN
          LA=(FFS(KI)-FMIN)/AIRG*100.+1.5
          ORD(LA)=PLUS
        ENDIF
cc        IF(IK.EQ.1) ORD(LA)=BLANK
        GO TO 230
      ENDIF

      ORD(KK)=DOLAR
      IF(FS(K).GE.0.0) THEN
        LS=FS(K)/FMAX*(101-KK)
        LS=KK+LS
        ORD(LS)=ASTER
      ELSE
        LS=FS(K)/FMIN*KK
        LS=KK-LS
        ORD(LS)=ASTER
      ENDIF

      IF(KKP.EQ.1) GO TO 235
      IF(FFS(KI).GE.0.0) THEN
        IF(IK.NE.1) THEN
          LA=FFS(KI)/FMAX*(101-KK)
          LA=KK+LA
          ORD(LA)=PLUS
        ENDIF
CC        IF(IK.EQ.1) ORD(LA)=BLANK
      ELSE
        IF(IK.NE.1) THEN
          LA=FFS(KI)/FMIN*KK
          LA=KK-LA
          ORD(LA)=PLUS
        ENDIF
cc        IF(IK.EQ.1) ORD(LA)=BLANK
      ENDIF
  230 IF(DTHPLT.LT.DTHYD) GO TO 233
      IF(KI.EQ.LKI) GO TO 232
      ORD(LS)=BLANK
      WRITE(IPR,272) LMO,LDA,LHR,ORD,FFS(KI)
      IF(IDASH.EQ.0) IDASH=1
      IF(KI.NE.KKI.AND.KI+1.NE.LKI) GO TO 111
      IF(KI.NE.KKI) GO TO 240
      T11=0.
      IF(K.GT.1) T11=T1(KI+LT1)
CC      WRITE(IPR,280) T11
      GO TO 234
  232 LKI=LKI+LKK
CC      IF(KPL.EQ.3) ST=ST-GZERO
c-----------------------------------------------------------------------
c.......m=1 ==> plotting time step < 1 hour
c.......mp=0 ==> simulation mode (np=0)
c..........................................
      IF(MP.EQ.0.AND.M.EQ.0) WRITE(IPR,295) T,ORD,(QT/1000.),ST,FFS(KI)
      IF(MP.EQ.0.AND.M.EQ.1) WRITE(IPR,275) LMO,LDA,LHR,ORD,
     * (QT/1000.),ST,FFS(KI),T
      IF(MP.GE.1.AND.M.EQ.0) WRITE(IPR,295) T,ORD,(XQTC/1000.),FS(K),
     * FFS(KI)
      IF(MP.GE.1.AND.M.EQ.1) WRITE(IPR,275) LMO,LDA,LHR,ORD,
     *  (QT/1000.),FS(K),FFS(KI),T
cc      WRITE(IPR,275) LMO,LDA,LHR,ORD,QT,ST,FFS(KI)
C     WRITE(IPR,999) ORD,QT,ST,FFS(KI)
C 999 FORMAT(1X,101A1,F7.3,1X,F7.2,1X,F7.2)
      IF(IDASH.EQ.0) IDASH=1
      IF(KI.NE.KKI.AND.KI+1.NE.LKI) GO TO 111
      IF(KI.NE.KKI) GO TO 240
      T11=0.
      IF(K.GT.1) T11=T1(KI+LT1)
cc      WRITE(IPR,280) T11
      GO TO 234
  233 IF(K.NE.LKI.AND.IK.NE.1) ORD(LA)=BLANK
      IF(MP.EQ.0.AND.LKI.EQ.K.AND.M.EQ.0) WRITE(IPR,290) T,
     . ORD,(QT/1000.),ST,FFS(KI)
      IF(MP.EQ.0.AND.LKI.EQ.K.AND.M.EQ.1) WRITE(IPR,270) LMO,LDA,LHR,
     . ORD,(QT/1000.),ST,FFS(KI),T
      IF(MP.EQ.0.AND.LKI.NE.K.AND.M.EQ.0) WRITE(IPR,290) T,
     . ORD,(QT/1000.),ST
      IF(MP.EQ.0.AND.LKI.NE.K.AND.M.EQ.1) WRITE(IPR,271) LMO,LDA,LHR,
     . ORD,(QT/1000.),ST,T
      IF(MP.EQ.1.AND.M.EQ.0) WRITE(IPR,275) T,ORD,(QT/1000.),FS(K),
     . FFS(KI)
      IF(MP.GE.1.AND.M.EQ.1) WRITE(IPR,275) LMO,IDA,LHR,ORD,
     . (QT/1000.),FS(K),FFS(KI),T
      IF(NP.EQ.0.AND.LKI.EQ.K) WRITE(IPR,270) LMO,LDA,LHR,ORD,
     . QT,ST,FFS(KI),T
      IF(NP.EQ.0.AND.LKI.NE.K) WRITE(IPR,271) LMO,LDA,LHR,ORD,
     . QT,ST,T
CC      IF(NP.GE.1) WRITE(IPR,275) LMO,LDA,LHR,ORD,QT,FS(K),FFS(KI),T
      IF(IDASH.EQ.0) IDASH=1
      IF(K.NE.KKI) GO TO 240
      TIME=0.
      IF(K.GT.1) TIME=T1(KI+LT1)
      IF(K.NE.LKI) TIME=TIME+DTHPLT*(K-LKI)
cc      WRITE(IPR,280) TIME
  234 KKI=KKI+ISPLT
      IF(KKI.GT.KTME) KKI=KTME
      IF(DTHPLT.GE.DTHYD.AND.KI+1.NE.LKI) GO TO 111
      GO TO 240

CC  235 WRITE(IPR,270) LMO,LDA,LHR,ORD,QT,ST
  235 IF(M.EQ.0)WRITE(IPR,290) T,ORD,(QT/1000.),ST
      IF(M.EQ.1)WRITE(IPR,271) LMO,LDA,LHR,ORD,(QT/1000.),ST,T
      IF(K.EQ.KKI) THEN
cc        WRITE(IPR,280) T
        KKI=KKI+ISPLT
        IF(KKI.GT.KTME) KKI=KTME
      ENDIF
  240 CONTINUE

  260 FORMAT(/5H TIME,F5.0,10F10.0,'  Q-FCST  H-FCST   H-OBS      TIME')
  261 FORMAT(/5H TIME,F5.0,10F10.0,'  Q-FCST  H-FCST   Q-OBS      TIME')
  270 FORMAT(1X,I2,1H/,I2,1H/,I2,101A1,F8.2,F8.2,F8.2,F10.3)
  271 FORMAT(1X,I2,1H/,I2,1H/,I2,101A1,F8.2,F8.2,8X,F10.3)
  272 FORMAT(1X,I2,1H/,I2,1H/,I2,101A1,16X,F8.2)
  275 FORMAT(1X,I2,1H/,I2,1H/,I2,101A1,F8.2,F8.2,F8.2,F10.3)
  280 FORMAT(1H+,98X,F6.1,5H HRS.)
  290 FORMAT(1X,F8.2,101A1,F8.2,F8.2,F8.2)
  295 FORMAT(1X,F8.2,101A1,F8.2,F8.2,F8.2)

CC  260 FORMAT(5H TIME,F5.0,10F10.0,' Q-FCST  H-FCST    H-OBS')
CC  261 FORMAT(5H TIME,F5.0,10F10.0,' Q-FCST  H-FCST    Q-OBS')
CC  270 FORMAT(1X,I2,1H/,I2,1H/,I2,101A1,F7.3,2(1X,F7.2))
CC  272 FORMAT(1X,I2,1H/,I2,1H/,I2,101A1,16X,F7.2)
CC  275 FORMAT(1X,I2,1H/,I2,1H/,I2,101A1,F7.3,2(1X,F7.2))
CC  280 FORMAT(1H+,98X,F6.1,5H HRS.)
C
 8000 IF(ITRACE.EQ.1) WRITE(IODBUG,9000) SNAME
 9000 FORMAT(1H0,2H**,1X,2A4,8H EXITED.)
      RETURN
      END
