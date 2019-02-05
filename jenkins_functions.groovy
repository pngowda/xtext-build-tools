
def addUpstream(jenkinsfile, upstreamJob, branchName){
   def insertTrigger=", pipelineTriggers([upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"\$BRANCH_NAME\", \"UTF-8\"))])"
   println insertTrigger
   File fh = new File(jenkinsfile)
   def linenum=0
   def linesR = fh.readLines()
   def linesW = fh.readLines()
   for (line in linesR){
     linenum++
     if (line=~/^\s+]/){
       linesW.add(linenum-1, insertTrigger)
     }
   }   
   def w = fh.newWriter() 
   for(wline in linesW){
       w<< wline +"\n"
     }
   w.close()
}
return this