# deep-features-for-plagiarism-detection

### Prototype

In folder `features-calculation` there is a subfolder named `prototype`. It contains a prototype of a tool which can possibly detect code duplicates.

**How to configure**:

1. Inside `features-calculation/prototype` create an empty subfolder named `snippets`.
2. Inside `features-calculation` create a subfolder named `networks`. Prototype expects that a trained network named `network` resides in this folder. So, there must be a binary file `features-calculation/networks/network`.
3. In the same way prototype assumes existance of a binary file `features-calculation/svm/standard` which contains a trained instance of SVM.

**How to run**:
Prototype has two commands:

- *Add pull-request from github to "database"*
Command line arguments: `--add https://github.com/Ivan-Veselov/pull-request-test/pull/3`
This command will save features of all methods that can be found in a given PR to `snippets` folder.
- *Find methods from a given pull-request that has similar pairs inside the "database"*
Command line arguments: `--find-similarities https://github.com/Ivan-Veselov/pull-request-test/pull/4`
This command compares methods from given pull-request with methods that were inserted inside the "database" by the previous command and shows the results of comparison.
