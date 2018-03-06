## train.py

`python3 train.py network_name path/to/data`

Trains network from `network/network_name` file on all files from folder `path/to/data`. Number of epochs is 2000. Saves training results after processing of one more file.

## run_char_rnn.py

`python3 run_char_rnn.py network_name`

Tries to generate a text by using network `network_name`. Beginning part of generated sentece is hardcoded into the script.
