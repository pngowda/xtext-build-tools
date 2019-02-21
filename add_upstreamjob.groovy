
def addUpstream(upstreamJob){
   def insertTrigger=", pipelineTriggers([upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"\$BRANCH_NAME\", \"UTF-8\"))])"
   def appendTrigger="upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"\$BRANCH_NAME\", \"UTF-8\"))"
   File fh = new File("${workspace}/test_jenkinsfile")
   def linenum=0
   def lineToReplace
   def insert_new=0
   def insert_append=0
   def linesR = fh.readLines()
   def linesW = fh.readLines()
   def exisitngTrigger
   for (line in linesR){
      println "..."+line
     linenum++
     if(line=~ /^\s+.*pipelineTriggers\(\[upstream.*/){
        println "Upstream trigger already present, so skipping"
        return this
     }
     if(line=~ /^\s+pipelineTriggers\(\[(.*)\]\)/){
        insert_append=linenum
        (line=~ /^\s+pipelineTriggers\(\[(.*)\]\)/).each {match -> exisitngTrigger=match[1] }
        continue
     }      
     if (line=~/^\s+]\)/){
       insert_new=linenum
       continue
     }
   }
   if(exisitngTrigger){
      insertTrigger="pipelineTriggers([${exisitngTrigger}, ${appendTrigger}])"
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
   
