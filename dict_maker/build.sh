#!/bin/bash

ROOT_DIR=$(cd $(dirname $0) && pwd)

cd $ROOT_DIR/tensorflow/kika

bazel build -s -c dbg --cxxopt=-msse4 \
  @org_tensorflow//tensorflow/contrib/lite/toco:toco \
  //dict_tools/python:tf2lite \
  //dict_tools/python:tfecho \
  //dict_tools/python:quantize

cd $ROOT_DIR/dict_composer
gradle build