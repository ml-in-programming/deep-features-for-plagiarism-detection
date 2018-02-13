import os

import sys
from sklearn import svm

from commons import get_text_file_content
from network import CharacterNetwork


def read_features(path, network):
    return network.calculate_feature(get_text_file_content(path))


def read_vector(path, network):
    v1 = read_features(os.path.join(path, 'a.java'), network)
    v2 = read_features(os.path.join(path, 'b.java'), network)

    diff = v1 - v2
    if diff[0] < 0:
        diff *= -1

    return diff


def read_vectors(path, network):
    return [
        read_vector(os.path.join(path, directory), network)
        for directory in os.listdir(path)
    ]


def read_data(path, network):
    X0 = read_vectors(os.path.join(path, 'not-copies'), network)
    X1 = read_vectors(os.path.join(path, 'copies'), network)

    Y = [0 for _ in X0] + [1 for _ in X1]

    return X0 + X1, Y


def accuracy_on(data, network, clf):
    X, y = read_data(data, network)
    # print(X)
    actual = clf.predict(X)

    good = 0
    for i in range(0, len(y)):
        if y[i] == actual[i]:
            good += 1

    return good / len(y)


def main(network_name, training_data, validating_data):
    network = CharacterNetwork(network_name)

    clf = svm.SVC()
    clf.fit(*read_data(training_data, network))

    tacc = accuracy_on(training_data, network, clf)
    print('accuracy on training data: ', tacc)

    vacc = accuracy_on(validating_data, network, clf)
    print('accuracy on validation data: ', vacc)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2], sys.argv[3])
