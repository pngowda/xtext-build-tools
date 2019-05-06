
def addUpstream(jenkinsfile, upstreamJob, branchName){
   def insertTrigger=", pipelineTriggers([upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"\$branchName\", \"UTF-8\"))])"
   def appendTrigger="upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"\$branchName\", \"UTF-8\"))"
   File fh = new File(jenkinsfile)
   def linenum=0
   def lineToReplace
   def insert_new=0
   def insert_append=0
   def linesR = fh.readLines()
   def linesW = fh.readLines()
   def existingTrigger
   for (line in linesR){
     linenum++
     if(line=~ /^\s+.*pipelineTriggers\(\[upstream.*/){
        println "Upstream job trigger already present"
        return this
     }
     if(line=~ /^\s+pipelineTriggers\(\[(.*)\]\)/){
        insert_append=linenum
        (line=~ /^\s+pipelineTriggers\(\[(.*)\]\)/).each {match -> existingTrigger=match[1] }
        continue
     }      
     if (line=~/^\s+]\)/){
       insert_new=linenum
       continue
     }
   }
   if(existingTrigger){
      insertTrigger="pipelineTriggers([${existingTrigger}, ${appendTrigger}])"
      linesW.set(insert_append-1, "\t\t"+insertTrigger)
   }
   else{
      linesW.add(insert_new-1, "\t\t"+insertTrigger)
   }
   def w = fh.newWriter() 
   for(wline in linesW){
       w<< wline +"\n"
     }
   w.close()
}
return this
