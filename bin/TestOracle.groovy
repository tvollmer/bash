#!/bin/env groovy

@GrabResolver(name='artifactory', root='http://artifactory.somewhere.com:8081/artifactory/repo')
@Grapes([
    @Grab(group='com.oracle', module='ojdbc5', version='11.2.0.1.0'),
    @Grab('log4j:log4j:1.2.16'),
    @Grab('joda-time:joda-time:1.6.2'),
    @GrabConfig(systemClassLoader=true, initContextClassLoader=true)
])

import groovy.sql.Sql
import org.joda.time.*
import org.apache.log4j.*

Logger log = Logger.getRootLogger()
log.setLevel(Level.DEBUG)

def timedTask = { cls ->
  DateTime start = new DateTime()
  log.info start
  try {
      cls()
  } finally {
      DateTime end = new DateTime()
      log.info end
      log.info new Duration(start, end)
  }
}

//user = 'TIVO'
user = 'CBLPRD'
pass = user
driver = 'oracle.jdbc.driver.OracleDriver'

connect = "jdbc:oracle:thin:@CUSTFOO:1521:CUSTFOO"

def stmts = []
stmts << """
select    *
from  CBLFOO.TITO_AUDIT_LOG
where rownum <= 5
order by LOG_ID
"""

sql = Sql.newInstance(connect, user, pass, driver)

timedTask{
    log.info "querying rows ..."
    sql.rows(stmts[0]).each{
        log.info it
    }
}

log.info "finished!"
