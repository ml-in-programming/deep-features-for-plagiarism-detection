import re

import numpy as np

networks_folder_name = 'networks'


def get_text_file_content(file):
    with open(file, encoding="latin-1") as f:
        return f.read()
        # return ''.join(list(filter(lambda c: ord(c) < 128, f.read())))
        # todo: specify 128 magic const, ascii size


def str_to_one_hot_sequence(string, alphabet):
    sequence = np.zeros((len(string), len(alphabet)), dtype=np.bool)
    for index, char in enumerate(string):
        sequence[index, alphabet.from_ASCII(char)] = True

    return sequence


def get_samples_from_sequence(sequence, sample_size):
    sequence_size = len(sequence)

    X = []
    y = []

    for i in range(0, sequence_size - sample_size, sample_size):
        last_symbol_index = i + sample_size

        X.append(sequence[i:last_symbol_index])
        y.append(sequence[i + 1: last_symbol_index + 1])

    return np.stack(X), np.stack(y)


def str_to_vectors_batch(str, alphabet_size):
    batch = np.zeros((1, len(str), alphabet_size), dtype=np.bool)
    for t, char in enumerate(str):
        batch[0, t, ord(char)] = 1

    return batch


def similar(name1, name2):
    return re.sub('[^0-9]', '', name1) == re.sub('[^0-9]', '', name2)
