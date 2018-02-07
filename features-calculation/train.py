import sys
import os

import keras
from keras.models import Sequential
from keras.layers import LSTM, Dense, Activation, Lambda
from keras.optimizers import RMSprop

import numpy as np

from commons import networks_folder_name, get_text_file_content, str_to_vectors_batch


class CharacterNetwork:
    alphabet_size = 128  # ascii
    lstm_units = 128  # todo: should be 512
    number_of_lstm_layers = 2  # todo: should be 3

    def __init__(self, name):
        self._name = name
        filepath = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                networks_folder_name,
                                self._name)

        if os.path.isfile(filepath):
            print('Loading existing network')
            self.__load(filepath)
        else:
            print('Creating new network')
            self.__build()

        print(self._char_model.summary())

        # this want work if we have models with different number of lstm layers
        self._model = keras.models.Model(
                            inputs=self._char_model.input,
                            outputs=self._char_model.layers[self.number_of_lstm_layers - 1].output)

    def __build(self):
        self._char_model = Sequential()

        assert(self.number_of_lstm_layers > 0)
        self._char_model.add(LSTM(self.lstm_units,
                                  input_shape=(None, self.alphabet_size),
                                  return_sequences=True))

        for _ in range(0, self.number_of_lstm_layers - 1):
            self._char_model.add(LSTM(self.lstm_units, return_sequences=True))

        def forget(x):  # assuming input is a 3D tensor
            return x[:, -1, :]

        def forget_output_shape(shape):
            assert(len(shape) == 3)
            return shape[0], shape[2]

        self._char_model.add(Lambda(forget, forget_output_shape))

        self._char_model.add(Dense(self.alphabet_size))
        self._char_model.add(Activation('softmax'))

        self._char_model.compile(loss='categorical_crossentropy',
                                 optimizer=RMSprop(lr=0.01))
        # lr=0.002 decay=0.95

    def __load(self, filepath):
        self._char_model = keras.models.load_model(filepath)

    def train_on_file(self, file):
        batch_size = 1  # todo: should be 1
        sample_len = 100  # todo: want 200

        text = get_text_file_content(file)
        number_of_samples = len(text) - sample_len

        # todo: adjust logging
        for first_sample_id in range(0, number_of_samples, batch_size):
            sentences = []
            next_chars = []

            next_sample_id = min(first_sample_id + batch_size, number_of_samples)
            for sample_id in range(first_sample_id, next_sample_id):
                sentences.append(text[sample_id:sample_id + sample_len])
                next_chars.append(text[sample_id + sample_len])

            x = np.zeros((len(sentences), sample_len, self.alphabet_size), dtype=np.bool)
            y = np.zeros((len(sentences), self.alphabet_size), dtype=np.bool)
            for i, sentence in enumerate(sentences):
                y[i, ord(next_chars[i])] = 1
                for t, char in enumerate(sentence):
                    x[i, t, ord(char)] = 1

            self._char_model.fit(x, y, batch_size=batch_size, epochs=1, verbose=2)
            print(next_sample_id, '/', number_of_samples, 'samples processed')

    def save(self):
        self._char_model.save(os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                           networks_folder_name,
                                           self._name))

    def calculate_feature(self, code):
        batch = str_to_vectors_batch(code, self.alphabet_size)
        return np.mean(self._model.predict_on_batch(batch)[0], 0)


def main(network_name, data_dir):
    network = CharacterNetwork(network_name)

    number_of_epochs = 1
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
