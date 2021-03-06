import os
import math

import keras
import numpy
import numpy as np
import sys
from keras.layers import LSTM, Dense, Activation, TimeDistributed
from keras.models import Sequential
from keras.optimizers import RMSprop

from commons import networks_folder_name, get_text_file_content, str_to_vectors_batch, \
    str_to_one_hot_sequence, get_samples_from_sequence


class CharacterNetwork:
    # alphabet_size = 128  # ascii
    lstm_units = 512
    number_of_lstm_layers = 2

    def __init__(self, name, alphabet):
        self._name = name
        self.alphabet = alphabet
        self._alphabet_size = len(alphabet)
        filepath = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                networks_folder_name,
                                self._name)

        if os.path.isfile(filepath):
            print('Loading existing network', file=sys.stderr)
            self.__load(filepath)
        else:
            print('Creating new network', file=sys.stderr)
            self.__build()

        self._char_model.summary(print_fn=lambda x: print(x, file=sys.stderr))

        # this want work if we have models with different number of lstm layers
        self._model = keras.models.Model(
                            inputs=self._char_model.input,
                            outputs=self._char_model.layers[self.number_of_lstm_layers - 1].output)

    def __build(self):
        self._char_model = Sequential()

        assert(self.number_of_lstm_layers > 0)
        self._char_model.add(LSTM(self.lstm_units,
                                  input_shape=(None, self._alphabet_size),
                                  return_sequences=True))

        for _ in range(self.number_of_lstm_layers - 1):
            self._char_model.add(LSTM(self.lstm_units, return_sequences=True))

        self._char_model.add(TimeDistributed(Dense(self._alphabet_size)))

        self._char_model.add(Activation('softmax'))

        self._char_model.compile(loss='categorical_crossentropy',
                                 optimizer=RMSprop(lr=0.002))
        # todo: decay? Should consider several epochs

    def __load(self, filepath):
        self._char_model = keras.models.load_model(filepath)
        self._char_model.load_weights(filepath)

    def __file_to_sample_chunks(self, file):
        sample_size = 200
        chunk_size = 500 * sample_size + 1

        file_size = os.path.getsize(file)

        chunks_left = math.ceil(file_size / chunk_size)
        print('File is split on %s chunks' % chunks_left)

        with open(file, encoding="latin-1") as f:
            for text in iter(lambda: f.read(chunk_size), ''):
                sequence = str_to_one_hot_sequence(text, self.alphabet)

                # todo: it is useless to predict first characters in the sequence
                # todo: as they don't have context
                # todo: need to consider this in get_samples_from_sequence
                X, y = get_samples_from_sequence(sequence, sample_size)
                chunks_left -= 1

                yield X, y, chunks_left

    def train_on_file(self, file, start_epoch=1):
        batch_size = 64
        num_of_epochs = 50

        for epoch in range(start_epoch, num_of_epochs + 1):
            print('Epoch #', epoch)

            for X, y, chunks_left in self.__file_to_sample_chunks(file):
                self._char_model.fit(X, y, batch_size=batch_size, epochs=1)
                print('%s chunks left' % chunks_left)

            self.save(str(epoch))

    def test_on_file(self, file):
        batch_size = 64
        loss = []

        # todo: if get_samples_from_sequence will be rewritten to not train on first characters
        # todo: we shouldn't use __file_to_sample_chunks which uses it
        for X, y, _ in self.__file_to_sample_chunks(file):
            loss.append(self._char_model.evaluate(X, y, batch_size=batch_size))

        return numpy.mean(loss)

    def save(self, suffix=''):
        dir_path = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                networks_folder_name)

        if not os.path.exists(dir_path):
            os.makedirs(dir_path)

        self._char_model.save(os.path.join(dir_path, self._name + suffix))

    def calculate_feature(self, code):
        sequence = np.expand_dims(str_to_one_hot_sequence(code, self.alphabet), axis=0)
        return np.mean(self._model.predict_on_batch(sequence)[0], axis=0)
