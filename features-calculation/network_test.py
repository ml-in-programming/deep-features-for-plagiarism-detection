import sys

import numpy
from matplotlib import pyplot

from alphabet import java_alphabet
from commons import get_text_file_content
from network import CharacterNetwork


def main(filepath):
    network_names = ['network17', 'network28', 'network40', 'network41', 'network53',
                     'network63', 'network68', 'network74', 'network84']

    for network_name in network_names:
        network = CharacterNetwork(network_name, java_alphabet)
        print('%s loss: %s' % (network_name, network.test_on_file(filepath)))

    # network17 loss: 1.64799409246
    # network28 loss: 1.85438524342
    # network40 loss: 1.98001976967
    # network41 loss: 1.96927907419
    # network53 loss: 2.06370159197
    # network63 loss: 2.07270663214
    # network68 loss: 2.05573948336
    # network74 loss: 2.19113012552
    # network84 loss: 2.22412909508

if __name__ == "__main__":
    main(sys.argv[1])
