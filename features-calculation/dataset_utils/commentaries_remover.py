import re
import sys
import os

from commons import get_text_file_content


def remove_comments(source):
    def repl(matchobj):
        if matchobj.group(3) is not None:
            return '""'
        else:
            if matchobj.group(4) is not None:
                return "''"
            else:
                return ''

    return re.sub(r'(/\*.*?\*/)|(//.*?$)|(".*?")|(\'.*?\')', repl, source, flags=re.DOTALL | re.MULTILINE)


def main(dir_name):
    all_files = []
    for root, _, files in os.walk(dir_name):
        for file in files:
            all_files.append(os.path.join(root, file))

    for file in all_files:
        content = get_text_file_content(file)
        content = remove_comments(content)

        pref, name = os.path.split(dir_name)
        if len(name) > 0:
            pref = os.path.join(pref, name)

        path = os.path.join(pref + '-no_comments', file)
        if not os.path.exists(os.path.dirname(path)):
            os.makedirs(os.path.dirname(path))

        with open(path, 'w+') as f:
            f.write(content)

if __name__ == "__main__":
    main(sys.argv[1])
