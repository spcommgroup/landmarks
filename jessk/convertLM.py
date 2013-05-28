#!/usr/bin/python

# TODO: Combine 2 tiers, or all tiers?

from TGProcess import *
import sys
import os
import itertools
import operator

def compileRules():
    """ Uses rules.txt to create dictionary from string pair of Choi LM to list of Liu LM .
    Example: "#-Sr": ["", "", "+b, (+g)"] 
    Also creates dictionary from Choi LMs to LM types"""

    f = open("rules.txt", "r")
    rules = {}
    def ifAppend(i,a):
        if (i==""): return "" 
        return i+a

    suffix_pairs = itertools.permutations(["","-x","-+"],2)
    #print("Suffix pairs:"+str(suffix_pairs))
    for line in f:
        items = line.strip("\n").split("/")
        if len(items) == 5: # keeps empty lines and such from being added
            #print("Adding rule "+line)
            for s_0 in ["","-x","-+"]:
                for s_1 in ["","-x","-+"]:
                    #print(spair)
                    rules[items[0]+s_0+"-"+items[1]+s_1] = [ifAppend(items[2],s_0), ifAppend(items[3],s_0), ifAppend(items[4],s_1)]
                    #if(items[0]=="#" and items[1]=="V"):
                    #    print("Adding "+items[0]+s_0+"-"+items[1]+s_1+" -> "+ str([ifAppend(items[2],s_0), ifAppend(items[3],s_0), ifAppend(items[4],s_1)]))
            # rules[items[0]+"-"+items[1]] = items[2:]
            # rules[items[0]+"-x-"+items[1]] = [ifAppend(items[2],"-x"), ifAppend(items[3],"-x"), items[4]]
            # rules[items[0]+"-+-"+items[1]] = [ifAppend(items[2],"-+"), ifAppend(items[3],"-+"), items[4]]
            # rules[items[0]+"-"+items[1]+"-x"] = [items[2], items[3], ifAppend(items[4],"-x")]
            # rules[items[0]+"-"+items[1]+"-+"] = [items[2], items[3], ifAppend(items[4],"-+")]
        #else:
        #    print("Skipped line "+line)

    # Dictionary of conversions from Choi LM (e.g. ng-cl) to LM type (e.g. Nc)
    LMtypes = {"V": "V", "#": "#"}
    LMtypes.update({h  : "#"  for h  in ["<OS>","<breath>"]})
    LMtypes.update({nc : "Nc" for nc in ["n-cl","m-cl","ng-cl"]})
    LMtypes.update({nr : "Nr" for nr in ["n","m","ng"]})
    LMtypes.update({g  : "G"  for g  in ["r","r-cl","l","w","y","h"]})
    LMtypes.update({fc : "Fc" for fc in ["v-cl","f-cl","z-cl","s-cl","th-cl","dh-cl","sh-cl","j-cl","dj-1","ch1","jh1"]})
    LMtypes.update({fr : "Fr" for fr in ["v","f","z","s","th","dh","sh","j","dj-2","ch2","jh2"]})
    LMtypes.update({sc : "Sc" for sc in ["b-cl","p-cl","t-cl","d-cl","k-cl","g-cl","dj-cl","ch-cl","jh-cl"]})
    LMtypes.update({sr : "Sr" for sr in ["b","p","t","d","k","g"]})

    LMtypes_plus, rules_plus = ({},{})
    for key in LMtypes:
        LMtypes_plus[key+"-x"]= LMtypes[key]+"-x"
        LMtypes_plus[key+"-+"]= LMtypes[key]+"-+"
    LMtypes.update(LMtypes_plus)
    # for key in rules:
    #     f,t = key.split("-")
    #     rules_plus[f+"-x-"+t]= []
    #     rules_plus[key+"-x"]= [a+"-x" for a in rules[key] if a != ""]
    #     rules_plus[key+"-+"]= [a+"-+" for a in rules[key] if a != ""]
    # rules.update(rules_plus)

    # for i in rules:
    #     if i.startswith("#"):
    #         print(i+"\t"+str(rules[i]))

    #Decide which tiers to operate on:
    tiers = t.tiers
    print("Available tiers:")
    default = ""
    for tier_i, tier in zip(range(len(t.tiers)),t.tiers):
        print("\t"+str(tier_i)+" "+tier.name)
        default += ","+str(tier_i)
    useTiers = input("Tiers to use (default="+default.strip(",")+"): ").split(",")
    if useTiers != "":
        for thing in useTiers:
            try:
                i = int(thing)
                if i<0 or i>len(t.tiers):
                    useTiers = default.split(",")
                    print("Invalid input, using default")
                    break
            except ValueError:
                useTiers = default.split(",")
                print("Invalid input, using default")
                break
    else:
        useTiers = default.split(",")
    return rules, LMtypes, useTiers

def process(t,o, rules, LMtypes, useTiers):
    not_found = {}
    for tier_i in useTiers:
        tier_i = int(tier_i)
        tier  = t.tiers[tier_i] # input tier
        otier = o.tiers[tier_i] # output tier
        toAdd = [] # Add new items at the end so they don't interfere with ids
        (last_item_i, last_item) = (None, None)
        for (item_i, item) in zip(range(len(tier.items)), tier.items):
            if item_i == 0 and type(item)==Point: #First item, implicit "#" beforehand
                if not item.mark.strip() in LMtypes:
                    if not item.mark.strip() in not_found:
                        not_found[item.mark.strip()] = item.time
                    #print("Warning: Item not found in conversion table: "+item.mark)
                    otier.items[item_i].mark=""
                else:
                    if not "#-"+LMtypes[item.mark] in rules:
                        print("Warning: Rule not found: "+"#-"+LMtypes[item.mark])
                    else:
                        rule = rules["#-"+LMtypes[item.mark]]
                        #print("Applying rule "+str(rule)+" to # - "+item.mark)
                        otier.items[item_i].mark = rule[2]

            elif(item_i > 0 and type(item)==Point): #starts on second item, so that last_item is defined
                e=0
                #if last_item == None:
                #    print("What?" + item.mark)
                if not last_item.mark.strip() in LMtypes:
                    if not last_item.mark.strip() in not_found:
                        not_found[last_item.mark.strip()] = last_item.time
                    #print("Warning: Item not found in conversion table: "+last_item.mark)
                    otier.items[last_item_i].mark=""
                    e=1
                if not item.mark.strip() in LMtypes:
                    if not item.mark.strip() in not_found:
                        not_found[item.mark.strip()] = item.time
                    #print("Warning: Item not found in conversion table: "+item.mark)
                    otier.items[item_i].mark=""
                    e=1
                if e==0: 
                    rule = rules[LMtypes[last_item.mark.strip()]+"-"+LMtypes[item.mark.strip()]]
                    #print("Applying rule "+str(rule)+" to "+last_item.mark+" - "+item.mark)
                    if(otier.items[last_item_i].mark==""):
                        #print("\tChanging empty li to "+rule[0])
                        otier.items[last_item_i].mark = rule[0]
                    elif(otier.items[last_item_i].mark.strip() != rule[0] and rule[0]!=""):
                        print("\tWarning: changed "+otier.items[last_item_i].mark+" to "+rule[0])
                        otier.items[last_item_i].mark = rule[0]
                    if rule[1]:
                        #print("\tQueuing point to add "+rule[1])
                        toAdd.append(Point(str(float(item.time)-.001), rule[1]))
                    if otier.items[item_i].mark in ["+g","-g","+b","-b","+s","-s"]:
                        print("\tChanging "+otier.items[item_i].mark+" to "+rule[2])
                    otier.items[item_i].mark = rule[2]
            (last_item_i, last_item) = (item_i, item)

        # Apply last rule, X-# at end of tier
        if type(last_item)==Point and not last_item.mark.strip() in LMtypes:
            if not last_item.mark.strip() in not_found:
                not_found[last_item.mark.strip()] = last_item.time
            #print("Warning: Item not found in conversion table: "+last_item.mark)
            otier.items[last_item_i].mark=""
        elif type(last_item)==Point: 
            rule = rules[LMtypes[last_item.mark.strip()]+"-#"]
            #print("Applying rule "+str(rule)+" to "+last_item.mark+" - #")
            if(o.tiers[tier_i].items[last_item_i].mark.strip()==""):
                #print("\tChanging empty li to "+rule[0])
                otier.items[last_item_i].mark = rule[0]
            elif(otier.items[last_item_i].mark.strip() != rule[0] and rule[0]!=""):
                print("\tWarning: changed "+otier.items[last_item_i].mark+" to "+rule[0])
                otier.items[last_item_i].mark = rule[0]
            if rule[1]:
                #print("\tQueuing point to add "+rule[1])
                toAdd.append(Point(str(float(tier.xmax)-.001), rule[1]))
            
        for a in toAdd:
            #print("Adding point "+str(a))
            otier.addPoint(a)
        otier.removeBlankPoints()
    print("Not found marks:")
    for mark,time in sorted(not_found.items(),key=operator.itemgetter(1)):
        print("\t"+mark + " ("+time +")")
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

    rules, LMtypes, useTiers = compileRules();
    print("Processing TextGrid...\n")
    process(t,o,rules, LMtypes, useTiers)
    o.writeGridToPath(destpath)
    print("File written to " + destpath +  ".")
    print("The TextGrid Processor has finished.")
    print("")

