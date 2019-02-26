import jenkins.model.*
import hudson.model.*
import groovy.xml.XmlUtil

def changePomDependencyVersion(xtext_version, pomFile, snapshot_version) {
  def pom = readXML(pomFile)

  pom.dependencies.dependency.each { dependency ->
     pom.dependencies.dependency.version="${dependency.version}".replace(snapshot_version, xtext_version)
  }
  writeXML (pom, pomFile)
}

def setUpstreamBranch (pomFile, branchName) {
  def pom = readXML (pomFile)
  pom.properties.upstreamBranch = branchName
  writeXML (pom, pomFile)
}

// TODO Move to some XML Utility file
def readXML (xmlFile) {
  curdir = new File('.')
  println "XML file to process: ${curdir.absolutePath} ${xmlFile}"
  def xml = new XmlSlurper( false, false ).parseText(new File(xmlFile).getText())
  return xml
}

// TODO Move to some XML Utility file
def writeXML (xml, xmlFile) {
  def file = new File(xmlFile)
  XmlUtil xmlUtil = new XmlUtil()
  xmlUtil.serialize(xml, new FileWriter(file))
  // xml is serialized with space indentation, but we want to indent with tabs => postprocess and change that
  def content = file.getText()
  content = content.replaceAll('  ','\t')
  file.setText(content)
}

def pomVersionUpdate(pomFile, xtext_version) {
  def pom = readXML(pomFile)
  
  pom.version = xtext_version
  if (pom.parent != null) {
    pom.parent.version = xtext_version
  }
  
  writeXML (pom, pomFile)
}

def setProperty (pomFile, propertyName, propertyValue) {
  def pom = readXML(pomFile)
  
  pom.properties.'*'.findResult { node -> node.name() == propertyName } = propertyValue
  
  writeXML (pom, pomFile)
}

def pomZipVersionUpdate(xtext_version, pomFile,snapshot_version) {
  def update_cmd
  update_cmd = sh (
    script: "sed -i -r 's/tofile=(.*)repository-${snapshot_version}.zip/tofile=\\1repository-${xtext_version}.zip/g\' ${pomFile}",
    returnStdout: true
  ).trim()
  return update_cmd
}

def xtextXtendPomVersionUpdate(xtext_version, pomFile, snapshot_version) {
  def update_cmd
  update_cmd = sh (
    script: "sed -i -e \"s/${snapshot_version}/${xtext_version}/g\" ${pomFile}",
    returnStdout: true
  ).trim()
  return update_cmd
}
return this
