import os
import sys


# todo: watch out hardcoded strings

def sshVersion(repo):
    return 'git@github.com:' + repo[len('https://github.com/'):]


def do(repo):
    folder = repo.split('/')[-1]
    os.system("git clone " + repo)
    os.system("./transform.sh " + os.path.abspath(folder))
    os.system("cp -r " + folder + "/transformation/ pairs/" + folder)
    os.system("rm -rf " + folder)


def main(projects_list):
    with open(projects_list) as f:
        i = 0
        for repo in f:
            do(sshVersion(repo.strip()))
            i = i + 1
            print(i, 'repos processed')


if __name__ == "__main__":
    main(sys.argv[1])
