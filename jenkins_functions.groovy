import java.util.regex.*

def void addDeclarativeUpstream (jenkinsfile, upstreamJob, branchName, dryRun=false) {
  def file = new File(jenkinsfile)
  if (!file.text.contains('pipeline')) throw new IllegalArgumentException ("$jenkinsfile is not a declarative Jenkins pipeline file")

  def upstreamProject = upstreamJob + '/' + URLEncoder.encode(branchName, 'UTF-8')
  def String newContent
  if (file.text.contains('triggers')) {
    newContent = file.text.replaceFirst("triggers\\s*\\{", "triggers {\n    upstream(upstreamProjects: '${upstreamProject}', threshold: hudson.model.Result.SUCCESS)")
  } else {
    // insert after closing brace of options block; options block is always present
    def offset = file.text.indexOf('}', file.text.indexOf('options')) + 1
    newContent = file.text.substring(0, offset)
    newContent += "\n\n  triggers {\n    upstream(upstreamProjects: '${upstreamProject}', threshold: hudson.model.Result.SUCCESS)\n  }\n"
    newContent += file.text.substring(offset+1)
  }
  file.write(newContent)
}

def void addScriptedUpstream (jenkinsfile, upstreamJob, branchName, dryRun=false) {
  println "jdd"
  def file = new File(jenkinsfile)
  if (!file.text.contains('node')) throw new IllegalArgumentException ("$jenkinsfile is not a scripted Jenkins pipeline file")

  def upstreamProject = upstreamJob + '/' + URLEncoder.encode(branchName, 'UTF-8')
  def String newContent
  def trigger          = "upstream(threshold: 'SUCCESS', upstreamProjects: '${upstreamProject}')"
  def pipelineTriggers = "pipelineTriggers([${trigger}])"

  // 'pipelineTriggers' exists, append within section
  def PATTERN_ADD_TO_PIPELINETRIGGERS=Pattern.compile('(.*(?:pipelineTriggers)\\(\\[.*)(\\]\\)\\s+\\]\\).*)', Pattern.DOTALL).matcher(file.text)
  // 'properties' section exists before 'stage' section
  def PATTERN_ADD_TO_PROPERTIES=Pattern.compile('(.*(?:properties).*)(\\]\\)\\s+(?:stage).*)', Pattern.DOTALL).matcher(file.text)

  if (PATTERN_ADD_TO_PIPELINETRIGGERS.find()) {
    // pipelineTriggers already there, add another one
    newContent = PATTERN_ADD_TO_PIPELINETRIGGERS.group(1)
    newContent += ", ${trigger}"
    newContent += PATTERN_ADD_TO_PIPELINETRIGGERS.group(2)
  } else if (PATTERN_ADD_TO_PROPERTIES.find()) {
    newContent = PATTERN_ADD_TO_PROPERTIES.group(1)
    newContent += "\t, ${pipelineTriggers}\n\t"
    newContent += PATTERN_ADD_TO_PROPERTIES.group(2)
  } else {
    // insert after closing brace of options block; options block is always present
    def offset = file.text.indexOf('stage')
    newContent = file.text.substring(0, offset)
    newContent += "\n\n    properties([\n      ${pipelineTriggers}\n    ])\n\n"
    newContent += file.text.substring(offset+1)
  }
  if (dryRun) {
    println(newContent)
  } else {
   file.write(newContent)
  }
}

return this
