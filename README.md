A simple tool to compare similar daikon invariants files by ppt. `diff` is not suitable for this case because it compares file by line.  
It will record all the ppts only exist in left file and all the ppts only exist in right file.  
For the common ppts, it will record the invariants only exist in left file and all the invariants only exist in right file.  
### Usage:
1. java -cp *pathToTheClasses* ca.concordia.apr.invsdiff.App invsfile0 invsfile1 invsfile2 ... invsfilen. The differences will be recorded into diff1.txt(diff between file0 and file1) diff2.txt ... repectively
2. pack it to a jar
    - mvn package
    - java -jar invsdiff-0.0.1.jar invsfile0 invsfile1 invsfile2 ... invsfilen
3. import it to eclipse and set running args