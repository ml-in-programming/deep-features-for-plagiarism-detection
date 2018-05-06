import os
import sys

import numpy
import pickle

from sklearn import svm
from sklearn.metrics import classification_report
from sklearn.model_selection import train_test_split


models_dir = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'svm')


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


def load_svm(model_name):
    with open(os.path.join(models_dir, model_name), 'rb') as f:
        return pickle.load(f)


def main(data, model_name):
    X, y = read_data(data)

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, shuffle=True)

    clf = svm.SVC(kernel='linear')
    clf.fit(X_train, y_train)

    print('Train data:')
    score_data(clf, X_train, y_train)

    print('Test data:')
    score_data(clf, X_test, y_test)

    with open(os.path.join(models_dir, model_name), 'wb') as f:
        pickle.dump(clf, f)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
