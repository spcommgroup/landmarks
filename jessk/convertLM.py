#!/usr/bin/python

from TGProcess import *
import sys
import os

def compileRules():
    """ Uses rules.txt to create dictionary from string pair of Choi LM to list of Liu LM .
    Example: "#-Sr": ["", "", "+b, (+g)"] 
    Also creates dictionary from Choi LMs to LM types"""

    f = open("rules.txt", "r")
    rules = {}
    for line in f:
        items = line.strip("\n").split("/")
        if len(items) == 5: # keeps empty lines and such from being added
            rules[items[0]+"-"+items[1]] = items[2:]

    # Dictionary of conversions from Choi LM (e.g. ng-cl) to LM type (e.g. Nc)
    LMtypes = {"V": "V", "#": "#"}
    LMtypes.update({nc : "Nc" for nc in ["n-cl","m-cl","ng-cl"]})
    LMtypes.update({nr : "Nr" for nr in ["n","m","ng"]})
    LMtypes.update({g : "G" for g in ["r","l","w","y","h"]})
    LMtypes.update({fc : "Fc" for fc in ["v-cl","f-cl","z-cl","s-cl","th-cl","dh-cl","sh-cl","j-cl","dj-1","ch-1"]})
    LMtypes.update({fr : "Fr" for fr in ["v","f","z","s","th","dh","sh","j","dj-2","ch-2"]})
    LMtypes.update({sc : "Sc" for sc in ["b-cl","p-cl","t-cl","d-cl","k-cl","g-cl","dj-cl","ch-cl"]})
    LMtypes.update({sr : "Sr" for sr in ["b","p","t","d","k","g"]})
    return rules, LMtypes

def process(t,o, rules, LMtypes):
    for (tier_i, tier) in zip(range(len(t.tiers)), t.tiers):
        otier = o.tiers[tier_i] # output tier
        toAdd = [] # Add new items at the end so they don't interfere with ids
        (last_item_i, last_item) = (None, None)
        for (item_i, item) in zip(range(len(tier.items)), tier.items):
            if(item_i > 0): #starts on second item, so that last_item is defined
                try:
                    rule = rules[LMtypes[last_item.mark]+"-"+LMtypes[item.mark]]
                except KeyError:
                    print("Warning: Item not found in conversion table: "+last_item.mark+" - "+item.mark)
                    # TODO handle error, probably by removing LM
                    continue
                print("Applying rule "+str(rule)+" to "+last_item.mark+" - "+item.mark)
                if(o.tiers[tier_i].items[last_item_i].mark==""):
                    print("\tChanging empty li to "+rule[0])
                    otier.items[last_item_i].mark = rule[0]
                elif(otier.items[last_item_i].mark != rule[0]):# and rule[0]!=""):
                    print("\tWarning: changed "+otier.items[last_item_i].mark+" to "+rule[0])
                    otier.items[last_item_i].mark = rule[0]
                if rule[1]:
                    print("\tQueuing point to add "+rule[1])
                    toAdd.append(Point(str(float(item.time)-.001), rule[1]))
                print("\tChanging "+otier.items[item_i].mark+" to "+rule[2])
                otier.items[item_i].mark = rule[2]
            (last_item_i, last_item) = (item_i, item)
        for a in toAdd:
            print("Adding point "+str(a))
            otier.addPoint(a)
        # TODO: Remove empty points
    return None

#If is program was run on its own (not imported into another file):
if __name__=="__main__":
    if len(sys.argv) < 2:
        exit("Usage: python convertLM.py /Path/To/File.TextGrid")
    filepath = os.path.abspath(sys.argv[1])
    pathsplit = os.path.splitext(filepath)
    destpath = pathsplit[0] + ".processed" + pathsplit[1]
    
    t = TextGrid(filepath=filepath)
    o = TextGrid(filepath=filepath)#output textgrid
        
    # rules = userInterface(t)

    rules, LMtypes = compileRules();
    print("Processing TextGrid...\n")
    process(t,o,rules, LMtypes)
    o.writeGridToPath(destpath)
    print("File written to " + destpath +  ".")
    print("The TextGrid Processor has finished.")
    print("")

