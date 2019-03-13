import groovy.sql.Sql
import java.util.concurrent.*


/**
  * Runnable class to wrap overall work
  */
def fileName = ""
def query = """
select  a.*, b.last_created
from    (
        select  siteid, name, description
        from    eds.site
        where   active = 1
        order by ordernbr
        ) a left outer join
        (
        select  siteid, max(created_date) as last_created
        from    eds.workorder
        where   created_date >= to_date('10/2/2009 2:14:10 PM', 'MM/DD/YYYY hh:mi:ss am')
        group by siteid
        --order by siteid
        ) b on a.siteid = b.siteid
"""
def startTime = (new Date()).getTime()
println startTime + " " + args[0] + ":" + fileName

def sql = Sql.newInstance("jdbc:oracle:thin:@${args[0]}:1521:${args[0]}", args[1], args[2], "oracle.jdbc.OracleDriver")

java.sql.Timestamp.metaClass.toString = {->
    return "hello"
}

sql.eachRow(query, { row ->
    println "${row}"
})

def endTime = (new Date()).getTime()
println endTime + " " + args[0] + ":" + fileName + ", ${endTime.minus(startTime)} millis"
