import glob
import subprocess


def collect_files():
    files = glob.glob("scripts/*.lox")
    print("Collected {} files for test: \n\t{}".format(len(files), "\n\t".join(files)))
    return files


def run_test_script(file):
    print("Testing {}".format(file))
    # proc = subprocess.Popen("python", stdout=subprocess.PIPE)
    # output = proc.stdout.read()
    # print(output)


files = collect_files()
for file in files:
    run_test_script(file)
