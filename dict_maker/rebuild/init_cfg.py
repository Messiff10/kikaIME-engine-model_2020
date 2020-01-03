import configparser
import argparse

def vocabSize(file):
    count = 0
    with open(file) as f:
        for _ in f:
            count=count+1
    return str(count)

def writeConfigFile(args):
    config = configparser.ConfigParser()
    config.read(args.original_cfg)
    config.set("lm", "input_size", vocabSize(args.vocab_in_words))
    config.set("kc", "input_size", vocabSize(args.vocab_in_letters))
    config.set('kc', "softmax", vocabSize(args.vocab_out_words))
    config.set("output", "dir", args.output_dir)

    with open(args.dst_cfg, "w") as f:
        config.write(f)
    return

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-c", "--original_cfg", default='model.cfg')
    parser.add_argument("vocab_in_words", type=str)
    parser.add_argument("vocab_in_letters", type=str)
    parser.add_argument("vocab_out_words", type=str)
    parser.add_argument("output_dir", type=str)
    parser.add_argument("dst_cfg", type=str)
    args = parser.parse_args()
    writeConfigFile(args)