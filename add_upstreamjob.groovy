
def addUpstream(upstreamJob, branchName){
   def insertTrigger=", pipelineTriggers([upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"$branchName\", \"UTF-8\"))])"
   println insertTrigger
   File fh = new File("${workspace}/test_jenkinsfile")
   def linenum=0
   def count=0
   def linesR = fh.readLines()
   def linesW = fh.readLines()
   for (line in linesR){
     linenum++
     if (line=~/^\s+]/){
       count++
       print linenum
       //linesW.add(linenum-1, insertTrigger)
     }
   }
   print count
   def w = fh.newWriter() 
   for(wline in linesW){
       w<< wline +"\n"
     }
   w.close()
}
return this
   
