import os
import sys

from alphabet import java_alphabet
from network import CharacterNetwork


def main(network_name, filepath):
    network = CharacterNetwork(network_name, java_alphabet)

    number_of_epochs = 2000
    for e in range(1, number_of_epochs + 1):
        print('Epoch #', e)

        network.train_on_file(filepath)
        network.save()


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
