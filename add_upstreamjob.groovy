
def addUpstream(upstreamJob){
   def BRANCH_NAME="test"
   def insertTrigger=", pipelineTriggers([upstream(threshold: \'SUCCESS\', upstreamProjects: \'$upstreamJob/\' + URLEncoder.encode(\"$BRANCH_NAME\", \"UTF-8\"))])"
   println insertTrigger
   File fh = new File("${workspace}/test_jenkinsfile")
   def linenum=0
   def lines = fh.readLines()
   println lines
   LineNumberReader reader = fh.newReader()
   while ((line = reader.readLine()) != null) {
     linenum++
     println line
     if (line=~/^\s+]/){
       println line
       println linenum
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
   
