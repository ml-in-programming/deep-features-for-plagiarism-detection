import os

import keras
import numpy as np
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
                                  input_shape=(None, self._alphabet_size),
                                  return_sequences=True))

        for _ in range(self.number_of_lstm_layers - 1):
            self._char_model.add(LSTM(self.lstm_units, return_sequences=True))

        self._char_model.add(TimeDistributed(Dense(self._alphabet_size)))

        self._char_model.add(Activation('softmax'))

        self._char_model.compile(loss='categorical_crossentropy',
                                 optimizer=RMSprop(lr=0.002))
        # todo: decay?

    def __load(self, filepath):
        self._char_model = keras.models.load_model(filepath)
        self._char_model.load_weights(filepath)

    def train_on_file(self, file):
        sample_size = 200
        chunk_size = 100000
        # todo: 499 but not 500 batches (200 last characters are not used) 100001 = f(sample_size)
        batch_size = 1  # todo: 128?


        # todo: print number of chunks
        with open(file, encoding="latin-1") as f:
            for text in iter(lambda: f.read(chunk_size), ''):
                sequence = str_to_one_hot_sequence(text, self.alphabet)

                # todo: it is useless to predict first characters in the sequence
                # todo: as they don't have context
                # todo: need to consider this in get_samples_from_sequence
                X, y = get_samples_from_sequence(sequence, sample_size)

                self._char_model.fit(X, y, batch_size=batch_size, epochs=1)

    def save(self):
        dir_path = os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                networks_folder_name)

        if not os.path.exists(dir_path):
            os.makedirs(dir_path)

        self._char_model.save(os.path.join(dir_path, self._name))

    def calculate_feature(self, code):
        batch = str_to_vectors_batch(code, self._alphabet_size)
        return np.mean(self._model.predict_on_batch(batch)[0], 0)
