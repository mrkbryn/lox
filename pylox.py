import sys
from scanner import Scanner


def run_file(script):
    print("Running with source: {}".format(script))
    with open(script) as f:
        for line in f:
            f.run(line)

def run_prompt():
    while True:
        line = input("> ")
        scanner = Scanner(line)
        tokens = scanner.scan_tokens()
        print(tokens)


if __name__ == "__main__":
    if len(sys.argv) > 2:
        print("Usage: pylox [script]")
        exit(1)
    if len(sys.argv) > 1:
        run_file(sys.argv[1])
    else:
        run_prompt()
