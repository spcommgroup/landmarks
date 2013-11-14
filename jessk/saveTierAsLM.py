#!/usr/bin/python

"""
saveTierAsLM.py
Select a tier and save as .lm
I used this to make a tier easier to read in Java 
(.lm is a simpler file format)
"""

from TGProcess import *
import sys, os, itertools, operator, logging

def userOptions():
    """Choose which tier to save"""

    #Decide which tiers to operate on:
    print("Which tier do you want to save?")
    tiers = t.tiers
    print("Available tiers:")
    for tier_i, tier in zip(range(len(t.tiers)+1),t.tiers):
        print("\t"+str(tier_i)+" "+tier.name)
    useTier = input("Tier to save (default=0): ")
    try:
    	useTier = int(useTier)
    except ValueError:
    	useTier = 0
    	print("Using default (0)")

    return useTier

def process(t, useTier,destpath):
    """
    Removes all but tier useTier
    """

    o = TextGrid("ooTextFile","TextGrid","0 ",t.xmax,"exists",None, False)
    o.append(t.tiers[useTier])
    o.saveAsLM(destpath)
    
    return o

#If is program was run on its own (not imported into another file):
if __name__=="__main__":

    print("saveTierAsLM.py: save 1 tier as .lm file")
    print("")
    if len(sys.argv) < 2:
        exit("Usage: python saveTierAsLM.py /Path/To/File.TextGrid")
    filepath = os.path.abspath(sys.argv[1])
    pathsplit = os.path.splitext(filepath)

    t = TextGrid(filepath=filepath, oprint=False)
    useTier = userOptions();
    destpath = pathsplit[0]+"_tier"+str(useTier)+".lm"
    process(t,useTier,destpath)
    print("Finished. Saved as "+destpath)
    print("")

