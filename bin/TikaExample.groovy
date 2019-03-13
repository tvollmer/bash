#!/bin/env groovy

//@GrabResolver(name='artifactory', root='http://artifactory.suddenlink.cequel3.com:8081/artifactory/repo')
@Grapes([
    @Grab('org.apache.tika:tika-core:1.1'),
    @Grab('log4j:log4j:1.2.16'),
    @Grab('joda-time:joda-time:1.6.2'),
//    @GrabConfig(systemClassLoader=true, initContextClassLoader=true)
])

import java.io.*
import org.apache.tika.mime.*
import org.apache.tika.metadata.*
import org.apache.tika.config.*
import org.apache.tika.*
import org.joda.time.*
import org.apache.log4j.*

Logger log = Logger.getRootLogger()
log.setLevel(Level.DEBUG)

log.info "you were here"

TikaConfig config = TikaConfig.defaultConfig

// find files without an extension
File file = new File('/opt/projects/optionsWiki/resources/pdfs/JS_CALENDAR.pdf')
InputStream stream = new BufferedInputStream(new FileInputStream(file))
Metadata metadata = new Metadata()
MediaType mediaType = config.getMimeRepository().detect(stream, metadata)
MimeType mimeType = config.getMimeRepository().forName(mediaType.toString())
log.info "mimeType : $mimeType"

//mimeType.metaClass.methods.each{ method ->
//    log.info "method : ${method.name}"
//}
//mimeType.metaClass.properties.each{ prop ->
//    log.info "prop : ${prop.name}"
//}

//String extension = mimeType.getExtension()
//log.info "extension is ${extension}"
if ("application/pdf".equals(mimeType.toString())){
    log.info "need to add the .pdf to the file name."
}

log.info "finished!"