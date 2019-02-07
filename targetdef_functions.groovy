/**
 * Processes .target files in a given directory and updates Xtext repository URLs.
 * @param path Directory containing .target files
 * @param jenkinsUrl URL of a Jenkins instances with Xtext multibranch-pipeline jobs
 * @param branchName The branch name to redirect to
 * @param dryRun When true do not modify the original file and print the modified target file
 *               content to the console
 */
def void updateXtextRepositories (CharSequence path, CharSequence jenkinsUrl, String branchName, boolean dryRun) {
  def targetFiles = new File(path).listFiles()
  targetFiles.each { file ->
    println "Target definition to process: ${file.absolutePath}"
    // read .target file
    def targetDef = readXML(file.absolutePath)
    // process all repository locations containing 'xtext-' jobs.
    targetDef.locations.location.repository
      .findAll { node -> node.@location.text().contains('job/xtext-')}*.forEach {node ->
        def newLocation = getRedirectedLocation(node.@location.toString(), jenkinsUrl, branchName).toString()
        println "${node.@location} -> ${newLocation}"
        node.@location = newLocation
      }
    writeXML(targetDef, file.absolutePath, true, true)
  } 
}


/**
 * Replaces Jenkins URL and branch name for an Xtext repository location URI
 * @param locationUri A repository URI
 * @param jenkinsUrl Jenkins URL
 * @param branchName New branch name
 * @return The location with potentially changed Jenkins URL and branch
 */
def getRedirectedLocation (locationUri, jenkinsUrl, branchName) {
  // Define pattern matcher for location
  // Match groups: 1->Xtext Repository Name, 2->tail
  def match = (locationUri =~ /^.*\/job\/(xtext-\w+)\/job\/\w+\/(.*)/)
  match.find() ? "${jenkinsUrl}/job/${match[0][1]}/job/${branchName}/${match[0][2]}" : locationUri
}

def writeXML (xml, xmlFile) {
  writeXML (xml, xmlFile, true, false)
}

def writeXML (xml, xmlFile, boolean useTabIndent, boolean dryRun) {
  XmlUtil xmlUtil = new XmlUtil()
  if (!dryRun) {
    def file = new File(xmlFile)
    xmlUtil.serialize(xml, new FileWriter(file))
    if (useTabIndent) {
    // xml is serialized with space indentation, but we want to indent with tabs => postprocess and change that
    def content = file.getText()
    content = content.replaceAll('  ','\t')
    file.setText(content)
    }
  } else {
    sw = new StringWriter()
    xmlUtil.serialize(xml, sw)
    content = sw.toString()
    if (useTabIndent) {
    content = content.replace("  ", "\t")
    }
    println (content)
  }
}

