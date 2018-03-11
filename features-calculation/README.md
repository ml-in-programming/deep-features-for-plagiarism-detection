## train.py

`python3 train.py network_name path/to/data`

Trains network from `network/network_name` file (or creates such network) on all files from folder `path/to/data`. Number of epochs is 2000. Saves training results after processing one more file. For example, if `path/to/data` contains 100 files and there is 2000 epochs of training then network will be resaved 200 000 times.

## run_char_rnn.py

`python3 run_char_rnn.py network_name`

Tries to generate a text by using network `network_name`. Beginning part of generated sentece is hardcoded into the script.

## performance_test.py

`python3 performance_test.py sample-file`

Runs one training iteration and measures performance.
