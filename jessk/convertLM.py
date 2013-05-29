#!/usr/bin/python

from TGProcess import *
import sys, os, itertools, operator, logging

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
    for line in f:
        items = line.strip("\n").split("/")
        if len(items) == 5: # keeps empty lines and such from being added
            for s_0 in ["","-x","-+"]:
                for s_1 in ["","-x","-+"]:
                    rules[items[0]+s_0+"-"+items[1]+s_1] = [ifAppend(items[2],s_0), ifAppend(items[3],s_0), ifAppend(items[4],s_1)]

    # Dictionary of conversions from Choi LM (e.g. ng-cl) to LM type (e.g. Nc)
    LMtypes = {"V": "V", "#": "#"}
    LMtypes.update({h  : "#"  for h  in ["<OS>","<breath>"]})
    LMtypes.update({nc : "Nc" for nc in ["n-cl","m-cl","ng-cl"]})
    LMtypes.update({nr : "Nr" for nr in ["n","m","ng"]})
    LMtypes.update({g  : "G"  for g  in ["r","r-cl","l","w","y","h","t-glide","d-glide","g-glide","k-glide","dh-glide","v-glide","l-cl","l-rl","m-glide"]})
    LMtypes.update({fc : "Fc" for fc in ["v-cl","f-cl","z-cl","s-cl","th-cl","dh-cl","sh-cl","j-cl","dj-1","ch1","jh1","h-cl","d-fric-cl","k-fric-cl","dh-cl-?"]})
    LMtypes.update({fr : "Fr" for fr in ["v","f","z","s","th","dh","sh","j","dj-2","jh-2","ch2","jh2","t-fric","d-fric","k-fric","g-fric","h-rl"]})
    LMtypes.update({sc : "Sc" for sc in ["b-cl","p-cl","t-cl","d-cl","k-cl","g-cl","dj-cl","ch-cl","jh-cl","dh-stop-cl"]})
    LMtypes.update({sr : "Sr" for sr in ["b","p","t","d","k","g","dh-stop","k-fric-rl","gl-stop","t-glot"]})

    LMtypes_plus, rules_plus = ({},{})
    for key in LMtypes:
        LMtypes_plus[key+"-x"]= LMtypes[key]+"-x"
        LMtypes_plus[key+"-+"]= LMtypes[key]+"-+"
    LMtypes.update(LMtypes_plus)

    #Decide which tiers to operate on:
    print("Choose which tiers to convert. Any unselected tiers will remain intact. Selected tiers will be erased and rewritten.")
    tiers = t.tiers
    print("Available tiers:")
    default = ""
    for tier_i, tier in zip(range(len(t.tiers)+1),t.tiers):
        print("\t"+str(tier_i)+" "+tier.name)
        default += ","+str(tier_i)
    useTiers = input("Tiers to convert (default="+default.strip(",")+"): ").split(",")
    if useTiers != "":
        for thing in useTiers:
            try:
                i = int(thing)
                if i<0 or i>len(t.tiers):
                    useTiers = default.strip(",").split(",")
                    print("Invalid input, using default")
                    break
            except ValueError:
                useTiers = default.strip(",").split(",")
                print("Invalid input, using default")
                break
    else:
        useTiers = default.strip(",").split(",")
    return rules, LMtypes, useTiers

def process(t, rules, LMtypes, useTiers):
    logging.info("Running process()...")
    ctiers = []
    output = []
    not_found = {}
    print("Building TextGrid list...")
    for i in useTiers:
        ctiers.extend([(item.mark.strip(), float(item.time), int(i)) for item in t.tiers[int(i)].items if type(item)==Point and (item.mark.strip() in LMtypes)])
        not_found.update({item.mark: item.time for item in t.tiers[int(i)] if type(item)==Point and item.mark.strip() not in LMtypes})
    ctiers.sort(key=operator.itemgetter(1,2))
    ctiers.insert(0,("#",0.0,0))
    ctiers.append(("#",float(t.tiers[0].xmax),0))
    ctiers_no_xs = [item_t for item_t in ctiers if not item_t[0].endswith("-x")]
    print("Translating pairs...")
    for pair in zip(ctiers_no_xs, ctiers_no_xs[1:]):
        logging.debug("Operating on pair "+str(pair))
        error=0
        for item_t in pair:
            if not item_t[0] in LMtypes:
                if not item_t[0] in not_found: 
                    not_found[item_t[0]] = item_t[1]
                error=1
        if not error:
            try:
                rule = rules[LMtypes[pair[0][0]]+"-"+LMtypes[pair[1][0]]]
            except KeyError:
                logging.warning("RULE NOT FOUND: "+LMtypes[pair[0][0]]+"-"+LMtypes[pair[1][0]])
                continue
            if rule[0] and not [item_t for item_t in output if item_t[1]==pair[0][1] and item_t[0]==rule[0]]: #Item doesn't already exist 
                output.append((rule[0], pair[0][1], int(useTiers[1]) if rule[0].endswith("-+") else int(useTiers[0])))
            if rule[1]:
                output.append((rule[1], pair[1][1]-0.001, int(useTiers[1]) if rule[1].endswith("-+") else int(useTiers[0])))
            if rule[2]:
                output.append((rule[2], pair[1][1], int(useTiers[1]) if rule[2].endswith("-+") else int(useTiers[0])))
    print("Translating -x pairs...")
    for pair in zip(ctiers, ctiers[1:]):
        if pair[0][0].endswith("-x") or pair[1][0].endswith("-x"):
            error=0
            for item_t in pair:
                if not item_t[0] in LMtypes:
                    if not item_t[0] in not_found: 
                        not_found[item_t[0]] = item_t[1]
                    error=1
            if not error:
                try:
                    rule = rules[LMtypes[pair[0][0]]+"-"+LMtypes[pair[1][0]]]
                except KeyError:
                    logging.warning("RULE NOT FOUND: "+LMtypes[pair[0][0]]+"-"+LMtypes[pair[1][0]])
                    continue
                if rule[0].endswith("-x"):
                    output.append((rule[0], pair[0][1], int(useTiers[1]) )) 
                if rule[1].endswith("-x"):
                    output.append((rule[1], pair[1][1]-0.001, int(useTiers[1]) )) 
                if rule[2].endswith("-x"):
                    output.append((rule[2], pair[1][1], int(useTiers[1]) )) 

    logging.warning("Not found LMs:")
    for mark, time in not_found.items():
        logging.warning("\t"+mark+" "+str(time))

    #Recreate textgrid
    print("Recompiling TextGrid...")
    output.sort(key=operator.itemgetter(1,2))
    o = TextGrid("ooTextFile","TextGrid","0 ",t.xmax,"exists",None)
    for i in range(len(t.tiers)):
        if str(i) in useTiers:
            i = int(i)
            items = [item for item in output if item[2]==i]

            tier = Tier("TextTier",t.tiers[i].name,t.tiers[i].xmin,t.tiers[i].xmax)
            for item in items:
                point = Point(str(item[1]),item[0])
                tier.append(point)
            o.append(tier)
        else:
            o.append(t.tiers[i]) #copy over unused tier
    return o

#If is program was run on its own (not imported into another file):
if __name__=="__main__":

    print("convertLM.py: Convert Choi LM to Liu LM")
    print("")
    if len(sys.argv) < 2:
        exit("Usage: python convertLM.py /Path/To/File.TextGrid")
    filepath = os.path.abspath(sys.argv[1])
    pathsplit = os.path.splitext(filepath)
    destpath = pathsplit[0] + ".processed" + pathsplit[1]
    logging.basicConfig(filename='log.txt',level=logging.INFO)

    t = TextGrid(filepath=filepath)
    rules, LMtypes, useTiers = compileRules();
    print("Processing TextGrid...\n")
    o = process(t,rules, LMtypes, useTiers)
    o.writeGridToPath(destpath)
    print("File written to " + destpath +  ".")
    print("The TextGrid Processor has finished.")
    print("")

