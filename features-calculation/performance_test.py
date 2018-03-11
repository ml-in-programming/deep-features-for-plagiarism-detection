import sys
import time

from alphabet import java_alphabet
from commons import get_text_file_content
from network import CharacterNetwork


def main(file_path):
    network = CharacterNetwork('abcdefgz', java_alphabet)

    num_of_chars = len(get_text_file_content(file_path))

    begin_cpu_time = time.process_time()
    begin_real_time = time.perf_counter()
    network.train_on_file(file_path)
    end_real_time = time.perf_counter()
    end_cpu_time = time.process_time()

    cpu_execution_time = end_cpu_time - begin_cpu_time
    real_execution_time = end_real_time - begin_real_time
    print('consumed CPU time: %s\nelapsed wall-clock time: %s\ncharacters per second: %s' %
          (cpu_execution_time, real_execution_time, num_of_chars / real_execution_time))

    # consumed CPU time: 4814.927231151
    # elapsed wall-clock time: 1722.6947384289997
    # characters per second: 119.09364754146527


if __name__ == "__main__":
    main(sys.argv[1])
