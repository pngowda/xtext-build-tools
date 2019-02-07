
def addUpstream(upstreamJob, branchName){
   def insertTrigger=", pipelineTriggers([upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"$branchName\", \"UTF-8\"))])"
   //println insertTrigger
   File fh = new File("${workspace}/test_jenkinsfile")
   def linenum=0
   def count=0
   def insertLineNumber=0
   def linesR = fh.readLines()
   def linesW = fh.readLines()
   def mask=/^\s+pipelineTriggers\((.*)\)/
   for (line in linesR){
     linenum++
     if((mtcher = line=~ mask)){
     //if((mtcher = line=~ /^\s+pipelineTriggers\((.*)\)/)){
        println ${mtcher[0]}
     }
     if (line=~/^\s+]\)/){
       count++
       insertLineNumber=linenum
     }
   }
   print count
   //linesW.add(insertLineNumber-1, "\t\t"+insertTrigger)
   def w = fh.newWriter() 
   for(wline in linesW){
       w<< wline +"\n"
     }
   w.close()
}
return this
   
