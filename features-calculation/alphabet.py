import os

from commons import get_text_file_content


class Alphabet:
    # def __init__(self, data_dir):
    #     all_files = []
    #     for root, _, files in os.walk(data_dir):
    #         all_files += [root + '/' + file for file in files]
    #
    #     chars = set()
    #     for file in all_files:
    #         for c in get_text_file_content(file):
    #             chars.add(c)
    #
    #     self.__alphabet = sorted(list(chars))
    #     print(self.__alphabet)
    #     print(len(self.__alphabet))

    def __init__(self, alphabet):
        self.__alphabet = alphabet

    def __len__(self):
        return len(self.__alphabet)

    def from_ASCII(self, code):
        return self.__alphabet.index(code)

    def get_ASCII(self, index):
        return self.__alphabet[index]

    def translate_from_ASCII(self, string):
        return [self.from_ASCII(c) for c in string]


java_alphabet = Alphabet(['\t', '\n', '\x0c', ' ', '!', '"', '#', '$', '%', '&', "'", '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~'])
