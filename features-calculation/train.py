import os
import sys

from alphabet import java_alphabet
from network import CharacterNetwork


def main(network_name, filepath, start_epoch):
    network = CharacterNetwork(network_name, java_alphabet)
    network.train_on_file(filepath, start_epoch)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2], int(sys.argv[3]))
