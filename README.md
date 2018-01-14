# MastersProject

This project is used to data mine knowledge corpuses to enrich knowledge graphs. The knowledge corpus we deal with is ConceptNet.

ModifyConceptNetCorpus is a class used to perform different operations on this corpus to have it fit for use. This code should be run as a once off to modify the corpus. It is very slow at running and may take several hours to run considering the size of the orignal corpus. 

In the Main class, we take in the ConceptNet corpus and different input knowledge graphs and find the missing background information. This class will use the other two classes of ExtendedTriplet and Triplet. 
