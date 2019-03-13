import groovy.sql.Sql
import java.util.concurrent.*

public class BillingAccount{
    String siteId
    String accountNumber
    String company
    String division
    String accountNumber13
    String firstName
    String lastName
    String customerName
    String ssn
    String pin
    String customerStatus
    String customerComments
    String hsiServicesCount

    String toString(){
        "siteId:$siteId,acctNbr13:$accountNumber13,pin:$pin,cuStatus:$customerStatus,hsiSvcsCnt:$hsiServicesCount,cuName:$customerName"
    }
}


/**
  * Runnable class to wrap overall work
  *
  * CLASSPATH="/c/repository/ojdbc/ojdbc/14/ojdbc-14.jar" groovy ~/unregisteredCustomers.groovy SDMPRD TVOLLMER pwd IDMPRD TVOLLMER pwd
  *
  */
def fileName = ""
def querySDM = """
/* Formatted on 2009/06/08 16:05 (Formatter Plus v4.8.8) */
select  *
from    (
    SELECT   cumstpf.cunrov as site_id, cumstpf.cucno AS account_number, cumstpf.cudv, cumstpf.cuco
            , lpad(to_char(cumstpf.cuco), 2, '0') || lpad(to_char(cumstpf.cudv), 2, '0') || lpad(to_char(cumstpf.cucno), 9, '0') as cucno_13
            , cumstpf.cufnm, cumstpf.culnm
            , cumstpf.cuss#, cumstpf.cupin#, cumstpf.custa, cumstpf.cucom
            ,sum(case when srvrpf.aosuh8 = 'A' AND srvrpf.aosvcd in ('30151'
,'30102','30103','30115','31456','30169','40104','20500','31457','40111','30101','40106','30118','22000','15308','371','30120'
,'40101','41108','361','15309','15454','22001','30119','12342','12335','30202','30123','15311','40116','21057','21050','21054'
,'21008','21003','21005','21000','22002','40115','21002','21006','21052','12330','12331','12344','21007','41105','15306','15307'
,'15305','333','339','347','364','348','30140','30141','30142','40101WV','40102WV','40103WV','40104WV','40105WV','F10CSTA','FTLSVL'
,'F100CTA','15430','30502','30511','22000','22001','22002','30126','30127','30128','40117','40118','40119','358','15413','359','360'
,'15414','2550','2551','2552','2553','2554','2555','2556','2557','2558','2559','2560','2561','2562','2564','40100','15308','371'
,'40109','40121','40120','31458','31459','31462','31460','31461','30145','40113','40111','40120','40120','40120','40111','40120'
,'40122','30152','30153','30154','30156','30157','30158','30159','30161','31404','31405','31412','31453','31420','31439','31437'
,'30143','31444','31414','31403','31402','31448','31449','31445','31447','31450','31451','31452','31454','31455','31407','31408','31409'
,'31410','31411','31413','31433','31434','31440','31441','30155','30160','30162','30163','30167','30168','30138','30133','31435'
,'31436','31442','31443','31493','31465','31466','31467','31468','31469','31470','31471','31472','31473','31474','31475','31476','31477'
,'31478','31479','31417','31418','31419','31421','31431','31432','31438','31446','31485','31486','31487','31488','31489','31490'
,'31480','31481','31482','31483','31484','31491','31492','31463','31464') then 1 else 0 end) as hsi_services
            , cumstpf.cunam
            /*, srvrpf.aosvcd AS service_code,
             SUM (srvrpf.aoqtpt) + SUM (srvrpf.aoqtp5) AS service_qty,
             srvrpf.aosuh8 AS status*/
        FROM pstage.icoms_cumstpf cumstpf 
          inner join pstage.icoms_srvrpf srvrpf on cumstpf.cucno = srvrpf.aocnbr AND cumstpf.cunrov = srvrpf.aonrov
       WHERE cumstpf.cunrov > '100'
         AND CUMSTPF.CUSTA IN ('N')
         and  length(trim(cupin#)) = 4
/*         and  rownum <= 500*/
    GROUP BY cumstpf.cunrov, cumstpf.cucno, cumstpf.cudv, cumstpf.cuco
            , lpad(to_char(cumstpf.cuco), 2, '0') || lpad(to_char(cumstpf.cudv), 2, '0') || lpad(to_char(cumstpf.cucno), 9, '0')
            , cumstpf.cufnm, cumstpf.culnm
            , cumstpf.cuss#, cumstpf.cupin#, cumstpf.custa, cumstpf.cucom
            , cumstpf.cunam
    ORDER BY cumstpf.cupin# DESC
    ) x
where   rownum <= 50
"""
def startTime = (new Date()).getTime()
println startTime + " " + args[0] + ":" + fileName

def sqlSDM = Sql.newInstance("jdbc:oracle:thin:@${args[0]}:1521:${args[0]}", args[1], args[2], "oracle.jdbc.OracleDriver")

java.sql.Timestamp.metaClass.toString = {->
    return "hello"
}

def billingAccountsMappedByAccountNumber13 = [:]
sqlSDM.eachRow(querySDM, { row ->
    //println "${row}"
    def billingAccount = new BillingAccount(siteId:row[0],accountNumber:row[1],company:row[3],division:row[2],accountNumber13:row[4],firstName:row[5],lastName:row[6],ssn:row[7],pin:row[8],customerStatus:row[9],customerComments:row[10],hsiServicesCount:row[11],customerName:row[12])
    billingAccountsMappedByAccountNumber13.put(row[4],billingAccount) 
})

def endTime = (new Date()).getTime()
println endTime + " " + args[0] + ":" + fileName + ", ${endTime.minus(startTime)} millis"

def sb = new StringBuilder("'0'")
billingAccountsMappedByAccountNumber13.each{ acct13, acct ->
    //println acct13
    //println "billingAccount : $acct"
    sb << ",'$acct13'"
}
println billingAccountsMappedByAccountNumber13.size() + " rows found in SDM."


def String queryIDM = """
select  account_number
from    idm.idm_user
where   account_number in ($sb)
  and   idm_status_id = 3
  and   primary_user_id = 0
"""
//println queryIDM
def sqlIDM = Sql.newInstance("jdbc:oracle:thin:@${args[3]}:1521:${args[3]}", args[4], args[5], "oracle.jdbc.OracleDriver")
sqlIDM.eachRow(queryIDM, { row ->
    //println "found $row[0]"
    billingAccountsMappedByAccountNumber13.remove(row[0])
})
println billingAccountsMappedByAccountNumber13.size() + " rows un-registered."

billingAccountsMappedByAccountNumber13.each{ acct13,acct ->
    println acct
}


