from __future__ import print_function

import statistics
import sys

import numpy as np

import matplotlib.pyplot as plt

from alphabet import Alphabet
from commons import get_text_file_content
from network import CharacterNetwork


def convert_to_vector(string, alphabet):
    x_pred = np.zeros((1, len(string), len(alphabet)))
    for t, char in enumerate(string):
        x_pred[0, t, alphabet.from_ASCII(char)] = 1.

    return x_pred


def median_for_file(network, alphabet, file):
    content = get_text_file_content(file)

    error = []
    for i in range(1, len(content)):
        x_pred = convert_to_vector(content[max(0, i - 50):i], alphabet)
        preds = network._char_model.predict(x_pred, verbose=0)[0]

        error.append(1. - preds[alphabet.from_ASCII(content[i])])

    return statistics.median(error)


def errors_for_file(network, alphabet, file):
    content = get_text_file_content(file)

    error = []
    median = []
    for i in range(1, len(content)):
        x_pred = convert_to_vector(content[max(0, i - 50):i], alphabet)
        preds = network._char_model.predict(x_pred, verbose=0)[0]

        error.append(1. - preds[alphabet.from_ASCII(content[i])])
        median.append(statistics.median(error))
        print(i, '/', len(content))

    return median


def draw_error_plot(network, alphabet, file, label):
    error = errors_for_file(network, alphabet, file)

    plt.plot(list(range(len(error))), error, label = label)


def main(network1_name, network2_name, data_dir, file):
    alphabet = Alphabet(data_dir)
    network1 = CharacterNetwork(network1_name, alphabet)
    draw_error_plot(network1, alphabet, file, '100 iterations')

    network2 = CharacterNetwork(network2_name, alphabet)
    draw_error_plot(network2, alphabet, file, '300 iterations')

    plt.legend()
    plt.show()
    # plt.savefig(filename)
    plt.clf()


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
