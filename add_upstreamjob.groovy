
def addUpstream(upstreamJob, branchName){
   def insertTrigger=", pipelineTriggers([upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"$branchName\", \"UTF-8\"))])"
   def appendTrigger="[upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"$branchName\", \"UTF-8\"))]"
   //println insertTrigger
   File fh = new File("${workspace}/test_jenkinsfile")
   def linenum=0
   def lineToReplace
   def count=0
   def insertLineNumber=0
   def insertLineNumber1=0
   def linesR = fh.readLines()
   def linesW = fh.readLines()
   def exisitngTrigger
   for (line in linesR){
     linenum++
     if(line=~ /^\s+pipelineTriggers\((.*)\)/){
        insertLineNumber1=linenum
        lineToReplace=line
        (line=~ /^\s+pipelineTriggers\((.*)\)/).each {match -> exisitngTrigger=match[1] }
     }      
     if (line=~/^\s+]\)/){
       count++
       insertLineNumber=linenum
     }
   }
   println count
   println insertLineNumber1
   println insertLineNumber
   println "Trigger to append "+ exisitngTrigger+ ", " +appendTrigger
   insertTrigger="pipelineTriggers(${exisitngTrigger}, ${appendTrigger})"
   println "Trigger to append "+ insertTrigger
   //linesW.add(insertLineNumber-1, "\t\t"+insertTrigger)
   //linesW.drop(insertLineNumber1)
   linesW.set(insertLineNumber1, "\t\t"+insertTrigger)
   def w = fh.newWriter() 
   for(wline in linesW){
       w<< wline +"\n"
     }
   w.close()
}
return this
   
