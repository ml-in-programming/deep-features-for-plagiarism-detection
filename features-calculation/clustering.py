import os
import sys

import numpy as np
from scipy.cluster.vq import kmeans, whiten

from commons import similar
from train import CharacterNetwork


def main(network_name, dir_path):
    network = CharacterNetwork(network_name)

    all_files = []
    for root, _, files in os.walk(dir_path):
        all_files += [root + '/' + file for file in files]

    features = []
    for i in range(len(all_files)):
        for j in range(i + 1, len(all_files)):
            file1 = all_files[i]
            file2 = all_files[j]
            feature1 = network.calculate_feature(file1)
            feature2 = network.calculate_feature(file2)
            features.append((np.absolute(feature1 - feature2), similar(file1, file2)))

    number_of_clusters = 20
    whitened = whiten(np.array([f[0] for f in features]))
    centroids, _ = kmeans(whitened, number_of_clusters)

    classes = [[] for _ in range(number_of_clusters)]
    for i in range(0, len(features)):
        class_id = np.argmin(list(map(lambda c: np.linalg.norm(c - whitened[i]), centroids)))
        classes[class_id].append(features[i][1])

    for clazz in classes:
        print(clazz)

if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
