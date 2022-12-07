#!/bin/bash


#no input is provided, the number o steps are hardcoded

#alias runCoverage='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/media/soha/DATA/git/jrTCG/lib TARGET_CLASSPATH_WALA=/media/soha/DATA/git/jrTCG/build/examples/ java -Djava.library.path=/media/soha/DATA/git/jrTCG/lib  -ea -Xms20000m -Xmx20000m -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+UseCompressedOops -XX:PermSize=512m -XX:MaxPermSize=512m -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

alias runCoverage='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/media/soha/DATA/git/jrTCG/lib TARGET_CLASSPATH_WALA=/media/soha/DATA/git/jrTCG/build/examples/ java -Djava.library.path=/media/soha/DATA/git/jrTCG/lib  -ea -Xmx12000m -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

shopt -s expand_aliases


COVERAGEDIR=/media/soha/DATA/git/jrTCG

MAX_STEPS=1 && export MAX_STEPS

echo "maxsteps is $MAX_STEPS"

mkdir $COVERAGEDIR/logs/log_cli
mkdir $COVERAGEDIR/logs/log_cli/partialproblem

#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheCollect_Path.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheCollect_Path_steps$MAX_STEPS.log \
#&& echo "SPF path finished" \


#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheJR_Collect.mode2.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheJRCollect.mode2_steps$MAX_STEPS.log \
#&& echo "JR mode2 onTheGoON finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheJR_Collect.mode2PlainJR.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheJRCollect.mode2PlainJR_steps$MAX_STEPS.log \
#&& echo "JR mode2 PlainJR finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheJR_Collect.mode3.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheJRCollect.mode3_steps$MAX_STEPS.log \
#&& echo "JR mode3 onTheGoON finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheJR_Collect.mode3PlainJR.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheJRCollect.mode3PlainJR_steps$MAX_STEPS.log \
#&& echo "JR mode3 PlainJR finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheJR_Collect.mode4.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheJRCollect.mode4_steps$MAX_STEPS.log \
#&& echo "JR mode4 onTheGoON finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheJR_Collect.mode4PlainJR.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheJRCollect.mode4PlainJR_steps$MAX_STEPS.log \
#&& echo "JR mode4 PlainJR finished" \

for i in {1..3}
do
  sleep 10
  date
  runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheCollect.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheCollect_steps$MAX_STEPS.log
  echo "SPF branch onTheGoON finished"
done

date

for i in {1..3}
do
  sleep 10
  date
  runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheJR_Collect.mode5.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheJRCollect.mode5_steps$MAX_STEPS.log
  echo "JR mode5 onTheGoON finished"
done

date

for i in {1..3}
do
  sleep 10
  date
  runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/apachecli/partialproblem/apacheJR_Collect.mode5PlainJR.jpf >& $COVERAGEDIR/logs/log_cli/partialproblem/apacheJRCollect.mode5PlainJR_steps$MAX_STEPS.log
  echo "JR mode5 PlainJR finished"
done

date