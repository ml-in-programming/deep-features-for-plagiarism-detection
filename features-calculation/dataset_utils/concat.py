import os
from random import shuffle

import sys

from commentaries_remover import remove_comments
from commons import get_text_file_content


def concat_files(folder, num_of_characters):
    all_files = []
    for root, _, files in os.walk(folder):
        for file in files:
            all_files += [os.path.join(root, file)]

    shuffle(all_files)

    result = str()
    current_len = 0
    for file in all_files:
        if current_len >= num_of_characters:
            break

        content = remove_comments(get_text_file_content(file))
        result += content
        current_len += len(content)

    if current_len < num_of_characters:
        print('Too few characters in given data set.')
        return None

    return result


def main(folder, num_of_characters):
    with open('concat', 'w') as f:
        f.write(concat_files(folder, num_of_characters))


if __name__ == '__main__':
    main(sys.argv[1], int(sys.argv[2]))
