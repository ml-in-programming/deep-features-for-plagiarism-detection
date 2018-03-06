import os

import keras
import numpy as np
from keras.layers import LSTM, Lambda, Dense, Activation
from keras.models import Sequential
from keras.optimizers import RMSprop

from commons import networks_folder_name, get_text_file_content, str_to_vectors_batch


class CharacterNetwork:
    # alphabet_size = 128  # ascii
    lstm_units = 512
    number_of_lstm_layers = 3

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

        for _ in range(0, self.number_of_lstm_layers - 1):
            self._char_model.add(LSTM(self.lstm_units, return_sequences=True))

        def forget(x):  # assuming input is a 3D tensor
            return x[:, -1, :]

        def forget_output_shape(shape):
            assert(len(shape) == 3)
            return shape[0], shape[2]

        self._char_model.add(Lambda(forget, forget_output_shape))

        self._char_model.add(Dense(self._alphabet_size))
        self._char_model.add(Activation('softmax'))

        self._char_model.compile(loss='categorical_crossentropy',
                                 optimizer=RMSprop(lr=0.002))
        # todo: was 0.01

    def __load(self, filepath):
        self._char_model = keras.models.load_model(filepath)
        self._char_model.load_weights(filepath)

    def train_on_file(self, file):
        batch_size = 1  # todo: should be 1
        sample_len = 20  # todo: want 200

        text = get_text_file_content(file)
        number_of_samples = len(text) - sample_len

        loss = []
        # todo: adjust logging
        for first_sample_id in range(0, number_of_samples, batch_size):
            sentences = []
            next_chars = []

            next_sample_id = min(first_sample_id + batch_size, number_of_samples)
            for sample_id in range(first_sample_id, next_sample_id):
                sentences.append(text[sample_id:sample_id + sample_len])
                next_chars.append(text[sample_id + sample_len])

            x = np.zeros((len(sentences), sample_len, self._alphabet_size), dtype=float)
            y = np.zeros((len(sentences), self._alphabet_size), dtype=float)
            for i, sentence in enumerate(sentences):
                y[i, self.alphabet.from_ASCII(next_chars[i])] = 1.0
                for t, char in enumerate(sentence):
                    x[i, t, self.alphabet.from_ASCII(char)] = 1.0

            history = self._char_model.fit(x, y, batch_size=batch_size, epochs=1, verbose=2)
            print("'", next_chars[-1], "'")

            loss.append(history.history['loss'][0])

            print(next_sample_id, '/', number_of_samples, 'samples processed')

        return loss

    def save(self):
        self._char_model.save(os.path.join(os.path.dirname(os.path.realpath(__file__)),
                                           networks_folder_name,
                                           self._name))

    def calculate_feature(self, code):
        batch = str_to_vectors_batch(code, self._alphabet_size)
        return np.mean(self._model.predict_on_batch(batch)[0], 0)