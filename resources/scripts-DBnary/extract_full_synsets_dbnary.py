# -*- coding: utf-8 -*-
# Author: Zied Elloumi 
# Affiliation: LIG -- GETALP -- Univ. Grenoble Alpes (France)
# Contact: zied.elloumi@imag.fr
#

import sys;
import re;
import os;
import commands;
import string
from SPARQLWrapper import SPARQLWrapper, JSON
import ast

def ExtractFullSynsets(server, language_code_iso, language_code_dbnary):
	dict_syn={};
	vocab=[];
	sparql = SPARQLWrapper(server)
	sparql.setQuery("""
	PREFIX lexvo: <http://lexvo.org/id/iso639-3/>
	PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>
	SELECT distinct ?word  ?syn from <http://kaiko.getalp.org/dbnary/"""+language_code_dbnary+""">
	WHERE { 
 		?syn dbnary:synonym ?word. 
		?word dbnary:refersTo ?le.
		?le lemon:language '"""+language_code_iso+"""'
	 } LIMIT 10000 OFFSET 0 """)
	sparql.setReturnFormat(JSON)
	results = sparql.query().convert()
	limite=0	
	print len(results ["results"]["bindings"])
	
	while len(results["results"]["bindings"]) > 1:
		print "limite",limite
		for i in range (0,len(results ["results"]["bindings"])):
			word=results ["results"]["bindings"][i]["word"]["value"].replace("http://kaiko.getalp.org/dbnary/"""+language_code_dbnary+"/","").encode('utf-8').strip()
			syn= results ["results"]["bindings"][i]["syn"]["value"].replace("http://kaiko.getalp.org/dbnary/"""+language_code_dbnary+"/","").encode('utf-8').strip()	
			syn=re.sub(r"^\s*__\w+_\d+_(.+)__.+__\d+\s*$",r"\1",syn)
			syn=re.sub(r"^([^\_]+.+)__.+\_\_[0-9]$",r"\1",syn) 

			#vocab
			vocab.append(syn)
			vocab.append(word)

			if word in dict_syn.keys() : 
				dict_syn[word].append(syn)
			else:
				dict_syn[word]=[syn]

			
		limite=limite+10000		
		sparql = SPARQLWrapper(server)
		sparql.setQuery("""
			PREFIX lexvo: <http://lexvo.org/id/iso639-3/>
			PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>
			SELECT distinct ?word  ?syn from <http://kaiko.getalp.org/dbnary/"""+language_code_dbnary+""">
			WHERE { 
 				?syn dbnary:synonym ?word. 
				?word dbnary:refersTo ?le.
				 ?le lemon:language '"""+language_code_iso+"""' 
			} LIMIT 10000 OFFSET """+str(limite)+""" """)
		sparql.setReturnFormat(JSON)
	 	results = sparql.query().convert()
	post_processing (vocab,dict_syn,language_code_iso)
	return 0

def post_processing (vocab,dict_syn,language_code_iso):
	word_id=0
	fdict=open("synsets-dbnary."+language_code_iso,"w")
	syn_id={}
	for i in range (0,len(vocab)):
		word_id=word_id+1
		syn_id[vocab[i]]=str(word_id).zfill(8)
	
	for k in dict_syn.keys():
		syn_list=k+"\n"+syn_id[k];
		l=len(dict_syn[k])
		z=0
		for z in dict_syn[k] :
			syn_list=syn_list+" "+syn_id[z]
		fdict.write( syn_list+"\n")
	fdict.close()
	return 0 
	
	
	
	return 0
if __name__ == "__main__":
    langex=["eng","fra","rus","spa","deu"]
    langsh=["en","fr","ru","es","de"]
    for i  in range(0,len(langex)):
      print("Extraction of the dictionnary for language ",langsh[i],langex[i])
      ExtractFullSynsets("http://kaiko.getalp.org/sparql", langsh[i],langex[i])
