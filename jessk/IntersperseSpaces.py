"""
IntersperseSpaces.py
takes two .lm files and adds the spaces at times from one into the other. 
The purpose of this is to take a file with landmarks and split it into 
words based on the word boundaries in the phones file.
"""

from TGProcess import *
import sys 
# Require Python 3.x
if sys.version_info[0] < 3:
    print("Error: The TextGrid processor requires Python 3.0 or above. Exiting.\n")
    sys.exit(1)

if len(sys.argv) == 3:
    fpath = sys.argv[1]
    f2path = sys.argv[2]
elif len(sys.argv) == 2:
    fpath = sys.argv[1]
f = None
while not f:
    try:
        if not fpath.lower().endswith(".lm"): raise Exception("Invalid file type.")
        f = open(fpath, "r")
    except:
        fpath = input("Enter the path of the main .lm file: ")
f2 = None
while not f2:
    try:
        if not f2path.lower().endswith(".lm"): raise Exception("Invalid file type.")
        f2 = open(f2path, "r")
    except:
        f2path = input("Enter the path of the .lm file to take spaces from: ")

t = TextGrid(filepath=fpath, oprint=False)
for line in f2:
    linesplit = line.strip("\n").split()
    if not line.startswith("#") and (len(linesplit)==1 or linesplit[1] in " _#"):
        ltime = float(linesplit[0])
        if len([point for point in t[0] if point.time == ltime]) > 0:
            ltime -= .001
        if t[0].xmax < ltime:
            ltime = t[0].xmax
        t[0].insert(Point(ltime, "_"))
outpath = ".".join(fpath.split(".")[:-1])+"_spaces.lm"
t.saveAsLM(outpath)
print("File saved as "+outpath)