import os
import sys

import pickle
import numpy

sys.path.append('..')

from network import CharacterNetwork
from alphabet import java_alphabet
from dataset_utils.commentaries_remover import remove_comments
from svm import load_svm


def main():
    snippets_dir = os.path.join(
        os.path.dirname(os.path.realpath(__file__)),
        'snippets'
    )

    network = CharacterNetwork('network', java_alphabet)
    snippet = remove_comments(sys.stdin.read())
    snippet_feature = network.calculate_feature(snippet)

    svm = load_svm('standard')

    similar_snippets = []
    for root, _, files in os.walk(snippets_dir):
        for file in files:
            with open(os.path.join(root, file), 'rb') as f:
                another_snippet, another_feature = pickle.load(f)

            if svm.predict([numpy.abs(snippet_feature - another_feature)])[0] == 'a copy':
                similar_snippets.append(another_snippet)

    print('%s similar code snippets have been found:' % len(similar_snippets))
    for s in similar_snippets:
        print(s)
        print()


if __name__ == '__main__':
    main()
