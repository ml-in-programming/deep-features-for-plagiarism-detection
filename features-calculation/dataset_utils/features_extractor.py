import os
import sys

import numpy

from alphabet import java_alphabet
from commons import get_text_file_content
from network import CharacterNetwork


def main(network_name, folder):
    """
        Scans <<folder>> for .java files inside it. For each found file feature calculation process
        is run with network <<network_name>>. Calculated feature is stored beside original file.
    """

    network = CharacterNetwork(network_name, java_alphabet)

    sources = []
    for root, _, files in os.walk(folder):
        for file in files:
            _, ext = os.path.splitext(file)
            if ext == '.java':
                sources += [os.path.join(root, file)]

    amount = len(sources)
    for i, file in enumerate(sources):
        feature = network.calculate_feature(get_text_file_content(file))
        numpy.save(file, feature)

        print('%s / %s files are processed' % (i + 1, amount))


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
