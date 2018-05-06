import os
import sys

import pickle

sys.path.append('..')

from network import CharacterNetwork
from alphabet import java_alphabet
from dataset_utils.commentaries_remover import remove_comments


def main():
    snippets_dir = os.path.join(
        os.path.dirname(os.path.realpath(__file__)),
        'snippets'
    )

    network = CharacterNetwork('network', java_alphabet)
    snippet = remove_comments(sys.stdin.read())

    used_numbers = set(map(int, os.listdir(snippets_dir)))
    i = 1
    while True:
        if i not in used_numbers:
            break

        i += 1

    with open(os.path.join(snippets_dir, str(i)), 'wb') as f:
        f.write(pickle.dumps((snippet, network.calculate_feature(snippet))))


if __name__ == '__main__':
    main()
