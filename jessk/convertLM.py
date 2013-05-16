#!/usr/bin/python

from TGProcess import *
import sys
import os
# import re

class Rule:
    def __init__(self,liuLM):
        self.liuLM = liuLM
    def __str__(self):
        #return str(self.choiLM) + " -> " + str(self.liuLM)
        return str(self.liuLM)
    __repr__ = __str__
    def toLiu(self, t,o):
        return None
    # @classmethod
    # def find(cls, rules, choiLM):
    #     return [rule for rule in rules if rule.choiLM == choiLM][0]

def compileRules():
    f = open("rules.txt", "r")
    rows = f.read().split('\n')
    rules = {}
    for row in rows:
        items = row.split("/")
        if len(items) == 5: 
            rules[items[0]+"-"+items[1]] = items[2:]
        # else: 
        #     print("Error: "+str(items))
    # rules = {row.split("/")[0]+"-"+row.split("/")[1]: Rule(row.split("/")[2:]) for row in rows};
    LMtypes = {"V": "V", "#": "#"}
    LMtypes.update({nc : "Nc" for nc in ["n-cl","m-cl","ng-cl"]})
    LMtypes.update({nr : "Nr" for nr in ["n","m","ng"]})
    LMtypes.update({g : "G" for g in ["r","l","w","y","h"]})
    LMtypes.update({fc : "Fc" for fc in ["v-cl","f-cl","z-cl","s-cl","th-cl","dh-cl","sh-cl","j-cl","dj-1","ch-1"]})
    LMtypes.update({fr : "Fr" for fr in ["v","f","z","s","th","dh","sh","j","dj-2","ch-2"]})
    LMtypes.update({sc : "Sc" for sc in ["b-cl","p-cl","t-cl","d-cl","k-cl","g-cl","dj-cl","ch-cl"]})
    LMtypes.update({sr : "Sr" for sr in ["b","p","t","d","k","g"]})
    return rules, LMtypes

def userInterface(t):
    return None

def process(t,o, rules, LMtypes):
    for (tier_i, tier) in zip(range(len(t.tiers)), t.tiers):
        toAdd = [] # Add new items at the end so they don't interfere with ids
        (last_item_i, last_item) = (None, None)
        for (item_i, item) in zip(range(len(tier.items)), tier.items):
            if(item_i > 0):
                try:
                    rule = rules[LMtypes[last_item.mark]+"-"+LMtypes[item.mark]]
                except KeyError:
                    print("Warning: Item not found in conversion table: "+last_item.mark+" - "+item.mark)
                    # TODO handle error
                    continue
                print("Applying rule "+str(rule)+" to "+last_item.mark+" - "+item.mark)
                if(o.tiers[tier_i].items[last_item_i].mark==""):
                    print("\tChanging empty li to "+rule[0])
                    o.tiers[tier_i].items[last_item_i].mark = rule[0]
                elif(o.tiers[tier_i].items[last_item_i].mark != rule[0]):# and rule[0]!=""):
                    print("\tWarning: changed "+o.tiers[tier_i].items[last_item_i].mark+" to "+rule[0])
                    o.tiers[tier_i].items[last_item_i].mark = rule[0]
                if rule[1]:
                    print("\tQueuing point to add "+rule[1])
                    #o.tiers[tier_i].addPoint(Point(str(float(item.time)-.001), rule[1]))
                    toAdd.append(Point(str(float(item.time)-.001), rule[1]))
                print("\tChanging "+o.tiers[tier_i].items[item_i].mark+" to "+rule[2])
                o.tiers[tier_i].items[item_i].mark = rule[2]
            (last_item_i, last_item) = (item_i, item)
        for a in toAdd:
            print("Adding point "+str(a))
            o.tiers[tier_i].addPoint(a)
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

