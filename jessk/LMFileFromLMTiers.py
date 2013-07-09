"""
LMFileFromLMTiers.py
Extracts the LM and LMmods tiers from a TextGrid and saves them as an .lm file.

Optionally "generalizes" LMs. e.g. converting t-cl to Sc
"""
from TGProcess import *
import sys
# Require Python 3.x
if sys.version_info[0] < 3:
    print("Error: The TextGrid processor requires Python 3.0 or above. Exiting.\n")
    sys.exit(1)

if len(sys.argv) == 2:
    fpath = sys.argv[1]
f = None
while not f:
    try:
        if not fpath.lower().endswith(".textgrid"): raise Exception("Invalid file type.")
        f = open(fpath, "r")
    except:
        fpath = input("Enter the path of the textgrid file: ")
t = TextGrid(filepath=fpath, oprint=False)
lmTier = None
modsTier = None
for tier in t:
    if tier.name.lower()=="lm":
        lmtcheck = input("Found tier "+tier.name+". Use as LM tier (yes/no)? ")
        if lmtcheck=="yes":
            lmTier = tier
    if tier.name.lower()=="lmmods":
        lmtcheck = input("Found tier "+tier.name+". Use as LMmods tier (yes/no)? ")
        if lmtcheck=="yes":
            modsTier = tier
if not (lmTier and modsTier):
    for i, tier in enumerate(t):
        print(str(i)+") "+tier.name)
    if not lmTier:
        lmtid = input("LM tier: ")
        lmTier = t[int(lmtid)]
    if not modsTier:
        mtid = input("LMmods tier: ")
        modsTier = t[int(mtid)]
print("Which LM types do you want to include: ")
print("1) Just LM tier (default)")
print("2) LM tier and -+ LMs")
print("3) LM tier and -x LMs")
print("4) LM tier, -+ LMs, and -x LMs")
lmtypes = input("> ")

combined_tiers = [item for item in lmTier]
if lmtypes in "24":
    combined_tiers.extend([item for item in modsTier if item.mark.strip().endswith("-+")])
if lmtypes in "34":
    combined_tiers.extend([item for item in modsTier if item.mark.strip().endswith("-x")])

if input("Do you want to generalize LMs? (e.g. t-cl-+ -> Sc) ")=="yes":
    conversionTable = {"V": "V", "#": "#"}
    conversionTable.update({h  : "#"  for h  in ["<OS>","<breath>"]})
    conversionTable.update({nc : "Nc" for nc in ["n-cl","m-cl","ng-cl"]})
    conversionTable.update({nr : "Nr" for nr in ["n","m","ng"]})
    conversionTable.update({g  : "G"  for g  in ["r","r-cl","l","w","y","h","t-glide","d-glide","g-glide","k-glide","dh-glide","v-glide","l-cl","l-rl","m-glide"]})
    conversionTable.update({fc : "Fc" for fc in ["v-cl","f-cl","z-cl","s-cl","th-cl","dh-cl","sh-cl","j-cl","dj-1","ch1","jh1","h-cl","d-fric-cl","k-fric-cl","dh-cl-?"]})
    conversionTable.update({fr : "Fr" for fr in ["v","f","z","s","th","dh","sh","j","dj-2","jh-2","ch2","jh2","t-fric","d-fric","k-fric","g-fric","h-rl"]})
    conversionTable.update({sc : "Sc" for sc in ["b-cl","p-cl","t-cl","d-cl","k-cl","g-cl","dj-cl","ch-cl","jh-cl","dh-stop-cl"]})
    conversionTable.update({sr : "Sr" for sr in ["b","p","t","d","k","g","dh-stop","k-fric-rl","gl-stop","t-glot"]})

    for item in combined_tiers:
        item.mark = item.mark.strip()
        if item.mark.endswith("-+") or item.mark.endswith("-x"):
            if item.mark[:-2] in conversionTable:
                item.mark = conversionTable[item.mark[:-2]]
            else:
                print("Warning: LM not found in table: "+item.mark[:-2])

        elif item.mark in conversionTable:
            item.mark = conversionTable[item.mark]
        else:
            print("Warning: LM not found in table: "+item.mark)

combined_tiers.sort(key=lambda item: item.time)
o = TextGrid(oprint=False)
o.append(combined_tiers)
opath = ".".join(fpath.split(".")[:-1])+".LMtiers"
o.saveAsLM(opath)
print("Saved as "+opath+".lm")