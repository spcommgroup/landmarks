"""
LexiconFromTextGrid.py
Creates a targeted lexicon from a TextGrid file, so that the matcher can use it to get a very close match. 
"""

from TGProcess import *

f = False
while not f:
	path = input("Path to .textgrid file: ")
	if path.lower().endswith(".textgrid"):
		try:
			f = open(path, "r")
		except IOError:
			print ("Couldn't open file")
	else:
		print ("File must be .TextGrid format")


tg = TextGrid()
tg.readGridFromPath(path)
wtier = None
for tier in tg:
	if ('word' in tier.name or 'conv' in tier.name) and isinstance(tier, IntervalTier):
		wtier = tier
		break
if not wtier and isinstance(tg[0], IntervalTier):
	wtier = tg[0]
if wtier:
	print ("Found word tier "+wtier.name)
	ok = input("Use this tier (Yes/no)? ").lower()
else:
	ok = "no"

if ok=="no":
	while ok=="no":
		for tier, i in zip(tg,range(len(tg))):
			if isinstance(tier, IntervalTier):
				print(i, " ", tier.name)
		use_id = input("Choose tier to use: ")
		try:
			use_id = int(use_id)
			if isinstance(tg[use_id],IntervalTier):
				ok = "yes"
			else:
				print("Please select an Interval tier")
		except ValueError:
			print ("Please select tier number")
	wtier = tg[use_id]

for interval in wtier:

# UNFINISHED. Using LexiconExtract.py in the "landmark" repository instead.