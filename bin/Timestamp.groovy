
import java.text.DateFormat
import java.util.Date

println "now in millis : ${new Date().getTime()}"
//Date pstageDate = DateFormat.getInstance().parse("06/26/2009 1:20 PM, CST");
Date pstageDate = DateFormat.getInstance().parse("11/15/2009 0:01 AM, CST");
println "first entry in millis : ${pstageDate.getTime()}"
(0..20).each{
    Date newDate = new Date(pstageDate.getTime() + (it*250*60*60*24))
    println "pstageDate $newDate, millis: ${newDate.getTime()}"
}
println DateFormat.getInstance().parse("08/20/2009 0:0 AM, CST").getTime()
println DateFormat.getInstance().parse("09/21/2009 2:15 PM, CST").getTime()
println DateFormat.getInstance().parse("09/29/2009 2:15 PM, CST").getTime()
println DateFormat.getInstance().parse("11/15/2009 3:31 PM, CST").getTime()
println DateFormat.getInstance().parse("11/15/2009 3:31 PM, GMT").getTime()

println DateFormat.getInstance().parse("02/06/2011 10:00 PM, CST").getTime()

