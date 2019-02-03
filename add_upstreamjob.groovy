
def addUpstream(upstreamJob){
   def BRANCH_NAME="test"
   def insertTrigger=", pipelineTriggers([upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"$BRANCH_NAME\", \"UTF-8\"))])"
   def lines = fh.readLines()
   println insertTrigger
   File fh = new File("${workspace}/test_jenkinsfile")
   def linenum=0
   LineNumberReader reader = fh.newReader()
   while ((line = reader.readLine()) != null) {
     linenum++
     if (line=~/^\s+]/){
       lines.add(linenum-1, insertstring)
     }
   }   
   def w = fh.newWriter() 
   for(wline in lines){
       w<< wline +"\n"
     }
   w.close()
}
return this
   
