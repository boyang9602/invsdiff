A simple tool to compare similar daikon invariants files by ppt. `diff` is not suitable for this case because it compares file by line.  
It will record all the ppts only exist in left file and all the ppts only exist in right file.  
For the common ppts, it will record the invariants only exist in left file and all the invariants only exist in right file.  
### Usage:
    1 mvn clean compile assembly:single
    2 java -jar invsdiff-0.0.1-jar-with-dependencies.jar [options] invsfile0 invsfile1 invsfile2 ... invsfilen. 
    3 options
        --format=[format]. the ouput file format. it could be txt or json[default].
    4. The differences will be recorded into diff1.json(diff between file0 and file1) diff2.json(diff between file0 and file2) ... repectively
