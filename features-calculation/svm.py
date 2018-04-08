import os
import sys

import numpy
from sklearn import svm
from sklearn.metrics import classification_report


def read_features(path):
    return numpy.load(path)


def read_vector(path):
    v1 = read_features(os.path.join(path, 'a.java.npy'))
    v2 = read_features(os.path.join(path, 'b.java.npy'))

    return numpy.abs(v1 - v2)


def read_vectors(path):
    result = []

    dirs = os.listdir(path)
    for directory in dirs:
        result.append(read_vector(os.path.join(path, directory)))

    return result


def read_data(path):
    X0 = read_vectors(os.path.join(path, 'not-copies'))
    X1 = read_vectors(os.path.join(path, 'copies'))

    Y = ['not a copy' for _ in X0] + ['a copy' for _ in X1]

    return X0 + X1, Y


def score_data(clf, X, y):
    y_pred = clf.predict(X)
    print(classification_report(y, y_pred))


def main(train_data, test_data):
    X_train, y_train = read_data(train_data)
    X_test, y_test = read_data(test_data)

    clf = svm.SVC()
    clf.fit(X_train, y_train)

    print('Train data:')
    score_data(clf, X_train, y_train)

    print('Test data:')
    score_data(clf, X_test, y_test)

    # Train data:
    # copy: precision = 0.5427841634738186, recall = 0.9964830011723329
    # non-copy: precision = 0.9785714285714285, recall = 0.16060961313012895
    #
    # Test data:
    # copy: precision = 0.5233160621761658, recall = 1.0
    # non-copy: precision = 1.0, recall = 0.0891089108910891


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
