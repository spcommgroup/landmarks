import csv
import re
import sys

if len(sys.argv) < 3:
    exit("Usage: python outToCSV.py condition-name /Path/To/InputFile.txt /Path/To/OutputFile.csv")

condition_name= sys.argv[1]
in_path = sys.argv[2]
out_path = sys.argv[3]


cond_re = re.compile(r'\t"' + condition_name + r'": (.*),\n')
wpr_re = re.compile(r'\t"wordsPerRanking": (.*),\n')
pps_re = re.compile(r'\t"partitioningsPerSentence": (.*),\n')

txtFile = open(in_path, 'r')

writer = csv.writer(open(out_path, 'w'))
writer.writerow([condition_name, 'Words Per Ranking', 'Partitionings Per Sentence'])

line = txtFile.readline()
while line != "":
    assert line == "{\n"

    line = txtFile.readline()
    m = re.match(cond_re, line)
    assert m != None
    cond = m.group(1)

    line = txtFile.readline()
    m = re.match(wpr_re, line)
    assert m != None
    wpr = m.group(1)

    line = txtFile.readline()
    m = re.match(pps_re, line)
    assert m != None
    pps = m.group(1)
    
    writer.writerow([cond, wpr, pps])

    line = txtFile.readline()
    assert line == "},\n"

    line = txtFile.readline()

    
print "File from %s converted to CSV (Excel-readable) format and saved to %s)" % (in_path, out_path)
