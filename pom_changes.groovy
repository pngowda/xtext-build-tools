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

// TODO Move to some XML Utility file
def readXML (String xmlFile) {
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

def setProperty (String pomFile, propertyName, propertyValue) {
  println ("Set property $propertyName")
  def pom = readXML (pomFile)
  pom.properties.'*'.find { node -> node.name() == propertyName }.replaceBody(propertyValue)
  writeXML (pom, pomFile)
}

// just for debugging with groovysh
def setProperty2 (pom, propertyName, propertyValue) {
  println ("Set property $propertyName")
  pom.properties.'*'.find { node -> node.name() == propertyName }.replaceBody(propertyValue)
  println(XmlUtil.serialize(pom))
}

def pomZipVersionUpdate(xtext_version, pomFile,snapshot_version) {
  def file = new File(pomFile)
  def newContent = file.text.replaceFirst("tofile=(.*)repository-.+\\.zip", "tofile=\$1repository-${xtext_version}.zip")
  file.write(newContent)
}

return this
