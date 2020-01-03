import tensorflow as tf
import argparse
import configparser
import os
from tensorflow.python.framework.graph_util import convert_variables_to_constants

lm_map_vars = {'embedding/embedding_array:0': 'WordModel/Lm/Embedding/embedding:0',
               'embedding/embedding_proj:0': 'WordModel/Lm/Embedding/embedding_to_rnn:0',
               'rnn/multi_rnn_cell/cell_0/basic_lstm_cell/kernel:0': 'WordModel/Lm/RNN/multi_rnn_cell/cell_0/basic_lstm_cell/kernel:0',
               'rnn/multi_rnn_cell/cell_0/basic_lstm_cell/bias:0': 'WordModel/Lm/RNN/multi_rnn_cell/cell_0/basic_lstm_cell/bias:0',
               'rnn/multi_rnn_cell/cell_1/basic_lstm_cell/kernel:0': 'WordModel/Lm/RNN/multi_rnn_cell/cell_1/basic_lstm_cell/kernel:0',
               'rnn/multi_rnn_cell/cell_1/basic_lstm_cell/bias:0': 'WordModel/Lm/RNN/multi_rnn_cell/cell_1/basic_lstm_cell/bias:0'
               }

kc_map_vars = {'embedding/embedding_array:0': 'LetterModel/Embedding/embedding:0',
               'embedding/embedding_proj:0': 'LetterModel/Embedding/embedding_to_rnn:0',
               'rnn/multi_rnn_cell/cell_0/basic_lstm_cell/kernel:0': 'LetterModel/RNN/rnn/multi_rnn_cell/cell_0/basic_lstm_cell/kernel:0',
               'rnn/multi_rnn_cell/cell_0/basic_lstm_cell/bias:0': 'LetterModel/RNN/rnn/multi_rnn_cell/cell_0/basic_lstm_cell/bias:0',
               'rnn/multi_rnn_cell/cell_1/basic_lstm_cell/kernel:0': 'LetterModel/RNN/rnn/multi_rnn_cell/cell_1/basic_lstm_cell/kernel:0',
               'rnn/multi_rnn_cell/cell_1/basic_lstm_cell/bias:0': 'LetterModel/RNN/rnn/multi_rnn_cell/cell_1/basic_lstm_cell/bias:0',
               'softmax/rnn_output_proj:0': 'LetterModel/Softmax/rnn_output_to_final_output:0',
               'softmax/softmax_w:0': 'LetterModel/Softmax/softmax_w:0',
               'softmax/softmax_b:0': 'LetterModel/Softmax/softmax_b:0'
               }

def print_ops(graph):
    tmp_set = set()
    for i in graph.as_graph_def().node:
        tmp_set.add(i.op)
    for i in sorted(list(tmp_set)):
        print(i)

def print_vars(graph):
    all_vars = graph.get_collection(tf.GraphKeys.GLOBAL_VARIABLES)
    for i in all_vars:
        print(i)


def init_graph_from_frozen_pb(pb_file):
    gdef = tf.GraphDef()
    with open(pb_file, 'rb') as fp:
        gdef.ParseFromString(fp.read())
    g = tf.Graph()
    with g.as_default():
        tf.import_graph_def(gdef, name='')
    return g


def build_gdef_lm(config, orig_g):
    input_size = config.getint('lm', 'input_size')
    embedding_width = config.getint('lm', 'embedding_width')
    num_layers = config.getint('lm', 'num_layers')
    hidden_size = config.getint('lm', 'hidden_size')

    g = tf.Graph()
    with g.as_default():
        with tf.variable_scope("input"):
            input_id = tf.placeholder(dtype=tf.int32, shape=[1], name='input_id')
            input_state = tf.placeholder(dtype=tf.float32, shape=(num_layers, 2, 1, hidden_size), name='input_state')

        with tf.variable_scope("embedding"):
            embedding_array = tf.get_variable('embedding_array', shape=(input_size, embedding_width))
            embedding_proj = tf.get_variable('embedding_proj', shape=(embedding_width, hidden_size))

            embedding = tf.gather(embedding_array, input_id)
            proj = tf.matmul(embedding, embedding_proj)

        with tf.variable_scope("rnn"):
            lstm_proto = init_lstm(num_layers, hidden_size)
            unstacked_input_states = [tf.unstack(x) for x in tf.unstack(input_state)]
            _, rnn_state = lstm_proto(proj, unstacked_input_states)

        with tf.variable_scope('output'):
            output_state = tf.identity(rnn_state, name='output_state')

        # print_vars(g)
        # print_ops(g)

    gdef = assign_graph(g, orig_g, lm_map_vars, ['output/output_state'])
    return gdef


def build_gdef_kc(config, orig_g):
    input_size = config.getint('kc', 'input_size')
    embedding_width = config.getint('kc', 'embedding_width')
    num_layers = config.getint('kc', 'num_layers')
    hidden_size = config.getint('kc', 'hidden_size')
    softmax_size = config.getint('kc', 'softmax')

    g = tf.Graph()
    with g.as_default():
        with tf.variable_scope('input'):
            input_topk = tf.placeholder(dtype=tf.int32, shape=[], name='input_topk')
            input_id = tf.placeholder(dtype=tf.int32, shape=[1], name='input_id')
            input_state = tf.placeholder(dtype=tf.float32, shape=(num_layers, 2, 1, hidden_size), name='input_state')

        with tf.variable_scope('embedding'):
            embedding_array = tf.get_variable('embedding_array', shape=(input_size, embedding_width))
            embedding_proj = tf.get_variable('embedding_proj', shape=(embedding_width, hidden_size))

            embedding = tf.gather(embedding_array, input_id)
            proj = tf.matmul(embedding, embedding_proj)

        with tf.variable_scope('rnn'):
            lstm_proto = init_lstm(num_layers, hidden_size)
            unstacked_input_states = [tf.unstack(x) for x in tf.unstack(input_state)]
            rnn_out, rnn_state = lstm_proto(proj, unstacked_input_states)

        with tf.variable_scope('softmax'):
            rnn_output_proj = tf.get_variable('rnn_output_proj', shape=(hidden_size, embedding_width))
            softmax_w = tf.get_variable('softmax_w', shape=(embedding_width, softmax_size))
            softmax_b = tf.get_variable('softmax_b', shape=(softmax_size,))

            s_proj = tf.matmul(rnn_out, rnn_output_proj)
            logits = tf.matmul(s_proj, softmax_w) + softmax_b
            probs = tf.nn.softmax(logits)
            topk_p, topk_i = tf.nn.top_k(probs, input_topk)

        with tf.variable_scope('output'):
            output_states = tf.identity(rnn_state, name='output_state')
            output_probs = tf.identity(topk_p, name='output_probs')
            output_indices = tf.identity(topk_i, name='output_indices')

        # print_vars(g)
        # print_ops(g)

    gdef = assign_graph(g, orig_g, kc_map_vars, ['output/output_state', 'output/output_probs', 'output/output_indices'])
    return gdef


def init_lstm(num_layers, hidden_size):
    cell_proto = lambda: tf.nn.rnn_cell.BasicLSTMCell(hidden_size)
    return tf.nn.rnn_cell.MultiRNNCell([cell_proto() for _ in range(num_layers)])


def fetch_graph_val(src_g, m_vars):
    m_var_val = {}
    with tf.Session(graph=src_g) as sess:
        for name in m_vars:
            t = src_g.get_tensor_by_name(m_vars[name])
            m_var_val[name] = sess.run(t)
    return m_var_val


def assign_graph(dst_g, src_g, m_vars, outputs):
    m_var_val = fetch_graph_val(src_g, m_vars)
    with tf.Session(graph=dst_g) as sess:
        all_vars = dst_g.get_collection(tf.GraphKeys.GLOBAL_VARIABLES)
        for it_var in all_vars:
            it_var.load(m_var_val[it_var.name])
        gdef = convert_variables_to_constants(sess, dst_g.as_graph_def(), outputs)
    return gdef


def output_path(config, file_name):
    output_dir = config.get('output', 'dir')
    if not os.path.isdir(output_dir):
        os.makedirs(output_dir)
    return os.path.join(output_dir, config.get('output', file_name))


def write_gdef(dst, gdef):
    with open(dst, 'wb') as fp:
        fp.write(gdef.SerializeToString())


def main(args):
    with tf.device('/cpu:0'):
        config = configparser.ConfigParser()
        config.read(args.config_file)
        orig_g = init_graph_from_frozen_pb(args.original_pb)

        lm_gdef = build_gdef_lm(config, orig_g)
        kc_gdef = build_gdef_kc(config, orig_g)

        lm_path = output_path(config, 'lm')
        kc_path = output_path(config, 'kc')

        write_gdef(lm_path, lm_gdef)
        write_gdef(kc_path, kc_gdef)
    return 0


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("-c", "--config_file", type=str, default='model.cfg')
    parser.add_argument("original_pb", type=str)
    args = parser.parse_args()
    main(args)
