import os
from random import randint


# todo: watch out hardcoded strings


def do(repo):
    folder = repo.split('/')[-1]
    os.system("git clone " + repo)

    sources = []
    for root, _, files in os.walk(folder):
        for file in files:
            _, ext = os.path.splitext(file)
            if ext == '.java':
                sources += [os.path.join(root, file)]

    if len(sources) != 0:
        file_path = sources[randint(0, len(sources) - 1)]
        os.system("cp " + file_path + " sources")

    os.system("rm -rf " + folder)


def main():
    with open('deep-features/repos') as f:
        i = 0
        for repo in f:
            do(repo.strip())
            i = i + 1
            print(i, 'repos processed')


if __name__ == "__main__":
    main()
