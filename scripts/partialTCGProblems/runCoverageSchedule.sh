#!/bin/bash


#no input is provided, the number o steps are hardcoded


alias runCoverage='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/media/soha/DATA/git/jrTCG/lib TARGET_CLASSPATH_WALA=/media/soha/DATA/git/jrTCG/build/examples/ java -Djava.library.path=/media/soha/DATA/git/jrTCG/lib  -ea -Xmx5000m -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '


shopt -s expand_aliases


COVERAGEDIR=/media/soha/DATA/git/jrTCG

MAX_STEPS=2 && export MAX_STEPS

echo "maxsteps is $MAX_STEPS"

mkdir $COVERAGEDIR/logs/log_schedule2
mkdir $COVERAGEDIR/logs/log_schedule2/partialproblem

runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/schedule/partialproblem/scheduleCollect.jpf>& $COVERAGEDIR/logs/log_schedule2/partialproblem/scheduleCollect_steps$MAX_STEPS.log

runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/schedule/partialproblem/scheduleJR_Collect.mode2.jpf >& $COVERAGEDIR/logs/log_schedule2/partialproblem/scheduleJRCollect.mode2_steps$MAX_STEPS.log
runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/schedule/partialproblem/scheduleJR_Collect.mode3.jpf >& $COVERAGEDIR/logs/log_schedule2/partialproblem/scheduleJRCollect.mode3_steps$MAX_STEPS.log
runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/schedule/partialproblem/scheduleJR_Collect.mode4.jpf >& $COVERAGEDIR/logs/log_schedule2/partialproblem/scheduleJRCollect.mode4_steps$MAX_STEPS.log
runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/schedule/partialproblem/scheduleJR_Collect.mode5.jpf >& $COVERAGEDIR/logs/log_schedule2/partialproblem/scheduleJRCollect.mode5_steps$MAX_STEPS.log
