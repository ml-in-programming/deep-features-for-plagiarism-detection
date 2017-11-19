import re

import numpy as np

networks_folder_name = 'networks'


def get_text_file_content(file):
    with open(file, encoding="latin-1") as f:
        return ''.join(list((filter(lambda c: ord(c) < 128, f.read()))))
        # todo: specify 128 magic const, ascii size


def str_to_vectors_batch(str, alphabet_size):
    batch = np.zeros((1, len(str), alphabet_size), dtype=np.bool)
    for t, char in enumerate(str):
        batch[0, t, ord(char)] = 1

    return batch


def similar(name1, name2):
    return re.sub('[^0-9]', '', name1) == re.sub('[^0-9]', '', name2)
