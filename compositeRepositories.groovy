import jenkins.model.*
import hudson.model.*
import groovy.xml.XmlUtil

def addChildToComposite (String compositeDescriptorPath, String childLocation) {
  XmlUtil xmlUtil = new XmlUtil()
  def xmlFromFile = new File(compositeDescriptorPath)
  def repository = new XmlSlurper( false, false ).parseText(xmlFromFile.getText())
  xmlUtil.serialize(repository, new FileWriter(new File(compositeDescriptorPath+".bak")))
  
  repository.properties.property[0].value = System.currentTimeMillis()
  if (!repository.children.child.any { node -> node.@location == childLocation }) {
    repository.children.appendNode {
      child (location:childLocation)
    }
    println "${compositeDescriptorPath}: child ${childLocation} added"
  } else {
    println "${compositeDescriptorPath}: child ${childLocation} already exists"
  }
  xmlUtil.serialize(repository, new FileWriter(xmlFromFile))
}

def getBuildId (String promotePropertiesPath) {
  Properties props = new Properties()
  File propsFile = new File(promotePropertiesPath)
  props.load(propsFile.newDataInputStream())
  return props.getProperty('build.id')
}

def getVersion (String publisherPropertiesPath) {
  Properties props = new Properties()
  File propsFile = new File(publisherPropertiesPath)
  props.load(propsFile.newDataInputStream())
  return props.getProperty('version')
}

def buildType = System.getProperty('BUILD_TYPE')
def buildId = getBuildId('build-result/promote.properties')
def version = getVersion('build-result/publisher.properties')

if (buildType == 'S') {
  addChildToComposite('build-result/composite/compositeArtifacts.xml', buildId)
  addChildToComposite('build-result/composite/compositeContent.xml', buildId)
}
if (buildType == 'R') {
  addChildToComposite('build-result/composite/compositeArtifacts.xml', version)
  addChildToComposite('build-result/composite/compositeContent.xml', version)
}

