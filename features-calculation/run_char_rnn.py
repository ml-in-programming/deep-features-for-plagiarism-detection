from __future__ import print_function

import sys

import numpy as np

from network import CharacterNetwork


def run_character_rnn(network):
    def sample(preds):
        preds = np.asarray(preds).astype('float64')
        preds = preds / np.sum(preds)
        probas = np.random.multinomial(1, preds, 1)

        return np.argmax(probas)

    sentence = 'public static void main(String[] args)'
    sys.stdout.write(sentence)

    for i in range(400):
        x_pred = np.zeros((1, len(sentence), 128))
        for t, char in enumerate(sentence):
            x_pred[0, t, ord(char)] = 1.

        preds = network._char_model.predict(x_pred, verbose=0)[0]

        next_index = sample(preds)
        next_char = chr(next_index)

        sentence = sentence[1:] + next_char

        sys.stdout.write(next_char)


def main(network_name):
    network = CharacterNetwork(network_name)
    run_character_rnn(network)


if __name__ == "__main__":
    main(sys.argv[1])
