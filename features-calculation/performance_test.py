import sys
import time

from alphabet import java_alphabet
from commons import get_text_file_content
from network import CharacterNetwork


def main(file_path):
    network = CharacterNetwork('abcdefgz', java_alphabet)

    num_of_chars = len(get_text_file_content(file_path))

    begin_time = time.process_time()
    network.train_on_file(file_path)
    end_time = time.process_time()

    execution_time = end_time - begin_time
    print('Execution time: %s, Chars per sec: %s' % (execution_time, num_of_chars / execution_time))
    # Execution time: 784.18588533, Chars per sec: 1.4868923578104514


if __name__ == "__main__":
    main(sys.argv[1])
