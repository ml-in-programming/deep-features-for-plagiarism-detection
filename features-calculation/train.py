import os
import statistics
import sys

import matplotlib.pyplot as plt

from alphabet import Alphabet
from network import CharacterNetwork
from plot import median_for_file


def main(network_name, data_dir):
    alphabet = Alphabet(data_dir)
    network = CharacterNetwork(network_name, alphabet)

    medians = []

    number_of_epochs = 2
    for e in range(1, number_of_epochs + 1):
        print('Epoch #', e)
        all_files = []
        for root, _, files in os.walk(data_dir):
            all_files += [root + '/' + file for file in files]

        ctr = 0
        for file in all_files:
            print('Training on:', file)
            medians.append(statistics.median(network.train_on_file(file)))
            network.save()

            ctr += 1
            print(ctr, '/', len(all_files), 'files processed')

    plt.xlabel('Number of files')
    plt.ylabel('Losses median')
    plt.plot(list(range(len(medians))), medians)
    # plt.show()
    plt.savefig('loss.png')
    plt.clf()

    # run_character_rnn(network)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
