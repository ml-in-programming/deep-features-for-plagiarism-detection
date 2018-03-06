import os
import sys

from alphabet import java_alphabet
from network import CharacterNetwork


def main(network_name, data_dir):
    network = CharacterNetwork(network_name, java_alphabet)

    number_of_epochs = 2000
    for e in range(1, number_of_epochs + 1):
        print('Epoch #', e)
        all_files = []
        for root, _, files in os.walk(data_dir):
            all_files += [root + '/' + file for file in files]

        ctr = 0
        for file in all_files:
            print('Training on:', file)
            network.train_on_file(file)
            network.save()

            ctr += 1
            print(ctr, '/', len(all_files), 'files processed')


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
