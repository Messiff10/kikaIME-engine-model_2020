#!/usr/bin/env bash

# Created by msj on 18-8-22.

if [ $# != 3 ]; then
    echo "Usage: $0 <locale> <src_dir> <frozen_graph_file>"
    exit 1
fi

LOCALE=$1
SRC_DIR=$2
FROZEN_GRAPH=${SRC_DIR}/$3
VOCAB_IN_WORDS=${SRC_DIR}/vocab_in_words
VOCAB_IN_LETTERS=${SRC_DIR}/vocab_in_letters
VOCAB_OUT_WORDS=${SRC_DIR}/vocab_out

DST_DICT_NAME=split_rnn_${LOCALE}_9_5_8

ROOT_DIR=$(cd $(dirname $0) && pwd)
BUILD_DIR=${ROOT_DIR}/build/${LOCALE}
BUILD_DIR_TMP=${BUILD_DIR}/tmp


# 0. init build dir
cd ${ROOT_DIR}
if [ -d ${BUILD_DIR} ]; then
    rm -rf ${BUILD_DIR}
fi
mkdir -p ${BUILD_DIR_TMP}


# 1. make vocabs
cd ${ROOT_DIR}
python3 make_vocab.py ${VOCAB_IN_WORDS} ${BUILD_DIR_TMP}/vocab_in_words
python3 make_vocab.py ${VOCAB_IN_LETTERS} ${BUILD_DIR_TMP}/vocab_in_letters
python3 make_vocab.py -r ${VOCAB_OUT_WORDS} ${BUILD_DIR_TMP}/vocab_out_words


# 2. rebuild frozen-graph to sub-graphs that tf-lite supports
cd ${ROOT_DIR}/rebuild
python3 init_cfg.py ${BUILD_DIR_TMP}/vocab_in_words \
                    ${BUILD_DIR_TMP}/vocab_in_letters \
                    ${BUILD_DIR_TMP}/vocab_out_words \
                    ${BUILD_DIR_TMP} \
                    ${BUILD_DIR_TMP}/cfg

python3 rebuild_split_model.py -c ${BUILD_DIR_TMP}/cfg ${FROZEN_GRAPH}


# 3. convert to lite and quantify
cd ${ROOT_DIR}/tensorflow/kika
bazel run -s -c dbg //dict_tools/python:tf2lite -- \
  --toco `pwd`/bazel-bin/external/org_tensorflow/tensorflow/contrib/lite/toco/toco \
  --input input/input_id \
  --input input/input_state \
  --output output/output_state \
  --dst ${BUILD_DIR_TMP}/lm.lite \
  ${BUILD_DIR_TMP}/rebuilt_lm.pb

bazel run -s -c dbg //dict_tools/python:tf2lite -- \
  --toco `pwd`/bazel-bin/external/org_tensorflow/tensorflow/contrib/lite/toco/toco \
  --input input/input_id \
  --input input/input_state \
  --input input/input_topk \
  --output output/output_state \
  --output output/output_probs \
  --output output/output_indices \
  --dst ${BUILD_DIR_TMP}/kc.lite \
  ${BUILD_DIR_TMP}/rebuilt_kc.pb

bazel run -s -c dbg //dict_tools/python:tfecho -- \
  --input ${BUILD_DIR_TMP}/lm.lite \
  --output ${BUILD_DIR_TMP}/lm.echo

bazel run -s -c dbg //dict_tools/python:tfecho -- \
  --input ${BUILD_DIR_TMP}/kc.lite \
  --output ${BUILD_DIR_TMP}/kc.echo

bazel run -s -c dbg //dict_tools/python:quantize -- e \
  ${BUILD_DIR_TMP}/lm.lite \
  ${BUILD_DIR_TMP}/lm.echo \
  ${BUILD_DIR_TMP}/lm.q

bazel run -s -c dbg //dict_tools/python:quantize -- e \
  ${BUILD_DIR_TMP}/kc.lite \
  ${BUILD_DIR_TMP}/kc.echo \
  ${BUILD_DIR_TMP}/kc.q


# 4. compose dictionary
cd ${ROOT_DIR}/dict_composer
java -jar build/libs/dict_composer.jar \
    ${BUILD_DIR_TMP}/vocab_in_letters \
    ${BUILD_DIR_TMP}/vocab_in_words \
    ${BUILD_DIR_TMP}/vocab_out_words \
    ${BUILD_DIR_TMP}/lm.q \
    ${BUILD_DIR_TMP}/kc.q \
    ${BUILD_DIR_TMP}/${DST_DICT_NAME}

cd ${BUILD_DIR_TMP}
cp ${DST_DICT_NAME} ../