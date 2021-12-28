#!/bin/bash

#no input is provided, the number o steps are hardcoded


alias runCoverage='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/media/soha/DATA/git/jrTCG/lib TARGET_CLASSPATH_WALA=/media/soha/DATA/git/jrTCG/build/examples/ java -Djava.library.path=/media/soha/DATA/git/jrTCG/lib  -ea -Xmx5000m -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '


shopt -s expand_aliases


COVERAGEDIR=/media/soha/DATA/git/jrTCG

MAX_STEPS=1 && export MAX_STEPS

echo "maxsteps is $MAX_STEPS"

mkdir $COVERAGEDIR/logs/log_replace11

runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/replace/replaceCollect.jpf >& $COVERAGEDIR/logs/log_replace11/replaceCollect_steps$MAX_STEPS.log


runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/replace/replaceJR_Collect.mode2.jpf >& $COVERAGEDIR/logs/log_replace11/replaceJR_Collect.mode2_steps$MAX_STEPS.log
runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/replace/replaceJR_Collect.mode3.jpf >& $COVERAGEDIR/logs/log_replace11/replaceJR_Collect.mode3_steps$MAX_STEPS.log
runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/replace/replaceJR_Collect.mode4.jpf >& $COVERAGEDIR/logs/log_replace11/replaceJR_Collect.mode4_steps$MAX_STEPS.log
runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/replace/replaceJR_Collect.mode5.jpf >& $COVERAGEDIR/logs/log_replace11/replaceJR_Collect.mode5_steps$MAX_STEPS.log

runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/replace/replaceJR_Collect.mode3NoBatch.jpf >& $COVERAGEDIR/logs/log_replace11/replaceJR_Collect.mode3NoBatch_steps$MAX_STEPS.log
runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/replace/replaceJR_Collect.mode3PlainJR.jpf >& $COVERAGEDIR/logs/log_replace11/replaceJR_Collect.mode3PlainJR_steps$MAX_STEPS.log
