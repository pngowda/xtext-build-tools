import jenkins.model.*
import hudson.model.*
import groovy.xml.XmlUtil

def changePomDependencyVersion(xtext_version, pomFile, variant_string){
    println "Pom File to process: "+pomFile
    def xmlFromFile = new File(pomFile)
    def pom = new XmlSlurper( false, false ).parseText(xmlFromFile.getText())
	
    pom.dependencies.dependency.each { dependency ->
       pom.dependencies.dependency.version="${dependency.version}".replace("-SNAPSHOT", variant_string)
    }
    XmlUtil xmlUtil = new XmlUtil()
    xmlUtil.serialize(pom, new FileWriter(xmlFromFile))
}


def pomVersionUpdate(path,xtext_version){
  def update_cmd
    dir(path) {
        update_cmd = sh (
            script: "find ./ -type f -name \"pom.xml\" | xargs  sed -i -e \"s/${xtext_version}-SNAPSHOT/${xtext_version}/g\"",
	    returnStdout: true
        ).trim()
    }
    return update_cmd
}

def pomZipVersionUpdate(path,xtext_version, pomFile){
  def update_cmd
    dir(path) {
        update_cmd = sh (
            script: "sed -i -r 's/tofile=(.*)repository-${xtext_version}-SNAPSHOT.zip/tofile=\\1repository-${xtext_version}.zip/g\' ${pomFile}",
	    returnStdout: true
        ).trim()
    }
    return update_cmd
}

def xtextXtendPomVersionUpdate(path,xtext_version, pomFile){
  def update_cmd
    dir(path) {
        update_cmd = sh (
            script: "sed -i -e \"s/${xtext_version}-SNAPSHOT/${xtext_version}/g\" ${pomFile}",
	    returnStdout: true
        ).trim()
    }
    return update_cmd
}
return this
