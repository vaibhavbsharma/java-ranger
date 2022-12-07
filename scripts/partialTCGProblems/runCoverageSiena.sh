#!/bin/bash


#no input is provided, the number o steps are hardcoded


alias runCoverage='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/media/soha/DATA/git/jrTCG/lib TARGET_CLASSPATH_WALA=/media/soha/DATA/git/jrTCG/build/examples/ java -Djava.library.path=/media/soha/DATA/git/jrTCG/lib  -ea -Xmx12000m -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '


shopt -s expand_aliases


COVERAGEDIR=/media/soha/DATA/git/jrTCG

MAX_STEPS=1 && export MAX_STEPS

echo "maxsteps is $MAX_STEPS"

mkdir $COVERAGEDIR/logs/log_siena
mkdir $COVERAGEDIR/logs/log_siena/partialproblem

#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaCollect_Path.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaCollect_Path_steps$MAX_STEPS.log \
#&& echo "SPF path finished" \


#runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaCollect.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaCollect_steps$MAX_STEPS.log \
#&& echo "SPF branch onTheGoON finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode2.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode2_steps$MAX_STEPS.log \
#&& echo "JR mode2 onTheGoON finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode2PlainJR.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode2PlainJR_steps$MAX_STEPS.log \
#&& echo "JR mode2 PlainJR finished"\
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode3.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode3_steps$MAX_STEPS.log \
#&& echo "JR mode3 onTheGoON finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode3PlainJR.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode3PlainJR_steps$MAX_STEPS.log \
#&& echo "JR mode3 PlainJR finished"\
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode4.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode4_steps$MAX_STEPS.log \
#&& echo "JR mode4 onTheGoON finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode4PlainJR.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode4PlainJR_steps$MAX_STEPS.log \
#&& echo "JR mode4 PlainJR finished"\
#sleep 10  \
#; date \
#; runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaCollect.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaCollect_steps$MAX_STEPS.log \
#; echo "SPF branch onTheGoON finished" \
#; date \
#; sleep 10  \
#; date \
#; runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaCollect.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaCollect_steps$MAX_STEPS.log \
#; echo "SPF branch onTheGoON finished" \
#; date \
#; sleep 10  \
#; date \
#; runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode5.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode5_steps$MAX_STEPS.log \
#; echo "JR mode5 onTheGoON finished" \
#; date \
#; sleep 10  \
#; date \
#; runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode5.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode5_steps$MAX_STEPS.log \
#; echo "JR mode5 onTheGoON finished" \
#; date \
#; sleep 10  \
#; date \
#; runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode5PlainJR.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode5PlainJR_steps$MAX_STEPS.log \
#; echo "JR mode5 PlainJR finished" \
#; date \
#; sleep 10  \
#; date \
#; runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode5PlainJR.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode5PlainJR_steps$MAX_STEPS.log \
#; echo "JR mode5 PlainJR finished" \
#; date



for i in {1..3} ; do
  sleep 10
  date
  runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaCollect.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaCollect_steps$MAX_STEPS.log
  echo "SPF branch onTheGoON finished"
done

date

for i in {1..3} ; do
  sleep 10
  date
  runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode5.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode5_steps$MAX_STEPS.log
  echo "JR mode5 onTheGoON finished"
done

date

for i in {1..3} ; do
  sleep 10
  date
  runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/siena/partialproblem/sienaJR_Collect.mode5PlainJR.jpf >& $COVERAGEDIR/logs/log_siena/partialproblem/sienaJRCollect.mode5PlainJR_steps$MAX_STEPS.log
  echo "JR mode5 PlainJR finished"
done

date