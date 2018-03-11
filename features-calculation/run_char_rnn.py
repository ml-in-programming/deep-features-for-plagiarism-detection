from __future__ import print_function

import sys

import numpy as np

from alphabet import java_alphabet
from network import CharacterNetwork


def run_character_rnn(network, alphabet):
    def sample(preds):
        preds = np.asarray(preds).astype('float64')
        preds = preds / np.sum(preds)
        probas = np.random.multinomial(1, preds, 1)

        return np.argmax(probas)

    sentence = 'ababab' # 'public static void main(String[] args)'
    sys.stdout.write(sentence)

    for i in range(400):
        x_pred = np.zeros((1, len(sentence), len(alphabet)))
        for t, char in enumerate(sentence):
            x_pred[0, t, alphabet.from_ASCII(char)] = 1.

        preds = network._char_model.predict(x_pred, verbose=0)[0]

        next_index = sample(preds)
        next_char = alphabet.get_ASCII(next_index)

        sentence = sentence[1:] + next_char

        sys.stdout.write(next_char)


def main(network_name):
    network = CharacterNetwork(network_name, java_alphabet)
    run_character_rnn(network, java_alphabet)


if __name__ == "__main__":
    main(sys.argv[1])
