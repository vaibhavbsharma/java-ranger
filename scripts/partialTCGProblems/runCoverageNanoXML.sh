#!/bin/bash


#no input is provided, the number o steps are hardcoded


alias runCoverage='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/media/soha/DATA/git/jrTCG/lib TARGET_CLASSPATH_WALA=/media/soha/DATA/git/jrTCG/build/examples/ java -Djava.library.path=/media/soha/DATA/git/jrTCG/lib  -ea -Xmx12000m -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '


shopt -s expand_aliases


COVERAGEDIR=/media/soha/DATA/git/jrTCG

MAX_STEPS=1 && export MAX_STEPS

echo "maxsteps is $MAX_STEPS"

mkdir $COVERAGEDIR/logs/log_dumpxml
mkdir $COVERAGEDIR/logs/log_dumpxml/partialproblem

#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLCollect_Path.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLCollect_Path_steps$MAX_STEPS.log \
#&& echo "SPF path finished" \
#
runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode2.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode2_steps$MAX_STEPS.log \
&& echo "JR mode2 onTheGoON finished" \
&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode2_onTheGoOFF.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode2_onTheGoOFF_steps$MAX_STEPS.log \
&& echo "JR mode2 onTheGoOFF finished" \
&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode2PlainJR.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode2PlainJR_steps$MAX_STEPS.log \
&& echo "JR mode2 PlainJR finished" \
&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode4_onTheGoOFF.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode4_onTheGoOFF_steps$MAX_STEPS.log \
&& echo "JR mode4 onTheGoOFF finished" \
&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode4.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode4_steps$MAX_STEPS.log \
&& echo "JR mode4 onTheGoON finished" \
&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode4PlainJR.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode4PlainJR_steps$MAX_STEPS.log \
&& echo "JR mode4 PlainJR finished" \
&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode5_onTheGoOFF.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode5_onTheGoOFF_steps$MAX_STEPS.log \
&& echo "JR mode5 onTheGoOFF finished" \
&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode5.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode5_steps$MAX_STEPS.log \
&& echo "JR mode5 onTheGoON finished" \
&&runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode5PlainJR.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode5PlainJR_steps$MAX_STEPS.log \
&& echo "JR mode5 PlainJR finished"
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode3PlainJR.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode3PlainJR_steps$MAX_STEPS.log \
#&& echo "JR mode3 PlainJR finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode3_onTheGoOFF.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode3_onTheGoOFF_steps$MAX_STEPS.log \
#&& echo "JR mode3 onTheGoOFF finished" \
#&& runCoverage $COVERAGEDIR/src/examples/tcgbenchmarks/runconfig/nanoxml/partialproblem/NanoXMLJR_Collect.mode3.jpf >& $COVERAGEDIR/logs/log_dumpxml/partialproblem/nanoXMLJRCollect.mode3_steps$MAX_STEPS.log \
#&& echo "JR mode3 onTheGoON finished" \

