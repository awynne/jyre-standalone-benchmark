# jyre-standalone-benchmark

This project comprises a benchmark test for jyre. It 
demonstrates the following:

* The pure java version of jyre experiences message loss at 
higher volumes
* An initial version of a JNI wrapper for libzyre does not
have the same message loss

The JNI wrapper works but could use some improvement.  It is currently
checked into the zyre project 
[bindings directory](https://github.com/zeromq/zyre/tree/master/bindings/java/zyre-jni).
If you are running on linux, you can probably use the already compiled
so which is stored in 

    native/linux/libzrejni.so

This test runs in a single process and does the following:

* A responder thread sends N "shout" messages to R responders
* each responder sends a unicast "whisper" response back to the responder
* the responder counts up the received messages and reports on the percentage
  received (out of the total expected)

## Running from the command line

To run from the command line, first build the binary distribution

### Building the binary distribution

run the following command

    ./gradlew clean distZip

then retrieve the zip from the directory

    build/distributions

### Run from the command line

First, Unzip the distribution zip
then, cd to the directory where the files are unpacked and run 
the following command to learn the command line args:

    > bin/jyre-standalone-benchmark --help
    usage: jyre-standalone-benchmark
    ...

## Example command

This was tested on a linux VM:

    > bin/jyre-standalone-benchmark --zyreImpl jni --numResponders 10 --numMsgs 10000 --interval 0
    
You can tell it succeeds because the console output reports that it received 
100% of expected messages:
    
    INFO  org.test.zyre.jni.JniRequester - sent: 10000 expected: 100000 received: 100000 (100%)
    
The above command works when the jni implementation is used, but 
not when the java implementation is used.

