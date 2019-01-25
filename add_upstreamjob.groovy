
def addUpstream(){
   def insertstring="pipelineTriggers([upstream(threshold: \'SUCCESS\',upstreamProjects: \'xtext-lib/\' + URLEncoder.encode(\"BRANCH_NAME\", \"UTF-8\"))])"
   filename="test_jenkinsfile"
	 File fh = new File("${workspace}//test_jenkinsfile")
   def linenum=0
   def lines = fh.readLines()
   for (line in lines) {
	    linenum++
	    if (line=~/BuildDiscarderProperty/){
		    lines.add(linenum, insertstring)
		   }
    }
for (line in lines) {
    println line+"\n"
}
  }
  
return this
       
