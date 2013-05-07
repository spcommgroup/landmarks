#!/usr/bin/python

from TGProcess import *
import sys
import os
# import re

class Rule:
    def __init__(self,fromLM,toLM,tier=-1):
        self.fromLM = fromLM
        self.toLM = toLM
        self.tier = tier
    def __str__(self):
        return self.fromLM + " -> " + self.toLM
    __repr__ = __str__
    def apply(self, item):
        if type(item)==Point and item.mark == self.fromLM:
            print("Applied rule "+str(self)+" on "+str(item))
            #TODO: add tier constraint to rule, if needed
            #if tier>-1 or 
            item.mark=self.toLM

def userInterface(t):
    #Rules initialization
    rules = [Rule("x","k-fric-+"),Rule("V","example-bad-rule")]

    print("The following rules are available:")
    for (i,rule) in zip(range(len(rules)),rules):
        print("\t" + str(i+1) + ") " + str(rule))
    if(input("Apply all rules? (Y/n) ")=="n"):
        rlist = input("Enter a space-separated list of rules: ").split(" ")
        rules = [rules[int(i)-1] for i in rlist]

    return rules

def process(t, rules):
    for (tier_i, tier) in zip(range(len(t.tiers)), t.tiers):
        for (item_i, item) in zip(range(len(tier.items)), tier.items):
            for rule in rules:
                rule.apply(item)
                    

#If is program was run on its own (not imported into another file):
if __name__=="__main__":
    if len(sys.argv) < 2:
        exit("Usage: python convertLM.py /Path/To/File.TextGrid")
    filepath = os.path.abspath(sys.argv[1])
    pathsplit = os.path.splitext(filepath)
    destpath = pathsplit[0] + ".processed" + pathsplit[1]
    
    t = TextGrid(filepath=filepath)
        
    rules = userInterface(t)
    print("Processing TextGrid...\n")
    process(t,rules)
    t.writeGridToPath(destpath)
    print("File written to " + destpath +  ".")
    print("The TextGrid Processor has finished.")
    print("")

