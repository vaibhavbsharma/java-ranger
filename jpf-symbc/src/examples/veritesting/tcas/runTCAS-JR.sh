#!/bin/bash

JRDIR=$(pwd)/../../../../
JPF_CORE_DIR=$JRDIR/../jpf-core
alias runSPF-tcas='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JRDIR/lib TARGET_CLASSPATH_WALA=$JRDIR/build/examples/ java -Djava.library.path=$JRDIR/lib -Xmx12288m -ea -Dfile.encoding=UTF-8 -jar $JPF_CORE_DIR/build/RunJPF.jar '
shopt -s expand_aliases

# run tcas with 1 step for 5 modes
#MAX_STEPS=1 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.log
#MAX_STEPS=1 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode2.jpf >& $JRDIR/logs/tcas.mode2.log
#MAX_STEPS=1 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode3.jpf >& $JRDIR/logs/tcas.mode3.log
#MAX_STEPS=1 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode4.jpf >& $JRDIR/logs/tcas.mode4.log
#MAX_STEPS=1 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode4.jpf >& $JRDIR/logs/tcas.mode4.log
#MAX_STEPS=1 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.log


TIMEOUT_MINS=720 && export TIMEOUT_MINS
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JRDIR/lib && export LD_LIBRARY_PATH
TARGET_CLASSPATH_WALA=$JRDIR/build/tcas/ && export TARGET_CLASSPATH_WALA


# run tcas with up to 10 steps in mode 5
#echo "Running 1 step - mode 5"
#MAX_STEPS=1 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
#echo "Running 2 step - mode 5"
#MAX_STEPS=2 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
#echo "Running 3 step - mode 5"
#MAX_STEPS=3 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
#echo "Running 4 step - mode 5"
#MAX_STEPS=4 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
#echo "Running 5 step - mode 5"
#MAX_STEPS=5 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
#echo "Running 6 step - mode 5"
#MAX_STEPS=6 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
#echo "Running 7 step - mode 5"
#MAX_STEPS=7 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
#echo "Running 8 step - mode 5"
#MAX_STEPS=8 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
#echo "Running 9 step - mode 5"
#MAX_STEPS=9 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.mode5.$(($MAX_STEPS))step.log
echo "Running 10 step - mode 5"
MAX_STEPS=10 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode5.jpf >& $JRDIR/logs/tcas.$(($MAX_STEPS))step.mode5.log
echo "Running 10 step - mode 4"
MAX_STEPS=10 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode4.jpf >& $JRDIR/logs/tcas.$(($MAX_STEPS))step.mode4.log
echo "Running 10 step - mode 3"
MAX_STEPS=10 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode3.jpf >& $JRDIR/logs/tcas.$(($MAX_STEPS))step.mode3.log
echo "Running 10 step - mode 2"
MAX_STEPS=10 && export MAX_STEPS && runSPF-tcas $JRDIR/src/examples/veritesting/tcas/tcas.mode2.jpf >& $JRDIR/logs/tcas.$(($MAX_STEPS))step.mode2.log
# run tcas with up to 10 steps in mode 1
# echo "Running 1 step - mode 1"
# MAX_STEPS=1 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 2 step - mode 1"
# MAX_STEPS=2 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 3 step - mode 1"
# MAX_STEPS=3 && export MAX_STEPS 
#    timeout $(($TIMEOUT_MINS))m  java -Djava.library.path=$JRDIR/lib -Xmx12288m -ea -Dfile.encoding=UTF-8 -jar $JPF_CORE_DIR/build/RunJPF.jar $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 4 step - mode 1"
# MAX_STEPS=4 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 5 step - mode 1"
# MAX_STEPS=5 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 6 step - mode 1"
# MAX_STEPS=6 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 7 step - mode 1"
# MAX_STEPS=7 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 8 step - mode 1"
# MAX_STEPS=8 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 9 step - mode 1"
# MAX_STEPS=9 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log
# echo "Running 10 step - mode 1"
# MAX_STEPS=10 && export MAX_STEPS && runSPF-tcas $JRDIR/src/tcas/tcas.mode1.jpf >& $JRDIR/logs/tcas.mode1.$(($MAX_STEPS))step.log

