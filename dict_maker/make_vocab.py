import argparse
import re

prog = re.compile(r"[-]+")

def main(args):
    vocab = []
    with open(args.src_vocab) as f_in:
        for l in f_in:
            word = l.strip()
            word = word[0:word.find('##')]
            vocab.append(word)

    with open(args.dst_vocab, 'w') as f_out:
        for i in vocab:
            if args.replace and prog.search(i) != None:
                f_out.write('<eos>\n')
            else:
                f_out.write(i + '\n')

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("-r", "--replace", action="store_true", help="replace words with some symbols")
    parser.add_argument("src_vocab", type=str)
    parser.add_argument("dst_vocab", type=str)
    args = parser.parse_args()
    main(args)