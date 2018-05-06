from __future__ import print_function

import os

import numpy as np

from alphabet import java_alphabet
from network import CharacterNetwork


def run_character_rnn(network, alphabet):
    def sample(preds):
        preds = np.asarray(preds).astype('float64')
        preds = preds / np.sum(preds)
        probas = np.random.multinomial(1, preds, 1)

        return np.argmax(probas)

    sentence = 'public static void main(String[] args)'
    result = sentence

    for i in range(1000):
        x_pred = np.zeros((1, len(sentence), len(alphabet)))
        for t, char in enumerate(sentence):
            x_pred[0, t, alphabet.from_ASCII(char)] = 1.

        preds = network._char_model.predict(x_pred, verbose=0)[0, -1]

        next_index = sample(preds)
        next_char = alphabet.get_ASCII(next_index)

        sentence = sentence[1:] + next_char

        result = result + next_char

    return result


def main():
    network_names = ['network17', 'network28', 'network40', 'network41', 'network53',
                     'network63', 'network68', 'network74', 'network84']

    for network_name in network_names:
        network = CharacterNetwork(network_name, java_alphabet)
        code = run_character_rnn(network, java_alphabet)

        with open(os.path.join('generated-code', network_name), 'w') as f:
            f.write(code)


if __name__ == "__main__":
    main()
